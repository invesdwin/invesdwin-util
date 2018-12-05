package de.invesdwin.util.collections.factory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.fast.IFastIterableList;
import de.invesdwin.util.collections.fast.IFastIterableMap;
import de.invesdwin.util.collections.fast.IFastIterableSet;
import de.invesdwin.util.collections.fast.concurrent.ASynchronizedFastIterableDelegateList;
import de.invesdwin.util.collections.fast.concurrent.ASynchronizedFastIterableDelegateMap;
import de.invesdwin.util.collections.fast.concurrent.ASynchronizedFastIterableDelegateSet;
import de.invesdwin.util.collections.loadingcache.ALoadingCache;
import de.invesdwin.util.collections.loadingcache.ALoadingCacheConfig;
import de.invesdwin.util.concurrent.Executors;
import de.invesdwin.util.concurrent.WrappedExecutorService;
import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.Locks;
import de.invesdwin.util.concurrent.nested.ANestedExecutor;
import de.invesdwin.util.concurrent.nested.INestedExecutor;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

@Immutable
public final class SynchronizedLockCollectionFactory implements ILockCollectionFactory {

    public static final SynchronizedLockCollectionFactory INSTANCE = new SynchronizedLockCollectionFactory();

    private SynchronizedLockCollectionFactory() {}

    @Override
    public ILock newLock(final String name) {
        return Locks.newReentrantLock(name);
    }

    @Override
    public <T> IFastIterableSet<T> newFastIterableLinkedSet() {
        return new SynchronizedFastIterableLinkedSet<T>();
    }

    @Override
    public <T> IFastIterableList<T> newFastIterableArrayList() {
        return new SynchronizedFastIterableArrayList<T>();
    }

    @Override
    public <K, V> Map<K, V> newMap() {
        return Collections.synchronizedMap(new Object2ObjectOpenHashMap<K, V>());
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
    public <K, V> IFastIterableMap<K, V> newFastIterableLinkedMap() {
        return new SynchronizedFastIterableLinkedMap<K, V>();
    }

    @Override
    public <K, V> Map<K, V> newConcurrentMap() {
        return new ConcurrentHashMap<>();
    }

    @Override
    public <T> List<T> newArrayList() {
        return Collections.synchronizedList(new ArrayList<>());
    }

    @Override
    public <T> IFastIterableSet<T> newFastIterableSet() {
        return new SynchronizedFastIterableSet<T>();
    }

    @Override
    public <K, V> IFastIterableMap<K, V> newFastIterableMap() {
        return new SynchronizedFastIterableMap<K, V>();
    }

    @Override
    public <K, V> Map<K, V> newLinkedMap() {
        return Collections.synchronizedMap(new Object2ObjectLinkedOpenHashMap<>());
    }

    @Override
    public <T> Set<T> newSet() {
        return Collections.synchronizedSet(new ObjectOpenHashSet<>());
    }

    @Override
    public <T> Set<T> newLinkedSet() {
        return Collections.synchronizedSet(new ObjectLinkedOpenHashSet<>());
    }

    private static final class SynchronizedFastIterableMap<K, V> extends ASynchronizedFastIterableDelegateMap<K, V> {
        @Override
        protected Map<K, V> newDelegate() {
            return new Object2ObjectOpenHashMap<>();
        }
    }

    private static final class SynchronizedFastIterableSet<T> extends ASynchronizedFastIterableDelegateSet<T> {
        @Override
        protected Set<T> newDelegate() {
            return new ObjectOpenHashSet<>();
        }
    }

    private static final class SynchronizedFastIterableLinkedMap<K, V> extends ASynchronizedFastIterableDelegateMap<K, V> {
        @Override
        protected Map<K, V> newDelegate() {
            return new Object2ObjectLinkedOpenHashMap<K, V>();
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

    private static final class SynchronizedFastIterableArrayList<T> extends ASynchronizedFastIterableDelegateList<T> {
        @Override
        protected List<T> newDelegate() {
            return new ArrayList<>();
        }
    }

    private static final class SynchronizedFastIterableLinkedSet<T> extends ASynchronizedFastIterableDelegateSet<T> {
        @Override
        protected Set<T> newDelegate() {
            return new ObjectLinkedOpenHashSet<>();
        }
    }
}
