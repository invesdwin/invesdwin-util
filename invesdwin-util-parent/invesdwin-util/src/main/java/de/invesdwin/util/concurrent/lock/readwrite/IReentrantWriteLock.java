package de.invesdwin.util.concurrent.lock.readwrite;

import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.strategy.ILockingStrategy;

public interface IReentrantWriteLock extends ILock {

    int getHoldCount();

    /**
     * The wait queue of the read-write pair this view belongs to: a read-write lock has one queue shared by both
     * views, so the number is always the pair's.
     */
    int getQueueLength();

    @Override
    IReentrantWriteLock withStrategy(ILockingStrategy strategy);

}
