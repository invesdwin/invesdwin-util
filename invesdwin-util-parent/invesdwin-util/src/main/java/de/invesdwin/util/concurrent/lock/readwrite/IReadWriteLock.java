package de.invesdwin.util.concurrent.lock.readwrite;

import java.util.concurrent.locks.ReadWriteLock;

import de.invesdwin.util.concurrent.lock.ILock;

public interface IReadWriteLock extends ReadWriteLock {

    String getName();

    @Override
    ILock readLock();

    @Override
    ILock writeLock();

    boolean isWriteLocked();

    boolean isWriteLockedByCurrentThread();

}
