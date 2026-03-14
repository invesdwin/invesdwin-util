package de.invesdwin.util.concurrent.lock.padded;

import javax.annotation.concurrent.ThreadSafe;

/**
 * @see com.google.common.util.concurrent.Striped.PaddedLock
 */
@ThreadSafe
public final class PaddedCloseableReentrantReadWriteLock extends CloseableReentrantReadWriteLock {
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
}