package de.invesdwin.util.collections.primitive.intkey;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.iterable.buffer.BufferingIterator;
import de.invesdwin.util.collections.primitive.APrimitiveConcurrentMap;
import de.invesdwin.util.collections.primitive.PrimitiveConcurrentMapConfig;
import de.invesdwin.util.collections.primitive.objkey.striped.IObjectIterator;
import de.invesdwin.util.concurrent.lock.ICloseableLock;
import de.invesdwin.util.math.Integers;
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;

@ThreadSafe
public class ConcurrentInt2ObjectMap<V> extends APrimitiveConcurrentMap<Integer, V> implements Int2ObjectMap<V> {
    protected final Int2ObjectOpenHashMap<V>[] maps;
    protected final V defaultValue;
    private final ObjectSet<Int2ObjectMap.Entry<V>> int2ObjectEntrySet;
    private final IntSet keySet;
    private final ObjectCollection<V> values;

    public ConcurrentInt2ObjectMap() {
        this(PrimitiveConcurrentMapConfig.DEFAULT);
    }

    public ConcurrentInt2ObjectMap(final PrimitiveConcurrentMapConfig config) {
        this(config, null);
    }

    @SuppressWarnings("unchecked")
    public ConcurrentInt2ObjectMap(final PrimitiveConcurrentMapConfig config, final V defaultValue) {
        super(config);
        final int concurrencyLevel = config.getConcurrencyLevel();
        this.maps = new Int2ObjectOpenHashMap[concurrencyLevel];
        this.defaultValue = defaultValue;
        final int bucketCapacity = config.getBucketCapacity();
        final float loadFactor = config.getLoadFactor();
        for (int i = 0; i < concurrencyLevel; i++) {
            final Int2ObjectOpenHashMap<V> map = new Int2ObjectOpenHashMap<V>(bucketCapacity, loadFactor);
            map.defaultReturnValue(defaultValue);
            maps[i] = map;
        }
        this.int2ObjectEntrySet = new Int2ObjectEntrySet();
        this.keySet = new KeySet();
        this.values = new ValuesCollection();
    }

