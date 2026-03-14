package de.invesdwin.util.collections.fast.concurrent.locked;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.bean.tuple.ImmutableEntry;
import de.invesdwin.util.bean.tuple.NodeImmutableEntry;
import de.invesdwin.util.collections.Collections;
import de.invesdwin.util.collections.fast.IFastIterableMap;
import de.invesdwin.util.collections.iterable.buffer.NodeBufferingIterator;
import de.invesdwin.util.collections.iterable.collection.ArrayCloseableIterator;
import de.invesdwin.util.collections.primitive.APrimitiveConcurrentMap;
import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.Locks;

/**
 * Boosts the iteration speed over the values by keeping a fast iterator instance that only gets modified when changes
 * to the map occur.
 * 
 * The iterator returned from this map is also suitable for concurrent modification during iteration.
 */
@ThreadSafe
public abstract class ALockedFastIterableDelegateMap<K, V> implements IFastIterableMap<K, V> {

    //arraylist wins in raw iterator speed compared to bufferingIterator since no remove is needed, though we need protection against concurrent modification
    @GuardedBy("lock")
    private transient NodeBufferingIterator<NodeImmutableEntry<K, V>> fastIterable;
    @GuardedBy("lock")
    private transient Entry<K, V>[] entryArray;
    @GuardedBy("lock")
    private transient K[] keyArray;
    @GuardedBy("lock")
    private transient V[] valueArray;
    @GuardedBy("lock")
    private final Map<K, V> delegate;
    private final ILock lock;
    private final Set<Entry<K, V>> entrySet = new EntrySet();
    private final Set<K> keySet = new KeySet();
    private final Collection<V> values = new ValuesCollection();

    protected ALockedFastIterableDelegateMap(final Map<K, V> delegate) {
        this.delegate = delegate;
        refreshFastIterable();
        this.lock = Locks.newReentrantLock(getClass().getSimpleName());
    }

    protected ALockedFastIterableDelegateMap(final Map<K, V> delegate, final ILock lock) {
        this.delegate = delegate;
        refreshFastIterable();
        this.lock = lock;
    }

    public ALockedFastIterableDelegateMap() {
        this.delegate = newDelegate();
        refreshFastIterable();
        this.lock = Locks.newReentrantLock(getClass().getSimpleName());
    }

    public ALockedFastIterableDelegateMap(final ILock lock) {
        this.delegate = newDelegate();
        refreshFastIterable();
        this.lock = lock;
    }

    protected abstract Map<K, V> newDelegate();

