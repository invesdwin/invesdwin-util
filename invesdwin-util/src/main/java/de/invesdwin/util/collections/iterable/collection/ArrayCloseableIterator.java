package de.invesdwin.util.collections.iterable.collection;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.error.FastNoSuchElementException;

@NotThreadSafe
public class ArrayCloseableIterator<E> implements ICloseableIterator<E> {

    private final E[] array;
    private final int size;
    private int offset;

    public ArrayCloseableIterator(final E[] array, final int offset, final int count) {
        this.array = array;
        this.offset = offset;
        this.size = count + offset;
    }

    public ArrayCloseableIterator(final E[] array) {
        this(array, 0, array.length);
    }

    @Override
    public boolean hasNext() {
        return offset < size;
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
        offset = size;
    }

}
