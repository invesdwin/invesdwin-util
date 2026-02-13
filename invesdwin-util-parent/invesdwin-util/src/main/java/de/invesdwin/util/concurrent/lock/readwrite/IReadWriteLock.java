package de.invesdwin.util.concurrent.lock.readwrite;

import java.util.concurrent.locks.ReadWriteLock;

import de.invesdwin.util.concurrent.lock.ICloseableLock;
import de.invesdwin.util.concurrent.lock.ILock;

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

    boolean isWriteLocked();

    boolean isWriteLockedByCurrentThread();

}
