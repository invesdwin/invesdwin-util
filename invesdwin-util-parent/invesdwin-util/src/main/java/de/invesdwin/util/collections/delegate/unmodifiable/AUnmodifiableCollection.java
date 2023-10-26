package de.invesdwin.util.collections.delegate.unmodifiable;

import java.util.Collection;

import javax.annotation.concurrent.Immutable;

@Immutable
public abstract class AUnmodifiableCollection<E> implements Collection<E> {

    @Override
    public final boolean add(final E e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final boolean remove(final Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final boolean addAll(final Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final boolean removeAll(final Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final boolean retainAll(final Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract String toString();

}
