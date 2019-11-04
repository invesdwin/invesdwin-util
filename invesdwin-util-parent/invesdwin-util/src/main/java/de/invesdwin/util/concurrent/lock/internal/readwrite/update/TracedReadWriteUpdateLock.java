package de.invesdwin.util.concurrent.lock.internal.readwrite.update;

import javax.annotation.concurrent.ThreadSafe;

import com.googlecode.concurentlocks.ReadWriteUpdateLock;

import de.invesdwin.util.concurrent.lock.internal.TracedLock;
import de.invesdwin.util.concurrent.lock.readwrite.IReadWriteUpdateLock;
import de.invesdwin.util.lang.Objects;

@ThreadSafe
public class TracedReadWriteUpdateLock implements IReadWriteUpdateLock {

    private final String name;
    private final ReadWriteUpdateLock delegate;
    private final TracedLock readLock;
    private final TracedLock writeLock;
    private final TracedLock updateLock;

    public TracedReadWriteUpdateLock(final String name, final ReadWriteUpdateLock delegate) {
        this.name = name;
        this.delegate = delegate;
        this.readLock = new TracedLock(name + "_readLock", delegate.readLock());
        this.writeLock = new TracedLock(name + "_writeLock", delegate.writeLock());
        this.updateLock = new TracedLock(name + "_updateLock", delegate.updateLock());
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
    public TracedLock updateLock() {
        return updateLock;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).addValue(name).addValue(delegate).toString();
    }
}
