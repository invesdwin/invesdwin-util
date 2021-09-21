package de.invesdwin.util.collections.fast.concurrent.locked;

import java.util.Iterator;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.ILock;

@ThreadSafe
public class LockedIterable<E> implements Iterable<E> {

    private final Iterable<E> delegate;
    private final ILock lock;

    public LockedIterable(final Iterable<E> delegate, final ILock lock) {
        this.delegate = delegate;
        this.lock = lock;
    }

    @Override
    public Iterator<E> iterator() {
        return new LockedIterator<E>(delegate.iterator(), lock);
    }

}
