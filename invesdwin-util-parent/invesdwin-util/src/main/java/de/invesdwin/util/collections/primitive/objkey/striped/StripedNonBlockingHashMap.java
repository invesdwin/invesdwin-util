package de.invesdwin.util.collections.primitive.objkey.striped;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.LongConsumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.annotation.concurrent.ThreadSafe;

import org.jctools.maps.NonBlockingHashMap;
import org.jctools.maps.NonBlockingHashMapLong;
import org.jspecify.annotations.Nullable;

import de.invesdwin.util.collections.primitive.IPrimitiveConcurrentKeyMap;
import de.invesdwin.util.collections.primitive.longkey.ConcurrentLong2ObjectMap;
import de.invesdwin.util.collections.primitive.util.BucketHashUtil;
import de.invesdwin.util.concurrent.lock.ICloseableLock;
import de.invesdwin.util.concurrent.lock.padded.PaddedCloseableReentrantLock;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectSet;

/**
 * Similar to {@link ConcurrentLong2ObjectMap}, but backed with NonBlockingHashMapLong ⇒ non-blocking reads 🚀
 * https://github.com/JCTools/JCTools/blob/master/jctools-core/src/main/java/org/jctools/maps/NonBlockingHashMapLong.java
 * https://stackoverflow.com/questions/61721386/caffeine-cache-specify-expiry-for-an-entry
 * 
 * @see org.jctools.maps.NonBlockingHashMapLong
 * @see it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 * @see com.google.common.util.concurrent.Striped
 * 
 * @see AStripedNonBlockingHashMapCacheExpirer
 */
