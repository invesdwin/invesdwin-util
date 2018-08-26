package de.invesdwin.util.concurrent.lock.readwrite;

import com.googlecode.concurentlocks.ReadWriteUpdateLock;

import de.invesdwin.util.concurrent.lock.ILock;

public interface IReadWriteUpdateLock extends IReadWriteLock, ReadWriteUpdateLock {

    @Override
    ILock updateLock();

}
