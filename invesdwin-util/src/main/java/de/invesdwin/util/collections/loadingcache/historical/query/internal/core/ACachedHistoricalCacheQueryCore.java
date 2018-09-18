package de.invesdwin.util.collections.loadingcache.historical.query.internal.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.SingleValueIterable;
import de.invesdwin.util.collections.iterable.WrapperCloseableIterable;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.HistoricalCacheAssertValue;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.HistoricalCacheQuery;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.IHistoricalCacheInternalMethods;
import de.invesdwin.util.time.fdate.FDate;

@ThreadSafe
public abstract class ACachedHistoricalCacheQueryCore<V> implements IHistoricalCacheQueryCore<V> {

    @GuardedBy("getParent().getLock()")
    protected final List<Entry<FDate, V>> cachedPreviousEntries = new ArrayList<>();
    @GuardedBy("getParent().getLock()")
    protected FDate cachedPreviousEntriesKey = null;
    @GuardedBy("getParent().getLock()")
    protected List<Entry<FDate, V>> cachedPreviousResult_notFilteringDuplicates = null;
    @GuardedBy("getParent().getLock()")
    protected List<Entry<FDate, V>> cachedPreviousResult_filteringDuplicates = null;
    @GuardedBy("getParent().getLock()")
    protected Integer cachedPreviousResult_shiftBackUnits = null;

