package de.invesdwin.util.collections.primitive.longkey;

import java.util.function.BiFunction;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.primitive.APrimitiveConcurrentMap;
import de.invesdwin.util.collections.primitive.APrimitiveConcurrentMapBuilder;
import de.invesdwin.util.collections.primitive.PrimitiveConcurrentMapMode;
import de.invesdwin.util.concurrent.lock.ICloseableLock;
import de.invesdwin.util.error.UnknownArgumentException;
import it.unimi.dsi.fastutil.longs.Long2FloatFunction;
import it.unimi.dsi.fastutil.longs.Long2FloatOpenHashMap;

@ThreadSafe
public class ConcurrentLong2FloatMap extends APrimitiveConcurrentMap<Long, Float> {
    protected final Long2FloatOpenHashMap[] maps;
    protected final float defaultValue;

    public ConcurrentLong2FloatMap(final int initialCapacity, final float loadFactor, final int concurrencyLevel,
            final float defaultValue) {
        super(concurrencyLevel);
        this.maps = new Long2FloatOpenHashMap[concurrencyLevel];
        this.defaultValue = defaultValue;
        final int bucketCapacity = APrimitiveConcurrentMapBuilder.newBucketCapacity(initialCapacity, concurrencyLevel);
        for (int i = 0; i < concurrencyLevel; i++) {
            maps[i] = new Long2FloatOpenHashMap(bucketCapacity, loadFactor);
        }
    }

    public float getDefaultValue() {
        return defaultValue;
    }

    @Override
    protected Long2FloatOpenHashMap mapAt(final int index) {
        return maps[index];
    }

    public boolean containsKey(final long key) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = readAt(bucket)) {
            return maps[bucket].containsKey(key);
        }
    }

    public float get(final long key) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = readAt(bucket)) {
            return maps[bucket].getOrDefault(key, defaultValue);
        }
    }

    public float put(final long key, final float value) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            return maps[bucket].put(key, value);
        }
    }

    public float remove(final long key) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            return maps[bucket].remove(key);
        }
    }

    public boolean remove(final long key, final float value) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            return maps[bucket].remove(key, value);
        }
    }

    public float computeIfAbsent(final long key, final Long2FloatFunction mappingFunction) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            return maps[bucket].computeIfAbsent(key, mappingFunction);
        }
    }

    public float computeIfPresent(final int key, final BiFunction<Long, Float, Float> mappingFunction) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            return maps[bucket].computeIfPresent(key, mappingFunction);
        }
    }

    public static APrimitiveConcurrentMapBuilder<ConcurrentLong2FloatMap, Float> newBuilder() {
        return new APrimitiveConcurrentMapBuilder<ConcurrentLong2FloatMap, Float>() {
            @Override
            public ConcurrentLong2FloatMap build() {
                final float def = super.defaultValue != null ? super.defaultValue : 0;
                switch (mode) {
                case BUSY_WAITING:
                    return new BusyWaitingConcurrentLong2FloatMap(initialCapacity, loadFactor, concurrencyLevel, def);
                case BLOCKING:
                    return new ConcurrentLong2FloatMap(initialCapacity, loadFactor, concurrencyLevel, def);
                default:
                    throw UnknownArgumentException.newInstance(PrimitiveConcurrentMapMode.class, mode);
                }
            }
        };
    }
}