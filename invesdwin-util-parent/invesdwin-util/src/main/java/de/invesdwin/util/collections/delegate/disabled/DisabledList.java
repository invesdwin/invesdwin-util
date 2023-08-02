package de.invesdwin.util.collections.delegate.disabled;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.collections4.iterators.EmptyListIterator;

@Immutable
public class DisabledList<E> extends DisabledCollection<E> implements List<E> {

    @SuppressWarnings("rawtypes")
    private static final DisabledList INSTANCE = new DisabledList<>();

    @Override
    public boolean add(final E e) {
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
    public E get(final int index) {
        return null;
    }

    @Override
    public E set(final int index, final E element) {
        return null;
    }

    @Override
    public void add(final int index, final E element) {}

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

    @SuppressWarnings("unchecked")
    @Override
    public ListIterator<E> listIterator() {
        return EmptyListIterator.INSTANCE;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ListIterator<E> listIterator(final int index) {
        return EmptyListIterator.INSTANCE;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<E> subList(final int fromIndex, final int toIndex) {
        return INSTANCE;
    }

    @SuppressWarnings("unchecked")
    public static <T> DisabledList<T> getInstance() {
        return INSTANCE;
    }

}
