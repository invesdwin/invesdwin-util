package de.invesdwin.util.collections.iterable.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.IFastToListProvider;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
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
            throw new FastNoSuchElementException("ListCloseableIterator: hasNext returned false");
        }
        return array[offset++];
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
                final ArrayList<E> list = new ArrayList<E>();
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

}
