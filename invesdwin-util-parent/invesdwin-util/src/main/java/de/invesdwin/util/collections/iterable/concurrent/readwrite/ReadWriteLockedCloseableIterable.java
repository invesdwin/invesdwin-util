package de.invesdwin.util.collections.iterable.concurrent.readwrite;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.concurrent.lock.readwrite.IReadWriteLock;

@ThreadSafe
public class ReadWriteLockedCloseableIterable<E> implements ICloseableIterable<E> {

    private final ICloseableIterable<E> delegate;
    private final IReadWriteLock lock;

    public ReadWriteLockedCloseableIterable(final ICloseableIterable<E> delegate, final IReadWriteLock lock) {
        this.delegate = delegate;
        this.lock = lock;
    }

    @Override
    public ICloseableIterator<E> iterator() {
        return new ReadWriteLockedCloseableIterator<E>(delegate.iterator(), lock);
    }

}
