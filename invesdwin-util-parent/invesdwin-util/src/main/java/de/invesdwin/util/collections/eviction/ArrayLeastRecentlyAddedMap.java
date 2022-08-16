package de.invesdwin.util.collections.eviction;

import java.util.Collection;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.error.FastNoSuchElementException;
import de.invesdwin.util.math.Integers;

/**
 * This implemetation requires a lot less memory while being almost as fast as the commons implementation
 */
@NotThreadSafe
public class ArrayLeastRecentlyAddedMap<K, V> implements Map<K, V>, IEvictionMap<K, V> {

    private static final int EVICTION_SIZE_MULTIPLIER = 2;

    private int maximumSize;
    private int evictionSize;
    private Object[] orderedKeys;
    private int leastRecentlyAddedKeyIndex = -1;
    private int mostRecentlyAddedKeyIndex = -1;
    private final Map<K, V> map;

    public ArrayLeastRecentlyAddedMap(final int maximumSize) {
        setMaximumSize(maximumSize);
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
        if (mostRecentlyAddedKeyIndex >= evictionSize) {
            mostRecentlyAddedKeyIndex = 0;
        }
        if (orderedKeys.length <= mostRecentlyAddedKeyIndex) {
            final Object[] oldArray = orderedKeys;
            orderedKeys = new Object[Integers.max(1, Integers.min(evictionSize, oldArray.length * 2))];
            System.arraycopy(oldArray, 0, orderedKeys, 0, oldArray.length);
        }
        if (map.size() > evictionSize) {
            while (map.size() > maximumSize) {
                do {
                    leastRecentlyAddedKeyIndex++;
                    if (leastRecentlyAddedKeyIndex >= evictionSize
                            || leastRecentlyAddedKeyIndex >= orderedKeys.length) {
                        leastRecentlyAddedKeyIndex = 0;
                    }
                    //jump over removed keys
                } while (orderedKeys[leastRecentlyAddedKeyIndex] == null);
                map.remove(orderedKeys[leastRecentlyAddedKeyIndex]);
                orderedKeys[leastRecentlyAddedKeyIndex] = null;
            }
            if (map.size() > evictionSize) {
                //ordered keys are async and map is too large, reset everything
                clear();
                return;
            }
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
    public V remove(final Object key) {
        final V removed = map.remove(key);
        if (removed != null) {
            if (map.isEmpty()) {
                orderedKeys = new Object[0];
                leastRecentlyAddedKeyIndex = -1;
                mostRecentlyAddedKeyIndex = -1;
            } else {
                try {
                    final int index = findIndex(key);
                    if (index == mostRecentlyAddedKeyIndex) {
                        orderedKeys[mostRecentlyAddedKeyIndex] = null;
                        mostRecentlyAddedKeyIndex--;
                    } else {
                        orderedKeys[index] = null;
                    }
                } catch (final NoSuchElementException e) {
                    //the ordered keys got async and the map has grown too much, reset everything
                    if (map.size() > evictionSize) {
                        clear();
                    }
                }
            }
        }
        return removed;
    }

    private int findIndex(final Object key) {
        for (int i = 0; i < orderedKeys.length; i++) {
            final Object cur = orderedKeys[i];
            if (cur != null && cur.equals(key)) {
                return i;
            }
        }
        throw new FastNoSuchElementException("Key not found");
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
        this.evictionSize = maximumSize * EVICTION_SIZE_MULTIPLIER;
    }

    @Override
    public int getMaximumSize() {
        return maximumSize;
    }

}
