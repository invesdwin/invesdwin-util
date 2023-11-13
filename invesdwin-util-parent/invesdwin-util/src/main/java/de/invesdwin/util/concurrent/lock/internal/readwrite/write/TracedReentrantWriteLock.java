package de.invesdwin.util.concurrent.lock.internal.readwrite.write;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.Threads;
import de.invesdwin.util.concurrent.lock.Locks;
import de.invesdwin.util.concurrent.lock.readwrite.IReentrantWriteLock;
import de.invesdwin.util.lang.Objects;

@ThreadSafe
public class TracedReentrantWriteLock implements IReentrantWriteLock {

    private final String readLockName;
    private final String name;
    private final ReentrantReadWriteLock parent;
    private final WriteLock delegate;

    public TracedReentrantWriteLock(final String readLockName, final String name, final ReentrantReadWriteLock parent,
            final WriteLock delegate) {
        this.readLockName = name;
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

    protected void onLocked() {
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
            assertReadLockNotHeldByCurrentThread();
            delegate.lock();
            onLocked();
        } catch (final Throwable t) {
            throw Locks.getLockTrace().handleLockException(getName(), t);
        }
    }

    private void assertReadLockNotHeldByCurrentThread() {
        if (Locks.getLockTrace().isLockedByThisThread(readLockName)) {
            throw Locks.getLockTrace()
                    .handleLockException(getName(),
                            new IllegalStateException("read lock [" + readLockName
                                    + "] already held by current thread [" + Threads.getCurrentThreadName()
                                    + "] while trying to acquire write lock [" + name + "]"));
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        try {
            assertReadLockNotHeldByCurrentThread();
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
            assertReadLockNotHeldByCurrentThread();
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
            assertReadLockNotHeldByCurrentThread();
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
    public String toString() {
        return Objects.toStringHelper(this).addValue(name).addValue(delegate).toString();
    }

}
