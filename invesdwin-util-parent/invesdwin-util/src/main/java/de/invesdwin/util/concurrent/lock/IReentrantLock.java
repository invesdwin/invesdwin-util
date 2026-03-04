package de.invesdwin.util.concurrent.lock;

import java.util.concurrent.locks.Condition;

import de.invesdwin.util.concurrent.lock.strategy.ILockingStrategy;

public interface IReentrantLock extends ILock {

    int getHoldCount();

    boolean isFair();

    boolean hasQueuedThreads();

    boolean hasQueuedThread(Thread thread);

    int getQueueLength();

    boolean hasWaiters(Condition condition);

    int getWaitQueueLength(Condition condition);

    @Override
    IReentrantLock withStrategy(ILockingStrategy strategy);

}
