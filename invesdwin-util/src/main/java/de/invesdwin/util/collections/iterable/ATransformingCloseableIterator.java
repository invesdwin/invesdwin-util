package de.invesdwin.util.collections.iterable;

import java.util.Iterator;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public abstract class ATransformingCloseableIterator<S, R> extends ACloseableIterator<R> {

    private final ACloseableIterator<? extends S> delegate;

    public ATransformingCloseableIterator(final ACloseableIterator<? extends S> delegate) {
        this.delegate = delegate;
    }

    public ATransformingCloseableIterator(final Iterator<? extends S> delegate) {
        this.delegate = new WrapperCloseableIterator<S>(delegate);
    }

    @Override
    protected boolean innerHasNext() {
        return delegate.hasNext();
    }

    @Override
    protected R innerNext() {
        final S next = delegate.next();
        if (next == null) {
            return null;
        } else {
            return transform(next);
        }
    }

    protected abstract R transform(S value);

    @Override
    protected void innerClose() {
        delegate.close();
    }

}
