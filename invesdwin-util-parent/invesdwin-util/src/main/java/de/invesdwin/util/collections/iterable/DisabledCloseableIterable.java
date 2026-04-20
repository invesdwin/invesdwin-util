package de.invesdwin.util.collections.iterable;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class DisabledCloseableIterable<E> implements IPeekingCloseableIterable<E>, IReverseCloseableIterable<E> {

    @SuppressWarnings("rawtypes")
    private static final DisabledCloseableIterable INSTANCE = new DisabledCloseableIterable();

    private DisabledCloseableIterable() {}

    @Override
    public IPeekingCloseableIterator<E> iterator() {
        return DisabledCloseableIterator.getInstance();
    }

    @SuppressWarnings("unchecked")
    public static <T> DisabledCloseableIterable<T> getInstance() {
        return INSTANCE;
    }

    @Override
    public ICloseableIterator<E> reverseIterator() {
        return DisabledCloseableIterator.getInstance();
    }

}
