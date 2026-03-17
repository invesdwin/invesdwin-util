package de.invesdwin.util.concurrent.lock.disabled;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.concurrent.lock.readwrite.IReentrantWriteLock;
import de.invesdwin.util.concurrent.lock.strategy.ILockingStrategy;

@Immutable
public class DisabledReentrantWriteLock extends DisabledLock implements IReentrantWriteLock {

    public static final DisabledReentrantWriteLock INSTANCE = new DisabledReentrantWriteLock();

    protected DisabledReentrantWriteLock() {}

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
