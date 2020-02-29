package de.invesdwin.util.collections.loadingcache.map;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.loadingcache.ILoadingCache;

@Immutable
public class NoCachingLoadingCache<K, V> implements ILoadingCache<K, V> {

    private final Function<K, V> loadValue;

    public NoCachingLoadingCache(final Function<K, V> loadValue) {
        this.loadValue = loadValue;

    }

    @Override
    public V get(final K key) {
        return loadValue.apply(key);
    }

    @Override
    public void clear() {
    }

    @Override
    public boolean containsKey(final K key) {
        return false;
    }

    @Override
    public void remove(final K key) {
    }

    @Override
    public void put(final K key, final V value) {
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return Collections.emptySet();
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public Set<K> keySet() {
        return Collections.emptySet();
    }

    @Override
    public Collection<V> values() {
        return Collections.emptySet();
    }

    @Override
    public Map<K, V> asMap() {
        return Collections.emptyMap();
    }

    @Override
    public void increaseMaximumSize(final int maximumSize) {
        //ignore
    }

    @Override
    public V getIfPresent(final K key) {
        return null;
    }

    @Override
    public V computeIfAbsent(final K key, final Function<K, V> mappingFunction) {
        return mappingFunction.apply(key);
    }

}
