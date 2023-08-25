package de.invesdwin.util.collections.iterable.collection.arraylist;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.concurrent.lock.ILock;

@ThreadSafe
public class LockedArrayListCloseableIterable<E> implements IArrayListCloseableIterable<E> {

    private final IArrayListCloseableIterable<E> delegate;
    private final ILock lock;

    public LockedArrayListCloseableIterable(final IArrayListCloseableIterable<E> delegate, final ILock lock) {
        this.delegate = delegate;
        this.lock = lock;
    }

    @Override
    public ICloseableIterator<E> reverseIterator() {
        lock.lock();
        try {
            return delegate.reverseIterator();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public ICloseableIterator<E> reverseIterator(final int highIndex, final int lowIndex) {
        lock.lock();
        try {
            return delegate.reverseIterator(highIndex, lowIndex);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public ICloseableIterator<E> iterator() {
        lock.lock();
        try {
            return delegate.iterator();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public ICloseableIterator<E> iterator(final int lowIndex, final int highIndex) {
        lock.lock();
        try {
            return delegate.iterator(lowIndex, highIndex);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public List<E> toList() {
        lock.lock();
        try {
            return delegate.toList();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public List<E> toList(final List<E> list) {
        lock.lock();
        try {
            return delegate.toList(list);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public ArrayList<? extends E> getList() {
        lock.lock();
        try {
            return delegate.getList();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void reset() {
        lock.lock();
        try {
            delegate.reset();
        } finally {
            lock.unlock();
        }
    }

}
