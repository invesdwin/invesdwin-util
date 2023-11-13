package de.invesdwin.util.concurrent.lock.internal.readwrite;

import java.util.concurrent.locks.ReadWriteLock;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.internal.readwrite.read.TracedReadLock;
import de.invesdwin.util.concurrent.lock.internal.readwrite.write.TracedWriteLock;
import de.invesdwin.util.concurrent.lock.readwrite.IReadWriteLock;
import de.invesdwin.util.lang.Objects;

@ThreadSafe
public class TracedReadWriteLock implements IReadWriteLock {

    private final String name;
    private final ReadWriteLock delegate;
    private final TracedReadLock readLock;
    private final TracedWriteLock writeLock;

    public TracedReadWriteLock(final String name, final ReadWriteLock delegate) {
        this.name = name;
        this.delegate = delegate;
        this.readLock = new TracedReadLock(name + "_readLock", this, delegate.readLock());
        this.writeLock = new TracedWriteLock(readLock.getName(), name + "_writeLock", delegate.writeLock());
    }

    @Override
    public boolean isWriteLocked() {
        return writeLock.isLocked();
    }

    @Override
    public boolean isWriteLockedByCurrentThread() {
        return writeLock.isLockedByCurrentThread();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public TracedReadLock readLock() {
        return readLock;
    }

    @Override
    public TracedWriteLock writeLock() {
        return writeLock;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).addValue(name).addValue(delegate).toString();
    }

}
