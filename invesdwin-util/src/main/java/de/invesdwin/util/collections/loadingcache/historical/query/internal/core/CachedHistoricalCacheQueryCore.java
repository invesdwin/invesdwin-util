package de.invesdwin.util.collections.loadingcache.historical.query.internal.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.assertj.core.util.Lists;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.SingleValueIterable;
import de.invesdwin.util.collections.iterable.WrapperCloseableIterable;
import de.invesdwin.util.collections.loadingcache.historical.AHistoricalCache;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.HistoricalCacheAssertValue;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.HistoricalCacheQuery;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.IHistoricalCacheInternalMethods;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.core.impl.GetPreviousEntryQueryImpl;
import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.time.fdate.FDate;

@ThreadSafe
public class CachedHistoricalCacheQueryCore<V> implements IHistoricalCacheQueryCore<V> {

    private static final int INITIAL_MAX_CACHED_INDEX = 10000;
    private static final org.slf4j.ext.XLogger LOG = org.slf4j.ext.XLoggerFactory
            .getXLogger(CachedHistoricalCacheQueryCore.class);
    private static final int REQUIRED_SIZE_MULTIPLICATOR = 2;
    private static final int COUNT_RESETS_BEFORE_WARNING = 100;

    private int countResets = 0;
    private final DefaultHistoricalCacheQueryCore<V> delegate;
    @GuardedBy("this")
    private final List<Entry<FDate, V>> cachedPreviousEntries = new ArrayList<>();
    @GuardedBy("this")
    private FDate cachedPreviousEntriesKey = null;
    @GuardedBy("this")
    private List<Entry<FDate, V>> cachedPreviousResult_notFilteringDuplicates = null;
    @GuardedBy("this")
    private List<Entry<FDate, V>> cachedPreviousResult_filteringDuplicates = null;
    @GuardedBy("this")
    private Integer cachedPreviousResult_shiftBackUnits = null;
    private volatile boolean cachedQueryActive;
    @GuardedBy("this")
    private int maxCachedIndex;

    public CachedHistoricalCacheQueryCore(final IHistoricalCacheInternalMethods<V> parent) {
        this.delegate = new DefaultHistoricalCacheQueryCore<V>(parent);
        this.maxCachedIndex = Integers.max(INITIAL_MAX_CACHED_INDEX, parent.getMaximumSize());
    }

    @Override
    public IHistoricalCacheInternalMethods<V> getParent() {
        return delegate.getParent();
    }

    @Override
    public Entry<FDate, V> getPreviousEntry(final IHistoricalCacheQueryInternalMethods<V> query, final FDate key,
            final int shiftBackUnits) {
        if (shiftBackUnits == 0) {
            return delegate.getPreviousEntry(query, key, 0);
        } else {
            //use arraylist since we don't want to have the overhead of filtering duplicates
            final boolean filterDuplicateKeys = false;
            final int incrementedShiftBackUnits = shiftBackUnits + 1;
            final List<Entry<FDate, V>> previousEntries = getPreviousEntriesList(query, key, incrementedShiftBackUnits,
                    filterDuplicateKeys);
            if (previousEntries.isEmpty()) {
                return null;
            } else {
                return previousEntries.get(0);
            }
        }
    }

    private void maybeIncreaseMaximumSize(final int requiredSize) {
        final int adjRequiredSize = requiredSize * REQUIRED_SIZE_MULTIPLICATOR;
        final Integer maximumSize = getParent().getMaximumSize();
        if (maximumSize != null && maximumSize < requiredSize) {
            getParent().increaseMaximumSize(adjRequiredSize, CachedHistoricalCacheQueryCore.class.getSimpleName()
                    + " encountered higher shiftBackUnits of " + requiredSize);
        }
        maxCachedIndex = Math.max(maxCachedIndex, adjRequiredSize);
    }

    @Override
    public ICloseableIterable<Entry<FDate, V>> getPreviousEntries(final IHistoricalCacheQueryInternalMethods<V> query,
            final FDate key, final int shiftBackUnits) {
        if (shiftBackUnits == 1) {
            final Entry<FDate, V> entry = delegate.getPreviousEntry(query, key, 0);
            return new SingleValueIterable<Entry<FDate, V>>(entry);
        } else {
            final List<Entry<FDate, V>> result = getPreviousEntriesList(query, key, shiftBackUnits,
                    query.isFilterDuplicateKeys());
            return WrapperCloseableIterable.maybeWrap(result);
        }
    }

