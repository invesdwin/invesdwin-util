package de.invesdwin.util.collections.iterable.sort;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.iterable.ATransformingIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.iterable.WrapperCloseableIterable;
import de.invesdwin.util.lang.comparator.IComparator;

@NotThreadSafe
public class SortedFeedsIterable<E> implements ICloseableIterable<E> {

    private final IComparator<? super E> comparator;
    private final Iterable<Iterable<E>> feeds;

    @SuppressWarnings("unchecked")
    public SortedFeedsIterable(final IComparator<? super E> comparator, final Iterable<? extends E>... feeds) {
        this(comparator, Arrays.asList(feeds));
    }

    @SuppressWarnings("unchecked")
    public SortedFeedsIterable(final IComparator<? super E> comparator,
            final Iterable<? extends Iterable<? extends E>> feeds) {
        this.comparator = comparator;
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
        return new SortedFeedsIterator<E>(comparator, iterators);
    }

}
