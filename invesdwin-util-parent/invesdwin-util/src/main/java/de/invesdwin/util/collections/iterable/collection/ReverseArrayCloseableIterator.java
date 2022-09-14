package de.invesdwin.util.collections.iterable.collection;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.list.IFastToListProvider;
import de.invesdwin.util.collections.list.Lists;
import de.invesdwin.util.error.FastNoSuchElementException;

@NotThreadSafe
public class ReverseArrayCloseableIterator<E> implements ICloseableIterator<E>, IFastToListProvider<E> {

    private final E[] array;
    private int offset;
    private final int lowIndex;

    public ReverseArrayCloseableIterator(final E[] array, final int offset, final int count) {
        this.array = array;
        this.lowIndex = offset - count;
        this.offset = offset;
    }

    public ReverseArrayCloseableIterator(final E[] array) {
        this(array, 0, array.length);
    }

    @Override
    public boolean hasNext() {
        return offset >= 0 && lowIndex <= offset;
    }

    @Override
    public E next() {
        if (!hasNext()) {
            throw FastNoSuchElementException.getInstance("ArrayCloseableIterator: hasNext returned false");
        }
        try {
            final E value = array[offset];
            offset--;
            return value;
        } catch (final IndexOutOfBoundsException e) {
            throw FastNoSuchElementException
                    .getInstance("ArrayCloseableIterator: next threw IndexOutOfBoundsException");
        }
    }

    @Override
    public void close() {
        offset = lowIndex - 1;
    }

    @Override
    public List<E> toList() {
        try {
            if (lowIndex == 0 && offset == array.length) {
                return Lists.reverse(Arrays.asList(array));
            } else {
                final ArrayList<E> list = new ArrayList<E>(offset);
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
            if (lowIndex == 0 && offset == array.length) {
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
        for (int i = offset; i >= lowIndex; i--) {
            list.add(0, array[i]);
        }
    }

    /**
     * @param fromIndex
     *            low endpoint (inclusive)
     * @param toIndex
     *            high endpoint (inclusive)
     */
    public static <T> ReverseArrayCloseableIterator<T> fromToInclusive(final T[] array, final int from, final int to) {
        return new ReverseArrayCloseableIterator<>(array, from, to - from + 1);
    }

    /**
     * @param fromIndex
     *            low endpoint (inclusive)
     * @param toIndex
     *            high endpoint (exclusive)
     */
    public static <T> ReverseArrayCloseableIterator<T> fromToExclusive(final T[] array, final int from, final int to) {
        return new ReverseArrayCloseableIterator<>(array, from, to - from);
    }

}