    @Override
    public V put(final K key, final V value) {
        lock.lock();
        try {
            final V prev = delegate.put(key, value);
            if (prev == null) {
                addToFastIterable(key, value);
            } else if (prev != value) {
                refreshFastIterable();
            }
            return prev;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public V putIfAbsent(final K key, final V value) {
        lock.lock();
        try {
            final V prev = delegate.putIfAbsent(key, value);
            if (prev == null) {
                addToFastIterable(key, value);
            } else if (prev != value) {
                refreshFastIterable();
            }
            return prev;
        } finally {
            lock.unlock();
        }
    }

    protected void addToFastIterable(final K key, final V value) {
        if (fastIterable != null) {
            fastIterable.add(NodeImmutableEntry.of(key, value));
        }
        entryArray = null;
        keyArray = null;
        valueArray = null;
    }

    @Override
    public void clear() {
        lock.lock();
        try {
            if (delegate.isEmpty()) {
                return;
            }
            delegate.clear();
            if (fastIterable != null) {
                fastIterable = new NodeBufferingIterator<NodeImmutableEntry<K, V>>();
            }
            entryArray = null;
            keyArray = null;
            valueArray = null;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public V remove(final Object key) {
        lock.lock();
        try {
            final V removed = delegate.remove(key);
            if (removed != null) {
                refreshFastIterable();
            }
            return removed;
        } finally {
            lock.unlock();
        }
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
    public boolean remove(final Object key, final Object value) {
        lock.lock();
        try {
            final boolean removed = delegate.remove(key, value);
            if (removed) {
                refreshFastIterable();
            }
            return removed;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Collection<V> values() {
        return values;
    }

    @Override
    public V[] asValueArray(final V[] emptyArray) {
        lock.lock();
        try {
            if (valueArray == null) {
                if (values.isEmpty()) {
                    assert emptyArray.length == 0 : "emptyArray.length needs to be 0: " + emptyArray.length;
                    valueArray = emptyArray;
                } else {
                    valueArray = onValueArrayCreated(values.toArray(emptyArray));
                }
            }
            return valueArray;
        } finally {
            lock.unlock();
        }
    }

    protected V[] onValueArrayCreated(final V[] array) {
        return array;
    }

    @Override
    public Set<K> keySet() {
        return keySet;
    }

    @Override
    public K[] asKeyArray(final K[] emptyArray) {
        lock.lock();
        try {
            if (keyArray == null) {
                if (keySet.isEmpty()) {
                    assert emptyArray.length == 0 : "emptyArray.length needs to be 0: " + emptyArray.length;
                    keyArray = emptyArray;
                } else {
                    keyArray = onKeyArrayCreated(keySet.toArray(emptyArray));
                }
            }
            return keyArray;
        } finally {
            lock.unlock();
        }
    }

    protected K[] onKeyArrayCreated(final K[] array) {
        return array;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return entrySet;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Entry<K, V>[] asEntryArray() {
        lock.lock();
        try {
            if (entryArray == null) {
                if (entrySet.isEmpty()) {
                    entryArray = Collections.EMPTY_ENTRY_ARRAY;
                } else {
                    final Entry<K, V>[] array = entrySet.toArray(Collections.EMPTY_ENTRY_ARRAY);
                    for (int i = 0; i < array.length; i++) {
                        final Entry<K, V> e = array[i];
                        //koloboke and other maps reuse/reset its entries, thus we have to make a safe copy
                        array[i] = ImmutableEntry.of(e.getKey(), e.getValue());
                    }
                    entryArray = onEntryArrayCreated(array);
                }
            }
            return entryArray;
        } finally {
            lock.unlock();
        }
    }

    protected Entry<K, V>[] onEntryArrayCreated(final Entry<K, V>[] array) {
        return array;
    }

    @Override
    public boolean isEmpty() {
        lock.lock();
        try {
            return delegate.isEmpty();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int size() {
        lock.lock();
        try {
            return delegate.size();
        } finally {
            lock.unlock();
        }
    }

    private UnsupportedOperationException newUnmodifiableException() {
        return APrimitiveConcurrentMap.newUnmodifiableException();
    }

    @Override
    public boolean containsKey(final Object key) {
        lock.lock();
        try {
            return delegate.containsKey(key);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean containsValue(final Object value) {
        lock.lock();
        try {
            return delegate.containsValue(value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public V get(final Object key) {
        lock.lock();
        try {
            return delegate.get(key);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void putAll(final Map<? extends K, ? extends V> m) {
        lock.lock();
        try {
            delegate.putAll(m);
            refreshFastIterable();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String toString() {
        lock.lock();
        try {
            return delegate.toString();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int hashCode() {
        lock.lock();
        try {
            return delegate.hashCode();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        lock.lock();
        try {
            return delegate.equals(obj);
        } finally {
            lock.unlock();
        }
    }

    private final class ValuesCollection implements Collection<V>, Serializable {
        @Override
        public int size() {
            return ALockedFastIterableDelegateMap.this.size();
        }

        @Override
        public boolean isEmpty() {
            return ALockedFastIterableDelegateMap.this.isEmpty();
        }

        @Override
        public boolean contains(final Object o) {
            lock.lock();
            try {
                return delegate.containsValue(o);
            } finally {
                lock.unlock();
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
            lock.lock();
            try {
                return delegate.values().toArray();
            } finally {
                lock.unlock();
            }
        }

        @Override
        public <T> T[] toArray(final T[] a) {
            lock.lock();
            try {
                return delegate.values().toArray(a);
            } finally {
                lock.unlock();
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
            lock.lock();
            try {
                return delegate.values().containsAll(c);
            } finally {
                lock.unlock();
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

        @Override
        public int hashCode() {
            lock.lock();
            try {
                return delegate.values().hashCode();
            } finally {
                lock.unlock();
            }
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj == this) {
                return true;
            }
            lock.lock();
            try {
                return delegate.values().equals(obj);
            } finally {
                lock.unlock();
            }
        }
    }

    private final class KeySet implements Set<K>, Serializable {
        @Override
        public int size() {
            return ALockedFastIterableDelegateMap.this.size();
        }

        @Override
        public boolean isEmpty() {
            return ALockedFastIterableDelegateMap.this.isEmpty();
        }

        @Override
        public boolean contains(final Object o) {
            lock.lock();
            try {
                return delegate.containsKey(o);
            } finally {
                lock.unlock();
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
            lock.lock();
            try {
                return delegate.keySet().toArray();
            } finally {
                lock.unlock();
            }
        }

        @Override
        public <T> T[] toArray(final T[] a) {
            lock.lock();
            try {
                return delegate.keySet().toArray(a);
            } finally {
                lock.unlock();
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
            lock.lock();
            try {
                return delegate.keySet().containsAll(c);
            } finally {
                lock.unlock();
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

        @Override
        public int hashCode() {
            lock.lock();
            try {
                return delegate.keySet().hashCode();
            } finally {
                lock.unlock();
            }
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj == this) {
                return true;
            }
            lock.lock();
            try {
                return delegate.keySet().equals(obj);
            } finally {
                lock.unlock();
            }
        }
    }

    private final class EntrySet implements Set<Entry<K, V>>, Serializable {
        @Override
        public int size() {
            return ALockedFastIterableDelegateMap.this.size();
        }

        @Override
        public boolean isEmpty() {
            return ALockedFastIterableDelegateMap.this.isEmpty();
        }

        @Override
        public boolean contains(final Object o) {
            lock.lock();
            try {
                return delegate.entrySet().contains(o);
            } finally {
                lock.unlock();
            }
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        @Override
        public Iterator<Entry<K, V>> iterator() {
            lock.lock();
            try {
                if (entryArray != null) {
                    return new ArrayCloseableIterator<>(entryArray);
                }
                if (fastIterable == null) {
                    fastIterable = new NodeBufferingIterator<NodeImmutableEntry<K, V>>();
                    for (final Entry<K, V> e : delegate.entrySet()) {
                        //koloboke and other maps reuse/reset its entries, thus we have to make a safe copy
                        fastIterable.add(NodeImmutableEntry.of(e.getKey(), e.getValue()));
                    }
                }
                return (Iterator) fastIterable.iterator();
            } finally {
                lock.unlock();
            }
        }

        @Override
        public Object[] toArray() {
            lock.lock();
            try {
                return delegate.entrySet().toArray();
            } finally {
                lock.unlock();
            }
        }

        @Override
        public <T> T[] toArray(final T[] a) {
            lock.lock();
            try {
                return delegate.entrySet().toArray(a);
            } finally {
                lock.unlock();
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
            lock.lock();
            try {
                return delegate.entrySet().containsAll(c);
            } finally {
                lock.unlock();
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

        @Override
        public int hashCode() {
            lock.lock();
            try {
                return delegate.entrySet().hashCode();
            } finally {
                lock.unlock();
            }
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj == this) {
                return true;
            }
            lock.lock();
            try {
                return delegate.entrySet().equals(obj);
            } finally {
                lock.unlock();
            }
        }
    }

}
