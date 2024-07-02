package de.invesdwin.util.collections.factory;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Supplier;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.Collections;
import de.invesdwin.util.collections.bitset.IBitSet;
import de.invesdwin.util.collections.bitset.JavaBitSet;
import de.invesdwin.util.collections.bitset.RoaringBitSet;
import de.invesdwin.util.collections.fast.FastIterableDelegateList;
import de.invesdwin.util.collections.fast.FastIterableDelegateMap;
import de.invesdwin.util.collections.fast.FastIterableDelegateSet;
import de.invesdwin.util.collections.fast.IFastIterableList;
import de.invesdwin.util.collections.fast.IFastIterableMap;
import de.invesdwin.util.collections.fast.IFastIterableSet;
import de.invesdwin.util.collections.loadingcache.ALoadingCache;
import de.invesdwin.util.collections.loadingcache.ALoadingCacheConfig;
import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.disabled.DisabledLock;
import de.invesdwin.util.concurrent.lock.disabled.DisabledReadWriteLock;
import de.invesdwin.util.concurrent.lock.readwrite.IReadWriteLock;
import de.invesdwin.util.concurrent.nested.DisabledNestedExecutor;
import de.invesdwin.util.concurrent.nested.INestedExecutor;
import de.invesdwin.util.concurrent.reference.lazy.ILazyReference;
import de.invesdwin.util.concurrent.reference.lazy.LazyReference;
import de.invesdwin.util.lang.comparator.IComparator;
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
    private static final int ROARING_BITMAP_THRESHOLD = 1_000_000;

    private DisabledLockCollectionFactory() {}

    @Override
    public ILock newLock(final String name) {
        return DisabledLock.INSTANCE;
    }

    @Override
    public IReadWriteLock newReadWriteLock(final String name) {
        return DisabledReadWriteLock.INSTANCE;
    }

    @Override
    public IBitSet newBitSet(final int initialSize) {
        /*
         * java bitsets are about twice as fast as roaring bitsets, though roaring might be interesting to use with
         * larger sizes to stay in memory limits
         */
        if (initialSize > ROARING_BITMAP_THRESHOLD) {
            return new RoaringBitSet(initialSize);
        } else {
            return new JavaBitSet(initialSize);
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
    public <K, V> Map<K, V> newConcurrentMap(final int initialSize, final float loadFactor) {
        return newMap(initialSize, loadFactor);
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
    public <K, V> NavigableMap<K, V> newTreeMap() {
        return new TreeMap<K, V>();
    }

    @Override
    public <K, V> NavigableMap<K, V> newTreeMap(final IComparator<? super K> comparator) {
        return new TreeMap<K, V>(comparator.asTyped());
    }

    @Override
    public <T> Set<T> newConcurrentSet(final int initialSize, final float loadFactor) {
        return newSet(initialSize, loadFactor);
    }

    @Override
    public <T> Set<T> newIdentitySet(final int initialSize) {
        return Collections.newSetFromMap(newIdentityMap(initialSize));
    }

    @Override
    public <K, V> Map<K, V> newIdentityMap(final int initialSize) {
        return new IdentityHashMap<K, V>(initialSize);
    }

    @Override
    public <T> ILazyReference<T> newLazyReference(final Supplier<T> factory) {
        return new LazyReference<>(factory);
    }

    @Override
    public boolean isThreadSafe() {
        return false;
    }

}
