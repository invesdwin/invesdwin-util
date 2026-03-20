package de.invesdwin.util.concurrent.lock.trace;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.IReentrantLock;
import de.invesdwin.util.concurrent.lock.readwrite.IReadWriteLock;
import de.invesdwin.util.concurrent.lock.readwrite.IReentrantReadWriteLock;

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

    @Override
    public ILock wrap(final String lockName, final Lock lock) {
        return new de.invesdwin.util.concurrent.lock.internal.WrappedLock(lockName, lock);
    }

    @Override
    public IReentrantLock wrap(final String lockName, final ReentrantLock lock) {
        return new de.invesdwin.util.concurrent.lock.internal.WrappedReentrantLock(lockName, lock);
    }

    @Override
    public IReadWriteLock wrap(final String lockName, final ReadWriteLock lock) {
        return new de.invesdwin.util.concurrent.lock.internal.readwrite.WrappedReadWriteLock(lockName, lock);
    }

    @Override
    public IReentrantReadWriteLock wrap(final String lockName, final ReentrantReadWriteLock lock) {
        return new de.invesdwin.util.concurrent.lock.internal.readwrite.WrappedReentrantReadWriteLock(lockName, lock);
    }

    @Override
    public ILock maybeWrap(final ILock lock) {
        return lock;
    }

    @Override
    public IReentrantLock maybeWrap(final IReentrantLock lock) {
        return lock;
    }

    @Override
    public IReadWriteLock maybeWrap(final IReadWriteLock lock) {
        return lock;
    }

    @Override
    public IReentrantReadWriteLock maybeWrap(final IReentrantReadWriteLock lock) {
        return lock;
    }

}
