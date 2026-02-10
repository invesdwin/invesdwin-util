package de.invesdwin.util.collections.iterable.concurrent.readwrite;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.concurrent.lock.readwrite.IReadWriteLock;

@ThreadSafe
public class ReadWriteLockedCloseableIterator<E> implements ICloseableIterator<E> {

    private final ICloseableIterator<E> delegate;
    private final IReadWriteLock lock;

    public ReadWriteLockedCloseableIterator(final ICloseableIterator<E> delegate, final IReadWriteLock lock) {
        this.delegate = delegate;
        this.lock = lock;
    }

    @Override
    public boolean hasNext() {
        lock.readLock().lock();
        try {
            return delegate.hasNext();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public E next() {
        lock.readLock().lock();
        try {
            return delegate.next();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void remove() {
        lock.writeLock().lock();
        try {
            delegate.remove();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void close() {
        lock.readLock().lock();
        try {
            delegate.close();
        } finally {
            lock.readLock().unlock();
        }
    }

}
