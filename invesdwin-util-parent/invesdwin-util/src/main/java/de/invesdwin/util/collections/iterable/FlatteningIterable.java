package de.invesdwin.util.collections.iterable;

import java.util.Iterator;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class FlatteningIterable<E> implements ICloseableIterable<E> {

    private final ICloseableIterable<? extends Iterable<? extends E>> delegate;

    @SafeVarargs
    public FlatteningIterable(final Iterable<? extends E>... delegate) {
        this.delegate = WrapperCloseableIterable.maybeWrap(delegate);
    }

    public FlatteningIterable(final ICloseableIterable<? extends Iterable<? extends E>> delegate) {
        this.delegate = delegate;
    }

    @Override
    public ICloseableIterator<E> iterator() {
        final ATransformingCloseableIterator<Iterable<? extends E>, Iterator<? extends E>> transformingDelegate = new ATransformingCloseableIterator<Iterable<? extends E>, Iterator<? extends E>>(
                delegate.iterator()) {
            @Override
            protected Iterator<? extends E> transform(final Iterable<? extends E> value) {
                return WrapperCloseableIterable.maybeWrap(value).iterator();
            }
        };
        return new FlatteningIterator<E>(transformingDelegate);
    }

}