    private List<Entry<FDate, V>> getPreviousEntriesList(final IHistoricalCacheQueryInternalMethods<V> query,
            final FDate key, final int shiftBackUnits, final boolean filterDuplicateKeys) {
        synchronized (getLock()) {
            if (cachedQueryActive) {
                //prevent nested/recursive cached queries that might f**k up the cache
                final List<Entry<FDate, V>> trailing = newEntriesList(query, shiftBackUnits, filterDuplicateKeys);
                final List<Entry<FDate, V>> result = defaultGetPreviousEntries(query, shiftBackUnits, key, trailing);
                return result;
            } else {
                cachedQueryActive = true;
                try {
                    try {
                        final List<Entry<FDate, V>> result = tryCachedGetPreviousEntriesIfAvailable(query, key,
                                shiftBackUnits, filterDuplicateKeys);
                        return result;
                    } catch (final ResetCacheException e) {
                        countResets++;
                        if (countResets % COUNT_RESETS_BEFORE_WARNING == 0
                                || AHistoricalCache.isDebugAutomaticReoptimization()) {
                            if (LOG.isWarnEnabled()) {
                                //CHECKSTYLE:OFF
                                LOG.warn(
                                        "{}: resetting {} for the {}. time now and retrying after exception [{}: {}], if this happens too often we might encounter bad performance due to inefficient caching",
                                        delegate.getParent(), getClass().getSimpleName(), countResets,
                                        e.getClass().getSimpleName(), e.getMessage());
                                //CHECKSTYLE:ON
                            }
                        }
                        resetForRetry();
                        try {
                            final List<Entry<FDate, V>> result = tryCachedGetPreviousEntriesIfAvailable(query, key,
                                    shiftBackUnits, filterDuplicateKeys);
                            return result;
                        } catch (final ResetCacheException e1) {
                            throw new RuntimeException("Follow up " + ResetCacheException.class.getSimpleName()
                                    + " on retry after:" + e.toString(), e1);
                        }
                    }
                } finally {
                    cachedQueryActive = false;
                }
            }
        }
    }

    private Object getLock() {
        return getParent().getLock();
    }

    private List<Entry<FDate, V>> tryCachedGetPreviousEntriesIfAvailable(
            final IHistoricalCacheQueryInternalMethods<V> query, final FDate key, final int shiftBackUnits,
            final boolean filterDuplicateKeys) throws ResetCacheException {
        List<Entry<FDate, V>> result;
        if (!cachedPreviousEntries.isEmpty()) {
            result = cachedGetPreviousEntries(query, shiftBackUnits, key, filterDuplicateKeys);
            if (!result.isEmpty() && query.getAssertValue() != HistoricalCacheAssertValue.ASSERT_VALUE_WITH_FUTURE) {
                //fix when first withFuture and then withFutureNull is called on the cached result
                final Entry<FDate, V> firstResult = result.get(0);
                final Entry<FDate, V> assertedValue = query.getAssertValue().assertValue(delegate.getParent(), key,
                        firstResult.getKey(), firstResult.getValue());
                if (assertedValue == null) {
                    result = Lists.emptyList();
                }
            }
        } else {
            final List<Entry<FDate, V>> trailing = newEntriesList(query, shiftBackUnits, filterDuplicateKeys);
            result = defaultGetPreviousEntries(query, shiftBackUnits, key, trailing);
            updateCachedPreviousResult(query, shiftBackUnits, result, filterDuplicateKeys);
        }
        return result;
    }

