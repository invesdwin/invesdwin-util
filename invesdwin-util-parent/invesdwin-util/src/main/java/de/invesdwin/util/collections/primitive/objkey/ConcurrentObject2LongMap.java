package de.invesdwin.util.collections.primitive.objkey;

import java.util.function.BiFunction;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.primitive.APrimitiveConcurrentMap;
import de.invesdwin.util.collections.primitive.APrimitiveConcurrentMapBuilder;
import de.invesdwin.util.collections.primitive.PrimitiveConcurrentMapMode;
import de.invesdwin.util.concurrent.lock.ICloseableLock;
import de.invesdwin.util.error.UnknownArgumentException;
import it.unimi.dsi.fastutil.objects.Object2LongFunction;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;

@ThreadSafe
public class ConcurrentObject2LongMap<K> extends APrimitiveConcurrentMap<K, Long> {
    protected final Object2LongOpenHashMap<K>[] maps;
    protected final long defaultValue;

    @SuppressWarnings("unchecked")
    public ConcurrentObject2LongMap(final int numBuckets, final int initialCapacity, final float loadFactor,
            final long defaultValue) {
        super(numBuckets);
        this.maps = new Object2LongOpenHashMap[numBuckets];
        this.defaultValue = defaultValue;
        for (int i = 0; i < numBuckets; i++) {
            maps[i] = new Object2LongOpenHashMap<>(initialCapacity, loadFactor);
        }
    }

    @Override
    protected final Object2LongOpenHashMap<K> mapAt(final int index) {
        return maps[index];
    }

    public boolean containsKey(final K key) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = readAt(bucket)) {
            return maps[bucket].containsKey(key);
        }
    }

    public long get(final K key) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = readAt(bucket)) {
            return maps[bucket].getOrDefault(key, defaultValue);
        }
    }

    public long put(final K key, final long value) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            return maps[bucket].put(key, value);
        }
    }

    public long getDefaultValue() {
        return defaultValue;
    }

    public long remove(final K key) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            return maps[bucket].removeLong(key);
        }
    }

    public boolean remove(final K key, final long value) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            return maps[bucket].remove(key, value);
        }
    }

    public long computeIfAbsent(final K key, final Object2LongFunction<K> mappingFunction) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            return maps[bucket].computeIfAbsent(key, mappingFunction);
        }
    }

    public long computeIfPresent(final K key, final BiFunction<K, Long, Long> mappingFunction) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            return maps[bucket].computeIfPresent(key, mappingFunction);
        }
    }

    public static <K> APrimitiveConcurrentMapBuilder<ConcurrentObject2LongMap<K>, Long> newBuilder() {
        return new APrimitiveConcurrentMapBuilder<ConcurrentObject2LongMap<K>, Long>() {
            @Override
            public ConcurrentObject2LongMap<K> build() {
                switch (mapMode) {
                case BUSY_WAITING:
                    return new BusyWaitingConcurrentObject2LongMap<>(buckets, initialCapacity, loadFactor,
                            super.defaultValue);
                case BLOCKING:
                    return new ConcurrentObject2LongMap<>(buckets, initialCapacity, loadFactor, super.defaultValue);
                default:
                    throw UnknownArgumentException.newInstance(PrimitiveConcurrentMapMode.class, mapMode);
                }
            }
        };
    }
}