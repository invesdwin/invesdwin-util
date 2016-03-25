package de.invesdwin.util.time.duration;

import java.util.Arrays;
import java.util.List;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.lang.Strings;
import de.invesdwin.util.time.Instant;
import de.invesdwin.util.time.fdate.FDate;
import de.invesdwin.util.time.fdate.FTimeUnit;

@ThreadSafe
public class Duration extends Number implements Comparable<Object> {

    public static final Duration ZERO = new Duration(0, FTimeUnit.NANOSECONDS);
    public static final Duration ONE_NANOSECOND = new Duration(1, FTimeUnit.NANOSECONDS);
    public static final Duration ONE_MICROSECOND = new Duration(1, FTimeUnit.MICROSECONDS);
    public static final Duration ONE_MILLISECOND = new Duration(1, FTimeUnit.MILLISECONDS);
    public static final Duration ONE_SECOND = new Duration(1, FTimeUnit.SECONDS);
    public static final Duration ONE_MINUTE = new Duration(1, FTimeUnit.MINUTES);
    public static final Duration ONE_HOUR = new Duration(1, FTimeUnit.HOURS);
    public static final Duration ONE_DAY = new Duration(1, FTimeUnit.DAYS);
    public static final Duration ONE_WEEK = new Duration(1, FTimeUnit.WEEKS);
    public static final Duration ONE_MONTH = new Duration(1, FTimeUnit.MONTHS);
    public static final Duration ONE_YEAR = new Duration(1, FTimeUnit.YEARS);

    private static final long serialVersionUID = 1L;

    private final long duration;
    private final FTimeUnit timeUnit;
    @GuardedBy("none for performance")
    private Integer cachedHashCode;

    public Duration(final Instant start) {
        this(start, FTimeUnit.NANOSECONDS);
    }

    public Duration(final Instant start, final FTimeUnit timeUnit) {
        this(start, new Instant(), timeUnit);
    }

    public Duration(final Instant start, final Instant end) {
        this(start, end, FTimeUnit.NANOSECONDS);
    }

    public Duration(final Instant start, final Instant end, final FTimeUnit timeUnit) {
        this(end.longValue(timeUnit) - start.longValue(timeUnit), timeUnit);
    }

    public Duration(final Duration start, final Duration end, final FTimeUnit timeUnit) {
        this(end.longValue(timeUnit) - start.longValue(timeUnit), timeUnit);
    }

    public Duration(final FDate start) {
        this(start, new FDate(), FTimeUnit.MILLISECONDS);
    }

    public Duration(final FDate start, final FTimeUnit timeUnit) {
        this(start, new FDate(), timeUnit);
    }

    public Duration(final FDate start, final FDate end) {
        this(start, end, FTimeUnit.MILLISECONDS);
    }

    public Duration(final FDate start, final FDate end, final FTimeUnit timeUnit) {
        this(end.longValue(timeUnit) - start.longValue(timeUnit), timeUnit);
    }

    public Duration(final long duration, final FTimeUnit timeUnit) {
        this.duration = Long.valueOf(duration);
        this.timeUnit = timeUnit;
    }

    /**
     * Creates a new duration derived from this one by multipliying with the given factor.
     */
    public Duration multiply(final double factor) {
        return new Duration((long) (longValue(FTimeUnit.NANOSECONDS) * factor), FTimeUnit.NANOSECONDS);
    }

    public boolean isGreaterThan(final long duration, final FTimeUnit timeUnit) {
        final long comparableDuration = Math.abs(FTimeUnit.NANOSECONDS.convert(duration, timeUnit));
        return Math.abs(longValue(FTimeUnit.NANOSECONDS)) > comparableDuration;
    }

    public boolean isGreaterThan(final Duration duration) {
        return isGreaterThan(duration.duration, duration.timeUnit);
    }

    public boolean isGreaterThanOrEqualTo(final Duration duration) {
        return isGreaterThanOrEqualTo(duration.duration, duration.timeUnit);
    }

    public boolean isGreaterThanOrEqualTo(final long duration, final FTimeUnit timeUnit) {
        return !isLessThan(duration, timeUnit);
    }

    public boolean isLessThan(final long duration, final FTimeUnit timeUnit) {
        final long comparableDuration = Math.abs(FTimeUnit.NANOSECONDS.convert(duration, timeUnit));
        return Math.abs(longValue(FTimeUnit.NANOSECONDS)) < comparableDuration;
    }

    public boolean isLessThan(final Duration duration) {
        return isLessThan(duration.duration, duration.timeUnit);
    }

    public boolean isLessThanOrEqualTo(final long duration, final FTimeUnit timeUnit) {
        return !isGreaterThan(duration, timeUnit);
    }

    public boolean isLessThanOrEqualTo(final Duration duration) {
        return isLessThanOrEqualTo(duration.duration, duration.timeUnit);
    }