    private List<Entry<FDate, V>> cachedGetPreviousEntries(final IHistoricalCacheQueryInternalMethods<V> query,
            final int shiftBackUnits, final FDate key, final boolean filterDuplicateKeys) throws ResetCacheException {
        if (Objects.equals(key, cachedPreviousEntriesKey) || Objects.equals(key, getLastCachedEntry().getKey())) {
            final List<Entry<FDate, V>> tryCachedPreviousResult = tryCachedPreviousResult_sameKey(query, shiftBackUnits,
                    filterDuplicateKeys);
            if (tryCachedPreviousResult != null) {
                return tryCachedPreviousResult;
            } else {
                final List<Entry<FDate, V>> trailing = newEntriesList(query, shiftBackUnits, filterDuplicateKeys);
                final List<Entry<FDate, V>> result = cachedGetPreviousEntries_sameKey(query, shiftBackUnits, key,
                        trailing);
                updateCachedPreviousResult(query, shiftBackUnits, result, filterDuplicateKeys);
                return result;
            }
        } else if ((key.isAfter(cachedPreviousEntriesKey) || key.isAfter(getLastCachedEntry().getKey()))
                /*
                 * when we go a higher key and only want to load 1 value, we can just go with direct map access since we
                 * won't hit cache anyway because first access will be against map anyway. we make sure to not replace
                 * the cached values in this case in the default query
                 */
                && shiftBackUnits > 1) {
            final List<Entry<FDate, V>> tryCachedPreviousResult = tryCachedPreviousResult_incrementedKey(query,
                    shiftBackUnits, filterDuplicateKeys, key);
            if (tryCachedPreviousResult != null) {
                return tryCachedPreviousResult;
            } else {
                final List<Entry<FDate, V>> trailing = newEntriesList(query, shiftBackUnits, filterDuplicateKeys);
                final List<Entry<FDate, V>> result = cachedGetPreviousEntries_incrementedKey(query, shiftBackUnits, key,
                        trailing);
                updateCachedPreviousResult(query, shiftBackUnits, result, filterDuplicateKeys);
                return result;
            }
        } else if (key.isBeforeOrEqualTo(cachedPreviousEntriesKey)
                && key.isAfterOrEqualTo(getFirstCachedEntry().getKey())
                && key.isBeforeOrEqualTo(getLastCachedEntry().getKey())) {
            final List<Entry<FDate, V>> trailing = newEntriesList(query, shiftBackUnits, filterDuplicateKeys);
            return cachedGetPreviousEntries_decrementedKey(query, shiftBackUnits, key, trailing);
        } else {
            /*
             * value will not be found in cache (we are before the first cached entry), so we just go with the default
             * query and renew the cache if possible; jumping around wildly in the history is expensive right now
             */
            final List<Entry<FDate, V>> trailing = newEntriesList(query, shiftBackUnits, filterDuplicateKeys);
            final List<Entry<FDate, V>> result = defaultGetPreviousEntries(query, shiftBackUnits, key, trailing);
            updateCachedPreviousResult(query, shiftBackUnits, result, filterDuplicateKeys);
            return result;
        }
    }

    /**
     * Use sublist if possible to reduce memory footprint of transient array lists to reduce garbage collection overhead
     * 
     * @throws ResetCacheException
     */

    private List<Entry<FDate, V>> tryCachedPreviousResult_incrementedKey(
            final IHistoricalCacheQueryInternalMethods<V> query, final int shiftBackUnits,
            final boolean filterDuplicateKeys, final FDate key) throws ResetCacheException {
        if (cachedPreviousEntries.isEmpty() || cachedPreviousResult_shiftBackUnits == null) {
            return null;
        }
        if (shiftBackUnits > cachedPreviousResult_shiftBackUnits + 1) {
            resetCachedPreviousResult();
            return null;
        }
        final FDate lastCachedEntryKey = determineConsistentLastCachedEntryKey();

        //go through query as long as we found the first entry in the cache
        final GetPreviousEntryQueryImpl<V> impl = new GetPreviousEntryQueryImpl<V>(this, query, key, shiftBackUnits);
        Entry<FDate, V> latestValue = null;
        while (!impl.iterationFinished()) {
            final Entry<FDate, V> value = impl.getResult();
            if (value == null) {
                resetCachedPreviousResult();
                return null; //abort since we could not find any values
            } else {
                if (value.getKey().equals(lastCachedEntryKey)) {
                    break; //continue with tryCachedPreviousResult_sameKey
                } else {
                    if (latestValue == null) {
                        latestValue = value;
                    } else {
                        resetCachedPreviousResult();
                        return null; //abort since we could not find just one new value
                    }
                }
            }
        }

        appendCachedEntryAndResult(query, key, shiftBackUnits, latestValue);
        return tryCachedPreviousResult_sameKey(query, shiftBackUnits, filterDuplicateKeys);
    }

