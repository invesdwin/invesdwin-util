package de.invesdwin.util.collections.primitive.objkey;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.iterable.buffer.BufferingIterator;
import de.invesdwin.util.collections.primitive.APrimitiveConcurrentMap;
import de.invesdwin.util.collections.primitive.PrimitiveConcurrentMapConfig;
import de.invesdwin.util.collections.primitive.objkey.striped.IObjectIterator;
import de.invesdwin.util.concurrent.lock.ICloseableLock;
import it.unimi.dsi.fastutil.objects.Object2ObjectFunction;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;

@ThreadSafe
public class ConcurrentObject2ObjectMap<K, V> extends APrimitiveConcurrentMap<K, V> implements Object2ObjectMap<K, V> {
    protected final Object2ObjectOpenHashMap<K, V>[] maps;
    protected final V defaultValue;
    private final ObjectSet<Object2ObjectMap.Entry<K, V>> object2ObjectEntrySet;
    private final ObjectSet<K> keySet;
    private final ObjectCollection<V> values;

    public ConcurrentObject2ObjectMap() {
        this(PrimitiveConcurrentMapConfig.DEFAULT);
    }

    public ConcurrentObject2ObjectMap(final PrimitiveConcurrentMapConfig config) {
        this(config, null);
    }

    @SuppressWarnings("unchecked")
    public ConcurrentObject2ObjectMap(final PrimitiveConcurrentMapConfig config, final V defaultValue) {
        super(config);
        final int concurrencyLevel = config.getConcurrencyLevel();
        this.maps = new Object2ObjectOpenHashMap[concurrencyLevel];
        this.defaultValue = defaultValue;
        final int bucketCapacity = config.getBucketCapacity();
        final float loadFactor = config.getLoadFactor();
        for (int i = 0; i < concurrencyLevel; i++) {
            final Object2ObjectOpenHashMap<K, V> map = new Object2ObjectOpenHashMap<>(bucketCapacity, loadFactor);
            map.defaultReturnValue(defaultValue);
            maps[i] = map;
        }
        this.object2ObjectEntrySet = new Object2ObjectEntrySet();
        this.keySet = new KeySet();
        this.values = new ValuesCollection();
    }

    @Override
    protected final Object2ObjectOpenHashMap<K, V> mapAt(final int index) {
        return maps[index];
    }

    @Override
    public boolean containsKey(final Object key) {
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

    @Override
    public V get(final Object key) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = readAt(bucket)) {
            return maps[bucket].get(key);
        }
    }

    @Override
    public V getOrDefault(final Object key, final V defaultValue) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = readAt(bucket)) {
            return maps[bucket].getOrDefault(key, defaultValue);
        }
    }

    @Override
    public V put(final K key, final V value) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            return maps[bucket].put(key, value);
        }
    }

    @Override
    public V remove(final Object key) {
        final int bucket = getBucket(key);
        try (ICloseableLock lock = writeAt(bucket)) {
            return maps[bucket].remove(key);
        }
    }

    @Override
    public boolean remove(final Object key, final Object value) {
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

    @Override
    public V computeIfAbsent(final K key, final Object2ObjectFunction<? super K, ? extends V> mappingFunction) {
        final int bucket = getBucket(key);
        final Object2ObjectOpenHashMap<K, V> m = mapAt(bucket);
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
    public V computeIfAbsent(final K key, final Function<? super K, ? extends V> mappingFunction) {
        final int bucket = getBucket(key);
        final Object2ObjectOpenHashMap<K, V> m = mapAt(bucket);
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
    public ObjectSet<Map.Entry<K, V>> entrySet() {
        return Object2ObjectMap.super.entrySet();
    }

    @Override
    public ObjectSet<Object2ObjectMap.Entry<K, V>> object2ObjectEntrySet() {
        return object2ObjectEntrySet;
    }

    @Override
    public ObjectSet<K> keySet() {
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
            return ConcurrentObject2ObjectMap.this.size();
        }

        @Override
        public boolean isEmpty() {
            return ConcurrentObject2ObjectMap.this.isEmpty();
        }

        @Override
        public boolean contains(final Object o) {
            return containsValue(o);
        }

        @Override
        public IObjectIterator<V> iterator() {
            final Iterator<Object2ObjectMap.Entry<K, V>> it = object2ObjectEntrySet.iterator();
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
            return ConcurrentObject2ObjectMap.this.size();
        }

        @Override
        public boolean isEmpty() {
            return ConcurrentObject2ObjectMap.this.isEmpty();
        }

        @Override
        public boolean contains(final Object o) {
            return containsKey(o);
        }

        @Override
        public ObjectIterator<K> iterator() {
            final Iterator<Object2ObjectMap.Entry<K, V>> it = object2ObjectEntrySet.iterator();
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

    private final class Object2ObjectEntrySet extends AbstractSet<Object2ObjectMap.Entry<K, V>>
            implements ObjectSet<Object2ObjectMap.Entry<K, V>> {

        private final ObjectSet<Object2ObjectMap.Entry<K, V>>[] delegates;

        @SuppressWarnings("unchecked")
        private Object2ObjectEntrySet() {
            this.delegates = new ObjectSet[maps.length];
            for (int i = 0; i < maps.length; i++) {
                delegates[i] = maps[i].object2ObjectEntrySet();
            }
        }

        @Override
        public int size() {
            return ConcurrentObject2ObjectMap.this.size();
        }

        @Override
        public boolean isEmpty() {
            return ConcurrentObject2ObjectMap.this.isEmpty();
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
        public ObjectIterator<Object2ObjectMap.Entry<K, V>> iterator() {
            if (isEmpty()) {
                return it.unimi.dsi.fastutil.objects.ObjectIterators.emptyIterator();
            }
            return new IObjectIterator<Object2ObjectMap.Entry<K, V>>() {
                private int bucketIndex = -1;
                private Iterator<Object2ObjectMap.Entry<K, V>> currentIterator = it.unimi.dsi.fastutil.objects.ObjectIterators
                        .emptyIterator();
                private Object2ObjectMap.Entry<K, V> seenEntry;

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

                private Iterator<Object2ObjectMap.Entry<K, V>> newIterator(final int bucket) {
                    try (ICloseableLock lock = readAt(bucket)) {
                        return new BufferingIterator<Object2ObjectMap.Entry<K, V>>(delegates[bucket]);
                    }
                }

                @Override
                public Object2ObjectMap.Entry<K, V> next() {
                    if (!hasNext()) {
                        throw new IllegalStateException("No more elements");
                    }
                    seenEntry = currentIterator.next();
                    return seenEntry;
                }

                @Override
                public void remove() {
                    ConcurrentObject2ObjectMap.this.remove(seenEntry.getKey());
                }

                @Override
                public String toString() {
                    return bucketIndex + ": " + currentIterator;
                }
            };
        }

        @Override
        public boolean add(final Object2ObjectMap.Entry<K, V> e) {
            throw newUnmodifiableException();
        }

        @Override
        public boolean remove(final Object o) {
            throw newUnmodifiableException();
        }

        @Override
        public boolean addAll(final Collection<? extends Object2ObjectMap.Entry<K, V>> c) {
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