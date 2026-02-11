package de.invesdwin.util.collections.primitive.intkey;

import java.util.function.BiFunction;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.primitive.APrimitiveConcurrentMap;
import de.invesdwin.util.collections.primitive.APrimitiveConcurrentMapBuilder;
import de.invesdwin.util.collections.primitive.PrimitiveConcurrentMapMode;
import de.invesdwin.util.concurrent.lock.ICloseableLock;
import de.invesdwin.util.error.UnknownArgumentException;
import it.unimi.dsi.fastutil.ints.Int2FloatFunction;
import it.unimi.dsi.fastutil.ints.Int2FloatOpenHashMap;

@ThreadSafe
public class ConcurrentInt2FloatMap extends APrimitiveConcurrentMap<Integer, Float> {
    protected final Int2FloatOpenHashMap[] maps;
    protected final float defaultValue;

    public ConcurrentInt2FloatMap(final int initialCapacity, final float loadFactor, final int concurrencyLevel,
            final float defaultValue) {
        super(concurrencyLevel);
        this.maps = new Int2FloatOpenHashMap[concurrencyLevel];
        this.defaultValue = defaultValue;
        final int bucketCapacity = APrimitiveConcurrentMapBuilder.newBucketCapacity(initialCapacity, concurrencyLevel);
        for (int i = 0; i < concurrencyLevel; i++) {
            maps[i] = new Int2FloatOpenHashMap(bucketCapacity, loadFactor);
        }
    }

    @Override
    protected Int2FloatOpenHashMap mapAt(final int index) {
        return maps[index];
    }

    public boolean containsKey(final int key) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = readAt(bucket)) {
            return maps[bucket].containsKey(key);
        }
    }

    public float get(final int key) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = readAt(bucket)) {
            return maps[bucket].getOrDefault(key, defaultValue);
        }
    }

    public float put(final int key, final float value) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            return maps[bucket].put(key, value);
        }
    }

    public float getDefaultValue() {
        return defaultValue;
    }

    public float remove(final int key) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            return maps[bucket].remove(key);
        }
    }

    public boolean remove(final int key, final float value) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            return maps[bucket].remove(key, value);
        }
    }

    public float computeIfAbsent(final int key, final Int2FloatFunction mappingFunction) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            return maps[bucket].computeIfAbsent(key, mappingFunction);
        }
    }

    public float computeIfPresent(final int key, final BiFunction<Integer, Float, Float> mappingFunction) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            return maps[bucket].computeIfPresent(key, mappingFunction);
        }
    }

    public static APrimitiveConcurrentMapBuilder<ConcurrentInt2FloatMap, Float> newBuilder() {
        return new APrimitiveConcurrentMapBuilder<ConcurrentInt2FloatMap, Float>() {
            @Override
            public ConcurrentInt2FloatMap build() {
                final float def = super.defaultValue != null ? super.defaultValue : 0;
                switch (mode) {
                case BUSY_WAITING:
                    return new BusyWaitingConcurrentInt2FloatMap(initialCapacity, loadFactor, concurrencyLevel, def);
                case BLOCKING:
                    return new ConcurrentInt2FloatMap(initialCapacity, loadFactor, concurrencyLevel, def);
                default:
                    throw UnknownArgumentException.newInstance(PrimitiveConcurrentMapMode.class, mode);
                }
            }
        };
    }
}