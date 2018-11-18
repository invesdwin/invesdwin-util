package de.invesdwin.util.collections.factory;

import java.util.List;
import java.util.Map;

import de.invesdwin.util.collections.fast.IFastIterableCollection;
import de.invesdwin.util.collections.fast.IFastIterableList;
import de.invesdwin.util.collections.fast.IFastIterableMap;
import de.invesdwin.util.collections.loadingcache.ALoadingCache;
import de.invesdwin.util.collections.loadingcache.ALoadingCacheConfig;
import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.nested.INestedExecutor;

public interface ILockCollectionFactory {

    ILock newLock(String name);

    <T> IFastIterableCollection<T> newFastIterableLinkedHashSet();

    <T> IFastIterableList<T> newFastIterableArrayList();

    <K, V> Map<K, V> newMap();

    <K, V> IFastIterableMap<K, V> newFastIterableLinkedHashMap();

    <K, V> ALoadingCache<K, V> newLoadingCache(ALoadingCacheConfig<K, V> config);

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

    <K, V> Map<K, V> newConcurrentMap();

    <T> List<T> newArrayList();

}
