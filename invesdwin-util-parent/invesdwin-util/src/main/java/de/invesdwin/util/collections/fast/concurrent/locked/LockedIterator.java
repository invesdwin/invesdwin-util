package de.invesdwin.util.collections.fast.concurrent.locked;

import java.util.Iterator;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.ILock;

@ThreadSafe
public class LockedIterator<E> implements Iterator<E> {

    private final Iterator<E> delegate;
    private final ILock lock;

    public LockedIterator(final Iterator<E> delegate, final ILock lock) {
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

}