    @Override
    public Entry<FDate, V> getPreviousEntry(final IHistoricalCacheQueryInternalMethods<V> query, final FDate key,
            final int shiftBackUnits) {
        if (shiftBackUnits == 0) {
            return getDelegate().getPreviousEntry(query, key, 0);
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

    @Override
    public IHistoricalCacheInternalMethods<V> getParent() {
        return getDelegate().getParent();
    }

    protected abstract IHistoricalCacheQueryCore<V> getDelegate();

    @Override
    public ICloseableIterable<Entry<FDate, V>> getPreviousEntries(final IHistoricalCacheQueryInternalMethods<V> query,
            final FDate key, final int shiftBackUnits) {
        if (shiftBackUnits == 1) {
            final Entry<FDate, V> entry = getDelegate().getPreviousEntry(query, key, 0);
            return new SingleValueIterable<Entry<FDate, V>>(entry);
        } else {
            final List<Entry<FDate, V>> result = getPreviousEntriesList(query, key, shiftBackUnits,
                    query.isFilterDuplicateKeys());
            return WrapperCloseableIterable.maybeWrap(result);
        }
    }

    protected abstract List<Entry<FDate, V>> getPreviousEntriesList(IHistoricalCacheQueryInternalMethods<V> query,
            FDate key, int shiftBackUnits, boolean filterDuplicateKeys);

    protected List<Entry<FDate, V>> newEntriesList(final IHistoricalCacheQueryInternalMethods<V> query,
            final int shiftBackUnits, final boolean filterDuplicateKeys) {
        if (filterDuplicateKeys) {
            return query.newEntriesList(shiftBackUnits);
        } else {
            return new ArrayList<Entry<FDate, V>>();
        }
    }

    /**
     * Use sublist if possible to reduce memory footprint of transient array lists to reduce garbage collection overhead
     */
    protected List<Entry<FDate, V>> tryCachedPreviousResult_sameKey(final IHistoricalCacheQueryInternalMethods<V> query,
            final int shiftBackUnits, final boolean filterDuplicateKeys) {
        if (cachedPreviousResult_shiftBackUnits == null || cachedPreviousResult_shiftBackUnits < shiftBackUnits) {
            return null;
        }
        if (filterDuplicateKeys) {
            if (cachedPreviousResult_filteringDuplicates == null) {
                if (cachedPreviousResult_notFilteringDuplicates == null) {
                    return null;
                } else {
                    cachedPreviousResult_filteringDuplicates = newEntriesList(query,
                            cachedPreviousResult_shiftBackUnits, true);
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

    /**
     * This needs to be called wherever replaceCachedEntries() was called before
     * 
     * @throws ResetCacheException
     */
    protected void updateCachedPreviousResult(final IHistoricalCacheQueryInternalMethods<V> query,
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
            //defensive copy so that underlying caches do not modify this instance
            cachedPreviousResult_filteringDuplicates = newEntriesList(query, result.size(), true);
            cachedPreviousResult_filteringDuplicates.addAll(result);
            cachedPreviousResult_notFilteringDuplicates = null;
        } else {
            cachedPreviousResult_filteringDuplicates = null;
            //defensive copy so that underlying caches do not modify this instance
            cachedPreviousResult_notFilteringDuplicates = new ArrayList<Entry<FDate, V>>(result);
        }
        cachedPreviousResult_shiftBackUnits = shiftBackUnits;
    }

    protected void resetCachedPreviousResult() {
        cachedPreviousResult_filteringDuplicates = null;
        cachedPreviousResult_notFilteringDuplicates = null;
        cachedPreviousResult_shiftBackUnits = null;
    }

    protected void replaceCachedEntries(final FDate key, final List<Entry<FDate, V>> trailing) {
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

    protected abstract void maybeIncreaseMaximumSize(int requiredSize);

    protected abstract int bisect(FDate skippingKeysAbove, List<Entry<FDate, V>> list, Integer unitsBack)
            throws ResetCacheException;

    //CHECKSTYLE:OFF
    protected void appendCachedEntryAndResult(final FDate key, final Integer shiftBackUnits,
            final Entry<FDate, V> latestEntry) throws ResetCacheException {
        //CHECKSTYLE:ON
        if (latestEntry != null) {
            if (shiftBackUnits != null && cachedPreviousResult_shiftBackUnits == null) {
                throw new ResetCacheException(
                        "cachedPreviousResult_shiftBackUnits is null even though it should be extended");
            }

            cachedPreviousEntries.add(latestEntry);

            if (shiftBackUnits != null && cachedPreviousResult_shiftBackUnits < shiftBackUnits) {
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

    protected FDate determineConsistentLastCachedEntryKey() throws ResetCacheException {
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
        if (!lastCachedEntryKey.equalsNotNullSafe(lastCachedResultKey)) {
            throw new ResetCacheException(
                    "lastCachedEntryKey[" + lastCachedEntryKey + "] != lastCachedResultKey[" + lastCachedResultKey
                            + "], might happen on far reaching recursive queries or long looped queries into the past");
        }
    }

    protected Entry<FDate, V> getLastCachedEntry() throws ResetCacheException {
        if (cachedPreviousEntries.isEmpty()) {
            throw new ResetCacheException("lastCachedEntry cannot be retrieved since cachedPreviousEntries is empty");
        }
        return cachedPreviousEntries.get(cachedPreviousEntries.size() - 1);
    }

    protected Entry<FDate, V> getFirstCachedEntry() {
        return cachedPreviousEntries.get(0);
    }

    protected void resetForRetry() {
        getDelegate().clear();
        cachedPreviousEntries.clear();
        cachedPreviousEntriesKey = null;
        resetCachedPreviousResult();
    }

    @Override
    public final V getValue(final IHistoricalCacheQueryInternalMethods<V> query, final FDate key,
            final HistoricalCacheAssertValue assertValue) {
        return getDelegate().getValue(query, key, assertValue);
    }

    @Override
    public final Entry<FDate, V> getEntry(final IHistoricalCacheQueryInternalMethods<V> query, final FDate key,
            final HistoricalCacheAssertValue assertValue) {
        return getDelegate().getEntry(query, key, assertValue);
    }

    @Override
    public final Entry<FDate, V> computeEntry(final HistoricalCacheQuery<V> historicalCacheQuery, final FDate key,
            final HistoricalCacheAssertValue assertValue) {
        return getDelegate().computeEntry(historicalCacheQuery, key, assertValue);
    }

    @Override
    public final ICloseableIterable<Entry<FDate, V>> getNextEntries(final IHistoricalCacheQueryInternalMethods<V> query,
            final FDate key, final int shiftForwardUnits) {
        return getDelegate().getNextEntries(query, key, shiftForwardUnits);
    }

    @Override
    public final Entry<FDate, V> getNextEntry(final IHistoricalCacheQueryInternalMethods<V> query, final FDate key,
            final int shiftForwardUnits) {
        return getDelegate().getNextEntry(query, key, shiftForwardUnits);
    }

}
