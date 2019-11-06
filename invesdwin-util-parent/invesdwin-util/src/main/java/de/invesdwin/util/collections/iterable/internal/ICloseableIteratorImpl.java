package de.invesdwin.util.collections.iterable.internal;

import de.invesdwin.util.collections.iterable.ICloseableIterator;

public interface ICloseableIteratorImpl<E> extends ICloseableIterator<E> {

    boolean isClosed();

}
