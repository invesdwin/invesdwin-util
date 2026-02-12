package de.invesdwin.util.collections.factory.pool.set;

import java.util.Set;

import de.invesdwin.util.streams.closeable.ISafeCloseable;

public interface ICloseableSet<E> extends Set<E>, ISafeCloseable {

}
