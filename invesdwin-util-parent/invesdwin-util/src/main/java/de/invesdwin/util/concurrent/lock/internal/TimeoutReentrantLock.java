package de.invesdwin.util.concurrent.lock.internal;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.IReentrantLock;
import de.invesdwin.util.concurrent.lock.Locks;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.time.duration.Duration;

@ThreadSafe
public class TimeoutReentrantLock implements IReentrantLock {

    private final IReentrantLock delegate;
    private final Duration lockWaitTimeout;

    public TimeoutReentrantLock(final IReentrantLock delegate, final Duration lockWaitTimeout) {
        this.delegate = delegate;
        this.lockWaitTimeout = lockWaitTimeout;
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public void lock() {
        Locks.timeoutLock(delegate, lockWaitTimeout);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        Locks.timeoutLock(delegate, lockWaitTimeout);
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
    public boolean isHeldByCurrentThread() {
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
        return Objects.toStringHelper(this).addValue(delegate).toString();
    }
}
