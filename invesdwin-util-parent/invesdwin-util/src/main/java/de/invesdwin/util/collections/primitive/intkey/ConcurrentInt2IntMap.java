package de.invesdwin.util.collections.primitive.intkey;

import java.util.function.BiFunction;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.primitive.APrimitiveConcurrentMap;
import de.invesdwin.util.collections.primitive.APrimitiveConcurrentMapBuilder;
import de.invesdwin.util.collections.primitive.PrimitiveConcurrentMapMode;
import de.invesdwin.util.concurrent.lock.ICloseableLock;
import de.invesdwin.util.error.UnknownArgumentException;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;

@ThreadSafe
public class ConcurrentInt2IntMap extends APrimitiveConcurrentMap<Integer, Integer> {
    protected final Int2IntOpenHashMap[] maps;
    protected final int defaultValue;

    public ConcurrentInt2IntMap(final int initialCapacity, final float loadFactor, final int concurrencyLevel,
            final int defaultValue) {
        super(concurrencyLevel);
        this.maps = new Int2IntOpenHashMap[concurrencyLevel];
        this.defaultValue = defaultValue;
        final int bucketCapacity = APrimitiveConcurrentMapBuilder.newBucketCapacity(initialCapacity, concurrencyLevel);
        for (int i = 0; i < concurrencyLevel; i++) {
            maps[i] = new Int2IntOpenHashMap(bucketCapacity, loadFactor);
        }
    }

    @Override
    protected Int2IntOpenHashMap mapAt(final int index) {
        return maps[index];
    }

    public boolean containsKey(final int key) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = readAt(bucket)) {
            return maps[bucket].containsKey(key);
        }
    }

    public int get(final int key) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = readAt(bucket)) {
            return maps[bucket].getOrDefault(key, defaultValue);
        }
    }

    public int put(final int key, final int value) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            return maps[bucket].put(key, value);
        }
    }

    public int getDefaultValue() {
        return defaultValue;
    }

    public int remove(final int key) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            return maps[bucket].remove(key);
        }
    }

    public boolean remove(final int key, final int value) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            return maps[bucket].remove(key, value);
        }
    }

    public int computeIfAbsent(final int key, final Int2IntFunction mappingFunction) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            return maps[bucket].computeIfAbsent(key, mappingFunction);
        }
    }

    public int computeIfPresent(final int key, final BiFunction<Integer, Integer, Integer> mappingFunction) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            return maps[bucket].computeIfPresent(key, mappingFunction);
        }
    }

    public static APrimitiveConcurrentMapBuilder<ConcurrentInt2IntMap, Integer> newBuilder() {
        return new APrimitiveConcurrentMapBuilder<ConcurrentInt2IntMap, Integer>() {
            @Override
            public ConcurrentInt2IntMap build() {
                final int def = super.defaultValue != null ? super.defaultValue : 0;
                switch (mode) {
                case BUSY_WAITING:
                    return new BusyWaitingConcurrentInt2IntMap(initialCapacity, loadFactor, concurrencyLevel, def);
                case BLOCKING:
                    return new ConcurrentInt2IntMap(initialCapacity, loadFactor, concurrencyLevel, def);
                default:
                    throw UnknownArgumentException.newInstance(PrimitiveConcurrentMapMode.class, mode);
                }
            }
        };
    }
}