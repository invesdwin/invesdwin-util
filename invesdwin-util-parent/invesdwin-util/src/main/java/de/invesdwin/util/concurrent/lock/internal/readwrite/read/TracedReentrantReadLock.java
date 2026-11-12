package de.invesdwin.util.concurrent.lock.internal.readwrite.read;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.IQueuedLock;
import de.invesdwin.util.concurrent.lock.readwrite.IReentrantReadWriteLock;
import de.invesdwin.util.concurrent.lock.trace.ILockTrace;

@ThreadSafe
public class TracedReentrantReadLock extends TracedReadLock implements IQueuedLock {

    private final IReentrantReadWriteLock pair;

    public TracedReentrantReadLock(final ILockTrace lockTrace, final IReentrantReadWriteLock pair,
            final ILock delegate) {
        super(lockTrace, pair, delegate);
        this.pair = pair;
    }

    @Override
    public int getQueueLength() {
        return pair.getQueueLength();
    }

}