    @Override
    protected Int2ObjectOpenHashMap<V> mapAt(final int index) {
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

    @Deprecated
    @Override
    public V get(final Object key) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = readAt(bucket)) {
            return maps[bucket].get(key);
        }
    }

    @Override
    public V get(final int key) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = readAt(bucket)) {
            return maps[bucket].get(key);
        }
    }

    @Deprecated
    @Override
    public V getOrDefault(final Object key, final V defaultValue) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = readAt(bucket)) {
            return maps[bucket].getOrDefault(key, defaultValue);
        }
    }

    @Override
    public V getOrDefault(final int key, final V defaultValue) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = readAt(bucket)) {
            return maps[bucket].getOrDefault(key, defaultValue);
        }
    }

    @Deprecated
    @Override
    public V put(final Integer key, final V value) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            return maps[bucket].put(key, value);
        }
    }

    @Override
    public V put(final int key, final V value) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            return maps[bucket].put(key, value);
        }
    }

    @Deprecated
    @Override
    public V remove(final Object key) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            return maps[bucket].remove(key);
        }
    }

    @Override
    public V remove(final int key) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            return maps[bucket].remove(key);
        }
    }

    @Deprecated
    @Override
    public boolean remove(final Object key, final Object value) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            return maps[bucket].remove(key, value);
        }
    }

    @Override
    public boolean remove(final int key, final Object value) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            return maps[bucket].remove(key, value);
        }
    }

    @Deprecated
    @Override
    public void defaultReturnValue(final V rv) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V defaultReturnValue() {
        return defaultValue;
    }

    @Deprecated
    @Override
    public V computeIfAbsent(final Integer key, final Function<? super Integer, ? extends V> mappingFunction) {
        final int bucket = getBucket(key);
        final Int2ObjectOpenHashMap<V> m = mapAt(bucket);
        V v;
        try (ICloseableLock lock = readAt(bucket)) {
            v = m.get(key);
        }
        if (v == null || v == defaultValue) {
            //bad idea to synchronize in apply, this might cause deadlocks when threads are used inside of it
            v = mappingFunction.apply(key);
            if (v != null && v != defaultValue) {
                try (ICloseableLock lock = writeAt(bucket)) {
                    final V oldV = m.get(key);
                    if (oldV != null && oldV != defaultValue) {
                        v = oldV;
                    } else {
                        m.put(key, v);
                    }
                }
            }
        }
        return v;
    }

    @Override
    public V computeIfAbsent(final int key, final Int2ObjectFunction<? extends V> mappingFunction) {
        final int bucket = getBucket(key);
        final Int2ObjectOpenHashMap<V> m = mapAt(bucket);
        V v;
        try (ICloseableLock lock = readAt(bucket)) {
            v = m.get(key);
        }
        if (v == null || v == defaultValue) {
            //bad idea to synchronize in apply, this might cause deadlocks when threads are used inside of it
            v = mappingFunction.apply(key);
            if (v != null && v != defaultValue) {
                try (ICloseableLock lock = writeAt(bucket)) {
                    final V oldV = m.get(key);
                    if (oldV != null && oldV != defaultValue) {
                        v = oldV;
                    } else {
                        m.put(key, v);
                    }
                }
            }
        }
        return v;
    }

    @Override
    public V computeIfAbsent(final int key, final IntFunction<? extends V> mappingFunction) {
        final int bucket = getBucket(key);
        final Int2ObjectOpenHashMap<V> m = mapAt(bucket);
        V v;
        try (ICloseableLock lock = readAt(bucket)) {
            v = m.get(key);
        }
        if (v == null || v == defaultValue) {
            //bad idea to synchronize in apply, this might cause deadlocks when threads are used inside of it
            v = mappingFunction.apply(key);
            if (v != null && v != defaultValue) {
                try (ICloseableLock lock = writeAt(bucket)) {
                    final V oldV = m.get(key);
                    if (oldV != null && oldV != defaultValue) {
                        v = oldV;
                    } else {
                        m.put(key, v);
                    }
                }
            }
        }
        return v;
    }

    @SuppressWarnings("deprecation")
    @Override
    public ObjectSet<Map.Entry<Integer, V>> entrySet() {
        return Int2ObjectMap.super.entrySet();
    }

    @Override
    public ObjectSet<Int2ObjectMap.Entry<V>> int2ObjectEntrySet() {
        return int2ObjectEntrySet;
    }

    @Override
    public IntSet keySet() {
        return keySet;
    }

    @Override
    public ObjectCollection<V> values() {
        return values;
    }

    private final class ValuesCollection extends AbstractCollection<V> implements ObjectCollection<V> {
        private final Collection<V>[] delegates;

        @SuppressWarnings("unchecked")
        private ValuesCollection() {
            this.delegates = new Collection[maps.length];
            for (int i = 0; i < maps.length; i++) {
                delegates[i] = maps[i].values();
            }
        }

        @Override
        public boolean add(final V k) {
            throw newUnmodifiableException();
        }

        @Override
        public boolean remove(final Object k) {
            throw newUnmodifiableException();
        }

        @Override
        public int size() {
            return ConcurrentInt2ObjectMap.this.size();
        }

        @Override
        public boolean isEmpty() {
            return ConcurrentInt2ObjectMap.this.isEmpty();
        }

        @Override
        public boolean contains(final Object o) {
            return containsValue(o);
        }

        @Override
        public IObjectIterator<V> iterator() {
            final Iterator<Int2ObjectMap.Entry<V>> it = int2ObjectEntrySet.iterator();
            return new IObjectIterator<V>() {
                @Override
                public boolean hasNext() {
                    return it.hasNext();
                }

                @Override
                public V next() {
                    return it.next().getValue();
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
        public IObjectIterator<V> spliterator() {
            return iterator();
        }

        @Override
        public Stream<V> stream() {
            return iterator().stream();
        }

        @Override
        public Stream<V> parallelStream() {
            return iterator().stream().parallel();
        }

        @Override
        public void clear() {
            throw newUnmodifiableException();
        }

        @Override
        public boolean addAll(final Collection<? extends V> c) {
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
        public boolean removeIf(final Predicate<? super V> filter) {
            throw newUnmodifiableException();
        }

    }

    private final class KeySet extends AbstractSet<Integer> implements IntSet {

        private final IntSet[] delegates;

        @SuppressWarnings("unchecked")
        private KeySet() {
            this.delegates = new IntSet[maps.length];
            for (int i = 0; i < maps.length; i++) {
                delegates[i] = maps[i].keySet();
            }
        }

        @Override
        public int size() {
            return ConcurrentInt2ObjectMap.this.size();
        }

        @Override
        public boolean isEmpty() {
            return ConcurrentInt2ObjectMap.this.isEmpty();
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
            final Iterator<Int2ObjectMap.Entry<V>> it = int2ObjectEntrySet.iterator();
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

    private final class Int2ObjectEntrySet extends AbstractSet<Int2ObjectMap.Entry<V>>
            implements ObjectSet<Int2ObjectMap.Entry<V>> {

        private final ObjectSet<Int2ObjectMap.Entry<V>>[] delegates;

        @SuppressWarnings("unchecked")
        private Int2ObjectEntrySet() {
            this.delegates = new ObjectSet[maps.length];
            for (int i = 0; i < maps.length; i++) {
                delegates[i] = maps[i].int2ObjectEntrySet();
            }
        }

        @Override
        public int size() {
            return ConcurrentInt2ObjectMap.this.size();
        }

        @Override
        public boolean isEmpty() {
            return ConcurrentInt2ObjectMap.this.isEmpty();
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
        public ObjectIterator<Int2ObjectMap.Entry<V>> iterator() {
            if (isEmpty()) {
                return it.unimi.dsi.fastutil.objects.ObjectIterators.emptyIterator();
            }
            return new IObjectIterator<Int2ObjectMap.Entry<V>>() {
                private int bucketIndex = -1;
                private Iterator<Int2ObjectMap.Entry<V>> currentIterator = it.unimi.dsi.fastutil.objects.ObjectIterators
                        .emptyIterator();
                private Int2ObjectMap.Entry<V> seenEntry;

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

                private Iterator<Int2ObjectMap.Entry<V>> newIterator(final int bucket) {
                    try (ICloseableLock lock = readAt(bucket)) {
                        return new BufferingIterator<Int2ObjectMap.Entry<V>>(delegates[bucket]);
                    }
                }

                @Override
                public Int2ObjectMap.Entry<V> next() {
                    if (!hasNext()) {
                        throw new IllegalStateException("No more elements");
                    }
                    seenEntry = currentIterator.next();
                    return seenEntry;
                }

                @Override
                public void remove() {
                    ConcurrentInt2ObjectMap.this.remove(seenEntry.getIntKey());
                }

                @Override
                public String toString() {
                    return bucketIndex + ": " + currentIterator;
                }
            };
        }

        @Override
        public boolean add(final Int2ObjectMap.Entry<V> e) {
            throw newUnmodifiableException();
        }

        @Override
        public boolean remove(final Object o) {
            throw newUnmodifiableException();
        }

        @Override
        public boolean addAll(final Collection<? extends Int2ObjectMap.Entry<V>> c) {
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