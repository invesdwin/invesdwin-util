package de.invesdwin.util.collections.fast.concurrent.locked;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.fast.IFastIterableList;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.iterable.concurrent.LockedCloseableIterator;
import de.invesdwin.util.concurrent.lock.ILock;

@ThreadSafe
public class LockedFastIterableList<E> extends LockedList<E> implements IFastIterableList<E> {

    public LockedFastIterableList(final IFastIterableList<E> delegate, final ILock lock) {
        super(delegate, lock);
    }

    @Override
    protected IFastIterableList<E> getDelegate() {
        return (IFastIterableList<E>) super.getDelegate();
    }

    @Override
    public E[] asArray(final E[] emptyArray) {
        getLock().lock();
        try {
            return getDelegate().asArray(emptyArray);
        } finally {
            getLock().unlock();
        }
    }

    @Override
    public ICloseableIterator<E> iterator() {
        getLock().lock();
        try {
            final ICloseableIterator<E> iterator = getDelegate().iterator();
            return new LockedCloseableIterator<E>(iterator, getLock());
        } finally {
            getLock().unlock();
        }
    }

    @Override
    public int removeRange(final int fromIndexInclusive, final int toIndexExclusive) {
        getLock().lock();
        try {
            return getDelegate().removeRange(fromIndexInclusive, toIndexExclusive);
        } finally {
            getLock().unlock();
        }
    }

}
