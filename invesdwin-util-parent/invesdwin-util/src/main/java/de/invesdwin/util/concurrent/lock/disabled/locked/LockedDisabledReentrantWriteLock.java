package de.invesdwin.util.concurrent.lock.disabled.locked;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.concurrent.lock.readwrite.IReentrantWriteLock;
import de.invesdwin.util.concurrent.lock.strategy.ILockingStrategy;

@Immutable
public class LockedDisabledReentrantWriteLock extends LockedDisabledLock implements IReentrantWriteLock {

    public static final LockedDisabledReentrantWriteLock INSTANCE = new LockedDisabledReentrantWriteLock();

    protected LockedDisabledReentrantWriteLock() {}

    @Override
    public int getHoldCount() {
        return 0;
    }

    //CHECKSTYLE:OFF
    @Override
    public IReentrantWriteLock withStrategy(final ILockingStrategy strategy) {
        //CHECKSTYLE:ON
        return this;
    }

}
