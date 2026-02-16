package de.invesdwin.util.concurrent.lock.disabled;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.strategy.DisabledLockingStrategy;
import de.invesdwin.util.concurrent.lock.strategy.ILockingStrategy;

@Immutable
public final class DisabledLock implements ILock {

    public static final DisabledLock INSTANCE = new DisabledLock();

    private DisabledLock() {}

    @Override
    public boolean isLocked() {
        return false;
    }

    @Override
    public boolean isLockedByCurrentThread() {
        return false;
    }

    @Override
    public void lock() {}

    @Override
    public void lockInterruptibly() throws InterruptedException {}

    @Override
    public boolean tryLock() {
        return true;
    }

    @Override
    public boolean tryLock(final long time, final TimeUnit unit) throws InterruptedException {
        return true;
    }

    @Override
    public void unlock() {}

    @Override
    public Condition newCondition() {
        return DisabledCondition.INSTANCE;
    }

    @Override
    public String getName() {
        return null;
    }

    //CHECKSTYLE:OFF
    @Override
    public ILock withStrategy(final ILockingStrategy strategy) {
        //CHECKSTYLE:ON
        return this;
    }

    @Override
    public ILockingStrategy getStrategy() {
        return DisabledLockingStrategy.INSTANCE;
    }

}
