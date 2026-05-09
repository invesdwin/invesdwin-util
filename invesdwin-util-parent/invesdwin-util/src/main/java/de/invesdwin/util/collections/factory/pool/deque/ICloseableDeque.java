package de.invesdwin.util.collections.factory.pool.deque;

import java.util.Deque;

import de.invesdwin.util.streams.closeable.ISafeCloseable;

public interface ICloseableDeque<E> extends Deque<E>, ISafeCloseable {

}
