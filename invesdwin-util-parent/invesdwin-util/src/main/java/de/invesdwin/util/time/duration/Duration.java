package de.invesdwin.util.time.duration;

import java.util.List;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.lang.comparator.IComparator;
import de.invesdwin.util.lang.string.Strings;
import de.invesdwin.util.math.Characters;
import de.invesdwin.util.math.Floats;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.Longs;
import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.math.random.PseudoRandomGenerators;
import de.invesdwin.util.time.Instant;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.internal.DurationParser;
import jakarta.persistence.Transient;

@ThreadSafe
public class Duration extends Number implements Comparable<Object> {

    public static final IComparator<Duration> COMPARATOR = IComparator.getDefaultInstance();

    public static final Duration ZERO = new Duration(0, FTimeUnit.NANOSECONDS);
    public static final Duration ONE_NANOSECOND = new Duration(1, FTimeUnit.NANOSECONDS);
    public static final Duration ONE_MICROSECOND = new Duration(1, FTimeUnit.MICROSECONDS);
    public static final Duration ONE_MILLISECOND = new Duration(1, FTimeUnit.MILLISECONDS);
    public static final Duration FIFTY_MILLISECONDS = new Duration(50, FTimeUnit.MILLISECONDS);
    public static final Duration ONE_HUNDRED_MILLISECONDS = new Duration(100, FTimeUnit.MILLISECONDS);
    public static final Duration ONE_SECOND = new Duration(1, FTimeUnit.SECONDS);
    public static final Duration TWO_SECONDS = new Duration(2, FTimeUnit.SECONDS);
    public static final Duration THREE_SECONDS = new Duration(3, FTimeUnit.SECONDS);
    public static final Duration FIVE_SECONDS = new Duration(5, FTimeUnit.SECONDS);
    public static final Duration TEN_SECONDS = new Duration(10, FTimeUnit.SECONDS);
    public static final Duration FIFTEEN_SECONDS = new Duration(15, FTimeUnit.SECONDS);
    public static final Duration THIRTY_SECONDS = new Duration(30, FTimeUnit.SECONDS);
    public static final Duration ONE_MINUTE = new Duration(1, FTimeUnit.MINUTES);
    public static final Duration THREE_MINUTES = new Duration(3, FTimeUnit.MINUTES);
    public static final Duration FIVE_MINUTES = new Duration(5, FTimeUnit.MINUTES);
    public static final Duration TEN_MINUTES = new Duration(10, FTimeUnit.MINUTES);
    public static final Duration FIFTEEN_MINUTES = new Duration(15, FTimeUnit.MINUTES);
    public static final Duration THIRTY_MINUTES = new Duration(30, FTimeUnit.MINUTES);
    public static final Duration ONE_HOUR = new Duration(1, FTimeUnit.HOURS);
    public static final Duration ONE_DAY = new Duration(1, FTimeUnit.DAYS);
    public static final Duration ONE_WEEK = new Duration(1, FTimeUnit.WEEKS);
    public static final Duration TWO_WEEKS = new Duration(2, FTimeUnit.WEEKS);
    public static final Duration ONE_MONTH = new Duration(1, FTimeUnit.MONTHS);
    public static final Duration ONE_YEAR = new Duration(1, FTimeUnit.YEARS);

    private static final long serialVersionUID = 1L;

    private final long duration;
    private final FTimeUnit timeUnit;
    @GuardedBy("none for performance")
    @JsonIgnore
    @Transient
    private transient Long nanos;

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

    public Duration(final long start, final long end, final FTimeUnit timeUnit) {
        this(end - start, timeUnit);
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
        this.duration = duration;
        if (timeUnit == null) {
            throw new NullPointerException("timeUnit should not be null");
        }
        this.timeUnit = timeUnit;
    }

    /**
     * Creates a new duration derived from this one by multipliying with the given factor.
     */
    public Duration multiply(final double factor) {
        return new Duration((long) (nanosValue() * factor), FTimeUnit.NANOSECONDS);
    }

    public boolean isGreaterThan(final Duration duration) {
        return isGreaterThan(duration.duration, duration.timeUnit);
    }

    public boolean isGreaterThan(final FDate date) {
        return isGreaterThan(date.toDurationMillis(), FTimeUnit.MILLISECONDS);
    }

    public boolean isGreaterThan(final FDate from, final FDate to) {
        return isGreaterThan(to.millisValue() - from.millisValue(), FTimeUnit.MILLISECONDS);
    }

