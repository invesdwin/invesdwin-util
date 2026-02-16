package de.invesdwin.util.collections.primitive.intkey;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import java.util.function.IntToDoubleFunction;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.iterable.buffer.BufferingIterator;
import de.invesdwin.util.collections.primitive.APrimitiveConcurrentMap;
import de.invesdwin.util.collections.primitive.PrimitiveConcurrentMapConfig;
import de.invesdwin.util.collections.primitive.objkey.striped.IObjectIterator;
import de.invesdwin.util.concurrent.lock.ICloseableLock;
import de.invesdwin.util.math.Floats;
import de.invesdwin.util.math.Integers;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.ints.Int2FloatFunction;
import it.unimi.dsi.fastutil.ints.Int2FloatMap;
import it.unimi.dsi.fastutil.ints.Int2FloatOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;

@ThreadSafe
public class ConcurrentInt2FloatMap extends APrimitiveConcurrentMap<Integer, Float> implements Int2FloatMap {
    public static final float DEFAULT_VALUE = Floats.DEFAULT_MISSING_VALUE;

    protected final Int2FloatOpenHashMap[] maps;
    protected final float defaultValue;
    private final ObjectSet<Int2FloatMap.Entry> int2FloatEntrySet;
    private final IntSet keySet;
    private final FloatCollection values;

    public ConcurrentInt2FloatMap() {
        this(PrimitiveConcurrentMapConfig.DEFAULT);
    }

    public ConcurrentInt2FloatMap(final PrimitiveConcurrentMapConfig config) {
        this(config, DEFAULT_VALUE);
    }

    public ConcurrentInt2FloatMap(final PrimitiveConcurrentMapConfig config, final float defaultValue) {
        super(config);
        final int concurrencyLevel = config.getConcurrencyLevel();
        this.maps = new Int2FloatOpenHashMap[concurrencyLevel];
        this.defaultValue = defaultValue;
        final int bucketCapacity = config.getBucketCapacity();
        final float loadFactor = config.getLoadFactor();
        for (int i = 0; i < concurrencyLevel; i++) {
            final Int2FloatOpenHashMap map = new Int2FloatOpenHashMap(bucketCapacity, loadFactor);
            map.defaultReturnValue(defaultValue);
            maps[i] = map;
        }
        this.int2FloatEntrySet = new Int2FloatEntrySet();
        this.keySet = new KeySet();
        this.values = new ValuesCollection();
    }

