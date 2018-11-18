package de.invesdwin.util.collections.fast;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.iterable.buffer.BufferingIterator;

@ThreadSafe
public abstract class AFastIterableDelegateList<E> implements IFastIterableList<E> {

    private BufferingIterator<E> fastIterable;
    private E[] array;
    private final List<E> delegate = newDelegate();

    public AFastIterableDelegateList() {
        refreshFastIterable();
    }

    protected abstract List<E> newDelegate();

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
    public boolean addAll(final int index, final Collection<? extends E> c) {
        final boolean added = delegate.addAll(index, c);
        if (added) {
            refreshFastIterable();
        }
        return added;
    }

    @Override
    public void add(final int index, final E element) {
        delegate.add(index, element);
        refreshFastIterable();
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

    @Override
    public E remove(final int index) {
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

    @SuppressWarnings("unchecked")
    @Override
    public E[] asArray(final Class<E> type) {
        if (array == null) {
            final E[] empty = (E[]) Array.newInstance(type, delegate.size());
            array = toArray(empty);
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
    public E get(final int index) {
        return delegate.get(index);
    }

    @Override
    public E set(final int index, final E element) {
        final E prev = delegate.set(index, element);
        refreshFastIterable();
        return prev;
    }

    @Override
    public int indexOf(final Object o) {
        return delegate.indexOf(o);
    }

    @Override
    public int lastIndexOf(final Object o) {
        return delegate.lastIndexOf(o);
    }

    @Override
    public ListIterator<E> listIterator() {
        return Collections.unmodifiableList(delegate).listIterator();
    }

    @Override
    public ListIterator<E> listIterator(final int index) {
        return Collections.unmodifiableList(delegate).listIterator(index);
    }

    @Override
    public List<E> subList(final int fromIndex, final int toIndex) {
        return Collections.unmodifiableList(delegate).subList(fromIndex, toIndex);
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

}
