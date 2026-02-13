package de.invesdwin.util.collections.primitive.longkey.striped;

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

import org.jctools.maps.NonBlockingHashMapLong;
import org.jspecify.annotations.Nullable;

import de.invesdwin.util.collections.primitive.IPrimitiveConcurrentMap;
import de.invesdwin.util.collections.primitive.longkey.ConcurrentLong2ObjectMap;
import de.invesdwin.util.collections.primitive.objkey.striped.IObjectIterator;
import de.invesdwin.util.collections.primitive.util.BucketHashUtil;
import de.invesdwin.util.concurrent.lock.ICloseableLock;
import de.invesdwin.util.concurrent.lock.padded.PaddedCloseableReentrantLock;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongSet;
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
 * @see AStripedNonBlockingHashMapLongCacheExpirer
 */
@ThreadSafe
public class StripedNonBlockingHashMapLong<V>
        implements ConcurrentMap<Long, V>, Long2ObjectMap<V>, IPrimitiveConcurrentMap, Iterable<Long> {
    public static final boolean DEFAULT_OPTIMIZE_FOR_SPACE = true;

    private final NonBlockingHashMapLong<V> m;
    /** @see com.google.common.util.concurrent.Striped#lock(int) */
    private final PaddedCloseableReentrantLock[] s;

    @SuppressWarnings("resource")
    public StripedNonBlockingHashMapLong(final int initialSize, final int concurrencyLevel) {
        this(initialSize, concurrencyLevel, DEFAULT_OPTIMIZE_FOR_SPACE);
    }

    @SuppressWarnings("resource")
    public StripedNonBlockingHashMapLong(final int initialSize, final int concurrencyLevel,
            final boolean optimizeForSpace) {
        assert concurrencyLevel > 0 : "Stripes must be positive, but " + concurrencyLevel;
        assert concurrencyLevel < 100_000_000 : "Too much Stripes: " + concurrencyLevel;
        m = new NonBlockingHashMapLong<>(Math.max(initialSize, concurrencyLevel), optimizeForSpace);
        s = new PaddedCloseableReentrantLock[concurrencyLevel];
        for (int i = 0; i < concurrencyLevel; i++) {
            s[i] = new PaddedCloseableReentrantLock();
        }
    }//new

    /** @see com.google.common.util.concurrent.Striped#get(Object) */
    protected PaddedCloseableReentrantLock write(final long key) {
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
        withAllKeysWriteLock(NonBlockingHashMapLong::clear);
    }

    public synchronized void clear(final boolean large) {
        withAllKeysWriteLock(map -> map.clear(large));
    }

    //CHECKSTYLE:OFF
    public void withAllKeysWriteLock(final Consumer<NonBlockingHashMapLong<V>> singleThreadMapModifier) {
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
    @Deprecated
    public V putIfAbsent(final Long key, final V value) {
        return putIfAbsent(key.longValue(), value);
    }

    @Override
    public V putIfAbsent(final long key, final V value) {
        try (ICloseableLock lock = write(key)) {
            return m.putIfAbsent(key, value);
        }
    }

    @Override
    @Deprecated
    public boolean remove(/* Long */final Object key, /* E */ final Object value) {
        return remove(((Long) key).longValue(), value);
    }

    @Override
    public boolean remove(final long key, /* E */ final Object value) {
        try (ICloseableLock lock = write(key)) {
            return m.remove(key, value);
        }
    }

    @Override
    @Deprecated
    public boolean replace(final Long key, final V oldValue, final V newValue) {
        return replace(key.longValue(), oldValue, newValue);
    }

    @Override
    public boolean replace(final long key, final V oldValue, final V newValue) {
        try (ICloseableLock lock = write(key)) {
            return m.replace(key, oldValue, newValue);
        }
    }

    @Override
    @Deprecated
    public V replace(final Long key, final V value) {
        return replace(key.longValue(), value);
    }

    @Override
    public V replace(final long key, final V value) {
        try (ICloseableLock lock = write(key)) {
            return m.replace(key, value);
        }
    }

    @Override
    @Deprecated
    public boolean containsKey(final Object key) {
        return m.containsKey(key);
    }

    @Override
    public boolean containsKey(final long key) {
        return m.containsKey(key);
    }

    @Override
    public boolean containsValue(final Object value) {
        return m.containsValue(value);
    }

    @Override
    @Deprecated
    public V get(final Object key) {
        return m.get(key);
    }

    @Override
    public V get(final long key) {
        return m.get(key);
    }

    @Override
    @Deprecated
    public V put(final Long key, final V value) {
        return put(key.longValue(), value);
    }

    @Override
    public V put(final long key, final V value) {
        try (ICloseableLock lock = write(key)) {
            return m.put(key, value);
        }
    }

    @Override
    @Deprecated
    public V remove(final Object key) {
        return remove(((Long) key).longValue());
    }

    @Override
    public V remove(final long key) {
        try (ICloseableLock lock = write(key)) {
            return m.remove(key);
        }
    }

    /** @see NonBlockingHashMapLong#putAll */
    @SuppressWarnings("deprecation")
    @Override
    public void putAll(final Map<? extends Long, ? extends V> fromMap) {
        for (final Map.Entry<Long, V> e : m.entrySet()) {
            put(e.getKey(), e.getValue());
        }
    }

    /** @see NonBlockingHashMapLong.IteratorLong */
    public static class StripedLongIterator implements ILong2ObjectIterator {
        private final StripedNonBlockingHashMapLong<?> owner;
        private final NonBlockingHashMapLong<?>.IteratorLong it;
        private long seenKey;// ^ safe for concurrent

        public StripedLongIterator(final StripedNonBlockingHashMapLong<?> owner) {
            this.owner = owner;
            it = (NonBlockingHashMapLong<?>.IteratorLong) owner.m.keys();
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
        public long nextLong() {
            seenKey = it.nextLong();
            return seenKey;
        }

        /** True if there are more keys to iterate over. */
        @Override
        public boolean hasNext() {
            return it.hasNext();
        }
    }//StripedLongIterator

    @Override
    public StripedLongIterator iterator() {
        return new StripedLongIterator(this);
    }

    public void forEachKey(final LongConsumer action) {
        final NonBlockingHashMapLong<V>.IteratorLong it = (NonBlockingHashMapLong<V>.IteratorLong) m.keys();
        try {
            while (it.hasNext()) {
                action.accept(it.nextLong());
            }
        } catch (final CancellationException ignored) {
        }
    }

    @Override
    public LongSet keySet() {
        throw new UnsupportedOperationException("keySet");
    }

    public long[] keySetLong() {
        return m.keySetLong();
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
    public ObjectSet<Map.Entry<Long, V>> entrySet() {
        throw new UnsupportedOperationException("entrySet");
    }

    @Override
    public V merge(final Long key, final V value,
            final BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        try (ICloseableLock lock = write(key)) {
            return m.merge(key, value, remappingFunction);
        }
    }

    @Override
    public V compute(final Long key, final BiFunction<? super Long, ? super V, ? extends V> remappingFunction) {
        try (ICloseableLock lock = write(key)) {
            return m.compute(key, remappingFunction);
        }
    }

    @Override
    public V computeIfPresent(final Long key,
            final BiFunction<? super Long, ? super V, ? extends V> remappingFunction) {
        try (ICloseableLock lock = write(key)) {
            return m.computeIfPresent(key, remappingFunction);
        }
    }

    @Override
    public V computeIfAbsent(final Long key, final Function<? super Long, ? extends V> mappingFunction) {
        try (ICloseableLock lock = write(key)) {
            return m.computeIfAbsent(key, mappingFunction);
        }
    }

    @Override
    public void replaceAll(final BiFunction<? super Long, ? super V, ? extends V> function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void forEach(final BiConsumer<? super Long, ? super V> action) {
        m.forEach(action);
    }

    @Override
    @Deprecated
    public V getOrDefault(final Object key, final V defaultValue) {
        return m.getOrDefault(key, defaultValue);
    }

    @Override
    public V getOrDefault(final long key, final V defaultValue) {
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
    public ObjectSet<Long2ObjectMap.Entry<V>> long2ObjectEntrySet() {
        throw new UnsupportedOperationException();
    }

    //CHECKSTYLE:OFF
    public <R> R withLock(final long key, final Function<Long2ObjectMap.Entry<V>, R> withLock) {
        //CHECKSTYLE:ON
        try (ICloseableLock lock = write(key)) {
            final Long2ObjectMap.Entry<V> x = new Long2ObjectMap.Entry<V>() {
                @Override
                public long getLongKey() {
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
    public abstract static class AStripedNonBlockingHashMapLongCacheExpirer<V> {
        protected final StripedNonBlockingHashMapLong<V> cacheMap;

        /**
         * Heuristic: {@link #expire()} is called from a single thread (scheduler) when working correctly:
         * Atomic/volatile is not needed
         */
        protected long expiredCount;

        public AStripedNonBlockingHashMapLongCacheExpirer(final StripedNonBlockingHashMapLong<V> cacheMap) {
            this.cacheMap = cacheMap;
        }//new

        /** Processing of a deleted (evicted, expired) entry */
        protected void postProcessExpiredEntry(final long key, final V value) {}

        /** ~ value.getDeadline() ≤ now */
        protected abstract boolean isExpired(long key, V value);

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
         * @see StripedNonBlockingHashMapLong#forEachKey(LongConsumer)
         * @see StripedNonBlockingHashMapLong#withLock
         */
        public long expire() {
            final long initialExpiredCount = expiredCount;
            beforeExpire();
            try {
                for (final NonBlockingHashMapLong<V>.IteratorLong it = (NonBlockingHashMapLong<V>.IteratorLong) cacheMap.m
                        .keys(); it.hasNext();) {
                    final long longKey = it.nextLong();
                    V value = cacheMap.get(longKey);
                    if (value != null && isExpired(longKey, value)) {// first "light" check: value could be gone already #1

                        try (ICloseableLock lock = cacheMap.write(longKey)) {
                            value = cacheMap.get(longKey);// can be gone already #2
                            if (value != null && isExpired(longKey, value)) {// double check idiom
                                expiredCount++;
                                postProcessExpiredEntry(longKey, value);
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