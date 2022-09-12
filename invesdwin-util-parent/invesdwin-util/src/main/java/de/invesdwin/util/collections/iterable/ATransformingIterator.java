package de.invesdwin.util.collections.iterable;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public abstract class ATransformingIterator<S, R> implements ICloseableIterator<R> {

    private final ICloseableIterator<? extends S> delegate;

    public ATransformingIterator(final ICloseableIterator<? extends S> delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean hasNext() {
        return delegate.hasNext();
    }

    @Override
    public R next() {
        final S next = delegate.next();
        if (next == null) {
            throw new NullPointerException("ATransformingCloseableIterator: next is null");
        } else {
            return transform(next);
        }
    }

    protected abstract R transform(S value);

    @Override
    public void close() {
        delegate.close();
    }

}
