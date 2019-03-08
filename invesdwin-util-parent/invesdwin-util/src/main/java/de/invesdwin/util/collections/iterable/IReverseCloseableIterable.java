package de.invesdwin.util.collections.iterable;

public interface IReverseCloseableIterable<E> extends ICloseableIterable<E> {

    ICloseableIterator<E> reverseIterator();

}
