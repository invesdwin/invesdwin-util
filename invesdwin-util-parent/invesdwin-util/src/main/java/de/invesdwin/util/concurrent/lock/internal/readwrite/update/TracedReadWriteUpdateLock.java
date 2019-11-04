package de.invesdwin.util.concurrent.lock.internal.readwrite.update;

import javax.annotation.concurrent.ThreadSafe;

import com.googlecode.concurentlocks.ReadWriteUpdateLock;

import de.invesdwin.util.concurrent.lock.internal.readwrite.read.TracedReadLock;
import de.invesdwin.util.concurrent.lock.internal.readwrite.write.TracedWriteLock;
import de.invesdwin.util.concurrent.lock.readwrite.IReadWriteUpdateLock;
import de.invesdwin.util.lang.Objects;

@ThreadSafe
public class TracedReadWriteUpdateLock implements IReadWriteUpdateLock {

    private final String name;
    private final ReadWriteUpdateLock delegate;
    private final TracedReadLock readLock;
    private final TracedWriteLock writeLock;
    private final TracedUpdateLock updateLock;

    public TracedReadWriteUpdateLock(final String name, final ReadWriteUpdateLock delegate) {
        this.name = name;
        this.delegate = delegate;
        this.readLock = new TracedReadLock(name + "_readLock", delegate.readLock());
        this.writeLock = new TracedWriteLock(readLock.getName(), name + "_writeLock", delegate.writeLock());
        this.updateLock = new TracedUpdateLock(name + "_updateLock", delegate.updateLock());
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
    public TracedUpdateLock updateLock() {
        return updateLock;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).addValue(name).addValue(delegate).toString();
    }
}
