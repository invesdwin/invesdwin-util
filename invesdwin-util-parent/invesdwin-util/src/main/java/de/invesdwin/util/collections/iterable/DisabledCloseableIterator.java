package de.invesdwin.util.collections.iterable;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class DisabledCloseableIterator<E> implements IPeekingCloseableIterator<E> {

    @SuppressWarnings("rawtypes")
    private static final DisabledCloseableIterator INSTANCE = new DisabledCloseableIterator();

    private DisabledCloseableIterator() {}

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public E peek() {
        return null;
    }

    @Override
    public E next() {
        return null;
    }

    @Override
    public void remove() {
        //noop
    }

    @Override
    public void close() {
        //noop
    }

    @SuppressWarnings("unchecked")
    public static <T> DisabledCloseableIterator<T> getInstance() {
        return INSTANCE;
    }

}
