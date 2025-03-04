package de.invesdwin.util.collections.loadingcache.historical;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.circular.CircularGenericArrayQueue;
import de.invesdwin.util.collections.iterable.buffer.BufferingIterator;
import de.invesdwin.util.collections.loadingcache.historical.internal.AGapHistoricalCacheMissCounter;
import de.invesdwin.util.collections.loadingcache.historical.key.IHistoricalCacheAdjustKeyProvider;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQuery;
import de.invesdwin.util.math.expression.lambda.IEvaluateGenericFDate;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.FDates;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.date.IFDateProvider;
import de.invesdwin.util.time.duration.Duration;

/**
 * Tries to fill gaps via an intelligent caching algorithm to reduce the number of queries. This is also tolerant to
 * cache eviction.
 * 
 * This algorithm expects new values in the db to be only added on the high end and not anywhere inbetween.
 * 
 * This cache works best when iterating from the past to the future.
 * 
 * WARNING: This cache does not work when the underlying data changes, the min and max values are cached here and
 * changes do not get detected!!!
 */
@ThreadSafe
public abstract class AGapHistoricalCache<V> extends AHistoricalCache<V> {

    /**
     * 1k performs better than 10k because we get less often pauses from GC, this compensates for the additional file
     * seeks
     */
    public static final int DEFAULT_RETRIEVAL_COUNT = 1_000;

    /**
     * 10 days is a good value for daily caches.
     */
    public static final int DEFAULT_READ_BACK_STEP_ELEMENTS = 10;
    public static final long DEFAULT_READ_BACK_STEP_MILLIS = new Duration(DEFAULT_READ_BACK_STEP_ELEMENTS,
            FTimeUnit.DAYS).intValue(FTimeUnit.MILLISECONDS);
    /**
     * having 2 here helps with queries for elements that are filtered by end time
     */
    private static final int MAX_LAST_VALUES_FROM_LOAD_FURTHER_VALUES = 2;

    @GuardedBy("this")
    private final BufferingIterator<V> furtherValues = new BufferingIterator<V>();
    @GuardedBy("this")
    private final CircularGenericArrayQueue<V> lastValuesFromFurtherValues = new CircularGenericArrayQueue<V>(
            MAX_LAST_VALUES_FROM_LOAD_FURTHER_VALUES);
    @GuardedBy("this")
    private final AGapHistoricalCacheMissCounter<V> cacheMissCounter = new AGapHistoricalCacheMissCounter<V>() {

        @Override
        protected Integer getInitialMaximumSize() {
            return AGapHistoricalCache.this.getInitialMaximumSize();
        }

        @Override
        protected long getInitialReadBackStepMillis() {
            return AGapHistoricalCache.this.getInitialReadBackStepMillis();
        }

        @Override
        protected void increaseOptimalMaximumSize(final int optimalMaximumSize, final String reason) {
            AGapHistoricalCache.this.increaseMaximumSize(optimalMaximumSize, reason);
        }

        @Override
        protected String parentToString() {
            return AGapHistoricalCache.this.toString();
        }
    };
    /**
     * As a convenience a field even if always reset
     */
    @GuardedBy("this")
    private boolean furtherValuesLoaded;
    @GuardedBy("this")
    private FDate minKeyInDB;
    @GuardedBy("this")
    private FDate minKeyInDBFromLoadFurtherValues;
    @GuardedBy("this")
    private FDate maxKeyInDBFromLoadFurtherValues;
    /**
     * Remembering this for cache eviction
     */
    @GuardedBy("this")
    private FDate maxKeyInDB;
    @GuardedBy("this")
    private FDate maxKey;
    @GuardedBy("this")
    private FDate minKey;

    private final IHistoricalCacheQuery<V> thisQueryWithFuture = query().setFutureEnabled();

    private boolean clearRequested;

