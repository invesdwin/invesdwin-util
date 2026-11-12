package de.invesdwin.util.concurrent.lock;

import de.invesdwin.util.concurrent.lock.strategy.ILockingStrategy;

/**
 * A lock that can report its own wait queue - the lock of a read-write pair shares ONE queue between its read view and
 * its write view, so for a view every number here is the number of the whole pair. A queued lock also stays queued
 * when a locking strategy is applied to it.
 */
public interface IQueuedLock extends ILock {

    int getQueueLength();

    boolean hasQueuedThreads();

    boolean hasQueuedThread(Thread thread);

    @Override
    IQueuedLock withStrategy(ILockingStrategy strategy);

}
