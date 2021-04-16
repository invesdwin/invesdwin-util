package de.invesdwin.util.collections.list;

import java.util.Iterator;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.lang3.ArrayUtils;

import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.error.FastNoSuchElementException;

@NotThreadSafe
public class ReverseUnmodifiableList<E> extends AUnmodifiableList<E> {

    private final List<E> delegate;

    public ReverseUnmodifiableList(final List<E> delegate) {
        this.delegate = delegate;
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public boolean contains(final Object o) {
        return delegate.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return new ICloseableIterator<E>() {

            private int nextIndex = delegate.size() - 1;

            @Override
            public boolean hasNext() {
                return nextIndex >= 0;
            }

            @Override
            public E next() {
                if (nextIndex < 0) {
                    throw new FastNoSuchElementException("ReverseUnmodifiableList.iterator.next() reached end");
                }
                final E next = delegate.get(nextIndex);
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
        final Object[] array = delegate.toArray();
        ArrayUtils.reverse(array);
        return array;
    }

    @Override
    public <T> T[] toArray(final T[] a) {
        final T[] array = delegate.toArray(a);
        ArrayUtils.reverse(array);
        return array;
    }

    @Override
    public E get(final int index) {
        return delegate.get(reverseIndex(index));
    }

    private int reverseIndex(final int index) {
        return delegate.size() - 1 - index;
    }

    @Override
    public int indexOf(final Object o) {
        final int indexOf = delegate.indexOf(o);
        if (indexOf < 0) {
            return indexOf;
        } else {
            return reverseIndex(indexOf);
        }
    }

    @Override
    public int lastIndexOf(final Object o) {
        final int lastIndexOf = delegate.lastIndexOf(o);
        if (lastIndexOf < 0) {
            return lastIndexOf;
        } else {
            return reverseIndex(lastIndexOf);
        }
    }

}
