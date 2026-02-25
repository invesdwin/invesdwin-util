package de.invesdwin.util.collections.delegate.unmodifiable;

import java.util.Iterator;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.iterable.collection.ArrayCloseableIterator;
import de.invesdwin.util.collections.iterable.skip.ASkippingIterator;
import de.invesdwin.util.error.FastIndexOutOfBoundsException;

@Immutable
public abstract class AFilteringArrayUnmodifiableList<E> extends AUnmodifiableList<E> {

    private final E[] elements;

    public AFilteringArrayUnmodifiableList(final E[] elements) {
        this.elements = elements;
    }

    protected abstract boolean isAccepted(E element);

    @Override
    public int size() {
        int size = 0;
        for (int i = 0; i < elements.length; i++) {
            if (isAccepted(elements[i])) {
                size++;
            }
        }
        return size;
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < elements.length; i++) {
            if (isAccepted(elements[i])) {
                return false;
            }
        }
        return true;
    }

    @Override
    public E get(final int index) {
        int size = 0;
        for (int i = 0; i < elements.length; i++) {
            final E order = elements[i];
            if (isAccepted(order)) {
                if (index == size) {
                    return order;
                }
                size++;
            }
        }
        throw FastIndexOutOfBoundsException.getInstance("%s", index);
    }

    @Override
    public boolean contains(final Object o) {
        for (int i = 0; i < elements.length; i++) {
            final E element = elements[i];
            if (isAccepted(element) && o.equals(element)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Iterator<E> iterator() {
        return new ASkippingIterator<E>(new ArrayCloseableIterator<E>(elements)) {
            @Override
            protected boolean skip(final E element) {
                return !isAccepted(element);
            }
        };
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T[] toArray(final T[] a) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int indexOf(final Object o) {
        int size = 0;
        for (int i = 0; i < elements.length; i++) {
            final E element = elements[i];
            if (isAccepted(element)) {
                if (o.equals(element)) {
                    return size;
                }
                size++;
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(final Object o) {
        int size = 0;
        int lastIndexOf = -1;
        for (int i = 0; i < elements.length; i++) {
            final E element = elements[i];
            if (isAccepted(element)) {
                if (o.equals(element)) {
                    lastIndexOf = size;
                }
                size++;
            }
        }
        return lastIndexOf;
    }

}
