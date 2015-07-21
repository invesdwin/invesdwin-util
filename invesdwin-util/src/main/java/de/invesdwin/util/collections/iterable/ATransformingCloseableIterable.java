package de.invesdwin.util.collections.iterable;

import javax.annotation.concurrent.Immutable;

@Immutable
public abstract class ATransformingCloseableIterable<S, R> implements ICloseableIterable<R> {

    private final ICloseableIterable<? extends S> delegate;

    public ATransformingCloseableIterable(final ICloseableIterable<? extends S> delegate) {
        this.delegate = delegate;
    }

    public ATransformingCloseableIterable(final Iterable<? extends S> delegate) {
        this.delegate = new WrapperCloseableIterable<S>(delegate);
    }

    protected abstract R transform(S value);

    @Override
    public ICloseableIterator<R> iterator() {
        return new ATransformingCloseableIterator<S, R>(delegate.iterator()) {

            @Override
            protected R transform(final S value) {
                return ATransformingCloseableIterable.this.transform(value);
            }

        };
    }

}
