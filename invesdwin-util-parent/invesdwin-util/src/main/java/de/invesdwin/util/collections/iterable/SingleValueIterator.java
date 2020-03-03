package de.invesdwin.util.collections.iterable;

import java.util.Arrays;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.collection.fast.IFastToListCloseableIterator;
import de.invesdwin.util.error.FastNoSuchElementException;

@NotThreadSafe
public class SingleValueIterator<E> implements IFastToListCloseableIterator<E> {

    private E singleValue;

    public SingleValueIterator(final E singleValue) {
        this.singleValue = singleValue;
    }

    @Override
    public boolean hasNext() {
        return singleValue != null;
    }

    @Override
    public E next() {
        if (hasNext()) {
            final E ret = singleValue;
            singleValue = null;
            return ret;
        } else {
            throw new FastNoSuchElementException("SingleValueIterator: hasNext is false");
        }
    }

    @Override
    public void close() {
        singleValue = null;
    }

    @Override
    public List<E> toList() {
        return Arrays.asList(singleValue);
    }

    @Override
    public List<E> toList(final List<E> list) {
        list.add(singleValue);
        return list;
    }

    @Override
    public E getHead() {
        return singleValue;
    }

    @Override
    public E getTail() {
        return singleValue;
    }

}
