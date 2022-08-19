package de.invesdwin.util.collections.iterable;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.error.FastNoSuchElementException;

/**
 * This implementation is faster than Iterators.limit() when not using hasNext() and instead relying on next() and
 * NoSuchElementException.
 */
@NotThreadSafe
public class LimitingIterator<E> implements ICloseableIterator<E> {

    private final ICloseableIterator<? extends E> delegate;
    private final int limit;
    private int curCount;

    public LimitingIterator(final ICloseableIterator<? extends E> delegate, final int limit) {
        this.delegate = delegate;
        this.limit = limit;
    }

    @Override
    public boolean hasNext() {
        if (curCount >= limit) {
            close();
            return false;
        }
        return delegate.hasNext();
    }

    @Override
    public E next() {
        if (curCount >= limit) {
            close();
            throw FastNoSuchElementException.getInstance("LimitingIterator: next reached limit");
        }
        curCount++;
        return delegate.next();
    }

    @Override
    public void close() {
        delegate.close();
    }

}
