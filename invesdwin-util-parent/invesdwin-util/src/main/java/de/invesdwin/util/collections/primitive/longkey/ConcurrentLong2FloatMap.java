package de.invesdwin.util.collections.primitive.longkey;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import java.util.function.LongToDoubleFunction;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.iterable.buffer.BufferingIterator;
import de.invesdwin.util.collections.primitive.APrimitiveConcurrentMap;
import de.invesdwin.util.collections.primitive.PrimitiveConcurrentMapConfig;
import de.invesdwin.util.collections.primitive.objkey.striped.IObjectIterator;
import de.invesdwin.util.concurrent.lock.ICloseableLock;
import de.invesdwin.util.math.Floats;
import de.invesdwin.util.math.Longs;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.longs.Long2FloatFunction;
import it.unimi.dsi.fastutil.longs.Long2FloatMap;
import it.unimi.dsi.fastutil.longs.Long2FloatOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;

@ThreadSafe
public class ConcurrentLong2FloatMap extends APrimitiveConcurrentMap<Long, Float> implements Long2FloatMap {
    public static final float DEFAULT_VALUE = Floats.DEFAULT_MISSING_VALUE;

    protected final Long2FloatOpenHashMap[] maps;
    protected final float defaultValue;
    private final ObjectSet<Long2FloatMap.Entry> long2FloatEntrySet;
    private final LongSet keySet;
    private final FloatCollection values;

    public ConcurrentLong2FloatMap() {
        this(PrimitiveConcurrentMapConfig.DEFAULT);
    }

    public ConcurrentLong2FloatMap(final PrimitiveConcurrentMapConfig config) {
        this(config, DEFAULT_VALUE);
    }

    public ConcurrentLong2FloatMap(final PrimitiveConcurrentMapConfig config, final float defaultValue) {
        super(config);
        final int concurrencyLevel = config.getConcurrencyLevel();
        this.maps = new Long2FloatOpenHashMap[concurrencyLevel];
        this.defaultValue = defaultValue;
        final int bucketCapacity = config.getBucketCapacity();
        final float loadFactor = config.getLoadFactor();
        for (int i = 0; i < concurrencyLevel; i++) {
            final Long2FloatOpenHashMap map = new Long2FloatOpenHashMap(bucketCapacity, loadFactor);
            map.defaultReturnValue(defaultValue);
            maps[i] = map;
        }
        this.long2FloatEntrySet = new Long2FloatEntrySet();
        this.keySet = new KeySet();
        this.values = new ValuesCollection();
    }

    @Override
    protected Long2FloatOpenHashMap mapAt(final int index) {
        return maps[index];
    }

