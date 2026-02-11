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

    public ConcurrentLong2IntMap(final int initialCapacity, final float loadFactor, final int concurrencyLevel,
            final int defaultValue) {
        super(concurrencyLevel);
        this.maps = new Long2IntOpenHashMap[concurrencyLevel];
        this.defaultValue = defaultValue;
        final int bucketCapacity = APrimitiveConcurrentMapBuilder.newBucketCapacity(initialCapacity, concurrencyLevel);
        for (int i = 0; i < concurrencyLevel; i++) {
            maps[i] = new Long2IntOpenHashMap(bucketCapacity, loadFactor);
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
                switch (mode) {
                case BUSY_WAITING:
                    return new BusyWaitingConcurrentLong2IntMap(initialCapacity, loadFactor, concurrencyLevel, def);
                case BLOCKING:
                    return new ConcurrentLong2IntMap(initialCapacity, loadFactor, concurrencyLevel, def);
                default:
                    throw UnknownArgumentException.newInstance(PrimitiveConcurrentMapMode.class, mode);
                }
            }
        };
    }
}