package de.invesdwin.util.concurrent.lock.strategy.wrap.readwrite.read;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.IQueuedLock;
import de.invesdwin.util.concurrent.lock.strategy.DefaultLockingStrategy;
import de.invesdwin.util.concurrent.lock.strategy.ILockingStrategy;
import de.invesdwin.util.concurrent.lock.trace.ILockTrace;
import de.invesdwin.util.lang.Objects;

@ThreadSafe
public final class StrategyReentrantReadLock implements IQueuedLock {

    private final ILockingStrategy strategy;
    private final IQueuedLock delegate;

    private StrategyReentrantReadLock(final ILockingStrategy strategy, final IQueuedLock delegate) {
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
    public int getQueueLength() {
        return delegate.getQueueLength();
    }

    @Override
    public boolean isHeldByCurrentThread() {
        return delegate.isHeldByCurrentThread();
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
    public ILock withStrategy(final ILockingStrategy strategy) {
        //CHECKSTYLE:ON
        return new StrategyReentrantReadLock(strategy, delegate);
    }

    public static IQueuedLock maybeWrap(final ILockingStrategy strategy, final IQueuedLock lock) {
        if (strategy == DefaultLockingStrategy.INSTANCE) {
            return lock;
        }
        return new StrategyReentrantReadLock(strategy, lock);
    }

    @Override
    public ILockTrace getLockTrace() {
        return delegate.getLockTrace();
    }

    @Override
    public boolean isDisabled() {
        return delegate.isDisabled() || strategy.isDisabled();
    }

}
