package de.invesdwin.util.collections.primitive.longkey;

import java.util.concurrent.locks.Lock;
import java.util.function.BiFunction;

import javax.annotation.concurrent.ThreadSafe;

import it.unimi.dsi.fastutil.longs.Long2LongFunction;

@ThreadSafe
public class ConcurrentBusyWaitingLong2LongMap extends ConcurrentLong2LongMap {
    public ConcurrentBusyWaitingLong2LongMap(final int numBuckets, final int initialCapacity, final float loadFactor, final long defaultValue) {
        super(numBuckets, initialCapacity, loadFactor, defaultValue);
    }

    @Override
    public boolean containsKey(final long key) {
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
    public long get(final long key) {
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
    public long put(final long key, final long value) {
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
    public long remove(final long key) {
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
    public boolean remove(final long key, final long value) {
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
    public long computeIfAbsent(final long key, final Long2LongFunction mappingFunction) {
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
    public long computeIfPresent(final long key, final BiFunction<Long, Long, Long> mappingFunction) {
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