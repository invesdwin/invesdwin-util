package de.invesdwin.util.concurrent.lock.padded;

import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.ICloseableLock;
import de.invesdwin.util.concurrent.lock.strategy.ILockingStrategy;

/**
 * @see java.util.concurrent.locks.ReadWriteLock
 */
@ThreadSafe
public class CloseableReentrantReadWriteLock extends ReentrantReadWriteLock {

    public CloseableReentrantReadWriteLock() {
        super(false);
    }//new unfair

    public ICloseableLock readLocked() {
        final ReadLock readLock = readLock();
        readLock.lock();
        return readLock::unlock;
    }

    public ICloseableLock writeLocked() {
        final WriteLock writeLock = writeLock();
        writeLock.lock();
        return writeLock::unlock;
    }

    public ICloseableLock readLocked(final ILockingStrategy strategy) {
        final ReadLock readLock = readLock();
        strategy.lock(readLock);
        return readLock::unlock;
    }

    public ICloseableLock writeLocked(final ILockingStrategy strategy) {
        final WriteLock writeLock = writeLock();
        strategy.lock(writeLock);
        return writeLock::unlock;
    }

}