package de.invesdwin.util.concurrent.lock.strategy;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.concurrent.loop.spinwait.SpinWait;

@Immutable
public final class SpinWaitingLockingStrategy implements ILockingStrategy {

    public static final SpinWaitingLockingStrategy INSTANCE = new SpinWaitingLockingStrategy();

    private final SpinWait spinWait;

    private SpinWaitingLockingStrategy() {
        this(new SpinWait());
    }

    public SpinWaitingLockingStrategy(final SpinWait spinWait) {
        this.spinWait = spinWait;
    }

    @Override
    public void lock(final Lock lock) {
        try {
            spinWait.awaitFulfill(lock::tryLock, System.nanoTime());
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void lockInterruptibly(final Lock lock) throws InterruptedException {
        try {
            spinWait.awaitFulfill(lock::tryLock, System.nanoTime());
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            throw e;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean tryLock(final Lock lock) {
        return lock.tryLock();
    }

    @Override
    public boolean tryLock(final Lock lock, final long time, final TimeUnit unit) throws InterruptedException {
        try {
            return spinWait.awaitFulfill(lock::tryLock, System.nanoTime(), time, unit);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            throw e;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

}
