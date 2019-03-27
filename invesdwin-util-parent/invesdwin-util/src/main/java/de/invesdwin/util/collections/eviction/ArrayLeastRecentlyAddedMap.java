package de.invesdwin.util.collections.eviction;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.math.Integers;

/**
 * This implemetation requires a lot less memory while being almost as fast as the commons implementation
 */
@NotThreadSafe
public class ArrayLeastRecentlyAddedMap<K, V> implements Map<K, V>, IEvictionMap<K, V> {

    private int maximumSize;
    private Object[] orderedKeys;
    private int leastRecentlyAddedKeyIndex = -1;
    private int mostRecentlyAddedKeyIndex = -1;
    private final Map<K, V> map;

    public ArrayLeastRecentlyAddedMap(final int maximumSize) {
        this.maximumSize = maximumSize;
        this.orderedKeys = new Object[0];
        this.map = newMap();
    }

    protected Map<K, V> newMap() {
        return ILockCollectionFactory.getInstance(false).newMap();
    }

    @Override
    public void clear() {
        orderedKeys = new Object[0];
        leastRecentlyAddedKeyIndex = -1;
        mostRecentlyAddedKeyIndex = -1;
        map.clear();
    }

    @Override
    public boolean containsKey(final Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(final Object key) {
        return map.containsValue(key);
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return map.entrySet();
    }

    @Override
    public V get(final Object key) {
        return map.get(key);
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public Set<K> keySet() {
        return map.keySet();
    }

    @Override
    public V put(final K key, final V value) {
        final V put = map.put(key, value);
        if (put == null) {
            updateMostRecentlyAdded(key);
        }
        return put;
    }

    private void updateMostRecentlyAdded(final K key) {
        mostRecentlyAddedKeyIndex++;
        if (mostRecentlyAddedKeyIndex >= maximumSize) {
            mostRecentlyAddedKeyIndex = 0;
        }
        if (orderedKeys.length <= mostRecentlyAddedKeyIndex) {
            final Object[] oldArray = orderedKeys;
            orderedKeys = new Object[Integers.max(1, Integers.min(maximumSize, oldArray.length * 2))];
            System.arraycopy(oldArray, 0, orderedKeys, 0, oldArray.length);
        }
        while (map.size() > maximumSize) {
            do {
                leastRecentlyAddedKeyIndex++;
                if (leastRecentlyAddedKeyIndex >= maximumSize) {
                    leastRecentlyAddedKeyIndex = 0;
                }
                //jump over removed keys
            } while (orderedKeys[leastRecentlyAddedKeyIndex] == null);
            map.remove(orderedKeys[leastRecentlyAddedKeyIndex]);
            orderedKeys[leastRecentlyAddedKeyIndex] = null;
        }
        orderedKeys[mostRecentlyAddedKeyIndex] = key;
    }

    @Override
    public V putIfAbsent(final K key, final V value) {
        final V put = map.putIfAbsent(key, value);
        if (put == null) {
            updateMostRecentlyAdded(key);
        }
        return put;
    }

    @Override
    public void putAll(final Map<? extends K, ? extends V> key) {
        for (final Entry<? extends K, ? extends V> e : key.entrySet()) {
            put(e.getKey(), e.getValue());
        }
    }

    @Override
    public V remove(final Object arg0) {
        final V removed = map.remove(arg0);
        if (removed != null) {
            if (map.isEmpty()) {
                orderedKeys = new Object[0];
                leastRecentlyAddedKeyIndex = -1;
                mostRecentlyAddedKeyIndex = -1;
            } else {
                final int index = findIndex(arg0);
                if (index == mostRecentlyAddedKeyIndex) {
                    orderedKeys[mostRecentlyAddedKeyIndex] = null;
                    mostRecentlyAddedKeyIndex--;
                } else {
                    orderedKeys[index] = null;
                }
            }
        }
        return removed;
    }

    private int findIndex(final Object arg0) {
        for (int i = 0; i < orderedKeys.length; i++) {
            final Object cur = orderedKeys[i];
            if (cur != null && cur.equals(arg0)) {
                return i;
            }
        }
        throw new IllegalStateException("Key not found: " + arg0);
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public Collection<V> values() {
        return map.values();
    }

    @Override
    public EvictionMode getEvictionMode() {
        return EvictionMode.LeastRecentlyAdded;
    }

    @Override
    public void setMaximumSize(final int maximumSize) {
        this.maximumSize = maximumSize;
    }

    @Override
    public int getMaximumSize() {
        return maximumSize;
    }

}
