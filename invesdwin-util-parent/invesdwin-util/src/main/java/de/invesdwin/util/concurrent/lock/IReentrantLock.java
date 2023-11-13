package de.invesdwin.util.concurrent.lock;

import java.util.concurrent.locks.Condition;

public interface IReentrantLock extends ILock {

    int getHoldCount();

    boolean isFair();

    boolean hasQueuedThreads();

    boolean hasQueuedThread(Thread thread);

    int getQueueLength();

    boolean hasWaiters(Condition condition);

    int getWaitQueueLength(Condition condition);

}
