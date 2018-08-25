package de.invesdwin.util.collections.loadingcache.map;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import java.util.function.Function;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.loadingcache.ILoadingCache;
import de.invesdwin.util.concurrent.Threads;

@ThreadSafe
public abstract class AReadWriteLockLoadingCache<K, V> implements ILoadingCache<K, V> {

    private final Map<K, V> map;
    private final Function<K, V> loadValue;
    private final ReentrantReadWriteLock lock = Threads.getCycleDetectingLockFactory()
            .newReentrantReadWriteLock(AReadWriteLockLoadingCache.class.getSimpleName() + "_lock");
    private final WriteLock writeLock = lock.writeLock();
    private final ReadLock readLock = lock.readLock();

    public AReadWriteLockLoadingCache(final Function<K, V> loadValue, final Map<K, V> map) {
        this.loadValue = loadValue;
        this.map = map;
    }

    @Override
    public V get(final K key) {
        V v = internalGet(key);
        if (v == null) {
            writeLock.lock();
            try {
                v = loadValue.apply(key);
                map.put(key, v);
            } finally {
                writeLock.unlock();
            }
        }
        return v;
    }

    private V internalGet(final K key) {
        readLock.lock();
        try {
            return map.get(key);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void clear() {
        writeLock.lock();
        try {
            map.clear();
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean containsKey(final K key) {
        readLock.lock();
        try {
            return map.containsKey(key);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void remove(final K key) {
        writeLock.lock();
        try {
            map.remove(key);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void put(final K key, final V value) {
        writeLock.lock();
        try {
            map.put(key, value);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        readLock.lock();
        try {
            return map.entrySet();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public int size() {
        readLock.lock();
        try {
            return map.size();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<K> keySet() {
        readLock.lock();
        try {
            return map.keySet();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Collection<V> values() {
        readLock.lock();
        try {
            return map.values();
        } finally {
            readLock.unlock();
        }
    }

}
