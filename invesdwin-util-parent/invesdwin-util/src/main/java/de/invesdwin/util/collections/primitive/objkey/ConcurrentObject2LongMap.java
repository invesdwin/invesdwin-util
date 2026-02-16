package de.invesdwin.util.collections.primitive.objkey;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToLongFunction;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.iterable.buffer.NodeBufferingIterator;
import de.invesdwin.util.collections.primitive.APrimitiveConcurrentMap;
import de.invesdwin.util.collections.primitive.PrimitiveConcurrentMapConfig;
import de.invesdwin.util.collections.primitive.objkey.entry.NodeImmutableObject2LongEntry;
import de.invesdwin.util.collections.primitive.objkey.striped.IObjectIterator;
import de.invesdwin.util.concurrent.lock.ICloseableLock;
import de.invesdwin.util.math.Longs;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.Object2LongFunction;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;

@ThreadSafe
public class ConcurrentObject2LongMap<K> extends APrimitiveConcurrentMap<K, Long> implements Object2LongMap<K> {
    public static final long DEFAULT_VALUE = Longs.DEFAULT_MISSING_VALUE;

    protected final Object2LongOpenHashMap<K>[] maps;
    protected final long defaultValue;
    private final ObjectSet<Object2LongMap.Entry<K>> object2LongEntrySet;
    private final ObjectSet<K> keySet;
    private final LongCollection values;

    public ConcurrentObject2LongMap() {
        this(PrimitiveConcurrentMapConfig.DEFAULT);
    }

    public ConcurrentObject2LongMap(final PrimitiveConcurrentMapConfig config) {
        this(config, DEFAULT_VALUE);
    }

    @SuppressWarnings("unchecked")
    public ConcurrentObject2LongMap(final PrimitiveConcurrentMapConfig config, final long defaultValue) {
        super(config);
        final int concurrencyLevel = config.getConcurrencyLevel();
        this.maps = new Object2LongOpenHashMap[concurrencyLevel];
        this.defaultValue = defaultValue;
        final int bucketCapacity = config.getBucketCapacity();
        final float loadFactor = config.getLoadFactor();
        for (int i = 0; i < concurrencyLevel; i++) {
            final Object2LongOpenHashMap<K> map = new Object2LongOpenHashMap<>(bucketCapacity, loadFactor);
            map.defaultReturnValue(defaultValue);
            maps[i] = map;
        }
        this.object2LongEntrySet = new Object2LongEntrySet();
        this.keySet = new KeySet();
        this.values = new ValuesCollection();
    }

    @Override
    protected final Object2LongOpenHashMap<K> mapAt(final int index) {
        return maps[index];
    }

