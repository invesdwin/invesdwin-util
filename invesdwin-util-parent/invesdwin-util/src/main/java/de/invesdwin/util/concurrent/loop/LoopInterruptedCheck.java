package de.invesdwin.util.concurrent.loop;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.concurrent.Threads;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.Duration;

@NotThreadSafe
public class LoopInterruptedCheck {

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

    public Duration getCheckInterval() {
        return checkInterval;
    }

    protected long getInitialNanoTime() {
        return System.nanoTime();
    }

    public final boolean checkNoInterrupt() {
        try {
            return check();
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public final boolean check() throws InterruptedException {
        checksInInterval++;
        if (checksInInterval > checksPerInterval) {
            final long newIntervalNanos = System.nanoTime();
            if (newIntervalNanos > nextIntervalNanos) {
                final boolean result = onInterval();
                checksPerInterval = checksInInterval;
                checksInInterval = 0;
                nextIntervalNanos = newIntervalNanos + checkIntervalNanos;
                return result;
            }
        }
        return false;
    }

    protected boolean onInterval() throws InterruptedException {
        Threads.throwIfInterrupted(currentThread);
        return true;
    }

}
