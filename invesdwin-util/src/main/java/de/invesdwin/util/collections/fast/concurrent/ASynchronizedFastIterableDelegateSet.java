package de.invesdwin.util.collections.fast.concurrent;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Set;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.fast.IFastIterableSet;
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
public abstract class ASynchronizedFastIterableDelegateSet<E> implements IFastIterableSet<E> {

    //arraylist wins in raw iterator speed compared to bufferingIterator since no remove is needed, though we need protection against concurrent modification
    @GuardedBy("this")
    private BufferingIterator<E> fastIterable;
    @GuardedBy("this")
    private E[] array;
    @GuardedBy("this")
    private boolean empty;
    @GuardedBy("this")
    private int size;
    @GuardedBy("this")
    private final Set<E> delegate = newDelegate();

    public ASynchronizedFastIterableDelegateSet() {
        refreshFastIterable();
    }

    protected abstract Set<E> newDelegate();

    @Override
    public synchronized boolean add(final E e) {
        final boolean added = delegate.add(e);
        if (added) {
            addToFastIterable(e);
        }
        return added;
    }

    protected void addToFastIterable(final E e) {
        if (fastIterable != null) {
            fastIterable.add(e);
        }
        array = null;
        empty = false;
        size++;
    }

    @Override
    public synchronized boolean addAll(final Collection<? extends E> c) {
        final boolean added = delegate.addAll(c);
        if (added) {
            refreshFastIterable();
        }
        return added;
    }

    @Override
    public synchronized boolean remove(final Object o) {
        final boolean removed = delegate.remove(o);
        if (removed) {
            refreshFastIterable();
        }
        return removed;
    }

    @Override
    public synchronized boolean removeAll(final Collection<?> c) {
        final boolean removed = delegate.removeAll(c);
        if (removed) {
            refreshFastIterable();
        }
        return removed;
    }

    /**
     * protected so it can be used inside addToFastIterable to refresh instead if desired by overriding
     */
    protected void refreshFastIterable() {
        fastIterable = null;
        array = null;
        size = delegate.size();
        empty = size == 0;
    }

    @Override
    public synchronized void clear() {
        delegate.clear();
        fastIterable = new BufferingIterator<E>();
        array = null;
        empty = true;
        size = 0;
    }

    @Override
    public synchronized ICloseableIterator<E> iterator() {
        if (fastIterable == null) {
            fastIterable = new BufferingIterator<E>(delegate);
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

    @Override
    public synchronized boolean contains(final Object o) {
        return delegate.contains(o);
    }

    @Override
    public synchronized Object[] toArray() {
        return delegate.toArray();
    }

    @Override
    public synchronized <T> T[] toArray(final T[] a) {
        return delegate.toArray(a);
    }

    @Override
    public synchronized boolean containsAll(final Collection<?> c) {
        return delegate.containsAll(c);
    }

    @Override
    public synchronized boolean retainAll(final Collection<?> c) {
        return delegate.retainAll(c);
    }

    @Override
    public synchronized String toString() {
        return delegate.toString();
    }

}