    public boolean isGreaterThan(final Instant instant) {
        return isGreaterThanNanos(instant.toDurationNanos());
    }

    public boolean isGreaterThan(final long duration, final FTimeUnit timeUnit) {
        final long durationNanos = FTimeUnit.NANOSECONDS.convert(duration, timeUnit);
        return isGreaterThanNanos(durationNanos);
    }

    public boolean isGreaterThanNanos(final long durationNanos) {
        return nanosValue() > durationNanos;
    }

    public boolean isGreaterThanOrEqualTo(final Duration duration) {
        return isGreaterThanOrEqualTo(duration.duration, duration.timeUnit);
    }

    public boolean isGreaterThanOrEqualTo(final FDate date) {
        return isGreaterThanOrEqualTo(date.toDurationMillis(), FTimeUnit.MILLISECONDS);
    }

    public boolean isGreaterThanOrEqualTo(final FDate from, final FDate to) {
        return isGreaterThanOrEqualTo(to.millisValue() - from.millisValue(), FTimeUnit.MILLISECONDS);
    }

    public boolean isGreaterThanOrEqualTo(final Instant instant) {
        return isGreaterThanOrEqualToNanos(instant.toDurationNanos());
    }

    public boolean isGreaterThanOrEqualTo(final long duration, final FTimeUnit timeUnit) {
        final long durationNanos = FTimeUnit.NANOSECONDS.convert(duration, timeUnit);
        return isGreaterThanOrEqualToNanos(durationNanos);
    }

    public boolean isGreaterThanOrEqualToNanos(final long durationNanos) {
        return nanosValue() >= durationNanos;
    }

    public boolean isLessThan(final Duration duration) {
        return isLessThan(duration.duration, duration.timeUnit);
    }

    public boolean isLessThan(final FDate date) {
        return isLessThan(date.toDurationMillis(), FTimeUnit.MILLISECONDS);
    }

    public boolean isLessThan(final FDate from, final FDate to) {
        return isLessThan(to.millisValue() - from.millisValue(), FTimeUnit.MILLISECONDS);
    }

    public boolean isLessThan(final Instant instant) {
        return isLessThanNanos(instant.toDurationNanos());
    }

    public boolean isLessThan(final long duration, final FTimeUnit timeUnit) {
        final long durationNanos = FTimeUnit.NANOSECONDS.convert(duration, timeUnit);
        return isLessThanNanos(durationNanos);
    }

    public boolean isLessThanNanos(final long durationNanos) {
        return nanosValue() < durationNanos;
    }

    public boolean isLessThanOrEqualTo(final Duration duration) {
        return isLessThanOrEqualTo(duration.duration, duration.timeUnit);
    }

    public boolean isLessThanOrEqualTo(final FDate date) {
        return isLessThanOrEqualTo(date.toDurationMillis(), FTimeUnit.MILLISECONDS);
    }

    public boolean isLessThanOrEqualTo(final FDate from, final FDate to) {
        return isLessThanOrEqualTo(to.millisValue() - from.millisValue(), FTimeUnit.MILLISECONDS);
    }

    public boolean isLessThanOrEqualTo(final Instant instant) {
        return isLessThanOrEqualToNanos(instant.toDurationNanos());
    }

    public boolean isLessThanOrEqualTo(final long duration, final FTimeUnit timeUnit) {
        final long durationNanos = FTimeUnit.NANOSECONDS.convert(duration, timeUnit);
        return isLessThanOrEqualToNanos(durationNanos);
    }

    public boolean isLessThanOrEqualToNanos(final long durationNanos) {
        return nanosValue() <= durationNanos;
    }

    public FTimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void sleep() throws InterruptedException {
        timeUnit.sleep(duration);
    }

    public void sleepRandom() throws InterruptedException {
        final long randomDuration = PseudoRandomGenerators.getThreadLocalPseudoRandom().nextLong(nanosValue());
        FTimeUnit.NANOSECONDS.sleep(randomDuration);
    }

    @Override
    public int intValue() {
        return intValue(timeUnit);
    }

    public int intValue(final FTimeUnit timeUnit) {
        return Integers.checkedCast(longValue(timeUnit));
    }

    public long nanosValue() {
        if (nanos == null) {
            nanos = FTimeUnit.NANOSECONDS.convert(duration, timeUnit);
        }
        return nanos;
    }

