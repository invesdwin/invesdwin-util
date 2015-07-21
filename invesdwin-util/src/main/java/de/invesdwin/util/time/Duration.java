package de.invesdwin.util.time;

import java.util.concurrent.TimeUnit;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.lang.Strings;
import de.invesdwin.util.time.fdate.FDate;

@ThreadSafe
public final class Duration extends Number implements Comparable<Object> {

    public static final int DAYS_IN_YEAR = 365;
    public static final int MONTHS_IN_YEAR = 12;
    public static final int DAYS_IN_WEEK = 7;
    public static final int DAYS_IN_MONTH = DAYS_IN_YEAR / MONTHS_IN_YEAR;
    public static final int WEEKS_IN_MONTH = DAYS_IN_MONTH / DAYS_IN_WEEK;
    public static final int WEEKS_IN_YEAR = DAYS_IN_YEAR / DAYS_IN_WEEK;
    public static final int HOURS_IN_DAY = 24;
    public static final int MINUTES_IN_HOUR = 60;
    public static final int SECONDS_IN_MINUTE = 60;
    public static final int MILLISECONDS_IN_SECOND = 1000;
    public static final int MICROSECONDS_IN_MILLISECOND = 1000;
    public static final int NANOSECONDS_IN_MICROSECOND = 1000;

    public static final Duration ONE_MILLISECOND = new Duration(1, TimeUnit.MILLISECONDS);
    public static final Duration ONE_SECOND = new Duration(1, TimeUnit.SECONDS);
    public static final Duration ONE_MINUTE = new Duration(1, TimeUnit.MINUTES);
    public static final Duration ONE_HOUR = new Duration(1, TimeUnit.HOURS);
    public static final Duration ONE_DAY = new Duration(1, TimeUnit.DAYS);
    public static final Duration ONE_WEEK = new Duration(DAYS_IN_WEEK, TimeUnit.DAYS);
    public static final Duration ONE_MONTH = new Duration(DAYS_IN_MONTH, TimeUnit.DAYS);
    public static final Duration ONE_YEAR = new Duration(DAYS_IN_YEAR, TimeUnit.DAYS);

    private static final long serialVersionUID = 1L;

    private final long duration;
    private final TimeUnit timeUnit;
    @GuardedBy("none for performance")
    private Integer cachedHashCode;

    public Duration(final Instant start) {
        this(start, TimeUnit.NANOSECONDS);
    }

    public Duration(final Instant start, final TimeUnit timeUnit) {
        this(start, new Instant(), timeUnit);
    }

    public Duration(final Instant start, final Instant end) {
        this(start, end, TimeUnit.NANOSECONDS);
    }

    public Duration(final Instant start, final Instant end, final TimeUnit timeUnit) {
        this(end.longValue(timeUnit) - start.longValue(timeUnit), timeUnit);
    }

    public Duration(final Duration start, final Duration end, final TimeUnit timeUnit) {
        this(end.longValue(timeUnit) - start.longValue(timeUnit), timeUnit);
    }

    public Duration(final FDate start) {
        this(start, new FDate(), TimeUnit.MILLISECONDS);
    }

    public Duration(final FDate start, final TimeUnit timeUnit) {
        this(start, new FDate(), timeUnit);
    }

    public Duration(final FDate start, final FDate end) {
        this(start, end, TimeUnit.MILLISECONDS);
    }

    public Duration(final FDate start, final FDate end, final TimeUnit timeUnit) {
        this(end.longValue(timeUnit) - start.longValue(timeUnit), timeUnit);
    }

    public Duration(final long duration, final TimeUnit timeUnit) {
        this.duration = Long.valueOf(duration);
        this.timeUnit = timeUnit;
    }

    /**
     * Creates a new duration derived from this one by multipliying with the given factor.
     */
    public Duration multiply(final double factor) {
        return new Duration((long) (longValue(TimeUnit.NANOSECONDS) * factor), TimeUnit.NANOSECONDS);
    }

    public boolean isGreaterThan(final long duration, final TimeUnit timeUnit) {
        final long comparableDuration = Math.abs(TimeUnit.NANOSECONDS.convert(duration, timeUnit));
        return Math.abs(longValue(TimeUnit.NANOSECONDS)) > comparableDuration;
    }

    public boolean isGreaterThan(final Duration duration) {
        return isGreaterThan(duration.duration, duration.timeUnit);
    }

    public boolean isLessThan(final long duration, final TimeUnit timeUnit) {
        final long comparableDuration = Math.abs(TimeUnit.NANOSECONDS.convert(duration, timeUnit));
        return Math.abs(longValue(TimeUnit.NANOSECONDS)) < comparableDuration;
    }

