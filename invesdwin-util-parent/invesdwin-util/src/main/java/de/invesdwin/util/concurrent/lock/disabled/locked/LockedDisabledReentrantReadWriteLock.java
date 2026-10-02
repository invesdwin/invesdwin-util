package de.invesdwin.util.concurrent.lock.disabled.locked;

import java.util.concurrent.locks.Condition;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.concurrent.lock.readwrite.IReentrantReadWriteLock;
import de.invesdwin.util.concurrent.lock.readwrite.IReentrantWriteLock;
import de.invesdwin.util.concurrent.lock.strategy.ILockingStrategy;

@Immutable
public class LockedDisabledReentrantReadWriteLock extends LockedDisabledReadWriteLock implements IReentrantReadWriteLock {

    public static final LockedDisabledReentrantReadWriteLock INSTANCE = new LockedDisabledReentrantReadWriteLock();

    protected LockedDisabledReentrantReadWriteLock() {}

    @Override
    public IReentrantWriteLock writeLock() {
        return LockedDisabledReentrantWriteLock.INSTANCE;
    }

    //CHECKSTYLE:OFF
    @Override
    public IReentrantReadWriteLock withStrategy(final ILockingStrategy strategy) {
        //CHECKSTYLE:ON
        return this;
    }

    @Override
    public boolean isFair() {
        return false;
    }

    @Override
    public int getReadLockCount() {
        return 0;
    }

    @Override
    public int getWriteHoldCount() {
        return 0;
    }

    @Override
    public int getReadHoldCount() {
        return 0;
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
