package de.invesdwin.util.concurrent.lock.internal.readwrite.update;

import javax.annotation.concurrent.ThreadSafe;

import com.googlecode.concurentlocks.ReadWriteUpdateLock;

import de.invesdwin.util.concurrent.lock.internal.readwrite.read.WrappedReadLock;
import de.invesdwin.util.concurrent.lock.internal.readwrite.write.WrappedWriteLock;
import de.invesdwin.util.concurrent.lock.readwrite.IReadWriteUpdateLock;
import de.invesdwin.util.lang.Objects;

@ThreadSafe
public class WrappedReadWriteUpdateLock implements IReadWriteUpdateLock {

    private final String name;
    private final ReadWriteUpdateLock delegate;
    private final WrappedReadLock readLock;
    private final WrappedWriteLock writeLock;
    private final WrappedUpdateLock updateLock;

    public WrappedReadWriteUpdateLock(final String name, final ReadWriteUpdateLock delegate) {
        this.name = name;
        this.delegate = delegate;
        this.readLock = new WrappedReadLock(name + "_readLock", delegate.readLock());
        this.writeLock = new WrappedWriteLock(name + "_writeLock", delegate.writeLock());
        this.updateLock = new WrappedUpdateLock(name + "_updateLock", delegate.updateLock());
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public WrappedReadLock readLock() {
        return readLock;
    }

    @Override
    public WrappedWriteLock writeLock() {
        return writeLock;
    }

    @Override
    public WrappedUpdateLock updateLock() {
        return updateLock;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).addValue(name).addValue(delegate).toString();
    }
}
