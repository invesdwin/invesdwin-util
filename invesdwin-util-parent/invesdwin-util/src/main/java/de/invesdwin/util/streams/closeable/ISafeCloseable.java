package de.invesdwin.util.streams.closeable;

import java.io.Closeable;

/**
 * A functional interface representing a resource that can be SAFELY closed.
 * <p>
 * Extends {@link java.io.Closeable}, which itself extends {@link AutoCloseable}. Unlike {@link Closeable}, the
 * {@code close()} method in this interface does not declare or throw any checked exceptions, making it safe for use in
 * contexts where exceptions are not desired.
 * </p>
 *
 * @see java.lang.AutoCloseable
 * @see java.io.Closeable
 */
@FunctionalInterface
public interface ISafeCloseable extends Closeable {

    @Override
    void close();

}