@ThreadSafe
public class StripedNonBlockingHashMap<K, V>
        implements ConcurrentMap<K, V>, Object2ObjectMap<K, V>, IPrimitiveConcurrentKeyMap, Iterable<K> {
    private final NonBlockingHashMap<K, V> m;
    /** @see com.google.common.util.concurrent.Striped#lock(int) */
    private final PaddedCloseableReentrantLock[] s;

    @SuppressWarnings("resource")
    public StripedNonBlockingHashMap(final int initialSize, final int concurrencyLevel) {
        assert concurrencyLevel > 0 : "Stripes must be positive, but " + concurrencyLevel;
        assert concurrencyLevel < 100_000_000 : "Too much Stripes: " + concurrencyLevel;
        m = new NonBlockingHashMap<K, V>(Math.max(initialSize, concurrencyLevel));
        s = new PaddedCloseableReentrantLock[concurrencyLevel];
        for (int i = 0; i < concurrencyLevel; i++) {
            s[i] = new PaddedCloseableReentrantLock();
        }
    }//new

    /** @see com.google.common.util.concurrent.Striped#get(Object) */
    protected PaddedCloseableReentrantLock write(final Object key) {
        final PaddedCloseableReentrantLock lock = s[BucketHashUtil.bucket(key, s.length)];
        lock.lock();
        return lock;
    }

    @Override
    public int size() {
        return m.size();
    }

    @Override
    public boolean isEmpty() {
        return m.isEmpty();
    }

    @Override
    public synchronized void clear() {
        withAllKeysWriteLock(map -> map.clear());
    }

    //CHECKSTYLE:OFF
    public void withAllKeysWriteLock(final Consumer<NonBlockingHashMap<K, V>> singleThreadMapModifier) {
        //CHECKSTYLE:ON
        for (final PaddedCloseableReentrantLock paddedLock : s) {
            paddedLock.lock();
        }
        try {
            singleThreadMapModifier.accept(m);
        } finally {
            for (final Lock lock : s) {
                lock.unlock();
            }
        }
    }

    @Override
    public V putIfAbsent(final K key, final V value) {
        try (ICloseableLock lock = write(key)) {
            return m.putIfAbsent(key, value);
        }
    }

    @Override
    public boolean remove(final Object key, /* E */ final Object value) {
        try (ICloseableLock lock = write(key)) {
            return m.remove(key, value);
        }
    }

    @Override
    public boolean replace(final K key, final V oldValue, final V newValue) {
        try (ICloseableLock lock = write(key)) {
            return m.replace(key, oldValue, newValue);
        }
    }

    @Override
    public V replace(final K key, final V value) {
        try (ICloseableLock lock = write(key)) {
            return m.replace(key, value);
        }
    }

    @Override
    public boolean containsKey(final Object key) {
        return m.containsKey(key);
    }

    @Override
    public boolean containsValue(final Object value) {
        return m.containsValue(value);
    }

    @Override
    public V get(final Object key) {
        return m.get(key);
    }

    @Override
    public V put(final K key, final V value) {
        try (ICloseableLock lock = write(key)) {
            return m.put(key, value);
        }
    }

    @Override
    public V remove(final Object key) {
        try (ICloseableLock lock = write(key)) {
            return m.remove(key);
        }
    }

    /** @see NonBlockingHashMapLong#putAll */
    @SuppressWarnings("deprecation")
    @Override
    public void putAll(final Map<? extends K, ? extends V> fromMap) {
        for (final Map.Entry<K, V> e : m.entrySet()) {
            put(e.getKey(), e.getValue());
        }
    }

    /** @see NonBlockingHashMapLong.IteratorLong */
    public static class StripedKeyIterator<K> implements IObjectIterator<K> {
        private final StripedNonBlockingHashMap<K, ?> owner;
        private final Iterator<K> it;
        private K seenKey;// ^ safe for concurrent

        @SuppressWarnings("unchecked")
        public StripedKeyIterator(final StripedNonBlockingHashMap<K, ?> owner) {
            this.owner = owner;
            it = (Iterator<K>) owner.m.keys();
        }//new

        /** Remove last key returned by {@link #next} or {@link #nextLong}. */
        @Override
        public void remove() {
            try (ICloseableLock lock = owner.write(seenKey)) {
                it.remove();
            }
        }

        /** Return the next key as a primitive {@code long}. */
        @Override
        public K next() {
            seenKey = it.next();
            return seenKey;
        }

        /** True if there are more keys to iterate over. */
        @Override
        public boolean hasNext() {
            return it.hasNext();
        }
    }//StripedLongIterator

    @Override
    public StripedKeyIterator<K> iterator() {
        return new StripedKeyIterator<K>(this);
    }

    @SuppressWarnings("unchecked")
    public void forEachKey(final Consumer<K> action) {
        final Iterator<K> it = (Iterator<K>) m.keys();
        try {
            while (it.hasNext()) {
                action.accept(it.next());
            }
        } catch (final CancellationException ignored) {
        }
    }

    @Override
    public ObjectSet<K> keySet() {
        throw new UnsupportedOperationException("keySet");
    }

    /**
     * @see NonBlockingHashMapLong#values()
     * @see it.unimi.dsi.fastutil.objects.ObjectCollections#unmodifiable(ObjectCollection)
     */
    @Override
    public ObjectCollection<V> values() {
        final Collection<V> collection = m.values();
        return new ObjectCollection<V>() {
            @Override
            public boolean add(final V k) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean remove(final Object k) {
                throw new UnsupportedOperationException();
            }

            @Override
            public int size() {
                return collection.size();
            }

            @Override
            public boolean isEmpty() {
                return collection.isEmpty();
            }

            @Override
            public boolean contains(final Object o) {
                return collection.contains(o);
            }

            @Override
            public IObjectIterator<V> iterator() {
                final Iterator<V> it = collection.iterator();
                return new IObjectIterator<V>() {
                    @Override
                    public boolean hasNext() {
                        return it.hasNext();
                    }

                    @Override
                    public V next() {
                        return it.next();
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
                throw new UnsupportedOperationException();
            }

            @Override
            public <T> T[] toArray(final T[] a) {
                return collection.toArray(a);
            }

            @Override
            public Object[] toArray() {
                return collection.toArray();
            }

            @Override
            public void forEach(final Consumer<? super V> action) {
                collection.forEach(action);
            }

            @Override
            public boolean containsAll(final Collection<?> c) {
                return collection.containsAll(c);
            }

            @Override
            public boolean addAll(final Collection<? extends V> c) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean removeAll(final Collection<?> c) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean retainAll(final Collection<?> c) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean removeIf(final Predicate<? super V> filter) {
                throw new UnsupportedOperationException();
            }

            @Override
            public String toString() {
                return collection.toString();
            }

            @Override
            public int hashCode() {
                return collection.hashCode();
            }

            @Override
            public boolean equals(final Object o) {
                if (o == this) {
                    return true;
                }
                return collection.equals(o);
            }
        };
    }

    @Override
    public ObjectSet<Map.Entry<K, V>> entrySet() {
        throw new UnsupportedOperationException("entrySet");
    }

    @Override
    public V merge(final K key, final V value, final BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        try (ICloseableLock lock = write(key)) {
            return m.merge(key, value, remappingFunction);
        }
    }

    @Override
    public V compute(final K key, final BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        try (ICloseableLock lock = write(key)) {
            return m.compute(key, remappingFunction);
        }
    }

    @Override
    public V computeIfPresent(final K key, final BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        try (ICloseableLock lock = write(key)) {
            return m.computeIfPresent(key, remappingFunction);
        }
    }

    @Override
    public V computeIfAbsent(final K key, final Function<? super K, ? extends V> mappingFunction) {
        try (ICloseableLock lock = write(key)) {
            return m.computeIfAbsent(key, mappingFunction);
        }
    }

    @Override
    public void replaceAll(final BiFunction<? super K, ? super V, ? extends V> function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void forEach(final BiConsumer<? super K, ? super V> action) {
        m.forEach(action);
    }

    @Override
    public V getOrDefault(final Object key, final V defaultValue) {
        final V v = get(key);
        return v != null ? v : defaultValue;
    }

    @Override
    public void defaultReturnValue(final V rv) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @Nullable V defaultReturnValue() {
        return null;
    }

    @Override
    public ObjectSet<Object2ObjectMap.Entry<K, V>> object2ObjectEntrySet() {
        throw new UnsupportedOperationException();
    }

    //CHECKSTYLE:OFF
    public <R> R withLock(final K key, final Function<Object2ObjectMap.Entry<K, V>, R> withLock) {
        //CHECKSTYLE:ON
        try (ICloseableLock lock = write(key)) {
            final Object2ObjectMap.Entry<K, V> x = new Object2ObjectMap.Entry<K, V>() {
                @Override
                public K getKey() {
                    return key;
                }

                @Override
                public V getValue() {
                    return m.get(key);
                }// can be changed inside withLock

                @Override
                public V setValue(final V value) {
                    return value != null ? m.put(key, value) : m.remove(key);
                }
            };
            return withLock.apply(x);
        }
    }

    /** Simple template to add an expiration support */
    public abstract static class AStripedNonBlockingHashMapCacheExpirer<K, V> {
        protected final StripedNonBlockingHashMap<K, V> cacheMap;

        /**
         * Heuristic: {@link #expire()} is called from a single thread (scheduler) when working correctly:
         * Atomic/volatile is not needed
         */
        protected long expiredCount;

        public AStripedNonBlockingHashMapCacheExpirer(final StripedNonBlockingHashMap<K, V> cacheMap) {
            this.cacheMap = cacheMap;
        }//new

        /** Processing of a deleted (evicted, expired) entry */
        protected void postProcessExpiredEntry(final K key, final V value) {}

        /** ~ value.getDeadline() ≤ now */
        protected abstract boolean isExpired(K key, V value);

        protected void beforeExpire() {
            // now = System.nanoTime();
        }

        /**
         * Example: how to make "true cache" with expiration.
         * 
         * https://github.com/JCTools/JCTools/blob/master/jctools-core/src/main/java/org/jctools/maps/NonBlockingHashMapLong.java
         * https://stackoverflow.com/questions/61721386/caffeine-cache-specify-expiry-for-an-entry
         * 
         * @see com.github.benmanes.caffeine.cache.Cache#policy()
         * @see com.github.benmanes.caffeine.cache.Policy#expireVariably()
         * 
         * @see org.springframework.scheduling.annotation.Scheduled
         * @see java.util.Set#removeIf
         * 
         * @see StripedNonBlockingHashMap#forEachKey(LongConsumer)
         * @see StripedNonBlockingHashMap#withLock
         */
        @SuppressWarnings("unchecked")
        public long expire() {
            final long initialExpiredCount = expiredCount;
            beforeExpire();
            try {
                for (final Iterator<K> it = (Iterator<K>) cacheMap.m.keys(); it.hasNext();) {
                    final K key = it.next();
                    V value = cacheMap.get(key);
                    if (value != null && isExpired(key, value)) {// first "light" check: value could be gone already #1

                        try (ICloseableLock lock = cacheMap.write(key)) {
                            value = cacheMap.get(key);// can be gone already #2
                            if (value != null && isExpired(key, value)) {// double check idiom
                                expiredCount++;
                                postProcessExpiredEntry(key, value);
                                it.remove();// expired ⇒ remove: we inside the write lock ⇒ allowed
                            }
                        }

                    } //#1
                } //f keys
            } catch (final CancellationException ignored) {
            }
            return afterExpire(initialExpiredCount);
        }

        /** e.g. reportExpireResult */
        protected long afterExpire(final long initialExpiredCount) {
            return expiredCount - initialExpiredCount;
        }
    }
}