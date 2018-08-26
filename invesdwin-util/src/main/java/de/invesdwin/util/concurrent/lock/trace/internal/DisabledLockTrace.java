package de.invesdwin.util.concurrent.lock.trace.internal;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.trace.ILockTrace;

@ThreadSafe
public final class DisabledLockTrace implements ILockTrace {

    public static final DisabledLockTrace INSTANCE = new DisabledLockTrace();

    private DisabledLockTrace() {}

    @Override
    public void locked(final String name) {}

    @Override
    public void unlocked(final String name) {}

    @Override
    public boolean isLockedByThisThread(final String lockName) {
        return false;
    }

    @Override
    public RuntimeException handleLockException(final String lockName, final Throwable lockException) {
        if (lockException instanceof RuntimeException) {
            return (RuntimeException) lockException;
        } else {
            return new RuntimeException(lockException);
        }
    }

}
