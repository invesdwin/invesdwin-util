package de.invesdwin.util.collections.iterable.collection.arraylist;

import java.util.ArrayList;

import de.invesdwin.util.collections.iterable.IReverseCloseableIterable;
import de.invesdwin.util.collections.list.IFastToListProvider;

public interface IArrayListCloseableIterable<E> extends IReverseCloseableIterable<E>, IFastToListProvider<E> {

    ArrayList<? extends E> getArrayList();

    void reset();

}