    private FDate determineConsistentLastCachedEntryKey() throws ResetCacheException {
        final FDate lastCachedEntryKey = getLastCachedEntry().getKey();
        if (cachedPreviousResult_filteringDuplicates != null && !cachedPreviousResult_filteringDuplicates.isEmpty()) {
            final FDate lastCachedResultKey = cachedPreviousResult_filteringDuplicates
                    .get(cachedPreviousResult_filteringDuplicates.size() - 1)
                    .getKey();
            assertSameLastKey(lastCachedEntryKey, lastCachedResultKey);
        }
        if (cachedPreviousResult_notFilteringDuplicates != null
                && !cachedPreviousResult_notFilteringDuplicates.isEmpty()) {
            final FDate lastCachedResultKey = cachedPreviousResult_notFilteringDuplicates
                    .get(cachedPreviousResult_notFilteringDuplicates.size() - 1)
                    .getKey();
            assertSameLastKey(lastCachedEntryKey, lastCachedResultKey);
        }
        return lastCachedEntryKey;
    }

    private void assertSameLastKey(final FDate lastCachedEntryKey, final FDate lastCachedResultKey)
            throws ResetCacheException {
        if (!lastCachedEntryKey.equals(lastCachedResultKey)) {
            throw new ResetCacheException(
                    "lastCachedEntryKey[" + lastCachedEntryKey + "] != lastCachedResultKey[" + lastCachedResultKey
                            + "], might happen on far reaching recursive queries or long looped queries into the past");
        }
    }

    /**
     * Use sublist if possible to reduce memory footprint of transient array lists to reduce garbage collection overhead
     */

    private List<Entry<FDate, V>> tryCachedPreviousResult_sameKey(final IHistoricalCacheQueryInternalMethods<V> query,
            final int shiftBackUnits, final boolean filterDuplicateKeys) {
        if (cachedPreviousResult_shiftBackUnits == null || cachedPreviousResult_shiftBackUnits < shiftBackUnits) {
            return null;
        }
        if (filterDuplicateKeys) {
            if (cachedPreviousResult_filteringDuplicates == null) {
                if (cachedPreviousResult_notFilteringDuplicates == null) {
                    return null;
                } else {
                    cachedPreviousResult_filteringDuplicates = query
                            .newEntriesList(cachedPreviousResult_shiftBackUnits);
                    cachedPreviousResult_filteringDuplicates.addAll(cachedPreviousResult_notFilteringDuplicates);
                }
            }
            final int toIndex = cachedPreviousResult_filteringDuplicates.size();
            final int fromIndex = Math.max(0, toIndex - shiftBackUnits);
            return cachedPreviousResult_filteringDuplicates.subList(fromIndex, toIndex);
        } else {
            if (cachedPreviousResult_notFilteringDuplicates == null) {
                if (cachedPreviousResult_filteringDuplicates == null) {
                    return null;
                } else {
                    cachedPreviousResult_notFilteringDuplicates = new ArrayList<Entry<FDate, V>>(
                            cachedPreviousResult_filteringDuplicates);
                    final int duplicatesRemaining = cachedPreviousResult_shiftBackUnits
                            - cachedPreviousResult_notFilteringDuplicates.size();
                    if (duplicatesRemaining > 0 && !cachedPreviousResult_filteringDuplicates.isEmpty()) {
                        final Entry<FDate, V> toBeDuplicated = cachedPreviousResult_filteringDuplicates.get(0);
                        for (int i = 0; i < duplicatesRemaining; i++) {
                            cachedPreviousResult_notFilteringDuplicates.add(0, toBeDuplicated);
                        }
                    }
                }
            }
            final int toIndex = cachedPreviousResult_notFilteringDuplicates.size();
            final int fromIndex = Math.max(0, toIndex - shiftBackUnits);
            return cachedPreviousResult_notFilteringDuplicates.subList(fromIndex, toIndex);
        }
    }

    private List<Entry<FDate, V>> newEntriesList(final IHistoricalCacheQueryInternalMethods<V> query,
            final int shiftBackUnits, final boolean filterDuplicateKeys) {
        if (filterDuplicateKeys) {
            return query.newEntriesList(shiftBackUnits);
        } else {
            return new ArrayList<Entry<FDate, V>>();
        }
    }

    /**
     * This needs to be called wherever replaceCachedEntries() was called before
     * 
     * @throws ResetCacheException
     */

