package de.invesdwin.util.concurrent.lock.internal.readwrite.write;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.Locks;
import de.invesdwin.util.concurrent.lock.strategy.ILockingStrategy;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.time.duration.Duration;

@ThreadSafe
public class TimeoutWriteLock implements ILock {

    private final ILock delegate;
    private final Duration lockWaitTimeout;

    public TimeoutWriteLock(final ILock delegate, final Duration lockWaitTimeout) {
        this.delegate = delegate;
        this.lockWaitTimeout = lockWaitTimeout;
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public boolean isLocked() {
        return delegate.isLocked();
    }

    @Override
    public boolean isLockedByCurrentThread() {
        return delegate.isLockedByCurrentThread();
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
    public String toString() {
        return Objects.toStringHelper(this).addValue(delegate).addValue(lockWaitTimeout).toString();
    }

    @Override
    public ILockingStrategy getStrategy() {
        return delegate.getStrategy();
    }

    //CHECKSTYLE:OFF
    @Override
    public ILock withStrategy(final ILockingStrategy strategy) {
        //CHECKSTYLE:ON
        return new TimeoutWriteLock(delegate.withStrategy(strategy), lockWaitTimeout);
    }

}
