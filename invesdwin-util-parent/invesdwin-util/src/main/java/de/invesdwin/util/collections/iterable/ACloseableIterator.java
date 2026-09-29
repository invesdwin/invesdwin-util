package de.invesdwin.util.collections.iterable;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.internal.ADebugCloseableIteratorImpl;
import de.invesdwin.util.collections.iterable.internal.AFastCloseableIteratorImpl;
import de.invesdwin.util.collections.iterable.internal.ICloseableIteratorImpl;
import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.lang.string.description.TextDescription;

@NotThreadSafe
public abstract class ACloseableIterator<E> implements ICloseableIterator<E> {

    private final ICloseableIteratorImpl<E> impl;

    public ACloseableIterator(final TextDescription name) {
        if (Throwables.isDebugStackTraceEnabled()) {
            this.impl = new ADebugCloseableIteratorImpl<E>(name, getClass().getName()) {

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

                @Override
                public void close() {
                    super.close();
                    ACloseableIterator.this.innerClose();
                }

            };
        } else {
            this.impl = new AFastCloseableIteratorImpl<E>(name, getClass().getName()) {

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

                @Override
                public void close() {
                    super.close();
                    ACloseableIterator.this.innerClose();
                }

            };
        }
    }

    @Override
    public final boolean hasNext() {
        return impl.hasNext();
    }

    protected abstract boolean innerHasNext();

    @Override
    public final E next() {
        return impl.next();
    }

    protected abstract E innerNext();

    @Override
    public final void remove() {
        impl.remove();
    }

    protected void innerRemove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void close() {
        impl.close();
    }

    protected abstract void innerClose();

    public boolean isClosed() {
        return impl.isClosed();
    }

}
