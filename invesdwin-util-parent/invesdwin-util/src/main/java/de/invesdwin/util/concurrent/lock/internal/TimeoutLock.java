package de.invesdwin.util.concurrent.lock.internal;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.Locks;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.time.duration.Duration;

@ThreadSafe
public final class TimeoutLock implements ILock {

    private static final org.slf4j.ext.XLogger LOG = org.slf4j.ext.XLoggerFactory.getXLogger(TimeoutLock.class);

    private final ILock delegate;
    private final Duration lockWaitTimeout;

    public TimeoutLock(final ILock delegate, final Duration lockWaitTimeout) {
        this.delegate = delegate;
        this.lockWaitTimeout = lockWaitTimeout;
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public void lock() {
        try {
            int count = 0;
            while (!delegate.tryLock(lockWaitTimeout.longValue(), lockWaitTimeout.getTimeUnit().timeUnitValue())) {
                count++;
                LOG.catching(Locks.getLockTrace()
                        .handleLockException(getName(), new TimeoutException(
                                "lock() wait timeout [" + lockWaitTimeout.multiply(count) + "] exceeded: " + count)));
            }
        } catch (final InterruptedException e) {
            throw Locks.getLockTrace().handleLockException(getName(), e);
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        try {
            int count = 0;
            while (!delegate.tryLock(lockWaitTimeout.longValue(), lockWaitTimeout.getTimeUnit().timeUnitValue())) {
                count++;
                LOG.catching(Locks.getLockTrace()
                        .handleLockException(getName(), new TimeoutException("lockInterruptibly() wait timeout ["
                                + lockWaitTimeout.multiply(count) + "] exceeded: " + count)));
            }
        } catch (final InterruptedException e) {
            throw Locks.getLockTrace().handleLockException(getName(), e);
        }
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
        return Objects.toStringHelper(this).addValue(delegate).toString();
    }

}
