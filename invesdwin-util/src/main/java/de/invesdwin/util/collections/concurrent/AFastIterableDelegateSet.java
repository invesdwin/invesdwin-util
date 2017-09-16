package de.invesdwin.util.collections.concurrent;

import java.lang.reflect.Array;
import java.util.Collection;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.ADelegateSet;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.iterable.buffer.BufferingIterator;

/**
 * Boosts the iteration speed over the values by keeping a fast iterator instance that only gets modified when changes
 * to the map occur.
 * 
 * The iterator returned from this set is also suitable for concurrent modification during iteration.
 * 
 * http://stackoverflow.com/questions/1006395/fastest-way-to-iterate-an-array-in-java-loop-variable-vs-enhanced-for-statement
 */
@ThreadSafe
public abstract class AFastIterableDelegateSet<E> extends ADelegateSet<E> implements IFastIterable<E> {

    //arraylist wins in raw iterator speed compared to bufferingIterator since no remove is needed, though we need protection against concurrent modification
    @GuardedBy("this")
    private BufferingIterator<E> fastIterable;
    @GuardedBy("this")
    private E[] array;
    @GuardedBy("this")
    private boolean empty;
    @GuardedBy("this")
    private int size;

    public AFastIterableDelegateSet() {
        refreshFastIterable();
    }

    @Override
    public boolean add(final E e) {
        final boolean added = super.add(e);
        if (added) {
            addToFastIterable(e);
        }
        return added;
    }

    protected synchronized void addToFastIterable(final E e) {
        if (fastIterable != null) {
            fastIterable.add(e);
        }
        array = null;
        empty = false;
        size++;
    }

    @Override
    public boolean addAll(final Collection<? extends E> c) {
        final boolean added = super.addAll(c);
        if (added) {
            refreshFastIterable();
        }
        return added;
    }

    @Override
    public boolean remove(final Object o) {
        final boolean removed = super.remove(o);
        if (removed) {
            refreshFastIterable();
        }
        return removed;
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        final boolean removed = super.removeAll(c);
        if (removed) {
            refreshFastIterable();
        }
        return removed;
    }

    /**
     * protected so it can be used inside addToFastIterable to refresh instead if desired by overriding
     */
    protected synchronized void refreshFastIterable() {
        fastIterable = null;
        array = null;
        size = getDelegate().size();
        empty = size == 0;
    }

    @Override
    public void clear() {
        super.clear();
        synchronized (this) {
            fastIterable = new BufferingIterator<E>();
            array = null;
            empty = true;
            size = 0;
        }
    }

    @Override
    public synchronized ICloseableIterator<E> iterator() {
        if (fastIterable == null) {
            fastIterable = new BufferingIterator<E>(getDelegate());
        }
        return fastIterable.iterator();
    }

    @Override
    public synchronized boolean isEmpty() {
        return empty;
    }

    @Override
    public synchronized int size() {
        return size;
    }

    @Override
    @SuppressWarnings("unchecked")
    public synchronized E[] asArray(final Class<E> type) {
        if (array == null) {
            final E[] empty = (E[]) Array.newInstance(type, size);
            array = toArray(empty);
        }
        return array;
    }

}
