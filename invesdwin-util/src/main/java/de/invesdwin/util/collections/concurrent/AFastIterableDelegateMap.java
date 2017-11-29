package de.invesdwin.util.collections.concurrent;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.bean.tuple.ImmutableEntry;
import de.invesdwin.util.collections.delegate.ADelegateMap;
import de.invesdwin.util.collections.iterable.buffer.BufferingIterator;

/**
 * Boosts the iteration speed over the values by keeping a fast iterator instance that only gets modified when changes
 * to the map occur.
 * 
 * The iterator returned from this map is also suitable for concurrent modification during iteration.
 */
@ThreadSafe
public abstract class AFastIterableDelegateMap<K, V> extends ADelegateMap<K, V> {

    //arraylist wins in raw iterator speed compared to bufferingIterator since no remove is needed, though we need protection against concurrent modification
    @GuardedBy("this")
    private BufferingIterator<Entry<K, V>> fastIterable;
    @GuardedBy("this")
    private boolean empty;
    @GuardedBy("this")
    private int size;

    @GuardedBy("this")
    private Entry<K, V>[] entryArray;
    @GuardedBy("this")
    private K[] keyArray;
    @GuardedBy("this")
    private V[] valueArray;
    private final Set<Entry<K, V>> entrySet = new Set<Entry<K, V>>() {
        @Override
        public int size() {
            return AFastIterableDelegateMap.this.size();
        }

        @Override
        public boolean isEmpty() {
            return AFastIterableDelegateMap.this.isEmpty();
        }

        @Override
        public boolean contains(final Object o) {
            return getDelegate().entrySet().contains(o);
        }

        @Override
        public Iterator<Entry<K, V>> iterator() {
            synchronized (AFastIterableDelegateMap.this) {
                if (fastIterable == null) {
                    fastIterable = new BufferingIterator<Entry<K, V>>(getDelegate().entrySet());
                }
                return fastIterable.iterator();
            }
        }

        @Override
        public Object[] toArray() {
            return getDelegate().entrySet().toArray();
        }

        @Override
        public <T> T[] toArray(final T[] a) {
            return getDelegate().entrySet().toArray(a);
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
            return getDelegate().entrySet().containsAll(c);
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
    };

    private final Set<K> keySet = new Set<K>() {
        @Override
        public int size() {
            return AFastIterableDelegateMap.this.size();
        }

        @Override
        public boolean isEmpty() {
            return AFastIterableDelegateMap.this.isEmpty();
        }

        @Override
        public boolean contains(final Object o) {
            return getDelegate().containsKey(o);
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
            return getDelegate().keySet().toArray();
        }

        @Override
        public <T> T[] toArray(final T[] a) {
            return getDelegate().keySet().toArray(a);
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
            return getDelegate().keySet().containsAll(c);
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
    };

    private final Collection<V> values = new Collection<V>() {
        @Override
        public int size() {
            return AFastIterableDelegateMap.this.size();
        }

        @Override
        public boolean isEmpty() {
            return AFastIterableDelegateMap.this.isEmpty();
        }

        @Override
        public boolean contains(final Object o) {
            return getDelegate().containsValue(o);
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
            return getDelegate().values().toArray();
        }

        @Override
        public <T> T[] toArray(final T[] a) {
            return getDelegate().values().toArray(a);
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
            return getDelegate().values().containsAll(c);
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
    };

    public AFastIterableDelegateMap() {
        refreshFastIterable();
    }

    @Override
    public V put(final K key, final V value) {
        final V prev = super.put(key, value);
        synchronized (this) {
            if (prev == null) {
                addToFastIterable(key, value);
            } else if (prev != value) {
                refreshFastIterable();
            }
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
        empty = false;
        size++;
    }

    @Override
    public synchronized void clear() {
        super.clear();
        fastIterable = new BufferingIterator<Entry<K, V>>();
        entryArray = null;
        keyArray = null;
        valueArray = null;
        empty = true;
        size = 0;
    }

    @Override
    public synchronized V remove(final Object key) {
        final V removed = super.remove(key);
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
        size = getDelegate().size();
        empty = size == 0;
    }

    @Override
    public synchronized boolean remove(final Object key, final Object value) {
        final boolean removed = super.remove(key, value);
        if (removed) {
            refreshFastIterable();
        }
        return removed;
    }

    @Override
    public Collection<V> values() {
        return values;
    }

    @SuppressWarnings("unchecked")
    public synchronized V[] asValueArray(final Class<V> valueType) {
        if (valueArray == null) {
            final V[] empty = (V[]) Array.newInstance(valueType, size);
            valueArray = values.toArray(empty);
        }
        return valueArray;
    }

    @Override
    public Set<K> keySet() {
        return keySet;
    }

    @SuppressWarnings("unchecked")
    public synchronized K[] asKeyArray(final Class<K> keyType) {
        if (keyArray == null) {
            final K[] empty = (K[]) Array.newInstance(keyType, size);
            keyArray = keySet.toArray(empty);
        }
        return keyArray;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return entrySet;
    }

    @SuppressWarnings("unchecked")
    public synchronized Entry<K, V>[] asEntryArray() {
        if (entryArray == null) {
            final Entry<K, V>[] empty = (Entry<K, V>[]) Array.newInstance(Entry.class, size);
            entryArray = entrySet.toArray(empty);
        }
        return entryArray;
    }

    @Override
    public synchronized boolean isEmpty() {
        return empty;
    }

    @Override
    public synchronized int size() {
        return size;
    }

    private UnsupportedOperationException newUnmodifiableException() {
        return new UnsupportedOperationException(
                "Unmodifiable, only size/isEmpty/contains/containsAll/iterator/toArray methods supported");
    }

}
