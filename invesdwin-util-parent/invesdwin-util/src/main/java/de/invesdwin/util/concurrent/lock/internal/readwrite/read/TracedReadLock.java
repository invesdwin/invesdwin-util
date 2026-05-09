package de.invesdwin.util.concurrent.lock.internal.readwrite.read;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.lang3.mutable.MutableInt;

import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.readwrite.IReadWriteLock;
import de.invesdwin.util.concurrent.lock.strategy.ILockingStrategy;
import de.invesdwin.util.concurrent.lock.trace.ILockTrace;
import de.invesdwin.util.concurrent.reference.WeakThreadLocalReference;
import de.invesdwin.util.lang.Objects;

@ThreadSafe
public class TracedReadLock implements ILock {

    private final ILockTrace lockTrace;
    private final IReadWriteLock parent;
    private final ILock delegate;
    /**
     * we need to separate this info per thread because read locks can be held concurrently by multiple threads
     */
    private final WeakThreadLocalReference<MutableInt> lockedThreadCount = new WeakThreadLocalReference<MutableInt>() {
        @Override
        protected MutableInt initialValue() {
            return new MutableInt();
        }
    };

    public TracedReadLock(final ILockTrace lockTrace, final IReadWriteLock parent, final ILock delegate) {
        this.lockTrace = lockTrace;
        this.parent = parent;
        this.delegate = delegate;
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public boolean isLocked() {
        //intentionally gives the info of writelock because readlocks don't block among themselves
        return parent.isWriteLocked();
    }

    @Override
    public boolean isHeldByCurrentThread() {
        //intentionally gives the info of writelock because readlocks don't block among themselves
        return parent.isWriteLockedByCurrentThread();
    }

    protected void onLocked() {
        if (lockedThreadCount.get().incrementAndGet() == 1) {
            lockTrace.locked(getName());
        }
    }

    protected void onUnlock() {
        if (lockedThreadCount.get().decrementAndGet() == 0) {
            lockTrace.unlocked(getName());
        }
    }

    @Override
    public void lock() {
        try {
            delegate.lock();
            onLocked();
        } catch (final Throwable t) {
            throw lockTrace.handleLockException(getName(), t);
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
            throw lockTrace.handleLockException(getName(), t);
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
            throw lockTrace.handleLockException(getName(), t);
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
            throw lockTrace.handleLockException(getName(), t);
        }
    }

    @Override
    public void unlock() {
        try {
            onUnlock();
            delegate.unlock();
        } catch (final Throwable t) {
            throw lockTrace.handleLockException(getName(), t);
        }
    }

    @Override
    public Condition newCondition() {
        return delegate.newCondition();
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).addValue(delegate).toString();
    }

    @Override
    public ILockingStrategy getStrategy() {
        return delegate.getStrategy();
    }

    //CHECKSTYLE:OFF
    @Override
    public ILock withStrategy(final ILockingStrategy strategy) {
        //CHECKSTYLE:ON
        return new TracedReadLock(lockTrace, parent, delegate.withStrategy(strategy));
    }

    @Override
    public ILockTrace getLockTrace() {
        return lockTrace;
    }

}
