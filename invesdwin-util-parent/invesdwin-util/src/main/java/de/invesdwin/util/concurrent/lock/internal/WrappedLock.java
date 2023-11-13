package de.invesdwin.util.concurrent.lock.internal;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.lang.Objects;

@ThreadSafe
public final class WrappedLock implements ILock {

    private final String name;
    private final Lock delegate;
    private volatile Thread lockedThread;
    @GuardedBy("delegate")
    private int lockedThreadCount;

    public WrappedLock(final String name, final Lock delegate) {
        this.name = name;
        this.delegate = delegate;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isLocked() {
        return lockedThread != null;
    }

    @Override
    public boolean isLockedByCurrentThread() {
        final Thread lockedThreadCopy = lockedThread;
        return lockedThreadCopy != null && lockedThreadCopy == Thread.currentThread();
    }

    protected void onLocked() {
        if (lockedThreadCount == 0) {
            lockedThread = Thread.currentThread();
        }
        lockedThreadCount++;
    }

    protected void onUnlock() {
        lockedThreadCount--;
        if (lockedThreadCount == 0) {
            lockedThread = null;
        }
    }

    @Override
    public void lock() {
        delegate.lock();
        onLocked();
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        delegate.lockInterruptibly();
        onLocked();
    }

    @Override
    public boolean tryLock() {
        final boolean locked = delegate.tryLock();
        if (locked) {
            onLocked();
        }
        return locked;
    }

    @Override
    public boolean tryLock(final long time, final TimeUnit unit) throws InterruptedException {
        final boolean locked = delegate.tryLock(time, unit);
        if (locked) {
            onLocked();
        }
        return locked;
    }

    @Override
    public void unlock() {
        onUnlock();
        delegate.unlock();
    }

    @Override
    public Condition newCondition() {
        return delegate.newCondition();
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).addValue(name).addValue(delegate).toString();
    }

}
