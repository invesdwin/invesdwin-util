package de.invesdwin.util.collections.fast;

import java.util.Collection;
import java.util.Set;

import javax.annotation.concurrent.NotThreadSafe;

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
@NotThreadSafe
public abstract class AFastIterableDelegateSet<E> implements IFastIterableSet<E> {

    private transient BufferingIterator<E> fastIterable;
    private transient E[] array;
    private final Set<E> delegate;

    protected AFastIterableDelegateSet(final Set<E> delegate) {
        this.delegate = delegate;
        refreshFastIterable();
    }

    public AFastIterableDelegateSet() {
        this.delegate = newDelegate();
        refreshFastIterable();
    }

    protected abstract Set<E> newDelegate();

    @Override
    public boolean add(final E e) {
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
    public boolean addAll(final Collection<? extends E> c) {
        final boolean added = delegate.addAll(c);
        if (added) {
            refreshFastIterable();
        }
        return added;
    }

    @Override
    public boolean remove(final Object o) {
        final boolean removed = delegate.remove(o);
        if (removed) {
            refreshFastIterable();
        }
        return removed;
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
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
    public void clear() {
        delegate.clear();
        fastIterable = new BufferingIterator<E>();
        array = null;
    }

    @Override
    public ICloseableIterator<E> iterator() {
        if (fastIterable == null) {
            fastIterable = new BufferingIterator<E>(delegate);
        }
        return fastIterable.iterator();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public E[] asArray(final E[] emptyArray) {
        if (array == null) {
            array = toArray(emptyArray);
        }
        return array;
    }

    @Override
    public boolean contains(final Object o) {
        return delegate.contains(o);
    }

    @Override
    public Object[] toArray() {
        return delegate.toArray();
    }

    @Override
    public <T> T[] toArray(final T[] a) {
        return delegate.toArray(a);
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        return delegate.containsAll(c);
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        return delegate.retainAll(c);
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

}
