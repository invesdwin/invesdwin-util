package de.invesdwin.util.concurrent.lock;

import java.io.Closeable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import de.invesdwin.util.time.duration.Duration;

public interface ILock extends Lock, Closeable {

    ILock[] EMPTY_ARRAY = new ILock[0];

    String getName();

    boolean isLocked();

    boolean isLockedByCurrentThread();

    @Override
    default void close() {
        unlock();
    }

    default boolean tryLock(final Duration timeout) throws InterruptedException {
        return tryLock(timeout.longValue(), timeout.getTimeUnit().timeUnitValue());
    }

    default boolean tryLockNoInterrupt(final Duration timeout) {
        return tryLockNoInterrupt(timeout.longValue(), timeout.getTimeUnit().timeUnitValue());
    }

    default boolean tryLockNoInterrupt(final long time, final TimeUnit unit) {
        try {
            return tryLock(time, unit);
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
