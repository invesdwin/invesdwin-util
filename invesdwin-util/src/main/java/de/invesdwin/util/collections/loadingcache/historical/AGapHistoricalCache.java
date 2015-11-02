package de.invesdwin.util.collections.loadingcache.historical;

import java.util.concurrent.TimeUnit;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.iterable.BufferingIterator;
import de.invesdwin.util.time.Duration;
import de.invesdwin.util.time.fdate.FDate;

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
     * 10 days is a good value for daily caches.
     */
    public static final long DEFAULT_READ_BACK_STEP_MILLIS = new Duration(10, TimeUnit.DAYS).intValue(TimeUnit.MILLISECONDS);
    /**
     * having 2 here helps with queries for elements that are filtered by end time
     */
    private static final int MAX_LAST_VALUES_FROM_LOAD_FURTHER_VALUES = 2;

    @GuardedBy("this")
    private final BufferingIterator<V> furtherValues = new BufferingIterator<V>();
    @GuardedBy("this")
    private final BufferingIterator<V> lastValuesFromFurtherValues = new BufferingIterator<V>();
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

    /**
     * Assumption: cache eviction does not cause values to be evicted with their keys not being evicted aswell.
     * 
     * Even maximiumSize eviction causes random entries in the list to be missing because of least-recently-used
     * strategy
     */
    @Override
    protected final synchronized V loadValue(final FDate key) {
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
            furtherValuesLoaded = eventuallyLoadFurtherValues("loadValue", key,
                    determineEaliestStartOfLoadFurtherValues(key), newMinKey, false);
        }
        value = searchInFurtherValues(key);
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
        //not updating highest allowed key, since this already happened during key adjustment
        final FDate newMaxKeyInDB = getAdjustKeyProvider().getHighestAllowedKey();
        if (newMaxKeyInDB != null) {
            if (newMaxKeyInDB.isAfter(maxKeyInDB)) {
                maxKeyInDB = newMaxKeyInDB;
                return true;
            } else {
                return false;
            }
        }
        //fallback to normal procedure if curHighWaterMark is not provided by provider
        if (maxKeyInDB == null || force) {
            final V maxValue = readNewestValueFromDB(maxKey());
            if (maxValue != null) {
                final FDate maxValueKey = extractKey(key, maxValue);
                if (maxKeyInDB == null || maxValueKey.compareTo(maxKeyInDB) <= -1) {
                    maxKeyInDB = maxValueKey;
                    getValuesMap().put(maxValueKey, maxValue);
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
                final FDate minValueKey = extractKey(key, minValue);
                //min key must be kept intact if all values have been loaded from a later key
                if (minKeyInDB == null || minValueKey.compareTo(minKeyInDB) <= -1) {
                    minKeyInDB = minValueKey;
                    getValuesMap().put(minValueKey, minValue);
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
            return query().withFuture().getValue(maxKeyInDB);
        }
        return (V) null;
    }

    private V eventuallyGetMinValue(final FDate key, final boolean newMinKey) {
        //if key < minKey; use value for minKey
        if (minKeyInDB != null) {
            final boolean afterMinKey = !newMinKey && key.compareTo(minKey) >= 0;
            if (afterMinKey && key.compareTo(minKeyInDB) <= 0 && containsKey(minKey)) {
                //via readNewestValueTo
                return query().withFuture().getValue(minKey);
            }
            if (key.compareTo(minKeyInDB) <= 0 && containsKey(minKeyInDB)) {
                //via searchInFurtherValues
                return query().withFuture().getValue(minKeyInDB);
            }
        }
        return (V) null;
    }

    private boolean eventuallyLoadFurtherValues(final String source, final FDate key, final FDate adjustedKey,
            final boolean newMinKey, final boolean forced) {
        if (forced || shouldLoadFurtherValues(key, newMinKey)) {
            final FDate keyForReadAllValues;
            if (newMinKey && minKeyInDBFromLoadFurtherValues != null && key.isBefore(minKeyInDBFromLoadFurtherValues)) {
                //performance optimization for first load
                keyForReadAllValues = FDate.min(minKeyInDB, FDate.max(minKeyInDB, adjustedKey));
            } else {
                keyForReadAllValues = FDate.max(minKeyInDB, adjustedKey);
            }
            furtherValues.clear();
            lastValuesFromFurtherValues.clear();
            furtherValues.addAll(readAllValuesAscendingFrom(keyForReadAllValues));

            if (!furtherValues.isEmpty()) {
                assertFurtherValuesSorting(key);
            }
            return true;
        }
        return false;
    }

    protected boolean allowNoDataInDBShortcut() {
        return true;
    }

    private boolean shouldLoadFurtherValues(final FDate key, final boolean newMinKey) {
        if (furtherValues.isEmpty()) {
            return true;
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
                && FDate.isSameMillisecond(minKeyInDBFromLoadFurtherValues, minKeyInDB);
    }

    private void assertFurtherValuesSorting(final FDate key) {
        final FDate firstKey = extractKey(key, furtherValues.getHead());
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
        minKeyInDBFromLoadFurtherValues = FDate.min(minKeyInDBFromLoadFurtherValues, firstKey);
        final FDate lastKey = extractKey(key, furtherValues.getTail());
        if (maxKeyInDB == null || lastKey.compareTo(maxKeyInDB) <= -1) {
            maxKeyInDB = FDate.max(maxKeyInDB, lastKey);
        }
        maxKeyInDBFromLoadFurtherValues = FDate.max(maxKeyInDBFromLoadFurtherValues, lastKey);

        if (furtherValues.size() > 1) {
            Assertions.assertThat(firstKey.compareTo(lastKey) <= 0)
            .as("Not ascending sorted! At firstKey [%s] and lastKey [%s]", firstKey, lastKey)
            .isTrue();
        }
    }

    private V searchInFurtherValues(final FDate key) {
        //Take the first matching value from the sorted list
        //Search for the newest value
        V prevValue = (V) null;
        FDate prevKey = null;
        if (!lastValuesFromFurtherValues.isEmpty() && !furtherValues.isEmpty()) {
            //though maybe use the last one for smaller increments than the data itself is loaded
            for (final V lastValueFromFurtherValues : lastValuesFromFurtherValues) {
                final FDate keyLastValueFromFurtherValues = extractKey(key, lastValueFromFurtherValues);
                if (keyLastValueFromFurtherValues.isBeforeOrEqual(key)) {
                    prevValue = lastValueFromFurtherValues;
                    prevKey = keyLastValueFromFurtherValues;
                } else {
                    //only go to further values if it might be possible that those are useable
                    return prevValue;
                }
            }
        }

        final FDate earliestStartOfLoadFurtherValues = determineEaliestStartOfLoadFurtherValues(key);
        while (furtherValues.size() > 0) {
            final V newValue = furtherValues.getHead();
            final FDate newValueKey = extractKey(key, newValue);
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
                put(newValueKey, newValue, prevKey, prevValue);
                pushLastValueFromFurtherValues();
                //continue with the next one
                prevValue = newValue;
                prevKey = newValueKey;

                if (furtherValues.isEmpty() && newValueKey.isBefore(maxKeyInDB) && key.isBefore(maxKeyInDB)
                        && maxKeyInDBFromLoadFurtherValues.isBefore(maxKeyInDB)) {
                    final FDate timeForLoadFurtherValues = FDate.max(newValueKey, earliestStartOfLoadFurtherValues);
                    Assertions.assertThat(
                            eventuallyLoadFurtherValues("searchInFurtherValues", newValueKey, timeForLoadFurtherValues,
                                    false, true)).isTrue();
                    if (!furtherValues.isEmpty()) {
                        pushLastValueFromFurtherValues();
                        if (!timeForLoadFurtherValues.equals(newValue)) {
                            //do not distort prev/next lookup when using earlisetStartOfLoadFurtherValues, thus reset those
                            prevValue = null;
                            prevKey = null;
                        }
                    }
                }
            }
        }
        return prevValue;
    }

    private void pushLastValueFromFurtherValues() {
        while (lastValuesFromFurtherValues.size() >= MAX_LAST_VALUES_FROM_LOAD_FURTHER_VALUES) {
            lastValuesFromFurtherValues.next();
        }
        lastValuesFromFurtherValues.add(furtherValues.next());
    }

    /**
     * when this does not match, then getLatestValue will be used automatically anyway to go further back in time
     */
    private FDate determineEaliestStartOfLoadFurtherValues(final FDate key) {
        //1 day is fine for most cases
        return key.addMilliseconds(-getReadBackStepMillis());
    }

    protected long getReadBackStepMillis() {
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
        if (newMaxKey && previousMaxKey != null && containsKey(previousMaxKey)) {
            //use the last maxKey
            //because this one is behind it and not a new one
            //thus working if the db does not have further values
            return query().withFuture().getValue(previousMaxKey);
        }
        return (V) null;
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
            final FDate valueKey = extractKey(key, value);
            getValuesMap().put(valueKey, value);
            return value;
        } else {
            return (V) null;
        }
    }

    protected abstract Iterable<? extends V> readAllValuesAscendingFrom(final FDate key);

    /**
     * This method first tries to load the nearest neighbor value to the given key. First it tries to load values <=
     * key, if this fails it tries to load values >= key. If the caller does not accept values from the future, this
     * will get handled properly later. For example financial backtests may not use values from the future to keep the
     * test realistic.
     */
    protected abstract V readLatestValueFor(final FDate key);

    @Override
    protected synchronized boolean maybeRefresh() {
        if (eventuallyGetMinMaxKeysInDB(maxKey(), true)) {
            return super.maybeRefresh();
        }
        return false;
    }

    @Override
    public synchronized void clear() {
        super.clear();
        //remove flags so that the limit check gets skipped if get has not been called yet and this method might be called again
        maxKeyInDB = null;
        minKeyInDB = null;
        //a clear forces the list to be completely reloaded next time get is called
        furtherValues.clear();
        lastValuesFromFurtherValues.clear();
    }

}
