package de.invesdwin.util.collections.primitive.intkey;

import java.util.concurrent.locks.Lock;
import java.util.function.BiFunction;

import javax.annotation.concurrent.ThreadSafe;

import it.unimi.dsi.fastutil.ints.Int2FloatFunction;

@ThreadSafe
public class BusyWaitingConcurrentInt2FloatMap extends ConcurrentInt2FloatMap {
    public BusyWaitingConcurrentInt2FloatMap(final int initialCapacity, final float loadFactor,
            final int concurrencyLevel, final float defaultValue) {
        super(initialCapacity, loadFactor, concurrencyLevel, defaultValue);
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
    public float get(final int key) {
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
    public float put(final int key, final float value) {
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
    public float remove(final int key) {
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
    public boolean remove(final int key, final float value) {
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
    public float computeIfAbsent(final int key, final Int2FloatFunction mappingFunction) {
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
    public float computeIfPresent(final int key, final BiFunction<Integer, Float, Float> mappingFunction) {
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