package de.invesdwin.util.concurrent.lock.internal.readwrite.read;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.readwrite.IReadWriteLock;
import de.invesdwin.util.concurrent.lock.strategy.DefaultLockingStrategy;
import de.invesdwin.util.concurrent.lock.strategy.ILockingStrategy;
import de.invesdwin.util.concurrent.lock.strategy.wrap.StrategyLock;
import de.invesdwin.util.lang.Objects;

@ThreadSafe
public class WrappedReadLock implements ILock {

    private final String name;
    private final IReadWriteLock parent;
    private final Lock delegate;

    public WrappedReadLock(final String name, final IReadWriteLock parent, final Lock delegate) {
        this.name = name;
        this.parent = parent;
        this.delegate = delegate;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isLocked() {
        //intentionally gives the info of writelock because readlocks don't block among themselves
        return parent.isWriteLocked();
    }

    @Override
    public boolean isLockedByCurrentThread() {
        //intentionally gives the info of writelock because readlocks don't block among themselves
        return parent.isWriteLockedByCurrentThread();
    }

    @Override
    public void lock() {
        delegate.lock();
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        delegate.lockInterruptibly();
    }

    @Override
    public boolean tryLock() {
        return delegate.tryLock();
    }

    @Override
    public boolean tryLock(final long time, final TimeUnit unit) throws InterruptedException {
        return delegate.tryLock(time, unit);
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
        return Objects.toStringHelper(this).addValue(name).addValue(delegate).toString();
    }

    @Override
    public ILockingStrategy getStrategy() {
        return DefaultLockingStrategy.INSTANCE;
    }

    //CHECKSTYLE:OFF
    @Override
    public ILock withStrategy(final ILockingStrategy strategy) {
        //CHECKSTYLE:ON
        return StrategyLock.maybeWrap(strategy, this);
    }

}