    @Override
    protected void innerIncreaseMaximumSize(final int maximumSize, final String reason) {
        super.innerIncreaseMaximumSize(maximumSize, reason);
        cacheMissCounter.increaseMaximumSize(maximumSize);
    }

    /**
     * Assumption: cache eviction does not cause values to be evicted with their keys not being evicted aswell.
     * 
     * Even maximiumSize eviction causes random entries in the list to be missing because of least-recently-used
     * strategy
     */
    @Override
    protected final IEvaluateGenericFDate<V> newLoadValue() {
        return this::loadValue;
    }

    private synchronized V loadValue(final IFDateProvider pKey) {
        maybeClear();
        final FDate key = pKey.asFDate();
        eventuallyGetMinMaxKeysInDB(key, false);

        this.furtherValuesLoaded = false;
        final FDate previousMaxKey = maxKey;
        final boolean newMaxKey = updateMaxKey(key);
        final boolean newMinKey = updateMinKey(key);

        //Try loading from cache before trying a query; via gap finding or through min key
        V value = loadFromCacheBeforeLoadFurtherValues(key, newMaxKey, newMinKey);
        if (value != null) {
            return value;
        }

        //Try the expensive query
        if (!furtherValuesLoaded) {
            final FDate adjKey = determineEaliestStartOfLoadFurtherValues(key);
            furtherValuesLoaded = eventuallyLoadFurtherValues("loadValue", key, adjKey, newMinKey, false);
        }
        value = searchInFurtherValues(key);
        if (!furtherValuesLoaded && isPotentiallyAlreadyEvicted(key, value)) {
            cacheMissCounter.checkSuccessiveCacheEvictions(key);
            final FDate adjKey = determineEaliestStartOfLoadFurtherValues(key);
            furtherValuesLoaded = eventuallyLoadFurtherValues("loadValueBecauseOfEviction", key, adjKey, newMinKey,
                    true);
            value = searchInFurtherValues(key);
        }
        if (value != null) {
            return value;
        }

        //Try to used the last value if there is no higher key in db
        value = tryLoadFromCacheAfterLoadFurtherValues(key, newMaxKey, previousMaxKey);
        if (value != null) {
            return value;
        }

        //And last we just try to get the newest value matching the key.
        //If there are no values in db, this method is only called once
        return readNewestValueFromDB(key);
    }

    private boolean isPotentiallyAlreadyEvicted(final FDate key, final V value) {
        final boolean isEvictedBeforeCurrentFurtherValues = (value == null || extractKey(null, value).isAfter(key))
                && (key.isAfter(minKeyInDB) || key.isAfter(minKeyInDBFromLoadFurtherValues));
        if (isEvictedBeforeCurrentFurtherValues) {
            return true;
        }
        final boolean mightBeEvictedAfterFurtherValues = value != null && furtherValues.isEmpty();
        if (mightBeEvictedAfterFurtherValues) {
            final FDate valueKey = extractKey(null, value);
            final boolean isEvictedAfterCurrentFurtherValues = valueKey.isBefore(key)
                    && valueKey.isBeforeOrEqualTo(maxKeyInDB);
            if (isEvictedAfterCurrentFurtherValues) {
                return true;
            }
        }

        return false;
    }

    private boolean eventuallyGetMinMaxKeysInDB(final FDate key, final boolean force) {
        boolean changed = false;
        if (eventuallyGetMinKeyInDB(key, force)) {
            changed = true;
        }
        if (eventuallyGetMaxKeyInDB(key, force)) {
            changed = true;
        }
        return changed;
    }

