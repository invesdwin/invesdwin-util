package de.invesdwin.util.concurrent.lock.internal.readwrite;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.internal.TimeoutLock;
import de.invesdwin.util.concurrent.lock.readwrite.IReadWriteLock;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.time.duration.Duration;

@ThreadSafe
public class TimeoutReadWriteLock implements IReadWriteLock {

    private final IReadWriteLock delegate;
    private final TimeoutLock readLock;
    private final TimeoutLock writeLock;

    public TimeoutReadWriteLock(final IReadWriteLock delegate, final Duration lockWaitTimeout) {
        this.delegate = delegate;
        this.readLock = new TimeoutLock(delegate.readLock(), lockWaitTimeout);
        this.writeLock = new TimeoutLock(delegate.writeLock(), lockWaitTimeout);
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
    public String toString() {
        return Objects.toStringHelper(this).addValue(delegate).toString();
    }

}
