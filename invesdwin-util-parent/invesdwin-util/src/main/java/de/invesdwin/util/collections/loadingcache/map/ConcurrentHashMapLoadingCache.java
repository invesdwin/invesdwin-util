package de.invesdwin.util.collections.loadingcache.map;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class ConcurrentHashMapLoadingCache<K, V> extends AMapLoadingCache<K, V> {

    public ConcurrentHashMapLoadingCache(final Function<K, V> loadValue) {
        super(loadValue, new ConcurrentHashMap<>());
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
