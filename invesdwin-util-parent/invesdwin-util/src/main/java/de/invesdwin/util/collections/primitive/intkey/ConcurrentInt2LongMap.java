package de.invesdwin.util.collections.primitive.intkey;

import java.util.function.BiFunction;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.primitive.APrimitiveConcurrentMap;
import de.invesdwin.util.collections.primitive.APrimitiveConcurrentMapBuilder;
import de.invesdwin.util.collections.primitive.PrimitiveConcurrentMapMode;
import de.invesdwin.util.concurrent.lock.ICloseableLock;
import de.invesdwin.util.error.UnknownArgumentException;
import it.unimi.dsi.fastutil.ints.Int2LongFunction;
import it.unimi.dsi.fastutil.ints.Int2LongOpenHashMap;

@ThreadSafe
public class ConcurrentInt2LongMap extends APrimitiveConcurrentMap<Integer, Long> {
    protected final Int2LongOpenHashMap[] maps;
    protected final long defaultValue;

    public ConcurrentInt2LongMap(final int numBuckets, final int initialCapacity, final float loadFactor,
            final long defaultValue) {
        super(numBuckets);

        this.maps = new Int2LongOpenHashMap[numBuckets];
        this.defaultValue = defaultValue;
        final int individualCapacity = APrimitiveConcurrentMapBuilder.newIndividualCapacity(initialCapacity,
                numBuckets);
        for (int i = 0; i < numBuckets; i++) {
            maps[i] = new Int2LongOpenHashMap(individualCapacity, loadFactor);
        }
    }

    @Override
    protected Int2LongOpenHashMap mapAt(final int index) {
        return maps[index];
    }

    public boolean containsKey(final int key) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = readAt(bucket)) {
            return maps[bucket].containsKey(key);
        }
    }

    public long get(final int intKey) {
        final int bucket = getBucket(intKey);
        try (ICloseableLock lock = readAt(bucket)) {
            return maps[bucket].getOrDefault(intKey, defaultValue);
        }
    }

    public long put(final int intKey, final long value) {
        final int bucket = getBucket(intKey);
        try (ICloseableLock lock = writeAt(bucket)) {
            return maps[bucket].put(intKey, value);
        }
    }

    public long getDefaultValue() {
        return defaultValue;
    }

    public long remove(final int intKey) {
        final int bucket = getBucket(intKey);
        try (ICloseableLock lock = writeAt(bucket)) {
            return maps[bucket].remove(intKey);
        }
    }

    public boolean remove(final int key, final long value) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            return maps[bucket].remove(key, value);
        }
    }

    public long computeIfAbsent(final int key, final Int2LongFunction mappingFunction) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            return maps[bucket].computeIfAbsent(key, mappingFunction);
        }
    }

    public long computeIfPresent(final int key, final BiFunction<Integer, Long, Long> mappingFunction) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            return maps[bucket].computeIfPresent(key, mappingFunction);
        }
    }

    public static APrimitiveConcurrentMapBuilder<ConcurrentInt2LongMap, Long> newBuilder() {
        return new APrimitiveConcurrentMapBuilder<ConcurrentInt2LongMap, Long>() {
            @Override
            public ConcurrentInt2LongMap build() {
                final long def = super.defaultValue != null ? super.defaultValue : 0;
                switch (mapMode) {
                case BUSY_WAITING:
                    return new BusyWaitingConcurrentInt2LongMap(buckets, initialCapacity, loadFactor, def);
                case BLOCKING:
                    return new ConcurrentInt2LongMap(buckets, initialCapacity, loadFactor, def);
                default:
                    throw UnknownArgumentException.newInstance(PrimitiveConcurrentMapMode.class, mapMode);
                }
            }
        };
    }
}