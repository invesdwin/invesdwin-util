package de.invesdwin.util.collections.iterable.concurrent;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.concurrent.lock.ILock;

@ThreadSafe
public class LockedCloseableIterator<E> implements ICloseableIterator<E> {

    private final ICloseableIterator<E> delegate;
    private final ILock lock;

    public LockedCloseableIterator(final ICloseableIterator<E> delegate, final ILock lock) {
        this.delegate = delegate;
        this.lock = lock;
    }

    @Override
    public boolean hasNext() {
        lock.lock();
        try {
            return delegate.hasNext();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public E next() {
        lock.lock();
        try {
            return delegate.next();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void close() {
        lock.lock();
        try {
            delegate.close();
        } finally {
            lock.unlock();
        }
    }

}
