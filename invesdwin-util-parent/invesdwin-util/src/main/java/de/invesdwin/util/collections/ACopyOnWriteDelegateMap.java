package de.invesdwin.util.collections;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.annotation.concurrent.ThreadSafe;

/**
 * A thread-safe version of {@link Map} in which all operations that change the Map are implemented by making a new copy
 * of the underlying Map.
 *
 * While the creation of a new Map can be expensive, this class is designed for cases in which the primary function is
 * to read data from the Map, not to modify the Map. Therefore the operations that do not cause a change to this class
 * happen quickly and concurrently.
 *
 */
@ThreadSafe
public abstract class ACopyOnWriteDelegateMap<K, V> implements Map<K, V>, Cloneable {
    private volatile Map<K, V> delegate;

    /**
     * Creates a new instance of ACopyOnWriteDelegateMap.
     *
     */
    public ACopyOnWriteDelegateMap() {
        delegate = newDelegate(Collections.<K, V> emptyMap());
    }

    protected abstract Map<K, V> newDelegate(Map<K, V> data);

    /**
     * Adds the provided key and value to this map.
     *
     * @see java.util.Map#put(java.lang.Object, java.lang.Object)
     */
    @Override
    public V put(final K key, final V value) {
        synchronized (this) {
            final Map<K, V> newMap = newDelegate(delegate);
            final V val = newMap.put(key, value);
            delegate = newMap;
            return val;
        }
    }

    /**
     * Removed the value and key from this map based on the provided key.
     *
     * @see java.util.Map#remove(java.lang.Object)
     */
    @Override
    public V remove(final Object key) {
        synchronized (this) {
            final Map<K, V> newMap = newDelegate(delegate);
            final V val = newMap.remove(key);
            delegate = newMap;
            return val;
        }
    }

    /**
     * Inserts all the keys and values contained in the provided map to this map.
     *
     * @see java.util.Map#putAll(java.util.Map)
     */
    @Override
    public void putAll(final Map<? extends K, ? extends V> newData) {
        synchronized (this) {
            final Map<K, V> newMap = newDelegate(delegate);
            newMap.putAll(newData);
            delegate = newMap;
        }
    }

    /**
     * Removes all entries in this map.
     *
     * @see java.util.Map#clear()
     */
    @Override
    public void clear() {
        synchronized (this) {
            delegate = newDelegate(Collections.<K, V> emptyMap());
        }
    }

    // ==============================================
    // ==== Below are methods that do not modify ====
    // ====         the internal Maps            ====
    // ==============================================
    /**
     * @return the number of key/value pairs in this map.
     *
     * @see java.util.Map#size()
     */
    @Override
    public int size() {
        return delegate.size();
    }

    /**
     * @return true if this map is empty, otherwise false.
     *
     * @see java.util.Map#isEmpty()
     */
    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    /**
     * @return true if this map contains the provided key, otherwise this method return false.
     *
     * @see java.util.Map#containsKey(java.lang.Object)
     */
    @Override
    public boolean containsKey(final Object key) {
        return delegate.containsKey(key);
    }

    /**
     * @return true if this map contains the provided value, otherwise this method returns false.
     *
     * @see java.util.Map#containsValue(java.lang.Object)
     */
    @Override
    public boolean containsValue(final Object value) {
        return delegate.containsValue(value);
    }

    /**
     * @return the value associated with the provided key from this map.
     *
     * @see java.util.Map#get(java.lang.Object)
     */
    @Override
    public V get(final Object key) {
        return delegate.get(key);
    }

    /**
     * This method will return a read-only {@link Set}.
     */
    @Override
    public Set<K> keySet() {
        return delegate.keySet();
    }

    /**
     * This method will return a read-only {@link Collection}.
     */
    @Override
    public Collection<V> values() {
        return delegate.values();
    }

    /**
     * This method will return a read-only {@link Set}.
     */
    @Override
    public Set<Entry<K, V>> entrySet() {
        return delegate.entrySet();
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (final CloneNotSupportedException e) {
            throw new InternalError();
        }
    }
}
