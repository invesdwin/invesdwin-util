package de.invesdwin.util.concurrent.lock.internal.readwrite.read;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.IQueuedLock;
import de.invesdwin.util.time.duration.Duration;

@ThreadSafe
public class TimeoutReentrantReadLock extends TimeoutReadLock implements IQueuedLock {

    private final IQueuedLock delegate;

    public TimeoutReentrantReadLock(final IQueuedLock delegate, final Duration lockWaitTimeout) {
        super(delegate, lockWaitTimeout);
        this.delegate = delegate;
    }

    @Override
    public int getQueueLength() {
        return delegate.getQueueLength();
    }

}
