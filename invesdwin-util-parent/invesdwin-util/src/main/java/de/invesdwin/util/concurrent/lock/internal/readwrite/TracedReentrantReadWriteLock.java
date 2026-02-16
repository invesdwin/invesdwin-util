package de.invesdwin.util.concurrent.lock.internal.readwrite;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.internal.readwrite.read.TracedReadLock;
import de.invesdwin.util.concurrent.lock.internal.readwrite.write.TracedReentrantWriteLock;
import de.invesdwin.util.concurrent.lock.readwrite.IReentrantReadWriteLock;
import de.invesdwin.util.concurrent.lock.strategy.DefaultLockingStrategy;
import de.invesdwin.util.concurrent.lock.strategy.ILockingStrategy;
import de.invesdwin.util.concurrent.lock.strategy.wrap.readwrite.StrategyReentrantReadWriteLock;
import de.invesdwin.util.lang.Objects;

@ThreadSafe
public class TracedReentrantReadWriteLock implements IReentrantReadWriteLock {

    private final String name;
    private final ReentrantReadWriteLock delegate;
    private final TracedReadLock readLock;
    private final TracedReentrantWriteLock writeLock;

    public TracedReentrantReadWriteLock(final String name, final ReentrantReadWriteLock delegate) {
        this.name = name;
        this.delegate = delegate;
        this.readLock = new TracedReadLock(name + "_readLock", this, delegate.readLock());
        this.writeLock = new TracedReentrantWriteLock(readLock.getName(), name + "_writeLock", delegate,
                delegate.writeLock());
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public TracedReadLock readLock() {
        return readLock;
    }

    @Override
    public TracedReentrantWriteLock writeLock() {
        return writeLock;
    }

    @Override
    public final boolean isFair() {
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
        return Objects.toStringHelper(this).addValue(name).addValue(delegate).toString();
    }

    @Override
    public ILockingStrategy getStrategy() {
        return DefaultLockingStrategy.INSTANCE;
    }

    //CHECKSTYLE:OFF
    @Override
    public IReentrantReadWriteLock withStrategy(final ILockingStrategy strategy) {
        //CHECKSTYLE:ON
        return StrategyReentrantReadWriteLock.maybeWrap(strategy, this);
    }

}
