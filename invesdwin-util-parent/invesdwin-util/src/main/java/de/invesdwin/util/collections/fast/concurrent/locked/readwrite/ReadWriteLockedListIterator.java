package de.invesdwin.util.collections.fast.concurrent.locked.readwrite;

import java.util.ListIterator;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.readwrite.IReadWriteLock;

@ThreadSafe
public class ReadWriteLockedListIterator<E> implements ListIterator<E> {

    private final ListIterator<E> delegate;
    private final IReadWriteLock lock;

    public ReadWriteLockedListIterator(final ListIterator<E> delegate, final IReadWriteLock lock) {
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
    public boolean hasPrevious() {
        lock.readLock().lock();
        try {
            return delegate.hasPrevious();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public E previous() {
        lock.readLock().lock();
        try {
            return delegate.previous();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int nextIndex() {
        lock.readLock().lock();
        try {
            return delegate.nextIndex();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int previousIndex() {
        lock.readLock().lock();
        try {
            return delegate.previousIndex();
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
    public void set(final E e) {
        lock.writeLock().lock();
        try {
            delegate.set(e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void add(final E e) {
        lock.writeLock().lock();
        try {
            delegate.add(e);
        } finally {
            lock.writeLock().unlock();
        }
    }

}
