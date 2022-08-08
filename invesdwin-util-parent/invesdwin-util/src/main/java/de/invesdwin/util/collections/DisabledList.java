package de.invesdwin.util.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.iterable.EmptyCloseableIterator;

/**
 * Does not throw any exceptions like Collections.emptyList() does on modification.
 */
@Immutable
public final class DisabledList<E> implements List<E> {

    @SuppressWarnings("rawtypes")
    private static final DisabledList INSTANCE = new DisabledList<>();

    private DisabledList() {
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public boolean contains(final Object o) {
        return false;
    }

    @Override
    public Iterator<E> iterator() {
        return EmptyCloseableIterator.getInstance();
    }

    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @Override
    public <T> T[] toArray(final T[] a) {
        return a;
    }

    @Override
    public boolean add(final E e) {
        return false;
    }

    @Override
    public boolean remove(final Object o) {
        return false;
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        return false;
    }

    @Override
    public boolean addAll(final Collection<? extends E> c) {
        return false;
    }

    @Override
    public boolean addAll(final int index, final Collection<? extends E> c) {
        return false;
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        return false;
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        return false;
    }

    @Override
    public void clear() {
    }

    @Override
    public E get(final int index) {
        return null;
    }

    @Override
    public E set(final int index, final E element) {
        return null;
    }

    @Override
    public void add(final int index, final E element) {
    }

    @Override
    public E remove(final int index) {
        return null;
    }

    @Override
    public int indexOf(final Object o) {
        return -1;
    }

    @Override
    public int lastIndexOf(final Object o) {
        return -1;
    }

    @Override
    public ListIterator<E> listIterator() {
        return Collections.emptyListIterator();
    }

    @Override
    public ListIterator<E> listIterator(final int index) {
        return Collections.emptyListIterator();
    }

    @Override
    public List<E> subList(final int fromIndex, final int toIndex) {
        return this;
    }

    @SuppressWarnings("unchecked")
    public static <T> DisabledList<T> getInstance() {
        return INSTANCE;
    }

}
