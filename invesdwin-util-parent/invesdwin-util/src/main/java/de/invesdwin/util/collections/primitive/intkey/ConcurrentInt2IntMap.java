package de.invesdwin.util.collections.primitive.intkey;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.IntUnaryOperator;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.iterable.buffer.NodeBufferingIterator;
import de.invesdwin.util.collections.primitive.APrimitiveConcurrentMap;
import de.invesdwin.util.collections.primitive.PrimitiveConcurrentMapConfig;
import de.invesdwin.util.collections.primitive.intkey.entry.NodeImmutableInt2IntEntry;
import de.invesdwin.util.collections.primitive.objkey.striped.IObjectIterator;
import de.invesdwin.util.concurrent.lock.ICloseableLock;
import de.invesdwin.util.math.Integers;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;

@ThreadSafe
public class ConcurrentInt2IntMap extends APrimitiveConcurrentMap<Integer, Integer> implements Int2IntMap {
    public static final int DEFAULT_VALUE = Integers.DEFAULT_MISSING_VALUE;

    protected final Int2IntOpenHashMap[] maps;
    protected final int defaultValue;
    private final ObjectSet<Int2IntMap.Entry> int2IntEntrySet;
    private final IntSet keySet;
    private final IntCollection values;

    public ConcurrentInt2IntMap() {
        this(PrimitiveConcurrentMapConfig.DEFAULT);
    }

    public ConcurrentInt2IntMap(final PrimitiveConcurrentMapConfig config) {
        this(config, DEFAULT_VALUE);
    }

    public ConcurrentInt2IntMap(final PrimitiveConcurrentMapConfig config, final int defaultValue) {
        super(config);
        final int concurrencyLevel = config.getConcurrencyLevel();
        this.maps = new Int2IntOpenHashMap[concurrencyLevel];
        this.defaultValue = defaultValue;
        final int bucketCapacity = config.getBucketCapacity();
        final float loadFactor = config.getLoadFactor();
        for (int i = 0; i < concurrencyLevel; i++) {
            final Int2IntOpenHashMap map = new Int2IntOpenHashMap(bucketCapacity, loadFactor);
            map.defaultReturnValue(defaultValue);
            maps[i] = map;
        }
        this.int2IntEntrySet = new Int2IntEntrySet();
        this.keySet = new KeySet();
        this.values = new ValuesCollection();
    }

    @Override
    protected Int2IntOpenHashMap mapAt(final int index) {
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
    public boolean containsValue(final int value) {
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
    public Integer get(final Object key) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = readAt(bucket)) {
            return maps[bucket].get(key);
        }
    }

