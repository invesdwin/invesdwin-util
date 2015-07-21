package de.invesdwin.util.collections.iterable.concurrent;

import java.io.IOException;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.iterable.ICloseableIterator;

@ThreadSafe
public class SynchronizedIterator<E> implements ICloseableIterator<E> {

    private final ICloseableIterator<E> delegate;

    public SynchronizedIterator(final ICloseableIterator<E> delegate) {
        this.delegate = delegate;
    }

    @Override
    public synchronized boolean hasNext() {
        return delegate.hasNext();
    }

    @Override
    public synchronized E next() {
        return delegate.next();
    }

    @Override
    public synchronized void close() throws IOException {
        delegate.close();
    }

}
