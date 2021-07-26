package de.invesdwin.util.concurrent.loop;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.Duration;

@NotThreadSafe
public abstract class ALoopConditionCheck {

    protected final Thread currentThread;
    private final long checkIntervalNanos;
    private long nextIntervalNanos;
    private int checksPerInterval;
    private int checksInInterval;
    private boolean condition = initialValue();

    public ALoopConditionCheck() {
        this(Duration.ONE_SECOND);
    }

    public ALoopConditionCheck(final Duration checkInterval) {
        this.currentThread = Thread.currentThread();
        this.checkIntervalNanos = checkInterval.longValue(FTimeUnit.NANOSECONDS);
        this.nextIntervalNanos = getInitialNanoTime() + checkIntervalNanos;
    }

    protected long getInitialNanoTime() {
        return System.nanoTime();
    }

    public final boolean check() {
        checksInInterval++;
        if (checksInInterval > checksPerInterval) {
            final long newIntervalNanos = getInitialNanoTime();
            if (newIntervalNanos > nextIntervalNanos) {
                condition = checkCondition();
                checksPerInterval = checksInInterval;
                checksInInterval = 0;
                nextIntervalNanos = newIntervalNanos + checkIntervalNanos;
            }
        }
        return condition;
    }

    protected boolean initialValue() {
        return false;
    }

    protected abstract boolean checkCondition();

}
