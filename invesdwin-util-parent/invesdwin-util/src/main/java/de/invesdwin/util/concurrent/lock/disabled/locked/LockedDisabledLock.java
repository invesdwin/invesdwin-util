package de.invesdwin.util.concurrent.lock.disabled.locked;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.concurrent.lock.IQueuedLock;
import de.invesdwin.util.concurrent.lock.Locks;
import de.invesdwin.util.concurrent.lock.strategy.DisabledLockingStrategy;
import de.invesdwin.util.concurrent.lock.strategy.ILockingStrategy;
import de.invesdwin.util.concurrent.lock.trace.ILockTrace;

@Immutable
public class LockedDisabledLock implements IQueuedLock {

    public static final LockedDisabledLock INSTANCE = new LockedDisabledLock();

    protected LockedDisabledLock() {}

    @Override
    public boolean isLocked() {
        return false;
    }

    @Override
    public boolean isHeldByCurrentThread() {
        return false;
    }

    @Override
    public void lock() {}

    @Override
    public void lockInterruptibly() throws InterruptedException {}

    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public boolean tryLock(final long time, final TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void unlock() {}

    @Override
    public Condition newCondition() {
        return LockedDisabledCondition.INSTANCE;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public int getQueueLength() {
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

    //CHECKSTYLE:OFF
    @Override
    public IQueuedLock withStrategy(final ILockingStrategy strategy) {
        //CHECKSTYLE:ON
        return this;
    }

    @Override
    public ILockingStrategy getStrategy() {
        return DisabledLockingStrategy.INSTANCE;
    }

    @SuppressWarnings("deprecation")
    @Override
    public ILockTrace getLockTrace() {
        return Locks.getDefaultLockTrace();
    }

    @Override
    public boolean isDisabled() {
        return true;
    }

}
