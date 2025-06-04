package de.invesdwin.util.collections.iterable;

public interface IPeekingCloseableIterable<E> extends ICloseableIterable<E> {

    @Override
    IPeekingCloseableIterator<E> iterator();

}