    @Deprecated
    @Override
    public boolean containsKey(final Object key) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = readAt(bucket)) {
            return maps[bucket].containsKey(key);
        }
    }

    @Override
    public boolean containsKey(final long key) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = readAt(bucket)) {
            return maps[bucket].containsKey(key);
        }
    }

    @Deprecated
    @Override
    public boolean containsValue(final Object value) {
        for (int i = 0; i < locks.length; i++) {
            try (ICloseableLock lock = readAt(i)) {
                if (mapAt(i).containsValue(value)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean containsValue(final float value) {
        for (int i = 0; i < locks.length; i++) {
            try (ICloseableLock lock = readAt(i)) {
                if (mapAt(i).containsValue(value)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Deprecated
    @Override
    public Float get(final Object key) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = readAt(bucket)) {
            return maps[bucket].get(key);
        }
    }

    @Override
    public float get(final long key) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = readAt(bucket)) {
            return maps[bucket].get(key);
        }
    }

    @Deprecated
    @Override
    public Float getOrDefault(final Object key, final Float defaultValue) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = readAt(bucket)) {
            return maps[bucket].getOrDefault(key, defaultValue);
        }
    }

    @Override
    public float getOrDefault(final long key, final float defaultValue) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = readAt(bucket)) {
            return maps[bucket].getOrDefault(key, defaultValue);
        }
    }

    @Deprecated
    @Override
    public Float put(final Long key, final Float value) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            final Float existing = maps[bucket].put(key, value);
            if (existing == null || existing == defaultValue) {
                size.incrementAndGet();
            }
            return existing;
        }
    }

    @Override
    public float put(final long key, final float value) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            final float existing = maps[bucket].put(key, value);
            if (existing == defaultValue) {
                size.incrementAndGet();
            }
            return existing;
        }
    }

    @Deprecated
    @Override
    public Float remove(final Object key) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            final Float removed = maps[bucket].remove(key);
            if (removed != null && removed != defaultValue) {
                size.decrementAndGet();
            }
            return removed;
        }
    }

    @Override
    public float remove(final long key) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            final float removed = maps[bucket].remove(key);
            if (removed != defaultValue) {
                size.decrementAndGet();
            }
            return removed;
        }
    }

    @Deprecated
    @Override
    public boolean remove(final Object key, final Object value) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            final boolean removed = maps[bucket].remove(key, value);
            if (removed) {
                size.decrementAndGet();
            }
            return removed;
        }
    }

    @Override
    public boolean remove(final long key, final float value) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            final boolean removed = maps[bucket].remove(key, value);
            if (removed) {
                size.decrementAndGet();
            }
            return removed;
        }
    }

    @Deprecated
    @Override
    public void defaultReturnValue(final float rv) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float defaultReturnValue() {
        return defaultValue;
    }

    @Deprecated
    @Override
    public Float computeIfAbsent(final Long key, final Function<? super Long, ? extends Float> mappingFunction) {
        final int bucket = getBucket(key);
        final Long2FloatOpenHashMap m = mapAt(bucket);
        float v;
        try (ICloseableLock lock = readAt(bucket)) {
            v = m.get(key);
        }
        if (v == defaultValue) {
            //bad idea to synchronize in apply, this might cause deadlocks when threads are used inside of it
            final Float newV = mappingFunction.apply(key);
            if (newV == null) {
                v = defaultValue;
            } else {
                v = newV;
            }
            if (v != defaultValue) {
                try (ICloseableLock lock = writeAt(bucket)) {
                    final float oldV = m.get(key);
                    if (oldV != defaultValue) {
                        v = oldV;
                    } else {
                        final Float existing = m.put(key.longValue(), v);
                        if (existing == null || existing == defaultValue) {
                            size.incrementAndGet();
                        }
                    }
                }
            }
        }
        return v;
    }

    @Override
    public float computeIfAbsent(final long key, final Long2FloatFunction mappingFunction) {
        final int bucket = getBucket(key);
        final Long2FloatOpenHashMap m = mapAt(bucket);
        float v;
        try (ICloseableLock lock = readAt(bucket)) {
            v = m.get(key);
        }
        if (v == defaultValue) {
            //bad idea to synchronize in apply, this might cause deadlocks when threads are used inside of it
            v = mappingFunction.apply(key);
            if (v != defaultValue) {
                try (ICloseableLock lock = writeAt(bucket)) {
                    final float oldV = m.get(key);
                    if (oldV != defaultValue) {
                        v = oldV;
                    } else {
                        final float existing = m.put(key, v);
                        if (existing == defaultValue) {
                            size.incrementAndGet();
                        }
                    }
                }
            }
        }
        return v;
    }

    @Override
    public float computeIfAbsent(final long key, final LongToDoubleFunction mappingFunction) {
        final int bucket = getBucket(key);
        final Long2FloatOpenHashMap m = mapAt(bucket);
        float v;
        try (ICloseableLock lock = readAt(bucket)) {
            v = m.get(key);
        }
        if (v == defaultValue) {
            //bad idea to synchronize in apply, this might cause deadlocks when threads are used inside of it
            v = Floats.checkedCast(mappingFunction.applyAsDouble(key));
            if (v != defaultValue) {
                try (ICloseableLock lock = writeAt(bucket)) {
                    final float oldV = m.get(key);
                    if (oldV != defaultValue) {
                        v = oldV;
                    } else {
                        final float existing = m.put(key, v);
                        if (existing == defaultValue) {
                            size.incrementAndGet();
                        }
                    }
                }
            }
        }
        return v;
    }

    @SuppressWarnings("deprecation")
    @Override
    public ObjectSet<Map.Entry<Long, Float>> entrySet() {
        return Long2FloatMap.super.entrySet();
    }

    @Override
    public ObjectSet<Long2FloatMap.Entry> long2FloatEntrySet() {
        return long2FloatEntrySet;
    }

    @Override
    public LongSet keySet() {
        return keySet;
    }

    @Override
    public FloatCollection values() {
        return values;
    }

    private final class ValuesCollection extends AbstractCollection<Float> implements FloatCollection {
        private final FloatCollection[] delegates;

        private ValuesCollection() {
            this.delegates = new FloatCollection[maps.length];
            for (int i = 0; i < maps.length; i++) {
                delegates[i] = maps[i].values();
            }
        }

        @Override
        public boolean add(final float key) {
            throw newUnmodifiableException();
        }

        @Override
        public boolean add(final Float k) {
            throw newUnmodifiableException();
        }

        @Override
        public boolean rem(final float key) {
            throw newUnmodifiableException();
        }

        @Override
        public boolean remove(final Object k) {
            throw newUnmodifiableException();
        }

        @Override
        public int size() {
            return ConcurrentLong2FloatMap.this.size();
        }

        @Override
        public boolean isEmpty() {
            return ConcurrentLong2FloatMap.this.isEmpty();
        }

        @Override
        public boolean contains(final Object o) {
            return containsValue(o);
        }

        @Override
        public boolean contains(final float value) {
            return containsValue(value);
        }

        @Override
        public FloatIterator iterator() {
            final Iterator<Long2FloatMap.Entry> it = long2FloatEntrySet.iterator();
            return new FloatIterator() {
                @Override
                public boolean hasNext() {
                    return it.hasNext();
                }

                @Deprecated
                @Override
                public Float next() {
                    return it.next().getValue();
                }

                @Override
                public float nextFloat() {
                    return it.next().getFloatValue();
                }

                @Override
                public void remove() {
                    it.remove();
                }

                @Override
                public String toString() {
                    return it.toString();
                }
            };
        }

        @Override
        public void clear() {
            throw newUnmodifiableException();
        }

        @Override
        public boolean addAll(final Collection<? extends Float> c) {
            throw newUnmodifiableException();
        }

        @Override
        public boolean removeAll(final Collection<?> c) {
            throw newUnmodifiableException();
        }

        @Override
        public boolean retainAll(final Collection<?> c) {
            throw newUnmodifiableException();
        }

        @Override
        public float[] toFloatArray() {
            return Floats.checkedCastVector(toArray(Floats.EMPTY_ARRAY_OBJ));
        }

        @Override
        public float[] toArray(final float[] a) {
            if (a.length != 0) {
                throw new IllegalArgumentException("a.length needs to be 0, but " + a.length);
            }
            return toFloatArray();
        }

        @Override
        public boolean addAll(final FloatCollection c) {
            throw newUnmodifiableException();
        }

        @Override
        public boolean containsAll(final FloatCollection c) {
            final FloatIterator it = c.iterator();
            while (it.hasNext()) {
                final float e = it.nextFloat();
                if (!contains(e)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean removeAll(final FloatCollection c) {
            throw newUnmodifiableException();
        }

        @Override
        public boolean retainAll(final FloatCollection c) {
            throw newUnmodifiableException();
        }

    }

    private final class KeySet extends AbstractSet<Long> implements LongSet {

        private final LongSet[] delegates;

        private KeySet() {
            this.delegates = new LongSet[maps.length];
            for (int i = 0; i < maps.length; i++) {
                delegates[i] = maps[i].keySet();
            }
        }

        @Override
        public int size() {
            return ConcurrentLong2FloatMap.this.size();
        }

        @Override
        public boolean isEmpty() {
            return ConcurrentLong2FloatMap.this.isEmpty();
        }

        @Deprecated
        @Override
        public boolean contains(final Object o) {
            return containsKey(o);
        }

        @Override
        public boolean contains(final long key) {
            return containsKey(key);
        }

        @Override
        public LongIterator iterator() {
            final Iterator<Long2FloatMap.Entry> it = long2FloatEntrySet.iterator();
            return new LongIterator() {
                @Override
                public boolean hasNext() {
                    return it.hasNext();
                }

                @Deprecated
                @Override
                public Long next() {
                    return it.next().getKey();
                }

                @Override
                public long nextLong() {
                    return it.next().getLongKey();
                }

                @Override
                public void remove() {
                    it.remove();
                }

                @Override
                public String toString() {
                    return it.toString();
                }

            };
        }

        @Override
        public boolean add(final Long e) {
            throw newUnmodifiableException();
        }

        @Override
        public boolean add(final long key) {
            throw newUnmodifiableException();
        }

        @Override
        public boolean remove(final Object o) {
            throw newUnmodifiableException();
        }

        @Override
        public boolean remove(final long k) {
            throw newUnmodifiableException();
        }

        @Override
        public boolean addAll(final Collection<? extends Long> c) {
            throw newUnmodifiableException();
        }

        @Override
        public boolean retainAll(final Collection<?> c) {
            throw newUnmodifiableException();
        }

        @Override
        public boolean removeAll(final Collection<?> c) {
            throw newUnmodifiableException();
        }

        @Override
        public void clear() {
            throw newUnmodifiableException();
        }

        @Override
        public long[] toLongArray() {
            return Longs.checkedCastVector(toArray(Longs.EMPTY_ARRAY_OBJ));
        }

        @Override
        public long[] toArray(final long[] a) {
            if (a.length != 0) {
                throw new IllegalArgumentException("a.length needs to be 0, but " + a.length);
            }
            return toLongArray();
        }

        @Override
        public boolean addAll(final LongCollection c) {
            throw newUnmodifiableException();
        }

        @Override
        public boolean containsAll(final LongCollection c) {
            final LongIterator it = c.iterator();
            while (it.hasNext()) {
                final long e = it.nextLong();
                if (!contains(e)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean removeAll(final LongCollection c) {
            throw newUnmodifiableException();
        }

        @Override
        public boolean retainAll(final LongCollection c) {
            throw newUnmodifiableException();
        }

    }

    private final class Long2FloatEntrySet extends AbstractSet<Long2FloatMap.Entry>
            implements ObjectSet<Long2FloatMap.Entry> {

        private final ObjectSet<Long2FloatMap.Entry>[] delegates;

        @SuppressWarnings("unchecked")
        private Long2FloatEntrySet() {
            this.delegates = new ObjectSet[maps.length];
            for (int i = 0; i < maps.length; i++) {
                delegates[i] = maps[i].long2FloatEntrySet();
            }
        }

        @Override
        public int size() {
            return ConcurrentLong2FloatMap.this.size();
        }

        @Override
        public boolean isEmpty() {
            return ConcurrentLong2FloatMap.this.isEmpty();
        }

        @Override
        public boolean contains(final Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry<?, ?> entry = (Map.Entry<?, ?>) o;
            final int bucket = getBucket(entry.getKey());
            try (ICloseableLock lock = readAt(bucket)) {
                if (delegates[bucket].contains(entry)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public ObjectIterator<Long2FloatMap.Entry> iterator() {
            if (isEmpty()) {
                return it.unimi.dsi.fastutil.objects.ObjectIterators.emptyIterator();
            }
            return new IObjectIterator<Long2FloatMap.Entry>() {
                private int bucketIndex = -1;
                private Iterator<Long2FloatMap.Entry> currentIterator = it.unimi.dsi.fastutil.objects.ObjectIterators
                        .emptyIterator();
                private long seenKey;

                @Override
                public boolean hasNext() {
                    while (!currentIterator.hasNext()) {
                        bucketIndex++;
                        if (bucketIndex >= delegates.length) {
                            return false;
                        }
                        currentIterator = newIterator(bucketIndex);
                    }
                    return true;
                }

                private Iterator<Long2FloatMap.Entry> newIterator(final int bucket) {
                    try (ICloseableLock lock = readAt(bucket)) {
                        return new BufferingIterator<Long2FloatMap.Entry>(delegates[bucket]);
                    }
                }

                @Override
                public Long2FloatMap.Entry next() {
                    if (!hasNext()) {
                        throw new IllegalStateException("No more elements");
                    }
                    final Long2FloatMap.Entry next = currentIterator.next();
                    seenKey = next.getLongKey();
                    return next;
                }

                @Override
                public void remove() {
                    ConcurrentLong2FloatMap.this.remove(seenKey);
                }

                @Override
                public String toString() {
                    return bucketIndex + ": " + currentIterator;
                }
            };
        }

        @Override
        public boolean add(final Long2FloatMap.Entry e) {
            throw newUnmodifiableException();
        }

        @Override
        public boolean remove(final Object o) {
            throw newUnmodifiableException();
        }

        @Override
        public boolean addAll(final Collection<? extends Long2FloatMap.Entry> c) {
            throw newUnmodifiableException();
        }

        @Override
        public boolean retainAll(final Collection<?> c) {
            throw newUnmodifiableException();
        }

        @Override
        public boolean removeAll(final Collection<?> c) {
            throw newUnmodifiableException();
        }

        @Override
        public void clear() {
            throw newUnmodifiableException();
        }

    }

}