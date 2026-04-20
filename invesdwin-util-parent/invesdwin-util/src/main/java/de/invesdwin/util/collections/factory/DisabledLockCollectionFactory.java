package de.invesdwin.util.collections.factory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Supplier;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.Collections;
import de.invesdwin.util.collections.array.large.bitset.ILargeBitSet;
import de.invesdwin.util.collections.array.large.bitset.JavaLargeBitSet;
import de.invesdwin.util.collections.array.large.bitset.roaring.RoaringLargeBitSet;
import de.invesdwin.util.collections.array.large.bitset.roaring.SegmentedRoaringLargeBitSet;
import de.invesdwin.util.collections.array.primitive.bitset.IPrimitiveBitSet;
import de.invesdwin.util.collections.array.primitive.bitset.JavaPrimitiveBitSet;
import de.invesdwin.util.collections.array.primitive.bitset.RoaringPrimitiveBitSet;
import de.invesdwin.util.collections.fast.FastIterableDelegateList;
import de.invesdwin.util.collections.fast.FastIterableDelegateMap;
import de.invesdwin.util.collections.fast.FastIterableDelegateSet;
import de.invesdwin.util.collections.fast.IFastIterableList;
import de.invesdwin.util.collections.fast.IFastIterableMap;
import de.invesdwin.util.collections.fast.IFastIterableSet;
import de.invesdwin.util.collections.list.Lists;
import de.invesdwin.util.collections.loadingcache.ALoadingCache;
import de.invesdwin.util.collections.loadingcache.ALoadingCacheConfig;
import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.disabled.DisabledLock;
import de.invesdwin.util.concurrent.lock.disabled.DisabledReentrantReadWriteLock;
import de.invesdwin.util.concurrent.lock.readwrite.IReentrantReadWriteLock;
import de.invesdwin.util.concurrent.nested.DisabledNestedExecutor;
import de.invesdwin.util.concurrent.nested.INestedExecutor;
import de.invesdwin.util.concurrent.reference.lazy.ILazyReference;
import de.invesdwin.util.concurrent.reference.lazy.LazyReference;
import de.invesdwin.util.lang.comparator.IComparator;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

@Immutable
public final class DisabledLockCollectionFactory implements ILockCollectionFactory {

    public static final DisabledLockCollectionFactory INSTANCE = new DisabledLockCollectionFactory();
    /**
     * At a few 100k elements the speed of roaring bitmap is similar to that of java bitset (about 20-30% slower instead
     * of 50%). Thus prefer the memory saver version at some threshold.
     */
    public static final int ROARING_BITMAP_THRESHOLD = 1_000_000;

    private DisabledLockCollectionFactory() {}

    @Override
    public ILock newLock(final String name) {
        return DisabledLock.INSTANCE;
    }

    @Override
    public IReentrantReadWriteLock newReadWriteLock(final String name) {
        return DisabledReentrantReadWriteLock.INSTANCE;
    }

    @Override
    public IPrimitiveBitSet newPrimitiveBitSet(final int initialSize) {
        /*
         * java bitsets are about twice as fast as roaring bitsets, though roaring might be interesting to use with
         * larger sizes to stay in memory limits
         */
        if (initialSize > ROARING_BITMAP_THRESHOLD) {
            return new RoaringPrimitiveBitSet(initialSize);
        } else {
            return new JavaPrimitiveBitSet(initialSize);
        }
    }

    @Override
    public ILargeBitSet newLargeBitSet(final long initialSize) {
        if (initialSize > RoaringLargeBitSet.MAX_SIZE) {
            /*
             * TODO: test which one is actually faster. Roaring64 implementations do not support range-based operations,
             * which are used in some places. They might be slower than the segmented version, which does support them.
             */
            //return new Roaring64LargeNavigableBitSet(initialSize);
            //return new Roaring64LargeBitSet(initialSize);
            return new SegmentedRoaringLargeBitSet(initialSize);
        } else if (initialSize > ROARING_BITMAP_THRESHOLD) {
            return new RoaringLargeBitSet(initialSize);
        } else {
            return new JavaLargeBitSet(ByteBuffers.checkedCast(initialSize));
        }
    }

    @Override
    public INestedExecutor newNestedExecutor(final String name) {
        return new DisabledNestedExecutor(name);
    }

    @Override
    public ILockCollectionFactory disabled() {
        return this;
    }

    @Override
    public <K, V> ALoadingCache<K, V> newLoadingCache(final ALoadingCacheConfig<K, V> config) {
        config.setHighConcurrencyOverride(false);
        config.setThreadSafeOverride(false);
        return config.newInstance();
    }

    @Override
    public <T> IFastIterableSet<T> newFastIterableLinkedSet(final int initialSize, final float loadFactor) {
        return new FastIterableDelegateSet<T>(newLinkedSet(initialSize, loadFactor));
    }