    public FTimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void sleep() throws InterruptedException {
        timeUnit.sleep(duration);
    }

    @Override
    public int intValue() {
        return intValue(timeUnit);
    }

    public int intValue(final FTimeUnit timeUnit) {
        return Long.valueOf(timeUnit.convert(duration, this.timeUnit)).intValue();
    }

    @Override
    public long longValue() {
        return longValue(timeUnit);
    }

    public long longValue(final FTimeUnit timeUnit) {
        return Long.valueOf(timeUnit.convert(duration, this.timeUnit)).longValue();
    }

    @Override
    public float floatValue() {
        return floatValue(timeUnit);
    }

    public float floatValue(final FTimeUnit timeUnit) {
        return Long.valueOf(timeUnit.convert(duration, this.timeUnit)).floatValue();
    }

    @Override
    public double doubleValue() {
        return doubleValue(timeUnit);
    }

    public double doubleValue(final FTimeUnit timeUnit) {
        return Long.valueOf(timeUnit.convert(duration, this.timeUnit)).doubleValue();
    }

    @Override
    public String toString() {
        return toString(timeUnit);
    }

    /**
     * Returns the duration the following format:
     * 
     * P[JY][MM][WW][TD][T[hH][mM][s[.f]S]]
     * 
     * The precision gets cut at the end.
     * 
     * @see <a href="http://de.wikipedia.org/wiki/ISO_8601">ISO_8601</a>
     */
    //CHECKSTYLE:OFF NPath
    public String toString(final FTimeUnit smallestFTimeUnit) {
        //CHECKSTYLE:ON
        final long nanos = Math.abs(FTimeUnit.NANOSECONDS.convert(duration, this.timeUnit));
        final long nanosAsMicros = FTimeUnit.NANOSECONDS.toMicros(nanos);
        final long nanosAsMillis = FTimeUnit.NANOSECONDS.toMillis(nanos);
        final long nanosAsSeconds = FTimeUnit.NANOSECONDS.toSeconds(nanos);
        final long nanosAsMinutes = FTimeUnit.NANOSECONDS.toMinutes(nanos);
        final long nanosAsHours = FTimeUnit.NANOSECONDS.toHours(nanos);
        final long nanosAsDays = FTimeUnit.NANOSECONDS.toDays(nanos);
        final long nanosAsWeeks = FTimeUnit.NANOSECONDS.toWeeks(nanos);
        final long nanosAsMonths = FTimeUnit.NANOSECONDS.toMonths(nanos);
        final long nanosAsYears = FTimeUnit.NANOSECONDS.toYears(nanos);

        final StringBuilder sb = new StringBuilder();
        long nanoseconds = 0;
        long mikroseconds = 0;
        long milliseconds = 0;
        long seconds = 0;
        switch (smallestFTimeUnit) {
        case NANOSECONDS:
            nanoseconds = nanos - nanosAsMicros * FTimeUnit.NANOSECONDS_IN_MICROSECOND;
            if (nanoseconds > 0) {
                sb.insert(0, Strings.leftPad(nanoseconds, 3, "0"));
                sb.insert(0, ".");
            }
        case MICROSECONDS:
            mikroseconds = nanosAsMicros - nanosAsMillis * FTimeUnit.MICROSECONDS_IN_MILLISECOND;
            if (mikroseconds + nanoseconds > 0) {
                sb.insert(0, Strings.leftPad(mikroseconds, 3, "0"));
                sb.insert(0, ".");
            }
        case MILLISECONDS:
            milliseconds = nanosAsMillis - nanosAsSeconds * FTimeUnit.MILLISECONDS_IN_SECOND;
            if (milliseconds + mikroseconds + nanoseconds > 0) {
                sb.insert(0, Strings.leftPad(milliseconds, 3, "0"));
                sb.insert(0, ".");
            }
        case SECONDS:
            seconds = nanosAsSeconds - nanosAsMinutes * FTimeUnit.SECONDS_IN_MINUTE;
            if (seconds + milliseconds + mikroseconds + nanoseconds > 0) {
                sb.insert(0, seconds);
                sb.append("S");
            }
        case MINUTES:
            final long minutes = nanosAsMinutes - nanosAsHours * FTimeUnit.MINUTES_IN_HOUR;
            if (minutes > 0) {
                sb.insert(0, "M");
                sb.insert(0, minutes);
            }
        case HOURS:
            final long hours = nanosAsHours - nanosAsDays * FTimeUnit.HOURS_IN_DAY;
            if (hours > 0) {
                sb.insert(0, "H");
                sb.insert(0, hours);
            }
            if (sb.length() > 0) {
                sb.insert(0, "T");
            }
        case DAYS:
            final long days = nanosAsDays - nanosAsWeeks * FTimeUnit.DAYS_IN_WEEK;
            if (days > 0) {
                sb.insert(0, "D");
                sb.insert(0, days);
            }
        case WEEKS:
            final long weeks = nanosAsWeeks - nanosAsMonths * FTimeUnit.WEEKS_IN_MONTH;
            if (weeks > 0) {
                sb.insert(0, "W");
                sb.insert(0, weeks);
            }
        case MONTHS:
            final long months = nanosAsMonths - nanosAsYears * FTimeUnit.MONTHS_IN_YEAR;
            if (months > 0) {
                sb.insert(0, "M");
                sb.insert(0, months);
            }
        case YEARS:
            final long years = nanosAsYears;
            if (years > 0) {
                sb.insert(0, "Y");
                sb.insert(0, years);
            }
            break;
        default:
            throw UnknownArgumentException.newInstance(FTimeUnit.class, smallestFTimeUnit);
        }
        if (sb.length() == 0) {
            sb.append("0");
        }
        sb.insert(0, "P");
        if (duration < 0 && !"P0".equals(sb.toString())) {
            sb.insert(0, "-");
        }

        return sb.toString();
    }

