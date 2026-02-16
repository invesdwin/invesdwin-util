package de.invesdwin.util.collections.primitive.objkey.striped;

import java.util.AbstractMap;
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
import org.jctools.maps.NonBlockingIdentityHashMap;
import org.jspecify.annotations.Nullable;

import de.invesdwin.util.collections.primitive.APrimitiveConcurrentMap;
import de.invesdwin.util.collections.primitive.IPrimitiveConcurrentMap;
import de.invesdwin.util.collections.primitive.PrimitiveConcurrentMapConfig;
import de.invesdwin.util.collections.primitive.longkey.ConcurrentLong2ObjectMap;
import de.invesdwin.util.collections.primitive.util.BucketHashUtil;
import de.invesdwin.util.concurrent.lock.ICloseableLock;
import de.invesdwin.util.concurrent.lock.padded.PaddedCloseableReentrantLock;
import de.invesdwin.util.concurrent.lock.strategy.ILockingStrategy;
import it.unimi.dsi.fastutil.objects.Object2ObjectFunction;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;

/**
 * Similar to {@link ConcurrentLong2ObjectMap}, but backed with NonBlockingHashMapLong ⇒ non-blocking reads 🚀
 * https://github.com/JCTools/JCTools/blob/master/jctools-core/src/main/java/org/jctools/maps/NonBlockingHashMapLong.java
 * https://stackoverflow.com/questions/61721386/caffeine-cache-specify-expiry-for-an-entry
 * 
 * @see org.jctools.maps.NonBlockingIdentityHashMap
 * @see it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 * @see com.google.common.util.concurrent.Striped
 * 
 * @see AStripedNonBlockingIdentityHashMapCacheExpirer
 */
