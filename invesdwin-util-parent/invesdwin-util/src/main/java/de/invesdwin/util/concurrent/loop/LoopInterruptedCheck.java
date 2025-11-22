package de.invesdwin.util.concurrent.loop;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.concurrent.Threads;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.Duration;

@NotThreadSafe
public class LoopInterruptedCheck implements ILoopInterruptedCheck {

    protected final Thread currentThread;
    private final Duration checkInterval;
    private final long checkIntervalNanos;
    private long nextIntervalNanos;
    private int checksPerInterval;
    private int checksInInterval;

    public LoopInterruptedCheck() {
        this(Duration.ONE_SECOND);
    }

    public LoopInterruptedCheck(final Duration checkInterval) {
        this.currentThread = Thread.currentThread();
        this.checkInterval = checkInterval;
        this.checkIntervalNanos = checkInterval.longValue(FTimeUnit.NANOSECONDS);
        this.nextIntervalNanos = getInitialNanoTime() + checkIntervalNanos;
    }

    @Override
    public void resetInterval() {
        this.nextIntervalNanos = Long.MIN_VALUE;
        this.checksPerInterval = 0;
        this.checksInInterval = 0;
    }

    public Duration getCheckInterval() {
        return checkInterval;
    }

    protected long getInitialNanoTime() {
        return System.nanoTime();
    }

    @Override
    public final boolean check() throws InterruptedException {
        checksInInterval++;
        if (checksInInterval > checksPerInterval) {
            return checkClock();
        } else {
            return false;
        }
    }

    @Override
    public final boolean checkClock() throws InterruptedException {
        final long newIntervalNanos = System.nanoTime();
        if (newIntervalNanos > nextIntervalNanos) {
            final boolean result = onInterval();
            checksPerInterval = checksInInterval;
            checksInInterval = 0;
            nextIntervalNanos = newIntervalNanos + checkIntervalNanos;
            return result;
        } else {
            return false;
        }
    }

    protected boolean onInterval() throws InterruptedException {
        Threads.throwIfInterrupted(currentThread);
        return true;
    }

}
