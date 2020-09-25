package de.invesdwin.util.collections.factory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;

import javax.annotation.concurrent.Immutable;

import com.koloboke.collect.impl.hash.LHashObjSetFactoryImpl;
import com.koloboke.collect.impl.hash.LHashParallelKVObjObjMapFactoryImpl;
import com.koloboke.collect.map.hash.HashObjObjMapFactory;
import com.koloboke.collect.set.hash.HashObjSetFactory;

import de.invesdwin.util.collections.bitset.IBitSet;
import de.invesdwin.util.collections.bitset.JavaBitSet;
import de.invesdwin.util.collections.bitset.RoaringBitSet;
import de.invesdwin.util.collections.fast.AFastIterableDelegateList;
import de.invesdwin.util.collections.fast.AFastIterableDelegateMap;
import de.invesdwin.util.collections.fast.AFastIterableDelegateSet;
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
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import uk.co.omegaprime.btreemap.BTreeMap;

@Immutable
public final class DisabledLockCollectionFactory implements ILockCollectionFactory {

    public static final int ROARING_BITMAP_THRESHOLD = 250000;

    public static final DisabledLockCollectionFactory INSTANCE = new DisabledLockCollectionFactory();
    //ServiceLoader does not work properly during maven builds, thus directly reference the actual factories
    private static final HashObjObjMapFactory<?, ?> KOLOBOKE_MAP_FACTORY = new LHashParallelKVObjObjMapFactoryImpl<Object, Object>();
    private static final HashObjSetFactory<?> KOLOBOKE_SET_FACTORY = new LHashObjSetFactoryImpl<Object>();

    private DisabledLockCollectionFactory() {
    }

    @Override
    public ILock newLock(final String name) {
        return DisabledLock.INSTANCE;
    }

    @Override
    public IReadWriteLock newReadWriteLock(final String name) {
        return DisabledReadWriteLock.INSTANCE;
    }

    @Override
    public IBitSet newBitSet(final int expectedSize) {
        /*
         * java bitsets are about twice as fast as roaring bitsets, though roaring might be interesting to use with
         * larger sizes to stay in memory limits
         */
        if (expectedSize > ROARING_BITMAP_THRESHOLD) {
            return new RoaringBitSet();
        } else {
            return new JavaBitSet(expectedSize);
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
    public <T> IFastIterableSet<T> newFastIterableLinkedSet() {
        return new DisabledFastIterableLinkedSet<T>();
    }

    @Override
    public <T> IFastIterableList<T> newFastIterableArrayList() {
        return new DisabledFastIterableArrayList<T>();
    }

    @Override
    public <T> IFastIterableList<T> newFastIterableArrayList(final int expectedSize) {
        return new DisabledFastIterableArrayListWithSize<T>(expectedSize);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public <K, V> Map<K, V> newMap() {
        //koboloke has the same memory efficiency as fastutil but is a bit faster
        return (Map) KOLOBOKE_MAP_FACTORY.newMutableMap();
    }

    @Override
    public <K, V> IFastIterableMap<K, V> newFastIterableLinkedMap() {
        return new DisabledFastIterableLinkedMap<K, V>();
    }

    @Override
    public <K, V> Map<K, V> newConcurrentMap() {
        return newMap();
    }

    @Override
    public <T> List<T> newArrayList() {
        return new ArrayList<>();
    }

    @Override
    public <T> List<T> newArrayList(final int expectedSize) {
        return new ArrayList<>(expectedSize);
    }

    @Override
    public <T> IFastIterableSet<T> newFastIterableSet() {
        return new DisabledFastIterableSet<T>();
    }

    @Override
    public <K, V> IFastIterableMap<K, V> newFastIterableMap() {
        return new DisabledFastIterableMap<K, V>();
    }

    @Override
    public <K, V> Map<K, V> newLinkedMap() {
        return new Object2ObjectLinkedOpenHashMap<>();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public <T> Set<T> newSet() {
        //koboloke has the same memory efficiency as fastutil but is a bit faster
        return (Set) KOLOBOKE_SET_FACTORY.newMutableSet();
    }

    @Override
    public <T> Set<T> newLinkedSet() {
        return new ObjectLinkedOpenHashSet<>();
    }

    private static final class DisabledFastIterableMap<K, V> extends AFastIterableDelegateMap<K, V> {
        @Override
        protected Map<K, V> newDelegate() {
            return INSTANCE.newMap();
        }
    }

    private static final class DisabledFastIterableSet<T> extends AFastIterableDelegateSet<T> {
        @Override
        protected Set<T> newDelegate() {
            return INSTANCE.newSet();
        }
    }

    private static final class DisabledFastIterableLinkedMap<K, V> extends AFastIterableDelegateMap<K, V> {
        @Override
        protected Map<K, V> newDelegate() {
            return INSTANCE.newLinkedMap();
        }
    }

    private static final class DisabledFastIterableArrayList<T> extends AFastIterableDelegateList<T> {
        @Override
        protected List<T> newDelegate() {
            return INSTANCE.newArrayList();
        }
    }

    private static final class DisabledFastIterableArrayListWithSize<T> extends AFastIterableDelegateList<T> {

        private DisabledFastIterableArrayListWithSize(final int expectedSize) {
            super(INSTANCE.newArrayList(expectedSize));
        }

        @Override
        protected List<T> newDelegate() {
            throw new UnsupportedOperationException();
        }
    }

    private static final class DisabledFastIterableLinkedSet<T> extends AFastIterableDelegateSet<T> {
        @Override
        protected Set<T> newDelegate() {
            return INSTANCE.newLinkedSet();
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public <K, V> NavigableMap<K, V> newTreeMap() {
        return (NavigableMap) BTreeMap.create();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public <K, V> NavigableMap<K, V> newTreeMap(final Comparator<? extends K> comparator) {
        return BTreeMap.<K, V> create((Comparator) comparator);
    }

    @Override
    public <T> Set<T> newConcurrentSet() {
        return newSet();
    }

    @Override
    public <T> Set<T> newIdentitySet() {
        return Collections.newSetFromMap(newIdentityMap());
    }

    @Override
    public <K, V> Map<K, V> newIdentityMap() {
        return new IdentityHashMap<K, V>();
    }

}
