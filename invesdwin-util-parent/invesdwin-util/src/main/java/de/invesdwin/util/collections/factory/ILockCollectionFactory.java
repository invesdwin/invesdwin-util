package de.invesdwin.util.collections.factory;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.function.Supplier;

import de.invesdwin.util.collections.bitset.IBitSet;
import de.invesdwin.util.collections.fast.IFastIterableList;
import de.invesdwin.util.collections.fast.IFastIterableMap;
import de.invesdwin.util.collections.fast.IFastIterableSet;
import de.invesdwin.util.collections.loadingcache.ALoadingCache;
import de.invesdwin.util.collections.loadingcache.ALoadingCacheConfig;
import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.readwrite.IReadWriteLock;
import de.invesdwin.util.concurrent.nested.INestedExecutor;
import de.invesdwin.util.concurrent.reference.lazy.ILazyReference;
import de.invesdwin.util.lang.comparator.IComparator;
import it.unimi.dsi.fastutil.Hash;

public interface ILockCollectionFactory {

    /**
     * See java.util.concurrent.ConcurrentHashMap.DEFAULT_CONCURRENCY_LEVEL
     */
    int DEFAULT_CONCURRENCY_LEVEL = 16;
    int DEFAULT_INITIAL_SIZE = Hash.DEFAULT_INITIAL_SIZE;
    int DEFAULT_INITIAL_SIZE_IDENTITY = 32;
    /*
     * higher load factor means better space efficiency while having a worse lookup performance (due to collisions)
     */
    float DEFAULT_LOAD_FACTOR = Hash.DEFAULT_LOAD_FACTOR;

    ILock newLock(String name);

    IReadWriteLock newReadWriteLock(String name);

    IBitSet newBitSet(int initialSize);

    <T> List<T> newArrayList(Collection<? extends T> copyOf);

    default <T> List<T> newArrayList() {
        return newArrayList(DEFAULT_INITIAL_SIZE);
    }

    <T> List<T> newArrayList(int initialSize);

    default <T> IFastIterableList<T> newFastIterableArrayList() {
        return newFastIterableArrayList(DEFAULT_INITIAL_SIZE);
    }

    <T> IFastIterableList<T> newFastIterableArrayList(int initialSize);

    default <T> Set<T> newSet(final Collection<? extends T> copyOf) {
        final Set<T> set = newSet(copyOf.size());
        set.addAll(copyOf);
        return set;
    }

    default <T> Set<T> newSet() {
        return newSet(DEFAULT_INITIAL_SIZE);
    }

    default <T> Set<T> newSet(final int initialSize) {
        return newSet(initialSize, DEFAULT_LOAD_FACTOR);
    }

    <T> Set<T> newSet(int initialSize, float loadFactor);

    <T> NavigableSet<T> newTreeSet();

    <T> NavigableSet<T> newTreeSet(IComparator<? super T> comparator);

    default <T> IFastIterableSet<T> newFastIterableSet() {
        return newFastIterableSet(DEFAULT_INITIAL_SIZE);
    }

    default <T> IFastIterableSet<T> newFastIterableSet(final int initialSize) {
        return newFastIterableSet(initialSize, DEFAULT_LOAD_FACTOR);
    }

    <T> IFastIterableSet<T> newFastIterableSet(int initialSize, float loadFactor);

    default <T> Set<T> newLinkedSet(final Collection<? extends T> copyOf) {
        final Set<T> set = newLinkedSet(copyOf.size());
        set.addAll(copyOf);
        return set;
    }

    default <T> Set<T> newLinkedSet() {
        return newLinkedSet(DEFAULT_INITIAL_SIZE);
    }

    default <T> Set<T> newLinkedSet(final int initialSize) {
        return newLinkedSet(initialSize, DEFAULT_LOAD_FACTOR);
    }

    <T> Set<T> newLinkedSet(int initialSize, float loadFactor);

