package de.invesdwin.util.collections.primitive.intkey;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.IntToLongFunction;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.iterable.buffer.NodeBufferingIterator;
import de.invesdwin.util.collections.primitive.APrimitiveConcurrentMap;
import de.invesdwin.util.collections.primitive.PrimitiveConcurrentMapConfig;
import de.invesdwin.util.collections.primitive.intkey.entry.NodeImmutableInt2LongEntry;
import de.invesdwin.util.collections.primitive.objkey.striped.IObjectIterator;
import de.invesdwin.util.concurrent.lock.ICloseableLock;
import de.invesdwin.util.error.FastNoSuchElementException;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.Longs;
import it.unimi.dsi.fastutil.ints.Int2LongFunction;
import it.unimi.dsi.fastutil.ints.Int2LongMap;
import it.unimi.dsi.fastutil.ints.Int2LongOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;

@ThreadSafe
public class ConcurrentInt2LongMap extends APrimitiveConcurrentMap<Integer, Long> implements Int2LongMap {
    public static final long DEFAULT_VALUE = Longs.DEFAULT_MISSING_VALUE;

    protected final Int2LongOpenHashMap[] maps;
    protected final long defaultValue;
    private final ObjectSet<Int2LongMap.Entry> int2LongEntrySet;
    private final IntSet keySet;
    private final LongCollection values;

    public ConcurrentInt2LongMap() {
        this(PrimitiveConcurrentMapConfig.DEFAULT);
    }

    public ConcurrentInt2LongMap(final PrimitiveConcurrentMapConfig config) {
        this(config, DEFAULT_VALUE);
    }

    public ConcurrentInt2LongMap(final PrimitiveConcurrentMapConfig config, final long defaultValue) {
        super(config);
        final int concurrencyLevel = config.getConcurrencyLevel();
        this.maps = new Int2LongOpenHashMap[concurrencyLevel];
        this.defaultValue = defaultValue;
        final int bucketCapacity = config.getBucketCapacity();
        final float loadFactor = config.getLoadFactor();
        for (int i = 0; i < concurrencyLevel; i++) {
            final Int2LongOpenHashMap map = new Int2LongOpenHashMap(bucketCapacity, loadFactor);
            map.defaultReturnValue(defaultValue);
            maps[i] = map;
        }
        this.int2LongEntrySet = new Int2LongEntrySet();
        this.keySet = new KeySet();
        this.values = new ValuesCollection();
    }

    @Override
    protected Int2LongOpenHashMap mapAt(final int index) {
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
    public boolean containsValue(final long value) {
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
    public Long get(final Object key) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = readAt(bucket)) {
            return maps[bucket].get(key);
        }
    }

