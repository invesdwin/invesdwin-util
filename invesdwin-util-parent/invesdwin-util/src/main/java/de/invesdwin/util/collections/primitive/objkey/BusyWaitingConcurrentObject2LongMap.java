package de.invesdwin.util.collections.primitive.objkey;

import java.util.concurrent.locks.Lock;
import java.util.function.BiFunction;

import javax.annotation.concurrent.ThreadSafe;

import it.unimi.dsi.fastutil.objects.Object2LongFunction;

@ThreadSafe
public class BusyWaitingConcurrentObject2LongMap<K> extends ConcurrentObject2LongMap<K> {
    public BusyWaitingConcurrentObject2LongMap(final int numBuckets, final int initialCapacity, final float loadFactor,
            final long defaultValue) {
        super(numBuckets, initialCapacity, loadFactor, defaultValue);
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
    public long get(final K key) {
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
    public long put(final K key, final long value) {
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
    public long remove(final K key) {
        final int bucket = getBucket(key);

        final Lock writeLock = writeLock(bucket);

        while (true) {
            if (writeLock.tryLock()) {
                try {
                    return maps[bucket].removeLong(key);
                } finally {
                    writeLock.unlock();
                }
            }
            Thread.onSpinWait();
        }
    }

    @Override
    public boolean remove(final K key, final long value) {
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
    public long computeIfAbsent(final K key, final Object2LongFunction<K> mappingFunction) {
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
    public long computeIfPresent(final K key, final BiFunction<K, Long, Long> mappingFunction) {
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