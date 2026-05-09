package de.invesdwin.util.concurrent.lock.internal.readwrite.write;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.Threads;
import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.strategy.ILockingStrategy;
import de.invesdwin.util.concurrent.lock.trace.ILockTrace;
import de.invesdwin.util.lang.Objects;

@ThreadSafe
public class TracedWriteLock implements ILock {

    private final ILockTrace lockTrace;
    private final String readLockName;
    private final ILock delegate;
    private volatile Thread lockedThread;
    @GuardedBy("delegate")
    private int lockedThreadCount;

    public TracedWriteLock(final ILockTrace lockTrace, final String readLockName, final ILock delegate) {
        this.lockTrace = lockTrace;
        this.readLockName = readLockName;
        this.delegate = delegate;
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public boolean isLocked() {
        return lockedThread != null;
    }

    @Override
    public boolean isHeldByCurrentThread() {
        final Thread lockedThreadCopy = lockedThread;
        return lockedThreadCopy != null && lockedThreadCopy == Thread.currentThread();
    }

    protected void onLocked() {
        if (lockedThreadCount == 0) {
            lockedThread = Thread.currentThread();
            lockTrace.locked(getName());
        }
        lockedThreadCount++;
    }

    protected void onUnlock() {
        lockedThreadCount--;
        if (lockedThreadCount == 0) {
            lockedThread = null;
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
        if (lockTrace.isLockedByThisThread(readLockName)) {
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
        return new TracedWriteLock(lockTrace, readLockName, delegate.withStrategy(strategy));
    }

    @Override
    public ILockTrace getLockTrace() {
        return lockTrace;
    }

}
