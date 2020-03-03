package de.invesdwin.util.collections.iterable;

import java.util.Arrays;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.fast.IFastIterable;
import de.invesdwin.util.collections.iterable.collection.fast.IFastToListCloseableIterable;
import de.invesdwin.util.collections.iterable.collection.fast.IFastToListCloseableIterator;

@Immutable
public class SingleValueIterable<E> implements IFastIterable<E>, IFastToListCloseableIterable<E> {

    private final E singleValue;

    public SingleValueIterable(final E singleValue) {
        this.singleValue = singleValue;
    }

    @Override
    public IFastToListCloseableIterator<E> iterator() {
        return new SingleValueIterator<E>(singleValue);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean contains(final E value) {
        return this.singleValue.equals(value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public E[] asArray(final Class<E> type) {
        return (E[]) new Object[] { singleValue };
    }

    @Override
    public List<E> toList() {
        return Arrays.asList(singleValue);
    }

    @Override
    public List<E> toList(final List<E> list) {
        list.add(singleValue);
        return list;
    }

    @Override
    public E getHead() {
        return singleValue;
    }

    @Override
    public E getTail() {
        return singleValue;
    }

}