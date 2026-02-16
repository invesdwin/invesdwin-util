package de.invesdwin.util.collections.delegate.unmodifiable;

import java.util.Collection;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.iterable.WrapperCloseableIterable;

@NotThreadSafe
public class UnmodifiableList<E> extends AUnmodifiableList<E> {

    private final List<E> delegate;

    public UnmodifiableList(final List<E> delegate) {
        this.delegate = delegate;
    }

    public List<E> getDelegate() {
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
    public E get(final int index) {
        return getDelegate().get(index);
    }

    @Override
    public int indexOf(final Object o) {
        return getDelegate().indexOf(o);
    }

    @Override
    public int lastIndexOf(final Object o) {
        return getDelegate().lastIndexOf(o);
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
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
