package de.invesdwin.util.concurrent.lock;

import java.io.Closeable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

public interface ILock extends Lock, Closeable {

    String getName();

    boolean isLocked();

    boolean isLockedByCurrentThread();

    @Override
    default void close() {
        unlock();
    }

    default boolean tryLockNoInterrupt(final long time, final TimeUnit unit) {
        try {
            return tryLock(time, unit);
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
