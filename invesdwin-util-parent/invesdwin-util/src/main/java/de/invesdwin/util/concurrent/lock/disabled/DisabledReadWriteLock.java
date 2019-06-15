package de.invesdwin.util.concurrent.lock.disabled;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.readwrite.IReadWriteLock;

@Immutable
public final class DisabledReadWriteLock implements IReadWriteLock {

    public static final DisabledReadWriteLock INSTANCE = new DisabledReadWriteLock();

    private DisabledReadWriteLock() {}

    @Override
    public String getName() {
        return null;
    }

    @Override
    public ILock readLock() {
        return DisabledLock.INSTANCE;
    }

    @Override
    public ILock writeLock() {
        return DisabledLock.INSTANCE;
    }

}
