package de.invesdwin.util.concurrent.lock.internal.readwrite;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.internal.readwrite.read.TracedReadLock;
import de.invesdwin.util.concurrent.lock.internal.readwrite.write.TracedWriteLock;
import de.invesdwin.util.concurrent.lock.readwrite.IReadWriteLock;
import de.invesdwin.util.concurrent.lock.strategy.ILockingStrategy;
import de.invesdwin.util.concurrent.lock.trace.ILockTrace;
import de.invesdwin.util.lang.Objects;

@ThreadSafe
public class TracedReadWriteLock implements IReadWriteLock {

    private final IReadWriteLock delegate;
    private final TracedReadLock readLock;
    private final TracedWriteLock writeLock;

    public TracedReadWriteLock(final ILockTrace lockTrace, final IReadWriteLock delegate) {
        this.delegate = delegate;
        this.readLock = new TracedReadLock(lockTrace, this, delegate.readLock());
        this.writeLock = new TracedWriteLock(lockTrace, readLock.getName(), delegate.writeLock());
    }

    @Override
    public boolean isWriteLocked() {
        return writeLock.isLocked();
    }

    @Override
    public boolean isWriteLockedByCurrentThread() {
        return writeLock.isHeldByCurrentThread();
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public TracedReadLock readLock() {
        return readLock;
    }

    @Override
    public TracedWriteLock writeLock() {
        return writeLock;
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
    public IReadWriteLock withStrategy(final ILockingStrategy strategy) {
        //CHECKSTYLE:ON
        return new TracedReadWriteLock(getLockTrace(), delegate.withStrategy(strategy));
    }

    @Override
    public ILockTrace getLockTrace() {
        return writeLock.getLockTrace();
    }

}
