package de.invesdwin.util.collections.iterable.collection;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.list.IFastToListProvider;
import de.invesdwin.util.collections.list.Lists;
import de.invesdwin.util.error.FastNoSuchElementException;

@NotThreadSafe
public class ReverseListCloseableIterator<E> implements ICloseableIterator<E>, IFastToListProvider<E> {

    private final List<? extends E> values;
    private int offset;
    private final int lowIndex;

    public ReverseListCloseableIterator(final List<? extends E> list, final int offset, final int count) {
        this.values = list;
        this.lowIndex = offset - count;
        this.offset = offset;
    }

    public ReverseListCloseableIterator(final List<? extends E> list) {
        this(list, 0, list.size());
    }

    @Override
    public boolean hasNext() {
        return offset >= 0 && lowIndex <= offset;
    }

    @Override
    public E next() {
        if (!hasNext()) {
            throw FastNoSuchElementException.getInstance("ReverseListCloseableIterator: hasNext returned false");
        }
        try {
            final E value = values.get(offset);
            offset--;
            return value;
        } catch (final IndexOutOfBoundsException e) {
            throw FastNoSuchElementException
                    .getInstance("ReverseListCloseableIterator: next threw IndexOutOfBoundsException");
        }
    }

    @Override
    public void close() {
        offset = lowIndex - 1;
    }

    @Override
    public List<E> toList() {
        try {
            if (lowIndex == 0 && offset == values.size()) {
                return Lists.reverse(new ArrayList<E>(values));
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
            if (lowIndex == 0 && offset == values.size()) {
                list.addAll(values);
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
            list.add(0, values.get(i));
        }
    }

    /**
     * @param fromIndex
     *            low endpoint (inclusive)
     * @param toIndex
     *            high endpoint (inclusive)
     */
    public static <T> ReverseListCloseableIterator<T> fromToInclusive(final List<T> list, final int from,
            final int to) {
        return new ReverseListCloseableIterator<>(list, from, to - from + 1);
    }

    /**
     * @param fromIndex
     *            low endpoint (inclusive)
     * @param toIndex
     *            high endpoint (exclusive)
     */
    public static <T> ReverseListCloseableIterator<T> fromToExclusive(final List<T> list, final int from,
            final int to) {
        return new ReverseListCloseableIterator<>(list, from, to - from);
    }

}
