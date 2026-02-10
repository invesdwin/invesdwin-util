package de.invesdwin.util.concurrent.lock.padded;

import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.ICloseableLock;

/**
 * @see com.google.common.util.concurrent.Striped.PaddedLock
 * @see java.util.concurrent.locks.ReadWriteLock
 */
@ThreadSafe
public class CloseableReentrantReadWriteLock extends ReentrantReadWriteLock implements ICloseableLock {

    private final ICloseableLock write = () -> writeLock().unlock();

    public CloseableReentrantReadWriteLock() {
        super(false);
    }//new unfair

    @Override
    public void close() {
        readLock().unlock();
    }

    public ICloseableLock read() {
        readLock().lock();
        return this;
    }

    public ICloseableLock write() {
        writeLock().lock();
        return write;
    }

}