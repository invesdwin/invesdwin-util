package de.invesdwin.util.concurrent.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

import javax.annotation.concurrent.Immutable;

@Immutable
public abstract class ASimpleLock implements ILock {

    @Override
    public void lockInterruptibly() throws InterruptedException {
        lock();
    }

    @Override
    public boolean tryLock() {
        lock();
        return true;
    }

    @Override
    public boolean tryLock(final long time, final TimeUnit unit) throws InterruptedException {
        lock();
        return true;
    }

    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException();
    }

}
