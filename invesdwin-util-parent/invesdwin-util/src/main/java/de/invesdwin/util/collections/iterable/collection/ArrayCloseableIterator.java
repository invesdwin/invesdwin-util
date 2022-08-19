package de.invesdwin.util.collections.iterable.collection;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.list.IFastToListProvider;
import de.invesdwin.util.error.FastNoSuchElementException;

@NotThreadSafe
public class ArrayCloseableIterator<E> implements ICloseableIterator<E>, IFastToListProvider<E> {

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
            throw FastNoSuchElementException.getInstance("ArrayCloseableIterator: hasNext returned false");
        }
        try {
            return array[offset++];
        } catch (final IndexOutOfBoundsException e) {
            throw FastNoSuchElementException
                    .getInstance("ArrayCloseableIterator: next threw IndexOutOfBoundsException");
        }
    }

    @Override
    public void close() {
        offset = size;
    }

    @Override
    public List<E> toList() {
        try {
            if (offset == 0 && size == array.length) {
                return Arrays.asList(array);
            } else {
                final ArrayList<E> list = new ArrayList<E>(size);
                addAllTo(list);
                return list;
            }
        } finally {
            close();
        }
    }

    @Override
    public List<E> toList(final List<E> list) {
        try {
            if (offset == 0 && size == array.length) {
                list.addAll(Arrays.asList(array));
            } else {
                addAllTo(list);
            }
            return list;
        } finally {
            close();
        }
    }

    protected void addAllTo(final List<E> list) {
        for (int i = offset; i < size; i++) {
            list.add(array[i]);
        }
    }

    /**
     * @param fromIndex
     *            low endpoint (inclusive)
     * @param toIndex
     *            high endpoint (inclusive)
     */
    public static <T> ArrayCloseableIterator<T> fromToInclusive(final T[] array, final int from, final int to) {
        return new ArrayCloseableIterator<>(array, from, to - from + 1);
    }

    /**
     * @param fromIndex
     *            low endpoint (inclusive)
     * @param toIndex
     *            high endpoint (exclusive)
     */
    public static <T> ArrayCloseableIterator<T> fromToExclusive(final T[] array, final int from, final int to) {
        return new ArrayCloseableIterator<>(array, from, to - from);
    }

}
