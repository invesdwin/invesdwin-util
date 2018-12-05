package de.invesdwin.util.collections.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.concurrent.Immutable;

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
import de.invesdwin.util.concurrent.nested.DisabledNestedExecutor;
import de.invesdwin.util.concurrent.nested.INestedExecutor;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

@Immutable
public final class DisabledLockCollectionFactory implements ILockCollectionFactory {

    public static final DisabledLockCollectionFactory INSTANCE = new DisabledLockCollectionFactory();

    private DisabledLockCollectionFactory() {}

    @Override
    public ILock newLock(final String name) {
        return DisabledLock.INSTANCE;
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
    public <K, V> Map<K, V> newMap() {
        return new Object2ObjectOpenHashMap<>();
    }

    @Override
    public INestedExecutor newNestedExecutor(final String name) {
        return DisabledNestedExecutor.INSTANCE;
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
    public <K, V> IFastIterableMap<K, V> newFastIterableLinkedMap() {
        return new DisabledFastIterableLinkedMap<K, V>();
    }

    @Override
    public <K, V> Map<K, V> newConcurrentMap() {
        return new Object2ObjectOpenHashMap<>();
    }

    @Override
    public <T> List<T> newArrayList() {
        return new ArrayList<>();
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

    @Override
    public <T> Set<T> newSet() {
        return new ObjectOpenHashSet<>();
    }

    @Override
    public <T> Set<T> newLinkedSet() {
        return new ObjectLinkedOpenHashSet<>();
    }

    private static final class DisabledFastIterableMap<K, V> extends AFastIterableDelegateMap<K, V> {
        @Override
        protected Map<K, V> newDelegate() {
            return new Object2ObjectOpenHashMap<>();
        }
    }

    private static final class DisabledFastIterableSet<T> extends AFastIterableDelegateSet<T> {
        @Override
        protected Set<T> newDelegate() {
            return new ObjectOpenHashSet<>();
        }
    }

    private static final class DisabledFastIterableLinkedMap<K, V> extends AFastIterableDelegateMap<K, V> {
        @Override
        protected Map<K, V> newDelegate() {
            return new Object2ObjectLinkedOpenHashMap<K, V>();
        }
    }

    private static final class DisabledFastIterableArrayList<T> extends AFastIterableDelegateList<T> {
        @Override
        protected List<T> newDelegate() {
            return new ArrayList<>();
        }
    }

    private static final class DisabledFastIterableLinkedSet<T> extends AFastIterableDelegateSet<T> {
        @Override
        protected Set<T> newDelegate() {
            return new ObjectLinkedOpenHashSet<>();
        }
    }

}
