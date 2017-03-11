package de.invesdwin.util.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.annotation.concurrent.NotThreadSafe;

/**
 * A performant way to keep a list of ordered elements
 */
@NotThreadSafe
@SuppressWarnings({ "rawtypes", "unchecked" })
public class SortedList<E> implements List<E> {
    private final Comparator<E> comparator;
    private final List<E> delegate;

    public SortedList(final Comparator comparator) {
        this(comparator, new ArrayList<E>());
    }

    public SortedList(final Comparator comparator, final List<E> delegate) {
        this.comparator = comparator;
        this.delegate = delegate;
    }

    /**
     * adds the element at its appropriate spot doing the search in ascending order to add it in the front
     */
    @Override
    public void add(final int index, final E o) {
        final int size = delegate.size();
        for (int i = 0; i < size; i++) {
            if (comparator.compare(delegate.get(i), o) > 0) {
                delegate.add(i, o);
                return;
            }
        }
        delegate.add(o);
    }

    /**
     * adds the element at its appropriate spot doing the search in descending order to add it in the end
     */
    @Override
    public boolean add(final E o) {
        final int size = delegate.size();
        for (int i = size; i > 0; i--) {
            if (comparator.compare(delegate.get(i - 1), o) < 0) {
                delegate.add(i, o);
                return true;
            }
        }
        delegate.add(0, o);
        return true;
    }

    @Override
    public boolean addAll(final Collection<? extends E> c) {
        for (final E o : c) {
            add(o);
        }
        return true;
    }

    @Override
    public boolean addAll(final int index, final Collection<? extends E> c) {
        return addAll(c);
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public boolean contains(final Object o) {
        return delegate.contains(o);
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        return delegate.containsAll(c);
    }

    @Override
    public boolean equals(final Object o) {
        return delegate.equals(o);
    }

    @Override
    public E get(final int index) {
        return delegate.get(index);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public int indexOf(final Object o) {
        return delegate.indexOf(o);
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public Iterator<E> iterator() {
        return delegate.iterator();
    }

    @Override
    public int lastIndexOf(final Object o) {
        return delegate.lastIndexOf(o);
    }

    @Override
    public ListIterator<E> listIterator() {
        return delegate.listIterator();
    }

    @Override
    public ListIterator<E> listIterator(final int index) {
        return delegate.listIterator(index);
    }

    @Override
    public E remove(final int index) {
        return delegate.remove(index);
    }

    @Override
    public boolean remove(final Object o) {
        return delegate.remove(o);
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        return delegate.removeAll(c);
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        return delegate.retainAll(c);
    }

    @Override
    public E set(final int index, final E element) {
        return delegate.set(index, element);
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public List<E> subList(final int fromIndex, final int toIndex) {
        return delegate.subList(fromIndex, toIndex);
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
    public String toString() {
        return delegate.toString();
    }

}
