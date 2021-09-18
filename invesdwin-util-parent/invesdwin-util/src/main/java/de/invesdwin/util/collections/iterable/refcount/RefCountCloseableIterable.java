package de.invesdwin.util.collections.iterable.refcount;

import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterator;

@ThreadSafe
public class RefCountCloseableIterable<E> implements ICloseableIterable<E> {

    protected volatile boolean used = false;
    private final ICloseableIterable<E> delegate;
    private final AtomicInteger refCount;

    public RefCountCloseableIterable(final ICloseableIterable<E> delegate) {
        this(delegate, new AtomicInteger());
    }

    public RefCountCloseableIterable(final ICloseableIterable<E> delegate, final AtomicInteger refCount) {
        this.delegate = delegate;
        this.refCount = refCount;
    }

    public ICloseableIterable<E> getDelegate() {
        return delegate;
    }

    public AtomicInteger getRefCount() {
        return refCount;
    }

    public boolean isUsed() {
        return used;
    }

    @Override
    public ICloseableIterator<E> iterator() {
        used = true;
        return new RefCountCloseableIterator<E>(delegate.iterator(), refCount);
    }

}
