package de.invesdwin.util.collections.iterable.collection;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterator;

@Immutable
public class ArrayCloseableIterable<E> implements ICloseableIterable<E> {

    private final E[] array;
    private final int offset;
    private final int count;

    public ArrayCloseableIterable(final E[] array, final int offset, final int count) {
        this.array = array;
        this.offset = offset;
        this.count = count;
    }

    public ArrayCloseableIterable(final E[] array) {
        this(array, 0, array.length);
    }

    @Override
    public ICloseableIterator<E> iterator() {
        return new ArrayCloseableIterator<>(array, offset, count);
    }

}
