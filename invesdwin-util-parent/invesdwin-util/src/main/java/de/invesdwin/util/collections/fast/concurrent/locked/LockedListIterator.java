package de.invesdwin.util.collections.fast.concurrent.locked;

import java.util.ListIterator;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.ILock;

@ThreadSafe
public class LockedListIterator<E> implements ListIterator<E> {

    private final ListIterator<E> delegate;
    private final ILock lock;

    public LockedListIterator(final ListIterator<E> delegate, final ILock lock) {
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
    public boolean hasPrevious() {
        lock.lock();
        try {
            return delegate.hasPrevious();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public E previous() {
        lock.lock();
        try {
            return delegate.previous();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int nextIndex() {
        lock.lock();
        try {
            return delegate.nextIndex();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int previousIndex() {
        lock.lock();
        try {
            return delegate.previousIndex();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void remove() {
        lock.lock();
        try {
            delegate.remove();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void set(final E e) {
        lock.lock();
        try {
            delegate.set(e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void add(final E e) {
        lock.lock();
        try {
            delegate.add(e);
        } finally {
            lock.unlock();
        }
    }

}
