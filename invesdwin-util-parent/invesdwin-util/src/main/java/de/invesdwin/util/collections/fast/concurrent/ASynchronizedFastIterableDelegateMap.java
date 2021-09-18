package de.invesdwin.util.collections.fast.concurrent;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.bean.tuple.ImmutableEntry;
import de.invesdwin.util.collections.fast.IFastIterableMap;
import de.invesdwin.util.collections.iterable.buffer.BufferingIterator;

/**
 * Boosts the iteration speed over the values by keeping a fast iterator instance that only gets modified when changes
 * to the map occur.
 * 
 * The iterator returned from this map is also suitable for concurrent modification during iteration.
 */
@ThreadSafe
public abstract class ASynchronizedFastIterableDelegateMap<K, V> implements IFastIterableMap<K, V> {

    //arraylist wins in raw iterator speed compared to bufferingIterator since no remove is needed, though we need protection against concurrent modification
    @GuardedBy("this")
    private transient BufferingIterator<Entry<K, V>> fastIterable;

    @GuardedBy("this")
    private transient Entry<K, V>[] entryArray;
    @GuardedBy("this")
    private transient K[] keyArray;
    @GuardedBy("this")
    private transient V[] valueArray;

    @GuardedBy("this")
    private final Map<K, V> delegate;

    private final Set<Entry<K, V>> entrySet = new EntrySet();

    private final Set<K> keySet = new KeySet();

    private final Collection<V> values = new ValuesCollection();

    protected ASynchronizedFastIterableDelegateMap(final Map<K, V> delegate) {
        this.delegate = newDelegate();
        refreshFastIterable();
    }

    public ASynchronizedFastIterableDelegateMap() {
        this.delegate = newDelegate();
        refreshFastIterable();
    }

    protected abstract Map<K, V> newDelegate();

    @Override
    public synchronized V put(final K key, final V value) {
        final V prev = delegate.put(key, value);
        if (prev == null) {
            addToFastIterable(key, value);
        } else if (prev != value) {
            refreshFastIterable();
        }
        return prev;
    }

    @Override
    public synchronized V putIfAbsent(final K key, final V value) {
        final V prev = delegate.putIfAbsent(key, value);
        if (prev == null) {
            addToFastIterable(key, value);
        } else if (prev != value) {
            refreshFastIterable();
        }
        return prev;
    }

    protected void addToFastIterable(final K key, final V value) {
        if (fastIterable != null) {
            fastIterable.add(ImmutableEntry.of(key, value));
        }
        entryArray = null;
        keyArray = null;
        valueArray = null;
    }

    @Override
    public synchronized void clear() {
        delegate.clear();
        fastIterable = new BufferingIterator<Entry<K, V>>();
        entryArray = null;
        keyArray = null;
        valueArray = null;
    }

    @Override
    public synchronized V remove(final Object key) {
        final V removed = delegate.remove(key);
        if (removed != null) {
            refreshFastIterable();
        }
        return removed;
    }

    /**
     * protected so it can be used inside addToFastIterable to refresh instead if desired by overriding
     */
    protected void refreshFastIterable() {
        fastIterable = null;
        entryArray = null;
        keyArray = null;
        valueArray = null;
    }

    @Override
    public synchronized boolean remove(final Object key, final Object value) {
        final boolean removed = delegate.remove(key, value);
        if (removed) {
            refreshFastIterable();
        }
        return removed;
    }

    @Override
    public Collection<V> values() {
        return values;
    }

    @Override
    public synchronized V[] asValueArray(final V[] emptyArray) {
        if (valueArray == null) {
            valueArray = values.toArray(emptyArray);
        }
        return valueArray;
    }

    @Override
    public Set<K> keySet() {
        return keySet;
    }

