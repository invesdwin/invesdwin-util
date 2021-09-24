package de.invesdwin.util.collections.iterable.sort;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.lang.comparator.IComparator;

@SuppressWarnings("rawtypes")
@NotThreadSafe
public class SortedBufferingIterable<E> implements ICloseableIterable<E> {

    private final ICloseableIterable<? extends E> delegate;
    private final IComparator comparator;
    private final int bufferSize;

    public SortedBufferingIterable(final ICloseableIterable<? extends E> delegate, final IComparator comparator,
            final int bufferSize) {
        this.delegate = delegate;
        this.comparator = comparator;
        this.bufferSize = bufferSize;
    }

    @Override
    public ICloseableIterator<E> iterator() {
        return new SortedBufferingIterator<E>(delegate.iterator(), comparator, bufferSize) {
            @Override
            protected void onElementSkipped(final E element, final E firstBufferElement) {
                SortedBufferingIterable.this.onElementSkipped(element, firstBufferElement);
            }
        };
    }

    protected void onElementSkipped(final E element, final E firstBufferElement) {
        throw SortedBufferingIterator.newWrongBufferSizeException(element, firstBufferElement, bufferSize);
    }

}
