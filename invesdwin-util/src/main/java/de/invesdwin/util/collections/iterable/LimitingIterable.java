package de.invesdwin.util.collections.iterable;

import javax.annotation.concurrent.NotThreadSafe;

/**
 * This implementation is faster than Iterators.limit() when not using hasNext() and instead relying on next() and
 * NoSuchElementException.
 */
@NotThreadSafe
public class LimitingIterable<E> implements ICloseableIterable<E> {

    private final ICloseableIterable<? extends E> delegate;
    private final int limit;

    public LimitingIterable(final ICloseableIterable<? extends E> delegate, final int limit) {
        this.delegate = delegate;
        this.limit = limit;
    }

    @Override
    public ACloseableIterator<E> iterator() {
        return new LimitingIterator<E>(delegate.iterator(), limit);
    }

}
