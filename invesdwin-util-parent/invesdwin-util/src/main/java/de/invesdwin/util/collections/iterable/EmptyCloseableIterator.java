package de.invesdwin.util.collections.iterable;

import java.util.Collections;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.iterable.collection.fast.IFastToListCloseableIterator;
import de.invesdwin.util.error.FastNoSuchElementException;

@Immutable
public final class EmptyCloseableIterator<E> implements IFastToListCloseableIterator<E> {

    @SuppressWarnings("rawtypes")
    private static final EmptyCloseableIterator INSTANCE = new EmptyCloseableIterator();

    private EmptyCloseableIterator() {
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public E next() {
        throw new FastNoSuchElementException("EmptyCloseableIterator: always empty");
    }

    @Override
    public void close() {
    }

    @SuppressWarnings("unchecked")
    public static <T> EmptyCloseableIterator<T> getInstance() {
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
