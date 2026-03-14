package de.invesdwin.util.concurrent.lock.strategy.wrap;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.IReentrantLock;
import de.invesdwin.util.concurrent.lock.strategy.DefaultLockingStrategy;
import de.invesdwin.util.concurrent.lock.strategy.ILockingStrategy;
import de.invesdwin.util.lang.Objects;

@ThreadSafe
public final class StrategyReentrantLock implements IReentrantLock {

    private final ILockingStrategy strategy;
    private final IReentrantLock delegate;

    private StrategyReentrantLock(final ILockingStrategy strategy, final IReentrantLock delegate) {
        this.strategy = strategy;
        this.delegate = delegate;
    }

    @Override
    public ILockingStrategy getStrategy() {
        return strategy;
    }

    public IReentrantLock getDelegate() {
        return delegate;
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public void lock() {
        strategy.lock(delegate);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        strategy.lockInterruptibly(delegate);
    }

    @Override
    public boolean tryLock() {
        return strategy.tryLock(delegate);
    }

    @Override
    public boolean tryLock(final long time, final TimeUnit unit) throws InterruptedException {
        return strategy.tryLock(delegate, time, unit);
    }

    @Override
    public void unlock() {
        delegate.unlock();
    }

    @Override
    public Condition newCondition() {
        return delegate.newCondition();
    }

    @Override
    public int getHoldCount() {
        return delegate.getHoldCount();
    }

    @Override
    public boolean isLockedByCurrentThread() {
        return delegate.isLockedByCurrentThread();
    }

    @Override
    public boolean isLocked() {
        return delegate.isLocked();
    }

    @Override
    public boolean isFair() {
        return delegate.isFair();
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
    public IReentrantLock withStrategy(final ILockingStrategy strategy) {
        //CHECKSTYLE:ON
        return new StrategyReentrantLock(strategy, delegate);
    }

    public static IReentrantLock maybeWrap(final ILockingStrategy strategy, final IReentrantLock lock) {
        if (strategy == DefaultLockingStrategy.INSTANCE) {
            return lock;
        }
        return new StrategyReentrantLock(strategy, lock);
    }
}
