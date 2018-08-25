package de.invesdwin.util.concurrent.lock.readwrite.update;

import javax.annotation.concurrent.ThreadSafe;

import com.googlecode.concurentlocks.ReentrantReadWriteUpdateLock;

import de.invesdwin.util.concurrent.lock.WrappedLock;

@ThreadSafe
public class WrappedReentrantReadWriteUpdateLock implements IReadWriteUpdateLock {

    private final String name;
    private final ReentrantReadWriteUpdateLock delegate;
    private final WrappedLock readLock;
    private final WrappedLock writeLock;
    private final WrappedLock updateLock;

    public WrappedReentrantReadWriteUpdateLock(final String name, final ReentrantReadWriteUpdateLock delegate) {
        this.name = name;
        this.delegate = delegate;
        this.readLock = new WrappedLock(name + "_readLock", delegate.readLock());
        this.writeLock = new WrappedLock(name + "_writeLock", delegate.writeLock());
        this.updateLock = new WrappedLock(name + "_updateLock", delegate.updateLock());
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
    public WrappedLock updateLock() {
        return updateLock;
    }

    @Override
    public String toString() {
        return delegate.toString();
    }
}
