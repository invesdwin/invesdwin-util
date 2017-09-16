package de.invesdwin.util.collections.concurrent;

import de.invesdwin.util.collections.iterable.ICloseableIterable;

public interface IFastIterable<E> extends ICloseableIterable<E> {

    boolean isEmpty();

    E[] asArray(Class<E> type);

}
