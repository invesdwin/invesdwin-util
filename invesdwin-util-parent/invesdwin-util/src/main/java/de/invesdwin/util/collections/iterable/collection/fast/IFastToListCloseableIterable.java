package de.invesdwin.util.collections.iterable.collection.fast;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.list.IFastToListProvider;

public interface IFastToListCloseableIterable<E> extends IFastToListProvider<E>, ICloseableIterable<E> {

    @Override
    IFastToListCloseableIterator<E> iterator();

}
