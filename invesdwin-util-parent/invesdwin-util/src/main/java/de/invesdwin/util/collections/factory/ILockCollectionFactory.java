package de.invesdwin.util.collections.factory;

import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;

import de.invesdwin.util.collections.bitset.IBitSet;
import de.invesdwin.util.collections.fast.IFastIterableList;
import de.invesdwin.util.collections.fast.IFastIterableMap;
import de.invesdwin.util.collections.fast.IFastIterableSet;
import de.invesdwin.util.collections.loadingcache.ALoadingCache;
import de.invesdwin.util.collections.loadingcache.ALoadingCacheConfig;
import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.readwrite.IReadWriteLock;
import de.invesdwin.util.concurrent.nested.INestedExecutor;
import de.invesdwin.util.lang.comparator.IComparator;
import it.unimi.dsi.fastutil.Hash;

public interface ILockCollectionFactory {

    int DEFAULT_INITIAL_SIZE = Hash.DEFAULT_INITIAL_SIZE;
    int DEFAULT_INITIAL_SIZE_IDENTITY = 32;
    /*
     * higher load factor means better space efficiency while having a worse lookup performance (due to collisions)
     */
    float DEFAULT_LOAD_FACTOR = Hash.DEFAULT_LOAD_FACTOR;

    ILock newLock(String name);

    IReadWriteLock newReadWriteLock(String name);

    IBitSet newBitSet(int initialSize);

    default <T> List<T> newArrayList() {
        return newArrayList(DEFAULT_INITIAL_SIZE);
    }

    <T> List<T> newArrayList(int initialSize);

    default <T> IFastIterableList<T> newFastIterableArrayList() {
        return newFastIterableArrayList(DEFAULT_INITIAL_SIZE);
    }

    <T> IFastIterableList<T> newFastIterableArrayList(int initialSize);

    default <T> Set<T> newSet() {
        return newSet(DEFAULT_INITIAL_SIZE);
    }

    default <T> Set<T> newSet(final int initialSize) {
        return newSet(initialSize, DEFAULT_LOAD_FACTOR);
    }

    <T> Set<T> newSet(int initialSize, float loadFactor);

    default <T> IFastIterableSet<T> newFastIterableSet() {
        return newFastIterableSet(DEFAULT_INITIAL_SIZE);
    }

    default <T> IFastIterableSet<T> newFastIterableSet(final int initialSize) {
        return newFastIterableSet(initialSize, DEFAULT_LOAD_FACTOR);
    }

    <T> IFastIterableSet<T> newFastIterableSet(int initialSize, float loadFactor);

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

    default <T> Set<T> newConcurrentSet() {
        return newConcurrentSet(DEFAULT_INITIAL_SIZE);
    }

    default <T> Set<T> newConcurrentSet(final int initialSize) {
        return newConcurrentSet(initialSize, DEFAULT_LOAD_FACTOR);
    }

    <T> Set<T> newConcurrentSet(int initialSize, float loadFactor);

    default <T> Set<T> newIdentitySet() {
        return newIdentitySet(DEFAULT_INITIAL_SIZE_IDENTITY);
    }

    <T> Set<T> newIdentitySet(int initialSize);

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

    <K, V> Map<K, V> newConcurrentMap(int initialSize, float loadFactor);

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

    <K, V> ALoadingCache<K, V> newLoadingCache(ALoadingCacheConfig<K, V> config);

    <K, V> NavigableMap<K, V> newTreeMap();

    <K, V> NavigableMap<K, V> newTreeMap(IComparator<? super K> comparator);

    INestedExecutor newNestedExecutor(String name);

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

}
