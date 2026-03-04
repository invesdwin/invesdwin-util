package de.invesdwin.util.concurrent.lock.internal.readwrite;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.internal.readwrite.read.TimeoutReadLock;
import de.invesdwin.util.concurrent.lock.internal.readwrite.write.TimeoutWriteLock;
import de.invesdwin.util.concurrent.lock.readwrite.IReadWriteLock;
import de.invesdwin.util.concurrent.lock.strategy.ILockingStrategy;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.time.duration.Duration;

@ThreadSafe
public class TimeoutReadWriteLock implements IReadWriteLock {

    private final IReadWriteLock delegate;
    private final Duration lockWaitTimeout;
    private final boolean onlyWriteLock;
    private final ILock readLock;
    private final TimeoutWriteLock writeLock;

    public TimeoutReadWriteLock(final IReadWriteLock delegate, final Duration lockWaitTimeout,
            final boolean onlyWriteLock) {
        this.delegate = delegate;
        this.lockWaitTimeout = lockWaitTimeout;
        this.onlyWriteLock = onlyWriteLock;
        if (onlyWriteLock) {
            this.readLock = delegate.readLock();
        } else {
            this.readLock = new TimeoutReadLock(delegate.readLock(), lockWaitTimeout);
        }
        this.writeLock = new TimeoutWriteLock(delegate.writeLock(), lockWaitTimeout);
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public boolean isWriteLocked() {
        return delegate.isWriteLocked();
    }

    @Override
    public boolean isWriteLockedByCurrentThread() {
        return delegate.isWriteLockedByCurrentThread();
    }

    @Override
    public ILock readLock() {
        return readLock;
    }

    @Override
    public TimeoutWriteLock writeLock() {
        return writeLock;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .addValue(delegate)
                .addValue(lockWaitTimeout)
                .addValue(onlyWriteLock)
                .toString();
    }

    @Override
    public ILockingStrategy getStrategy() {
        return delegate.getStrategy();
    }

    //CHECKSTYLE:OFF
    @Override
    public IReadWriteLock withStrategy(final ILockingStrategy strategy) {
        //CHECKSTYLE:ON
        return new TimeoutReadWriteLock(delegate.withStrategy(strategy), lockWaitTimeout, onlyWriteLock);
    }

}
