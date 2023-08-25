package de.invesdwin.util.collections.iterable.collection.arraylist;

import java.util.ArrayList;

import de.invesdwin.util.collections.iterable.collection.IListCloseableIterable;

public interface IArrayListCloseableIterable<E> extends IListCloseableIterable<E> {

    @Override
    ArrayList<? extends E> getList();

    void reset();

}
