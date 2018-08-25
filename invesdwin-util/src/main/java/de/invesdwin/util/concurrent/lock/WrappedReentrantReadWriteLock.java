package de.invesdwin.util.concurrent.lock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class WrappedReentrantReadWriteLock implements ReadWriteLock {

    private final ReentrantReadWriteLock delegate;
    private final WrappedReadLock readLock;
    private final WrappedWriteLock writeLock;

    public WrappedReentrantReadWriteLock(final ReentrantReadWriteLock delegate) {
        this.delegate = delegate;
        this.readLock = new WrappedReadLock(delegate.readLock());
        this.writeLock = new WrappedWriteLock(delegate.writeLock());
    }

    @Override
    public WrappedReadLock readLock() {
        return readLock;
    }

    @Override
    public WrappedWriteLock writeLock() {
        return writeLock;
    }

    public final boolean isFair() {
        return delegate.isFair();
    }

    public int getReadLockCount() {
        return delegate.getReadHoldCount();
    }

    public boolean isWriteLocked() {
        return delegate.isWriteLocked();
    }

    public boolean isWriteLockedByCurrentThread() {
        return delegate.isWriteLockedByCurrentThread();
    }

    public int getWriteHoldCount() {
        return delegate.getWriteHoldCount();
    }

    public int getReadHoldCount() {
        return delegate.getReadHoldCount();
    }

    public boolean hasQueuedThreads() {
        return delegate.hasQueuedThreads();
    }

    public boolean hasQueuedThread(final Thread thread) {
        return delegate.hasQueuedThread(thread);
    }

    public int getQueueLength() {
        return delegate.getQueueLength();
    }

    public boolean hasWaiters(final Condition condition) {
        return delegate.hasWaiters(condition);
    }

    public int getWaitQueueLength(final Condition condition) {
        return delegate.getWaitQueueLength(condition);
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

}
