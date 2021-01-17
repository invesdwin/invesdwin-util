package de.invesdwin.util.concurrent;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.time.duration.Duration;
import de.invesdwin.util.time.fdate.FTimeUnit;

@NotThreadSafe
public class LoopInterruptedCheck {

    protected final Thread currentThread;
    private final long checkIntervalNanos;
    private long nextIntervalNanos;
    private int checksPerInterval;
    private int checksInInterval;

    public LoopInterruptedCheck() {
        this(Duration.ONE_SECOND);
    }

    public LoopInterruptedCheck(final Duration checkInterval) {
        this.currentThread = Thread.currentThread();
        this.checkIntervalNanos = checkInterval.longValue(FTimeUnit.NANOSECONDS);
        this.nextIntervalNanos = getInitialNanoTime() + checkIntervalNanos;
    }

    protected long getInitialNanoTime() {
        return System.nanoTime();
    }

    public boolean check() throws InterruptedException {
        checksInInterval++;
        if (checksInInterval > checksPerInterval) {
            final long newIntervalNanos = getInitialNanoTime();
            if (newIntervalNanos > nextIntervalNanos) {
                onInterval();
                checksPerInterval = checksInInterval;
                checksInInterval = 0;
                nextIntervalNanos = newIntervalNanos + checkIntervalNanos;
                return true;
            }
        }
        return false;
    }

    protected void onInterval() throws InterruptedException {
        Threads.throwIfInterrupted(currentThread);
    }

}
