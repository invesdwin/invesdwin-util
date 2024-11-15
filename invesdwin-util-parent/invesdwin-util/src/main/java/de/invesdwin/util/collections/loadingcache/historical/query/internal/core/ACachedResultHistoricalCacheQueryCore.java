package de.invesdwin.util.collections.loadingcache.historical.query.internal.core;

import java.util.AbstractList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;
import java.util.Spliterator;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.lang3.mutable.MutableInt;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.iterable.collection.ArrayCloseableIterator;
import de.invesdwin.util.collections.iterable.collection.arraylist.ArrayListCloseableIterable;
import de.invesdwin.util.collections.list.Lists;
import de.invesdwin.util.collections.loadingcache.historical.IHistoricalEntry;
import de.invesdwin.util.collections.loadingcache.historical.ImmutableHistoricalEntry;
import de.invesdwin.util.collections.loadingcache.historical.query.error.ResetCacheException;
import de.invesdwin.util.collections.loadingcache.historical.query.index.IndexedFDate;
import de.invesdwin.util.collections.loadingcache.historical.query.index.QueryCoreIndex;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.FilterDuplicateKeysList;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.HistoricalCacheAssertValue;
import de.invesdwin.util.lang.reflection.Reflections;
import de.invesdwin.util.time.date.FDate;

@ThreadSafe
public abstract class ACachedResultHistoricalCacheQueryCore<V> extends ACachedEntriesHistoricalCacheQueryCore<V> {

    @GuardedBy("cachedQueryActiveLock")
    protected MutableInt cachedPreviousResult_modIncrementIndex = null;
    @GuardedBy("cachedQueryActiveLock")
    protected IndexedFDate cachedPreviousEntriesKey = null;
    @GuardedBy("cachedQueryActiveLock")
    protected List<IHistoricalEntry<V>> cachedPreviousResult_filteringDuplicates = null;
    @GuardedBy("cachedQueryActiveLock")
    protected Integer cachedPreviousResult_shiftBackUnits = null;

    /**
     * Use sublist if possible to reduce memory footprint of transient array lists to reduce garbage collection overhead
     *
     * @throws ResetCacheException
     */
    protected List<IHistoricalEntry<V>> tryCachedPreviousResult_sameKey(
            final IHistoricalCacheQueryInternalMethods<V> query, final int shiftBackUnits) throws ResetCacheException {
        if (cachedPreviousResult_shiftBackUnits == null || cachedPreviousResult_shiftBackUnits < shiftBackUnits) {
            return null;
        }
        determineConsistentLastCachedEntryKey(); //ensure the key is consistent
        final int toIndex = cachedPreviousResult_filteringDuplicates.size();
        final int fromIndex = Math.max(0, toIndex - shiftBackUnits);
        return new CachedPreviousResultSubList<V>(cachedPreviousResult_filteringDuplicates,
                cachedPreviousResult_modIncrementIndex, 0, fromIndex, toIndex);
    }

    /**
     * This needs to be called wherever replaceCachedEntries() was called before
     *
     * @param key
     */
    protected void updateCachedPreviousResult(final IHistoricalCacheQueryInternalMethods<V> query, final FDate key,
            final int shiftBackUnits, final List<IHistoricalEntry<V>> result) throws ResetCacheException {
        if (result.isEmpty() && query.getAssertValue() == HistoricalCacheAssertValue.ASSERT_VALUE_WITH_FUTURE_NULL) {
            //do not remember an empty result with future null (a call with future next might trip on it)
            return;
        }
        if (cachedPreviousResult_shiftBackUnits != null && !cachedPreviousEntries.isEmpty() && result.size() > 1) {
            //somehow this does not happen in debugger, maybe some JVM optimization
            throw new ResetCacheException("cachedPreviousResult should have been reset by preceeding code!");
        }
        final IndexedFDate replaced = super.replaceCachedEntries(key, result);
        if (replaced != null) {
            cachedPreviousResult_filteringDuplicates = result;
            cachedPreviousResult_shiftBackUnits = shiftBackUnits;
            cachedPreviousResult_modIncrementIndex = new MutableInt(0);
        }
    }

