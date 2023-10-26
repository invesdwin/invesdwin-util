package de.invesdwin.util.collections.delegate.unmodifiable;

import java.util.Collection;
import java.util.Set;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.iterable.WrapperCloseableIterable;

@NotThreadSafe
public class UnmodifiableSet<E> extends AUnmodifiableSet<E> {

    private final Set<E> delegate;

    public UnmodifiableSet(final Set<E> delegate) {
        this.delegate = delegate;
    }

    public Set<E> getDelegate() {
        return delegate;
    }

    @Override
    public int size() {
        return getDelegate().size();
    }

    @Override
    public boolean isEmpty() {
        return getDelegate().isEmpty();
    }

    @Override
    public boolean contains(final Object o) {
        return getDelegate().contains(o);
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        return getDelegate().containsAll(c);
    }

    @Override
    public ICloseableIterator<E> iterator() {
        return WrapperCloseableIterable.maybeWrap(delegate).iterator();
    }

    @Override
    public Object[] toArray() {
        return getDelegate().toArray();
    }

    @Override
    public <T> T[] toArray(final T[] a) {
        return getDelegate().toArray(a);
    }

    @Override
    public boolean equals(final Object o) {
        return getDelegate().equals(o);
    }

    @Override
    public int hashCode() {
        return getDelegate().hashCode();
    }

    @Override
    public String toString() {
        return getDelegate().toString();
    }

}
