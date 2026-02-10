package de.invesdwin.util.collections.fast.concurrent.locked.readwrite;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.fast.IFastIterableList;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.iterable.concurrent.readwrite.ReadWriteLockedCloseableIterator;
import de.invesdwin.util.concurrent.lock.readwrite.IReadWriteLock;

@ThreadSafe
public class ReadWriteLockedFastIterableList<E> extends ReadWriteLockedList<E> implements IFastIterableList<E> {

    public ReadWriteLockedFastIterableList(final IFastIterableList<E> delegate, final IReadWriteLock lock) {
        super(delegate, lock);
    }

    @Override
    protected IFastIterableList<E> getDelegate() {
        return (IFastIterableList<E>) super.getDelegate();
    }

    @Override
    public E[] asArray(final E[] emptyArray) {
        getLock().writeLock().lock();
        try {
            return getDelegate().asArray(emptyArray);
        } finally {
            getLock().writeLock().unlock();
        }
    }

    @Override
    public ICloseableIterator<E> iterator() {
        getLock().readLock().lock();
        try {
            final ICloseableIterator<E> iterator = getDelegate().iterator();
            return new ReadWriteLockedCloseableIterator<E>(iterator, getLock());
        } finally {
            getLock().readLock().unlock();
        }
    }

    @Override
    public int removeRange(final int fromIndexInclusive, final int toIndexExclusive) {
        getLock().writeLock().lock();
        try {
            return getDelegate().removeRange(fromIndexInclusive, toIndexExclusive);
        } finally {
            getLock().writeLock().unlock();
        }
    }

}
