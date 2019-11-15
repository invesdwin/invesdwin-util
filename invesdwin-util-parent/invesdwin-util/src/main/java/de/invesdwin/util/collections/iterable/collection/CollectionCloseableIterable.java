package de.invesdwin.util.collections.iterable.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.list.IFastToListProvider;

@Immutable
public class CollectionCloseableIterable<E> implements ICloseableIterable<E>, IFastToListProvider<E> {

    private final Collection<? extends E> collection;

    public CollectionCloseableIterable(final Collection<? extends E> collection) {
        this.collection = collection;
    }

    @Override
    public ICloseableIterator<E> iterator() {
        return new CollectionCloseableIterator<E>(collection);
    }

    @Override
    public List<E> toList() {
        return new ArrayList<E>(collection);
    }

    @Override
    public List<E> toList(final List<E> list) {
        list.addAll(collection);
        return list;
    }

}
