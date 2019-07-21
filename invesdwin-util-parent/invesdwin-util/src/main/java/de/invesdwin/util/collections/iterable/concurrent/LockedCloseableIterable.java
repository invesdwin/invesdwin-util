package de.invesdwin.util.collections.iterable.concurrent;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.concurrent.lock.ILock;

@ThreadSafe
public class LockedCloseableIterable<E> implements ICloseableIterable<E> {

    private final ICloseableIterable<E> delegate;
    private final ILock lock;

    public LockedCloseableIterable(final ICloseableIterable<E> delegate, final ILock lock) {
        this.delegate = delegate;
        this.lock = lock;
    }

    @Override
    public ICloseableIterator<E> iterator() {
        return new LockedCloseableIterator<E>(delegate.iterator(), lock);
    }

}
