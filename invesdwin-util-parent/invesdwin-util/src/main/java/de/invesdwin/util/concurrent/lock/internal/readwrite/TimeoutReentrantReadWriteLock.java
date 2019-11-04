package de.invesdwin.util.concurrent.lock.internal.readwrite;

import java.util.concurrent.locks.Condition;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.internal.TimeoutLock;
import de.invesdwin.util.concurrent.lock.readwrite.IReentrantReadWriteLock;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.time.duration.Duration;

@ThreadSafe
public class TimeoutReentrantReadWriteLock implements IReentrantReadWriteLock {

    private final IReentrantReadWriteLock delegate;
    private final TimeoutLock readLock;
    private final TimeoutWriteLock writeLock;

    public TimeoutReentrantReadWriteLock(final IReentrantReadWriteLock delegate, final Duration lockWaitTimeout) {
        this.delegate = delegate;
        this.readLock = new TimeoutLock(delegate.readLock(), lockWaitTimeout);
        this.writeLock = new TimeoutWriteLock(delegate.writeLock(), lockWaitTimeout);
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public TimeoutLock readLock() {
        return readLock;
    }

    @Override
    public TimeoutWriteLock writeLock() {
        return writeLock;
    }

    @Override
    public final boolean isFair() {
        return delegate.isFair();
    }

    @Override
    public int getReadLockCount() {
        return delegate.getReadHoldCount();
    }

    @Override
    public boolean isWriteLocked() {
        return delegate.isWriteLocked();
    }

    @Override
    public boolean isWriteLockedByCurrentThread() {
        return delegate.isWriteLockedByCurrentThread();
    }

    @Override
    public int getWriteHoldCount() {
        return delegate.getWriteHoldCount();
    }

    @Override
    public int getReadHoldCount() {
        return delegate.getReadHoldCount();
    }

    @Override
    public boolean hasQueuedThreads() {
        return delegate.hasQueuedThreads();
    }

    @Override
    public boolean hasQueuedThread(final Thread thread) {
        return delegate.hasQueuedThread(thread);
    }

    @Override
    public int getQueueLength() {
        return delegate.getQueueLength();
    }

    @Override
    public boolean hasWaiters(final Condition condition) {
        return delegate.hasWaiters(condition);
    }

    @Override
    public int getWaitQueueLength(final Condition condition) {
        return delegate.getWaitQueueLength(condition);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).addValue(delegate).toString();
    }

}