    default <T> IFastIterableSet<T> newFastIterableLinkedSet() {
        return newFastIterableLinkedSet(DEFAULT_INITIAL_SIZE);
    }

    default <T> IFastIterableSet<T> newFastIterableLinkedSet(final int initialSize) {
        return newFastIterableLinkedSet(initialSize, DEFAULT_LOAD_FACTOR);
    }

    <T> IFastIterableSet<T> newFastIterableLinkedSet(int initialSize, float loadFactor);

    default <T> IFastIterableSet<T> newFastIterableIdentitySet() {
        return newFastIterableIdentitySet(DEFAULT_INITIAL_SIZE);
    }

    <T> IFastIterableSet<T> newFastIterableIdentitySet(int initialSize);

    default <T> Set<T> newConcurrentSet() {
        return newConcurrentSet(DEFAULT_INITIAL_SIZE);
    }

    default <T> Set<T> newConcurrentSet(final int initialSize) {
        return newConcurrentSet(initialSize, DEFAULT_LOAD_FACTOR);
    }

    default <T> Set<T> newConcurrentSet(final int initialSize, final float loadFactor) {
        return newConcurrentSet(initialSize, loadFactor, DEFAULT_CONCURRENCY_LEVEL);
    }

    <T> Set<T> newConcurrentSet(int initialSize, float loadFactor, int concurrencyLevel);

    default <T> Set<T> newIdentitySet() {
        return newIdentitySet(DEFAULT_INITIAL_SIZE_IDENTITY);
    }

    <T> Set<T> newIdentitySet(int initialSize);

    default <K, V> Map<K, V> newMap(final Map<? extends K, ? extends V> copyOf) {
        final Map<K, V> map = newMap(copyOf.size());
        map.putAll(copyOf);
        return map;
    }

    default <K, V> Map<K, V> newMap() {
        return newMap(DEFAULT_INITIAL_SIZE);
    }

    default <K, V> Map<K, V> newMap(final int initialSize) {
        return newMap(initialSize, DEFAULT_LOAD_FACTOR);
    }

    <K, V> Map<K, V> newMap(int initialSize, float loadFactor);

    default <K, V> IFastIterableMap<K, V> newFastIterableMap() {
        return newFastIterableMap(DEFAULT_INITIAL_SIZE);
    }

    default <K, V> IFastIterableMap<K, V> newFastIterableMap(final int initialSize) {
        return newFastIterableMap(initialSize, DEFAULT_LOAD_FACTOR);
    }

    <K, V> IFastIterableMap<K, V> newFastIterableMap(int initialSize, float loadFactor);

    default <K, V> Map<K, V> newLinkedMap(final Map<? extends K, ? extends V> copyOf) {
        final Map<K, V> map = newLinkedMap(copyOf.size());
        map.putAll(copyOf);
        return map;
    }

    default <K, V> Map<K, V> newLinkedMap() {
        return newLinkedMap(DEFAULT_INITIAL_SIZE);
    }

    default <K, V> Map<K, V> newLinkedMap(final int initialSize) {
        return newLinkedMap(initialSize, DEFAULT_LOAD_FACTOR);
    }

    <K, V> Map<K, V> newLinkedMap(int initialSize, float loadFactor);

    default <K, V> Map<K, V> newConcurrentMap() {
        return newConcurrentMap(DEFAULT_INITIAL_SIZE);
    }

    default <K, V> Map<K, V> newConcurrentMap(final int initialSize) {
        return newConcurrentMap(initialSize, DEFAULT_LOAD_FACTOR);
    }

    default <K, V> Map<K, V> newConcurrentMap(final int initialSize, final float loadFactor) {
        return newConcurrentMap(initialSize, loadFactor, DEFAULT_CONCURRENCY_LEVEL);
    }

    <K, V> Map<K, V> newConcurrentMap(int initialSize, float loadFactor, int concurrencyLevel);

