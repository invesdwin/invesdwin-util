package de.invesdwin.util.concurrent.lock.strategy;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class DefaultLockingStrategy implements ILockingStrategy {

    public static final DefaultLockingStrategy INSTANCE = new DefaultLockingStrategy();

    private DefaultLockingStrategy() {}

    @Override
    public void lock(final Lock lock) {
        lock.lock();
    }

    @Override
    public void lockInterruptibly(final Lock lock) throws InterruptedException {
        lock.lockInterruptibly();
    }

    @Override
    public boolean tryLock(final Lock lock) {
        return lock.tryLock();
    }

    @Override
    public boolean tryLock(final Lock lock, final long time, final TimeUnit unit) throws InterruptedException {
        return lock.tryLock(time, unit);
    }

}
