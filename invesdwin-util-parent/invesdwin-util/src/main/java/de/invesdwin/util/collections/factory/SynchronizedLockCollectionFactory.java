package de.invesdwin.util.collections.factory;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentHashMap.KeySetView;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

import javax.annotation.concurrent.Immutable;

import org.jctools.maps.NonBlockingHashMap;

import de.invesdwin.util.collections.Collections;
import de.invesdwin.util.collections.bitset.IBitSet;
import de.invesdwin.util.collections.bitset.SynchronizedBitSet;
import de.invesdwin.util.collections.fast.IFastIterableList;
import de.invesdwin.util.collections.fast.IFastIterableMap;
import de.invesdwin.util.collections.fast.IFastIterableSet;
import de.invesdwin.util.collections.fast.concurrent.SynchronizedCollection;
import de.invesdwin.util.collections.fast.concurrent.SynchronizedFastIterableDelegateList;
import de.invesdwin.util.collections.fast.concurrent.SynchronizedFastIterableDelegateMap;
import de.invesdwin.util.collections.fast.concurrent.SynchronizedFastIterableDelegateSet;
import de.invesdwin.util.collections.fast.concurrent.SynchronizedList;
import de.invesdwin.util.collections.fast.concurrent.SynchronizedMap;
import de.invesdwin.util.collections.fast.concurrent.SynchronizedSet;
import de.invesdwin.util.collections.fast.concurrent.locked.LockedCollection;
import de.invesdwin.util.collections.fast.concurrent.locked.LockedList;
import de.invesdwin.util.collections.fast.concurrent.locked.LockedMap;
import de.invesdwin.util.collections.fast.concurrent.locked.LockedSet;
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
        return new RefreshingSynchronizedFastIterableDelegateMap<K, V>(
                DisabledLockCollectionFactory.INSTANCE.newTreeMap());
    }

    @Override
    public <K, V> IFastIterableMap<K, V> newFastIterableTreeMap(final IComparator<? super K> comparator) {
        return new RefreshingSynchronizedFastIterableDelegateMap<K, V>(
                DisabledLockCollectionFactory.INSTANCE.newTreeMap(comparator));
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
    public <K, V> ConcurrentMap<K, V> newConcurrentMap(final int initialSize, final float loadFactor,
            final int concurrencyLevel) {
        //CHECKSTYLE:OFF
        if (concurrencyLevel >= 32) {
            //optimized for parallel writes without blocking
            return new NonBlockingHashMap<K, V>(initialSize);
        } else {
            //generally best for low cpu counts
            return new ConcurrentHashMap<K, V>(initialSize, loadFactor, concurrencyLevel);
        }
        //CHECKSTYLE:ON
        //optimized for heap size and parallel reads
        //        return new ConcurrentObject2ObjectMap<>(new PrimitiveConcurrentMapConfig().setInitialCapacity(initialSize)
        //                .setLoadFactor(loadFactor)
        //                .setConcurrencyLevel(concurrencyLevel));
    }

    @Override
    public <T> Set<T> newConcurrentSet(final int initialSize, final float loadFactor, final int concurrencyLevel) {
        if (KEYSETVIEW_CONSTRUCTOR != null) {
            try {
                return (Set<T>) KEYSETVIEW_CONSTRUCTOR
                        .invoke(newConcurrentMap(initialSize, loadFactor, concurrencyLevel), Boolean.TRUE);
            } catch (final Throwable e) {
                throw new RuntimeException(e);
            }
        } else {
            return ConcurrentHashMap.newKeySet(initialSize);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> newArrayList(final T... copyOf) {
        return Collections.synchronizedList(DisabledLockCollectionFactory.INSTANCE.newArrayList(copyOf));
    }

    @Override
    public <T> List<T> newArrayList(final Collection<? extends T> copyOf) {
        return Collections.synchronizedList(DisabledLockCollectionFactory.INSTANCE.newArrayList(copyOf));
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
    public <T> NavigableSet<T> newTreeSet() {
        return Collections.synchronizedNavigableSet(DisabledLockCollectionFactory.INSTANCE.newTreeSet());
    }

    @Override
    public <T> NavigableSet<T> newTreeSet(final IComparator<? super T> comparator) {
        return Collections.synchronizedNavigableSet(DisabledLockCollectionFactory.INSTANCE.newTreeSet(comparator));
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

    private static final class RefreshingSynchronizedFastIterableDelegateMap<K, V>
            extends SynchronizedFastIterableDelegateMap<K, V> {
        private RefreshingSynchronizedFastIterableDelegateMap(final Map<K, V> delegate) {
            super(delegate);
        }

        @Override
        protected void addToFastIterable(final K key, final V value) {
            refreshFastIterable();
        }
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

    @Override
    public <K, V> Map<K, V> synchronizedMap(final Map<K, V> map) {
        return new SynchronizedMap<>(map);
    }

    @Override
    public <T> Set<T> synchronizedSet(final Set<T> set) {
        return new SynchronizedSet<>(set);
    }

    @Override
    public <T> List<T> synchronizedList(final List<T> list) {
        return new SynchronizedList<>(list);
    }

    @Override
    public <T> Collection<T> synchronizedCollection(final Collection<T> collection) {
        return new SynchronizedCollection<>(collection);
    }

    @Override
    public <K, V> Map<K, V> synchronizedMap(final Map<K, V> map, final Object lock) {
        return new SynchronizedMap<>(map, lock);
    }

    @Override
    public <T> Set<T> synchronizedSet(final Set<T> set, final Object lock) {
        return new SynchronizedSet<>(set, lock);
    }

    @Override
    public <T> List<T> synchronizedList(final List<T> list, final Object lock) {
        return new SynchronizedList<>(list, lock);
    }

    @Override
    public <T> Collection<T> synchronizedCollection(final Collection<T> collection, final Object lock) {
        return new SynchronizedCollection<>(collection, lock);
    }

    @Override
    public <K, V> Map<K, V> lockedMap(final Map<K, V> map) {
        return new LockedMap<>(map);
    }

    @Override
    public <T> Set<T> lockedSet(final Set<T> set) {
        return new LockedSet<>(set);
    }

    @Override
    public <T> List<T> lockedList(final List<T> list) {
        return new LockedList<>(list);
    }

    @Override
    public <T> Collection<T> lockedCollection(final Collection<T> collection) {
        return new LockedCollection<>(collection);
    }

    @Override
    public <K, V> Map<K, V> lockedMap(final Map<K, V> map, final ILock lock) {
        return new LockedMap<>(map, lock);
    }

    @Override
    public <T> Set<T> lockedSet(final Set<T> set, final ILock lock) {
        return new LockedSet<>(set, lock);
    }

    @Override
    public <T> List<T> lockedList(final List<T> list, final ILock lock) {
        return new LockedList<>(list, lock);
    }

    @Override
    public <T> Collection<T> lockedCollection(final Collection<T> collection, final ILock lock) {
        return new LockedCollection<>(collection, lock);
    }

    @Override
    public <T> Set<T> newImmutableSet(final Collection<? extends T> copyOf) {
        return DisabledLockCollectionFactory.INSTANCE.newImmutableSet(copyOf);
    }

    @Override
    public <T> Set<T> newImmutableSet(final Iterable<? extends T> copyOf) {
        return DisabledLockCollectionFactory.INSTANCE.newImmutableSet(copyOf);
    }

    @Override
    public <T> Set<T> newImmutableSet(final Iterator<? extends T> copyOf) {
        return DisabledLockCollectionFactory.INSTANCE.newImmutableSet(copyOf);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Set<T> newImmutableSet(final T... copyOf) {
        return DisabledLockCollectionFactory.INSTANCE.newImmutableSet(copyOf);
    }

    @Override
    public <T> List<T> newImmutableList(final Collection<? extends T> copyOf) {
        return DisabledLockCollectionFactory.INSTANCE.newImmutableList(copyOf);
    }

    @Override
    public <T> List<T> newImmutableList(final Iterable<? extends T> copyOf) {
        return DisabledLockCollectionFactory.INSTANCE.newImmutableList(copyOf);
    }

    @Override
    public <T> List<T> newImmutableList(final Iterator<? extends T> copyOf) {
        return DisabledLockCollectionFactory.INSTANCE.newImmutableList(copyOf);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> newImmutableList(final T... copyOf) {
        return DisabledLockCollectionFactory.INSTANCE.newImmutableList(copyOf);
    }

    @Override
    public <T> Set<T> newImmutableLinkedSet(final Collection<? extends T> copyOf) {
        return DisabledLockCollectionFactory.INSTANCE.newImmutableLinkedSet(copyOf);
    }

    @Override
    public <T> Set<T> newImmutableLinkedSet(final Iterable<? extends T> copyOf) {
        return DisabledLockCollectionFactory.INSTANCE.newImmutableLinkedSet(copyOf);
    }

    @Override
    public <T> Set<T> newImmutableLinkedSet(final Iterator<? extends T> copyOf) {
        return DisabledLockCollectionFactory.INSTANCE.newImmutableLinkedSet(copyOf);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Set<T> newImmutableLinkedSet(final T... copyOf) {
        return DisabledLockCollectionFactory.INSTANCE.newImmutableLinkedSet(copyOf);
    }

    @Override
    public <K, V> Map<K, V> newImmutableLinkedMap(final Map<? extends K, ? extends V> copyOf) {
        return DisabledLockCollectionFactory.INSTANCE.newImmutableLinkedMap(copyOf);
    }

}
