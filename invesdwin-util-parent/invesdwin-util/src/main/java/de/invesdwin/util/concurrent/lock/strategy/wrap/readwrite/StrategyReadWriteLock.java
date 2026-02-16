package de.invesdwin.util.concurrent.lock.strategy.wrap.readwrite;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.readwrite.IReadWriteLock;
import de.invesdwin.util.concurrent.lock.strategy.DefaultLockingStrategy;
import de.invesdwin.util.concurrent.lock.strategy.ILockingStrategy;
import de.invesdwin.util.concurrent.lock.strategy.wrap.StrategyLock;
import de.invesdwin.util.lang.Objects;

@ThreadSafe
public final class StrategyReadWriteLock implements IReadWriteLock {

    private final ILockingStrategy strategy;
    private final IReadWriteLock delegate;
    private final ILock readLock;
    private final ILock writeLock;

    private StrategyReadWriteLock(final ILockingStrategy strategy, final IReadWriteLock delegate) {
        this.strategy = strategy;
        this.delegate = delegate;
        this.readLock = StrategyLock.maybeWrap(strategy, delegate.readLock());
        this.writeLock = StrategyLock.maybeWrap(strategy, delegate.writeLock());
    }

    @Override
    public ILockingStrategy getStrategy() {
        return strategy;
    }

    public IReadWriteLock getDelegate() {
        return delegate;
    }

    @Override
    public boolean isWriteLocked() {
        return writeLock.isLocked();
    }

    @Override
    public boolean isWriteLockedByCurrentThread() {
        return writeLock.isLockedByCurrentThread();
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public ILock readLock() {
        return readLock;
    }

    @Override
    public ILock writeLock() {
        return writeLock;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).addValue(strategy).addValue(delegate).toString();
    }

    //CHECKSTYLE:OFF
    @Override
    public IReadWriteLock withStrategy(final ILockingStrategy strategy) {
        //CHECKSTYLE:ON
        return new StrategyReadWriteLock(strategy, delegate);
    }

    public static IReadWriteLock maybeWrap(final ILockingStrategy strategy, final IReadWriteLock lock) {
        if (strategy == DefaultLockingStrategy.INSTANCE) {
            return lock;
        }
        return new StrategyReadWriteLock(strategy, lock);
    }

}