    @Override
    public boolean containsKey(final Object key) {
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
    public long getLong(final Object key) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = readAt(bucket)) {
            return maps[bucket].getLong(key);
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
    public long getOrDefault(final Object key, final long defaultValue) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = readAt(bucket)) {
            return maps[bucket].getOrDefault(key, defaultValue);
        }
    }

    @Deprecated
    @Override
    public Long put(final K key, final Long value) {
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
    public long put(final K key, final long value) {
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
    public long removeLong(final Object key) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            final long removed = maps[bucket].removeLong(key);
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
    public boolean remove(final Object key, final long value) {
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
    public Long computeIfAbsent(final K key, final Function<? super K, ? extends Long> mappingFunction) {
        final int bucket = getBucket(key);
        final Object2LongOpenHashMap<K> m = mapAt(bucket);
        long v;
        try (ICloseableLock lock = readAt(bucket)) {
            v = m.getLong(key);
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
                    final long oldV = m.getLong(key);
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
    public long computeIfAbsent(final K key, final Object2LongFunction<? super K> mappingFunction) {
        final int bucket = getBucket(key);
        final Object2LongOpenHashMap<K> m = mapAt(bucket);
        long v;
        try (ICloseableLock lock = readAt(bucket)) {
            v = m.getLong(key);
        }
        if (v == defaultValue) {
            //bad idea to synchronize in apply, this might cause deadlocks when threads are used inside of it
            v = mappingFunction.applyAsLong(key);
            if (v != defaultValue) {
                try (ICloseableLock lock = writeAt(bucket)) {
                    final long oldV = m.getLong(key);
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
    public long computeIfAbsent(final K key, final ToLongFunction<? super K> mappingFunction) {
        final int bucket = getBucket(key);
        final Object2LongOpenHashMap<K> m = mapAt(bucket);
        long v;
        try (ICloseableLock lock = readAt(bucket)) {
            v = m.getLong(key);
        }
        if (v == defaultValue) {
            //bad idea to synchronize in apply, this might cause deadlocks when threads are used inside of it
            v = mappingFunction.applyAsLong(key);
            if (v != defaultValue) {
                try (ICloseableLock lock = writeAt(bucket)) {
                    final long oldV = m.getLong(key);
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

    @Deprecated
    @Override
    public ObjectSet<Map.Entry<K, Long>> entrySet() {
        return Object2LongMap.super.entrySet();
    }

    @Override
    public ObjectSet<Object2LongMap.Entry<K>> object2LongEntrySet() {
        return object2LongEntrySet;
    }

    @Override
    public ObjectSet<K> keySet() {
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
        public boolean add(final Long k) {
            throw newUnmodifiableException();
        }

        @Override
        public boolean remove(final Object k) {
            throw newUnmodifiableException();
        }

        @Override
        public int size() {
            return ConcurrentObject2LongMap.this.size();
        }

        @Override
        public boolean isEmpty() {
            return ConcurrentObject2LongMap.this.isEmpty();
        }

        @Deprecated
        @Override
        public boolean contains(final Object o) {
            return containsValue(o);
        }

        @Override
        public boolean contains(final long key) {
            return containsValue(key);
        }

        @Override
        public LongIterator iterator() {
            final Iterator<Object2LongMap.Entry<K>> it = object2LongEntrySet.iterator();
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
        public boolean removeIf(final Predicate<? super Long> filter) {
            throw newUnmodifiableException();
        }

        @Override
        public boolean add(final long key) {
            throw newUnmodifiableException();
        }

        @Override
        public boolean rem(final long key) {
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

    private final class KeySet extends AbstractSet<K> implements ObjectSet<K> {

        private final ObjectSet<K>[] delegates;

        @SuppressWarnings("unchecked")
        private KeySet() {
            this.delegates = new ObjectSet[maps.length];
            for (int i = 0; i < maps.length; i++) {
                delegates[i] = maps[i].keySet();
            }
        }

        @Override
        public int size() {
            return ConcurrentObject2LongMap.this.size();
        }

        @Override
        public boolean isEmpty() {
            return ConcurrentObject2LongMap.this.isEmpty();
        }

        @Override
        public boolean contains(final Object o) {
            return containsKey(o);
        }

        @Override
        public ObjectIterator<K> iterator() {
            final Iterator<Object2LongMap.Entry<K>> it = object2LongEntrySet.iterator();
            return new IObjectIterator<K>() {
                @Override
                public boolean hasNext() {
                    return it.hasNext();
                }

                @Override
                public K next() {
                    return it.next().getKey();
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
        public boolean add(final K e) {
            throw newUnmodifiableException();
        }

        @Override
        public boolean remove(final Object o) {
            throw newUnmodifiableException();
        }

        @Override
        public boolean addAll(final Collection<? extends K> c) {
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

    private final class Object2LongEntrySet extends AbstractSet<Object2LongMap.Entry<K>>
            implements ObjectSet<Object2LongMap.Entry<K>> {

        private final ObjectSet<Object2LongMap.Entry<K>>[] delegates;

        @SuppressWarnings("unchecked")
        private Object2LongEntrySet() {
            this.delegates = new ObjectSet[maps.length];
            for (int i = 0; i < maps.length; i++) {
                delegates[i] = maps[i].object2LongEntrySet();
            }
        }

        @Override
        public int size() {
            return ConcurrentObject2LongMap.this.size();
        }

        @Override
        public boolean isEmpty() {
            return ConcurrentObject2LongMap.this.isEmpty();
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
        public ObjectIterator<Object2LongMap.Entry<K>> iterator() {
            if (isEmpty()) {
                return it.unimi.dsi.fastutil.objects.ObjectIterators.emptyIterator();
            }
            return new IObjectIterator<Object2LongMap.Entry<K>>() {
                private int bucketIndex = -1;
                private Iterator<? extends Object2LongMap.Entry<K>> currentIterator = it.unimi.dsi.fastutil.objects.ObjectIterators
                        .emptyIterator();
                private K seenKey;

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

                private Iterator<? extends Object2LongMap.Entry<K>> newIterator(final int bucket) {
                    try (ICloseableLock lock = readAt(bucket)) {
                        final NodeBufferingIterator<NodeImmutableObject2LongEntry<K>> buffer = new NodeBufferingIterator<NodeImmutableObject2LongEntry<K>>();
                        final ObjectIterator<Object2LongMap.Entry<K>> it = delegates[bucket].iterator();
                        try {
                            while (true) {
                                final Object2LongMap.Entry<K> next = it.next();
                                buffer.add(NodeImmutableObject2LongEntry.of(next.getKey(), next.getLongValue()));
                            }
                        } catch (final NoSuchElementException e) {
                            //end reached
                        }
                        return buffer;
                    }
                }

                @Override
                public Object2LongMap.Entry<K> next() {
                    if (!hasNext()) {
                        throw new IllegalStateException("No more elements");
                    }
                    final Object2LongMap.Entry<K> next = currentIterator.next();
                    seenKey = next.getKey();
                    return next;
                }

                @Override
                public void remove() {
                    ConcurrentObject2LongMap.this.removeLong(seenKey);
                }

                @Override
                public String toString() {
                    return bucketIndex + ": " + currentIterator;
                }
            };
        }

        @Override
        public boolean add(final Object2LongMap.Entry<K> e) {
            throw newUnmodifiableException();
        }

        @Override
        public boolean remove(final Object o) {
            throw newUnmodifiableException();
        }

        @Override
        public boolean addAll(final Collection<? extends Object2LongMap.Entry<K>> c) {
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