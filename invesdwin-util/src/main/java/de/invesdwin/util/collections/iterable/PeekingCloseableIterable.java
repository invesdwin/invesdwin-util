package de.invesdwin.util.collections.iterable;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class PeekingCloseableIterable<E> implements ICloseableIterable<E> {

    private final ICloseableIterable<E> delegate;

    public PeekingCloseableIterable(final ICloseableIterable<E> delegate) {
        this.delegate = delegate;
    }

    @Override
    public PeekingCloseableIterator<E> iterator() {
        return new PeekingCloseableIterator<>(delegate.iterator());
    }
}
