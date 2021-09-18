package de.invesdwin.util.collections.iterable.refcount;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.iterable.IReverseCloseableIterable;

@ThreadSafe
public class RefCountReverseCloseableIterable<E> extends RefCountCloseableIterable<E>
        implements IReverseCloseableIterable<E> {

    public RefCountReverseCloseableIterable(final IReverseCloseableIterable<E> delegate) {
        super(delegate);
    }

    @Override
    public IReverseCloseableIterable<E> getDelegate() {
        return (IReverseCloseableIterable<E>) super.getDelegate();
    }

    @Override
    public ICloseableIterator<E> reverseIterator() {
        used = true;
        return new RefCountCloseableIterator<E>(getDelegate().reverseIterator(), getRefCount());
    }

}