    private void updateCachedPreviousResult(final IHistoricalCacheQueryInternalMethods<V> query,
            final int shiftBackUnits, final List<Entry<FDate, V>> result, final boolean filterDuplicateKeys)
            throws ResetCacheException {
        if (result.isEmpty() && query.getAssertValue() == HistoricalCacheAssertValue.ASSERT_VALUE_WITH_FUTURE_NULL) {
            //do not remember an empty result with future null (a call with future next might trip on it)
            return;
        }
        if (cachedPreviousResult_shiftBackUnits != null && !cachedPreviousEntries.isEmpty() && result.size() > 1) {
            throw new IllegalStateException("cachedPreviousResult should have been reset by preceeding code!");
        }
        if (filterDuplicateKeys) {
            cachedPreviousResult_filteringDuplicates = result;
            cachedPreviousResult_notFilteringDuplicates = null;
        } else {
            cachedPreviousResult_filteringDuplicates = null;
            cachedPreviousResult_notFilteringDuplicates = result;
        }
        cachedPreviousResult_shiftBackUnits = shiftBackUnits;
    }

    private List<Entry<FDate, V>> cachedGetPreviousEntries_decrementedKey(
            final IHistoricalCacheQueryInternalMethods<V> query, final int shiftBackUnits, final FDate key,
            final List<Entry<FDate, V>> trailing) throws ResetCacheException {
        int unitsBack = shiftBackUnits - 1;
        unitsBack = fillFromCacheAsFarAsPossible(trailing, unitsBack, key);
        if (unitsBack == -1) {
            //we could satisfy the query completely with cached values
            //cached values don't have to be updated
            return trailing;
        } else {
            final int trailingCountFoundInCache = trailing.size();
            loadFurtherTrailingValuesViaQuery(query, trailing, unitsBack);
            prependCachedEntries(key, trailing, trailingCountFoundInCache);
            return trailing;
        }
    }

    private List<Entry<FDate, V>> cachedGetPreviousEntries_incrementedKey(
            final IHistoricalCacheQueryInternalMethods<V> query, final int shiftBackUnits, final FDate key,
            final List<Entry<FDate, V>> trailing) throws ResetCacheException {
        int unitsBack = fillFromQueryUntilCacheCanBeUsed(query, shiftBackUnits, key, trailing);

        if (unitsBack == -1) {
            //we can replace the cache since it was too far away anyway
            replaceCachedEntries(key, trailing);
            //we were above the cache and did not find anything useful in the cache, so we filled everything from query
            return trailing;
        }

        final int trailingCountFoundInQuery = trailing.size();

        //then fill the rest from the cache as far as possible
        unitsBack = fillFromCacheAsFarAsPossible(trailing, unitsBack, null);

        if (unitsBack == -1) {
            //add elments from query since we found newer values and were able to use the cache
            appendCachedEntries(key, trailing, trailingCountFoundInQuery);

            //we could satisfy the query completely with cached values
            return trailing;
        } else {
            //and use the query again if there are missing elements at the end
            loadFurtherTrailingValuesViaQuery(query, trailing, unitsBack);
            //we can replace the cache since we extended it in both directions
            replaceCachedEntries(key, trailing);
            return trailing;
        }
    }

    private int fillFromQueryUntilCacheCanBeUsed(final IHistoricalCacheQueryInternalMethods<V> query,
            final int shiftBackUnits, final FDate key, final List<Entry<FDate, V>> trailing)
            throws ResetCacheException {
        int unitsBack = shiftBackUnits - 1;
        //go through query as long as we found the first entry in the cache
        final GetPreviousEntryQueryImpl<V> impl = new GetPreviousEntryQueryImpl<V>(this, query, key, shiftBackUnits);
        final FDate lastCachedEntryKey = getLastCachedEntry().getKey();
        final List<Entry<FDate, V>> trailingReverse;
        if (trailing.isEmpty()) {
            trailingReverse = trailing;
        } else {
            trailingReverse = query.newEntriesList(shiftBackUnits);
        }
        while (unitsBack >= 0 && !impl.iterationFinished()) {
            final Entry<FDate, V> value = impl.getResult();
            if (value != null) {
                if (value.getKey().equals(lastCachedEntryKey)) {
                    break; //continue with fillFromCacheAsFarAsPossible
                } else {
                    if (trailingReverse.add(value)) {
                        unitsBack--;
                    } else {
                        unitsBack = -1; //break
                    }
                }
            } else {
                unitsBack = -1; //break
            }
        }
        Collections.reverse(trailingReverse);
        if (trailing != trailingReverse) {
            trailing.addAll(0, trailingReverse);
        }
        return unitsBack;
    }

