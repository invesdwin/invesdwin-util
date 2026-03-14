package de.invesdwin.util.concurrent.lock.readwrite;

import java.util.concurrent.locks.ReadWriteLock;

import de.invesdwin.util.concurrent.lock.ICloseableLock;
import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.strategy.ILockingStrategy;

public interface IReadWriteLock extends ReadWriteLock {

    String getName();

    @Override
    ILock readLock();

    @Override
    ILock writeLock();

    default ICloseableLock readLocked() {
        final ILock readLock = readLock();
        readLock.lock();
        return readLock;
    }

    default ICloseableLock writeLocked() {
        final ILock writeLock = writeLock();
        writeLock.lock();
        return writeLock;
    }

    default ICloseableLock readLocked(final ILockingStrategy strategy) {
        final ILock readLock = readLock();
        strategy.lock(readLock);
        return readLock;
    }

    default ICloseableLock writeLocked(final ILockingStrategy strategy) {
        final ILock writeLock = writeLock();
        strategy.lock(writeLock);
        return writeLock;
    }

    boolean isWriteLocked();

    boolean isWriteLockedByCurrentThread();

    IReadWriteLock withStrategy(ILockingStrategy strategy);

    ILockingStrategy getStrategy();

}
