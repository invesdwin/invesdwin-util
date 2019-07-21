package de.invesdwin.util.collections.iterable.concurrent;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterator;

@ThreadSafe
public class SynchronizedCloseableIterable<E> implements ICloseableIterable<E> {

    private final ICloseableIterable<E> delegate;

    public SynchronizedCloseableIterable(final ICloseableIterable<E> delegate) {
        this.delegate = delegate;
    }

    @Override
    public ICloseableIterator<E> iterator() {
        return new SynchronizedCloseableIterator<E>(delegate.iterator());
    }

}
