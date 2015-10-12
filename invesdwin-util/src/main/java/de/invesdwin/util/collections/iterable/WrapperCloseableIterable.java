package de.invesdwin.util.collections.iterable;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class WrapperCloseableIterable<E> implements ICloseableIterable<E> {

    private final Iterable<? extends E> delegate;

    public WrapperCloseableIterable(final Iterable<? extends E> delegate) {
        this.delegate = delegate;
    }

    @Override
    public ACloseableIterator<E> iterator() {
        return new WrapperCloseableIterator<E>(delegate.iterator());
    }

    public static <T> ICloseableIterable<T> maybeWrap(final Iterable<T> iterator) {
        if (iterator instanceof ICloseableIterable) {
            return (ICloseableIterable<T>) iterator;
        } else {
            return new WrapperCloseableIterable<T>(iterator);
        }
    }

}