    @Override
    public synchronized K[] asKeyArray(final K[] emptyArray) {
        if (keyArray == null) {
            keyArray = keySet.toArray(emptyArray);
        }
        return keyArray;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return entrySet;
    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized Entry<K, V>[] asEntryArray() {
        if (entryArray == null) {
            final Entry<K, V>[] empty = (Entry<K, V>[]) Array.newInstance(Entry.class, delegate.size());
            entryArray = entrySet.toArray(empty);
        }
        return entryArray;
    }

    @Override
    public synchronized boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public synchronized int size() {
        return delegate.size();
    }

    private UnsupportedOperationException newUnmodifiableException() {
        return new UnsupportedOperationException(
                "Unmodifiable, only size/isEmpty/contains/containsAll/iterator/toArray methods supported");
    }

    @Override
    public synchronized boolean containsKey(final Object key) {
        return delegate.containsKey(key);
    }

    @Override
    public synchronized boolean containsValue(final Object value) {
        return delegate.containsValue(value);
    }

    @Override
    public synchronized V get(final Object key) {
        return delegate.get(key);
    }

    @Override
    public synchronized void putAll(final Map<? extends K, ? extends V> m) {
        delegate.putAll(m);
        refreshFastIterable();
    }

    @Override
    public synchronized String toString() {
        return delegate.toString();
    }

    private final class ValuesCollection implements Collection<V>, Serializable {
        @Override
        public int size() {
            return ASynchronizedFastIterableDelegateMap.this.size();
        }

        @Override
        public boolean isEmpty() {
            return ASynchronizedFastIterableDelegateMap.this.isEmpty();
        }

        @Override
        public boolean contains(final Object o) {
            synchronized (ASynchronizedFastIterableDelegateMap.this) {
                return delegate.containsValue(o);
            }
        }

        @Override
        public Iterator<V> iterator() {
            final Iterator<Entry<K, V>> iterator = entrySet.iterator();
            return new Iterator<V>() {
                @Override
                public boolean hasNext() {
                    return iterator.hasNext();
                }

                @Override
                public V next() {
                    return iterator.next().getValue();
                }

            };
        }

        @Override
        public Object[] toArray() {
            synchronized (ASynchronizedFastIterableDelegateMap.this) {
                return delegate.values().toArray();
            }
        }

        @Override
        public <T> T[] toArray(final T[] a) {
            synchronized (ASynchronizedFastIterableDelegateMap.this) {
                return delegate.values().toArray(a);
            }
        }

        @Override
        public boolean add(final V e) {
            throw newUnmodifiableException();
        }

        @Override
        public boolean remove(final Object o) {
            throw newUnmodifiableException();
        }

        @Override
        public boolean containsAll(final Collection<?> c) {
            synchronized (ASynchronizedFastIterableDelegateMap.this) {
                return delegate.values().containsAll(c);
            }
        }

        @Override
        public boolean addAll(final Collection<? extends V> c) {
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

    private final class KeySet implements Set<K>, Serializable {
        @Override
        public int size() {
            return ASynchronizedFastIterableDelegateMap.this.size();
        }

        @Override
        public boolean isEmpty() {
            return ASynchronizedFastIterableDelegateMap.this.isEmpty();
        }

        @Override
        public boolean contains(final Object o) {
            synchronized (ASynchronizedFastIterableDelegateMap.this) {
                return delegate.containsKey(o);
            }
        }

        @Override
        public Iterator<K> iterator() {
            final Iterator<Entry<K, V>> iterator = entrySet.iterator();
            return new Iterator<K>() {
                @Override
                public boolean hasNext() {
                    return iterator.hasNext();
                }

                @Override
                public K next() {
                    return iterator.next().getKey();
                }
            };
        }

        @Override
        public Object[] toArray() {
            synchronized (ASynchronizedFastIterableDelegateMap.this) {
                return delegate.keySet().toArray();
            }
        }

        @Override
        public <T> T[] toArray(final T[] a) {
            synchronized (ASynchronizedFastIterableDelegateMap.this) {
                return delegate.keySet().toArray(a);
            }
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
            synchronized (ASynchronizedFastIterableDelegateMap.this) {
                return delegate.keySet().containsAll(c);
            }
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

    private final class EntrySet implements Set<Entry<K, V>>, Serializable {
        @Override
        public int size() {
            return ASynchronizedFastIterableDelegateMap.this.size();
        }

        @Override
        public boolean isEmpty() {
            return ASynchronizedFastIterableDelegateMap.this.isEmpty();
        }

        @Override
        public boolean contains(final Object o) {
            synchronized (ASynchronizedFastIterableDelegateMap.this) {
                return delegate.entrySet().contains(o);
            }
        }

        @Override
        public Iterator<Entry<K, V>> iterator() {
            synchronized (ASynchronizedFastIterableDelegateMap.this) {
                if (fastIterable == null) {
                    fastIterable = new BufferingIterator<Entry<K, V>>();
                    for (final Entry<K, V> e : delegate.entrySet()) {
                        //koloboke reuses/resets its entries, thus we have to make a safe copy
                        fastIterable.add(ImmutableEntry.of(e.getKey(), e.getValue()));
                    }
                }
                return fastIterable.iterator();
            }
        }

        @Override
        public Object[] toArray() {
            synchronized (ASynchronizedFastIterableDelegateMap.this) {
                return delegate.entrySet().toArray();
            }
        }

        @Override
        public <T> T[] toArray(final T[] a) {
            synchronized (ASynchronizedFastIterableDelegateMap.this) {
                return delegate.entrySet().toArray(a);
            }
        }

        @Override
        public boolean add(final Entry<K, V> e) {
            throw newUnmodifiableException();
        }

        @Override
        public boolean remove(final Object o) {
            throw newUnmodifiableException();
        }

        @Override
        public boolean containsAll(final Collection<?> c) {
            synchronized (ASynchronizedFastIterableDelegateMap.this) {
                return delegate.entrySet().containsAll(c);
            }
        }

        @Override
        public boolean addAll(final Collection<? extends Entry<K, V>> c) {
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
