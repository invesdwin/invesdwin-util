package de.invesdwin.util.concurrent.lock.readwrite;

import de.invesdwin.util.concurrent.lock.IQueuedLock;
import de.invesdwin.util.concurrent.lock.strategy.ILockingStrategy;

public interface IReentrantWriteLock extends IQueuedLock {

    int getHoldCount();

    @Override
    IReentrantWriteLock withStrategy(ILockingStrategy strategy);

}
