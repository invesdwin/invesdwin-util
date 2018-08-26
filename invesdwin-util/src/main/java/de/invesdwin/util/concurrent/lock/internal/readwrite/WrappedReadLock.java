package de.invesdwin.util.concurrent.lock.internal.readwrite;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.ILock;

@ThreadSafe
public class WrappedReadLock implements ILock {

    private final String name;
    private final ReadLock delegate;

    public WrappedReadLock(final String name, final ReadLock delegate) {
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

}