    /**
     * Creates a new duration derived from this one with the added duration in the same unit as previously.
     */
    public Duration add(final long duration) {
        return add(duration, timeUnit);
    }

    /**
     * Creates a new duration derived from this one with the added duration in nanoseconds as timeunit.
     */
    public Duration add(final long duration, final FTimeUnit timeUnit) {
        final long comparableDuration = Math.abs(FTimeUnit.NANOSECONDS.convert(duration, timeUnit));
        return new Duration(this.longValue(FTimeUnit.NANOSECONDS) + comparableDuration, FTimeUnit.NANOSECONDS);
    }

    public Duration subtract(final long duration, final FTimeUnit timeUnit) {
        final long comparableDuration = Math.abs(FTimeUnit.NANOSECONDS.convert(duration, timeUnit));
        return new Duration(this.longValue(FTimeUnit.NANOSECONDS) - comparableDuration, FTimeUnit.NANOSECONDS);
    }

    public Duration divide(final Number dividend) {
        final long divided = (long) (longValue(FTimeUnit.NANOSECONDS) / dividend.doubleValue());
        return new Duration(divided, FTimeUnit.NANOSECONDS);
    }

    public Duration multiply(final Number multiplicant) {
        final long multiplied = (long) (longValue(FTimeUnit.NANOSECONDS) * multiplicant.doubleValue());
        return new Duration(multiplied, FTimeUnit.NANOSECONDS);
    }

    /**
     * Creates a new duration derived from this one with the added duration in nanoseconds as timeunit.
     */
    public Duration add(final Duration duration) {
        return add(duration.duration, duration.timeUnit);
    }

    public Duration subtract(final Duration duration) {
        return subtract(duration.duration, duration.timeUnit);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof Duration) {
            final Duration zObj = (Duration) obj;
            return zObj.longValue(FTimeUnit.NANOSECONDS) == this.longValue(FTimeUnit.NANOSECONDS);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        if (cachedHashCode == null) {
            cachedHashCode = Objects.hashCode(getClass(), longValue(FTimeUnit.NANOSECONDS));
        }
        return cachedHashCode;
    }

    public FDate subtractFrom(final FDate date) {
        return new FDate(date.millisValue() - longValue(FTimeUnit.MILLISECONDS));
    }

    public FDate addTo(final FDate date) {
        return new FDate(date.millisValue() + longValue(FTimeUnit.MILLISECONDS));
    }

    public Duration abs() {
        return new Duration(Math.abs(duration), timeUnit);
    }

    public boolean isExactMultipleOfPeriod(final Duration period) {
        return !isLessThan(period) && longValue(FTimeUnit.NANOSECONDS) % period.longValue(FTimeUnit.NANOSECONDS) == 0;
    }

    public double getNumMultipleOfPeriod(final Duration period) {
        return doubleValue(FTimeUnit.NANOSECONDS) / period.doubleValue(FTimeUnit.NANOSECONDS);
    }

    @Override
    public int compareTo(final Object o) {
        if (o instanceof Duration) {
            final Duration cO = (Duration) o;
            if (isGreaterThan(cO)) {
                return 1;
            } else if (isLessThan(cO)) {
                return -1;
            } else {
                return 0;
            }
        }
        return -1;
    }

    public static IDurationAggregate valueOf(final Duration... values) {
        return valueOf(Arrays.asList(values));
    }

    public static IDurationAggregate valueOf(final List<? extends Duration> values) {
        if (values == null || values.size() == 0) {
            return DummyDurationAggregate.INSTANCE;
        } else {
            return new DurationAggregate(values);
        }
    }

}
