package de.invesdwin.util.concurrent.lock;

import java.util.concurrent.locks.Condition;

import de.invesdwin.util.concurrent.lock.strategy.ILockingStrategy;

public interface IReentrantLock extends IQueuedLock {

    int getHoldCount();

    boolean isFair();

    boolean hasQueuedThreads();

    boolean hasQueuedThread(Thread thread);

    boolean hasWaiters(Condition condition);

    int getWaitQueueLength(Condition condition);

    @Override
    IReentrantLock withStrategy(ILockingStrategy strategy);

}
