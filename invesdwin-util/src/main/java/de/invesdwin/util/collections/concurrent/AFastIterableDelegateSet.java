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

    private volatile BufferingIterator<E> fastIterable = new BufferingIterator<E>(getDelegate());
    private volatile boolean empty = true;
    private volatile int size = 0;

    public AFastIterableDelegateSet() {
        refreshFastIterable();
    }

    @Override
    public synchronized boolean add(final E e) {
        final boolean added = super.add(e);
        if (added) {
            fastIterable.add(e);
            empty = false;
            size++; //it is actually safe here to increment since every modifier is synchronized
        }
        return added;
    }

    @Override
    public synchronized boolean addAll(final Collection<? extends E> c) {
        final boolean added = super.addAll(c);
        if (added) {
            refreshFastIterable();
        }
        return added;
    }

    @Override
    public synchronized boolean remove(final Object o) {
        final boolean removed = super.remove(o);
        if (removed) {
            refreshFastIterable();
        }
        return removed;
    }

    @Override
    public synchronized boolean removeAll(final Collection<?> c) {
        final boolean removed = super.removeAll(c);
        if (removed) {
            refreshFastIterable();
        }
        return removed;
    }

    private void refreshFastIterable() {
        fastIterable = new BufferingIterator<E>(getDelegate());
        empty = fastIterable.isEmpty();
        size = fastIterable.size();
    }

    @Override
    public synchronized void clear() {
        super.clear();
        fastIterable = new BufferingIterator<E>();
        empty = true;
        size = 0;
    }

    @Override
    public Iterator<E> iterator() {
        return fastIterable.iterator();
    }

    @Override
    public boolean isEmpty() {
        return empty;
    }

    @Override
    public int size() {
        return size;
    }
}
