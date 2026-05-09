package de.invesdwin.util.concurrent.lock.internal.readwrite.write;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.Threads;
import de.invesdwin.util.concurrent.lock.readwrite.IReentrantReadWriteLock;
import de.invesdwin.util.concurrent.lock.readwrite.IReentrantWriteLock;
import de.invesdwin.util.concurrent.lock.strategy.ILockingStrategy;
import de.invesdwin.util.concurrent.lock.trace.ILockTrace;
import de.invesdwin.util.lang.Objects;

@ThreadSafe
public class TracedReentrantWriteLock implements IReentrantWriteLock {

    private final ILockTrace lockTrace;
    private final String readLockName;
    private final IReentrantReadWriteLock parent;
    private final IReentrantWriteLock delegate;

    public TracedReentrantWriteLock(final ILockTrace lockTrace, final String readLockName,
            final IReentrantReadWriteLock parent, final IReentrantWriteLock delegate) {
        this.lockTrace = lockTrace;
        this.readLockName = readLockName;
        this.parent = parent;
        this.delegate = delegate;
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public boolean isLocked() {
        return parent.isWriteLocked();
    }

    @Override
    public boolean isHeldByCurrentThread() {
        return parent.isWriteLockedByCurrentThread();
    }

    protected void onLocked() {
        if (delegate.getHoldCount() == 1) {
            lockTrace.locked(getName());
        }
    }

    protected void onUnlock() {
        if (delegate.getHoldCount() == 1) {
            lockTrace.unlocked(getName());
        }
    }

    @Override
    public void lock() {
        try {
            assertReadLockNotHeldByCurrentThread();
            delegate.lock();
            onLocked();
        } catch (final Throwable t) {
            throw lockTrace.handleLockException(getName(), t);
        }
    }

    private void assertReadLockNotHeldByCurrentThread() {
        final int readHoldCount = parent.getReadHoldCount();
        if (readHoldCount > 0 && lockTrace.isLockedByThisThread(readLockName)) {
            throw lockTrace.handleLockException(getName(),
                    new IllegalStateException("read lock [" + readLockName + "] already held by current thread ["
                            + Threads.getCurrentThreadName() + "] while trying to acquire write lock [" + getName()
                            + "]"));
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
            throw lockTrace.handleLockException(getName(), t);
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
            throw lockTrace.handleLockException(getName(), t);
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
    public int getHoldCount() {
        return delegate.getHoldCount();
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
    public IReentrantWriteLock withStrategy(final ILockingStrategy strategy) {
        //CHECKSTYLE:ON
        return new TracedReentrantWriteLock(lockTrace, readLockName, parent, delegate.withStrategy(strategy));
    }

    @Override
    public ILockTrace getLockTrace() {
        return lockTrace;
    }

}
