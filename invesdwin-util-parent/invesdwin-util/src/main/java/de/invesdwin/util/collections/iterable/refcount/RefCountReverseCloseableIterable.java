package de.invesdwin.util.collections.iterable.refcount;

import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.iterable.IReverseCloseableIterable;
import de.invesdwin.util.lang.string.description.TextDescription;

@ThreadSafe
public class RefCountReverseCloseableIterable<E> extends RefCountCloseableIterable<E>
        implements IReverseCloseableIterable<E> {

    public RefCountReverseCloseableIterable(final TextDescription name, final IReverseCloseableIterable<E> delegate) {
        super(name, delegate);
    }

    public RefCountReverseCloseableIterable(final TextDescription name, final ICloseableIterable<E> delegate,
            final AtomicInteger refCount) {
        super(name, delegate, refCount);
    }

    @Override
    public IReverseCloseableIterable<E> getDelegate() {
        return (IReverseCloseableIterable<E>) super.getDelegate();
    }

    @Override
    public ICloseableIterator<E> reverseIterator() {
        used = true;
        return new RefCountCloseableIterator<E>(name, getDelegate().reverseIterator(), getRefCount());
    }

}
