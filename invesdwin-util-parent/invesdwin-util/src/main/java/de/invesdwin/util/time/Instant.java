package de.invesdwin.util.time;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.time.duration.Duration;
import de.invesdwin.util.time.fdate.FTimeUnit;

/**
 * This class represents an instant relative to the application start in nanoseconds.
 * 
 * @author subes
 * 
 */
@Immutable
public class Instant extends Number implements Comparable<Object> {

    public static final FTimeUnit DEFAULT_TIME_UNIT = FTimeUnit.NANOSECONDS;
    public static final Instant DUMMY = new Instant(-100, FTimeUnit.YEARS);

    private static final long serialVersionUID = 1L;

    private final long nanos;

    public Instant() {
        this.nanos = System.nanoTime();
    }

    public Instant(final long start, final FTimeUnit timeUnit) {
        this.nanos = DEFAULT_TIME_UNIT.convert(start, timeUnit);
    }

    @Override
    public String toString() {
        return toString(DEFAULT_TIME_UNIT);
    }

    public String toString(final FTimeUnit timeUnit) {
        return toDuration().toString(timeUnit);
    }

    public Duration toDuration() {
        return new Duration(toDurationNanos(), FTimeUnit.NANOSECONDS);
    }

    public long toDurationNanos() {
        return System.nanoTime() - nanos;
    }

    /**
     * Sleeps relative to this instant. Thus may not sleep at all if the time has already passed.
     */
    public void sleepRelative(final long amount, final FTimeUnit timeUnit) throws InterruptedException {
        final long alreadyPassedNanos = new Duration(this, new Instant()).longValue();
        final long durationNanos = DEFAULT_TIME_UNIT.convert(amount, timeUnit);
        final long remainingNanos = durationNanos - alreadyPassedNanos;
        if (remainingNanos > 0) {
            DEFAULT_TIME_UNIT.sleep(remainingNanos);
        }
    }

    public void sleepRelative(final Duration duration) throws InterruptedException {
        sleepRelative(duration.longValue(), duration.getTimeUnit());
    }

    public boolean shouldSleepRelative(final Duration duration) {
        return shouldSleepRelative(duration.longValue(), duration.getTimeUnit());
    }

    public boolean shouldSleepRelative(final long amount, final FTimeUnit timeUnit) {
        final long alreadyPassedNanos = new Duration(this, new Instant()).longValue();
        final long durationNanos = DEFAULT_TIME_UNIT.convert(amount, timeUnit);
        final long remainingNanos = durationNanos - alreadyPassedNanos;
        return remainingNanos > 0;
    }

    @Override
    public int intValue() {
        return intValue(DEFAULT_TIME_UNIT);
    }

    public int intValue(final FTimeUnit timeUnit) {
        return Long.valueOf(timeUnit.convert(nanos, DEFAULT_TIME_UNIT)).intValue();
    }

    @Override
    public long longValue() {
        return longValue(DEFAULT_TIME_UNIT);
    }

    public long longValue(final FTimeUnit timeUnit) {
        return Long.valueOf(timeUnit.convert(nanos, DEFAULT_TIME_UNIT)).longValue();
    }

    /**
     * Deprecated: Are you sure? Maybe you are looking for toDurationNanos() instead?
     */
    @Deprecated
    public long nanosValue() {
        return nanos;
    }

    @Override
    public float floatValue() {
        return floatValue(DEFAULT_TIME_UNIT);
    }

    public float floatValue(final FTimeUnit timeUnit) {
        return Long.valueOf(timeUnit.convert(nanos, DEFAULT_TIME_UNIT)).floatValue();
    }

    @Override
    public double doubleValue() {
        return doubleValue(DEFAULT_TIME_UNIT);
    }

    public double doubleValue(final FTimeUnit timeUnit) {
        return Long.valueOf(timeUnit.convert(nanos, DEFAULT_TIME_UNIT)).doubleValue();
    }

    public boolean isBefore(final Instant instant) {
        return longValue(FTimeUnit.NANOSECONDS) < instant.longValue(FTimeUnit.NANOSECONDS);
    }

    public boolean isBeforeOrEqualTo(final Instant instant) {
        return longValue(FTimeUnit.NANOSECONDS) <= instant.longValue(FTimeUnit.NANOSECONDS);
    }

    public boolean isAfter(final Instant instant) {
        return longValue(FTimeUnit.NANOSECONDS) > instant.longValue(FTimeUnit.NANOSECONDS);
    }

    public boolean isAfterOrEqualTo(final Instant instant) {
        return longValue(FTimeUnit.NANOSECONDS) >= instant.longValue(FTimeUnit.NANOSECONDS);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null || !(obj instanceof Instant)) {
            return false;
        } else {
            final Instant cObj = (Instant) obj;
            return nanos == cObj.nanos;
        }
    }

    @Override
    public int hashCode() {
        return Long.hashCode(nanos);
    }

    @Override
    public int compareTo(final Object o) {
        if (o == null || !(o instanceof Instant)) {
            return 1;
        } else {
            final Instant cO = (Instant) o;
            return Long.compare(nanos, cO.nanos);
        }
    }

    public static Instant min(final Instant first, final Instant second) {
        if (first == null) {
            return second;
        } else if (second == null) {
            return first;
        } else if (first.isBefore(second)) {
            return first;
        } else {
            return second;
        }
    }

    public boolean isGreaterThan(final Duration duration) {
        return duration.isLessThanOrEqualToNanos(toDurationNanos());
    }

    public boolean isGreaterThanOrEqualTo(final Duration duration) {
        return duration.isLessThanNanos(toDurationNanos());
    }

    public boolean isLessThan(final Duration duration) {
        return duration.isGreaterThanOrEqualToNanos(toDurationNanos());
    }

    public boolean isLessThanOrEqualTo(final Duration duration) {
        return duration.isGreaterThanNanos(toDurationNanos());
    }

}