package de.invesdwin.util.concurrent.lock.strategy;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class DisabledLockingStrategy implements ILockingStrategy {

    public static final DisabledLockingStrategy INSTANCE = new DisabledLockingStrategy();

    private DisabledLockingStrategy() {}

    @Override
    public void lock(final java.util.concurrent.locks.Lock lock) {}

    @Override
    public void lockInterruptibly(final java.util.concurrent.locks.Lock lock) throws InterruptedException {}

    @Override
    public boolean tryLock(final java.util.concurrent.locks.Lock lock) {
        return true;
    }

    @Override
    public boolean tryLock(final java.util.concurrent.locks.Lock lock, final long time,
            final java.util.concurrent.TimeUnit unit) throws InterruptedException {
        return true;
    }

}
