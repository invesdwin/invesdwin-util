package de.invesdwin.util.concurrent.lock.internal;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.IReentrantLock;
import de.invesdwin.util.concurrent.lock.Locks;
import de.invesdwin.util.lang.Objects;

@ThreadSafe
public class TracedReentrantLock implements IReentrantLock {

    private final String name;
    private final ReentrantLock delegate;

    public TracedReentrantLock(final String name, final ReentrantLock delegate) {
        this.name = name;
        this.delegate = delegate;
    }

    @Override
    public String getName() {
        return name;
    }

    public void onLocked() {
        if (delegate.getHoldCount() == 1) {
            Locks.getLockTrace().locked(getName());
        }
    }

    protected void onUnlock() {
        if (delegate.getHoldCount() == 1) {
            Locks.getLockTrace().unlocked(getName());
        }
    }

    @Override
    public void lock() {
        try {
            delegate.lock();
            onLocked();
        } catch (final Throwable t) {
            throw Locks.getLockTrace().handleLockException(getName(), t);
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        try {
            delegate.lockInterruptibly();
            onLocked();
        } catch (final InterruptedException t) {
            throw t;
        } catch (final Throwable t) {
            throw Locks.getLockTrace().handleLockException(getName(), t);
        }
    }

    @Override
    public boolean tryLock() {
        try {
            final boolean locked = delegate.tryLock();
            if (locked) {
                onLocked();
            }
            return locked;
        } catch (final Throwable t) {
            throw Locks.getLockTrace().handleLockException(getName(), t);
        }
    }

    @Override
    public boolean tryLock(final long time, final TimeUnit unit) throws InterruptedException {
        try {
            final boolean locked = delegate.tryLock(time, unit);
            if (locked) {
                onLocked();
            }
            return locked;
        } catch (final InterruptedException t) {
            throw t;
        } catch (final Throwable t) {
            throw Locks.getLockTrace().handleLockException(getName(), t);
        }
    }

    @Override
    public void unlock() {
        try {
            onUnlock();
            delegate.unlock();
        } catch (final Throwable t) {
            throw Locks.getLockTrace().handleLockException(getName(), t);
        }
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
