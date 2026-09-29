package de.invesdwin.util.collections.iterable.refcount;

import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.ACloseableIterator;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.lang.string.description.TextDescription;

@NotThreadSafe
public class RefCountCloseableIterator<E> extends ACloseableIterator<E> {

    private final ICloseableIterator<E> delegate;
    private AtomicInteger refCount;

    public RefCountCloseableIterator(final TextDescription name, final ICloseableIterator<E> delegate) {
        this(name, delegate, new AtomicInteger());
    }

    public RefCountCloseableIterator(final TextDescription name, final ICloseableIterator<E> delegate,
            final AtomicInteger refCount) {
        super(name);
        this.delegate = delegate;
        this.refCount = refCount;
        refCount.incrementAndGet();
    }

    @Override
    public boolean innerHasNext() {
        return delegate.hasNext();
    }

    @Override
    public E innerNext() {
        return delegate.next();
    }

    @Override
    public void innerClose() {
        if (refCount != null) {
            delegate.close();
            refCount.decrementAndGet();
            refCount = null;
        }
    }
}