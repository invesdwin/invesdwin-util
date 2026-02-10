package de.invesdwin.util.collections.loadingcache.map;

import java.util.function.Function;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.factory.ILockCollectionFactory;

@ThreadSafe
public class ConcurrentHashMapLoadingCache<K, V> extends AMapLoadingCache<K, V> {

    public ConcurrentHashMapLoadingCache(final Function<K, V> loadValue) {
        super(loadValue, ILockCollectionFactory.getInstance(true).newConcurrentMap());
    }

    @Override
    public V get(final K key) {
        return computeIfAbsent(key, loadValue);
    }

    @Override
    public V computeIfAbsent(final K key, final Function<K, V> mappingFunction) {
        return map.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public void increaseMaximumSize(final int maximumSize) {
        //noop
    }

}
