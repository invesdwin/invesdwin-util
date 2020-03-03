package de.invesdwin.util.collections.iterable.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.iterable.collection.fast.IFastToListCloseableIterable;
import de.invesdwin.util.collections.iterable.collection.fast.IFastToListCloseableIterator;

@Immutable
public class CollectionCloseableIterable<E> implements IFastToListCloseableIterable<E> {

    private final Collection<? extends E> collection;

    public CollectionCloseableIterable(final Collection<? extends E> collection) {
        this.collection = collection;
    }

    @Override
    public IFastToListCloseableIterator<E> iterator() {
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

    @Override
    public E getHead() {
        return collection.iterator().next();
    }

    @Override
    public E getTail() {
        throw new UnsupportedOperationException("too slow on collections");
    }

}
