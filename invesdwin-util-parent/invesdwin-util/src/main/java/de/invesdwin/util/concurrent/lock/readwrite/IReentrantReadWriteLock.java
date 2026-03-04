package de.invesdwin.util.concurrent.lock.readwrite;

import java.util.concurrent.locks.Condition;

import de.invesdwin.util.concurrent.lock.strategy.ILockingStrategy;

public interface IReentrantReadWriteLock extends IReadWriteLock {

    boolean isFair();

    int getReadLockCount();

    @Override
    boolean isWriteLocked();

    @Override
    boolean isWriteLockedByCurrentThread();

    int getWriteHoldCount();

    int getReadHoldCount();

    boolean hasQueuedThreads();

    boolean hasQueuedThread(Thread thread);

    int getQueueLength();

    boolean hasWaiters(Condition condition);

    int getWaitQueueLength(Condition condition);

    @Override
    IReentrantWriteLock writeLock();

    @Override
    IReentrantReadWriteLock withStrategy(ILockingStrategy strategy);

}
