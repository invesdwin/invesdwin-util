package de.invesdwin.util.collections.primitive.longkey;

import java.util.function.BiFunction;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.primitive.APrimitiveConcurrentMap;
import de.invesdwin.util.collections.primitive.APrimitiveConcurrentMapBuilder;
import de.invesdwin.util.collections.primitive.PrimitiveConcurrentMapMode;
import de.invesdwin.util.concurrent.lock.ICloseableLock;
import de.invesdwin.util.error.UnknownArgumentException;
import it.unimi.dsi.fastutil.longs.Long2IntFunction;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;

@ThreadSafe
public class ConcurrentLong2IntMap extends APrimitiveConcurrentMap<Long, Integer> {
    protected final Long2IntOpenHashMap[] maps;
    protected final int defaultValue;

    public ConcurrentLong2IntMap(final int numBuckets, final int initialCapacity, final float loadFactor,
            final int defaultValue) {
        super(numBuckets);
        this.maps = new Long2IntOpenHashMap[numBuckets];
        this.defaultValue = defaultValue;
        for (int i = 0; i < numBuckets; i++) {
            maps[i] = new Long2IntOpenHashMap(initialCapacity, loadFactor);
        }
    }

    @Override
    protected Long2IntOpenHashMap mapAt(final int index) {
        return maps[index];
    }

    public boolean containsKey(final long key) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = readAt(bucket)) {
            return maps[bucket].containsKey(key);
        }
    }

    public int get(final long key) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = readAt(bucket)) {
            return maps[bucket].getOrDefault(key, defaultValue);
        }
    }

    public int put(final long key, final int value) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            return maps[bucket].put(key, value);
        }
    }

    public int getDefaultValue() {
        return defaultValue;
    }

    public int remove(final long key) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            return maps[bucket].remove(key);
        }
    }

    public boolean remove(final long key, final int value) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            return maps[bucket].remove(key, value);
        }
    }

    public int computeIfAbsent(final long key, final Long2IntFunction mappingFunction) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            return maps[bucket].computeIfAbsent(key, mappingFunction);
        }
    }

    public int computeIfPresent(final long key, final BiFunction<Long, Integer, Integer> mappingFunction) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            return maps[bucket].computeIfPresent(key, mappingFunction);
        }
    }

    public static APrimitiveConcurrentMapBuilder<ConcurrentLong2IntMap, Integer> newBuilder() {
        return new APrimitiveConcurrentMapBuilder<ConcurrentLong2IntMap, Integer>() {
            @Override
            public ConcurrentLong2IntMap build() {
                final int def = super.defaultValue != null ? super.defaultValue : 0;
                switch (mapMode) {
                case BUSY_WAITING:
                    return new ConcurrentBusyWaitingLong2IntMap(buckets, initialCapacity, loadFactor, def);
                case BLOCKING:
                    return new ConcurrentLong2IntMap(buckets, initialCapacity, loadFactor, def);
                default:
                    throw UnknownArgumentException.newInstance(PrimitiveConcurrentMapMode.class, mapMode);
                }
            }
        };
    }
}