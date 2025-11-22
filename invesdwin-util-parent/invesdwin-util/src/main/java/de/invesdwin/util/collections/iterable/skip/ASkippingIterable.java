package de.invesdwin.util.collections.iterable.skip;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterator;

@Immutable
public abstract class ASkippingIterable<E> implements ICloseableIterable<E> {

    protected final ICloseableIterable<? extends E> delegate;

    public ASkippingIterable(final ICloseableIterable<? extends E> delegate) {
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

    protected abstract boolean skip(E element);

}
