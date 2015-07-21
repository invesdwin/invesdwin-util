package de.invesdwin.util.collections.iterable;

import java.io.IOException;
import java.util.Iterator;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public abstract class ATransformingCloseableIterator<S, R> implements ICloseableIterator<R> {

    private final ICloseableIterator<? extends S> delegate;

    public ATransformingCloseableIterator(final ICloseableIterator<? extends S> delegate) {
        this.delegate = delegate;
    }

    public ATransformingCloseableIterator(final Iterator<? extends S> delegate) {
        this.delegate = new WrapperCloseableIterator<S>(delegate);
    }

    @Override
    public boolean hasNext() {
        return delegate.hasNext();
    }

    @SuppressWarnings("null")
    @Override
    public R next() {
        final S next = delegate.next();
        if (next == null) {
            return null;
        } else {
            return transform(next);
        }
    }

    protected abstract R transform(S value);

    @Override
    public void close() throws IOException {
        delegate.close();
    }

}
