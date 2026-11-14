package de.invesdwin.util.concurrent.lock.readwrite;

import java.util.concurrent.locks.Condition;

import de.invesdwin.util.concurrent.lock.IQueuedLock;
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

    /**
     * The read view of this pair: it has no queue of its own, the pair has one that both views share.
     */
    @Override
    IQueuedLock readLock();

    @Override
    IReentrantWriteLock writeLock();

    @Override
    IReentrantReadWriteLock withStrategy(ILockingStrategy strategy);

}
