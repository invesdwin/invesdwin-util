package de.invesdwin.util.concurrent.lock.internal.readwrite.read;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.IQueuedLock;
import de.invesdwin.util.concurrent.lock.strategy.ILockingStrategy;
import de.invesdwin.util.concurrent.lock.strategy.wrap.readwrite.read.StrategyReentrantReadLock;
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

    @Override
    public boolean hasQueuedThreads() {
        return delegate.hasQueuedThreads();
    }

    @Override
    public boolean hasQueuedThread(final Thread thread) {
        return delegate.hasQueuedThread(thread);
    }

    //CHECKSTYLE:OFF
    @Override
    public IQueuedLock withStrategy(final ILockingStrategy strategy) {
        //CHECKSTYLE:ON
        return StrategyReentrantReadLock.maybeWrap(strategy, this);
    }

}
