package de.invesdwin.util.collections.factory.pool.list;

import java.util.List;

import de.invesdwin.util.streams.closeable.ISafeCloseable;

public interface ICloseableList<E> extends List<E>, ISafeCloseable {

}
