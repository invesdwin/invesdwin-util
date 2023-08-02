package de.invesdwin.util.collections.delegate.disabled;

import java.util.Collection;
import java.util.Iterator;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.iterable.EmptyCloseableIterator;
import de.invesdwin.util.lang.Objects;

@Immutable
public class DisabledCollection<E> implements Collection<E> {

    @SuppressWarnings({ "rawtypes" })
    private static final DisabledCollection INSTANCE = new DisabledCollection<>();

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
        return Objects.EMPTY_ARRAY;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T[] toArray(final T[] a) {
        return (T[]) Objects.EMPTY_ARRAY;
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
    public boolean removeAll(final Collection<?> c) {
        return false;
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        return false;
    }

    @Override
    public void clear() {}

    @SuppressWarnings("unchecked")
    public static <T> DisabledCollection<T> getInstance() {
        return INSTANCE;
    }

}
