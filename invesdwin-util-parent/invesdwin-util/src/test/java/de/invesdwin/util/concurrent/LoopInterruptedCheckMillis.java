package de.invesdwin.util.concurrent;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.time.duration.Duration;
import de.invesdwin.util.time.fdate.FTimeUnit;

@NotThreadSafe
public class LoopInterruptedCheckMillis {

    private final Thread currentThread;
    private final long checkIntervalMillis;
    private long nextIntervalMillis;
    private int checksPerInterval;
    private int checksInInterval;

    public LoopInterruptedCheckMillis(final Duration checkInterval) {
        this.currentThread = Thread.currentThread();
        this.checkIntervalMillis = checkInterval.longValue(FTimeUnit.MILLISECONDS);
        this.nextIntervalMillis = System.currentTimeMillis() + checkIntervalMillis;
    }

    public boolean check() throws InterruptedException {
        checksInInterval++;
        if (checksInInterval > checksPerInterval) {
            final long newIntervalNanos = System.currentTimeMillis();
            if (newIntervalNanos > nextIntervalMillis) {
                onInterval();
                checksPerInterval = checksInInterval;
                checksInInterval = 0;
                nextIntervalMillis = newIntervalNanos + checkIntervalMillis;
                return true;
            }
        }
        return false;
    }

    protected void onInterval() throws InterruptedException {
        Threads.throwIfInterrupted(currentThread);
    }

}
