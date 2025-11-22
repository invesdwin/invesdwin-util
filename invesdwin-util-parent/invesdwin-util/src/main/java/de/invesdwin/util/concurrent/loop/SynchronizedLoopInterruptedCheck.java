package de.invesdwin.util.concurrent.loop;

import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.concurrent.Threads;
import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.Duration;

@ThreadSafe
public class SynchronizedLoopInterruptedCheck implements ILoopInterruptedCheck {

    private final Duration checkInterval;
    private final long checkIntervalNanos;
    private volatile long nextIntervalNanos;
    private volatile int checksPerInterval;
    private final AtomicInteger checksInInterval = new AtomicInteger();
    private final ILock lock = ILockCollectionFactory.getInstance(true)
            .newLock(SynchronizedLoopInterruptedCheck.class.getSimpleName());

    public SynchronizedLoopInterruptedCheck() {
        this(Duration.ONE_SECOND);
    }

    public SynchronizedLoopInterruptedCheck(final Duration checkInterval) {
        this.checkInterval = checkInterval;
        this.checkIntervalNanos = checkInterval.longValue(FTimeUnit.NANOSECONDS);
        this.nextIntervalNanos = getInitialNanoTime() + checkIntervalNanos;
    }

    @Override
    public void resetInterval() {
        this.nextIntervalNanos = Long.MIN_VALUE;
        this.checksPerInterval = 0;
        this.checksInInterval.set(0);
    }

    public Duration getCheckInterval() {
        return checkInterval;
    }

    protected long getInitialNanoTime() {
        return System.nanoTime();
    }

    @Override
    public final boolean check() throws InterruptedException {
        final int checksInIntervalValue = checksInInterval.incrementAndGet();
        if (checksInIntervalValue > checksPerInterval) {
            return checkClock();
        } else {
            return false;
        }
    }

    @Override
    public final boolean checkClock() throws InterruptedException {
        final long newIntervalNanos = getInitialNanoTime();
        if (newIntervalNanos > nextIntervalNanos) {
            if (lock.tryLock()) {
                try {
                    if (newIntervalNanos > nextIntervalNanos) {
                        final boolean result = onInterval();
                        checksPerInterval = checksInInterval.get();
                        checksInInterval.set(0);
                        nextIntervalNanos = newIntervalNanos + checkIntervalNanos;
                        return result;
                    } else {
                        return false;
                    }
                } finally {
                    lock.unlock();
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    protected boolean onInterval() throws InterruptedException {
        Threads.throwIfInterrupted(Thread.currentThread());
        return true;
    }

}
