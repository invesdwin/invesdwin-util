package de.invesdwin.util.collections.iterable.sort;

import java.util.Comparator;
import java.util.NoSuchElementException;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.list.HighLowSortedList;
import de.invesdwin.util.error.FastNoSuchElementException;
import de.invesdwin.util.math.Integers;

/**
 * Keeps a buffer that can sort items coming in an occasionally semi random order
 */
@NotThreadSafe
public class SortedBufferingIterator<E> implements ICloseableIterator<E> {

    private ICloseableIterator<? extends E> delegate;
    private final Comparator<E> comparator;
    private final HighLowSortedList<E> buffer;
    private final int bufferSize;
    private final int halfBufferSize;
    private boolean reading = false;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public SortedBufferingIterator(final ICloseableIterator<? extends E> delegate, final Comparator comparator,
            final int bufferSize) {
        this.delegate = delegate;
        this.comparator = comparator;
        this.buffer = new HighLowSortedList<E>(comparator);
        this.bufferSize = Integer.max(10, bufferSize);
        this.halfBufferSize = Integers.max(1, bufferSize / 2);
    }

    @Override
    public boolean hasNext() {
        return readNext() != null;
    }

    @Override
    public E next() {
        final E readNext = readNext();
        if (readNext == null) {
            throw new FastNoSuchElementException("ASkippingIterator: readNext is null");
        }
        return readNext;
    }

    private E readNext() {
        if (buffer.size() < halfBufferSize) {
            fillBuffer();
        }
        reading = true;
        if (buffer.isEmpty()) {
            return null;
        } else {
            return buffer.remove(0);
        }
    }

    private void fillBuffer() {
        if (delegate == null) {
            return;
        }
        try {
            while (buffer.size() < bufferSize) {
                final E next = delegate.next();
                if (next == null) {
                    delegate.close();
                    delegate = null;
                } else if (!skip(next)) {
                    buffer.add(next);
                }
            }
            //catching nosuchelement might be faster sometimes than checking hasNext(), e.g. for LevelDB
        } catch (final NoSuchElementException e) {
            delegate.close();
            delegate = null;
            //end reached
        }
    }

    protected boolean skip(final E element) {
        //element is before first buffer element, maybe buffer size is too small?
        if (buffer.isEmpty()) {
            return false;
        }
        if (reading && comparator.compare(element, buffer.get(0)) < 0) {
            onElementSkipped(element);
            return true;
        }
        return false;
    }

    protected void onElementSkipped(final E element) {
        throw newWrongBufferSizeException(element, bufferSize);
    }

    public static IllegalStateException newWrongBufferSizeException(final Object element, final int bufferSize) {
        return new IllegalStateException("Can not prepend element [" + element
                + "] because it is beyond already read buffer [" + bufferSize + "], maybe increase buffer size?");
    }

    @Override
    public void close() {
        if (delegate != null) {
            delegate.close();
            delegate = null;
        }
        buffer.clear();
    }

}
