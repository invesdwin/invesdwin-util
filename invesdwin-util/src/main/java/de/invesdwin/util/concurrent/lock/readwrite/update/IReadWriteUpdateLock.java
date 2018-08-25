package de.invesdwin.util.concurrent.lock.readwrite.update;

import com.googlecode.concurentlocks.ReadWriteUpdateLock;

import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.readwrite.IReadWriteLock;

public interface IReadWriteUpdateLock extends IReadWriteLock, ReadWriteUpdateLock {

    @Override
    ILock updateLock();

}
