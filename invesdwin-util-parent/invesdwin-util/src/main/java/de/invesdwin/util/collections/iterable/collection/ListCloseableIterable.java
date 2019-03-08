package de.invesdwin.util.collections.iterable.collection;

import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.IFastToListProvider;
import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterator;

@Immutable
public class ListCloseableIterable<E> implements ICloseableIterable<E>, IFastToListProvider<E> {

    private final List<? extends E> list;

    public ListCloseableIterable(final List<? extends E> list) {
        this.list = list;
    }

    @Override
    public ICloseableIterator<E> iterator() {
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

}
