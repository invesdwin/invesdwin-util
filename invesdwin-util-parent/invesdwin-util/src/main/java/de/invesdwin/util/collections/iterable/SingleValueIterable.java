package de.invesdwin.util.collections.iterable;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.fast.IFastIterable;

@Immutable
public class SingleValueIterable<E> implements IFastIterable<E> {

    private final E singleValue;

    public SingleValueIterable(final E singleValue) {
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

    public static <T> ICloseableIterable<T> valueOf(final T singleValue) {
        if (singleValue == null) {
            return EmptyCloseableIterable.getInstance();
        } else {
            return new SingleValueIterable<>(singleValue);
        }
    }

}