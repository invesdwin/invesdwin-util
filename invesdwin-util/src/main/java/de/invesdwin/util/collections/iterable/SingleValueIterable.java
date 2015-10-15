package de.invesdwin.util.collections.iterable;

import javax.annotation.concurrent.Immutable;

@Immutable
public class SingleValueIterable<E> implements ICloseableIterable<E> {

    private final E singleValue;

    public SingleValueIterable(final E singleValue) {
        this.singleValue = singleValue;
    }

    @Override
    public ICloseableIterator<E> iterator() {
        return new SingleValueIterator<E>(singleValue);
    }

}
