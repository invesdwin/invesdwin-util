package de.invesdwin.util.collections.iterable;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public final class WrapperCloseableIterable<E> implements ICloseableIterable<E> {

    private final Iterable<? extends E> delegate;

    private WrapperCloseableIterable(final Iterable<? extends E> delegate) {
        this.delegate = delegate;
    }

    @Override
    public ICloseableIterator<E> iterator() {
        return WrapperCloseableIterator.maybeWrap(delegate.iterator());
    }

    public static <T> ICloseableIterable<T> maybeWrap(final Iterable<T> iterator) {
        if (iterator instanceof ICloseableIterable) {
            return (ICloseableIterable<T>) iterator;
        } else {
            return new WrapperCloseableIterable<T>(iterator);
        }
    }

}
