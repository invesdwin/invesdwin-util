package de.invesdwin.util.collections.primitive.longkey.striped;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
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
import de.invesdwin.util.math.Longs;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
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
    private final ObjectSet<Map.Entry<Long, V>> entrySet = new EntrySet();
    private final ObjectSet<Long2ObjectMap.Entry<V>> long2ObjectEntrySet = new Long2ObjectEntrySet();
    private final LongSet keySet = new KeySet();
    private final ObjectCollection<V> values = new ValuesCollection();

    @SuppressWarnings("resource")
    public StripedNonBlockingHashMapLong(final int initialSize, final int concurrencyLevel) {
        this(initialSize, concurrencyLevel, DEFAULT_OPTIMIZE_FOR_SPACE);
    }

    @SuppressWarnings("resource")
    public StripedNonBlockingHashMapLong(final int initialSize, final int concurrencyLevel,
            final boolean optimizeForSpace) {
        assert concurrencyLevel > 0 : "Stripes must be positive, but " + concurrencyLevel;
        assert concurrencyLevel < 100_000_000 : "Too many stripes: " + concurrencyLevel;
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
        if (isEmpty()) {
            return;
        }
        withAllKeysWriteLock(NonBlockingHashMapLong::clear);
    }

    public synchronized void clear(final boolean large) {
        if (isEmpty()) {
            return;
        }
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
        return keySet;
    }

    public long[] keySetLong() {
        return m.keySetLong();
    }

    private UnsupportedOperationException newUnmodifiableException() {
        return new UnsupportedOperationException("Unmodifiable, only reading methods supported");
    }

    /**
     * @see NonBlockingHashMapLong#values()
     * @see it.unimi.dsi.fastutil.objects.ObjectCollections#unmodifiable(ObjectCollection)
     */
    @Override
    public ObjectCollection<V> values() {
        return values;
    }

    @Override
    public ObjectSet<Map.Entry<Long, V>> entrySet() {
        return entrySet;
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
        withAllKeysWriteLock(map -> map.replaceAll(function));
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
        return long2ObjectEntrySet;
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

    private final class ValuesCollection implements ObjectCollection<V> {
        private final Collection<V> delegate = m.values();

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
            return StripedNonBlockingHashMapLong.this.size();
        }

        @Override
        public boolean isEmpty() {
            return StripedNonBlockingHashMapLong.this.isEmpty();
        }

        @Override
        public boolean contains(final Object o) {
            return containsValue(o);
        }

        @Override
        public IObjectIterator<V> iterator() {
            final Iterator<V> it = delegate.iterator();
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
            throw newUnmodifiableException();
        }

        @Override
        public <T> T[] toArray(final T[] a) {
            return delegate.toArray(a);
        }

        @Override
        public Object[] toArray() {
            return delegate.toArray();
        }

        @Override
        public void forEach(final Consumer<? super V> action) {
            delegate.forEach(action);
        }

        @Override
        public boolean containsAll(final Collection<?> c) {
            return delegate.containsAll(c);
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

        @Override
        public String toString() {
            return delegate.toString();
        }

        @Override
        public int hashCode() {
            return delegate.hashCode();
        }

        @Override
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            return delegate.equals(o);
        }
    }

    private final class KeySet implements LongSet {

        private final Set<Long> delegate = m.keySet();

        @Override
        public int size() {
            return StripedNonBlockingHashMapLong.this.size();
        }

        @Override
        public boolean isEmpty() {
            return StripedNonBlockingHashMapLong.this.isEmpty();
        }

        @Override
        public boolean contains(final Object o) {
            return delegate.contains(o);
        }

        @Override
        public boolean contains(final long key) {
            return delegate.contains(key);
        }

        @Override
        public LongIterator iterator() {
            final Iterator<Long> it = delegate.iterator();
            return new LongIterator() {
                @Override
                public boolean hasNext() {
                    return it.hasNext();
                }

                @Override
                public Long next() {
                    return it.next();
                }

                @Override
                public long nextLong() {
                    return it.next();
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
        public Object[] toArray() {
            return delegate.toArray();
        }

        @Override
        public <T> T[] toArray(final T[] a) {
            return delegate.toArray(a);
        }

        @Override
        public long[] toArray(final long[] a) {
            if (a.length != 0) {
                throw new IllegalArgumentException("a.length needs to be 0, but " + a.length);
            }
            return toLongArray();
        }

        @Override
        public long[] toLongArray() {
            return Longs.checkedCastVector(delegate.toArray(Longs.EMPTY_ARRAY_OBJ));
        }

        @Override
        public long[] toLongArray(final long[] a) {
            if (a.length != 0) {
                throw new IllegalArgumentException("a.length needs to be 0, but " + a.length);
            }
            return toLongArray();
        }

        @Override
        public boolean add(final long e) {
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
        public boolean containsAll(final Collection<?> c) {
            return delegate.containsAll(c);
        }

        @Override
        public boolean containsAll(final LongCollection c) {
            return delegate.containsAll(c);
        }

        @Override
        public boolean addAll(final Collection<? extends Long> c) {
            throw newUnmodifiableException();
        }

        @Override
        public boolean addAll(final LongCollection c) {
            throw newUnmodifiableException();
        }

        @Override
        public boolean retainAll(final Collection<?> c) {
            throw newUnmodifiableException();
        }

        @Override
        public boolean retainAll(final LongCollection c) {
            throw newUnmodifiableException();
        }

        @Override
        public boolean removeAll(final Collection<?> c) {
            throw newUnmodifiableException();
        }

        @Override
        public boolean removeAll(final LongCollection c) {
            throw newUnmodifiableException();
        }

        @Override
        public void clear() {
            throw newUnmodifiableException();
        }

    }

    private final class EntrySet implements ObjectSet<Map.Entry<Long, V>> {

        private final Set<Map.Entry<Long, V>> delegate = m.entrySet();

        @Override
        public int size() {
            return StripedNonBlockingHashMapLong.this.size();
        }

        @Override
        public boolean isEmpty() {
            return StripedNonBlockingHashMapLong.this.isEmpty();
        }

        @Override
        public boolean contains(final Object o) {
            return delegate.contains(o);
        }

        @Override
        public ObjectIterator<Map.Entry<Long, V>> iterator() {
            final Iterator<Map.Entry<Long, V>> it = delegate.iterator();
            return new IObjectIterator<Map.Entry<Long, V>>() {
                @Override
                public boolean hasNext() {
                    return it.hasNext();
                }

                @Override
                public Map.Entry<Long, V> next() {
                    return it.next();
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
        public Object[] toArray() {
            return delegate.toArray();
        }

        @Override
        public <T> T[] toArray(final T[] a) {
            return delegate.toArray(a);
        }

        @Override
        public boolean add(final Map.Entry<Long, V> e) {
            throw newUnmodifiableException();
        }

        @Override
        public boolean remove(final Object o) {
            throw newUnmodifiableException();
        }

        @Override
        public boolean containsAll(final Collection<?> c) {
            return delegate.containsAll(c);
        }

        @Override
        public boolean addAll(final Collection<? extends Map.Entry<Long, V>> c) {
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

    private final class Long2ObjectEntrySet implements ObjectSet<Long2ObjectMap.Entry<V>> {

        private final Set<Map.Entry<Long, V>> delegate = m.entrySet();

        @Override
        public int size() {
            return StripedNonBlockingHashMapLong.this.size();
        }

        @Override
        public boolean isEmpty() {
            return StripedNonBlockingHashMapLong.this.isEmpty();
        }

        @Override
        public boolean contains(final Object o) {
            return delegate.contains(o);
        }

        @Override
        public ObjectIterator<Long2ObjectMap.Entry<V>> iterator() {
            final Iterator<Map.Entry<Long, V>> it = delegate.iterator();
            return new IObjectIterator<Long2ObjectMap.Entry<V>>() {
                @Override
                public boolean hasNext() {
                    return it.hasNext();
                }

                @Override
                public Long2ObjectMap.Entry<V> next() {
                    final Map.Entry<Long, V> entry = it.next();
                    return new Long2ObjectMap.Entry<V>() {
                        @Override
                        public Long getKey() {
                            return entry.getKey();
                        }

                        @Override
                        public long getLongKey() {
                            return entry.getKey();
                        }

                        @Override
                        public V getValue() {
                            return entry.getValue();
                        }

                        @Override
                        public V setValue(final V value) {
                            return entry.setValue(value);
                        }

                    };
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
        public Object[] toArray() {
            return delegate.toArray();
        }

        @Override
        public <T> T[] toArray(final T[] a) {
            return delegate.toArray(a);
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
        public boolean containsAll(final Collection<?> c) {
            return delegate.containsAll(c);
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