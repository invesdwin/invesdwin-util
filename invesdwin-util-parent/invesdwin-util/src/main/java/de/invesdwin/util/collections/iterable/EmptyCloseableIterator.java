package de.invesdwin.util.collections.iterable;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.error.FastNoSuchElementException;

@Immutable
public final class EmptyCloseableIterator<E> implements IPeekingCloseableIterator<E> {

    @SuppressWarnings("rawtypes")
    private static final EmptyCloseableIterator INSTANCE = new EmptyCloseableIterator();

    private EmptyCloseableIterator() {}

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public E peek() {
        throw FastNoSuchElementException.getInstance("EmptyCloseableIterator: always empty");
    }

    @Override
    public E next() {
        throw FastNoSuchElementException.getInstance("EmptyCloseableIterator: always empty");
    }

    @Override
    public void remove() {
        throw new IllegalStateException("EmptyCloseableIterator: always empty");
    }

    @Override
    public void close() {}

    @SuppressWarnings("unchecked")
    public static <T> EmptyCloseableIterator<T> getInstance() {
        return INSTANCE;
    }

}