    protected void resetCachedPreviousResult() {
        cachedPreviousResult_filteringDuplicates = null;
        cachedPreviousResult_shiftBackUnits = null;
        cachedPreviousResult_modIncrementIndex = null;
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

    protected void appendCachedEntryAndResult(final FDate key, final Integer shiftBackUnits,
            final IHistoricalEntry<V> latestEntry) {
        if (latestEntry != null) {
            if (shiftBackUnits != null && cachedPreviousResult_shiftBackUnits == null) {
                throw new IllegalStateException(
                        "cachedPreviousResult_shiftBackUnits is null even though it should be extended");
            }

            final IndexedFDate indexedKey = IndexedFDate.maybeWrap(latestEntry.getKey());
            indexedKey.putQueryCoreIndex(this, new QueryCoreIndex(cachedPreviousEntries_modCount,
                    cachedPreviousEntries.size() - cachedPreviousEntries_modIncrementIndex.intValue()));
            final IHistoricalEntry<V> indexedEntry = ImmutableHistoricalEntry.of(indexedKey, latestEntry.getValue());
            cachedPreviousEntries.add(indexedEntry);

            if (shiftBackUnits != null && cachedPreviousResult_shiftBackUnits < shiftBackUnits) {
                //we added one more element and there is still demand for more
                cachedPreviousResult_shiftBackUnits++;
            }

            if (cachedPreviousResult_filteringDuplicates != null) {
                //ensure we stay in size limit
                cachedPreviousResult_filteringDuplicates.add(latestEntry);
                final int removed = Lists.maybeTrimSizeStart(cachedPreviousResult_filteringDuplicates,
                        cachedPreviousResult_shiftBackUnits);
                cachedPreviousResult_modIncrementIndex.subtract(removed);
            }

            final Integer maximumSize = getParent().getMaximumSize();
            if (maximumSize != null) {
                //ensure we stay in size limit
                final int removed = Lists.maybeTrimSizeStart(cachedPreviousEntries, maximumSize);
                cachedPreviousEntries_modIncrementIndex.subtract(removed);
            }

        }
        final IndexedFDate indexedKey = IndexedFDate.maybeWrap(key);
        indexedKey.putQueryCoreIndex(this, new QueryCoreIndex(cachedPreviousEntries_modCount,
                cachedPreviousEntries.size() - 1 - cachedPreviousEntries_modIncrementIndex.intValue()));
        cachedPreviousEntriesKey = indexedKey;
    }

    protected FDate determineConsistentLastCachedEntryKey() throws ResetCacheException {
        final FDate lastCachedEntryKey = getLastCachedEntry().getKey();
        if (cachedPreviousResult_filteringDuplicates != null && !cachedPreviousResult_filteringDuplicates.isEmpty()) {
            final FDate lastCachedResultKey = cachedPreviousResult_filteringDuplicates
                    .get(cachedPreviousResult_filteringDuplicates.size() - 1)
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

    @Override
    protected void resetForRetry() {
        super.resetForRetry();
        cachedPreviousEntriesKey = null;
        resetCachedPreviousResult();
    }

    private static class CachedPreviousResultSubList<_V> extends AbstractList<IHistoricalEntry<_V>>
            implements RandomAccess, ICloseableIterable<IHistoricalEntry<_V>> {
        private final List<IHistoricalEntry<_V>> list;
        private final MutableInt modIncrementIndex;
        private final int offset;
        private final int size;

        CachedPreviousResultSubList(final List<IHistoricalEntry<_V>> list, final MutableInt modIncrementIndex,
                final int offset, final int fromIndex, final int toIndex) {
            if (list instanceof FilterDuplicateKeysList) {
                final FilterDuplicateKeysList<_V> cList = (FilterDuplicateKeysList<_V>) list;
                this.list = cList.getDelegate();
            } else {
                this.list = list;
            }
            this.modIncrementIndex = modIncrementIndex;
            final int modIncrementIndexSnapshot = modIncrementIndex.intValue();
            this.offset = offset + fromIndex - modIncrementIndexSnapshot;
            this.size = toIndex - fromIndex;
        }

        @Override
        public IHistoricalEntry<_V> set(final int index, final IHistoricalEntry<_V> e) {
            return list.set(offset + modIncrementIndex.intValue() + index, e);
        }

        @Override
        public IHistoricalEntry<_V> get(final int index) {
            return list.get(offset + modIncrementIndex.intValue() + index);
        }

        @Override
        public int size() {
            return this.size;
        }

        @Override
        public void add(final int index, final IHistoricalEntry<_V> e) {
            throw new UnsupportedOperationException("read only");
        }

        @Override
        public IHistoricalEntry<_V> remove(final int index) {
            throw new UnsupportedOperationException("read only");
        }

        @Override
        protected void removeRange(final int fromIndex, final int toIndex) {
            throw new UnsupportedOperationException("read only");
        }

        @Override
        public boolean addAll(final Collection<? extends IHistoricalEntry<_V>> c) {
            throw new UnsupportedOperationException("read only");
        }

        @Override
        public boolean addAll(final int index, final Collection<? extends IHistoricalEntry<_V>> c) {
            throw new UnsupportedOperationException("read only");
        }

        @SuppressWarnings({ "rawtypes", "unchecked" })
        @Override
        public ICloseableIterator<IHistoricalEntry<_V>> iterator() {
            try {
                final Object[] array = (Object[]) Reflections.getUnsafe()
                        .getObject(list, ArrayListCloseableIterable.ARRAYLIST_ELEMENTDATA_FIELD_OFFSET);
                return new ArrayCloseableIterator(array, offset + modIncrementIndex.intValue(), size);
            } catch (final Throwable e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public ListIterator<IHistoricalEntry<_V>> listIterator(final int index) {
            throw new UnsupportedOperationException("not implemented");
        }

        @Override
        public List<IHistoricalEntry<_V>> subList(final int fromIndex, final int toIndex) {
            return new CachedPreviousResultSubList<_V>(list, modIncrementIndex, offset + modIncrementIndex.intValue(),
                    fromIndex, toIndex);
        }

        @Override
        public Spliterator<IHistoricalEntry<_V>> spliterator() {
            throw new UnsupportedOperationException("not implemented");
        }
    }

}
