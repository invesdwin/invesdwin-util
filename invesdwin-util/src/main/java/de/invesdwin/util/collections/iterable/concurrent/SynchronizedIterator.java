package de.invesdwin.util.collections.iterable.concurrent;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.iterable.ACloseableIterator;

@ThreadSafe
public class SynchronizedIterator<E> extends ACloseableIterator<E> {

    private final ACloseableIterator<E> delegate;

    public SynchronizedIterator(final ACloseableIterator<E> delegate) {
        this.delegate = delegate;
    }

    @Override
    protected synchronized boolean innerHasNext() {
        return delegate.hasNext();
    }

    @Override
    protected synchronized E innerNext() {
        return delegate.next();
    }

    @Override
    protected synchronized void innerClose() {
        delegate.close();
    }

}
