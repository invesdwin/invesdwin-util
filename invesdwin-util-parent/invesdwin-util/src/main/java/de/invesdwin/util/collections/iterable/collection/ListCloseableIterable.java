package de.invesdwin.util.collections.iterable.collection;

import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.iterable.collection.fast.IFastToListCloseableIterable;
import de.invesdwin.util.collections.iterable.collection.fast.IFastToListCloseableIterator;

@Immutable
public class ListCloseableIterable<E> implements IFastToListCloseableIterable<E> {

    private final List<? extends E> list;

    public ListCloseableIterable(final List<? extends E> list) {
        this.list = list;
    }

    @Override
    public IFastToListCloseableIterator<E> iterator() {
        /*
         * try to save us the effort for checking the mod count and bounds all the time by trying to directly go to the
         * underlying array
         */
        return new ListCloseableIterator<E>(list);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<E> toList() {
        return (List<E>) list;
    }

    @Override
    public List<E> toList(final List<E> list) {
        list.addAll(this.list);
        return list;
    }

    @Override
    public E getHead() {
        return list.get(0);
    }

    @Override
    public E getTail() {
        return list.get(list.size() - 1);
    }

}
