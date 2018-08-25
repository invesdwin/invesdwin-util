package de.invesdwin.util.concurrent.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class WrappedReentrantLock implements Lock {

    private final ReentrantLock delegate;

    public WrappedReentrantLock(final ReentrantLock delegate) {
        this.delegate = delegate;
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
    public boolean tryLock(final long timeout, final TimeUnit unit) throws InterruptedException {
        return delegate.tryLock(timeout, unit);
    }

    @Override
    public void unlock() {
        delegate.unlock();
    }

    @Override
    public Condition newCondition() {
        return delegate.newCondition();
    }

    public int getHoldCount() {
        return delegate.getHoldCount();
    }

    public boolean isHeldByCurrentThread() {
        return delegate.isHeldByCurrentThread();
    }

    public boolean isLocked() {
        return delegate.isLocked();
    }

    public boolean isFair() {
        return delegate.isFair();
    }

    public boolean hasQueuedThreads() {
        return delegate.hasQueuedThreads();
    }

    public final boolean hasQueuedThread(final Thread thread) {
        return delegate.hasQueuedThread(thread);
    }

    public final int getQueueLength() {
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
