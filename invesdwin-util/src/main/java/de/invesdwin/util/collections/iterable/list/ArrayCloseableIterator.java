package de.invesdwin.util.collections.iterable.list;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.error.FastNoSuchElementException;

@NotThreadSafe
public class ArrayCloseableIterator<E> implements ICloseableIterator<E> {

    private final E[] array;
    private final int length;
    private int offset;

    public ArrayCloseableIterator(final E[] array, final int offset, final int length) {
        this.array = array;
        this.offset = offset;
        this.length = length + offset;
    }

    public ArrayCloseableIterator(final E[] array) {
        this(array, 0, array.length);
    }

    @Override
    public boolean hasNext() {
        return offset < length;
    }

    @Override
    public E next() {
        if (!hasNext()) {
            throw new FastNoSuchElementException("ListCloseableIterator: hasNext returned false");
        }
        return array[offset++];
    }

    @Override
    public void close() {
        offset = length;
    }

}
