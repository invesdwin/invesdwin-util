package de.invesdwin.util.collections.primitive.objkey;

import java.util.function.BiFunction;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.primitive.APrimitiveConcurrentMap;
import de.invesdwin.util.collections.primitive.APrimitiveConcurrentMapBuilder;
import de.invesdwin.util.collections.primitive.PrimitiveConcurrentMapMode;
import de.invesdwin.util.concurrent.lock.ICloseableLock;
import de.invesdwin.util.error.UnknownArgumentException;
import it.unimi.dsi.fastutil.objects.Object2ObjectFunction;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

@ThreadSafe
public class ConcurrentObject2ObjectMap<K, V> extends APrimitiveConcurrentMap<K, V> {
    protected final Object2ObjectOpenHashMap<K, V>[] maps;
    protected final V defaultValue;

    @SuppressWarnings("unchecked")
    public ConcurrentObject2ObjectMap(final int initialCapacity, final float loadFactor, final int concurrencyLevel,
            final V defaultValue) {
        super(concurrencyLevel);
        this.maps = new Object2ObjectOpenHashMap[concurrencyLevel];
        this.defaultValue = defaultValue;
        final int bucketCapacity = APrimitiveConcurrentMapBuilder.newBucketCapacity(initialCapacity, concurrencyLevel);
        for (int i = 0; i < concurrencyLevel; i++) {
            maps[i] = new Object2ObjectOpenHashMap<>(bucketCapacity, loadFactor);
        }
    }

    @Override
    protected final Object2ObjectOpenHashMap<K, V> mapAt(final int index) {
        return maps[index];
    }

    public boolean containsKey(final K key) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = readAt(bucket)) {
            return maps[bucket].containsKey(key);
        }
    }

    public V get(final K key) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = readAt(bucket)) {
            return maps[bucket].getOrDefault(key, defaultValue);
        }
    }

    public V put(final K key, final V value) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            return maps[bucket].put(key, value);
        }
    }

    public V getDefaultValue() {
        return defaultValue;
    }

    public V remove(final K key) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            return maps[bucket].remove(key);
        }
    }

    public boolean remove(final K key, final V value) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            return maps[bucket].remove(key, value);
        }
    }

    public V computeIfAbsent(final K key, final Object2ObjectFunction<K, V> mappingFunction) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            return maps[bucket].computeIfAbsent(key, mappingFunction);
        }
    }

    public V computeIfPresent(final K key, final BiFunction<K, V, V> mappingFunction) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            return maps[bucket].computeIfPresent(key, mappingFunction);
        }
    }

    public static <K, V> APrimitiveConcurrentMapBuilder<ConcurrentObject2ObjectMap<K, V>, V> newBuilder() {
        return new APrimitiveConcurrentMapBuilder<ConcurrentObject2ObjectMap<K, V>, V>() {
            @Override
            public ConcurrentObject2ObjectMap<K, V> build() {
                switch (mode) {
                case BUSY_WAITING:
                    return new BusyWaitingConcurrentObject2ObjectMap<K, V>(initialCapacity, loadFactor,
                            concurrencyLevel, super.defaultValue);
                case BLOCKING:
                    return new ConcurrentObject2ObjectMap<K, V>(initialCapacity, loadFactor, concurrencyLevel,
                            super.defaultValue);
                default:
                    throw UnknownArgumentException.newInstance(PrimitiveConcurrentMapMode.class, mode);
                }
            }
        };
    }
}