    @Override
    public int get(final int key) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = readAt(bucket)) {
            return maps[bucket].get(key);
        }
    }

    @Deprecated
    @Override
    public Integer getOrDefault(final Object key, final Integer defaultValue) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = readAt(bucket)) {
            return maps[bucket].getOrDefault(key, defaultValue);
        }
    }

    @Override
    public int getOrDefault(final int key, final int defaultValue) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = readAt(bucket)) {
            return maps[bucket].getOrDefault(key, defaultValue);
        }
    }

    @Deprecated
    @Override
    public Integer put(final Integer key, final Integer value) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            final Integer existing = maps[bucket].put(key, value);
            if (existing == null || existing == defaultValue) {
                size.incrementAndGet();
            }
            return existing;
        }
    }

    @Override
    public int put(final int key, final int value) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            final int existing = maps[bucket].put(key, value);
            if (existing == defaultValue) {
                size.incrementAndGet();
            }
            return existing;
        }
    }

    @Deprecated
    @Override
    public Integer remove(final Object key) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            final Integer removed = maps[bucket].remove(key);
            if (removed != null && removed != defaultValue) {
                size.decrementAndGet();
            }
            return removed;
        }
    }

    @Override
    public int remove(final int key) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            final int removed = maps[bucket].remove(key);
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
    public boolean remove(final int key, final int value) {
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
    public void defaultReturnValue(final int rv) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int defaultReturnValue() {
        return defaultValue;
    }

    @Deprecated
    @Override
    public Integer computeIfAbsent(final Integer key,
            final Function<? super Integer, ? extends Integer> mappingFunction) {
        final int bucket = getBucket(key);
        final Int2IntOpenHashMap m = mapAt(bucket);
        int v;
        try (ICloseableLock lock = readAt(bucket)) {
            v = m.get(key);
        }
        if (v == defaultValue) {
            //bad idea to synchronize in apply, this might cause deadlocks when threads are used inside of it
            final Integer newV = mappingFunction.apply(key);
            if (newV == null) {
                v = defaultValue;
            } else {
                v = newV;
            }
            if (v != defaultValue) {
                try (ICloseableLock lock = writeAt(bucket)) {
                    final int oldV = m.get(key);
                    if (oldV != defaultValue) {
                        v = oldV;
                    } else {
                        final Integer existing = m.put(key.intValue(), v);
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
    public int computeIfAbsent(final int key, final Int2IntFunction mappingFunction) {
        final int bucket = getBucket(key);
        final Int2IntOpenHashMap m = mapAt(bucket);
        int v;
        try (ICloseableLock lock = readAt(bucket)) {
            v = m.get(key);
        }
        if (v == defaultValue) {
            //bad idea to synchronize in apply, this might cause deadlocks when threads are used inside of it
            v = mappingFunction.applyAsInt(key);
            if (v != defaultValue) {
                try (ICloseableLock lock = writeAt(bucket)) {
                    final int oldV = m.get(key);
                    if (oldV != defaultValue) {
                        v = oldV;
                    } else {
                        final int existing = m.put(key, v);
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
    public int computeIfAbsent(final int key, final IntUnaryOperator mappingFunction) {
        final int bucket = getBucket(key);
        final Int2IntOpenHashMap m = mapAt(bucket);
        int v;
        try (ICloseableLock lock = readAt(bucket)) {
            v = m.get(key);
        }
        if (v == defaultValue) {
            //bad idea to synchronize in apply, this might cause deadlocks when threads are used inside of it
            v = mappingFunction.applyAsInt(key);
            if (v != defaultValue) {
                try (ICloseableLock lock = writeAt(bucket)) {
                    final int oldV = m.get(key);
                    if (oldV != defaultValue) {
                        v = oldV;
                    } else {
                        final int existing = m.put(key, v);
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
    public ObjectSet<Map.Entry<Integer, Integer>> entrySet() {
        return Int2IntMap.super.entrySet();
    }

    @Override
    public ObjectSet<Int2IntMap.Entry> int2IntEntrySet() {
        return int2IntEntrySet;
    }

    @Override
    public IntSet keySet() {
        return keySet;
    }

    @Override
    public IntCollection values() {
        return values;
    }

    private final class ValuesCollection extends AbstractCollection<Integer> implements IntCollection {
        private final IntCollection[] delegates;

        private ValuesCollection() {
            this.delegates = new IntCollection[maps.length];
            for (int i = 0; i < maps.length; i++) {
                delegates[i] = maps[i].values();
            }
        }

        @Override
        public boolean add(final int key) {
            throw newUnmodifiableException();
        }

        @Override
        public boolean add(final Integer k) {
            throw newUnmodifiableException();
        }

        @Override
        public boolean rem(final int key) {
            throw newUnmodifiableException();
        }

        @Override
        public boolean remove(final Object k) {
            throw newUnmodifiableException();
        }

        @Override
        public int size() {
            return ConcurrentInt2IntMap.this.size();
        }

        @Override
        public boolean isEmpty() {
            return ConcurrentInt2IntMap.this.isEmpty();
        }

        @Override
        public boolean contains(final Object o) {
            return containsValue(o);
        }

        @Override
        public boolean contains(final int value) {
            return containsValue(value);
        }

        @Override
        public IntIterator iterator() {
            final Iterator<Int2IntMap.Entry> it = int2IntEntrySet.iterator();
            return new IntIterator() {
                @Override
                public boolean hasNext() {
                    return it.hasNext();
                }

                @Deprecated
                @Override
                public Integer next() {
                    return it.next().getValue();
                }

                @Override
                public int nextInt() {
                    return it.next().getIntValue();
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
        public boolean addAll(final Collection<? extends Integer> c) {
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
                final int e = it.nextInt();
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
            return ConcurrentInt2IntMap.this.size();
        }

        @Override
        public boolean isEmpty() {
            return ConcurrentInt2IntMap.this.isEmpty();
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
            final Iterator<Int2IntMap.Entry> it = int2IntEntrySet.iterator();
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

    private final class Int2IntEntrySet extends AbstractSet<Int2IntMap.Entry> implements ObjectSet<Int2IntMap.Entry> {

        private final ObjectSet<Int2IntMap.Entry>[] delegates;

        @SuppressWarnings("unchecked")
        private Int2IntEntrySet() {
            this.delegates = new ObjectSet[maps.length];
            for (int i = 0; i < maps.length; i++) {
                delegates[i] = maps[i].int2IntEntrySet();
            }
        }

        @Override
        public int size() {
            return ConcurrentInt2IntMap.this.size();
        }

        @Override
        public boolean isEmpty() {
            return ConcurrentInt2IntMap.this.isEmpty();
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
        public ObjectIterator<Int2IntMap.Entry> iterator() {
            if (isEmpty()) {
                return it.unimi.dsi.fastutil.objects.ObjectIterators.emptyIterator();
            }
            return new IObjectIterator<Int2IntMap.Entry>() {
                private int bucketIndex = -1;
                private Iterator<? extends Int2IntMap.Entry> currentIterator = it.unimi.dsi.fastutil.objects.ObjectIterators
                        .emptyIterator();
                private int seenKey;

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

                private Iterator<? extends Int2IntMap.Entry> newIterator(final int bucket) {
                    try (ICloseableLock lock = readAt(bucket)) {
                        final NodeBufferingIterator<NodeImmutableInt2IntEntry> buffer = new NodeBufferingIterator<NodeImmutableInt2IntEntry>();
                        final ObjectIterator<Int2IntMap.Entry> it = delegates[bucket].iterator();
                        try {
                            while (true) {
                                final Int2IntMap.Entry next = it.next();
                                buffer.add(NodeImmutableInt2IntEntry.of(next.getIntKey(), next.getIntValue()));
                            }
                        } catch (final NoSuchElementException e) {
                            //end reached
                        }
                        return buffer;
                    }
                }

                @Override
                public Int2IntMap.Entry next() {
                    if (!hasNext()) {
                        throw new IllegalStateException("No more elements");
                    }
                    final Int2IntMap.Entry next = currentIterator.next();
                    seenKey = next.getIntKey();
                    return next;
                }

                @Override
                public void remove() {
                    ConcurrentInt2IntMap.this.remove(seenKey);
                }

                @Override
                public String toString() {
                    return bucketIndex + ": " + currentIterator;
                }
            };
        }

        @Override
        public boolean add(final Int2IntMap.Entry e) {
            throw newUnmodifiableException();
        }

        @Override
        public boolean remove(final Object o) {
            throw newUnmodifiableException();
        }

        @Override
        public boolean addAll(final Collection<? extends Int2IntMap.Entry> c) {
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