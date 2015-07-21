package de.invesdwin.util.time;

import java.util.concurrent.TimeUnit;

import javax.annotation.concurrent.Immutable;

/**
 * This class represents an instant relative to the application start in nanoseconds.
 * 
 * @author subes
 * 
 */
@Immutable
public class Instant extends Number implements Comparable<Object> {

    public static final TimeUnit DEFAULT_TIMEUNIT = TimeUnit.NANOSECONDS;
    public static final Instant DUMMY = new Instant(0, DEFAULT_TIMEUNIT);

    private static final long serialVersionUID = 1L;

    private final long startNanos;

    public Instant() {
        this.startNanos = System.nanoTime();
    }

    public Instant(final long start, final TimeUnit timeUnit) {
        this.startNanos = DEFAULT_TIMEUNIT.convert(start, timeUnit);
    }

    @Override
    public String toString() {
        return toString(DEFAULT_TIMEUNIT);
    }

    public String toString(final TimeUnit timeUnit) {
        return new Duration(this, new Instant(), timeUnit).toString();
    }

    public Duration toDuration() {
        return new Duration(this, new Instant());
    }

    /**
     * Sleeps relative to this instant. Thus may not sleep at all if the time has already passed.
     */
    public void sleepRelative(final long amount, final TimeUnit timeUnit) throws InterruptedException {
        final long alreadyPassedNanos = new Duration(this, new Instant()).longValue();
        final long durationNanos = DEFAULT_TIMEUNIT.convert(amount, timeUnit);
        DEFAULT_TIMEUNIT.sleep(durationNanos - alreadyPassedNanos);
    }

    public void sleepRelativeTo(final Duration duration) throws InterruptedException {
        sleepRelative(duration.longValue(), duration.getTimeUnit());
    }

    @Override
    public int intValue() {
        return intValue(DEFAULT_TIMEUNIT);
    }

    public int intValue(final TimeUnit timeUnit) {
        return Long.valueOf(timeUnit.convert(startNanos, DEFAULT_TIMEUNIT)).intValue();
    }

    @Override
    public long longValue() {
        return longValue(DEFAULT_TIMEUNIT);
    }

    public long longValue(final TimeUnit timeUnit) {
        return Long.valueOf(timeUnit.convert(startNanos, DEFAULT_TIMEUNIT)).longValue();
    }

    @Override
    public float floatValue() {
        return floatValue(DEFAULT_TIMEUNIT);
    }

    public float floatValue(final TimeUnit timeUnit) {
        return Long.valueOf(timeUnit.convert(startNanos, DEFAULT_TIMEUNIT)).floatValue();
    }

    @Override
    public double doubleValue() {
        return doubleValue(DEFAULT_TIMEUNIT);
    }

    public double doubleValue(final TimeUnit timeUnit) {
        return Long.valueOf(timeUnit.convert(startNanos, DEFAULT_TIMEUNIT)).doubleValue();
    }

    public boolean before(final Instant time) {
        return longValue(TimeUnit.NANOSECONDS) < time.longValue(TimeUnit.NANOSECONDS);
    }

    public boolean after(final Instant zeitpunkt) {
        return longValue(TimeUnit.NANOSECONDS) > zeitpunkt.longValue(TimeUnit.NANOSECONDS);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null || !(obj instanceof Instant)) {
            return false;
        } else {
            final Instant o1 = this;
            final Instant o2 = (Instant) obj;
            return ((Long) o1.startNanos).equals(o2.startNanos);
        }
    }

    @Override
    public int hashCode() {
        return ((Long) startNanos).hashCode();
    }

    @Override
    public int compareTo(final Object o) {
        if (o == null || !(o instanceof Instant)) {
            return 1;
        } else {
            final Instant o1 = this;
            final Instant o2 = (Instant) o;
            return ((Long) o1.startNanos).compareTo(o2.startNanos);
        }
    }

    public static Instant min(final Instant first, final Instant second) {
        if (first == null) {
            return second;
        } else if (second == null) {
            return first;
        } else if (first.before(second)) {
            return first;
        } else {
            return second;
        }
    }

}