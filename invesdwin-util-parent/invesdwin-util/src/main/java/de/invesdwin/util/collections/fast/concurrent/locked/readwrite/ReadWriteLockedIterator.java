package de.invesdwin.util.collections.fast.concurrent.locked.readwrite;

import java.util.Iterator;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.readwrite.IReadWriteLock;

@ThreadSafe
public class ReadWriteLockedIterator<E> implements Iterator<E> {

    private final Iterator<E> delegate;
    private final IReadWriteLock lock;

    public ReadWriteLockedIterator(final Iterator<E> delegate, final IReadWriteLock lock) {
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

}
