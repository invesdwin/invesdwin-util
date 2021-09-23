package de.invesdwin.util.collections.iterable.sort;

import java.util.Comparator;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterator;

@SuppressWarnings("rawtypes")
@NotThreadSafe
public class SortedBufferingIterable<E> implements ICloseableIterable<E> {

    private final ICloseableIterable<? extends E> delegate;
    private final Comparator comparator;
    private final int bufferSize;

    public SortedBufferingIterable(final ICloseableIterable<? extends E> delegate, final Comparator comparator,
            final int bufferSize) {
        this.delegate = delegate;
        this.comparator = comparator;
        this.bufferSize = bufferSize;
    }

    @Override
    public ICloseableIterator<E> iterator() {
        return new SortedBufferingIterator<E>(delegate.iterator(), comparator, bufferSize) {
            @Override
            protected void onElementSkipped(final E element) {
                SortedBufferingIterable.this.onElementSkipped(element);
            }
        };
    }

    protected void onElementSkipped(final E element) {
        throw SortedBufferingIterator.newWrongBufferSizeException(element, bufferSize);
    }

}
