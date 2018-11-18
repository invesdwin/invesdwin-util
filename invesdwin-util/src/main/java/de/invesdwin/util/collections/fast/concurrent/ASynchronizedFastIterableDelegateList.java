package de.invesdwin.util.collections.fast.concurrent;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.fast.IFastIterableList;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.iterable.buffer.BufferingIterator;

@ThreadSafe
public abstract class ASynchronizedFastIterableDelegateList<E> implements IFastIterableList<E> {

    //arraylist wins in raw iterator speed compared to bufferingIterator since no remove is needed, though we need protection against concurrent modification
    @GuardedBy("this")
    private BufferingIterator<E> fastIterable;
    @GuardedBy("this")
    private E[] array;
    @GuardedBy("this")
    private final List<E> delegate = newDelegate();

    public ASynchronizedFastIterableDelegateList() {
        refreshFastIterable();
    }

    protected abstract List<E> newDelegate();

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
    public synchronized boolean addAll(final int index, final Collection<? extends E> c) {
        final boolean added = delegate.addAll(index, c);
        if (added) {
            refreshFastIterable();
        }
        return added;
    }

    @Override
    public synchronized void add(final int index, final E element) {
        delegate.add(index, element);
        refreshFastIterable();
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

    @Override
    public synchronized E remove(final int index) {
        final E removed = delegate.remove(index);
        refreshFastIterable();
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

    @SuppressWarnings("unchecked")
    @Override
    public synchronized E[] asArray(final Class<E> type) {
        if (array == null) {
            final E[] empty = (E[]) Array.newInstance(type, delegate.size());
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
    public synchronized E get(final int index) {
        return delegate.get(index);
    }

    @Override
    public synchronized E set(final int index, final E element) {
        final E prev = delegate.set(index, element);
        refreshFastIterable();
        return prev;
    }

    @Override
    public synchronized int indexOf(final Object o) {
        return delegate.indexOf(o);
    }

    @Override
    public synchronized int lastIndexOf(final Object o) {
        return delegate.lastIndexOf(o);
    }

    @Override
    public synchronized ListIterator<E> listIterator() {
        return Collections.unmodifiableList(delegate).listIterator();
    }

    @Override
    public synchronized ListIterator<E> listIterator(final int index) {
        return Collections.unmodifiableList(delegate).listIterator(index);
    }

    @Override
    public synchronized List<E> subList(final int fromIndex, final int toIndex) {
        return new SynchronizedList<E>(Collections.unmodifiableList(delegate).subList(fromIndex, toIndex), this);
    }

    @Override
    public synchronized String toString() {
        return delegate.toString();
    }

}
