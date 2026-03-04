package de.invesdwin.util.concurrent.lock;

import de.invesdwin.util.streams.closeable.ISafeCloseable;

/**
 * @see AutoCloseable
 * @see java.util.concurrent.locks.Lock
 * @see java.util.concurrent.locks.ReadWriteLock
 */
@FunctionalInterface
public interface ICloseableLock extends ISafeCloseable {

}