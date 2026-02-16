package de.invesdwin.util.concurrent.lock.strategy;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

public interface ILockingStrategy {

    void lock(Lock lock);

    void lockInterruptibly(Lock lock) throws InterruptedException;

    boolean tryLock(Lock lock);

    boolean tryLock(Lock lock, long time, TimeUnit unit) throws InterruptedException;

}
