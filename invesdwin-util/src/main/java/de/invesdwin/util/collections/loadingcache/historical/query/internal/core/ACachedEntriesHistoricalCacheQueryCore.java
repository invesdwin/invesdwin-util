package de.invesdwin.util.collections.loadingcache.historical.query.internal.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.bean.tuple.ImmutableEntry;
import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.SingleValueIterable;
import de.invesdwin.util.collections.iterable.WrapperCloseableIterable;
import de.invesdwin.util.collections.loadingcache.historical.query.index.IndexedFDate;
import de.invesdwin.util.collections.loadingcache.historical.query.index.QueryCoreIndex;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.HistoricalCacheAssertValue;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.HistoricalCacheQuery;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.IHistoricalCacheInternalMethods;
import de.invesdwin.util.time.fdate.FDate;

@ThreadSafe
public abstract class ACachedEntriesHistoricalCacheQueryCore<V> implements IHistoricalCacheQueryCore<V> {

    protected int modCount = 0;
    protected int modIncrementIndex = 0;
    @GuardedBy("getParent().getLock()")
    protected final List<Entry<IndexedFDate, V>> cachedPreviousEntries = new ArrayList<>();

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

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected IndexedFDate replaceCachedEntries(final FDate key, final List<Entry<FDate, V>> trailing) {
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
            final Entry<FDate, V> entry = trailing.get(i);
            final IndexedFDate indexedKey = IndexedFDate.maybeWrap(entry.getKey());
            indexedKey.putQueryCoreIndex(this, new QueryCoreIndex(modCount, i - modIncrementIndex));
            final Entry<IndexedFDate, V> indexedEntry = ImmutableEntry.of(indexedKey, entry.getValue());
            cachedPreviousEntries.add(indexedEntry);
            trailing.set(i, (Entry) indexedEntry);
        }

        //attach indexed key to outer key at least
        final IndexedFDate indexedKey = IndexedFDate.maybeWrap(key);
        indexedKey.putQueryCoreIndex(this,
                new QueryCoreIndex(modCount, cachedPreviousEntries.size() - 1 - modIncrementIndex));
        return indexedKey;
    }

    protected abstract Integer maybeIncreaseMaximumSize(int requiredSize);

    protected abstract int bisect(FDate skippingKeysAbove, List<Entry<FDate, V>> list, Integer unitsBack,
            ACachedEntriesHistoricalCacheQueryCore<V> useIndex) throws ResetCacheException;

    protected Entry<IndexedFDate, V> getLastCachedEntry() throws ResetCacheException {
        if (cachedPreviousEntries.isEmpty()) {
            throw new ResetCacheException("lastCachedEntry cannot be retrieved since cachedPreviousEntries is empty");
        }
        return cachedPreviousEntries.get(cachedPreviousEntries.size() - 1);
    }

    protected Entry<IndexedFDate, V> getFirstCachedEntry() {
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

    protected void appendCachedEntry(final FDate key, final Integer shiftBackUnits, final Entry<FDate, V> latestEntry)
            throws ResetCacheException {
        if (latestEntry != null) {
            final IndexedFDate indexedKey = IndexedFDate.maybeWrap(latestEntry.getKey());
            indexedKey.putQueryCoreIndex(this,
                    new QueryCoreIndex(modCount, cachedPreviousEntries.size() - modIncrementIndex));
            final Entry<IndexedFDate, V> indexedEntry = ImmutableEntry.of(indexedKey, latestEntry.getValue());
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
