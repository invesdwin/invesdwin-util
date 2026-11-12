package de.invesdwin.util.concurrent.lock.internal.readwrite.read;

import java.util.concurrent.locks.Lock;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.IQueuedLock;
import de.invesdwin.util.concurrent.lock.readwrite.IReentrantReadWriteLock;
import de.invesdwin.util.concurrent.lock.trace.ILockTrace;

@ThreadSafe
public class WrappedTracedReentrantReadLock extends WrappedTracedReadLock implements IQueuedLock {

    private final IReentrantReadWriteLock pair;

    public WrappedTracedReentrantReadLock(final ILockTrace lockTrace, final String name,
            final IReentrantReadWriteLock pair, final Lock delegate) {
        super(lockTrace, name, pair, delegate);
        this.pair = pair;
    }

    @Override
    public int getQueueLength() {
        return pair.getQueueLength();
    }

}
