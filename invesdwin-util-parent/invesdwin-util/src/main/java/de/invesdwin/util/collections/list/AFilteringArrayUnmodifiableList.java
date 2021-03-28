package de.invesdwin.util.collections.list;

import java.util.Iterator;

import javax.annotation.concurrent.Immutable;

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
        return false;
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
        return null;
    }

    @Override
    public boolean contains(final Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<E> iterator() {
        throw new UnsupportedOperationException();
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
        throw new UnsupportedOperationException();
    }

    @Override
    public int lastIndexOf(final Object o) {
        throw new UnsupportedOperationException();
    }

}
