package de.invesdwin.util.concurrent.lock.disabled;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.Locks;
import de.invesdwin.util.concurrent.lock.readwrite.IReadWriteLock;
import de.invesdwin.util.concurrent.lock.strategy.DisabledLockingStrategy;
import de.invesdwin.util.concurrent.lock.strategy.ILockingStrategy;
import de.invesdwin.util.concurrent.lock.trace.ILockTrace;

@Immutable
public class DisabledReadWriteLock implements IReadWriteLock {

    public static final DisabledReadWriteLock INSTANCE = new DisabledReadWriteLock();

    protected DisabledReadWriteLock() {}

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
        return DisabledLock.INSTANCE;
    }

    @Override
    public ILock writeLock() {
        return DisabledLock.INSTANCE;
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

}
