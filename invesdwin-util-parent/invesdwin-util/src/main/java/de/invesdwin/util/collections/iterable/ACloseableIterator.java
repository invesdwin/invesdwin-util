package de.invesdwin.util.collections.iterable;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.internal.ADebugCloseableIteratorImpl;
import de.invesdwin.util.collections.iterable.internal.AFastCloseableIteratorImpl;
import de.invesdwin.util.collections.iterable.internal.ICloseableIteratorImpl;
import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.lang.description.TextDescription;

@NotThreadSafe
public abstract class ACloseableIterator<E> implements ICloseableIterator<E> {

    private final ICloseableIteratorImpl<E> delegate;

    public ACloseableIterator(final TextDescription name) {
        if (Throwables.isDebugStackTraceEnabled()) {
            this.delegate = new ADebugCloseableIteratorImpl<E>(name, getClass().getName()) {

                @Override
                protected boolean innerHasNext() {
                    return ACloseableIterator.this.innerHasNext();
                }

                @Override
                protected E innerNext() {
                    return ACloseableIterator.this.innerNext();
                }

                @Override
                protected void innerRemove() {
                    ACloseableIterator.this.innerRemove();
                }

            };
        } else {
            this.delegate = new AFastCloseableIteratorImpl<E>(name, getClass().getName()) {

                @Override
                protected boolean innerHasNext() {
                    return ACloseableIterator.this.innerHasNext();
                }

                @Override
                protected E innerNext() {
                    return ACloseableIterator.this.innerNext();
                }

                @Override
                protected void innerRemove() {
                    ACloseableIterator.this.innerRemove();
                }

            };
        }
    }

    @Override
    public final boolean hasNext() {
        return delegate.hasNext();
    }

    protected abstract boolean innerHasNext();

    @Override
    public final E next() {
        return delegate.next();
    }

    protected abstract E innerNext();

    @Override
    public final void remove() {
        delegate.remove();
    }

    protected void innerRemove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() {
        delegate.close();
    }

    public boolean isClosed() {
        return delegate.isClosed();
    }

}
