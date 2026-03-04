package de.invesdwin.util.concurrent.lock.padded;

import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.ICloseableLock;
import de.invesdwin.util.concurrent.lock.strategy.ILockingStrategy;

/**
 * @see com.google.common.util.concurrent.Striped.PaddedLock
 */
@ThreadSafe
public final class PaddedCloseableReentrantLock extends ReentrantLock implements ICloseableLock {
    /*
     * Padding from 40 into 64 bytes, same size as cache line. Might be beneficial to add a fourth long here, to
     * minimize chance of interference between consecutive locks, but I couldn't observe any benefit from that.
     */
    @SuppressWarnings("unused")
    private final long unused1 = 0L;
    @SuppressWarnings("unused")
    private final long unused2 = 0L;
    @SuppressWarnings("unused")
    private final long unused3 = 0L;

    public PaddedCloseableReentrantLock() {
        super(false);
    }//new unfair

    /** @see #unlock() */
    @Override
    public void close() {
        unlock();
    }

    public ICloseableLock locked() {
        lock();
        return this;
    }

    public ICloseableLock locked(final ILockingStrategy strategy) {
        strategy.lock(this);
        return this;
    }
}