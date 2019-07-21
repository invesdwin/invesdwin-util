package de.invesdwin.util.collections.fast.concurrent;

import java.util.Iterator;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class SynchronizedIterator<E> implements Iterator<E> {

    private final Iterator<E> delegate;
    private final Object lock;

    public SynchronizedIterator(final Iterator<E> delegate) {
        this(delegate, delegate);
    }

    public SynchronizedIterator(final Iterator<E> delegate, final Object lock) {
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

}
