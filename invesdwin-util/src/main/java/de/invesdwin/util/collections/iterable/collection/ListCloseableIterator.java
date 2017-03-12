package de.invesdwin.util.collections.iterable.collection;

import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.error.FastNoSuchElementException;

@NotThreadSafe
public class ListCloseableIterator<E> implements ICloseableIterator<E> {

    private final List<? extends E> list;
    private final int size;
    private int i = 0;

    public ListCloseableIterator(final List<? extends E> list) {
        this.list = list;
        this.size = list.size();
    }

    @Override
    public boolean hasNext() {
        return i < size;
    }

    @Override
    public E next() {
        if (!hasNext()) {
            throw new FastNoSuchElementException("ListCloseableIterator: hasNext returned false");
        }
        return list.get(i++);
    }

    @Override
    public void close() {
        i = size;
    }

}