    default <K, V> Map<K, V> newIdentityMap() {
        return newIdentityMap(DEFAULT_INITIAL_SIZE_IDENTITY);
    }

    <K, V> Map<K, V> newIdentityMap(int initialSize);

    default <K, V> IFastIterableMap<K, V> newFastIterableLinkedMap() {
        return newFastIterableLinkedMap(DEFAULT_INITIAL_SIZE);
    }

    default <K, V> IFastIterableMap<K, V> newFastIterableLinkedMap(final int initialSize) {
        return newFastIterableLinkedMap(initialSize, DEFAULT_LOAD_FACTOR);
    }

    <K, V> IFastIterableMap<K, V> newFastIterableLinkedMap(int initialSize, float loadFactor);

    default <K, V> IFastIterableMap<K, V> newFastIterableIdentityMap() {
        return newFastIterableIdentityMap(DEFAULT_INITIAL_SIZE);
    }

    <K, V> IFastIterableMap<K, V> newFastIterableIdentityMap(int initialSize);

    <K, V> ALoadingCache<K, V> newLoadingCache(ALoadingCacheConfig<K, V> config);

    <K, V> NavigableMap<K, V> newTreeMap();

    <K, V> NavigableMap<K, V> newTreeMap(IComparator<? super K> comparator);

    <K, V> IFastIterableMap<K, V> newFastIterableTreeMap();

    <K, V> IFastIterableMap<K, V> newFastIterableTreeMap(IComparator<? super K> comparator);

    INestedExecutor newNestedExecutor(String name);

    <T> ILazyReference<T> newLazyReference(Supplier<T> factory);

    /**
     * returns a lock factory with disabled synchronization for thread unsafe usage (can be externally synchronized
     * maybe)
     */
    ILockCollectionFactory disabled();

    static ILockCollectionFactory getInstance(final boolean threadSafe) {
        if (threadSafe) {
            return SynchronizedLockCollectionFactory.INSTANCE;
        } else {
            return DisabledLockCollectionFactory.INSTANCE;
        }
    }

    boolean isThreadSafe();

    <K, V> Map<K, V> synchronizedMap(Map<K, V> map);

    <T> Set<T> synchronizedSet(Set<T> set);

    <T> List<T> synchronizedList(List<T> list);

    <T> Collection<T> synchronizedCollection(Collection<T> collection);

    <K, V> Map<K, V> synchronizedMap(Map<K, V> map, Object lock);

    <T> Set<T> synchronizedSet(Set<T> set, Object lock);

    <T> List<T> synchronizedList(List<T> list, Object lock);

    <T> Collection<T> synchronizedCollection(Collection<T> collection, Object lock);

    <K, V> Map<K, V> lockedMap(Map<K, V> map);

    <T> Set<T> lockedSet(Set<T> set);

    <T> List<T> lockedList(List<T> list);

    <T> Collection<T> lockedCollection(Collection<T> collection);

    <K, V> Map<K, V> lockedMap(Map<K, V> map, ILock lock);

    <T> Set<T> lockedSet(Set<T> set, ILock lock);

    <T> List<T> lockedList(List<T> list, ILock lock);

    <T> Collection<T> lockedCollection(Collection<T> collection, ILock lock);

    <T> Set<T> newImmutableSet(Collection<? extends T> copyOf);

    <T> Set<T> newImmutableSet(Iterable<? extends T> copyOf);

    <T> Set<T> newImmutableSet(Iterator<? extends T> copyOf);

    @SuppressWarnings("unchecked")
    <T> Set<T> newImmutableSet(T... copyOf);

    <T> List<T> newImmutableList(Collection<? extends T> copyOf);

    <T> List<T> newImmutableList(Iterable<? extends T> copyOf);

    <T> List<T> newImmutableList(Iterator<? extends T> copyOf);

    @SuppressWarnings("unchecked")
    <T> List<T> newImmutableList(T... copyOf);

}
