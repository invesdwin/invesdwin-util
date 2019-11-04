package de.invesdwin.util.concurrent.lock.internal.readwrite.update;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.internal.TimeoutLock;
import de.invesdwin.util.concurrent.lock.readwrite.IReadWriteUpdateLock;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.time.duration.Duration;

@ThreadSafe
public class TimeoutReadWriteUpdateLock implements IReadWriteUpdateLock {

    private final IReadWriteUpdateLock delegate;
    private final TimeoutLock readLock;
    private final TimeoutLock writeLock;
    private final TimeoutLock updateLock;

    public TimeoutReadWriteUpdateLock(final IReadWriteUpdateLock delegate, final Duration lockWaitTimeout) {
        this.delegate = delegate;
        this.readLock = new TimeoutLock(delegate.readLock(), lockWaitTimeout);
        this.writeLock = new TimeoutLock(delegate.writeLock(), lockWaitTimeout);
        this.updateLock = new TimeoutLock(delegate.updateLock(), lockWaitTimeout);
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public TimeoutLock readLock() {
        return readLock;
    }

    @Override
    public TimeoutLock writeLock() {
        return writeLock;
    }

    @Override
    public TimeoutLock updateLock() {
        return updateLock;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).addValue(delegate).toString();
    }
}
