package de.invesdwin.util.concurrent.lock.readwrite;

import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.strategy.ILockingStrategy;

public interface IReentrantWriteLock extends ILock {

    int getHoldCount();

    @Override
    IReentrantWriteLock withStrategy(ILockingStrategy strategy);

}
