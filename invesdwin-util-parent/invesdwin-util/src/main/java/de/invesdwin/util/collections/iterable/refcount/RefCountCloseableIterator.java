package de.invesdwin.util.collections.iterable.refcount;

import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.ICloseableIterator;

@NotThreadSafe
public class RefCountCloseableIterator<E> implements ICloseableIterator<E> {

    private final ICloseableIterator<E> delegate;
    private AtomicInteger refCount;

    public RefCountCloseableIterator(final ICloseableIterator<E> delegate, final AtomicInteger refCount) {
        this.delegate = delegate;
        this.refCount = refCount;
        refCount.incrementAndGet();
    }

    @Override
    public boolean hasNext() {
        return delegate.hasNext();
    }

    @Override
    public E next() {
        return delegate.next();
    }

    @Override
    public void close() {
        if (refCount != null) {
            delegate.close();
            refCount.decrementAndGet();
            refCount = null;
        }
    }
}