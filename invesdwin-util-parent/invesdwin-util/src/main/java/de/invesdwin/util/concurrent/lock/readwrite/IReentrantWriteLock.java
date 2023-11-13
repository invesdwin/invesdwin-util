package de.invesdwin.util.concurrent.lock.readwrite;

import de.invesdwin.util.concurrent.lock.ILock;

public interface IReentrantWriteLock extends ILock {

    int getHoldCount();

}
