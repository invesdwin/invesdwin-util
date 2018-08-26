package de.invesdwin.util.concurrent.lock.internal.readwrite;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.Locks;

@ThreadSafe
public class TracedReadLock implements ILock {

    private final String name;
    private final ReadLock delegate;

    public TracedReadLock(final String name, final ReadLock delegate) {
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

}
