package de.invesdwin.util.concurrent.lock.internal;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.IReentrantLock;
import de.invesdwin.util.lang.Objects;

@ThreadSafe
public class WrappedReentrantLock implements IReentrantLock {

    private final String name;
    private final ReentrantLock delegate;

    public WrappedReentrantLock(final String name, final ReentrantLock delegate) {
        this.name = name;
        this.delegate = delegate;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void lock() {
        delegate.lock();
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        delegate.lockInterruptibly();
    }

    @Override
    public boolean tryLock() {
        return delegate.tryLock();
    }

    @Override
    public boolean tryLock(final long time, final TimeUnit unit) throws InterruptedException {
        return delegate.tryLock(time, unit);
    }

    @Override
    public void unlock() {
        delegate.unlock();
    }

    @Override
    public Condition newCondition() {
        return delegate.newCondition();
    }

    @Override
    public int getHoldCount() {
        return delegate.getHoldCount();
    }

    @Override
    public boolean isLockedByCurrentThread() {
        return delegate.isHeldByCurrentThread();
    }

    @Override
    public boolean isLocked() {
        return delegate.isLocked();
    }

    @Override
    public boolean isFair() {
        return delegate.isFair();
    }

    @Override
    public boolean hasQueuedThreads() {
        return delegate.hasQueuedThreads();
    }

    @Override
    public final boolean hasQueuedThread(final Thread thread) {
        return delegate.hasQueuedThread(thread);
    }

    @Override
    public final int getQueueLength() {
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
