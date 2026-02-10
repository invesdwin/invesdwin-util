package de.invesdwin.util.collections.primitive.longkey;

import java.util.function.BiFunction;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.primitive.APrimitiveConcurrentMap;
import de.invesdwin.util.collections.primitive.APrimitiveConcurrentMapBuilder;
import de.invesdwin.util.collections.primitive.PrimitiveConcurrentMapMode;
import de.invesdwin.util.concurrent.lock.ICloseableLock;
import de.invesdwin.util.error.UnknownArgumentException;
import it.unimi.dsi.fastutil.longs.Long2LongFunction;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;

@ThreadSafe
public class ConcurrentLong2LongMap extends APrimitiveConcurrentMap<Long, Long> {
    protected final Long2LongOpenHashMap[] maps;
    protected final long defaultValue;

    public ConcurrentLong2LongMap(final int numBuckets, final int initialCapacity, final float loadFactor,
            final long defaultValue) {
        super(numBuckets);
        this.maps = new Long2LongOpenHashMap[numBuckets];
        this.defaultValue = defaultValue;
        for (int i = 0; i < numBuckets; i++) {
            maps[i] = new Long2LongOpenHashMap(initialCapacity, loadFactor);
        }
    }

    @Override
    protected final Long2LongOpenHashMap mapAt(final int index) {
        return maps[index];
    }

    public boolean containsKey(final long key) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = readAt(bucket)) {
            return maps[bucket].containsKey(key);
        }
    }

    public long get(final long key) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = readAt(bucket)) {
            return maps[bucket].getOrDefault(key, defaultValue);
        }
    }

    public long put(final long key, final long value) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            return maps[bucket].put(key, value);
        }
    }

    public long getDefaultValue() {
        return defaultValue;
    }

    public long remove(final long key) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            return maps[bucket].remove(key);
        }
    }

    public boolean remove(final long key, final long value) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            return maps[bucket].remove(key, value);
        }
    }

    public long computeIfAbsent(final long key, final Long2LongFunction mappingFunction) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            return maps[bucket].computeIfAbsent(key, mappingFunction);
        }
    }

    public long computeIfPresent(final long key, final BiFunction<Long, Long, Long> mappingFunction) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            return maps[bucket].computeIfPresent(key, mappingFunction);
        }
    }

    public static APrimitiveConcurrentMapBuilder<ConcurrentLong2LongMap, Long> newBuilder() {
        return new APrimitiveConcurrentMapBuilder<ConcurrentLong2LongMap, Long>() {
            @Override
            public ConcurrentLong2LongMap build() {
                final long def = super.defaultValue != null ? super.defaultValue : 0;
                switch (mapMode) {
                case BUSY_WAITING:
                    return new ConcurrentBusyWaitingLong2LongMap(buckets, initialCapacity, loadFactor, def);
                case BLOCKING:
                    return new ConcurrentLong2LongMap(buckets, initialCapacity, loadFactor, def);
                default:
                    throw UnknownArgumentException.newInstance(PrimitiveConcurrentMapMode.class, mapMode);
                }
            }
        };
    }
}