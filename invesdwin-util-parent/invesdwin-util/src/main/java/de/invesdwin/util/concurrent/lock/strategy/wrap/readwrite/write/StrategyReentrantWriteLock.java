package de.invesdwin.util.concurrent.lock.strategy.wrap.readwrite.write;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.readwrite.IReentrantWriteLock;
import de.invesdwin.util.concurrent.lock.strategy.DefaultLockingStrategy;
import de.invesdwin.util.concurrent.lock.strategy.ILockingStrategy;
import de.invesdwin.util.lang.Objects;

@ThreadSafe
public final class StrategyReentrantWriteLock implements IReentrantWriteLock {

    private final ILockingStrategy strategy;
    private final IReentrantWriteLock delegate;

    private StrategyReentrantWriteLock(final ILockingStrategy strategy, final IReentrantWriteLock delegate) {
        this.strategy = strategy;
        this.delegate = delegate;
    }

    @Override
    public ILockingStrategy getStrategy() {
        return strategy;
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
    public String toString() {
        return Objects.toStringHelper(this).addValue(strategy).addValue(delegate).toString();
    }

    //CHECKSTYLE:OFF
    @Override
    public IReentrantWriteLock withStrategy(final ILockingStrategy strategy) {
        //CHECKSTYLE:ON
        return new StrategyReentrantWriteLock(strategy, delegate);
    }

    public static IReentrantWriteLock maybeWrap(final ILockingStrategy strategy, final IReentrantWriteLock lock) {
        if (strategy == DefaultLockingStrategy.INSTANCE) {
            return lock;
        }
        return new StrategyReentrantWriteLock(strategy, lock);
    }

}
