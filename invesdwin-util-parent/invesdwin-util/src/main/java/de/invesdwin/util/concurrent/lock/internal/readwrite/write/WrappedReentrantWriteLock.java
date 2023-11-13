package de.invesdwin.util.concurrent.lock.internal.readwrite.write;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.readwrite.IReentrantWriteLock;
import de.invesdwin.util.lang.Objects;

@ThreadSafe
public class WrappedReentrantWriteLock implements IReentrantWriteLock {

    private final String name;
    private final WriteLock delegate;
    private final ReentrantReadWriteLock parent;

    public WrappedReentrantWriteLock(final String name, final ReentrantReadWriteLock parent, final WriteLock delegate) {
        this.name = name;
        this.parent = parent;
        this.delegate = delegate;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isLocked() {
        return parent.isWriteLocked();
    }

    @Override
    public boolean isLockedByCurrentThread() {
        return parent.isWriteLockedByCurrentThread();
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
    public String toString() {
        return Objects.toStringHelper(this).addValue(name).addValue(delegate).toString();
    }

}
