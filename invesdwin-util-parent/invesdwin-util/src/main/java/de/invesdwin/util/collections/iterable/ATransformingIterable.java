package de.invesdwin.util.collections.iterable;

import javax.annotation.concurrent.Immutable;

@Immutable
public abstract class ATransformingIterable<S, R> implements ICloseableIterable<R> {

    private final ICloseableIterable<? extends S> delegate;

    public ATransformingIterable(final ICloseableIterable<? extends S> delegate) {
        if (delegate == null) {
            throw new NullPointerException("delegate must not be null");
        }
        this.delegate = delegate;
    }

    protected abstract R transform(S value);

    @Override
    public ICloseableIterator<R> iterator() {
        return new ATransformingIterator<S, R>(delegate.iterator()) {

            @Override
            protected R transform(final S value) {
                return ATransformingIterable.this.transform(value);
            }

        };
    }

}
