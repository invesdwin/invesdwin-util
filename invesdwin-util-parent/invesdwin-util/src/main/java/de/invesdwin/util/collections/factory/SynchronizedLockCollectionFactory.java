package de.invesdwin.util.collections.factory;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentHashMap.KeySetView;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

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
import de.invesdwin.util.concurrent.reference.lazy.ILazyReference;
import de.invesdwin.util.concurrent.reference.lazy.SynchronizedLazyReference;
import de.invesdwin.util.lang.comparator.IComparator;
import de.invesdwin.util.lang.reflection.Reflections;

@Immutable
public final class SynchronizedLockCollectionFactory implements ILockCollectionFactory {

    public static final SynchronizedLockCollectionFactory INSTANCE = new SynchronizedLockCollectionFactory();

    private static final MethodHandle KEYSETVIEW_CONSTRUCTOR;

    static {
        KEYSETVIEW_CONSTRUCTOR = newKeySetViewConstructor();
    }

    private SynchronizedLockCollectionFactory() {}

    private static MethodHandle newKeySetViewConstructor() {
        try {
            @SuppressWarnings({ "unchecked", "rawtypes" })
            final Constructor<KeySetView> keySetViewConstructor = (Constructor<KeySetView>) KeySetView.class
                    .getDeclaredConstructors()[0];
            Reflections.makeAccessible(keySetViewConstructor);
            return MethodHandles.lookup().unreflectConstructor(keySetViewConstructor);
        } catch (final Throwable e) {
            return null;
        }
    }

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
    public <T> IFastIterableSet<T> newFastIterableIdentitySet(final int initialSize) {
        return new SynchronizedFastIterableDelegateSet<T>(
                DisabledLockCollectionFactory.INSTANCE.newIdentitySet(initialSize));
    }

    @Override
    public <T> IFastIterableList<T> newFastIterableArrayList() {
        return new SynchronizedFastIterableDelegateList<T>(DisabledLockCollectionFactory.INSTANCE.newArrayList());
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
    public <K, V> IFastIterableMap<K, V> newFastIterableTreeMap() {
        return new SynchronizedFastIterableDelegateMap<K, V>(DisabledLockCollectionFactory.INSTANCE.newTreeMap()) {
            @Override
            protected void addToFastIterable(final K key, final V value) {
                refreshFastIterable();
            }
        };
    }

    @Override
    public <K, V> IFastIterableMap<K, V> newFastIterableTreeMap(final IComparator<? super K> comparator) {
        return new SynchronizedFastIterableDelegateMap<K, V>(
                DisabledLockCollectionFactory.INSTANCE.newTreeMap(comparator)) {
            @Override
            protected void addToFastIterable(final K key, final V value) {
                refreshFastIterable();
            }
        };
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
    public <K, V> IFastIterableMap<K, V> newFastIterableIdentityMap(final int initialSize) {
        return new SynchronizedFastIterableDelegateMap<K, V>(
                DisabledLockCollectionFactory.INSTANCE.newIdentityMap(initialSize));
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
        if (KEYSETVIEW_CONSTRUCTOR != null) {
            try {
                return (Set<T>) KEYSETVIEW_CONSTRUCTOR
                        .invoke(new ConcurrentHashMap<T, Boolean>(initialSize, loadFactor), Boolean.TRUE);
            } catch (final Throwable e) {
                throw new RuntimeException(e);
            }
        } else {
            return ConcurrentHashMap.newKeySet(initialSize);
        }
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
    public <T> ILazyReference<T> newLazyReference(final Supplier<T> factory) {
        return new SynchronizedLazyReference<>(factory);
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
