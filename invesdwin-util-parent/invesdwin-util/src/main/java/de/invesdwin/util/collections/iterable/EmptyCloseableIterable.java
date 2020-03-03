package de.invesdwin.util.collections.iterable;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class EmptyCloseableIterable<E> implements ICloseableIterable<E> {

    @SuppressWarnings("rawtypes")
    private static final EmptyCloseableIterable INSTANCE = new EmptyCloseableIterable();

    private EmptyCloseableIterable() {}

    @Override
    public ICloseableIterator<E> iterator() {
        return EmptyCloseableIterator.getInstance();
    }

    @SuppressWarnings("unchecked")
    public static <T> EmptyCloseableIterable<T> getInstance() {
        return INSTANCE;
    }

}
