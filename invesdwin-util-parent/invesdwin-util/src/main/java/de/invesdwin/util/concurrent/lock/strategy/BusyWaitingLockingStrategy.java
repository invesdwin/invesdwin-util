package de.invesdwin.util.concurrent.lock.strategy;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.concurrent.Threads;
import de.invesdwin.util.concurrent.loop.base.ABaseSpinWait;

@Immutable
public final class BusyWaitingLockingStrategy implements ILockingStrategy {

    public static final BusyWaitingLockingStrategy INSTANCE = new BusyWaitingLockingStrategy();

    private BusyWaitingLockingStrategy() {}

    @Override
    public void lock(final Lock lock) {
        while (true) {
            if (lock.tryLock()) {
                return;
            }
            ABaseSpinWait.onSpinWaitStatic();
        }
    }

    @Override
    public void lockInterruptibly(final Lock lock) throws InterruptedException {
        while (true) {
            if (lock.tryLock()) {
                return;
            }
            ABaseSpinWait.onSpinWaitStatic();
            Threads.throwIfInterrupted();
        }
    }

    @Override
    public boolean tryLock(final Lock lock) {
        return lock.tryLock();
    }

    @Override
    public boolean tryLock(final Lock lock, final long time, final TimeUnit unit) throws InterruptedException {
        if (lock.tryLock()) {
            return true;
        }
        final long nanoRemaining = TimeUnit.NANOSECONDS.convert(time, unit);
        final long waitDeadline = System.nanoTime() + nanoRemaining;
        while (true) {
            if (lock.tryLock()) {
                return true;
            }
            ABaseSpinWait.onSpinWaitStatic();
            Threads.throwIfInterrupted();
            if (System.nanoTime() >= waitDeadline) {
                return false;
            }
        }
    }

}
