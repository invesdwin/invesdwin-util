package de.invesdwin.util.concurrent.lock.internal.readwrite;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.internal.WrappedLock;
import de.invesdwin.util.concurrent.lock.readwrite.IReentrantReadWriteLock;
import de.invesdwin.util.lang.Objects;

@ThreadSafe
public class WrappedReentrantReadWriteLock implements IReentrantReadWriteLock {

    private final String name;
    private final ReentrantReadWriteLock delegate;
    private final WrappedLock readLock;
    private final WrappedWriteLock writeLock;

    public WrappedReentrantReadWriteLock(final String name, final ReentrantReadWriteLock delegate) {
        this.name = name;
        this.delegate = delegate;
        this.readLock = new WrappedLock(name + "_readLock", delegate.readLock());
        this.writeLock = new WrappedWriteLock(name + "_writeLock", delegate.writeLock());
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public WrappedLock readLock() {
        return readLock;
    }

    @Override
    public WrappedWriteLock writeLock() {
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
        return Objects.toStringHelper(this).addValue(name).addValue(delegate).toString();
    }

}
