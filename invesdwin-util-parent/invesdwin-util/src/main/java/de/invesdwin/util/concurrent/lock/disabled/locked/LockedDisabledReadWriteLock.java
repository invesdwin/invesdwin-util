package de.invesdwin.util.concurrent.lock.disabled.locked;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.Locks;
import de.invesdwin.util.concurrent.lock.readwrite.IReadWriteLock;
import de.invesdwin.util.concurrent.lock.strategy.DisabledLockingStrategy;
import de.invesdwin.util.concurrent.lock.strategy.ILockingStrategy;
import de.invesdwin.util.concurrent.lock.trace.ILockTrace;

@Immutable
public class LockedDisabledReadWriteLock implements IReadWriteLock {

    public static final LockedDisabledReadWriteLock INSTANCE = new LockedDisabledReadWriteLock();

    protected LockedDisabledReadWriteLock() {}

    @Override
    public String getName() {
        return null;
    }

    @Override
    public boolean isWriteLocked() {
        return false;
    }

    @Override
    public boolean isWriteLockedByCurrentThread() {
        return false;
    }

    @Override
    public ILock readLock() {
        return LockedDisabledLock.INSTANCE;
    }

    @Override
    public ILock writeLock() {
        return LockedDisabledLock.INSTANCE;
    }

    //CHECKSTYLE:OFF
    @Override
    public IReadWriteLock withStrategy(final ILockingStrategy strategy) {
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