    @Override
    public <T> IFastIterableList<T> newFastIterableArrayList() {
        return new FastIterableDelegateList<T>(newArrayList());
    }

    @Override
    public <T> IFastIterableList<T> newFastIterableArrayList(final int initialSize) {
        return new FastIterableDelegateList<T>(newArrayList(initialSize));
    }

    @Override
    public <K, V> Map<K, V> newMap(final int initialSize, final float loadFactor) {
        return new Object2ObjectOpenHashMap<>(initialSize, loadFactor);
    }

    @Override
    public <K, V> IFastIterableMap<K, V> newFastIterableLinkedMap(final int initialSize, final float loadFactor) {
        return new FastIterableDelegateMap<K, V>(newLinkedMap(initialSize, loadFactor));
    }

    @Override
    public <K, V> IFastIterableMap<K, V> newFastIterableIdentityMap(final int initialSize) {
        return new FastIterableDelegateMap<K, V>(newIdentityMap(initialSize));
    }

    @Override
    public <K, V> Map<K, V> newConcurrentMap(final int initialSize, final float loadFactor,
            final int concurrencyLevel) {
        return newMap(initialSize, loadFactor);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> newArrayList(final T... copyOf) {
        return new ArrayList<>(Arrays.asList(copyOf));
    }

    @Override
    public <T> List<T> newArrayList(final Collection<? extends T> copyOf) {
        return new ArrayList<>(copyOf);
    }

    @Override
    public <T> List<T> newArrayList() {
        return new ArrayList<>();
    }

    @Override
    public <T> List<T> newArrayList(final int initialSize) {
        return new ArrayList<>(initialSize);
    }

    @Override
    public <T> IFastIterableSet<T> newFastIterableSet(final int initialSize, final float loadFactor) {
        return new FastIterableDelegateSet<T>(newSet(initialSize, loadFactor));
    }

    @Override
    public <T> IFastIterableSet<T> newFastIterableIdentitySet(final int initialSize) {
        return new FastIterableDelegateSet<T>(newIdentitySet(initialSize));
    }

    @Override
    public <K, V> IFastIterableMap<K, V> newFastIterableMap(final int initialSize, final float loadFactor) {
        return new FastIterableDelegateMap<K, V>(newMap(initialSize, loadFactor));
    }

    @Override
    public <K, V> Map<K, V> newLinkedMap(final int initialSize, final float loadFactor) {
        return new Object2ObjectLinkedOpenHashMap<>(initialSize, loadFactor);
    }

    @Override
    public <T> Set<T> newSet(final int initialSize, final float loadFactor) {
        return new ObjectOpenHashSet<>(initialSize, loadFactor);
    }

    @Override
    public <T> Set<T> newLinkedSet(final int initialSize, final float loadFactor) {
        return new ObjectLinkedOpenHashSet<>(initialSize, loadFactor);
    }

    @Override
    public <T> NavigableSet<T> newTreeSet() {
        //CHECKSTYLE:OFF
        return new TreeSet<T>();
        //CHECKSTYLE:ON
    }

    @Override
    public <T> NavigableSet<T> newTreeSet(final IComparator<? super T> comparator) {
        //CHECKSTYLE:OFF
        return new TreeSet<T>(comparator.asTyped());
        //CHECKSTYLE:ON
    }

    @Override
    public <K, V> NavigableMap<K, V> newTreeMap() {
        //CHECKSTYLE:OFF
        return new TreeMap<K, V>();
        //CHECKSTYLE:ON
    }

    @Override
    public <K, V> NavigableMap<K, V> newTreeMap(final IComparator<? super K> comparator) {
        //CHECKSTYLE:OFF
        return new TreeMap<K, V>(comparator.asTyped());
        //CHECKSTYLE:ON
    }

    @Override
    public <K, V> IFastIterableMap<K, V> newFastIterableTreeMap() {
        return new FastIterableDelegateMap<K, V>(newTreeMap()) {
            @Override
            protected void addToFastIterable(final K key, final V value) {
                refreshFastIterable();
            }
        };
    }

    @Override
    public <K, V> IFastIterableMap<K, V> newFastIterableTreeMap(final IComparator<? super K> comparator) {
        return new FastIterableDelegateMap<K, V>(newTreeMap(comparator)) {
            @Override
            protected void addToFastIterable(final K key, final V value) {
                refreshFastIterable();
            }
        };
    }

    @Override
    public <T> Set<T> newConcurrentSet(final int initialSize, final float loadFactor, final int concurrencyLevel) {
        return newSet(initialSize, loadFactor);
    }

    @Override
    public <T> Set<T> newIdentitySet(final int initialSize) {
        return Collections.newSetFromMap(newIdentityMap(initialSize));
    }

    @Override
    public <K, V> Map<K, V> newIdentityMap(final int initialSize) {
        //CHECKSTYLE:OFF
        return new IdentityHashMap<K, V>(initialSize);
        //CHECKSTYLE:ON
    }

    @Override
    public <T> ILazyReference<T> newLazyReference(final Supplier<T> factory) {
        return new LazyReference<>(factory);
    }

    @Override
    public boolean isThreadSafe() {
        return false;
    }

    @Override
    public <K, V> Map<K, V> synchronizedMap(final Map<K, V> map) {
        return map;
    }

    @Override
    public <T> Set<T> synchronizedSet(final Set<T> set) {
        return set;
    }

    @Override
    public <T> List<T> synchronizedList(final List<T> list) {
        return list;
    }

    @Override
    public <T> Collection<T> synchronizedCollection(final Collection<T> collection) {
        return collection;
    }

    @Override
    public <K, V> Map<K, V> synchronizedMap(final Map<K, V> map, final Object lock) {
        return map;
    }

    @Override
    public <T> Set<T> synchronizedSet(final Set<T> set, final Object lock) {
        return set;
    }

    @Override
    public <T> List<T> synchronizedList(final List<T> list, final Object lock) {
        return list;
    }

    @Override
    public <T> Collection<T> synchronizedCollection(final Collection<T> collection, final Object lock) {
        return collection;
    }

    @Override
    public <K, V> Map<K, V> lockedMap(final Map<K, V> map) {
        return map;
    }

    @Override
    public <T> Set<T> lockedSet(final Set<T> set) {
        return set;
    }

    @Override
    public <T> List<T> lockedList(final List<T> list) {
        return list;
    }

    @Override
    public <T> Collection<T> lockedCollection(final Collection<T> collection) {
        return collection;
    }

    @Override
    public <K, V> Map<K, V> lockedMap(final Map<K, V> map, final ILock lock) {
        return map;
    }

    @Override
    public <T> Set<T> lockedSet(final Set<T> set, final ILock lock) {
        return set;
    }

    @Override
    public <T> List<T> lockedList(final List<T> list, final ILock lock) {
        return list;
    }

    @Override
    public <T> Collection<T> lockedCollection(final Collection<T> collection, final ILock lock) {
        return collection;
    }

    @Override
    public <T> Set<T> newImmutableSet(final Collection<? extends T> copyOf) {
        return Collections.unmodifiableSet(newSet(copyOf));
    }

    @Override
    public <T> Set<T> newImmutableSet(final Iterable<? extends T> copyOf) {
        final Set<T> set = newSet();
        Collections.addAll(set, copyOf);
        return Collections.unmodifiableSet(set);
    }

    @Override
    public <T> Set<T> newImmutableSet(final Iterator<? extends T> copyOf) {
        final Set<T> set = newSet();
        Collections.addAll(set, copyOf);
        return Collections.unmodifiableSet(set);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Set<T> newImmutableSet(final T... copyOf) {
        return Collections.unmodifiableSet(newSet(Arrays.asList(copyOf)));
    }

    @Override
    public <T> List<T> newImmutableList(final Collection<? extends T> copyOf) {
        return Collections.unmodifiableList(newArrayList(copyOf));
    }

    @Override
    public <T> List<T> newImmutableList(final Iterable<? extends T> copyOf) {
        return Collections.unmodifiableList(Lists.toListWithoutHasNext(copyOf, newArrayList()));
    }

    @Override
    public <T> List<T> newImmutableList(final Iterator<? extends T> copyOf) {
        return Collections.unmodifiableList(Lists.toListWithoutHasNext(copyOf, newArrayList()));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> newImmutableList(final T... copyOf) {
        return Collections.unmodifiableList(Arrays.asList(copyOf));
    }

    @Override
    public <T> Set<T> newImmutableLinkedSet(final Collection<? extends T> copyOf) {
        return Collections.unmodifiableSet(newLinkedSet(copyOf));
    }

    @Override
    public <T> Set<T> newImmutableLinkedSet(final Iterable<? extends T> copyOf) {
        final Set<T> set = newLinkedSet();
        Collections.addAll(set, copyOf);
        return Collections.unmodifiableSet(set);
    }

    @Override
    public <T> Set<T> newImmutableLinkedSet(final Iterator<? extends T> copyOf) {
        final Set<T> set = newLinkedSet();
        Collections.addAll(set, copyOf);
        return Collections.unmodifiableSet(set);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Set<T> newImmutableLinkedSet(final T... copyOf) {
        return newImmutableLinkedSet(Arrays.asList(copyOf));
    }

    @Override
    public <K, V> Map<K, V> newImmutableLinkedMap(final Map<? extends K, ? extends V> copyOf) {
        return Collections.unmodifiableMap(newLinkedMap(copyOf));
    }

}
