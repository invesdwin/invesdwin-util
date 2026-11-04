package de.invesdwin.util.time.flow;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.math.Longs;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.Duration;

/**
 * A bounded exponential ladder of waiting gaps for repeated attempts at the same operation: every attempt arms a gap
 * that has to pass before the next one is allowed, and consecutive attempts of the same incident double that gap up to
 * the configured maximum. As soon as the operation is proven good again, the caller resets the ladder and the next
 * incident starts from the minimum again.
 *
 * Without it, every caller that finds the operation failing submits a new attempt, which turns an unavailable resource
 * into a storm of attempts that keeps taking it down over and over instead of waiting for it.
 *
 * It never sleeps and never decides by itself - it only answers whether an attempt is due and remembers the one that
 * was made, all on caller-supplied epoch milliseconds, which is what keeps it testable offline.
 */
@ThreadSafe
public final class ExponentialBackoff {

    /**
     * Smallest gap between two attempts. Below it a retry would only re-run the attempt that is still in flight, which
     * is why the ladder never shrinks to a sub-second retry.
     */
    public static final Duration DEFAULT_MIN_BACKOFF = Duration.THIRTY_SECONDS;

    private final long minBackoffMillis;
    private final long maxBackoffMillis;
    @GuardedBy("this")
    private long nextBackoffMillis;
    @GuardedBy("this")
    private long nextAttemptAllowedMillis;

    /**
     * Ladder growing from {@link #DEFAULT_MIN_BACKOFF} up to the given maximum, as the live source services run it.
     */
    public ExponentialBackoff(final Duration maxBackoff) {
        this(DEFAULT_MIN_BACKOFF, maxBackoff);
    }

    public ExponentialBackoff(final Duration minBackoff, final Duration maxBackoff) {
        this.minBackoffMillis = toMillis(minBackoff);
        //a maximum below the minimum would make the ladder shrink on every escalation, so the minimum is the floor
        this.maxBackoffMillis = Longs.max(this.minBackoffMillis, toMillis(maxBackoff));
        this.nextBackoffMillis = this.minBackoffMillis;
        //the first attempt of an incident is not held back by a previous one
        this.nextAttemptAllowedMillis = Long.MIN_VALUE;
    }

    /**
     * Whether an attempt may be made at the given instant. Does not change the state, the caller has to report the
     * attempt it actually performs with {@link #recordRestart(long)}.
     */
    public synchronized boolean isDue(final long nowMillis) {
        return nowMillis >= nextAttemptAllowedMillis;
    }

    /**
     * Records the attempt made at the given instant: its gap becomes the minimum distance to the next one and the gap
     * doubles for the following attempt, never past the configured maximum.
     *
     * @return the gap this attempt was held to, for the caller to log
     */
    public synchronized Duration recordRestart(final long nowMillis) {
        final long backoffMillis = nextBackoffMillis;
        nextAttemptAllowedMillis = nowMillis + backoffMillis;
        nextBackoffMillis = Longs.min(maxBackoffMillis, backoffMillis * 2);
        return new Duration(backoffMillis, FTimeUnit.MILLISECONDS);
    }

    /**
     * Ends the incident: the next one starts with an unrestricted first attempt and grows from the minimum again.
     */
    public synchronized void reset() {
        nextBackoffMillis = minBackoffMillis;
        nextAttemptAllowedMillis = Long.MIN_VALUE;
    }

    /**
     * The gap the next attempt would be held to, which is the one that grows towards the cap.
     */
    public synchronized Duration getNextBackoff() {
        return new Duration(nextBackoffMillis, FTimeUnit.MILLISECONDS);
    }

    private static long toMillis(final Duration duration) {
        return duration.longValue(FTimeUnit.MILLISECONDS);
    }

}
