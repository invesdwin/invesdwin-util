package de.invesdwin.util.collections.loadingcache.historical.query.internal.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.delegate.debug.DebugConcurrentModificationList;
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
    protected final List<IHistoricalEntry<V>> cachedPreviousEntries = new DebugConcurrentModificationList<IHistoricalEntry<V>>(
            new ArrayList<>()) {

        @Override
        public boolean add(final IHistoricalEntry<V> e) {
            System.out.println(
                    "cachedPreviousEntries " + System.identityHashCode(ACachedEntriesHistoricalCacheQueryCore.this)
                            + " " + Thread.currentThread().getName() + " add");
            return super.add(e);
        }

        @Override
        public boolean addAll(final Collection<? extends IHistoricalEntry<V>> c) {
            System.out.println(
                    "cachedPreviousEntries " + System.identityHashCode(ACachedEntriesHistoricalCacheQueryCore.this)
                            + " " + Thread.currentThread().getName() + " addAll");
            return super.addAll(c);
        }

        @Override
        public void clear() {
            System.out.println(
                    "cachedPreviousEntries " + System.identityHashCode(ACachedEntriesHistoricalCacheQueryCore.this)
                            + " " + Thread.currentThread().getName() + " clear");
            super.clear();
        }

        @Override
        public boolean remove(final Object o) {
            System.out.println(
                    "cachedPreviousEntries " + System.identityHashCode(ACachedEntriesHistoricalCacheQueryCore.this)
                            + " " + Thread.currentThread().getName() + " remove");
            return super.remove(o);
        }

        @Override
        public boolean removeAll(final Collection<?> c) {
            System.out.println(
                    "cachedPreviousEntries " + System.identityHashCode(ACachedEntriesHistoricalCacheQueryCore.this)
                            + " " + Thread.currentThread().getName() + " removeAll");
            return super.removeAll(c);
        }

        @Override
        public boolean retainAll(final Collection<?> c) {
            System.out.println(
                    "cachedPreviousEntries " + System.identityHashCode(ACachedEntriesHistoricalCacheQueryCore.this)
                            + " " + Thread.currentThread().getName() + " retainAll");
            return super.retainAll(c);
        }

        @Override
        public void add(final int index, final IHistoricalEntry<V> element) {
            System.out.println(
                    "cachedPreviousEntries " + System.identityHashCode(ACachedEntriesHistoricalCacheQueryCore.this)
                            + " " + Thread.currentThread().getName() + " add idx");
            super.add(index, element);
        }

        @Override
        public boolean addAll(final int index, final Collection<? extends IHistoricalEntry<V>> c) {
            System.out.println(
                    "cachedPreviousEntries " + System.identityHashCode(ACachedEntriesHistoricalCacheQueryCore.this)
                            + " " + Thread.currentThread().getName() + " addAll idx");
            return super.addAll(index, c);
        }

        @Override
        public IHistoricalEntry<V> remove(final int index) {
            System.out.println(
                    "cachedPreviousEntries " + System.identityHashCode(ACachedEntriesHistoricalCacheQueryCore.this)
                            + " " + Thread.currentThread().getName() + " remove idx");
            return super.remove(index);
        }

        @Override
        public void replaceAll(final UnaryOperator<IHistoricalEntry<V>> operator) {
            System.out.println(
                    "cachedPreviousEntries " + System.identityHashCode(ACachedEntriesHistoricalCacheQueryCore.this)
                            + " " + Thread.currentThread().getName() + " replaceAll");
            super.replaceAll(operator);
        }

        @Override
        public boolean removeIf(final Predicate<? super IHistoricalEntry<V>> filter) {
            System.out.println(
                    "cachedPreviousEntries " + System.identityHashCode(ACachedEntriesHistoricalCacheQueryCore.this)
                            + " " + Thread.currentThread().getName() + " removeIf");
            return super.removeIf(filter);
        }

        @Override
        public IHistoricalEntry<V> set(final int index, final IHistoricalEntry<V> element) {
            System.out.println(
                    "cachedPreviousEntries " + System.identityHashCode(ACachedEntriesHistoricalCacheQueryCore.this)
                            + " " + Thread.currentThread().getName() + " set");
            return super.set(index, element);
        }

        @Override
        public List<IHistoricalEntry<V>> subList(final int fromIndex, final int toIndex) {
            System.out.println(
                    "cachedPreviousEntries " + System.identityHashCode(ACachedEntriesHistoricalCacheQueryCore.this)
                            + " " + Thread.currentThread().getName() + " subList");
            return super.subList(fromIndex, toIndex);
        }

    };
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
            ACachedEntriesHistoricalCacheQueryCore<V> useIndex);

    protected IHistoricalEntry<V> getLastCachedEntry() {
        if (cachedPreviousEntries.isEmpty()) {
            throw new IllegalStateException("lastCachedEntry cannot be retrieved since cachedPreviousEntries is empty");
        }
        return cachedPreviousEntries.get(cachedPreviousEntries.size() - 1);
    }

    protected IHistoricalEntry<V> getFirstCachedEntry() {
        if (cachedPreviousEntries.isEmpty()) {
            throw new IllegalStateException("lastCachedEntry cannot be retrieved since cachedPreviousEntries is empty");
        }
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
            final IHistoricalEntry<V> latestEntry) {
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
