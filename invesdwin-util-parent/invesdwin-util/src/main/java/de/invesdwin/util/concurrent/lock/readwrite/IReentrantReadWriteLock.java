package de.invesdwin.util.concurrent.lock.readwrite;

import java.util.concurrent.locks.Condition;

public interface IReentrantReadWriteLock extends IReadWriteLock {

    boolean isFair();

    int getReadLockCount();

    boolean isWriteLocked();

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

}