    @Override
    protected Int2FloatOpenHashMap mapAt(final int index) {
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
    public boolean containsKey(final int key) {
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
    public float get(final int key) {
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
    public float getOrDefault(final int key, final float defaultValue) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = readAt(bucket)) {
            return maps[bucket].getOrDefault(key, defaultValue);
        }
    }

    @Deprecated
    @Override
    public Float put(final Integer key, final Float value) {
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
    public float put(final int key, final float value) {
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
    public float remove(final int key) {
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
    public boolean remove(final int key, final float value) {
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
    public Float computeIfAbsent(final Integer key, final Function<? super Integer, ? extends Float> mappingFunction) {
        final int bucket = getBucket(key);
        final Int2FloatOpenHashMap m = mapAt(bucket);
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
                        final Float existing = m.put(key.intValue(), v);
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
    public float computeIfAbsent(final int key, final Int2FloatFunction mappingFunction) {
        final int bucket = getBucket(key);
        final Int2FloatOpenHashMap m = mapAt(bucket);
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
    public float computeIfAbsent(final int key, final IntToDoubleFunction mappingFunction) {
        final int bucket = getBucket(key);
        final Int2FloatOpenHashMap m = mapAt(bucket);
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
    public ObjectSet<Map.Entry<Integer, Float>> entrySet() {
        return Int2FloatMap.super.entrySet();
    }

    @Override
    public ObjectSet<Int2FloatMap.Entry> int2FloatEntrySet() {
        return int2FloatEntrySet;
    }

    @Override
    public IntSet keySet() {
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
            return ConcurrentInt2FloatMap.this.size();
        }

        @Override
        public boolean isEmpty() {
            return ConcurrentInt2FloatMap.this.isEmpty();
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
            final Iterator<Int2FloatMap.Entry> it = int2FloatEntrySet.iterator();
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

    private final class KeySet extends AbstractSet<Integer> implements IntSet {

        private final IntSet[] delegates;

        private KeySet() {
            this.delegates = new IntSet[maps.length];
            for (int i = 0; i < maps.length; i++) {
                delegates[i] = maps[i].keySet();
            }
        }

        @Override
        public int size() {
            return ConcurrentInt2FloatMap.this.size();
        }

        @Override
        public boolean isEmpty() {
            return ConcurrentInt2FloatMap.this.isEmpty();
        }

        @Deprecated
        @Override
        public boolean contains(final Object o) {
            return containsKey(o);
        }

        @Override
        public boolean contains(final int key) {
            return containsKey(key);
        }

        @Override
        public IntIterator iterator() {
            final Iterator<Int2FloatMap.Entry> it = int2FloatEntrySet.iterator();
            return new IntIterator() {
                @Override
                public boolean hasNext() {
                    return it.hasNext();
                }

                @Deprecated
                @Override
                public Integer next() {
                    return it.next().getKey();
                }

                @Override
                public int nextInt() {
                    return it.next().getIntKey();
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
        public boolean add(final Integer e) {
            throw newUnmodifiableException();
        }

        @Override
        public boolean add(final int key) {
            throw newUnmodifiableException();
        }

        @Override
        public boolean remove(final Object o) {
            throw newUnmodifiableException();
        }

        @Override
        public boolean remove(final int k) {
            throw newUnmodifiableException();
        }

        @Override
        public boolean addAll(final Collection<? extends Integer> c) {
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
        public int[] toIntArray() {
            return Integers.checkedCastVector(toArray(Integers.EMPTY_ARRAY_OBJ));
        }

        @Override
        public int[] toArray(final int[] a) {
            if (a.length != 0) {
                throw new IllegalArgumentException("a.length needs to be 0, but " + a.length);
            }
            return toIntArray();
        }

        @Override
        public boolean addAll(final IntCollection c) {
            throw newUnmodifiableException();
        }

        @Override
        public boolean containsAll(final IntCollection c) {
            final IntIterator it = c.iterator();
            while (it.hasNext()) {
                final long e = it.nextInt();
                if (!contains(e)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean removeAll(final IntCollection c) {
            throw newUnmodifiableException();
        }

        @Override
        public boolean retainAll(final IntCollection c) {
            throw newUnmodifiableException();
        }

    }

    private final class Int2FloatEntrySet extends AbstractSet<Int2FloatMap.Entry>
            implements ObjectSet<Int2FloatMap.Entry> {

        private final ObjectSet<Int2FloatMap.Entry>[] delegates;

        @SuppressWarnings("unchecked")
        private Int2FloatEntrySet() {
            this.delegates = new ObjectSet[maps.length];
            for (int i = 0; i < maps.length; i++) {
                delegates[i] = maps[i].int2FloatEntrySet();
            }
        }

        @Override
        public int size() {
            return ConcurrentInt2FloatMap.this.size();
        }

        @Override
        public boolean isEmpty() {
            return ConcurrentInt2FloatMap.this.isEmpty();
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
        public ObjectIterator<Int2FloatMap.Entry> iterator() {
            if (isEmpty()) {
                return it.unimi.dsi.fastutil.objects.ObjectIterators.emptyIterator();
            }
            return new IObjectIterator<Int2FloatMap.Entry>() {
                private int bucketIndex = -1;
                private Iterator<Int2FloatMap.Entry> currentIterator = it.unimi.dsi.fastutil.objects.ObjectIterators
                        .emptyIterator();
                private Int2FloatMap.Entry seenEntry;

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

                private Iterator<Int2FloatMap.Entry> newIterator(final int bucket) {
                    try (ICloseableLock lock = readAt(bucket)) {
                        return new BufferingIterator<Int2FloatMap.Entry>(delegates[bucket]);
                    }
                }

                @Override
                public Int2FloatMap.Entry next() {
                    if (!hasNext()) {
                        throw new IllegalStateException("No more elements");
                    }
                    seenEntry = currentIterator.next();
                    return seenEntry;
                }

                @Override
                public void remove() {
                    ConcurrentInt2FloatMap.this.remove(seenEntry.getIntKey());
                }

                @Override
                public String toString() {
                    return bucketIndex + ": " + currentIterator;
                }
            };
        }

        @Override
        public boolean add(final Int2FloatMap.Entry e) {
            throw newUnmodifiableException();
        }

        @Override
        public boolean remove(final Object o) {
            throw newUnmodifiableException();
        }

        @Override
        public boolean addAll(final Collection<? extends Int2FloatMap.Entry> c) {
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