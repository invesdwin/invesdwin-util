package de.invesdwin.util.collections.primitive.longkey;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.LongUnaryOperator;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.iterable.buffer.NodeBufferingIterator;
import de.invesdwin.util.collections.primitive.APrimitiveConcurrentMap;
import de.invesdwin.util.collections.primitive.PrimitiveConcurrentMapConfig;
import de.invesdwin.util.collections.primitive.longkey.entry.NodeImmutableLong2LongEntry;
import de.invesdwin.util.collections.primitive.objkey.striped.IObjectIterator;
import de.invesdwin.util.concurrent.lock.ICloseableLock;
import de.invesdwin.util.error.FastNoSuchElementException;
import de.invesdwin.util.math.Longs;
import it.unimi.dsi.fastutil.longs.Long2LongFunction;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;

@ThreadSafe
public class ConcurrentLong2LongMap extends APrimitiveConcurrentMap<Long, Long> implements Long2LongMap {
    public static final long DEFAULT_VALUE = Longs.DEFAULT_MISSING_VALUE;

    protected final Long2LongOpenHashMap[] maps;
    protected final long defaultValue;
    private final ObjectSet<Long2LongMap.Entry> long2LongEntrySet;
    private final LongSet keySet;
    private final LongCollection values;

    public ConcurrentLong2LongMap() {
        this(PrimitiveConcurrentMapConfig.DEFAULT);
    }

    public ConcurrentLong2LongMap(final PrimitiveConcurrentMapConfig config) {
        this(config, DEFAULT_VALUE);
    }

    public ConcurrentLong2LongMap(final PrimitiveConcurrentMapConfig config, final long defaultValue) {
        super(config);
        final int concurrencyLevel = config.getConcurrencyLevel();
        this.maps = new Long2LongOpenHashMap[concurrencyLevel];
        this.defaultValue = defaultValue;
        final int bucketCapacity = config.getBucketCapacity();
        final float loadFactor = config.getLoadFactor();
        for (int i = 0; i < concurrencyLevel; i++) {
            final Long2LongOpenHashMap map = new Long2LongOpenHashMap(bucketCapacity, loadFactor);
            map.defaultReturnValue(defaultValue);
            maps[i] = map;
        }
        this.long2LongEntrySet = new Long2LongEntrySet();
        this.keySet = new KeySet();
        this.values = new ValuesCollection();
    }

    @Override
    protected final Long2LongOpenHashMap mapAt(final int index) {
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
    public long get(final long key) {
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
    public long getOrDefault(final long key, final long defaultValue) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = readAt(bucket)) {
            return maps[bucket].getOrDefault(key, defaultValue);
        }
    }

    @Deprecated
    @Override
    public Long put(final Long key, final Long value) {
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
    public long put(final long key, final long value) {
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
    public long remove(final long key) {
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
    public boolean remove(final long key, final long value) {
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
    public Long computeIfAbsent(final Long key, final Function<? super Long, ? extends Long> mappingFunction) {
        final int bucket = getBucket(key);
        final Long2LongOpenHashMap m = mapAt(bucket);
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
                        final Long existing = m.put(key.longValue(), v);
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
    public long computeIfAbsent(final long key, final Long2LongFunction mappingFunction) {
        final int bucket = getBucket(key);
        final Long2LongOpenHashMap m = mapAt(bucket);
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
    public long computeIfAbsent(final long key, final LongUnaryOperator mappingFunction) {
        final int bucket = getBucket(key);
        final Long2LongOpenHashMap m = mapAt(bucket);
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
    public ObjectSet<Map.Entry<Long, Long>> entrySet() {
        return Long2LongMap.super.entrySet();
    }

    @Override
    public ObjectSet<Long2LongMap.Entry> long2LongEntrySet() {
        return long2LongEntrySet;
    }

    @Override
    public LongSet keySet() {
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
            return ConcurrentLong2LongMap.this.size();
        }

        @Override
        public boolean isEmpty() {
            return ConcurrentLong2LongMap.this.isEmpty();
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
            final Iterator<Long2LongMap.Entry> it = long2LongEntrySet.iterator();
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
            return ConcurrentLong2LongMap.this.size();
        }

        @Override
        public boolean isEmpty() {
            return ConcurrentLong2LongMap.this.isEmpty();
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
            final Iterator<Long2LongMap.Entry> it = long2LongEntrySet.iterator();
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

    private final class Long2LongEntrySet extends AbstractSet<Long2LongMap.Entry>
            implements ObjectSet<Long2LongMap.Entry> {

        private final ObjectSet<Long2LongMap.Entry>[] delegates;

        @SuppressWarnings("unchecked")
        private Long2LongEntrySet() {
            this.delegates = new ObjectSet[maps.length];
            for (int i = 0; i < maps.length; i++) {
                delegates[i] = maps[i].long2LongEntrySet();
            }
        }

        @Override
        public int size() {
            return ConcurrentLong2LongMap.this.size();
        }

        @Override
        public boolean isEmpty() {
            return ConcurrentLong2LongMap.this.isEmpty();
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
        public ObjectIterator<Long2LongMap.Entry> iterator() {
            if (isEmpty()) {
                return it.unimi.dsi.fastutil.objects.ObjectIterators.emptyIterator();
            }
            return new IObjectIterator<Long2LongMap.Entry>() {
                private int bucketIndex = -1;
                private Iterator<? extends Long2LongMap.Entry> currentIterator = it.unimi.dsi.fastutil.objects.ObjectIterators
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

                private Iterator<? extends Long2LongMap.Entry> newIterator(final int bucket) {
                    try (ICloseableLock lock = readAt(bucket)) {
                        final NodeBufferingIterator<NodeImmutableLong2LongEntry> buffer = new NodeBufferingIterator<NodeImmutableLong2LongEntry>();
                        final ObjectIterator<Long2LongMap.Entry> it = delegates[bucket].iterator();
                        try {
                            while (true) {
                                final Long2LongMap.Entry next = it.next();
                                buffer.add(NodeImmutableLong2LongEntry.of(next.getLongKey(), next.getLongValue()));
                            }
                        } catch (final NoSuchElementException e) {
                            //end reached
                        }
                        return buffer;
                    }
                }

                @Override
                public Long2LongMap.Entry next() {
                    if (!hasNext()) {
                        throw FastNoSuchElementException.getInstance("No more elements");
                    }
                    final Long2LongMap.Entry next = currentIterator.next();
                    seenKey = next.getLongKey();
                    return next;
                }

                @Override
                public void remove() {
                    ConcurrentLong2LongMap.this.remove(seenKey);
                }

                @Override
                public String toString() {
                    return bucketIndex + ": " + currentIterator;
                }
            };
        }

        @Override
        public boolean add(final Long2LongMap.Entry e) {
            throw newUnmodifiableException();
        }

        @Override
        public boolean remove(final Object o) {
            throw newUnmodifiableException();
        }

        @Override
        public boolean addAll(final Collection<? extends Long2LongMap.Entry> c) {
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