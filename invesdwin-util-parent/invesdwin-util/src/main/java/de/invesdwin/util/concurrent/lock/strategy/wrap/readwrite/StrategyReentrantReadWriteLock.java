package de.invesdwin.util.concurrent.lock.strategy.wrap.readwrite;

import java.util.concurrent.locks.Condition;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.readwrite.IReentrantReadWriteLock;
import de.invesdwin.util.concurrent.lock.readwrite.IReentrantWriteLock;
import de.invesdwin.util.concurrent.lock.strategy.DefaultLockingStrategy;
import de.invesdwin.util.concurrent.lock.strategy.ILockingStrategy;
import de.invesdwin.util.concurrent.lock.strategy.wrap.StrategyLock;
import de.invesdwin.util.concurrent.lock.strategy.wrap.readwrite.write.StrategyReentrantWriteLock;
import de.invesdwin.util.lang.Objects;

@ThreadSafe
public final class StrategyReentrantReadWriteLock implements IReentrantReadWriteLock {

    private final ILockingStrategy strategy;
    private final IReentrantReadWriteLock delegate;
    private final ILock readLock;
    private final IReentrantWriteLock writeLock;

    private StrategyReentrantReadWriteLock(final ILockingStrategy strategy, final IReentrantReadWriteLock delegate) {
        this.strategy = strategy;
        this.delegate = delegate;
        this.readLock = StrategyLock.maybeWrap(strategy, delegate.readLock());
        this.writeLock = StrategyReentrantWriteLock.maybeWrap(strategy, delegate.writeLock());
    }

    @Override
    public ILockingStrategy getStrategy() {
        return strategy;
    }

    public IReentrantReadWriteLock getDelegate() {
        return delegate;
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
    public IReentrantWriteLock writeLock() {
        return writeLock;
    }

    @Override
    public boolean isFair() {
        return delegate.isFair();
    }

    @Override
    public int getReadLockCount() {
        return delegate.getReadHoldCount();
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
    public int getWriteHoldCount() {
        return delegate.getWriteHoldCount();
    }

    @Override
    public int getReadHoldCount() {
        return delegate.getReadHoldCount();
    }

    @Override
    public boolean hasQueuedThreads() {
        return delegate.hasQueuedThreads();
    }

    @Override
    public boolean hasQueuedThread(final Thread thread) {
        return delegate.hasQueuedThread(thread);
    }

    @Override
    public int getQueueLength() {
        return delegate.getQueueLength();
    }

    @Override
    public boolean hasWaiters(final Condition condition) {
        return delegate.hasWaiters(condition);
    }

    @Override
    public int getWaitQueueLength(final Condition condition) {
        return delegate.getWaitQueueLength(condition);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).addValue(strategy).addValue(delegate).toString();
    }

    //CHECKSTYLE:OFF
    @Override
    public IReentrantReadWriteLock withStrategy(final ILockingStrategy strategy) {
        //CHECKSTYLE:ON
        return new StrategyReentrantReadWriteLock(strategy, delegate);
    }

    public static IReentrantReadWriteLock maybeWrap(final ILockingStrategy strategy,
            final IReentrantReadWriteLock lock) {
        if (strategy == DefaultLockingStrategy.INSTANCE) {
            return lock;
        }
        return new StrategyReentrantReadWriteLock(strategy, lock);
    }
}