    private void prependCachedEntries(final FDate key, final List<Entry<FDate, V>> trailing,
            final int trailingCountFoundInCache) throws ResetCacheException {
        for (int i = trailing.size() - trailingCountFoundInCache - 1; i >= 0; i--) {
            final Entry<FDate, V> prependEntry = trailing.get(i);
            if (!cachedPreviousEntries.isEmpty()) {
                final Entry<FDate, V> firstCachedEntry = getFirstCachedEntry();
                if (!firstCachedEntry.getKey().isAfterOrEqualTo(prependEntry.getKey())) {
                    throw new IllegalStateException("prependEntry [" + prependEntry.getKey()
                            + "] should be after firstCachedEntry [" + firstCachedEntry.getKey() + "]");
                }
            }
            cachedPreviousEntries.add(0, prependEntry);
        }
        final Integer maximumSize = getParent().getMaximumSize();
        if (maximumSize != null) {
            maybeIncreaseMaximumSize(trailing.size());
            //ensure we stay in size limit
            while (cachedPreviousEntries.size() > maximumSize) {
                /*
                 * since we are going further back in time, we have to remove current values. We expect to go further
                 * back and will live with the cost of loading again the current values next time
                 */
                cachedPreviousEntries.remove(cachedPreviousEntries.size() - 1);
            }
            //reset cached results and set new marker so that we don't go accidentally into sameKey algorithm
            resetCachedPreviousResult();
            cachedPreviousEntriesKey = getLastCachedEntry().getKey();
        }
    }

    private void appendCachedEntries(final FDate key, final List<Entry<FDate, V>> trailing,
            final int trailingCountFoundInQuery) throws ResetCacheException {
        for (int i = trailing.size() - trailingCountFoundInQuery; i < trailing.size(); i++) {
            final Entry<FDate, V> appendEntry = trailing.get(i);
            if (!cachedPreviousEntries.isEmpty()) {
                final Entry<FDate, V> lastCachedEntry = getLastCachedEntry();
                if (!lastCachedEntry.getKey().isBefore(appendEntry.getKey())) {
                    throw new ResetCacheException("appendEntry [" + appendEntry.getKey()
                            + "] should be before firstCachedEntry [" + lastCachedEntry.getKey() + "]");
                }
            }
            cachedPreviousEntries.add(appendEntry);
        }
        cachedPreviousEntriesKey = key;
        final Integer maximumSize = getParent().getMaximumSize();
        if (maximumSize != null) {
            maybeIncreaseMaximumSize(trailing.size());
            //ensure we stay in size limit
            while (cachedPreviousEntries.size() > maximumSize) {
                cachedPreviousEntries.remove(0);
            }
        }
    }

    //CHECKSTYLE:OFF
    private void appendCachedEntryAndResult(final IHistoricalCacheQueryInternalMethods<V> query, final FDate key,
            final int shiftBackUnits, final Entry<FDate, V> latestEntry) throws ResetCacheException {
        //CHECKSTYLE:ON
        if (latestEntry != null) {
            if (cachedPreviousResult_shiftBackUnits == null) {
                throw new ResetCacheException(
                        "cachedPreviousResult_shiftBackUnits is null even though it should be extended");
            }

            cachedPreviousEntries.add(latestEntry);

            if (cachedPreviousResult_shiftBackUnits < shiftBackUnits) {
                //we added one more element and there is still demand for more
                cachedPreviousResult_shiftBackUnits++;
            }

            if (cachedPreviousResult_filteringDuplicates != null) {
                //ensure we stay in size limit
                cachedPreviousResult_filteringDuplicates.add(latestEntry);
                if (cachedPreviousResult_filteringDuplicates.size() > cachedPreviousResult_shiftBackUnits) {
                    cachedPreviousResult_filteringDuplicates.remove(0);
                }
            }
            if (cachedPreviousResult_notFilteringDuplicates != null) {
                //ensure we stay in size limit
                cachedPreviousResult_notFilteringDuplicates.add(latestEntry);
                if (cachedPreviousResult_notFilteringDuplicates.size() > cachedPreviousResult_shiftBackUnits) {
                    cachedPreviousResult_notFilteringDuplicates.remove(0);
                }
            }

            final Integer maximumSize = getParent().getMaximumSize();
            if (maximumSize != null) {
                //ensure we stay in size limit
                while (cachedPreviousEntries.size() > maximumSize) {
                    cachedPreviousEntries.remove(0);
                }
            }
        }
        cachedPreviousEntriesKey = key;
    }

