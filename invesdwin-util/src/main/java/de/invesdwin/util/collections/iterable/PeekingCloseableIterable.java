package de.invesdwin.util.collections.iterable;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class PeekingCloseableIterable<E> implements ICloseableIterable<E> {

    private final ICloseableIterable<? extends E> delegate;

    public PeekingCloseableIterable(final ICloseableIterable<? extends E> delegate) {
        this.delegate = delegate;
    }

    @Override
    public PeekingCloseableIterator<E> iterator() {
        return new PeekingCloseableIterator<>(delegate.iterator());
    }
}
