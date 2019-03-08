package de.invesdwin.util.collections.loadingcache.map;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.loadingcache.ILoadingCache;

@ThreadSafe
public abstract class AMapLoadingCache<K, V> implements ILoadingCache<K, V> {

    protected final Map<K, V> map;
    private final Function<K, V> loadValue;

    public AMapLoadingCache(final Function<K, V> loadValue, final Map<K, V> map) {
        this.loadValue = loadValue;
        this.map = map;
    }

    @Override
    public V get(final K key) {
        V v = map.get(key);
        if (v == null) {
            //bad idea to synchronize in apply, this might cause deadlocks when threads are used inside of it
            v = loadValue.apply(key);
            if (v != null) {
                final V oldV = map.get(key);
                if (oldV != null) {
                    v = oldV;
                } else {
                    map.put(key, v);
                }
            }
        }
        return v;
    }

    @Override
    public V getIfPresent(final K key) {
        final V v = map.get(key);
        return v;
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public boolean containsKey(final K key) {
        return map.containsKey(key);
    }

    @Override
    public void remove(final K key) {
        map.remove(key);
    }

    @Override
    public void put(final K key, final V value) {
        map.put(key, value);
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return map.entrySet();
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public Set<K> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<V> values() {
        return map.values();
    }

    @Override
    public Map<K, V> asMap() {
        return Collections.unmodifiableMap(map);
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

}
