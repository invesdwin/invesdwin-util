package de.invesdwin.util.collections.fast;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.iterable.SingleValueIterator;

@Immutable
public class SingleValueFastIterable<E> implements IFastIterable<E> {

    private final E singleValue;

    public SingleValueFastIterable(final E singleValue) {
        this.singleValue = singleValue;
    }

    @Override
    public ICloseableIterator<E> iterator() {
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

}
