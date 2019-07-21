package de.invesdwin.util.collections.iterable.concurrent;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.iterable.ICloseableIterator;

@ThreadSafe
public class SynchronizedCloseableIterator<E> implements ICloseableIterator<E> {

    private final ICloseableIterator<E> delegate;
    private final Object lock;

    public SynchronizedCloseableIterator(final ICloseableIterator<E> delegate) {
        this(delegate, delegate);
    }

    public SynchronizedCloseableIterator(final ICloseableIterator<E> delegate, final Object lock) {
        this.delegate = delegate;
        this.lock = lock;
    }

    @Override
    public boolean hasNext() {
        synchronized (lock) {
            return delegate.hasNext();
        }
    }

    @Override
    public E next() {
        synchronized (lock) {
            return delegate.next();
        }
    }

    @Override
    public void close() {
        synchronized (lock) {
            delegate.close();
        }
    }

}
