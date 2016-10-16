package de.invesdwin.util.collections.iterable;

import java.util.NoSuchElementException;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class EmptyCloseableIterator<E> implements ICloseableIterator<E> {

    @SuppressWarnings("rawtypes")
    private static final EmptyCloseableIterator INSTANCE = new EmptyCloseableIterator();

    private EmptyCloseableIterator() {}

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public E next() {
        throw new NoSuchElementException();
    }

    @Override
    public void close() {}

    @SuppressWarnings("unchecked")
    public static <T> EmptyCloseableIterator<T> getInstance() {
        return INSTANCE;
    }

}
