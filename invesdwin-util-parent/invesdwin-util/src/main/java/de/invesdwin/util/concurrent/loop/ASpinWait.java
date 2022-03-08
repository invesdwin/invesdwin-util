package de.invesdwin.util.concurrent.loop;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.reflection.Reflections;
import de.invesdwin.util.time.Instant;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.Duration;

@Immutable
public abstract class ASpinWait {

    public static final FTimeUnit FTIMEUNIT = FTimeUnit.NANOSECONDS;
    public static final TimeUnit TIMEUNIT = FTIMEUNIT.timeUnitValue();

    private static final MethodHandle ON_SPIN_WAIT = determineOnSpinWait();

    protected final boolean spinAllowed = determineSpinAllowed();
    /**
     * The number of times to spin before blocking in timed waits. The value is empirically derived -- it works well
     * across a variety of processors and OSes. Empirically, the best value seems not to vary with number of CPUs
     * (beyond 2) so is just a constant.
     */
    protected final int maxTimedSpins = (spinAllowed) ? determineMaxTimedSpins() : 0;

    /**
     * The number of times to spin before blocking in untimed waits. This is greater than timed value because untimed
     * waits spin faster since they don't need to check times on each spin.
     */
    protected final int maxUntimedSpins = determineMaxUntimedSpins();

    /**
     * The number of nanoseconds for which it is faster to spin rather than to use timed park. A rough estimate
     * suffices.
     */
    protected final long skipSpinAfterWaitingSince = determineSkipSpinAfterWaitingSince().longValue(FTIMEUNIT);
    /**
     * How long each park interval should last before we check again if the condition is fulfilled.
     */
    protected final long maxParkIntervalNanos = determineMaxParkInterval().longValue(FTIMEUNIT);
    /**
     * How long we want to spin timed
     */
    protected final long maxTimedSpinDuration = determineMaxTimedSpinDuration().longValue(FTIMEUNIT);

    protected Duration determineSkipSpinAfterWaitingSince() {
        //when we have been waiting a long time for a request/response we should keep the CPU usage to a minimum and thus don't even try to spin
        return new Duration(10, FTimeUnit.MILLISECONDS);
    }

    protected static MethodHandle determineOnSpinWait() {
        //use onSpinWait in Java9
        try {
            final Method onSpinWait = Reflections.findMethod(Thread.class, "onSpinWait");
            if (onSpinWait != null) {
                return MethodHandles.lookup().unreflect(onSpinWait);
            } else {
                final Method noop = Reflections.findMethod(ASpinWait.class, "noop");
                return MethodHandles.lookup().unreflect(noop);
            }
        } catch (final IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unused")
    private static void noop() {
    }

    protected int determineMaxUntimedSpins() {
        return maxTimedSpins * 16;
    }

    /**
     * We need a magnitude more than SynchronousQueue since two processes are involved
     */
    protected int determineMaxTimedSpins() {
        return 32 * 1000;
    }

    protected boolean determineSpinAllowed() {
        return Runtime.getRuntime().availableProcessors() > 1;
    }

    /**
     * with 1 microsecond sleep, the performance penalty is not too large while still keeping the CPU usage at minimum
     */
    protected Duration determineMaxParkInterval() {
        return new Duration(1, FTimeUnit.MICROSECONDS);
    }

    /**
     * since we have IPC the 1000 nanoseconds from SynchronousQueue for spinning are too short to optimal performance
     */
    public Duration determineMaxTimedSpinDuration() {
        return new Duration(10, FTimeUnit.MICROSECONDS);
    }

    public abstract boolean isConditionFulfilled() throws Exception;

    protected boolean isSpinAllowed(final long waitingSinceNanos) {
        return (System.nanoTime() - waitingSinceNanos) < skipSpinAfterWaitingSince;
    }

    public boolean awaitFulfill(final Instant waitingSince) throws Exception {
        return awaitFulfill(waitingSince.longValue());
    }

    public boolean awaitFulfill(final long waitingSinceNanos) throws Exception {
        return awaitFulfill(waitingSinceNanos, Duration.ONE_YEAR);
    }

    public boolean awaitFulfill(final Instant waitingSince, final Duration maxWait) throws Exception {
        return awaitFulfill(waitingSince.longValue(), maxWait);
    }

    public boolean awaitFulfill(final long waitingSinceNanos, final Duration maxWait) throws Exception {
        if (isConditionFulfilled()) {
            return true;
        }
        final boolean spinAllowedNow = spinAllowed && isSpinAllowed(waitingSinceNanos);
        if (spinAllowedNow) {
            for (int untimedSpins = 0; untimedSpins < maxUntimedSpins; untimedSpins++) {
                if (isConditionFulfilled()) {
                    return true;
                }
                onSpinWait();
            }
        }
        long nanosRemaining = maxWait.nanosValue();
        final long waitDeadline = System.nanoTime() + nanosRemaining;
        final Thread w = Thread.currentThread();
        int timedSpins = 0;
        while (true) {
            if (isConditionFulfilled()) {
                return true;
            }
            nanosRemaining = waitDeadline - System.nanoTime();
            if (nanosRemaining <= 0L) {
                //we have exceeded maxWait
                return false;
            }
            final boolean shouldSpin = spinAllowedNow && nanosRemaining < maxTimedSpinDuration
                    && timedSpins < maxTimedSpins;
            if (shouldSpin) {
                timedSpins++;
                onSpinWait();
            } else {
                //only check interrupted when we are on the slow lane anyway
                if (w.isInterrupted()) {
                    return false;
                }
                LockSupport.parkNanos(this, maxParkIntervalNanos);
            }
        }
    }

    public void onSpinWait() {
        onSpinWaitStatic();
    }

    public static void onSpinWaitStatic() {
        try {
            ON_SPIN_WAIT.invokeExact();
        } catch (final Throwable e) {
            throw new RuntimeException(e);
        }
    }

}
