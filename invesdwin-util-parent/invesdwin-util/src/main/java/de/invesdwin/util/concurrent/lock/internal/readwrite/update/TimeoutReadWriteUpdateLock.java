package de.invesdwin.util.concurrent.lock.internal.readwrite.update;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.internal.readwrite.read.TimeoutReadLock;
import de.invesdwin.util.concurrent.lock.internal.readwrite.write.TimeoutWriteLock;
import de.invesdwin.util.concurrent.lock.readwrite.IReadWriteUpdateLock;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.time.duration.Duration;

@ThreadSafe
public class TimeoutReadWriteUpdateLock implements IReadWriteUpdateLock {

    private final IReadWriteUpdateLock delegate;
    private final TimeoutReadLock readLock;
    private final TimeoutWriteLock writeLock;
    private final TimeoutUpdateLock updateLock;

    public TimeoutReadWriteUpdateLock(final IReadWriteUpdateLock delegate, final Duration lockWaitTimeout) {
        this.delegate = delegate;
        this.readLock = new TimeoutReadLock(delegate.readLock(), lockWaitTimeout);
        this.writeLock = new TimeoutWriteLock(delegate.writeLock(), lockWaitTimeout);
        this.updateLock = new TimeoutUpdateLock(delegate.updateLock(), lockWaitTimeout);
    }

    @Override
    public String getName() {
        return delegate.getName();
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
    public TimeoutUpdateLock updateLock() {
        return updateLock;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).addValue(delegate).toString();
    }
}
