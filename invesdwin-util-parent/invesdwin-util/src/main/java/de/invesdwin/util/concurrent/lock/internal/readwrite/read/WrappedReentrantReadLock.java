package de.invesdwin.util.concurrent.lock.internal.readwrite.read;

import java.util.concurrent.locks.Lock;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.IQueuedLock;
import de.invesdwin.util.concurrent.lock.readwrite.IReentrantReadWriteLock;

@ThreadSafe
public class WrappedReentrantReadLock extends WrappedReadLock implements IQueuedLock {

    private final IReentrantReadWriteLock pair;

    public WrappedReentrantReadLock(final String name, final IReentrantReadWriteLock pair, final Lock delegate) {
        super(name, pair, delegate);
        this.pair = pair;
    }

    @Override
    public int getQueueLength() {
        return pair.getQueueLength();
    }

}
