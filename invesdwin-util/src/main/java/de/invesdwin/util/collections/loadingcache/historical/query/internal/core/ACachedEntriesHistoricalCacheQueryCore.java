package de.invesdwin.util.collections.loadingcache.historical.query.internal.core;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.loadingcache.historical.IHistoricalEntry;
import de.invesdwin.util.collections.loadingcache.historical.ImmutableHistoricalEntry;
import de.invesdwin.util.collections.loadingcache.historical.query.index.IndexedFDate;
import de.invesdwin.util.collections.loadingcache.historical.query.index.QueryCoreIndex;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.HistoricalCacheAssertValue;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.HistoricalCacheQuery;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.IHistoricalCacheInternalMethods;
import de.invesdwin.util.time.fdate.FDate;

@ThreadSafe
public abstract class ACachedEntriesHistoricalCacheQueryCore<V> implements IHistoricalCacheQueryCore<V> {

    @GuardedBy("cachedQueryActiveLock")
    protected int modCount = 0;
    @GuardedBy("cachedQueryActiveLock")
    protected int modIncrementIndex = 0;
    @GuardedBy("cachedQueryActiveLock")
    protected final List<IHistoricalEntry<V>> cachedPreviousEntries = new ArrayList<>();
    private final int hashCode = super.hashCode();

    @Override
    public boolean equals(final Object obj) {
        return obj == this;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public IHistoricalCacheInternalMethods<V> getParent() {
        return getDelegate().getParent();
    }

    protected abstract IHistoricalCacheQueryCore<V> getDelegate();

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected IndexedFDate replaceCachedEntries(final FDate key, final List<IHistoricalEntry<V>> trailing) {
        if (trailing.isEmpty() ||
        /*
         * (maybe we went before the first entry) or (maybe we went after the last entry to only fetch one element), so
         * we don't want to throw away a cache that might already be filled
         */
                (trailing.size() == 1 && cachedPreviousEntries.size() > 1)) {
            return null;
        }
        maybeIncreaseMaximumSize(trailing.size());
        modCount++;
        modIncrementIndex = 0;
        cachedPreviousEntries.clear();
        for (int i = 0; i < trailing.size(); i++) {
            final IHistoricalEntry<V> entry = trailing.get(i);
            final IndexedFDate indexedKey = IndexedFDate.maybeWrap(entry.getKey());
            indexedKey.putQueryCoreIndex(this, new QueryCoreIndex(modCount, i - modIncrementIndex));
            final IHistoricalEntry<V> indexedEntry = ImmutableHistoricalEntry.of(indexedKey, entry.getValue());
            cachedPreviousEntries.add(indexedEntry);
            trailing.set(i, indexedEntry);
        }

        //attach indexed key to outer key at least
        final IndexedFDate indexedKey = IndexedFDate.maybeWrap(key);
        indexedKey.putQueryCoreIndex(this,
                new QueryCoreIndex(modCount, cachedPreviousEntries.size() - 1 - modIncrementIndex));
        return indexedKey;
    }

    protected abstract Integer maybeIncreaseMaximumSize(int requiredSize);

    protected abstract int bisect(FDate skippingKeysAbove, List<IHistoricalEntry<V>> list, Integer unitsBack,
            ACachedEntriesHistoricalCacheQueryCore<V> useIndex) throws ResetCacheException;

    protected IHistoricalEntry<V> getLastCachedEntry() throws ResetCacheException {
        if (cachedPreviousEntries.isEmpty()) {
            throw new ResetCacheException("lastCachedEntry cannot be retrieved since cachedPreviousEntries is empty");
        }
        return cachedPreviousEntries.get(cachedPreviousEntries.size() - 1);
    }

    protected IHistoricalEntry<V> getFirstCachedEntry() {
        return cachedPreviousEntries.get(0);
    }

    protected void resetForRetry() {
        getDelegate().clear();
        cachedPreviousEntries.clear();
        modCount++;
        modIncrementIndex = 0;
    }

    @Override
    public final V getValue(final IHistoricalCacheQueryInternalMethods<V> query, final FDate key,
            final HistoricalCacheAssertValue assertValue) {
        return getDelegate().getValue(query, key, assertValue);
    }

    @Override
    public final IHistoricalEntry<V> getEntry(final IHistoricalCacheQueryInternalMethods<V> query, final FDate key,
            final HistoricalCacheAssertValue assertValue) {
        return getDelegate().getEntry(query, key, assertValue);
    }

    @Override
    public final IHistoricalEntry<V> computeEntry(final HistoricalCacheQuery<V> historicalCacheQuery, final FDate key,
            final HistoricalCacheAssertValue assertValue) {
        return getDelegate().computeEntry(historicalCacheQuery, key, assertValue);
    }

    @Override
    public final ICloseableIterable<IHistoricalEntry<V>> getNextEntries(
            final IHistoricalCacheQueryInternalMethods<V> query, final FDate key, final int shiftForwardUnits) {
        return getDelegate().getNextEntries(query, key, shiftForwardUnits);
    }

    @Override
    public final IHistoricalEntry<V> getNextEntry(final IHistoricalCacheQueryInternalMethods<V> query, final FDate key,
            final int shiftForwardUnits) {
        return getDelegate().getNextEntry(query, key, shiftForwardUnits);
    }

    protected void appendCachedEntry(final FDate key, final Integer shiftBackUnits,
            final IHistoricalEntry<V> latestEntry) throws ResetCacheException {
        if (latestEntry != null) {
            final IndexedFDate indexedKey = IndexedFDate.maybeWrap(latestEntry.getKey());
            indexedKey.putQueryCoreIndex(this,
                    new QueryCoreIndex(modCount, cachedPreviousEntries.size() - modIncrementIndex));
            final IHistoricalEntry<V> indexedEntry = ImmutableHistoricalEntry.of(indexedKey, latestEntry.getValue());
            cachedPreviousEntries.add(indexedEntry);

            final Integer maximumSize = getParent().getMaximumSize();
            if (maximumSize != null) {
                //ensure we stay in size limit
                while (cachedPreviousEntries.size() > maximumSize) {
                    cachedPreviousEntries.remove(0);
                    modIncrementIndex--;
                }
            }
        }
        //attach indexed key to outer key at least
        final IndexedFDate indexedKey = IndexedFDate.maybeWrap(key);
        indexedKey.putQueryCoreIndex(this,
                new QueryCoreIndex(modCount, cachedPreviousEntries.size() - 1 - modIncrementIndex));
    }

}
