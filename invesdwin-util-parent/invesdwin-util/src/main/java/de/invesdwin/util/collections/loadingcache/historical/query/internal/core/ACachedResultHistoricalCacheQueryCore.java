package de.invesdwin.util.collections.loadingcache.historical.query.internal.core;

import java.util.List;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.loadingcache.historical.IHistoricalEntry;
import de.invesdwin.util.collections.loadingcache.historical.ImmutableHistoricalEntry;
import de.invesdwin.util.collections.loadingcache.historical.query.index.IndexedFDate;
import de.invesdwin.util.collections.loadingcache.historical.query.index.QueryCoreIndex;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.HistoricalCacheAssertValue;
import de.invesdwin.util.time.fdate.FDate;

@ThreadSafe
public abstract class ACachedResultHistoricalCacheQueryCore<V> extends ACachedEntriesHistoricalCacheQueryCore<V> {

    @GuardedBy("cachedQueryActiveLock")
    protected IndexedFDate cachedPreviousEntriesKey = null;
    @GuardedBy("cachedQueryActiveLock")
    protected List<IHistoricalEntry<V>> cachedPreviousResult_filteringDuplicates = null;
    @GuardedBy("cachedQueryActiveLock")
    protected Integer cachedPreviousResult_shiftBackUnits = null;

    /**
     * Use sublist if possible to reduce memory footprint of transient array lists to reduce garbage collection overhead
     */
    protected List<IHistoricalEntry<V>> tryCachedPreviousResult_sameKey(
            final IHistoricalCacheQueryInternalMethods<V> query, final int shiftBackUnits) {
        if (cachedPreviousResult_shiftBackUnits == null || cachedPreviousResult_shiftBackUnits < shiftBackUnits) {
            return null;
        }
        final int toIndex = cachedPreviousResult_filteringDuplicates.size();
        final int fromIndex = Math.max(0, toIndex - shiftBackUnits);
        return cachedPreviousResult_filteringDuplicates.subList(fromIndex, toIndex);
    }

    /**
     * This needs to be called wherever replaceCachedEntries() was called before
     * 
     * @
     */
    protected void updateCachedPreviousResult(final IHistoricalCacheQueryInternalMethods<V> query,
            final int shiftBackUnits, final List<IHistoricalEntry<V>> result) {
        if (result.isEmpty() && query.getAssertValue() == HistoricalCacheAssertValue.ASSERT_VALUE_WITH_FUTURE_NULL) {
            //do not remember an empty result with future null (a call with future next might trip on it)
            return;
        }
        if (cachedPreviousResult_shiftBackUnits != null && !cachedPreviousEntries.isEmpty() && result.size() > 1) {
            throw new IllegalStateException("cachedPreviousResult should have been reset by preceeding code!");
        }
        cachedPreviousResult_filteringDuplicates = result;
        cachedPreviousResult_shiftBackUnits = shiftBackUnits;
    }

    protected void resetCachedPreviousResult() {
        cachedPreviousResult_filteringDuplicates = null;
        cachedPreviousResult_shiftBackUnits = null;
    }

    @Override
    protected IndexedFDate replaceCachedEntries(final FDate key, final List<IHistoricalEntry<V>> trailing) {
        final IndexedFDate indexedKey = super.replaceCachedEntries(key, trailing);
        if (indexedKey != null) {
            cachedPreviousEntriesKey = indexedKey;
            resetCachedPreviousResult();
            return indexedKey;
        } else {
            return null;
        }
    }

    //CHECKSTYLE:OFF
    protected void appendCachedEntryAndResult(final FDate key, final Integer shiftBackUnits,
            final IHistoricalEntry<V> latestEntry) {
        //CHECKSTYLE:ON
        if (latestEntry != null) {
            if (shiftBackUnits != null && cachedPreviousResult_shiftBackUnits == null) {
                throw new IllegalStateException(
                        "cachedPreviousResult_shiftBackUnits is null even though it should be extended");
            }

            final IndexedFDate indexedKey = IndexedFDate.maybeWrap(latestEntry.getKey());
            indexedKey.putQueryCoreIndex(this,
                    new QueryCoreIndex(modCount, cachedPreviousEntries.size() - modIncrementIndex));
            final IHistoricalEntry<V> indexedEntry = ImmutableHistoricalEntry.of(indexedKey, latestEntry.getValue());
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

    protected FDate determineConsistentLastCachedEntryKey() {
        final FDate lastCachedEntryKey = getLastCachedEntry().getKey();
        if (cachedPreviousResult_filteringDuplicates != null && !cachedPreviousResult_filteringDuplicates.isEmpty()) {
            final FDate lastCachedResultKey = cachedPreviousResult_filteringDuplicates
                    .get(cachedPreviousResult_filteringDuplicates.size() - 1)
                    .getKey();
            assertSameLastKey(lastCachedEntryKey, lastCachedResultKey);
        }
        return lastCachedEntryKey;
    }

    private void assertSameLastKey(final FDate lastCachedEntryKey, final FDate lastCachedResultKey) {
        if (!lastCachedEntryKey.equalsNotNullSafe(lastCachedResultKey)) {
            throw new IllegalStateException(
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
