package de.invesdwin.util.collections.iterable.collection;

import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.list.IFastToListProvider;
import de.invesdwin.util.error.FastNoSuchElementException;

@NotThreadSafe
public class ListCloseableIterator<E> implements ICloseableIterator<E>, IFastToListProvider<E> {

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
            throw FastNoSuchElementException.getInstance("ListCloseableIterator: hasNext returned false");
        }
        return list.get(i++);
    }

    @Override
    public void close() {
        i = size;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<E> toList() {
        try {
            if (i == 0) {
                return (List<E>) list;
            } else {
                return (List<E>) list.subList(i, list.size());
            }
        } finally {
            close();
        }
    }

    @Override
    public List<E> toList(final List<E> list) {
        list.addAll(toList());
        return list;
    }

}
