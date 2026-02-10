package de.invesdwin.util.collections.fast.concurrent.locked.readwrite;

import java.util.Iterator;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.readwrite.IReadWriteLock;

@ThreadSafe
public class ReadWriteLockedIterable<E> implements Iterable<E> {

    private final Iterable<E> delegate;
    private final IReadWriteLock lock;

    public ReadWriteLockedIterable(final Iterable<E> delegate, final IReadWriteLock lock) {
        this.delegate = delegate;
        this.lock = lock;
    }

    @Override
    public Iterator<E> iterator() {
        return new ReadWriteLockedIterator<E>(delegate.iterator(), lock);
    }

}
