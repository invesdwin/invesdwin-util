package de.invesdwin.util.collections.iterable;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public abstract class ASortedFeedsIterable<E> implements ICloseableIterable<E> {

    private final Iterable<Iterable<E>> feeds;

    @SuppressWarnings("unchecked")
    public ASortedFeedsIterable(final Iterable<? extends Iterable<? extends E>> feeds) {
        this.feeds = (Iterable<Iterable<E>>) feeds;
    }

    @Override
    public ICloseableIterator<E> iterator() {
        final ATransformingIterable<Iterable<E>, ICloseableIterator<E>> iterators = new ATransformingIterable<Iterable<E>, ICloseableIterator<E>>(
                WrapperCloseableIterable.maybeWrap(feeds)) {
            @Override
            protected ICloseableIterator<E> transform(final Iterable<E> value) {
                return WrapperCloseableIterable.maybeWrap(value).iterator();
            }
        };
        return new ASortedFeedsIterator<E>(iterators) {
            @Override
            protected Comparable<?> getCompareCriteria(final E e) {
                return ASortedFeedsIterable.this.getCompareCriteria(e);
            }
        };
    }

    protected abstract Comparable<?> getCompareCriteria(E e);

}
