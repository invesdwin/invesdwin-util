package de.invesdwin.util.collections.iterable.collection;

import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.list.IFastToListProvider;
import de.invesdwin.util.error.FastNoSuchElementException;

@NotThreadSafe
public class ListCloseableIterator<E> implements ICloseableIterator<E>, IFastToListProvider<E> {

    private final List<? extends E> list;
    private final int count;
    private int offset = 0;

    public ListCloseableIterator(final List<? extends E> list, final int offset, final int count) {
        this.list = list;
        this.offset = offset;
        this.count = count;
    }

    public ListCloseableIterator(final List<? extends E> list) {
        this(list, 0, list.size());
    }

    @Override
    public boolean hasNext() {
        return offset < count;
    }

    @Override
    public E next() {
        if (!hasNext()) {
            throw FastNoSuchElementException.getInstance("ListCloseableIterator: hasNext returned false");
        }
        return list.get(offset++);
    }

    @Override
    public void close() {
        offset = count;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<E> toList() {
        try {
            if (offset == 0) {
                return (List<E>) list;
            } else {
                return (List<E>) list.subList(offset, list.size());
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
