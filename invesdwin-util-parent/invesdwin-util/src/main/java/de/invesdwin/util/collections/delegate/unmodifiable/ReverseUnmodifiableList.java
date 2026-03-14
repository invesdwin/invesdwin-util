package de.invesdwin.util.collections.delegate.unmodifiable;

import java.util.Collection;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.Collections;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.error.FastNoSuchElementException;

@NotThreadSafe
public class ReverseUnmodifiableList<E> extends AUnmodifiableList<E> {

    private final List<E> delegate;

    public ReverseUnmodifiableList(final List<E> delegate) {
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
        return new ICloseableIterator<E>() {

            private int nextIndex = getDelegate().size() - 1;

            @Override
            public boolean hasNext() {
                return nextIndex >= 0;
            }

            @Override
            public E next() {
                if (nextIndex < 0) {
                    throw FastNoSuchElementException.getInstance("ReverseUnmodifiableList.iterator.next() reached end");
                }
                final E next = getDelegate().get(nextIndex);
                nextIndex--;
                return next;
            }

            @Override
            public void close() {
                nextIndex = -1;
            }
        };
    }

    @Override
    public Object[] toArray() {
        final Object[] array = getDelegate().toArray();
        Arrays.reverse(array);
        return array;
    }

    @Override
    public <T> T[] toArray(final T[] a) {
        final T[] array = getDelegate().toArray(a);
        Arrays.reverse(array);
        return array;
    }

    @Override
    public E get(final int index) {
        return getDelegate().get(reverseIndex(index));
    }

    private int reverseIndex(final int index) {
        return getDelegate().size() - 1 - index;
    }

    @Override
    public int indexOf(final Object o) {
        final int indexOf = getDelegate().indexOf(o);
        if (indexOf < 0) {
            return indexOf;
        } else {
            return reverseIndex(indexOf);
        }
    }

    @Override
    public int lastIndexOf(final Object o) {
        final int lastIndexOf = getDelegate().lastIndexOf(o);
        if (lastIndexOf < 0) {
            return lastIndexOf;
        } else {
            return reverseIndex(lastIndexOf);
        }
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        return Collections.elementsEqual(this, obj);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public String toString() {
        return Collections.toString(this);
    }

}
