package de.invesdwin.util.collections.loadingcache.map;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.fast.concurrent.SynchronizedCollection;
import de.invesdwin.util.collections.fast.concurrent.SynchronizedMap;
import de.invesdwin.util.collections.fast.concurrent.SynchronizedSet;
import de.invesdwin.util.collections.loadingcache.ILoadingCache;

@ThreadSafe
public abstract class ASynchronizedMapLoadingCache<K, V> implements ILoadingCache<K, V> {

    @GuardedBy("this")
    protected final Map<K, V> map;
    private final Function<K, V> loadValue;

    public ASynchronizedMapLoadingCache(final Function<K, V> loadValue, final Map<K, V> map) {
        this.loadValue = loadValue;
        this.map = map;
    }

    @Override
    public V get(final K key) {
        V v;
        synchronized (this) {
            v = map.get(key);
        }
        if (v == null) {
            //bad idea to synchronize in apply, this might cause deadlocks when threads are used inside of it
            v = loadValue.apply(key);
            if (v != null) {
                synchronized (this) {
                    final V oldV = map.putIfAbsent(key, v);
                    if (oldV != null) {
                        v = oldV;
                    }
                }
            }
        }
        return v;
    }

    @Override
    public synchronized V getIfPresent(final K key) {
        final V v = map.get(key);
        return v;
    }

    @Override
    public synchronized void clear() {
        map.clear();
    }

    @Override
    public synchronized boolean containsKey(final K key) {
        return map.containsKey(key);
    }

    @Override
    public synchronized void remove(final K key) {
        map.remove(key);
    }

    @Override
    public synchronized void put(final K key, final V value) {
        map.put(key, value);
    }

    @Override
    public synchronized Set<Entry<K, V>> entrySet() {
        return new SynchronizedSet<>(map.entrySet(), this);
    }

    @Override
    public synchronized int size() {
        return map.size();
    }

    @Override
    public Set<K> keySet() {
        return new SynchronizedSet<>(map.keySet(), this);
    }

    @Override
    public Collection<V> values() {
        return new SynchronizedCollection<>(map.values(), this);
    }

    @Override
    public Map<K, V> asMap() {
        return Collections.unmodifiableMap(new SynchronizedMap<>(map, this));
    }

    @Override
    public synchronized boolean isEmpty() {
        return map.isEmpty();
    }

}
