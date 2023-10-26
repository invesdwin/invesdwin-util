package de.invesdwin.util.collections.fast.concurrent;

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
    private transient BufferingIterator<E> fastIterable;
    @GuardedBy("this")
    private transient E[] array;
    @GuardedBy("this")
    private final Set<E> delegate;

    protected ASynchronizedFastIterableDelegateSet(final Set<E> delegate) {
        this.delegate = delegate;
        refreshFastIterable();
    }

    public ASynchronizedFastIterableDelegateSet() {
        this.delegate = newDelegate();
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
    }

    @Override
    public synchronized void clear() {
        if (delegate.isEmpty()) {
            return;
        }
        delegate.clear();
        fastIterable = new BufferingIterator<E>();
        array = null;
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
        return delegate.isEmpty();
    }

    @Override
    public synchronized int size() {
        return delegate.size();
    }

    @Override
    public synchronized E[] asArray(final E[] emptyArray) {
        if (array == null) {
            if (delegate.isEmpty()) {
                assert emptyArray.length == 0 : "emptyArray.length needs to be 0: " + emptyArray.length;
                array = emptyArray;
            } else {
                array = onArrayCreated(delegate.toArray(emptyArray));
            }
        }
        return array;
    }

    protected E[] onArrayCreated(final E[] array) {
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

    @Override
    public synchronized int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public synchronized boolean equals(final Object obj) {
        return delegate.equals(obj);
    }

}
