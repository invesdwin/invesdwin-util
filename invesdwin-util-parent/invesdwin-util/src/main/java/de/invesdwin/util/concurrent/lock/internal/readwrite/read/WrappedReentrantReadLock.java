package de.invesdwin.util.concurrent.lock.internal.readwrite.read;

import java.util.concurrent.locks.Lock;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.IQueuedLock;
import de.invesdwin.util.concurrent.lock.readwrite.IReentrantReadWriteLock;
import de.invesdwin.util.concurrent.lock.strategy.ILockingStrategy;
import de.invesdwin.util.concurrent.lock.strategy.wrap.readwrite.read.StrategyReentrantReadLock;

@ThreadSafe
public class WrappedReentrantReadLock extends WrappedReadLock implements IQueuedLock {

    private final IReentrantReadWriteLock pair;

    public WrappedReentrantReadLock(final String name, final IReentrantReadWriteLock pair, final Lock delegate) {
        super(name, pair, delegate);
        this.pair = pair;
    }

    @Override
    public int getQueueLength() {
        return pair.getQueueLength();
    }

    @Override
    public boolean hasQueuedThreads() {
        return pair.hasQueuedThreads();
    }

    @Override
    public boolean hasQueuedThread(final Thread thread) {
        return pair.hasQueuedThread(thread);
    }

    //CHECKSTYLE:OFF
    @Override
    public IQueuedLock withStrategy(final ILockingStrategy strategy) {
        //CHECKSTYLE:ON
        return StrategyReentrantReadLock.maybeWrap(strategy, this);
    }

}
