package de.invesdwin.util.collections.fast.concurrent.locked.pre;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.Locks;
import de.invesdwin.util.lang.string.description.TextDescription;

@ThreadSafe
public abstract class APreLockedMap<K, V> implements Map<K, V> {

    protected final TextDescription iteratorName;
    /** The object to lock on, needed for List/SortedSet views */
    protected final ILock lock;

    private Set<K> keySet;
    private Set<Entry<K, V>> entrySet;
    private Collection<V> values;

    //-----------------------------------------------------------------------
    /**
     * Constructor that wraps (not copies).
     *
     * @param delegate
     *            the collection to decorate, must not be null
     * @throws NullPointerException
     *             if the collection is null
     */
    public APreLockedMap(final TextDescription iteratorName) {
        this.iteratorName = iteratorName;
        this.lock = Locks.newReentrantLock(getClass().getSimpleName());
    }

    /**
     * Constructor that wraps (not copies).
     *
     * @param collection
     *            the collection to decorate, must not be null
     * @param lock
     *            the lock object to use, must not be null
     * @throws NullPointerException
     *             if the collection or lock is null
     */
    public APreLockedMap(final TextDescription iteratorName, final ILock lock) {
        this.iteratorName = iteratorName;
        Assertions.checkNotNull(lock);
        this.lock = lock;
    }

    private APreLockedMap<K, V> getThis() {
        return this;
    }

    protected Collection<V> newValues() {
        return new APreLockedCollection<V>(iteratorName, lock) {
            @Override
            protected Collection<V> getPreLockedDelegate() {
                return getThis().getPreLockedDelegate().values();
            }
        };
    }

    protected Set<Entry<K, V>> newEntrySet() {
        return new APreLockedSet<Entry<K, V>>(iteratorName, lock) {
            @Override
            protected Set<Entry<K, V>> getPreLockedDelegate() {
                return getThis().getPreLockedDelegate().entrySet();
            }
        };
    }

    protected Set<K> newKeySet() {
        return new APreLockedSet<K>(iteratorName, lock) {
            @Override
            protected Set<K> getPreLockedDelegate() {
                return getThis().getPreLockedDelegate().keySet();
            }
        };
    }

    /**
     * Gets the collection being decorated.
     *
     * @return the decorated collection
     */
    protected abstract Map<K, V> getPreLockedDelegate();

    //-----------------------------------------------------------------------

    @Override
    public final void clear() {
        final Map<K, V> delegate = getPreLockedDelegate();
        try {
            delegate.clear();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public final boolean isEmpty() {
        final Map<K, V> delegate = getPreLockedDelegate();
        try {
            return delegate.isEmpty();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public final int size() {
        final Map<K, V> delegate = getPreLockedDelegate();
        try {
            return delegate.size();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public final boolean equals(final Object object) {
        final Map<K, V> delegate = getPreLockedDelegate();
        try {
            if (object == this) {
                return true;
            }
            return object == this || delegate.equals(object);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public final int hashCode() {
        final Map<K, V> delegate = getPreLockedDelegate();
        try {
            return delegate.hashCode();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public final String toString() {
        final Map<K, V> delegate = getPreLockedDelegate();
        try {
            return delegate.toString();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public final boolean containsKey(final Object key) {
        final Map<K, V> delegate = getPreLockedDelegate();
        try {
            return delegate.containsKey(key);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public final boolean containsValue(final Object value) {
        final Map<K, V> delegate = getPreLockedDelegate();
        try {
            return delegate.containsValue(value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public final V get(final Object key) {
        final Map<K, V> delegate = getPreLockedDelegate();
        try {
            return delegate.get(key);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public final V put(final K key, final V value) {
        final Map<K, V> delegate = getPreLockedDelegate();
        try {
            return delegate.put(key, value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public final V remove(final Object key) {
        final Map<K, V> delegate = getPreLockedDelegate();
        try {
            return delegate.remove(key);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public final void putAll(final Map<? extends K, ? extends V> m) {
        final Map<K, V> delegate = getPreLockedDelegate();
        try {
            delegate.putAll(m);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Set<K> keySet() {
        if (keySet == null) {
            keySet = newKeySet();
        }
        return keySet;
    }

    @Override
    public final Collection<V> values() {
        if (values == null) {
            values = newValues();
        }
        return values;
    }

    @Override
    public final Set<Entry<K, V>> entrySet() {
        if (entrySet == null) {
            entrySet = newEntrySet();
        }
        return entrySet;
    }

    @Override
    public final boolean remove(final Object key, final Object value) {
        final Map<K, V> delegate = getPreLockedDelegate();
        try {
            return delegate.remove(key, value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public final boolean replace(final K key, final V oldValue, final V newValue) {
        final Map<K, V> delegate = getPreLockedDelegate();
        try {
            return delegate.replace(key, oldValue, newValue);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public final V replace(final K key, final V value) {
        final Map<K, V> delegate = getPreLockedDelegate();
        try {
            return delegate.replace(key, value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public final V compute(final K key, final BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        final Map<K, V> delegate = getPreLockedDelegate();
        try {
            return delegate.compute(key, remappingFunction);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public final V computeIfAbsent(final K key, final Function<? super K, ? extends V> mappingFunction) {
        final Map<K, V> delegate = getPreLockedDelegate();
        try {
            return delegate.computeIfAbsent(key, mappingFunction);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public final V computeIfPresent(final K key,
            final BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        final Map<K, V> delegate = getPreLockedDelegate();
        try {
            return delegate.computeIfPresent(key, remappingFunction);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public final V getOrDefault(final Object key, final V defaultValue) {
        final Map<K, V> delegate = getPreLockedDelegate();
        try {
            return delegate.getOrDefault(key, defaultValue);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public final V putIfAbsent(final K key, final V value) {
        final Map<K, V> delegate = getPreLockedDelegate();
        try {
            return delegate.putIfAbsent(key, value);
        } finally {
            lock.unlock();
        }
    }

}
