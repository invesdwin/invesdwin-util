package de.invesdwin.util.collections.primitive.longkey;

import java.util.function.BiFunction;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.primitive.APrimitiveConcurrentMap;
import de.invesdwin.util.collections.primitive.APrimitiveConcurrentMapBuilder;
import de.invesdwin.util.collections.primitive.PrimitiveConcurrentMapMode;
import de.invesdwin.util.concurrent.lock.ICloseableLock;
import de.invesdwin.util.error.UnknownArgumentException;
import it.unimi.dsi.fastutil.longs.Long2ObjectFunction;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

@ThreadSafe
public class ConcurrentLong2ObjectMap<V> extends APrimitiveConcurrentMap<Long, V> {
    protected final Long2ObjectOpenHashMap<V>[] maps;
    protected final V defaultValue;

    @SuppressWarnings("unchecked")
    public ConcurrentLong2ObjectMap(final int initialCapacity, final float loadFactor, final int concurrencyLevel,
            final V defaultValue) {
        super(concurrencyLevel);
        this.maps = new Long2ObjectOpenHashMap[concurrencyLevel];
        this.defaultValue = defaultValue;
        final int bucketCapacity = APrimitiveConcurrentMapBuilder.newBucketCapacity(initialCapacity, concurrencyLevel);
        for (int i = 0; i < concurrencyLevel; i++) {
            maps[i] = new Long2ObjectOpenHashMap<>(bucketCapacity, loadFactor);
        }
    }

    @Override
    protected final Long2ObjectOpenHashMap<V> mapAt(final int index) {
        return maps[index];
    }

    public boolean containsKey(final long key) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = readAt(bucket)) {
            return maps[bucket].containsKey(key);
        }
    }

    public V get(final long key) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = readAt(bucket)) {
            return maps[bucket].getOrDefault(key, defaultValue);
        }
    }

    public V put(final long key, final V value) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            return maps[bucket].put(key, value);
        }
    }

    public V getDefaultValue() {
        return defaultValue;
    }

    public V remove(final long key) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            return maps[bucket].remove(key);
        }
    }

    public boolean remove(final long key, final V value) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            return maps[bucket].remove(key, value);
        }
    }

    public V computeIfAbsent(final long key, final Long2ObjectFunction<V> mappingFunction) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            return maps[bucket].computeIfAbsent(key, mappingFunction);
        }
    }

    public V computeIfPresent(final long key, final BiFunction<Long, V, V> mappingFunction) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            return maps[bucket].computeIfPresent(key, mappingFunction);
        }
    }

    public static <V> APrimitiveConcurrentMapBuilder<ConcurrentLong2ObjectMap<V>, V> newBuilder() {
        return new APrimitiveConcurrentMapBuilder<ConcurrentLong2ObjectMap<V>, V>() {
            @Override
            public ConcurrentLong2ObjectMap<V> build() {
                switch (mode) {
                case BUSY_WAITING:
                    return new BusyWaitingConcurrentLong2ObjectMap<>(initialCapacity, loadFactor, concurrencyLevel,
                            super.defaultValue);
                case BLOCKING:
                    return new ConcurrentLong2ObjectMap<>(initialCapacity, loadFactor, concurrencyLevel,
                            super.defaultValue);
                default:
                    throw UnknownArgumentException.newInstance(PrimitiveConcurrentMapMode.class, mode);
                }
            }
        };
    }
}