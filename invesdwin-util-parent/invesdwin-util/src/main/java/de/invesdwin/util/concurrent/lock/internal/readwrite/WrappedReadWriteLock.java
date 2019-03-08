package de.invesdwin.util.concurrent.lock.internal.readwrite;

import java.util.concurrent.locks.ReadWriteLock;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.internal.WrappedLock;
import de.invesdwin.util.concurrent.lock.readwrite.IReadWriteLock;

@ThreadSafe
public class WrappedReadWriteLock implements IReadWriteLock {

    private final String name;
    private final ReadWriteLock delegate;
    private final WrappedLock readLock;
    private final WrappedLock writeLock;

    public WrappedReadWriteLock(final String name, final ReadWriteLock delegate) {
        this.name = name;
        this.delegate = delegate;
        this.readLock = new WrappedLock(name + "_readLock", delegate.readLock());
        this.writeLock = new WrappedLock(name + "_writeLock", delegate.writeLock());
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public WrappedLock readLock() {
        return readLock;
    }

    @Override
    public WrappedLock writeLock() {
        return writeLock;
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

}
