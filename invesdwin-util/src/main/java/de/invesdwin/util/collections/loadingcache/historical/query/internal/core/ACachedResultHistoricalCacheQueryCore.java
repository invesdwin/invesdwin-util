package de.invesdwin.util.collections.loadingcache.historical.query.internal.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.bean.tuple.ImmutableEntry;
import de.invesdwin.util.collections.loadingcache.historical.query.index.IndexedFDate;
import de.invesdwin.util.collections.loadingcache.historical.query.index.QueryCoreIndex;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.HistoricalCacheAssertValue;
import de.invesdwin.util.time.fdate.FDate;

@ThreadSafe
public abstract class ACachedResultHistoricalCacheQueryCore<V> extends ACachedEntriesHistoricalCacheQueryCore<V> {

    @GuardedBy("getParent().getLock()")
    protected IndexedFDate cachedPreviousEntriesKey = null;
    @GuardedBy("getParent().getLock()")
    protected List<Entry<FDate, V>> cachedPreviousResult_notFilteringDuplicates = null;
    @GuardedBy("getParent().getLock()")
    protected List<Entry<FDate, V>> cachedPreviousResult_filteringDuplicates = null;
    @GuardedBy("getParent().getLock()")
    protected Integer cachedPreviousResult_shiftBackUnits = null;

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

    @Override
    protected boolean replaceCachedEntries(final FDate key, final List<Entry<FDate, V>> trailing) {
        if (super.replaceCachedEntries(key, trailing)) {
            final IndexedFDate indexedKey = IndexedFDate.maybeWrap(key);
            indexedKey.putQueryCoreIndex(this,
                    new QueryCoreIndex(modCount, cachedPreviousEntries.size() - 1 - modIncrementIndex));
            cachedPreviousEntriesKey = indexedKey;
            resetCachedPreviousResult();
            return true;
        } else {
            return false;
        }
    }

    //CHECKSTYLE:OFF
    protected void appendCachedEntryAndResult(final FDate key, final Integer shiftBackUnits,
            final Entry<FDate, V> latestEntry) throws ResetCacheException {
        //CHECKSTYLE:ON
        if (latestEntry != null) {
            if (shiftBackUnits != null && cachedPreviousResult_shiftBackUnits == null) {
                throw new ResetCacheException(
                        "cachedPreviousResult_shiftBackUnits is null even though it should be extended");
            }

            final IndexedFDate indexedKey = IndexedFDate.maybeWrap(latestEntry.getKey());
            indexedKey.putQueryCoreIndex(this,
                    new QueryCoreIndex(modCount, cachedPreviousEntries.size() - modIncrementIndex));
            final Entry<IndexedFDate, V> indexedEntry = ImmutableEntry.of(indexedKey, latestEntry.getValue());
            cachedPreviousEntries.add(indexedEntry);

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
                    modIncrementIndex--;
                }
            }

        }
        final IndexedFDate indexedKey = IndexedFDate.maybeWrap(key);
        indexedKey.putQueryCoreIndex(this,
                new QueryCoreIndex(modCount, cachedPreviousEntries.size() - 1 - modIncrementIndex));
        cachedPreviousEntriesKey = indexedKey;
    }

    protected FDate determineConsistentLastCachedEntryKey() throws ResetCacheException {
        final IndexedFDate lastCachedEntryKey = getLastCachedEntry().getKey();
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

    private void assertSameLastKey(final IndexedFDate lastCachedEntryKey, final FDate lastCachedResultKey)
            throws ResetCacheException {
        if (!lastCachedEntryKey.equalsNotNullSafe(lastCachedResultKey)) {
            throw new ResetCacheException(
                    "lastCachedEntryKey[" + lastCachedEntryKey + "] != lastCachedResultKey[" + lastCachedResultKey
                            + "], might happen on far reaching recursive queries or long looped queries into the past");
        }
    }

    @Override
    protected void resetForRetry() {
        super.resetForRetry();
        cachedPreviousEntriesKey = null;
        resetCachedPreviousResult();
    }

}
