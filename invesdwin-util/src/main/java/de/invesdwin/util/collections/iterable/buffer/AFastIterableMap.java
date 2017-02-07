package de.invesdwin.util.collections.iterable.buffer;

import java.util.Collection;
import java.util.Iterator;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.ADelegateMap;

/**
 * Boosts the iteration speed over the values by keeping a fast iterator instance that only gets modified when changes
 * to the map occur.
 */
@ThreadSafe
public abstract class AFastIterableMap<K, V> extends ADelegateMap<K, V> implements Iterable<V> {

    private volatile BufferingIterator<V> fastIterable = new BufferingIterator<V>();

    @Override
    public synchronized V put(final K key, final V value) {
        final V prev = super.put(key, value);
        if (prev == null) {
            fastIterable.add(value);
        }
        return prev;
    }

    @Override
    public synchronized void clear() {
        super.clear();
        fastIterable = new BufferingIterator<V>();
    }

    @Override
    public synchronized V remove(final Object key) {
        final V removed = super.remove(key);
        if (removed != null) {
            refreshFastIterator();
        }
        return removed;
    }

    private void refreshFastIterator() {
        fastIterable = new BufferingIterator<V>(getDelegate().values());
    }

    @Override
    public synchronized boolean remove(final Object key, final Object value) {
        final boolean removed = super.remove(key, value);
        if (removed) {
            refreshFastIterator();
        }
        return removed;
    }

    /**
     * Please use iterator() directly for faster access
     */
    @Override
    @Deprecated
    public Collection<V> values() {
        return super.values();
    }

    @Override
    public Iterator<V> iterator() {
        return fastIterable.iterator();
    }

}
