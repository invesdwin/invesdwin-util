package de.invesdwin.util.collections.primitive.longkey;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import java.util.function.LongFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.iterable.buffer.BufferingIterator;
import de.invesdwin.util.collections.primitive.APrimitiveConcurrentMap;
import de.invesdwin.util.collections.primitive.PrimitiveConcurrentMapConfig;
import de.invesdwin.util.collections.primitive.objkey.striped.IObjectIterator;
import de.invesdwin.util.concurrent.lock.ICloseableLock;
import de.invesdwin.util.math.Longs;
import it.unimi.dsi.fastutil.longs.Long2ObjectFunction;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;

@ThreadSafe
public class ConcurrentLong2ObjectMap<V> extends APrimitiveConcurrentMap<Long, V> implements Long2ObjectMap<V> {
    protected final Long2ObjectOpenHashMap<V>[] maps;
    protected final V defaultValue;
    private final ObjectSet<Long2ObjectMap.Entry<V>> long2ObjectEntrySet;
    private final LongSet keySet;
    private final ObjectCollection<V> values;

    public ConcurrentLong2ObjectMap() {
        this(PrimitiveConcurrentMapConfig.DEFAULT);
    }

    public ConcurrentLong2ObjectMap(final PrimitiveConcurrentMapConfig config) {
        this(config, null);
    }

    @SuppressWarnings("unchecked")
    public ConcurrentLong2ObjectMap(final PrimitiveConcurrentMapConfig config, final V defaultValue) {
        super(config);
        final int concurrencyLevel = config.getConcurrencyLevel();
        this.maps = new Long2ObjectOpenHashMap[concurrencyLevel];
        this.defaultValue = defaultValue;
        final int bucketCapacity = config.getBucketCapacity();
        final float loadFactor = config.getLoadFactor();
        for (int i = 0; i < concurrencyLevel; i++) {
            final Long2ObjectOpenHashMap<V> map = new Long2ObjectOpenHashMap<>(bucketCapacity, loadFactor);
            map.defaultReturnValue(defaultValue);
            maps[i] = map;
        }
        this.long2ObjectEntrySet = new Long2ObjectEntrySet();
        this.keySet = new KeySet();
        this.values = new ValuesCollection();
    }

    @Override
    protected final Long2ObjectOpenHashMap<V> mapAt(final int index) {
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
    public V get(final long key) {
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
    public V getOrDefault(final long key, final V defaultValue) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = readAt(bucket)) {
            return maps[bucket].getOrDefault(key, defaultValue);
        }
    }

    @Deprecated
    @Override
    public V put(final Long key, final V value) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            return maps[bucket].put(key, value);
        }
    }

    @Override
    public V put(final long key, final V value) {
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
    public V remove(final long key) {
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
    public boolean remove(final long key, final Object value) {
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
    public V computeIfAbsent(final Long key, final Function<? super Long, ? extends V> mappingFunction) {
        final int bucket = getBucket(key);
        final Long2ObjectOpenHashMap<V> m = mapAt(bucket);
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
    public V computeIfAbsent(final long key, final Long2ObjectFunction<? extends V> mappingFunction) {
        final int bucket = getBucket(key);
        final Long2ObjectOpenHashMap<V> m = mapAt(bucket);
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
    public V computeIfAbsent(final long key, final LongFunction<? extends V> mappingFunction) {
        final int bucket = getBucket(key);
        final Long2ObjectOpenHashMap<V> m = mapAt(bucket);
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
    public ObjectSet<Map.Entry<Long, V>> entrySet() {
        return Long2ObjectMap.super.entrySet();
    }

    @Override
    public ObjectSet<Long2ObjectMap.Entry<V>> long2ObjectEntrySet() {
        return long2ObjectEntrySet;
    }

    @Override
    public LongSet keySet() {
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
            return ConcurrentLong2ObjectMap.this.size();
        }

        @Override
        public boolean isEmpty() {
            return ConcurrentLong2ObjectMap.this.isEmpty();
        }

        @Override
        public boolean contains(final Object o) {
            return containsValue(o);
        }

        @Override
        public IObjectIterator<V> iterator() {
            final Iterator<Long2ObjectMap.Entry<V>> it = long2ObjectEntrySet.iterator();
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
            return ConcurrentLong2ObjectMap.this.size();
        }

        @Override
        public boolean isEmpty() {
            return ConcurrentLong2ObjectMap.this.isEmpty();
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
            final Iterator<Long2ObjectMap.Entry<V>> it = long2ObjectEntrySet.iterator();
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

    private final class Long2ObjectEntrySet extends AbstractSet<Long2ObjectMap.Entry<V>>
            implements ObjectSet<Long2ObjectMap.Entry<V>> {

        private final ObjectSet<Long2ObjectMap.Entry<V>>[] delegates;

        @SuppressWarnings("unchecked")
        private Long2ObjectEntrySet() {
            this.delegates = new ObjectSet[maps.length];
            for (int i = 0; i < maps.length; i++) {
                delegates[i] = maps[i].long2ObjectEntrySet();
            }
        }

        @Override
        public int size() {
            return ConcurrentLong2ObjectMap.this.size();
        }

        @Override
        public boolean isEmpty() {
            return ConcurrentLong2ObjectMap.this.isEmpty();
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
        public ObjectIterator<Long2ObjectMap.Entry<V>> iterator() {
            if (isEmpty()) {
                return it.unimi.dsi.fastutil.objects.ObjectIterators.emptyIterator();
            }
            return new IObjectIterator<Long2ObjectMap.Entry<V>>() {
                private int bucketIndex = -1;
                private Iterator<Long2ObjectMap.Entry<V>> currentIterator = it.unimi.dsi.fastutil.objects.ObjectIterators
                        .emptyIterator();
                private Long2ObjectMap.Entry<V> seenEntry;

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

                private Iterator<Long2ObjectMap.Entry<V>> newIterator(final int bucket) {
                    try (ICloseableLock lock = readAt(bucket)) {
                        return new BufferingIterator<Long2ObjectMap.Entry<V>>(delegates[bucket]);
                    }
                }

                @Override
                public Long2ObjectMap.Entry<V> next() {
                    if (!hasNext()) {
                        throw new IllegalStateException("No more elements");
                    }
                    seenEntry = currentIterator.next();
                    return seenEntry;
                }

                @Override
                public void remove() {
                    ConcurrentLong2ObjectMap.this.remove(seenEntry.getLongKey());
                }

                @Override
                public String toString() {
                    return bucketIndex + ": " + currentIterator;
                }
            };
        }

        @Override
        public boolean add(final Long2ObjectMap.Entry<V> e) {
            throw newUnmodifiableException();
        }

        @Override
        public boolean remove(final Object o) {
            throw newUnmodifiableException();
        }

        @Override
        public boolean addAll(final Collection<? extends Long2ObjectMap.Entry<V>> c) {
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