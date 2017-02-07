package de.invesdwin.util.collections.concurrent;

import java.util.Collection;
import java.util.Iterator;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.ADelegateSet;
import de.invesdwin.util.collections.iterable.buffer.BufferingIterator;

/**
 * Boosts the iteration speed over the values by keeping a fast iterator instance that only gets modified when changes
 * to the map occur.
 * 
 * The iterator returned from this set is also suitable for concurrent modification during iteration.
 */
@ThreadSafe
public abstract class AFastIterableDelegateSet<E> extends ADelegateSet<E> {

    private volatile BufferingIterator<E> fastIterable = new BufferingIterator<E>();

    @Override
    public synchronized boolean add(final E e) {
        final boolean added = super.add(e);
        if (added) {
            fastIterable.add(e);
        }
        return added;
    }

    @Override
    public synchronized boolean addAll(final Collection<? extends E> c) {
        final boolean added = super.addAll(c);
        if (added) {
            refreshFastIterator();
        }
        return added;
    }

    @Override
    public synchronized boolean remove(final Object o) {
        final boolean removed = super.remove(o);
        if (removed) {
            refreshFastIterator();
        }
        return removed;
    }

    @Override
    public synchronized boolean removeAll(final Collection<?> c) {
        final boolean removed = super.removeAll(c);
        if (removed) {
            refreshFastIterator();
        }
        return removed;
    }

    private void refreshFastIterator() {
        fastIterable = new BufferingIterator<E>(getDelegate());
    }

    @Override
    public synchronized void clear() {
        super.clear();
        fastIterable = new BufferingIterator<E>();
    }

    @Override
    public Iterator<E> iterator() {
        return fastIterable.iterator();
    }

}