@ThreadSafe
public class StripedNonBlockingIdentityHashMap<K, V> extends AbstractMap<K, V>
        implements ConcurrentMap<K, V>, Object2ObjectMap<K, V>, IPrimitiveConcurrentMap, Iterable<K> {
    private final ILockingStrategy lockingStrategy;
    private final NonBlockingIdentityHashMap<K, V> m;
    /** @see com.google.common.util.concurrent.Striped#lock(int) */
    private final PaddedCloseableReentrantLock[] s;
    private final ObjectSet<Map.Entry<K, V>> entrySet;
    private final ObjectSet<Object2ObjectMap.Entry<K, V>> object2ObjectEntrySet;
    private final ObjectSet<K> keySet;
    private final ObjectCollection<V> values;

    public StripedNonBlockingIdentityHashMap() {
        this(PrimitiveConcurrentMapConfig.DEFAULT);
    }

    public StripedNonBlockingIdentityHashMap(final PrimitiveConcurrentMapConfig config) {
        final int concurrencyLevel = config.getConcurrencyLevel();
        assert concurrencyLevel > 0 : "Stripes must be positive, but " + concurrencyLevel;
        assert concurrencyLevel < 100_000_000 : "Too many stripes: " + concurrencyLevel;
        this.lockingStrategy = config.getLockingStrategy();
        m = new NonBlockingIdentityHashMap<K, V>(Math.max(config.getInitialCapacity(), concurrencyLevel));
        s = new PaddedCloseableReentrantLock[concurrencyLevel];
        for (int i = 0; i < concurrencyLevel; i++) {
            s[i] = new PaddedCloseableReentrantLock();
        }
        this.entrySet = new EntrySet();
        this.object2ObjectEntrySet = new Object2ObjectEntrySet();
        this.keySet = new KeySet();
        this.values = new ValuesCollection();
    }//new

    /** @see com.google.common.util.concurrent.Striped#get(Object) */
    protected ICloseableLock write(final Object key) {
        final PaddedCloseableReentrantLock lock = s[BucketHashUtil.bucket(key, s.length)];
        return lock.locked(lockingStrategy);
    }

    @Override
    public int hashCode() {
        return m.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        return m.equals(obj);
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
        withAllKeysWriteLock(map -> map.clear());
    }

    //CHECKSTYLE:OFF
    public void withAllKeysWriteLock(final Consumer<NonBlockingIdentityHashMap<K, V>> singleThreadMapModifier) {
        //CHECKSTYLE:ON
        for (final PaddedCloseableReentrantLock paddedLock : s) {
            lockingStrategy.lock(paddedLock);
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

    /** @see NonBlockingHashMapLong.IteratorLong */
    public static class StripedKeyIterator<K> implements IObjectIterator<K> {
        private final StripedNonBlockingIdentityHashMap<K, ?> owner;
        private final Iterator<K> it;
        private K seenKey;// ^ safe for concurrent

        @SuppressWarnings("unchecked")
        public StripedKeyIterator(final StripedNonBlockingIdentityHashMap<K, ?> owner) {
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
        return keySet;
    }

    private UnsupportedOperationException newUnmodifiableException() {
        return APrimitiveConcurrentMap.newUnmodifiableException();
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
    public ObjectSet<Map.Entry<K, V>> entrySet() {
        return entrySet;
    }

    @Override
    public V merge(final K key, final V value, final BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        return ConcurrentMap.super.merge(key, value, remappingFunction);
    }

    @Override
    public V compute(final K key, final BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return ConcurrentMap.super.compute(key, remappingFunction);
    }

    @Override
    public V computeIfPresent(final K key, final BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return ConcurrentMap.super.computeIfPresent(key, remappingFunction);
    }

    @Override
    public V computeIfAbsent(final K key, final Function<? super K, ? extends V> mappingFunction) {
        V v = m.get(key);
        if (v == null) {
            //bad idea to synchronize in apply, this might cause deadlocks when threads are used inside of it
            v = mappingFunction.apply(key);
            if (v != null) {
                try (ICloseableLock lock = write(key)) {
                    final V oldV = m.get(key);
                    if (oldV != null) {
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
    public V computeIfAbsent(final K key, final Object2ObjectFunction<? super K, ? extends V> mappingFunction) {
        V v = m.get(key);
        if (v == null) {
            //bad idea to synchronize in apply, this might cause deadlocks when threads are used inside of it
            v = mappingFunction.apply(key);
            if (v != null) {
                try (ICloseableLock lock = write(key)) {
                    final V oldV = m.get(key);
                    if (oldV != null) {
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
    public void replaceAll(final BiFunction<? super K, ? super V, ? extends V> function) {
        withAllKeysWriteLock(map -> map.replaceAll(function));
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

    @Deprecated
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
        return object2ObjectEntrySet;
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
            return StripedNonBlockingIdentityHashMap.this.size();
        }

        @Override
        public boolean isEmpty() {
            return StripedNonBlockingIdentityHashMap.this.isEmpty();
        }

        @Override
        public boolean contains(final Object o) {
            return containsValue(o);
        }

        @Override
        public IObjectIterator<V> iterator() {
            final Iterator<Map.Entry<K, V>> it = entrySet.iterator();
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

    private final class KeySet implements ObjectSet<K> {

        private final Set<K> delegate = m.keySet();

        @Override
        public int size() {
            return StripedNonBlockingIdentityHashMap.this.size();
        }

        @Override
        public boolean isEmpty() {
            return StripedNonBlockingIdentityHashMap.this.isEmpty();
        }

        @Override
        public boolean contains(final Object o) {
            return containsKey(o);
        }

        @Override
        public ObjectIterator<K> iterator() {
            final Iterator<K> iterator = delegate.iterator();
            return new IObjectIterator<K>() {

                private K seenKey;

                @Override
                public boolean hasNext() {
                    return iterator.hasNext();
                }

                @Override
                public K next() {
                    seenKey = iterator.next();
                    return seenKey;
                }

                @Override
                public void remove() {
                    try (ICloseableLock lock = write(seenKey)) {
                        iterator.remove();
                    }
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
        public boolean add(final K e) {
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

    private final class EntrySet implements ObjectSet<Map.Entry<K, V>> {

        private final Set<Map.Entry<K, V>> delegate = m.entrySet();

        @Override
        public int size() {
            return StripedNonBlockingIdentityHashMap.this.size();
        }

        @Override
        public boolean isEmpty() {
            return StripedNonBlockingIdentityHashMap.this.isEmpty();
        }

        @Override
        public boolean contains(final Object o) {
            return delegate.contains(o);
        }

        @Override
        public ObjectIterator<Map.Entry<K, V>> iterator() {
            final Iterator<Map.Entry<K, V>> it = delegate.iterator();
            return new IObjectIterator<Map.Entry<K, V>>() {
                private Map.Entry<K, V> seenEntry;

                @Override
                public boolean hasNext() {
                    return it.hasNext();
                }

                @Override
                public Map.Entry<K, V> next() {
                    seenEntry = it.next();
                    return seenEntry;
                }

                @Override
                public void remove() {
                    try (ICloseableLock lock = write(seenEntry.getKey())) {
                        it.remove();
                    }
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
        public boolean add(final Map.Entry<K, V> e) {
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
        public boolean addAll(final Collection<? extends Map.Entry<K, V>> c) {
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

    private final class Object2ObjectEntrySet implements ObjectSet<Object2ObjectMap.Entry<K, V>> {

        private final Set<Map.Entry<K, V>> delegate = m.entrySet();

        @Override
        public int size() {
            return StripedNonBlockingIdentityHashMap.this.size();
        }

        @Override
        public boolean isEmpty() {
            return StripedNonBlockingIdentityHashMap.this.isEmpty();
        }

        @Override
        public boolean contains(final Object o) {
            return delegate.contains(o);
        }

        @Override
        public ObjectIterator<Object2ObjectMap.Entry<K, V>> iterator() {
            final Iterator<Map.Entry<K, V>> it = delegate.iterator();
            return new IObjectIterator<Object2ObjectMap.Entry<K, V>>() {
                private Map.Entry<K, V> seenEntry;

                @Override
                public boolean hasNext() {
                    return it.hasNext();
                }

                @Override
                public Object2ObjectMap.Entry<K, V> next() {
                    final Map.Entry<K, V> entry = it.next();
                    seenEntry = entry;
                    return new Object2ObjectMap.Entry<K, V>() {
                        @Override
                        public K getKey() {
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
                    try (ICloseableLock lock = write(seenEntry.getKey())) {
                        it.remove();
                    }
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
        public boolean add(final Object2ObjectMap.Entry<K, V> e) {
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

    /** Simple template to add an expiration support */
    public abstract static class AStripedNonBlockingIdentityHashMapCacheExpirer<K, V> {
        protected final StripedNonBlockingIdentityHashMap<K, V> cacheMap;

        /**
         * Heuristic: {@link #expire()} is called from a single thread (scheduler) when working correctly:
         * Atomic/volatile is not needed
         */
        protected long expiredCount;

        public AStripedNonBlockingIdentityHashMapCacheExpirer(final StripedNonBlockingIdentityHashMap<K, V> cacheMap) {
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
         * @see StripedNonBlockingIdentityHashMap#forEachKey(LongConsumer)
         * @see StripedNonBlockingIdentityHashMap#withLock
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