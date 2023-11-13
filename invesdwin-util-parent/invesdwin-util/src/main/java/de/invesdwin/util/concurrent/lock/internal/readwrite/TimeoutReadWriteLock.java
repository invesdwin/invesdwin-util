package de.invesdwin.util.concurrent.lock.internal.readwrite;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.internal.readwrite.read.TimeoutReadLock;
import de.invesdwin.util.concurrent.lock.internal.readwrite.write.TimeoutWriteLock;
import de.invesdwin.util.concurrent.lock.readwrite.IReadWriteLock;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.time.duration.Duration;

@ThreadSafe
public class TimeoutReadWriteLock implements IReadWriteLock {

    private final IReadWriteLock delegate;
    private final TimeoutReadLock readLock;
    private final TimeoutWriteLock writeLock;

    public TimeoutReadWriteLock(final IReadWriteLock delegate, final Duration lockWaitTimeout) {
        this.delegate = delegate;
        this.readLock = new TimeoutReadLock(delegate.readLock(), lockWaitTimeout);
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
    public TimeoutReadLock readLock() {
        return readLock;
    }

    @Override
    public TimeoutWriteLock writeLock() {
        return writeLock;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).addValue(delegate).toString();
    }

}
