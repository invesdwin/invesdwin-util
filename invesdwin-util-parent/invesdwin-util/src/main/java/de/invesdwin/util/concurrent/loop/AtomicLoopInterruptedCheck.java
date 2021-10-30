package de.invesdwin.util.concurrent.loop;

import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.Threads;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.Duration;

@ThreadSafe
public class AtomicLoopInterruptedCheck {

    private final long checkIntervalNanos;
    private volatile long nextIntervalNanos;
    private volatile int checksPerInterval;
    private final AtomicInteger checksInInterval = new AtomicInteger();

    public AtomicLoopInterruptedCheck() {
        this(Duration.ONE_SECOND);
    }

    public AtomicLoopInterruptedCheck(final Duration checkInterval) {
        this.checkIntervalNanos = checkInterval.longValue(FTimeUnit.NANOSECONDS);
        this.nextIntervalNanos = getInitialNanoTime() + checkIntervalNanos;
    }

    protected long getInitialNanoTime() {
        return System.nanoTime();
    }

    public final boolean check() throws InterruptedException {
        final int checksInIntervalValue = checksInInterval.incrementAndGet();
        if (checksInIntervalValue > checksPerInterval) {
            final long newIntervalNanos = getInitialNanoTime();
            if (newIntervalNanos > nextIntervalNanos) {
                synchronized (this) {
                    if (newIntervalNanos > nextIntervalNanos) {
                        onInterval();
                        checksPerInterval = checksInIntervalValue;
                        checksInInterval.set(0);
                        nextIntervalNanos = newIntervalNanos + checkIntervalNanos;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    protected void onInterval() throws InterruptedException {
        Threads.throwIfInterrupted(Thread.currentThread());
    }

}
