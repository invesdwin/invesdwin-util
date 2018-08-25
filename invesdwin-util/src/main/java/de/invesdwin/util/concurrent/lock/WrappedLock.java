package de.invesdwin.util.concurrent.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class WrappedLock implements ILock {

    private final String name;
    private final Lock delegate;

    public WrappedLock(final String name, final Lock delegate) {
        this.name = name;
        this.delegate = delegate;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void lock() {
        try {
            delegate.lock();
            Locks.getLockTrace().locked(getName());
        } catch (final Throwable t) {
            throw Locks.getLockTrace().handleLockException(getName(), t);
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        try {
            delegate.lockInterruptibly();
            Locks.getLockTrace().locked(getName());
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
                Locks.getLockTrace().locked(getName());
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
                Locks.getLockTrace().locked(getName());
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
            delegate.unlock();
            Locks.getLockTrace().unlocked(getName());
        } catch (final Throwable t) {
            throw Locks.getLockTrace().handleLockException(getName(), t);
        }
    }

    @Override
    public Condition newCondition() {
        return delegate.newCondition();
    }

    @Override
    public String toString() {
        return delegate.toString();
    }
}
