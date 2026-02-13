package de.invesdwin.util.collections.primitive.objkey;

import java.util.concurrent.locks.Lock;
import java.util.function.BiFunction;

import javax.annotation.concurrent.ThreadSafe;

import it.unimi.dsi.fastutil.objects.Object2ObjectFunction;

@ThreadSafe
public class BusyWaitingConcurrentObject2ObjectMap<K, V> extends ConcurrentObject2ObjectMap<K, V> {
    public BusyWaitingConcurrentObject2ObjectMap(final int initialCapacity, final float loadFactor,
            final int concurrencyLevel, final V defaultValue) {
        super(initialCapacity, loadFactor, concurrencyLevel, defaultValue);
    }

    @Override
    public boolean containsKey(final K key) {
        final int bucket = getBucket(key);

        final Lock readLock = readLock(bucket);

        while (true) {
            if (readLock.tryLock()) {
                try {
                    return maps[bucket].containsKey(key);
                } finally {
                    readLock.unlock();
                }
            }
            Thread.onSpinWait();
        }
    }

    @Override
    public V get(final K key) {
        final int bucket = getBucket(key);

        final Lock readLock = readLock(bucket);

        while (true) {
            if (readLock.tryLock()) {
                try {
                    return maps[bucket].getOrDefault(key, defaultValue);
                } finally {
                    readLock.unlock();
                }
            }
            Thread.onSpinWait();
        }
    }

    @Override
    public V put(final K key, final V value) {
        final int bucket = getBucket(key);

        final Lock writeLock = writeLock(bucket);

        while (true) {
            if (writeLock.tryLock()) {
                try {
                    return maps[bucket].put(key, value);
                } finally {
                    writeLock.unlock();
                }
            }
            Thread.onSpinWait();
        }
    }

    @Override
    public V remove(final K key) {
        final int bucket = getBucket(key);

        final Lock writeLock = writeLock(bucket);

        while (true) {
            if (writeLock.tryLock()) {
                try {
                    return maps[bucket].remove(key);
                } finally {
                    writeLock.unlock();
                }
            }
            Thread.onSpinWait();
        }
    }

    @Override
    public boolean remove(final K key, final V value) {
        final int bucket = getBucket(key);

        final Lock writeLock = writeLock(bucket);

        while (true) {
            if (writeLock.tryLock()) {
                try {
                    return maps[bucket].remove(key, value);
                } finally {
                    writeLock.unlock();
                }
            }
            Thread.onSpinWait();
        }
    }

    @Override
    public V computeIfAbsent(final K key, final Object2ObjectFunction<K, V> mappingFunction) {
        final int bucket = getBucket(key);

        final Lock writeLock = writeLock(bucket);

        while (true) {
            if (writeLock.tryLock()) {
                try {
                    return maps[bucket].computeIfAbsent(key, mappingFunction);
                } finally {
                    writeLock.unlock();
                }
            }
            Thread.onSpinWait();
        }
    }

    @Override
    public V computeIfPresent(final K key, final BiFunction<K, V, V> mappingFunction) {
        final int bucket = getBucket(key);

        final Lock writeLock = writeLock(bucket);

        while (true) {
            if (writeLock.tryLock()) {
                try {
                    return maps[bucket].computeIfPresent(key, mappingFunction);
                } finally {
                    writeLock.unlock();
                }
            }
            Thread.onSpinWait();
        }
    }
}