package de.invesdwin.util.collections.fast.concurrent;

import java.util.ListIterator;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class SynchronizedListIterator<E> implements ListIterator<E> {

    private final ListIterator<E> delegate;
    private final Object lock;

    public SynchronizedListIterator(final ListIterator<E> delegate) {
        this(delegate, delegate);
    }

    public SynchronizedListIterator(final ListIterator<E> delegate, final Object lock) {
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
    public boolean hasPrevious() {
        synchronized (lock) {
            return delegate.hasPrevious();
        }
    }

    @Override
    public E previous() {
        synchronized (lock) {
            return delegate.previous();
        }
    }

    @Override
    public int nextIndex() {
        synchronized (lock) {
            return delegate.nextIndex();
        }
    }

    @Override
    public int previousIndex() {
        synchronized (lock) {
            return delegate.previousIndex();
        }
    }

    @Override
    public void remove() {
        synchronized (lock) {
            delegate.remove();
        }
    }

    @Override
    public void set(final E e) {
        synchronized (lock) {
            delegate.set(e);
        }
    }

    @Override
    public void add(final E e) {
        synchronized (lock) {
            delegate.add(e);
        }
    }

}
