package de.invesdwin.util.concurrent.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class WrappedWriteLock implements Lock {

    private final WriteLock delegate;

    public WrappedWriteLock(final WriteLock delegate) {
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

    public boolean isHeldByCurrentThread() {
        return delegate.isHeldByCurrentThread();
    }

    public int getHoldCount() {
        return delegate.getHoldCount();
    }

}