    private boolean eventuallyGetMaxKeyInDB(final FDate key, final boolean force) {
        final IHistoricalCacheAdjustKeyProvider adjustKeyProvider = getAdjustKeyProvider();
        final boolean alreadyAdjustingKey = adjustKeyProvider.isAlreadyAdjustingKey();
        if (!alreadyAdjustingKey) {
            //not updating highest allowed key, since this already happened during key adjustment
            final FDate newMaxKeyInDB = adjustKeyProvider.getHighestAllowedKey();
            if (newMaxKeyInDB != null) {
                if (maxKeyInDB == null || newMaxKeyInDB.isAfterNotNullSafe(maxKeyInDB)) {
                    maxKeyInDB = newMaxKeyInDB;
                    return true;
                } else {
                    return false;
                }
            }
        }
        //fallback to normal procedure if curHighWaterMark is not provided by provider
        if (maxKeyInDB == null || force || alreadyAdjustingKey) {
            final V maxValue = readNewestValueFromDB(maxKey());
            if (maxValue != null) {
                final IHistoricalEntry<V> maxValueEntry = ImmutableHistoricalEntry.maybeExtractKey(this, null,
                        maxValue);
                final FDate maxValueKey = maxValueEntry.getKey();
                if (maxKeyInDB == null || maxValueKey.compareTo(maxKeyInDB) >= 1) {
                    maxKeyInDB = maxValueKey;
                    getValuesMap().put(maxValueKey, maxValueEntry);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean eventuallyGetMinKeyInDB(final FDate key, final boolean force) {
        if (minKeyInDB == null || force) {
            final V minValue = readNewestValueFromDB(minKey());
            if (minValue != null) {
                final IHistoricalEntry<V> minValueEntry = ImmutableHistoricalEntry.maybeExtractKey(this, null,
                        minValue);
                final FDate minValueKey = minValueEntry.getKey();
                //min key must be kept intact if all values have been loaded from a later key
                if (minKeyInDB == null || minValueKey.compareTo(minKeyInDB) <= -1) {
                    minKeyInDB = minValueKey;
                    getValuesMap().put(minValueKey, minValueEntry);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean updateMaxKey(final FDate key) {
        if (maxKey == null || key.compareTo(maxKey) >= 1) {
            maxKey = key;
            return true;
        } else {
            return false;
        }
    }

    private boolean updateMinKey(final FDate key) {
        if (minKey == null || key.compareTo(minKey) <= -1) {
            minKey = key;
            return true;
        } else {
            return false;
        }
    }

    private V loadFromCacheBeforeLoadFurtherValues(final FDate key, final boolean newMaxKey, final boolean newMinKey) {
        final V value = eventuallyGetMinValue(key, newMinKey);
        if (value != null) {
            return value;
        }

        //maybe use max value
        if (maxKeyInDB != null && key.compareTo(maxKeyInDB) >= 0 && containsKey(maxKeyInDB)) {
            return thisQueryWithFuture.getEntry(maxKeyInDB).getValueIfPresent();
        }
        return null;
    }

    private V eventuallyGetMinValue(final FDate key, final boolean newMinKey) {
        //if key < minKey; use value for minKey
        if (minKeyInDB != null) {
            final boolean afterMinKey = !newMinKey && key.compareTo(minKey) >= 0;
            if (afterMinKey && key.compareTo(minKeyInDB) <= 0 && containsKey(minKey)) {
                //via readNewestValueTo
                return thisQueryWithFuture.getEntry(minKey).getValueIfPresent();
            }
            if (key.compareTo(minKeyInDB) <= 0 && containsKey(minKeyInDB)) {
                //via searchInFurtherValues
                return thisQueryWithFuture.getEntry(minKeyInDB).getValueIfPresent();
            }
        }
        return null;
    }

    private boolean eventuallyLoadFurtherValues(final String source, final FDate key, final FDate loadFurtherValuesKey,
            final boolean newMinKey, final boolean forced) {
        if (forced || shouldLoadFurtherValues(key, newMinKey)) {
            final FDate keyForReadAllValues = FDates.max(minKeyInDB, loadFurtherValuesKey);
            furtherValues.clear();
            lastValuesFromFurtherValues.clear();
            FDate curKey = keyForReadAllValues;
            while (true) {
                final BufferingIterator<V> newFurtherValuesBuffer = new BufferingIterator<V>();
                final Iterable<? extends V> newFurtherValues = readAllValuesAscendingFrom(curKey);
                newFurtherValuesBuffer.consume(newFurtherValues);
                if (newFurtherValuesBuffer.isEmpty()) {
                    //end of data reached
                    break;
                }
                final boolean furtherValuesEmpty = furtherValues.isEmpty();
                if (!furtherValuesEmpty) {
                    final FDate tailKey = innerExtractKey(furtherValues.getTail());
                    final FDate newTailKey = innerExtractKey(newFurtherValuesBuffer.getTail());
                    if (newTailKey.isAfter(tailKey)) {
                        //skip duplicates on further queries
                        skipDuplicates(key, curKey, newFurtherValuesBuffer);
                    } else {
                        //just a duplicate result...
                        break;
                    }
                }
                final boolean added = furtherValues.consume(newFurtherValuesBuffer);
                if (!added) {
                    //end of data reached
                    break;
                }
                final FDate tailKey = innerExtractKey(furtherValues.getTail());
                maybeLimitOptimalReadBackStepByLoadFurtherValuesRange(furtherValuesEmpty, tailKey);
                if (tailKey.isAfterOrEqualTo(key) || tailKey.equals(maxKeyInDB)) {
                    //request fulfilled
                    break;
                }
                curKey = tailKey.addMilliseconds(1);
            }

            if (!furtherValues.isEmpty()) {
                assertFurtherValuesSorting(key);
            }
            return true;
        }
        return false;
    }

    private void maybeLimitOptimalReadBackStepByLoadFurtherValuesRange(final boolean furtherValuesEmpty,
            final FDate tailKey) {
        //if the further values is less than 10 we might be at the beginning of the history, thus we should not count this
        if (furtherValuesEmpty && furtherValues.size() > 10) {
            final FDate headKey = innerExtractKey(furtherValues.getHead());
            cacheMissCounter.maybeLimitOptimalReadBackStepByLoadFurtherValuesRange(new Duration(headKey, tailKey));
        }
    }

    private void skipDuplicates(final FDate key, final FDate curKey,
            final BufferingIterator<V> newFurtherValuesBuffer) {
        while (!newFurtherValuesBuffer.isEmpty()
                && innerExtractKey(newFurtherValuesBuffer.getHead()).isBefore(curKey)) {
            newFurtherValuesBuffer.next();
        }
    }

    protected boolean allowNoDataInDBShortcut() {
        return true;
    }

    private boolean shouldLoadFurtherValues(final FDate key, final boolean newMinKey) {
        if (furtherValues.isEmpty()) {
            final V tail = lastValuesFromFurtherValues.getTail();
            if (tail == null) {
                return true;
            }
            final V head = lastValuesFromFurtherValues.getHead();
            final FDate tailKey = extractKey(null, tail);
            final FDate headKey = extractKey(null, head);
            final boolean isEndReachedAnyway = tailKey.equals(maxKeyInDB) && key.isBeforeOrEqualTo(maxKeyInDB)
                    && headKey.isBeforeOrEqualTo(key);
            return !isEndReachedAnyway;
        }
        final boolean keyIsBeforeMinKeyFromLoadFurtherValues = newMinKey
                && key.isBefore(minKeyInDBFromLoadFurtherValues);
        if (keyIsBeforeMinKeyFromLoadFurtherValues) {
            return true;
        }
        final boolean newMinKeyFromDBMayFindNewValues = isMinKeyInDBFromLoadFurtherValues()
                && key.compareTo(minKeyInDB) <= -1 && newMinKey;
        if (newMinKeyFromDBMayFindNewValues) {
            return true;
        }

        return false;
    }

    private boolean isMinKeyInDBFromLoadFurtherValues() {
        return minKeyInDBFromLoadFurtherValues != null
                && FDates.isSameMillisecond(minKeyInDBFromLoadFurtherValues, minKeyInDB);
    }

    private void assertFurtherValuesSorting(final FDate key) {
        final FDate firstKey = extractKey(null, furtherValues.getHead());
        if (firstKey.compareTo(key) <= -1) {
            /*
             * readAllValuesAscendingFrom loads all data, thus we set the min key very deep so that later queries are
             * skipped if they are before minKey
             */
            minKey = minKey();
        }
        if (minKeyInDB == null || firstKey.compareTo(minKey) <= -1) {
            minKeyInDB = firstKey;
        }
        minKeyInDBFromLoadFurtherValues = FDates.min(minKeyInDBFromLoadFurtherValues, firstKey);
        final FDate lastKey = extractKey(null, furtherValues.getTail());
        if (maxKeyInDB == null || lastKey.compareTo(maxKeyInDB) <= -1) {
            maxKeyInDB = FDates.max(maxKeyInDB, lastKey);
        }
        maxKeyInDBFromLoadFurtherValues = FDates.max(maxKeyInDBFromLoadFurtherValues, lastKey);

        if (furtherValues.size() > 1) {
            Assertions.checkState(firstKey.compareTo(lastKey) <= 0,
                    "Not ascending sorted! At firstKey [%s] and lastKey [%s]", firstKey, lastKey);
        }
    }

    private V searchInFurtherValues(final FDate key) {
        //Take the first matching value from the sorted list
        //Search for the newest value
        V prevValue = null;
        FDate prevKey = null;
        if (!lastValuesFromFurtherValues.isEmpty()) {
            //though maybe use the last one for smaller increments than the data itself is loaded
            for (int i = 0; i < lastValuesFromFurtherValues.size(); i++) {
                final V lastValueFromFurtherValues = lastValuesFromFurtherValues.get(i);
                final FDate keyLastValueFromFurtherValues = extractKey(null, lastValueFromFurtherValues);
                if (keyLastValueFromFurtherValues.isBeforeOrEqualTo(key)) {
                    prevValue = lastValueFromFurtherValues;
                    prevKey = keyLastValueFromFurtherValues;
                } else {
                    //only go to further values if it might be possible that those are useable
                    return prevValue;
                }
            }
        }

        final FDate earliestStartOfLoadFurtherValues = determineEaliestStartOfLoadFurtherValues(key);
        FDate prevLoadFurtherValuesKey = FDates.MIN_DATE;
        while (!furtherValues.isEmpty()) {
            final V newValue = furtherValues.getHead();
            final FDate newValueKey = extractKey(null, newValue);
            final int compare = key.compareTo(newValueKey);
            if (compare < 0) {
                //key < newValueKey
                //run over the key we wanted
                break;
            } else if (compare == 0) {
                //key == newValueKey
                //This is the value we searched for! It will later be added with the db key to the cache.
                pushLastValueFromFurtherValues();
                return newValue;
            } else {
                //key > newValueKey

                //put this value into the cache; gaps do not get filled here, so that the max size of the cache does not get reached prematurely
                getPutProvider().put(newValueKey, newValue, prevKey, prevValue, false);

                pushLastValueFromFurtherValues();
                if (prevKey != null) {
                    cacheMissCounter.recordElementDistance(prevKey, newValueKey);
                }
                //continue with the next one
                prevValue = newValue;
                prevKey = newValueKey;

                if (furtherValues.isEmpty() && newValueKey.isBefore(maxKeyInDB) && key.isBefore(maxKeyInDB)
                        && maxKeyInDBFromLoadFurtherValues.isBefore(maxKeyInDB)) {
                    final FDate loadFurtherValuesKey = FDates.max(newValueKey, earliestStartOfLoadFurtherValues);
                    if (loadFurtherValuesKey.isBeforeOrEqualToNotNullSafe(prevLoadFurtherValuesKey)) {
                        break;
                    }
                    Assertions.checkState(eventuallyLoadFurtherValues("searchInFurtherValues", newValueKey,
                            loadFurtherValuesKey, false, true));
                    prevLoadFurtherValuesKey = loadFurtherValuesKey;
                    if (furtherValues.isEmpty()) {
                        break;
                    }
                    pushLastValueFromFurtherValues();
                    //do not distort prev/next lookup when using earliestStartOfLoadFurtherValues, thus reset those
                    prevValue = null;
                    prevKey = null;
                }
            }
        }
        return prevValue;
    }

    private void pushLastValueFromFurtherValues() {
        lastValuesFromFurtherValues.circularAdd(furtherValues.next());
    }

    /**
     * when this does not match, then getLatestValue will be used automatically anyway to go further back in time
     */

    private FDate determineEaliestStartOfLoadFurtherValues(final FDate key) {
        //1 day is fine for most cases
        final long readBackStepMillis = cacheMissCounter.getOptimalReadBackStepMillis();
        return key.addMilliseconds(-readBackStepMillis);
    }

    protected long getInitialReadBackStepMillis() {
        return DEFAULT_READ_BACK_STEP_MILLIS;
    }

    /**
     * These checks may only be called after furtherValues were searched and eventuelly the list has been reloaded.
     */

    private V tryLoadFromCacheAfterLoadFurtherValues(final FDate key, final boolean newMaxKey,
            final FDate previousMaxKey) {
        //maybe minKey in db did not change even though the minKey in the cache changed
        //after reloading of furtherValues it is ok to search this again instead of doing another query for the newest value
        if (furtherValuesLoaded) {
            final V value = eventuallyGetMinValue(key, false);
            if (value != null) {
                return value;
            }
        }

        //with maxKey
        if (newMaxKey && previousMaxKey != null && containsKey(previousMaxKey) && key.isAfterOrEqualTo(maxKeyInDB)) {
            //use the last maxKey
            //because this one is behind it and not a new one
            //thus working if the db does not have further values
            return thisQueryWithFuture.getEntry(previousMaxKey).getValueIfPresent();
        }
        return null;
    }

    private V readNewestValueFromDB(final FDate key) {
        // we give up and use the newest value from db
        V value = readLatestValueFor(key);

        //try to use first value of furthervalues
        if (value == null && furtherValuesLoaded && !furtherValues.isEmpty()) {
            value = furtherValues.getHead();
        }

        if (value != null) {
            //we remember the db key of the value so that it can be found again later
            //to use the parameter key would make the result incorrect
            final IHistoricalEntry<V> valueEntry = ImmutableHistoricalEntry.maybeExtractKey(this, null, value);
            getValuesMap().put(valueEntry.getKey(), valueEntry);
            return value;
        } else {
            return null;
        }
    }

    protected abstract Iterable<? extends V> readAllValuesAscendingFrom(FDate key);

    /**
     * This method first tries to load the nearest neighbor value to the given key. First it tries to load values <=
     * key, if this fails it tries to load values >= key. If the caller does not accept values from the future, this
     * will get handled properly later. For example financial backtests may not use values from the future to keep the
     * test realistic.
     * 
     * This method may return null, in which case the cache only relies on data returned from
     * readAllValuesAscendingFrom().
     */
    protected abstract V readLatestValueFor(FDate key);

    @Override
    public void clear() {
        super.clear();
        //don't synchronize this clear method, instead just set the flag so that next loadValue clears
        clearRequested = true;
    }

    private void maybeClear() {
        if (clearRequested) {
            //remove flags so that the limit check gets skipped if get has not been called yet and this method might be called again
            maxKeyInDB = null;
            minKeyInDB = null;
            //a clear forces the list to be completely reloaded next time get is called
            furtherValues.clear();
            lastValuesFromFurtherValues.clear();
            clearRequested = false;
        }
    }

}