    @Override
    public long longValue() {
        return longValue(timeUnit);
    }

    public long longValue(final FTimeUnit timeUnit) {
        if (timeUnit == FTimeUnit.NANOSECONDS) {
            return nanosValue();
        } else {
            return timeUnit.convert(duration, this.timeUnit);
        }
    }

    @Override
    public float floatValue() {
        return floatValue(timeUnit);
    }

    public float floatValue(final FTimeUnit timeUnit) {
        return Floats.checkedCast(doubleValue(timeUnit));
    }

    @Override
    public double doubleValue() {
        return doubleValue(timeUnit);
    }

    public double doubleValue(final FTimeUnit timeUnit) {
        if (timeUnit == FTimeUnit.NANOSECONDS) {
            return nanosValue();
        } else {
            return timeUnit.asFractional().convert(duration, this.timeUnit.asFractional());
        }
    }

    public Decimal decimalValue() {
        return decimalValue(timeUnit);
    }

    public Decimal decimalValue(final FTimeUnit timeUnit) {
        return new Decimal(longValue(timeUnit));
    }

    @Override
    public String toString() {
        return toString(timeUnit);
    }

    /**
     * Returns the duration in the following format:
     * 
     * P[JY][MM][WW][TD][T[hH][mM][s[.f]S]]
     * 
     * The precision gets cut at the end.
     * 
     * @see <a href="http://de.wikipedia.org/wiki/ISO_8601">ISO_8601</a>
     */
    //CHECKSTYLE:OFF NPath
    public String toString(final FTimeUnit smallestTimeUnit) {
        //CHECKSTYLE:ON
        long nanoseconds = Longs.abs(nanosValue());
        final long years = FTimeUnit.NANOSECONDS.toYears(nanoseconds);
        nanoseconds -= FTimeUnit.YEARS.toNanos(years);
        final long months = FTimeUnit.NANOSECONDS.toMonths(nanoseconds);
        nanoseconds -= FTimeUnit.MONTHS.toNanos(months);
        final long weeks = FTimeUnit.NANOSECONDS.toWeeks(nanoseconds);
        nanoseconds -= FTimeUnit.WEEKS.toNanos(weeks);
        final long days = FTimeUnit.NANOSECONDS.toDays(nanoseconds);
        nanoseconds -= FTimeUnit.DAYS.toNanos(days);
        final long hours = FTimeUnit.NANOSECONDS.toHours(nanoseconds);
        nanoseconds -= FTimeUnit.HOURS.toNanos(hours);
        final long minutes = FTimeUnit.NANOSECONDS.toMinutes(nanoseconds);
        nanoseconds -= FTimeUnit.MINUTES.toNanos(minutes);
        final long seconds = FTimeUnit.NANOSECONDS.toSeconds(nanoseconds);
        nanoseconds -= FTimeUnit.SECONDS.toNanos(seconds);
        final long milliseconds = FTimeUnit.NANOSECONDS.toMillis(nanoseconds);
        nanoseconds -= FTimeUnit.MILLISECONDS.toNanos(milliseconds);
        final long microseconds = FTimeUnit.NANOSECONDS.toMicros(nanoseconds);
        nanoseconds -= FTimeUnit.MICROSECONDS.toNanos(microseconds);

        final StringBuilder sb = new StringBuilder();
        switch (smallestTimeUnit) {
        case NANOSECONDS:
            if (nanoseconds > 0) {
                sb.insert(0, Strings.leftPad(nanoseconds, 3, "0"));
                sb.insert(0, ".");
            }
            // fall through
        case MICROSECONDS:
            if (microseconds + nanoseconds > 0) {
                sb.insert(0, Strings.leftPad(microseconds, 3, "0"));
                sb.insert(0, ".");
            }
            // fall through
        case MILLISECONDS:
            if (milliseconds + microseconds + nanoseconds > 0) {
                sb.insert(0, Strings.leftPad(milliseconds, 3, "0"));
                sb.insert(0, ".");
            }
            // fall through
        case SECONDS:
            if (seconds + milliseconds + microseconds + nanoseconds > 0) {
                sb.insert(0, seconds);
                sb.append("S");
            }
            // fall through
        case MINUTES:
            if (minutes > 0) {
                sb.insert(0, "M");
                sb.insert(0, minutes);
            }
            // fall through
        case HOURS:
            if (hours > 0) {
                sb.insert(0, "H");
                sb.insert(0, hours);
            }
            if (sb.length() > 0) {
                sb.insert(0, "T");
            }
            // fall through
        case DAYS:
            if (days > 0) {
                sb.insert(0, "D");
                sb.insert(0, days);
            }
            // fall through
        case WEEKS:
            if (weeks > 0) {
                sb.insert(0, "W");
                sb.insert(0, weeks);
            }
            // fall through
        case MONTHS:
            if (months > 0) {
                sb.insert(0, "M");
                sb.insert(0, months);
            }
            // fall through
        case YEARS:
            if (years > 0) {
                sb.insert(0, "Y");
                sb.insert(0, years);
            }
            break;
        default:
            throw UnknownArgumentException.newInstance(FTimeUnit.class, smallestTimeUnit);
        }
        if (sb.length() == 0) {
            sb.append("0");
        }
        sb.insert(0, "P");
        if (duration < 0 && !isP0(sb)) {
            sb.insert(0, "-");
        }

        return sb.toString();
    }

