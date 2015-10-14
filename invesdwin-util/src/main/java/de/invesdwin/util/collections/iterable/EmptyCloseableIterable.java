package de.invesdwin.util.collections.iterable;

import javax.annotation.concurrent.Immutable;

@Immutable
public class EmptyCloseableIterable<E> implements ICloseableIterable<E> {

    @Override
    public ICloseableIterator<E> iterator() {
        return new EmptyCloseableIterator<E>();
    }

}