    public boolean isLessThan(final Duration duration) {
        return isLessThan(duration.duration, duration.timeUnit);
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void sleep() throws InterruptedException {
        timeUnit.sleep(duration);
    }

    @Override
    public int intValue() {
        return intValue(timeUnit);
    }

    public int intValue(final TimeUnit timeUnit) {
        return Long.valueOf(timeUnit.convert(duration, this.timeUnit)).intValue();
    }

    @Override
    public long longValue() {
        return longValue(timeUnit);
    }

    public long longValue(final TimeUnit timeUnit) {
        return Long.valueOf(timeUnit.convert(duration, this.timeUnit)).longValue();
    }

    @Override
    public float floatValue() {
        return floatValue(timeUnit);
    }

    public float floatValue(final TimeUnit timeUnit) {
        return Long.valueOf(timeUnit.convert(duration, this.timeUnit)).floatValue();
    }

    @Override
    public double doubleValue() {
        return doubleValue(timeUnit);
    }

    public double doubleValue(final TimeUnit timeUnit) {
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
    public String toString(final TimeUnit smallestTimeUnit) {
        //CHECKSTYLE:ON
        final long nanos = Math.abs(TimeUnit.NANOSECONDS.convert(duration, this.timeUnit));
        final long nanosAsMicros = TimeUnit.NANOSECONDS.toMicros(nanos);
        final long nanosAsMillis = TimeUnit.NANOSECONDS.toMillis(nanos);
        final long nanosAsSeconds = TimeUnit.NANOSECONDS.toSeconds(nanos);
        final long nanosAsMinutes = TimeUnit.NANOSECONDS.toMinutes(nanos);
        final long nanosAsHours = TimeUnit.NANOSECONDS.toHours(nanos);
        final long nanosAsDays = TimeUnit.NANOSECONDS.toDays(nanos);
        final long nanosAsWeeks = nanosAsDays / DAYS_IN_WEEK;
        final long nanosAsMonths = nanosAsWeeks / WEEKS_IN_MONTH;
        final long nanosAsYears = nanosAsMonths / MONTHS_IN_YEAR;

        final StringBuilder sb = new StringBuilder();
        long nanoseconds = 0;
        long mikroseconds = 0;
        long milliseconds = 0;
        long seconds = 0;
        switch (smallestTimeUnit) {
        case NANOSECONDS:
            nanoseconds = nanos - nanosAsMicros * NANOSECONDS_IN_MICROSECOND;
            if (nanoseconds > 0) {
                sb.insert(0, Strings.leftPad(nanoseconds, 3, "0"));
                sb.insert(0, ".");
            }
        case MICROSECONDS:
            mikroseconds = nanosAsMicros - nanosAsMillis * MICROSECONDS_IN_MILLISECOND;
            if (mikroseconds + nanoseconds > 0) {
                sb.insert(0, Strings.leftPad(mikroseconds, 3, "0"));
                sb.insert(0, ".");
            }
        case MILLISECONDS:
            milliseconds = nanosAsMillis - nanosAsSeconds * MILLISECONDS_IN_SECOND;
            if (milliseconds + mikroseconds + nanoseconds > 0) {
                sb.insert(0, Strings.leftPad(milliseconds, 3, "0"));
                sb.insert(0, ".");
            }
        case SECONDS:
            seconds = nanosAsSeconds - nanosAsMinutes * SECONDS_IN_MINUTE;
            if (seconds + milliseconds + mikroseconds + nanoseconds > 0) {
                sb.insert(0, seconds);
                sb.append("S");
            }
        case MINUTES:
            final long minutes = nanosAsMinutes - nanosAsHours * MINUTES_IN_HOUR;
            if (minutes > 0) {
                sb.insert(0, "M");
                sb.insert(0, minutes);
            }
        case HOURS:
            final long hours = nanosAsHours - nanosAsDays * HOURS_IN_DAY;
            if (hours > 0) {
                sb.insert(0, "H");
                sb.insert(0, hours);
            }
            if (sb.length() > 0) {
                sb.insert(0, "T");
            }
        case DAYS:
            final long days = nanosAsDays - nanosAsWeeks * DAYS_IN_WEEK;
            if (days > 0) {
                sb.insert(0, "D");
                sb.insert(0, days);
            }
            final long weeks = nanosAsWeeks - nanosAsMonths * WEEKS_IN_MONTH;
            if (weeks > 0) {
                sb.insert(0, "W");
                sb.insert(0, weeks);
            }
            final long months = nanosAsMonths - nanosAsYears * MONTHS_IN_YEAR;
            if (months > 0) {
                sb.insert(0, "M");
                sb.insert(0, months);
            }
            final long years = nanosAsYears;
            if (years > 0) {
                sb.insert(0, "Y");
                sb.insert(0, years);
            }
            break;
        default:
            throw UnknownArgumentException.newInstance(TimeUnit.class, smallestTimeUnit);
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
    public Duration add(final long duration, final TimeUnit timeUnit) {
        final long comparableDuration = Math.abs(TimeUnit.NANOSECONDS.convert(duration, timeUnit));
        return new Duration(this.longValue(TimeUnit.NANOSECONDS) + comparableDuration, TimeUnit.NANOSECONDS);
    }

    /**
     * Creates a new duration derived from this one with the added duration in nanoseconds as timeunit.
     */
    public Duration add(final Duration duration) {
        return add(duration.duration, duration.timeUnit);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof Duration) {
            final Duration zObj = (Duration) obj;
            return zObj.longValue(TimeUnit.NANOSECONDS) == this.longValue(TimeUnit.NANOSECONDS);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        if (cachedHashCode == null) {
            cachedHashCode = Objects.hashCode(getClass(), longValue(TimeUnit.NANOSECONDS));
        }
        return cachedHashCode;
    }

    public FDate subtractFrom(final FDate date) {
        return new FDate(date.millisValue() - longValue(TimeUnit.MILLISECONDS));
    }

    public FDate addTo(final FDate date) {
        return new FDate(date.millisValue() + longValue(TimeUnit.MILLISECONDS));
    }

    public Duration abs() {
        return new Duration(Math.abs(duration), timeUnit);
    }

    public boolean isExactMultipleOfPeriod(final Duration period) {
        return !isLessThan(period) && longValue(TimeUnit.NANOSECONDS) % period.longValue(TimeUnit.NANOSECONDS) == 0;
    }

    public double getNumMultipleOfPeriod(final Duration period) {
        return doubleValue(TimeUnit.NANOSECONDS) / period.doubleValue(TimeUnit.NANOSECONDS);
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

}
