package de.invesdwin.util.concurrent.lock.strategy.wrap;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.strategy.DefaultLockingStrategy;
import de.invesdwin.util.concurrent.lock.strategy.ILockingStrategy;
import de.invesdwin.util.lang.Objects;

@ThreadSafe
public final class StrategyLock implements ILock {

    private final ILockingStrategy strategy;
    private final ILock delegate;

    private StrategyLock(final ILockingStrategy strategy, final ILock delegate) {
        this.strategy = strategy;
        this.delegate = delegate;
    }

    @Override
    public ILockingStrategy getStrategy() {
        return strategy;
    }

    public ILock getDelegate() {
        return delegate;
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public boolean isLocked() {
        return delegate.isLocked();
    }

    @Override
    public boolean isLockedByCurrentThread() {
        return delegate.isLockedByCurrentThread();
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
    public String toString() {
        return Objects.toStringHelper(this).addValue(strategy).addValue(delegate).toString();
    }

    //CHECKSTYLE:OFF
    @Override
    public ILock withStrategy(final ILockingStrategy strategy) {
        //CHECKSTYLE:ON
        return new StrategyLock(strategy, delegate);
    }

    public static ILock maybeWrap(final ILockingStrategy strategy, final ILock lock) {
        if (strategy == DefaultLockingStrategy.INSTANCE) {
            return lock;
        }
        return new StrategyLock(strategy, lock);
    }

}
