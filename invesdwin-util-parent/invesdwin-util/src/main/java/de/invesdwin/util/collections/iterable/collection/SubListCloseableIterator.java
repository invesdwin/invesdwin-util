package de.invesdwin.util.collections.iterable.collection;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.iterable.collection.arraylist.SynchronizedArrayListCloseableIterable;
import de.invesdwin.util.collections.list.IFastToListProvider;
import de.invesdwin.util.error.FastNoSuchElementException;

@NotThreadSafe
public class SubListCloseableIterator<E> implements ICloseableIterator<E>, IFastToListProvider<E> {

    private final List<E> array;
    private final int size;
    private int offset;

    public SubListCloseableIterator(final List<E> array, final int offset, final int count) {
        this.array = array;
        this.offset = offset;
        this.size = count + offset;
        if (array.size() < size) {
            throw new IllegalArgumentException(
                    "Maybe a spot where " + SynchronizedArrayListCloseableIterable.class.getSimpleName()
                            + " is needed: array.size [" + array.size() + "] < size[" + size + "]");
        }
    }

    public SubListCloseableIterator(final List<E> array) {
        this(array, 0, array.size());
    }

    @Override
    public boolean hasNext() {
        return offset < size;
    }

    @Override
    public E next() {
        if (!hasNext()) {
            throw FastNoSuchElementException.getInstance("ArrayListCloseableIterator: hasNext returned false");
        }
        try {
            final E value = array.get(offset++);
            return value;
        } catch (final IndexOutOfBoundsException e) {
            throw FastNoSuchElementException
                    .getInstance("ArrayListCloseableIterator: next threw IndexOutOfBoundsException");
        }
    }

    @Override
    public void close() {
        offset = size;
    }

    @Override
    public List<E> toList() {
        try {
            if (offset == 0 && size == array.size()) {
                return new ArrayList<E>(array);
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
            if (offset == 0 && size == array.size()) {
                list.addAll(array);
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
            list.add(array.get(i));
        }
    }

    /**
     * @param fromIndex
     *            low endpoint (inclusive)
     * @param toIndex
     *            high endpoint (inclusive)
     */
    public static <T> SubListCloseableIterator<T> fromToInclusive(final List<T> array, final int from, final int to) {
        return new SubListCloseableIterator<>(array, from, to - from + 1);
    }

    /**
     * @param fromIndex
     *            low endpoint (inclusive)
     * @param toIndex
     *            high endpoint (exclusive)
     */
    public static <T> SubListCloseableIterator<T> fromToExclusive(final List<T> array, final int from, final int to) {
        return new SubListCloseableIterator<>(array, from, to - from);
    }

}
