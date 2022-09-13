package de.invesdwin.util.collections.iterable.collection.arraylist;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.iterable.ICloseableIterator;

@ThreadSafe
public class SynchronizedArrayListCloseableIterable<E> implements IArrayListCloseableIterable<E> {

    private final IArrayListCloseableIterable<E> delegate;

    public SynchronizedArrayListCloseableIterable(final IArrayListCloseableIterable<E> delegate) {
        this.delegate = delegate;
    }

    @Override
    public synchronized ICloseableIterator<E> reverseIterator() {
        return delegate.reverseIterator();
    }

    @Override
    public synchronized ICloseableIterator<E> iterator() {
        return delegate.iterator();
    }

    @Override
    public synchronized List<E> toList() {
        return delegate.toList();
    }

    @Override
    public synchronized List<E> toList(final List<E> list) {
        return delegate.toList(list);
    }

    @Override
    public synchronized ArrayList<? extends E> getArrayList() {
        return delegate.getArrayList();
    }

    @Override
    public synchronized void reset() {
        delegate.reset();
    }

}