    private void replaceCachedEntries(final FDate key, final List<Entry<FDate, V>> trailing) {
        if (trailing.isEmpty() ||
        /*
         * (maybe we went before the first entry) or (maybe we went after the last entry to only fetch one element), so
         * we don't want to throw away a cache that might already be filled
         */
                (trailing.size() == 1 && cachedPreviousEntries.size() > 1)) {
            return;
        }
        maybeIncreaseMaximumSize(trailing.size());
        cachedPreviousEntries.clear();
        cachedPreviousEntries.addAll(trailing);
        cachedPreviousEntriesKey = key;
        resetCachedPreviousResult();
    }

    private void resetCachedPreviousResult() {
        cachedPreviousResult_filteringDuplicates = null;
        cachedPreviousResult_notFilteringDuplicates = null;
        cachedPreviousResult_shiftBackUnits = null;
    }

    private List<Entry<FDate, V>> cachedGetPreviousEntries_sameKey(final IHistoricalCacheQueryInternalMethods<V> query,
            final int shiftBackUnits, final FDate key, final List<Entry<FDate, V>> trailing)
            throws ResetCacheException {
        int unitsBack = shiftBackUnits - 1;
        unitsBack = fillFromCacheAsFarAsPossible(trailing, unitsBack, null);
        if (unitsBack == -1) {
            //maybe cached previous result has smaller shift back
            resetCachedPreviousResult();
            //we could satisfy the query completely with cached values
            //cached values don't have to be updated
            return trailing;
        } else {
            loadFurtherTrailingValuesViaQuery(query, trailing, unitsBack);
            replaceCachedEntries(key, trailing);
            return trailing;
        }
    }

    private void loadFurtherTrailingValuesViaQuery(final IHistoricalCacheQueryInternalMethods<V> query,
            final List<Entry<FDate, V>> trailing, final int unitsBack) throws ResetCacheException {
        assertUnitsBackNotExchausted(unitsBack);
        final FDate lastTrailingKey = trailing.get(0).getKey();
        //we need to load further values from the map
        final int skipFirstValueIncrement = 1;
        final GetPreviousEntryQueryImpl<V> impl = new GetPreviousEntryQueryImpl<V>(this, query, lastTrailingKey,
                unitsBack + skipFirstValueIncrement);
        impl.setIterations(skipFirstValueIncrement);
        int newUnitsBack = unitsBack;
        newUnitsBack = fillFromQuery(query, trailing, impl, newUnitsBack);
    }

    private int fillFromQuery(final IHistoricalCacheQueryInternalMethods<V> query, final List<Entry<FDate, V>> trailing,
            final GetPreviousEntryQueryImpl<V> impl, final int unitsBack) {
        int newUnitsBack = unitsBack;
        final List<Entry<FDate, V>> trailingReverse;
        if (trailing.isEmpty()) {
            trailingReverse = trailing;
        } else {
            trailingReverse = query.newEntriesList(unitsBack);
        }
        while (newUnitsBack >= 0 && !impl.iterationFinished()) {
            final Entry<FDate, V> value = impl.getResult();
            if (value != null) {
                if (!trailingReverse.add(value)) {
                    newUnitsBack = -1; //break
                    break;
                }
            } else {
                break;
            }
            newUnitsBack--;
        }
        Collections.reverse(trailingReverse);
        if (trailing != trailingReverse) {
            trailing.addAll(0, trailingReverse);
        }
        return newUnitsBack;
    }

    private void assertUnitsBackNotExchausted(final int unitsBack) throws ResetCacheException {
        if (unitsBack < 0) {
            throw new IllegalStateException("unitsBack should not become smaller than -1: " + unitsBack);
        }
    }

    private List<Entry<FDate, V>> defaultGetPreviousEntries(final IHistoricalCacheQueryInternalMethods<V> query,
            final int shiftBackUnits, final FDate key, final List<Entry<FDate, V>> trailing) {
        final GetPreviousEntryQueryImpl<V> impl = new GetPreviousEntryQueryImpl<V>(this, query, key, shiftBackUnits);
        int unitsBack = shiftBackUnits - 1;
        unitsBack = fillFromQuery(query, trailing, impl, unitsBack);
        replaceCachedEntries(key, trailing);
        return trailing;
    }

