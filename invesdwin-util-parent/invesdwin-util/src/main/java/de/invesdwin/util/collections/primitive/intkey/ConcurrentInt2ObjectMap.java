package de.invesdwin.util.collections.primitive.intkey;

import java.util.function.BiFunction;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.primitive.APrimitiveConcurrentMap;
import de.invesdwin.util.collections.primitive.APrimitiveConcurrentMapBuilder;
import de.invesdwin.util.collections.primitive.PrimitiveConcurrentMapMode;
import de.invesdwin.util.concurrent.lock.ICloseableLock;
import de.invesdwin.util.error.UnknownArgumentException;
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

@ThreadSafe
public class ConcurrentInt2ObjectMap<V> extends APrimitiveConcurrentMap<Integer, V> {
    protected final Int2ObjectOpenHashMap<V>[] maps;
    protected final V defaultValue;

    @SuppressWarnings("unchecked")
    public ConcurrentInt2ObjectMap(final int numBuckets, final int initialCapacity, final float loadFactor,
            final V defaultValue) {
        super(numBuckets);

        this.maps = new Int2ObjectOpenHashMap[numBuckets];
        this.defaultValue = defaultValue;
        for (int i = 0; i < numBuckets; i++) {
            maps[i] = new Int2ObjectOpenHashMap<V>(initialCapacity, loadFactor);
        }
    }

    @Override
    protected Int2ObjectOpenHashMap<V> mapAt(final int index) {
        return maps[index];
    }

    public boolean containsKey(final int key) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = readAt(bucket)) {
            return maps[bucket].containsKey(key);
        }
    }

    public V get(final int intKey) {
        final int bucket = getBucket(intKey);
        try (ICloseableLock lock = readAt(bucket)) {
            return maps[bucket].getOrDefault(intKey, defaultValue);
        }
    }

    public V put(final int intKey, final V value) {
        final int bucket = getBucket(intKey);
        try (ICloseableLock lock = writeAt(bucket)) {
            return maps[bucket].put(intKey, value);
        }
    }

    public V getDefaultValue() {
        return defaultValue;
    }

    public V remove(final int intKey) {
        final int bucket = getBucket(intKey);
        try (ICloseableLock lock = writeAt(bucket)) {
            return maps[bucket].remove(intKey);
        }
    }

    public boolean remove(final int key, final V value) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            return maps[bucket].remove(key, value);
        }
    }

    public V computeIfAbsent(final int key, final Int2ObjectFunction<V> mappingFunction) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            return maps[bucket].computeIfAbsent(key, mappingFunction);
        }
    }

    public V computeIfPresent(final int key, final BiFunction<Integer, V, V> mappingFunction) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            return maps[bucket].computeIfPresent(key, mappingFunction);
        }
    }

    public static <V> APrimitiveConcurrentMapBuilder<ConcurrentInt2ObjectMap<V>, V> newBuilder() {
        return new APrimitiveConcurrentMapBuilder<ConcurrentInt2ObjectMap<V>, V>() {
            @Override
            public ConcurrentInt2ObjectMap<V> build() {
                final V def = super.defaultValue;
                switch (mapMode) {
                case BUSY_WAITING:
                    return new ConcurrentBusyWaitingInt2ObjectMap<V>(buckets, initialCapacity, loadFactor, def);
                case BLOCKING:
                    return new ConcurrentInt2ObjectMap<V>(buckets, initialCapacity, loadFactor, def);
                default:
                    throw UnknownArgumentException.newInstance(PrimitiveConcurrentMapMode.class, mapMode);
                }
            }
        };
    }
}