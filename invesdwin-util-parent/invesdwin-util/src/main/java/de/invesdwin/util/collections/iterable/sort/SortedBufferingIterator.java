package de.invesdwin.util.collections.iterable.sort;

import java.util.NoSuchElementException;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.list.HighLowSortedList;
import de.invesdwin.util.error.FastNoSuchElementException;
import de.invesdwin.util.lang.comparator.IComparator;
import de.invesdwin.util.math.Integers;

/**
 * Keeps a buffer that can sort items coming in an occasionally semi random order
 */
@NotThreadSafe
public class SortedBufferingIterator<E> implements ICloseableIterator<E> {

    private ICloseableIterator<? extends E> delegate;
    private final IComparator<E> comparator;
    private final HighLowSortedList<E> buffer;
    private final int bufferSize;
    private final int halfBufferSize;
    private boolean reading = false;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public SortedBufferingIterator(final ICloseableIterator<? extends E> delegate, final IComparator<E> comparator,
            final int bufferSize) {
        this.delegate = delegate;
        this.comparator = comparator;
        this.buffer = new HighLowSortedList<E>(comparator.asNotNullSafe());
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
        if (reading) {
            final E firstBufferElement = buffer.get(0);
            if (comparator.compare(element, firstBufferElement) < 0) {
                onElementSkipped(element, firstBufferElement);
                return true;
            }
        }
        return false;
    }

    protected void onElementSkipped(final E element, final E firstBufferElement) {
        throw newWrongBufferSizeException(element, firstBufferElement, bufferSize);
    }

    public static IllegalStateException newWrongBufferSizeException(final Object element,
            final Object firstBufferElement, final int bufferSize) {
        return new IllegalStateException("Can not prepend element [" + element
                + "] because it is beyond already read buffer, maybe increase buffer size? bufferSize=" + bufferSize
                + " firstBufferElement=" + firstBufferElement + "");
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
