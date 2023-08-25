package de.invesdwin.util.collections.iterable.collection;

import java.util.List;

import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.iterable.IReverseCloseableIterable;
import de.invesdwin.util.collections.list.IFastToListProvider;

public interface IListCloseableIterable<E> extends IReverseCloseableIterable<E>, IFastToListProvider<E> {

    List<? extends E> getList();

    ICloseableIterator<E> iterator(int lowIndex, int highIndex);

    ICloseableIterator<E> reverseIterator(int highIndex, int lowIndex);

}
