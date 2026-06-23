package de.invesdwin.util.concurrent.lock.disabled;

import java.util.concurrent.locks.Condition;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.concurrent.lock.IReentrantLock;
import de.invesdwin.util.concurrent.lock.strategy.ILockingStrategy;

@Immutable
public class DisabledReentrantLock extends DisabledLock implements IReentrantLock {

    public static final DisabledReentrantLock INSTANCE = new DisabledReentrantLock();

    protected DisabledReentrantLock() {}

    @Override
    public int getHoldCount() {
        return 0;
    }

    //CHECKSTYLE:OFF
    @Override
    public IReentrantLock withStrategy(final ILockingStrategy strategy) {
        //CHECKSTYLE:ON
        return this;
    }

    @Override
    public boolean isFair() {
        return false;
    }

    @Override
    public boolean hasQueuedThreads() {
        return false;
    }

    @Override
    public boolean hasQueuedThread(final Thread thread) {
        return false;
    }

    @Override
    public int getQueueLength() {
        return 0;
    }

    @Override
    public boolean hasWaiters(final Condition condition) {
        return false;
    }

    @Override
    public int getWaitQueueLength(final Condition condition) {
        return 0;
    }

}