    @Override
    public long get(final int key) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = readAt(bucket)) {
            return maps[bucket].get(key);
        }
    }

    @Deprecated
    @Override
    public Long getOrDefault(final Object key, final Long defaultValue) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = readAt(bucket)) {
            return maps[bucket].getOrDefault(key, defaultValue);
        }
    }

    @Override
    public long getOrDefault(final int key, final long defaultValue) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = readAt(bucket)) {
            return maps[bucket].getOrDefault(key, defaultValue);
        }
    }

    @Deprecated
    @Override
    public Long put(final Integer key, final Long value) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            final Long existing = maps[bucket].put(key, value);
            if (existing == null || existing == defaultValue) {
                size.incrementAndGet();
            }
            return existing;
        }
    }

    @Override
    public long put(final int key, final long value) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            final long existing = maps[bucket].put(key, value);
            if (existing == defaultValue) {
                size.incrementAndGet();
            }
            return existing;
        }
    }

    @Deprecated
    @Override
    public Long remove(final Object key) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            final Long removed = maps[bucket].remove(key);
            if (removed != null && removed != defaultValue) {
                size.decrementAndGet();
            }
            return removed;
        }
    }

    @Override
    public long remove(final int key) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            final long removed = maps[bucket].remove(key);
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
    public boolean remove(final int key, final long value) {
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
    public void defaultReturnValue(final long rv) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long defaultReturnValue() {
        return defaultValue;
    }

    @Deprecated
    @Override
    public Long computeIfAbsent(final Integer key, final Function<? super Integer, ? extends Long> mappingFunction) {
        final int bucket = getBucket(key);
        final Int2LongOpenHashMap m = mapAt(bucket);
        long v;
        try (ICloseableLock lock = readAt(bucket)) {
            v = m.get(key);
        }
        if (v == defaultValue) {
            //bad idea to synchronize in apply, this might cause deadlocks when threads are used inside of it
            final Long newV = mappingFunction.apply(key);
            if (newV == null) {
                v = defaultValue;
            } else {
                v = newV;
            }
            if (v != defaultValue) {
                try (ICloseableLock lock = writeAt(bucket)) {
                    final long oldV = m.get(key);
                    if (oldV != defaultValue) {
                        v = oldV;
                    } else {
                        final Long existing = m.put(key.intValue(), v);
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
    public long computeIfAbsent(final int key, final Int2LongFunction mappingFunction) {
        final int bucket = getBucket(key);
        final Int2LongOpenHashMap m = mapAt(bucket);
        long v;
        try (ICloseableLock lock = readAt(bucket)) {
            v = m.get(key);
        }
        if (v == defaultValue) {
            //bad idea to synchronize in apply, this might cause deadlocks when threads are used inside of it
            v = mappingFunction.applyAsLong(key);
            if (v != defaultValue) {
                try (ICloseableLock lock = writeAt(bucket)) {
                    final long oldV = m.get(key);
                    if (oldV != defaultValue) {
                        v = oldV;
                    } else {
                        final long existing = m.put(key, v);
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
    public long computeIfAbsent(final int key, final IntToLongFunction mappingFunction) {
        final int bucket = getBucket(key);
        final Int2LongOpenHashMap m = mapAt(bucket);
        long v;
        try (ICloseableLock lock = readAt(bucket)) {
            v = m.get(key);
        }
        if (v == defaultValue) {
            //bad idea to synchronize in apply, this might cause deadlocks when threads are used inside of it
            v = mappingFunction.applyAsLong(key);
            if (v != defaultValue) {
                try (ICloseableLock lock = writeAt(bucket)) {
                    final long oldV = m.get(key);
                    if (oldV != defaultValue) {
                        v = oldV;
                    } else {
                        final long existing = m.put(key, v);
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
    public ObjectSet<Map.Entry<Integer, Long>> entrySet() {
        return Int2LongMap.super.entrySet();
    }

    @Override
    public ObjectSet<Int2LongMap.Entry> int2LongEntrySet() {
        return int2LongEntrySet;
    }

    @Override
    public IntSet keySet() {
        return keySet;
    }

    @Override
    public LongCollection values() {
        return values;
    }

    private final class ValuesCollection extends AbstractCollection<Long> implements LongCollection {
        private final LongCollection[] delegates;

        private ValuesCollection() {
            this.delegates = new LongCollection[maps.length];
            for (int i = 0; i < maps.length; i++) {
                delegates[i] = maps[i].values();
            }
        }

        @Override
        public boolean add(final long key) {
            throw newUnmodifiableException();
        }

        @Override
        public boolean add(final Long k) {
            throw newUnmodifiableException();
        }

        @Override
        public boolean rem(final long key) {
            throw newUnmodifiableException();
        }

        @Override
        public boolean remove(final Object k) {
            throw newUnmodifiableException();
        }

        @Override
        public int size() {
            return ConcurrentInt2LongMap.this.size();
        }

        @Override
        public boolean isEmpty() {
            return ConcurrentInt2LongMap.this.isEmpty();
        }

        @Override
        public boolean contains(final Object o) {
            return containsValue(o);
        }

        @Override
        public boolean contains(final long value) {
            return containsValue(value);
        }

        @Override
        public LongIterator iterator() {
            final Iterator<Int2LongMap.Entry> it = int2LongEntrySet.iterator();
            return new LongIterator() {
                @Override
                public boolean hasNext() {
                    return it.hasNext();
                }

                @Deprecated
                @Override
                public Long next() {
                    return it.next().getValue();
                }

                @Override
                public long nextLong() {
                    return it.next().getLongValue();
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
        public boolean addAll(final Collection<? extends Long> c) {
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
            return ConcurrentInt2LongMap.this.size();
        }

        @Override
        public boolean isEmpty() {
            return ConcurrentInt2LongMap.this.isEmpty();
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
            final Iterator<Int2LongMap.Entry> it = int2LongEntrySet.iterator();
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

    private final class Int2LongEntrySet extends AbstractSet<Int2LongMap.Entry>
            implements ObjectSet<Int2LongMap.Entry> {

        private final ObjectSet<Int2LongMap.Entry>[] delegates;

        @SuppressWarnings("unchecked")
        private Int2LongEntrySet() {
            this.delegates = new ObjectSet[maps.length];
            for (int i = 0; i < maps.length; i++) {
                delegates[i] = maps[i].int2LongEntrySet();
            }
        }

        @Override
        public int size() {
            return ConcurrentInt2LongMap.this.size();
        }

        @Override
        public boolean isEmpty() {
            return ConcurrentInt2LongMap.this.isEmpty();
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
        public ObjectIterator<Int2LongMap.Entry> iterator() {
            if (isEmpty()) {
                return it.unimi.dsi.fastutil.objects.ObjectIterators.emptyIterator();
            }
            return new IObjectIterator<Int2LongMap.Entry>() {
                private int bucketIndex = -1;
                private Iterator<? extends Int2LongMap.Entry> currentIterator = it.unimi.dsi.fastutil.objects.ObjectIterators
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

                private Iterator<? extends Int2LongMap.Entry> newIterator(final int bucket) {
                    try (ICloseableLock lock = readAt(bucket)) {
                        final NodeBufferingIterator<NodeImmutableInt2LongEntry> buffer = new NodeBufferingIterator<NodeImmutableInt2LongEntry>();
                        final ObjectIterator<Int2LongMap.Entry> it = delegates[bucket].iterator();
                        try {
                            while (true) {
                                final Int2LongMap.Entry next = it.next();
                                buffer.add(NodeImmutableInt2LongEntry.of(next.getIntKey(), next.getLongValue()));
                            }
                        } catch (final NoSuchElementException e) {
                            //end reached
                        }
                        return buffer;
                    }
                }

                @Override
                public Int2LongMap.Entry next() {
                    if (!hasNext()) {
                        throw FastNoSuchElementException.getInstance("No more elements");
                    }
                    final Int2LongMap.Entry next = currentIterator.next();
                    seenKey = next.getIntKey();
                    return next;
                }

                @Override
                public void remove() {
                    ConcurrentInt2LongMap.this.remove(seenKey);
                }

                @Override
                public String toString() {
                    return bucketIndex + ": " + currentIterator;
                }
            };
        }

        @Override
        public boolean add(final Int2LongMap.Entry e) {
            throw newUnmodifiableException();
        }

        @Override
        public boolean remove(final Object o) {
            throw newUnmodifiableException();
        }

        @Override
        public boolean addAll(final Collection<? extends Int2LongMap.Entry> c) {
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