package de.invesdwin.util.concurrent.lock.internal.readwrite.read;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.lang3.mutable.MutableInt;

import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.Locks;
import de.invesdwin.util.concurrent.lock.readwrite.IReadWriteLock;
import de.invesdwin.util.concurrent.reference.WeakThreadLocalReference;
import de.invesdwin.util.lang.Objects;

@ThreadSafe
public class TracedReadLock implements ILock {

    private final String name;
    private final IReadWriteLock parent;
    private final Lock delegate;
    /**
     * we need to separate this info per thread because read locks can be held concurrently by multiple threads
     */
    private final WeakThreadLocalReference<MutableInt> lockedThreadCount = new WeakThreadLocalReference<MutableInt>() {
        @Override
        protected MutableInt initialValue() {
            return new MutableInt();
        }
    };

    public TracedReadLock(final String name, final IReadWriteLock parent, final Lock delegate) {
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
        //intentionally gives the info of writelock because readlocks don't block among themselves
        return parent.isWriteLocked();
    }

    @Override
    public boolean isLockedByCurrentThread() {
        //intentionally gives the info of writelock because readlocks don't block among themselves
        return parent.isWriteLockedByCurrentThread();
    }

    protected void onLocked() {
        if (lockedThreadCount.get().incrementAndGet() == 1) {
            Locks.getLockTrace().locked(getName());
        }
    }

    protected void onUnlock() {
        if (lockedThreadCount.get().decrementAndGet() == 0) {
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
    public String toString() {
        return Objects.toStringHelper(this).addValue(name).addValue(delegate).toString();
    }

}