    private boolean isP0(final StringBuilder sb) {
        return sb.length() == 2 && sb.charAt(0) == 'P' && sb.charAt(1) == '0';
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
        final long comparableDuration = FTimeUnit.NANOSECONDS.convert(duration, timeUnit);
        return new Duration(Math.addExact(nanosValue(), comparableDuration), FTimeUnit.NANOSECONDS);
    }

    public Duration subtract(final long duration, final FTimeUnit timeUnit) {
        final long comparableDuration = FTimeUnit.NANOSECONDS.convert(duration, timeUnit);
        return new Duration(Math.subtractExact(nanosValue(), comparableDuration), FTimeUnit.NANOSECONDS);
    }

    public Duration divide(final Number dividend) {
        final long divided = (long) (nanosValue() / dividend.doubleValue());
        return new Duration(divided, FTimeUnit.NANOSECONDS);
    }

    public Duration multiply(final Number multiplicant) {
        final long multiplied = (long) (nanosValue() * multiplicant.doubleValue());
        return new Duration(multiplied, FTimeUnit.NANOSECONDS);
    }

    /**
     * Creates a new duration derived from this one with the added duration in nanoseconds as timeunit.
     */
    public Duration add(final Duration duration) {
        if (duration == null) {
            return this;
        } else {
            return add(duration.duration, duration.timeUnit);
        }
    }

