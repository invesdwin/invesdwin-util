package de.invesdwin.util.concurrent.lock.padded;

import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.ICloseableLock;

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

}