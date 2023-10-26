package de.invesdwin.util.collections.delegate.unmodifiable;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

import javax.annotation.concurrent.Immutable;

@Immutable
public abstract class AUnmodifiableList<E> extends AUnmodifiableCollection<E> implements List<E> {

    @Override
    public final boolean addAll(final int index, final Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final ListIterator<E> listIterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public final ListIterator<E> listIterator(final int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final List<E> subList(final int fromIndex, final int toIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final E set(final int index, final E element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void add(final int index, final E element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final E remove(final int index) {
        throw new UnsupportedOperationException();
    }
}