    private int fillFromCacheAsFarAsPossible(final List<Entry<FDate, V>> trailing, final int unitsBack,
            final FDate skippingKeysAbove) throws ResetCacheException {
        //prefill what is possible and add suffixes by query as needed
        final int cachedToIndex;
        if (skippingKeysAbove != null) {
            cachedToIndex = bisect(skippingKeysAbove, unitsBack);
        } else {
            cachedToIndex = cachedPreviousEntries.size() - 1;
        }

        final int toIndex = cachedToIndex + 1;
        final int fromIndex = Math.max(0, toIndex - unitsBack - 1);
        final int size = toIndex - fromIndex;
        final int newUnitsBack = unitsBack - size;
        trailing.addAll(0, cachedPreviousEntries.subList(fromIndex, toIndex));

        return newUnitsBack;
    }

    private int bisect(final FDate skippingKeysAbove, final int unitsBack) throws ResetCacheException {
        int lo = 0;
        int hi = cachedPreviousEntries.size();
        FDate loTime = cachedPreviousEntries.get(lo).getKey();
        if (skippingKeysAbove.isBeforeOrEqualTo(loTime) && hi >= maxCachedIndex) {
            throw new ResetCacheException("Not enough data in cache for fillFromCacheAsFarAsPossible [" + unitsBack
                    + "/" + maxCachedIndex + "/" + (hi - 1) + "]");
        }

        //bisect
        while (lo < hi) {
            final int mid = (lo + hi) / 2;
            //if (x < list.get(mid)) {
            final FDate midKey = cachedPreviousEntries.get(mid).getKey();
            final int compareTo = midKey.compareTo(skippingKeysAbove);
            switch (compareTo) {
            case -1:
                lo = mid + 1;
                break;
            case 0:
                return mid;
            case 1:
                hi = mid;
                break;
            default:
                throw UnknownArgumentException.newInstance(Integer.class, compareTo);
            }
        }
        loTime = cachedPreviousEntries.get(lo).getKey();
        if (loTime.isAfter(skippingKeysAbove)) {
            return lo - 1;
        } else {
            return lo;
        }
    }

    private Entry<FDate, V> getLastCachedEntry() throws ResetCacheException {
        if (cachedPreviousEntries.isEmpty()) {
            throw new ResetCacheException("lastCachedEntry cannot be retrieved since cachedPreviousEntries is empty");
        }
        return cachedPreviousEntries.get(cachedPreviousEntries.size() - 1);
    }

    private Entry<FDate, V> getFirstCachedEntry() {
        return cachedPreviousEntries.get(0);
    }

    @Override
    public ICloseableIterable<Entry<FDate, V>> getNextEntries(final IHistoricalCacheQueryInternalMethods<V> query,
            final FDate key, final int shiftForwardUnits) {
        return delegate.getNextEntries(query, key, shiftForwardUnits);
    }

    @Override
    public Entry<FDate, V> getNextEntry(final IHistoricalCacheQueryInternalMethods<V> query, final FDate key,
            final int shiftForwardUnits) {
        return delegate.getNextEntry(query, key, shiftForwardUnits);
    }

    @Override
    public void clear() {
        if (!cachedQueryActive) {
            synchronized (getLock()) {
                resetForRetry();
                countResets = 0;
            }
        }
    }

    private void resetForRetry() {
        delegate.clear();
        cachedPreviousEntries.clear();
        cachedPreviousEntriesKey = null;
        resetCachedPreviousResult();
    }

    @Override
    public void increaseMaximumSize(final int maximumSize) {
        this.maxCachedIndex = Math.max(maxCachedIndex, maximumSize);
    }

    @Override
    public V getValue(final IHistoricalCacheQueryInternalMethods<V> query, final FDate key,
            final HistoricalCacheAssertValue assertValue) {
        return delegate.getValue(query, key, assertValue);
    }

    @Override
    public Entry<FDate, V> getEntry(final IHistoricalCacheQueryInternalMethods<V> query, final FDate key,
            final HistoricalCacheAssertValue assertValue) {
        return delegate.getEntry(query, key, assertValue);
    }

    @Override
    public Entry<FDate, V> computeEntry(final HistoricalCacheQuery<V> historicalCacheQuery, final FDate key,
            final HistoricalCacheAssertValue assertValue) {
        return delegate.computeEntry(historicalCacheQuery, key, assertValue);
    }

}
