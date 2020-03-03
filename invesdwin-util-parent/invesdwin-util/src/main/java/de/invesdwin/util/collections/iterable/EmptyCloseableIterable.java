package de.invesdwin.util.collections.iterable;

import java.util.Collections;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.iterable.collection.fast.IFastToListCloseableIterable;
import de.invesdwin.util.collections.iterable.collection.fast.IFastToListCloseableIterator;

@Immutable
public final class EmptyCloseableIterable<E> implements IFastToListCloseableIterable<E> {

    @SuppressWarnings("rawtypes")
    private static final EmptyCloseableIterable INSTANCE = new EmptyCloseableIterable();

    private EmptyCloseableIterable() {
    }

    @Override
    public IFastToListCloseableIterator<E> iterator() {
        return EmptyCloseableIterator.getInstance();
    }

    @SuppressWarnings("unchecked")
    public static <T> EmptyCloseableIterable<T> getInstance() {
        return INSTANCE;
    }

    @Override
    public List<E> toList() {
        return Collections.emptyList();
    }

    @Override
    public List<E> toList(final List<E> list) {
        return list;
    }

    @Override
    public E getHead() {
        return null;
    }

    @Override
    public E getTail() {
        return null;
    }

}
