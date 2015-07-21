package de.invesdwin.util.collections.iterable;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public abstract class ASkippingIterable<E> implements ICloseableIterable<E> {

    private final ICloseableIterable<E> delegate;

    public ASkippingIterable(final ICloseableIterable<E> delegate) {
        this.delegate = delegate;
    }

    @Override
    public ICloseableIterator<E> iterator() {
        return new ASkippingIterator<E>(delegate.iterator()) {
            @Override
            protected boolean skip(final E element) {
                return ASkippingIterable.this.skip(element);
            }
        };
    }

    protected abstract boolean skip(final E element);

}
