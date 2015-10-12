package de.invesdwin.util.collections.iterable;

import java.util.NoSuchElementException;

import javax.annotation.concurrent.NotThreadSafe;

/**
 * This implementation is faster than Iterators.limit() when not using hasNext() and instead relying on next() and
 * NoSuchElementException.
 */
@NotThreadSafe
public class LimitingIterator<E> extends ACloseableIterator<E> {

    private final ACloseableIterator<? extends E> delegate;
    private final int limit;
    private int curCount;

    public LimitingIterator(final ACloseableIterator<? extends E> delegate, final int limit) {
        this.delegate = delegate;
        this.limit = limit;
    }

    @Override
    protected boolean innerHasNext() {
        if (curCount >= limit) {
            return false;
        }
        return delegate.hasNext();
    }

    @Override
    protected E innerNext() {
        if (curCount >= limit) {
            throw new NoSuchElementException();
        }
        curCount++;
        return delegate.next();
    }

    @Override
    protected void innerClose() {
        delegate.close();
    }

}
