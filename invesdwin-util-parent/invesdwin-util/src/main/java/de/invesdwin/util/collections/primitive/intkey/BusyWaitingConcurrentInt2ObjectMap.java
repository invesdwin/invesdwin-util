package de.invesdwin.util.collections.primitive.intkey;

import java.util.concurrent.locks.Lock;
import java.util.function.BiFunction;

import javax.annotation.concurrent.ThreadSafe;

import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;

@ThreadSafe
public class BusyWaitingConcurrentInt2ObjectMap<V> extends ConcurrentInt2ObjectMap<V> {
    public BusyWaitingConcurrentInt2ObjectMap(final int numBuckets, final int initialCapacity, final float loadFactor,
            final V defaultValue) {
        super(numBuckets, initialCapacity, loadFactor, defaultValue);
    }

    @Override
    public boolean containsKey(final int key) {
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
    public V get(final int intKey) {
        final int bucket = getBucket(intKey);
        final Lock readLock = readLock(bucket);

        while (true) {
            if (readLock.tryLock()) {
                try {
                    return maps[bucket].getOrDefault(intKey, defaultValue);
                } finally {
                    readLock.unlock();
                }
            }
            Thread.onSpinWait();
        }
    }

    @Override
    public V put(final int key, final V value) {
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
    public V remove(final int key) {
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
    public boolean remove(final int key, final V value) {
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
    public V computeIfAbsent(final int key, final Int2ObjectFunction<V> mappingFunction) {
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
    public V computeIfPresent(final int key, final BiFunction<Integer, V, V> mappingFunction) {
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