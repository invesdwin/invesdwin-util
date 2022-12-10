package de.invesdwin.util.collections.factory;

import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentMapKeySetView;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.Collections;
import de.invesdwin.util.collections.bitset.IBitSet;
import de.invesdwin.util.collections.bitset.SynchronizedBitSet;
import de.invesdwin.util.collections.fast.IFastIterableList;
import de.invesdwin.util.collections.fast.IFastIterableMap;
import de.invesdwin.util.collections.fast.IFastIterableSet;
import de.invesdwin.util.collections.fast.concurrent.SynchronizedFastIterableDelegateList;
import de.invesdwin.util.collections.fast.concurrent.SynchronizedFastIterableDelegateMap;
import de.invesdwin.util.collections.fast.concurrent.SynchronizedFastIterableDelegateSet;
import de.invesdwin.util.collections.loadingcache.ALoadingCache;
import de.invesdwin.util.collections.loadingcache.ALoadingCacheConfig;
import de.invesdwin.util.concurrent.Executors;
import de.invesdwin.util.concurrent.WrappedExecutorService;
import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.Locks;
import de.invesdwin.util.concurrent.lock.readwrite.IReadWriteLock;
import de.invesdwin.util.concurrent.nested.ANestedExecutor;
import de.invesdwin.util.concurrent.nested.INestedExecutor;
import de.invesdwin.util.lang.comparator.IComparator;

@Immutable
public final class SynchronizedLockCollectionFactory implements ILockCollectionFactory {

    public static final SynchronizedLockCollectionFactory INSTANCE = new SynchronizedLockCollectionFactory();

    private SynchronizedLockCollectionFactory() {}

    @Override
    public ILock newLock(final String name) {
        return Locks.newReentrantLock(name);
    }

    @Override
    public IReadWriteLock newReadWriteLock(final String name) {
        return Locks.newReentrantReadWriteLock(name);
    }

    @Override
    public IBitSet newBitSet(final int initialSize) {
        return new SynchronizedBitSet(DisabledLockCollectionFactory.INSTANCE.newBitSet(initialSize));
    }

    @Override
    public <T> IFastIterableSet<T> newFastIterableLinkedSet(final int initialSize, final float loadFactor) {
        return new SynchronizedFastIterableDelegateSet<T>(
                DisabledLockCollectionFactory.INSTANCE.newLinkedSet(initialSize, loadFactor));
    }

    @Override
    public <T> IFastIterableList<T> newFastIterableArrayList(final int initialSize) {
        return new SynchronizedFastIterableDelegateList<T>(
                DisabledLockCollectionFactory.INSTANCE.newArrayList(initialSize));
    }

    @Override
    public <K, V> Map<K, V> newMap(final int initialSize, final float loadFactor) {
        return Collections.synchronizedMap(DisabledLockCollectionFactory.INSTANCE.newMap(initialSize, loadFactor));
    }

    @Override
    public <K, V> NavigableMap<K, V> newTreeMap() {
        return Collections.synchronizedNavigableMap(DisabledLockCollectionFactory.INSTANCE.newTreeMap());
    }

    @Override
    public <K, V> NavigableMap<K, V> newTreeMap(final IComparator<? super K> comparator) {
        return Collections.synchronizedNavigableMap(DisabledLockCollectionFactory.INSTANCE.newTreeMap(comparator));
    }

    @Override
    public INestedExecutor newNestedExecutor(final String name) {
        return new SynchronizedNestedExecutor(name);
    }

    @Override
    public ILockCollectionFactory disabled() {
        return DisabledLockCollectionFactory.INSTANCE;
    }

    @Override
    public <K, V> ALoadingCache<K, V> newLoadingCache(final ALoadingCacheConfig<K, V> config) {
        return config.newInstance();
    }

    @Override
    public <K, V> IFastIterableMap<K, V> newFastIterableLinkedMap(final int initialSize, final float loadFactor) {
        return new SynchronizedFastIterableDelegateMap<K, V>(
                DisabledLockCollectionFactory.INSTANCE.newLinkedMap(initialSize, loadFactor));
    }

    @Override
    public <K, V> ConcurrentMap<K, V> newConcurrentMap() {
        return newConcurrentMap(DEFAULT_INITIAL_SIZE);
    }

    @Override
    public <K, V> ConcurrentMap<K, V> newConcurrentMap(final int initialSize) {
        return newConcurrentMap(initialSize, DEFAULT_LOAD_FACTOR);
    }

    @Override
    public <K, V> ConcurrentMap<K, V> newConcurrentMap(final int initialSize, final float loadFactor) {
        return new ConcurrentHashMap<>(initialSize, loadFactor);
    }

    @Override
    public <T> Set<T> newConcurrentSet(final int initialSize, final float loadFactor) {
        return new ConcurrentMapKeySetView<T, Boolean>(new ConcurrentHashMap<T, Boolean>(initialSize, loadFactor),
                Boolean.TRUE);
    }

    @Override
    public <T> List<T> newArrayList(final int initialSize) {
        return Collections.synchronizedList(DisabledLockCollectionFactory.INSTANCE.newArrayList(initialSize));
    }

    @Override
    public <T> IFastIterableSet<T> newFastIterableSet(final int initialSize, final float loadFactor) {
        return new SynchronizedFastIterableDelegateSet<T>(
                DisabledLockCollectionFactory.INSTANCE.newSet(initialSize, loadFactor));
    }

    @Override
    public <K, V> IFastIterableMap<K, V> newFastIterableMap(final int initialSize, final float loadFactor) {
        return new SynchronizedFastIterableDelegateMap<K, V>(
                DisabledLockCollectionFactory.INSTANCE.newMap(initialSize, loadFactor));
    }

    @Override
    public <K, V> Map<K, V> newLinkedMap(final int initialSize, final float loadFactor) {
        return Collections
                .synchronizedMap(DisabledLockCollectionFactory.INSTANCE.newLinkedMap(initialSize, loadFactor));
    }

    @Override
    public <T> Set<T> newSet(final int initialSize, final float loadFactor) {
        return Collections.synchronizedSet(DisabledLockCollectionFactory.INSTANCE.newSet(initialSize, loadFactor));
    }

    @Override
    public <T> Set<T> newLinkedSet(final int initialSize, final float loadFactor) {
        return Collections
                .synchronizedSet(DisabledLockCollectionFactory.INSTANCE.newLinkedSet(initialSize, loadFactor));
    }

    @Override
    public <T> Set<T> newIdentitySet(final int initialSize) {
        return Collections.synchronizedSet(DisabledLockCollectionFactory.INSTANCE.newIdentitySet(initialSize));
    }

    @Override
    public <K, V> Map<K, V> newIdentityMap(final int initialSize) {
        return Collections.synchronizedMap(DisabledLockCollectionFactory.INSTANCE.newIdentityMap(initialSize));
    }

    @Override
    public boolean isThreadSafe() {
        return true;
    }

    private static final class SynchronizedNestedExecutor extends ANestedExecutor {
        private SynchronizedNestedExecutor(final String name) {
            super(name);
        }

        @Override
        protected WrappedExecutorService newNestedExecutor(final String nestedName) {
            return Executors.newFixedThreadPool(nestedName, Executors.getCpuThreadPoolCount());
        }
    }

}