    public Duration subtract(final Duration duration) {
        if (duration == null) {
            return this;
        } else {
            return subtract(duration.duration, duration.timeUnit);
        }
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof Duration) {
            final Duration zObj = (Duration) obj;
            return equalsNotNullSafe(zObj);
        } else {
            return false;
        }
    }

    public boolean equals(final Duration obj) {
        return obj != null && equalsNotNullSafe(obj);
    }

    public boolean equalsNotNullSafe(final Duration obj) {
        return nanosValue() == obj.nanosValue();
    }

    @Override
    public int hashCode() {
        return Long.hashCode(nanosValue());
    }

    public FDate subtractFrom(final FDate date) {
        return new FDate(date.millisValue() - longValue(FTimeUnit.MILLISECONDS));
    }

    public FDate addTo(final FDate date) {
        return new FDate(date.millisValue() + longValue(FTimeUnit.MILLISECONDS));
    }

    public long subtractFrom(final long millis) {
        return millis - longValue(FTimeUnit.MILLISECONDS);
    }

    public long addTo(final long millis) {
        return millis + longValue(FTimeUnit.MILLISECONDS);
    }

    public Duration abs() {
        return new Duration(Longs.abs(duration), timeUnit);
    }

    public boolean isExactMultipleOfPeriod(final Duration period) {
        return !isLessThan(period) && nanosValue() % period.nanosValue() == 0;
    }

    public double getNumMultipleOfPeriod(final Duration period) {
        return doubleValue(FTimeUnit.NANOSECONDS) / period.doubleValue(FTimeUnit.NANOSECONDS);
    }

    @Override
    public int compareTo(final Object o) {
        if (o == null || !(o instanceof Duration)) {
            return 1;
        }
        final Duration cO = (Duration) o;
        return Long.compare(nanosValue(), cO.nanosValue());
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

    public Duration orHigher(final Duration other) {
        if (other == null) {
            return this;
        }

        if (compareTo(other) > 0) {
            return this;
        } else {
            return other;
        }
    }

    public Duration orLower(final Duration other) {
        if (other == null) {
            return this;
        }

        if (compareTo(other) < 0) {
            return this;
        } else {
            return other;
        }
    }

    public static Duration sum(final Duration value1, final Duration value2) {
        if (value1 == null) {
            return value2;
        } else {
            return value1.add(value2);
        }
    }

    public static Duration max(final Duration value1, final Duration value2) {
        if (value1 == null) {
            return value2;
        } else {
            return value1.orHigher(value2);
        }
    }

    public static Duration min(final Duration value1, final Duration value2) {
        if (value1 == null) {
            return value2;
        } else {
            return value1.orLower(value2);
        }
    }

    public boolean isZero() {
        return duration == 0;
    }

    public final boolean isNotZero() {
        return !isZero();
    }

    /**
     * 0 is counted as positive as well here to make things simpler.
     */
    public boolean isPositive() {
        return duration >= 0;
    }

    /**
     * This one excludes 0 from positive.
     */
    public boolean isPositiveNonZero() {
        return isPositive() && !isZero();
    }

    public boolean isNegative() {
        return !isPositive();
    }

    public boolean isNegativeOrZero() {
        return !isPositiveNonZero();
    }

    public Duration negate() {
        return new Duration(-duration, timeUnit);
    }

    public static Duration zeroToNull(final Duration duration) {
        if (duration == null) {
            return null;
        } else if (duration.isZero()) {
            return null;
        } else {
            return duration;
        }
    }

    public static Duration nullToZero(final Duration duration) {
        if (duration == null) {
            return Duration.ZERO;
        } else {
            return duration;
        }
    }

    public java.time.Duration javaTimeValue() {
        return java.time.Duration.of(duration, timeUnit.javaTimeValue());
    }

    public org.joda.time.Duration jodaTimeValue() {
        return org.joda.time.Duration.millis(longValue(FTimeUnit.MILLISECONDS));
    }

    public static Duration valueOf(final java.time.Duration duration) {
        return new Duration(duration.toNanos(), FTimeUnit.NANOSECONDS);
    }

    public static Duration valueOf(final org.joda.time.Duration duration) {
        return new Duration(duration.getMillis(), FTimeUnit.MILLISECONDS);
    }

    public static java.time.Duration toJavaTimeValue(final Duration duration) {
        if (duration == null) {
            return null;
        } else {
            return duration.javaTimeValue();
        }
    }

    public static org.joda.time.Duration toJodaTimeValue(final Duration duration) {
        if (duration == null) {
            return null;
        } else {
            return duration.jodaTimeValue();
        }
    }

    public static String toStringValue(final Duration duration) {
        if (duration == null) {
            return null;
        } else {
            return duration.stringValue();
        }
    }

    public static Duration valueOf(final String value) {
        final String normalizedValue = DurationParser.normalizeValue(value);
        if (Strings.contains(normalizedValue, " ")) {
            try {
                final String[] values = normalizedValue.split(" ");
                final int duration = Integer.parseInt(values[0]);
                final FTimeUnit unit = FTimeUnit.valueOfAlias(values[1]);
                if (unit == null) {
                    return null;
                }
                return new Duration(duration, unit);
            } catch (final Throwable e) {
                return null;
            }
        } else {
            if (Characters.isAsciiNumeric(normalizedValue.charAt(0))
                    && Characters.isAsciiAlpha(normalizedValue.charAt(normalizedValue.length() - 1))) {
                for (final FTimeUnit unit : FTimeUnit.values()) {
                    String number = null;
                    if (Strings.endsWith(normalizedValue, unit.name())) {
                        number = Strings.removeEnd(normalizedValue, unit.name());
                    }
                    if (number == null) {
                        for (final String alias : unit.getAliases()) {
                            if (Strings.endsWith(normalizedValue, alias)) {
                                number = Strings.removeEnd(normalizedValue, alias);
                                break;
                            }
                        }
                    }
                    if (number != null) {
                        try {
                            final int duration = Integer.parseInt(number);
                            return new Duration(duration, unit);
                        } catch (final Throwable e) {
                            return null;
                        }
                    }
                }
            }
            return new DurationParser(normalizedValue).parse();
        }

    }

    public String stringValue() {
        return duration + " " + timeUnit;
    }

    public void sleepNoInterrupt() {
        try {
            sleep();
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
