package de.invesdwin.util.concurrent.lock.internal.readwrite;

import java.util.concurrent.locks.ReadWriteLock;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.internal.TracedLock;
import de.invesdwin.util.concurrent.lock.readwrite.IReadWriteLock;

@ThreadSafe
public class TracedReadWriteLock implements IReadWriteLock {

    private final String name;
    private final ReadWriteLock delegate;
    private final TracedLock readLock;
    private final TracedLock writeLock;

    public TracedReadWriteLock(final String name, final ReadWriteLock delegate) {
        this.name = name;
        this.delegate = delegate;
        this.readLock = new TracedLock(name + "_readLock", delegate.readLock());
        this.writeLock = new TracedLock(name + "_writeLock", delegate.writeLock());
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public TracedLock readLock() {
        return readLock;
    }

    @Override
    public TracedLock writeLock() {
        return writeLock;
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

}
