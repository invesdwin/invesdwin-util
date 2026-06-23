package de.invesdwin.util.time.duration;

import java.math.BigInteger;
import java.util.List;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.lang.comparator.IComparator;
import de.invesdwin.util.lang.string.Strings;
import de.invesdwin.util.math.Characters;
import de.invesdwin.util.math.Floats;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.Longs;
import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.math.random.IRandomGenerator;
import de.invesdwin.util.math.random.PseudoRandomGenerators;
import de.invesdwin.util.time.Instant;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.date.FTimeUnitFractional;
import de.invesdwin.util.time.date.millis.FDateMillis;
import de.invesdwin.util.time.date.millis.FDatePicos;
import de.invesdwin.util.time.duration.internal.DurationParser;

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
    public static final Duration TWO_MINUTES = new Duration(2, FTimeUnit.MINUTES);
    public static final Duration THREE_MINUTES = new Duration(3, FTimeUnit.MINUTES);
    public static final Duration FIVE_MINUTES = new Duration(5, FTimeUnit.MINUTES);
    public static final Duration TEN_MINUTES = new Duration(10, FTimeUnit.MINUTES);
    public static final Duration FIFTEEN_MINUTES = new Duration(15, FTimeUnit.MINUTES);
    public static final Duration THIRTY_MINUTES = new Duration(30, FTimeUnit.MINUTES);
    public static final Duration ONE_HOUR = new Duration(1, FTimeUnit.HOURS);
    public static final Duration ONE_DAY = new Duration(1, FTimeUnit.DAYS);
    public static final Duration TWO_DAYS = new Duration(2, FTimeUnit.DAYS);
    public static final Duration FOUR_DAYS = new Duration(4, FTimeUnit.DAYS);
    public static final Duration ONE_WEEK = new Duration(1, FTimeUnit.WEEKS);
    public static final Duration TWO_WEEKS = new Duration(2, FTimeUnit.WEEKS);
    public static final Duration ONE_MONTH = new Duration(1, FTimeUnit.MONTHS);
    public static final Duration TWO_MONTHS = new Duration(2, FTimeUnit.MONTHS);
    public static final Duration THREE_MONTHS = new Duration(3, FTimeUnit.MONTHS);
    public static final Duration ONE_YEAR = new Duration(1, FTimeUnit.YEARS);

    private static final long serialVersionUID = 1L;

    // 106 days
    private static final long MAX_SAFE_MILLIS = 106L * FTimeUnit.MILLISECONDS_IN_DAY;
    private static final long MIN_SAFE_MILLIS = -MAX_SAFE_MILLIS;

    private final long millis;
    private final int picos;
    private final FTimeUnit timeUnit;

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
        final long picosMaybeOverflow = FDatePicos.addPicosecondsMaybeOverflow(end.picosValue(), -start.picosValue());
        final long millisOverflow = FDatePicos.toMillisecondsOverflow(picosMaybeOverflow);
        final int durationPicos = FDatePicos.toPicosWithoutOverflow(picosMaybeOverflow);
        final long durationMillis = end.millisValue() - start.millisValue() + millisOverflow;
        this.millis = durationMillis;
        this.picos = durationPicos;
        this.timeUnit = timeUnit;
    }

    public Duration(final FDate start) {
        this(start, FDate.now(), FTimeUnit.PICOSECONDS);
    }

    public Duration(final FDate start, final FTimeUnit timeUnit) {
        this(start, FDate.now(), timeUnit);
    }

    public Duration(final FDate start, final FDate end) {
        this(start, end, FTimeUnit.PICOSECONDS);
    }

    public Duration(final FDate start, final FDate end, final FTimeUnit timeUnit) {
        final long picosMaybeOverflow = FDatePicos.addPicosecondsMaybeOverflow(end.picosValue(), -start.picosValue());
        final long millisOverflow = FDatePicos.toMillisecondsOverflow(picosMaybeOverflow);
        final int durationPicos = FDatePicos.toPicosWithoutOverflow(picosMaybeOverflow);
        final long durationMillis = end.millisValue() - start.millisValue() + millisOverflow;
        this.millis = durationMillis;
        this.picos = durationPicos;
        this.timeUnit = timeUnit;
    }

    public Duration(final long millis, final int picos, final FTimeUnit timeUnit) {
        this.millis = millis;
        this.picos = picos;
        this.timeUnit = timeUnit;
    }

    public Duration(final long duration, final FTimeUnit timeUnit) {
        switch (timeUnit) {
        case MILLENIA:
            millis = duration * FTimeUnit.MILLISECONDS_IN_MILLENIUM;
            picos = 0;
            break;
        case CENTURIES:
            millis = duration * FTimeUnit.MILLISECONDS_IN_CENTURY;
            picos = 0;
            break;
        case DECADES:
            millis = duration * FTimeUnit.MILLISECONDS_IN_DECADE;
            picos = 0;
            break;
        case YEARS:
            millis = duration * FTimeUnit.MILLISECONDS_IN_YEAR;
            picos = 0;
            break;
        case MONTHS:
            millis = duration * FTimeUnit.MILLISECONDS_IN_MONTH;
            picos = 0;
            break;
        case WEEKS:
            millis = duration * FTimeUnit.MILLISECONDS_IN_WEEK;
            picos = 0;
            break;
        case DAYS:
            millis = duration * FTimeUnit.MILLISECONDS_IN_DAY;
            picos = 0;
            break;
        case HOURS:
            millis = duration * FTimeUnit.MILLISECONDS_IN_HOUR;
            picos = 0;
            break;
        case MINUTES:
            millis = duration * FTimeUnit.MILLISECONDS_IN_MINUTE;
            picos = 0;
            break;
        case SECONDS:
            millis = duration * FTimeUnit.MILLISECONDS_IN_SECOND;
            picos = 0;
            break;
        case MILLISECONDS:
            millis = duration;
            picos = 0;
            break;
        case MICROSECONDS: {
            final long milliseconds = duration / FTimeUnit.MICROSECONDS_IN_MILLISECOND;
            final long microseconds = duration % FTimeUnit.MICROSECONDS_IN_MILLISECOND;
            final long picosMaybeOverflow = FDatePicos.addMicrosecondsMaybeOverflow(0, microseconds);
            final long millisOverflow = FDatePicos.toMillisecondsOverflow(picosMaybeOverflow);
            millis = milliseconds + millisOverflow;
            picos = FDatePicos.toPicosWithoutOverflow(picosMaybeOverflow);
            break;
        }
        case NANOSECONDS: {
            final long milliseconds = duration / FTimeUnit.NANOSECONDS_IN_MILLISECOND;
            final long nanoseconds = duration % FTimeUnit.NANOSECONDS_IN_MILLISECOND;
            final long picosMaybeOverflow = FDatePicos.addNanosecondsMaybeOverflow(0, nanoseconds);
            final long millisOverflow = FDatePicos.toMillisecondsOverflow(picosMaybeOverflow);
            millis = milliseconds + millisOverflow;
            picos = FDatePicos.toPicosWithoutOverflow(picosMaybeOverflow);
            break;
        }
        case PICOSECONDS: {
            final long milliseconds = duration / FTimeUnit.PICOSECONDS_IN_MILLISECOND;
            final long picoseconds = duration % FTimeUnit.PICOSECONDS_IN_MILLISECOND;
            final long picosMaybeOverflow = FDatePicos.addPicosecondsMaybeOverflow(0, picoseconds);
            final long millisOverflow = FDatePicos.toMillisecondsOverflow(picosMaybeOverflow);
            millis = milliseconds + millisOverflow;
            picos = FDatePicos.toPicosWithoutOverflow(picosMaybeOverflow);
            break;
        }
        default:
            throw UnknownArgumentException.newInstance(FTimeUnit.class, timeUnit);
        }
        this.timeUnit = timeUnit;
    }

    public Duration(final double millisFractionalPicos) {
        this.millis = (long) millisFractionalPicos;
        this.picos = (int) ((millisFractionalPicos - millis) * FTimeUnit.PICOSECONDS_IN_MILLISECOND);
        this.timeUnit = FTimeUnit.PICOSECONDS;
    }

    public Duration(final double timeUnitFractional, final FTimeUnit timeUnit) {
        this(timeUnitFractional, timeUnit.asFractional());
    }

    public Duration(final double timeUnitFractional, final FTimeUnitFractional timeUnit) {
        final double millisFractional = timeUnit.toMillis(timeUnitFractional);
        this.millis = (long) millisFractional;
        this.picos = (int) ((millisFractional - millis) * FTimeUnit.PICOSECONDS_IN_MILLISECOND);
        this.timeUnit = timeUnit.asNonFractional();
    }

    public Duration truncate() {
        return truncate(timeUnit);
    }

    public Duration truncate(final FTimeUnit timeUnit) {
        final long truncatedMillis = FDateMillis.truncate(millis, timeUnit);
        final int truncatedPicos = FDatePicos.truncate(picos, timeUnit);
        return new Duration(truncatedMillis, truncatedPicos, timeUnit);
    }

    public boolean isGreaterThan(final Duration duration) {
        return isGreaterThan(duration.millisValue(), duration.picosValue());
    }

    public boolean isGreaterThan(final FDate date) {
        return isGreaterThan(date, FDate.now());
    }

    public boolean isGreaterThan(final FDate from, final FDate to) {
        final long picosMaybeOverflow = FDatePicos.addPicosecondsMaybeOverflow(to.picosValue(), -from.picosValue());
        final long millisOverflow = FDatePicos.toMillisecondsOverflow(picosMaybeOverflow);
        final int durationPicos = FDatePicos.toPicosWithoutOverflow(picosMaybeOverflow);
        final long durationMillis = to.millisValue() - from.millisValue() + millisOverflow;
        return isGreaterThan(durationMillis, durationPicos);
    }

    public boolean isGreaterThan(final Instant instant) {
        return isGreaterThanNanos(instant.toDurationNanos());
    }

    public boolean isGreaterThanMillis(final long durationMillis) {
        return isGreaterThan(durationMillis, 0);
    }

    public boolean isGreaterThanNanos(final long durationNanos) {
        final long milliseconds = durationNanos / FTimeUnit.NANOSECONDS_IN_MILLISECOND;
        final long nanoseconds = durationNanos % FTimeUnit.NANOSECONDS_IN_MILLISECOND;
        final long picosMaybeOverflow = FDatePicos.addNanosecondsMaybeOverflow(0, nanoseconds);
        final long millisOverflow = FDatePicos.toMillisecondsOverflow(picosMaybeOverflow);
        final long durationMillis = milliseconds + millisOverflow;
        final int durationPicos = FDatePicos.toPicosWithoutOverflow(picosMaybeOverflow);
        return isGreaterThan(durationMillis, durationPicos);
    }

    public boolean isGreaterThan(final long duration, final FTimeUnit timeUnit) {
        final long durationMillis;
        final int durationPicos;
        switch (timeUnit) {
        case MILLENIA:
            durationMillis = duration * FTimeUnit.MILLISECONDS_IN_MILLENIUM;
            durationPicos = 0;
            break;
        case CENTURIES:
            durationMillis = duration * FTimeUnit.MILLISECONDS_IN_CENTURY;
            durationPicos = 0;
            break;
        case DECADES:
            durationMillis = duration * FTimeUnit.MILLISECONDS_IN_DECADE;
            durationPicos = 0;
            break;
        case YEARS:
            durationMillis = duration * FTimeUnit.MILLISECONDS_IN_YEAR;
            durationPicos = 0;
            break;
        case MONTHS:
            durationMillis = duration * FTimeUnit.MILLISECONDS_IN_MONTH;
            durationPicos = 0;
            break;
        case WEEKS:
            durationMillis = duration * FTimeUnit.MILLISECONDS_IN_WEEK;
            durationPicos = 0;
            break;
        case DAYS:
            durationMillis = duration * FTimeUnit.MILLISECONDS_IN_DAY;
            durationPicos = 0;
            break;
        case HOURS:
            durationMillis = duration * FTimeUnit.MILLISECONDS_IN_HOUR;
            durationPicos = 0;
            break;
        case MINUTES:
            durationMillis = duration * FTimeUnit.MILLISECONDS_IN_MINUTE;
            durationPicos = 0;
            break;
        case SECONDS:
            durationMillis = duration * FTimeUnit.MILLISECONDS_IN_SECOND;
            durationPicos = 0;
            break;
        case MILLISECONDS:
            return isGreaterThanMillis(duration);
        case MICROSECONDS: {
            final long milliseconds = duration / FTimeUnit.MICROSECONDS_IN_MILLISECOND;
            final long microseconds = duration % FTimeUnit.MICROSECONDS_IN_MILLISECOND;
            final long picosMaybeOverflow = FDatePicos.addMicrosecondsMaybeOverflow(0, microseconds);
            final long millisOverflow = FDatePicos.toMillisecondsOverflow(picosMaybeOverflow);
            durationMillis = milliseconds + millisOverflow;
            durationPicos = FDatePicos.toPicosWithoutOverflow(picosMaybeOverflow);
            break;
        }
        case NANOSECONDS: {
            return isGreaterThanNanos(duration);
        }
        case PICOSECONDS: {
            final long milliseconds = duration / FTimeUnit.PICOSECONDS_IN_MILLISECOND;
            final long picoseconds = duration % FTimeUnit.PICOSECONDS_IN_MILLISECOND;
            final long picosMaybeOverflow = FDatePicos.addPicosecondsMaybeOverflow(0, picoseconds);
            final long millisOverflow = FDatePicos.toMillisecondsOverflow(picosMaybeOverflow);
            durationMillis = milliseconds + millisOverflow;
            durationPicos = FDatePicos.toPicosWithoutOverflow(picosMaybeOverflow);
            break;
        }
        default:
            throw UnknownArgumentException.newInstance(FTimeUnit.class, timeUnit);
        }
        return isGreaterThan(durationMillis, durationPicos);
    }

    public boolean isGreaterThan(final long durationMillis, final int durationPicos) {
        return millisValue() > durationMillis || (millisValue() == durationMillis && picosValue() > durationPicos);
    }

    public boolean isGreaterThanOrEqualTo(final Duration duration) {
        return isGreaterThanOrEqualTo(duration.millisValue(), duration.picosValue());
    }

    public boolean isGreaterThanOrEqualTo(final FDate date) {
        return isGreaterThanOrEqualTo(date, FDate.now());
    }

    public boolean isGreaterThanOrEqualTo(final FDate from, final FDate to) {
        final long picosMaybeOverflow = FDatePicos.addPicosecondsMaybeOverflow(to.picosValue(), -from.picosValue());
        final long millisOverflow = FDatePicos.toMillisecondsOverflow(picosMaybeOverflow);
        final int durationPicos = FDatePicos.toPicosWithoutOverflow(picosMaybeOverflow);
        final long durationMillis = to.millisValue() - from.millisValue() + millisOverflow;
        return isGreaterThanOrEqualTo(durationMillis, durationPicos);
    }

    public boolean isGreaterThanOrEqualTo(final Instant instant) {
        return isGreaterThanOrEqualToNanos(instant.toDurationNanos());
    }

    public boolean isGreaterThanOrEqualToMillis(final long durationMillis) {
        return isGreaterThanOrEqualTo(durationMillis, 0);
    }

    public boolean isGreaterThanOrEqualToNanos(final long durationNanos) {
        final long milliseconds = durationNanos / FTimeUnit.NANOSECONDS_IN_MILLISECOND;
        final long nanoseconds = durationNanos % FTimeUnit.NANOSECONDS_IN_MILLISECOND;
        final long picosMaybeOverflow = FDatePicos.addNanosecondsMaybeOverflow(0, nanoseconds);
        final long millisOverflow = FDatePicos.toMillisecondsOverflow(picosMaybeOverflow);
        final long durationMillis = milliseconds + millisOverflow;
        final int durationPicos = FDatePicos.toPicosWithoutOverflow(picosMaybeOverflow);
        return isGreaterThanOrEqualTo(durationMillis, durationPicos);
    }

    public boolean isGreaterThanOrEqualTo(final long duration, final FTimeUnit timeUnit) {
        final long durationMillis;
        final int durationPicos;
        switch (timeUnit) {
        case MILLENIA:
            durationMillis = duration * FTimeUnit.MILLISECONDS_IN_MILLENIUM;
            durationPicos = 0;
            break;
        case CENTURIES:
            durationMillis = duration * FTimeUnit.MILLISECONDS_IN_CENTURY;
            durationPicos = 0;
            break;
        case DECADES:
            durationMillis = duration * FTimeUnit.MILLISECONDS_IN_DECADE;
            durationPicos = 0;
            break;
        case YEARS:
            durationMillis = duration * FTimeUnit.MILLISECONDS_IN_YEAR;
            durationPicos = 0;
            break;
        case MONTHS:
            durationMillis = duration * FTimeUnit.MILLISECONDS_IN_MONTH;
            durationPicos = 0;
            break;
        case WEEKS:
            durationMillis = duration * FTimeUnit.MILLISECONDS_IN_WEEK;
            durationPicos = 0;
            break;
        case DAYS:
            durationMillis = duration * FTimeUnit.MILLISECONDS_IN_DAY;
            durationPicos = 0;
            break;
        case HOURS:
            durationMillis = duration * FTimeUnit.MILLISECONDS_IN_HOUR;
            durationPicos = 0;
            break;
        case MINUTES:
            durationMillis = duration * FTimeUnit.MILLISECONDS_IN_MINUTE;
            durationPicos = 0;
            break;
        case SECONDS:
            durationMillis = duration * FTimeUnit.MILLISECONDS_IN_SECOND;
            durationPicos = 0;
            break;
        case MILLISECONDS:
            return isGreaterThanOrEqualToMillis(duration);
        case MICROSECONDS: {
            final long milliseconds = duration / FTimeUnit.MICROSECONDS_IN_MILLISECOND;
            final long microseconds = duration % FTimeUnit.MICROSECONDS_IN_MILLISECOND;
            final long picosMaybeOverflow = FDatePicos.addMicrosecondsMaybeOverflow(0, microseconds);
            final long millisOverflow = FDatePicos.toMillisecondsOverflow(picosMaybeOverflow);
            durationMillis = milliseconds + millisOverflow;
            durationPicos = FDatePicos.toPicosWithoutOverflow(picosMaybeOverflow);
            break;
        }
        case NANOSECONDS: {
            return isGreaterThanOrEqualToNanos(duration);
        }
        case PICOSECONDS: {
            final long milliseconds = duration / FTimeUnit.PICOSECONDS_IN_MILLISECOND;
            final long picoseconds = duration % FTimeUnit.PICOSECONDS_IN_MILLISECOND;
            final long picosMaybeOverflow = FDatePicos.addPicosecondsMaybeOverflow(0, picoseconds);
            final long millisOverflow = FDatePicos.toMillisecondsOverflow(picosMaybeOverflow);
            durationMillis = milliseconds + millisOverflow;
            durationPicos = FDatePicos.toPicosWithoutOverflow(picosMaybeOverflow);
            break;
        }
        default:
            throw UnknownArgumentException.newInstance(FTimeUnit.class, timeUnit);
        }
        return isGreaterThanOrEqualTo(durationMillis, durationPicos);
    }

    public boolean isGreaterThanOrEqualTo(final long durationMillis, final int durationPicos) {
        return millisValue() > durationMillis || (millisValue() == durationMillis && picosValue() >= durationPicos);
    }

    public boolean isLessThan(final Duration duration) {
        return isLessThan(duration.millisValue(), duration.picosValue());
    }

    public boolean isLessThan(final FDate date) {
        return isLessThan(date, FDate.now());
    }

    public boolean isLessThan(final FDate from, final FDate to) {
        final long picosMaybeOverflow = FDatePicos.addPicosecondsMaybeOverflow(to.picosValue(), -from.picosValue());
        final long millisOverflow = FDatePicos.toMillisecondsOverflow(picosMaybeOverflow);
        final int durationPicos = FDatePicos.toPicosWithoutOverflow(picosMaybeOverflow);
        final long durationMillis = to.millisValue() - from.millisValue() + millisOverflow;
        return isLessThan(durationMillis, durationPicos);
    }

    public boolean isLessThan(final Instant instant) {
        return isLessThanNanos(instant.toDurationNanos());
    }

    public boolean isLessThanMillis(final long durationMillis) {
        return isLessThan(durationMillis, 0);
    }

    public boolean isLessThanNanos(final long durationNanos) {
        final long milliseconds = durationNanos / FTimeUnit.NANOSECONDS_IN_MILLISECOND;
        final long nanoseconds = durationNanos % FTimeUnit.NANOSECONDS_IN_MILLISECOND;
        final long picosMaybeOverflow = FDatePicos.addNanosecondsMaybeOverflow(0, nanoseconds);
        final long millisOverflow = FDatePicos.toMillisecondsOverflow(picosMaybeOverflow);
        final long durationMillis = milliseconds + millisOverflow;
        final int durationPicos = FDatePicos.toPicosWithoutOverflow(picosMaybeOverflow);
        return isLessThan(durationMillis, durationPicos);
    }

    public boolean isLessThan(final long duration, final FTimeUnit timeUnit) {
        final long durationMillis;
        final int durationPicos;
        switch (timeUnit) {
        case MILLENIA:
            durationMillis = duration * FTimeUnit.MILLISECONDS_IN_MILLENIUM;
            durationPicos = 0;
            break;
        case CENTURIES:
            durationMillis = duration * FTimeUnit.MILLISECONDS_IN_CENTURY;
            durationPicos = 0;
            break;
        case DECADES:
            durationMillis = duration * FTimeUnit.MILLISECONDS_IN_DECADE;
            durationPicos = 0;
            break;
        case YEARS:
            durationMillis = duration * FTimeUnit.MILLISECONDS_IN_YEAR;
            durationPicos = 0;
            break;
        case MONTHS:
            durationMillis = duration * FTimeUnit.MILLISECONDS_IN_MONTH;
            durationPicos = 0;
            break;
        case WEEKS:
            durationMillis = duration * FTimeUnit.MILLISECONDS_IN_WEEK;
            durationPicos = 0;
            break;
        case DAYS:
            durationMillis = duration * FTimeUnit.MILLISECONDS_IN_DAY;
            durationPicos = 0;
            break;
        case HOURS:
            durationMillis = duration * FTimeUnit.MILLISECONDS_IN_HOUR;
            durationPicos = 0;
            break;
        case MINUTES:
            durationMillis = duration * FTimeUnit.MILLISECONDS_IN_MINUTE;
            durationPicos = 0;
            break;
        case SECONDS:
            durationMillis = duration * FTimeUnit.MILLISECONDS_IN_SECOND;
            durationPicos = 0;
            break;
        case MILLISECONDS:
            return isLessThanMillis(duration);
        case MICROSECONDS: {
            final long milliseconds = duration / FTimeUnit.MICROSECONDS_IN_MILLISECOND;
            final long microseconds = duration % FTimeUnit.MICROSECONDS_IN_MILLISECOND;
            final long picosMaybeOverflow = FDatePicos.addMicrosecondsMaybeOverflow(0, microseconds);
            final long millisOverflow = FDatePicos.toMillisecondsOverflow(picosMaybeOverflow);
            durationMillis = milliseconds + millisOverflow;
            durationPicos = FDatePicos.toPicosWithoutOverflow(picosMaybeOverflow);
            break;
        }
        case NANOSECONDS: {
            return isLessThanNanos(duration);
        }
        case PICOSECONDS: {
            final long milliseconds = duration / FTimeUnit.PICOSECONDS_IN_MILLISECOND;
            final long picoseconds = duration % FTimeUnit.PICOSECONDS_IN_MILLISECOND;
            final long picosMaybeOverflow = FDatePicos.addPicosecondsMaybeOverflow(0, picoseconds);
            final long millisOverflow = FDatePicos.toMillisecondsOverflow(picosMaybeOverflow);
            durationMillis = milliseconds + millisOverflow;
            durationPicos = FDatePicos.toPicosWithoutOverflow(picosMaybeOverflow);
            break;
        }
        default:
            throw UnknownArgumentException.newInstance(FTimeUnit.class, timeUnit);
        }
        return isLessThan(durationMillis, durationPicos);
    }

    public boolean isLessThan(final long durationMillis, final int durationPicos) {
        return millisValue() < durationMillis || (millisValue() == durationMillis && picosValue() < durationPicos);
    }

    public boolean isLessThanOrEqualTo(final Duration duration) {
        return isLessThanOrEqualTo(duration.millisValue(), duration.picosValue());
    }

    public boolean isLessThanOrEqualTo(final FDate date) {
        return isLessThanOrEqualTo(date, FDate.now());
    }

    public boolean isLessThanOrEqualTo(final FDate from, final FDate to) {
        final long picosMaybeOverflow = FDatePicos.addPicosecondsMaybeOverflow(to.picosValue(), -from.picosValue());
        final long millisOverflow = FDatePicos.toMillisecondsOverflow(picosMaybeOverflow);
        final int durationPicos = FDatePicos.toPicosWithoutOverflow(picosMaybeOverflow);
        final long durationMillis = to.millisValue() - from.millisValue() + millisOverflow;
        return isLessThanOrEqualTo(durationMillis, durationPicos);
    }

    public boolean isLessThanOrEqualTo(final Instant instant) {
        return isLessThanOrEqualToNanos(instant.toDurationNanos());
    }

    public boolean isLessThanOrEqualToMillis(final long durationMillis) {
        return isLessThanOrEqualTo(durationMillis, 0);
    }

    public boolean isLessThanOrEqualToNanos(final long durationNanos) {
        final long milliseconds = durationNanos / FTimeUnit.NANOSECONDS_IN_MILLISECOND;
        final long nanoseconds = durationNanos % FTimeUnit.NANOSECONDS_IN_MILLISECOND;
        final long picosMaybeOverflow = FDatePicos.addNanosecondsMaybeOverflow(0, nanoseconds);
        final long millisOverflow = FDatePicos.toMillisecondsOverflow(picosMaybeOverflow);
        final long durationMillis = milliseconds + millisOverflow;
        final int durationPicos = FDatePicos.toPicosWithoutOverflow(picosMaybeOverflow);
        return isLessThanOrEqualTo(durationMillis, durationPicos);
    }

    public boolean isLessThanOrEqualTo(final long duration, final FTimeUnit timeUnit) {
        final long durationMillis;
        final int durationPicos;
        switch (timeUnit) {
        case MILLENIA:
            durationMillis = duration * FTimeUnit.MILLISECONDS_IN_MILLENIUM;
            durationPicos = 0;
            break;
        case CENTURIES:
            durationMillis = duration * FTimeUnit.MILLISECONDS_IN_CENTURY;
            durationPicos = 0;
            break;
        case DECADES:
            durationMillis = duration * FTimeUnit.MILLISECONDS_IN_DECADE;
            durationPicos = 0;
            break;
        case YEARS:
            durationMillis = duration * FTimeUnit.MILLISECONDS_IN_YEAR;
            durationPicos = 0;
            break;
        case MONTHS:
            durationMillis = duration * FTimeUnit.MILLISECONDS_IN_MONTH;
            durationPicos = 0;
            break;
        case WEEKS:
            durationMillis = duration * FTimeUnit.MILLISECONDS_IN_WEEK;
            durationPicos = 0;
            break;
        case DAYS:
            durationMillis = duration * FTimeUnit.MILLISECONDS_IN_DAY;
            durationPicos = 0;
            break;
        case HOURS:
            durationMillis = duration * FTimeUnit.MILLISECONDS_IN_HOUR;
            durationPicos = 0;
            break;
        case MINUTES:
            durationMillis = duration * FTimeUnit.MILLISECONDS_IN_MINUTE;
            durationPicos = 0;
            break;
        case SECONDS:
            durationMillis = duration * FTimeUnit.MILLISECONDS_IN_SECOND;
            durationPicos = 0;
            break;
        case MILLISECONDS:
            return isLessThanOrEqualToMillis(duration);
        case MICROSECONDS: {
            final long milliseconds = duration / FTimeUnit.MICROSECONDS_IN_MILLISECOND;
            final long microseconds = duration % FTimeUnit.MICROSECONDS_IN_MILLISECOND;
            final long picosMaybeOverflow = FDatePicos.addMicrosecondsMaybeOverflow(0, microseconds);
            final long millisOverflow = FDatePicos.toMillisecondsOverflow(picosMaybeOverflow);
            durationMillis = milliseconds + millisOverflow;
            durationPicos = FDatePicos.toPicosWithoutOverflow(picosMaybeOverflow);
            break;
        }
        case NANOSECONDS: {
            return isLessThanOrEqualToNanos(duration);
        }
        case PICOSECONDS: {
            final long milliseconds = duration / FTimeUnit.PICOSECONDS_IN_MILLISECOND;
            final long picoseconds = duration % FTimeUnit.PICOSECONDS_IN_MILLISECOND;
            final long picosMaybeOverflow = FDatePicos.addPicosecondsMaybeOverflow(0, picoseconds);
            final long millisOverflow = FDatePicos.toMillisecondsOverflow(picosMaybeOverflow);
            durationMillis = milliseconds + millisOverflow;
            durationPicos = FDatePicos.toPicosWithoutOverflow(picosMaybeOverflow);
            break;
        }
        default:
            throw UnknownArgumentException.newInstance(FTimeUnit.class, timeUnit);
        }
        return isLessThanOrEqualTo(durationMillis, durationPicos);
    }

    public boolean isLessThanOrEqualTo(final long durationMillis, final int durationPicos) {
        return millisValue() < durationMillis || (millisValue() == durationMillis && picosValue() <= durationPicos);
    }

    public FTimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void sleep() throws InterruptedException {
        final int nanos = picos / FTimeUnit.PICOSECONDS_IN_NANOSECOND;
        Thread.sleep(millis, nanos);
    }

    public void sleepRandom() throws InterruptedException {
        final IRandomGenerator random = PseudoRandomGenerators.getThreadLocalPseudoRandom();
        final long millis = millisValue();
        final int picos = picosValue();
        if (millis == 0) {
            if (picos == 0) {
                Thread.yield();
                return;
            }
            final int nanos = picos / FTimeUnit.PICOSECONDS_IN_NANOSECOND;
            if (nanos == 0) {
                Thread.yield();
                return;
            }
            final int randomNanos = random.nextInt(nanos);
            Thread.sleep(millis, randomNanos);
        } else {
            final long randomMillis = random.nextLong(millis);
            if (picos == 0) {
                Thread.sleep(randomMillis);
                return;
            }
            final int nanos = picos / FTimeUnit.PICOSECONDS_IN_NANOSECOND;
            if (nanos == 0) {
                Thread.sleep(randomMillis);
                return;
            }
            final int randomNanos = random.nextInt(nanos);
            Thread.sleep(millis, randomNanos);
        }
    }

    @Override
    public int intValue() {
        return intValue(timeUnit);
    }

    public int intValue(final FTimeUnit timeUnit) {
        return Integers.checkedCast(longValue(timeUnit));
    }

    public long millisValue() {
        return millis;
    }

    public int picosValue() {
        return picos;
    }

    public long nanosValue() {
        return millis * FTimeUnit.NANOSECONDS_IN_MILLISECOND + picos / FTimeUnit.PICOSECONDS_IN_NANOSECOND;
    }

    @Override
    public long longValue() {
        return longValue(timeUnit);
    }

    public long longValue(final FTimeUnit timeUnit) {
        final long duration;
        switch (timeUnit) {
        case MILLENIA:
            duration = millis / FTimeUnit.MILLISECONDS_IN_MILLENIUM;
            break;
        case CENTURIES:
            duration = millis / FTimeUnit.MILLISECONDS_IN_CENTURY;
            break;
        case DECADES:
            duration = millis / FTimeUnit.MILLISECONDS_IN_DECADE;
            break;
        case YEARS:
            duration = millis / FTimeUnit.MILLISECONDS_IN_YEAR;
            break;
        case MONTHS:
            duration = millis / FTimeUnit.MILLISECONDS_IN_MONTH;
            break;
        case WEEKS:
            duration = millis / FTimeUnit.MILLISECONDS_IN_WEEK;
            break;
        case DAYS:
            duration = millis / FTimeUnit.MILLISECONDS_IN_DAY;
            break;
        case HOURS:
            duration = millis / FTimeUnit.MILLISECONDS_IN_HOUR;
            break;
        case MINUTES:
            duration = millis / FTimeUnit.MILLISECONDS_IN_MINUTE;
            break;
        case SECONDS:
            duration = millis / FTimeUnit.MILLISECONDS_IN_SECOND;
            break;
        case MILLISECONDS:
            duration = millis;
            break;
        case MICROSECONDS:
            duration = millis * FTimeUnit.MICROSECONDS_IN_MILLISECOND + picos / FTimeUnit.PICOSECONDS_IN_MICROSECOND;
            break;
        case NANOSECONDS:
            duration = millis * FTimeUnit.NANOSECONDS_IN_MILLISECOND + picos / FTimeUnit.PICOSECONDS_IN_NANOSECOND;
            break;
        case PICOSECONDS:
            duration = millis * FTimeUnit.PICOSECONDS_IN_MILLISECOND + picos;
            break;
        default:
            throw UnknownArgumentException.newInstance(FTimeUnit.class, timeUnit);
        }
        return duration;
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
        final double millisDouble = millis;
        final double picosDouble = picos;
        final double duration;
        switch (timeUnit) {
        case MILLENIA:
            duration = millisDouble / FTimeUnit.MILLISECONDS_IN_MILLENIUM
                    + picosDouble / FTimeUnit.PICOSECONDS_IN_MILLENIUM;
            break;
        case CENTURIES:
            duration = millisDouble / FTimeUnit.MILLISECONDS_IN_CENTURY
                    + picosDouble / FTimeUnit.PICOSECONDS_IN_CENTURY;
            break;
        case DECADES:
            duration = millisDouble / FTimeUnit.MILLISECONDS_IN_DECADE + picosDouble / FTimeUnit.PICOSECONDS_IN_DECADE;
            break;
        case YEARS:
            duration = millisDouble / FTimeUnit.MILLISECONDS_IN_YEAR + picosDouble / FTimeUnit.PICOSECONDS_IN_YEAR;
            break;
        case MONTHS:
            duration = millisDouble / FTimeUnit.MILLISECONDS_IN_MONTH + picosDouble / FTimeUnit.PICOSECONDS_IN_MONTH;
            break;
        case WEEKS:
            duration = millisDouble / FTimeUnit.MILLISECONDS_IN_WEEK + picosDouble / FTimeUnit.PICOSECONDS_IN_WEEK;
            break;
        case DAYS:
            duration = millisDouble / FTimeUnit.MILLISECONDS_IN_DAY + picosDouble / FTimeUnit.PICOSECONDS_IN_DAY;
            break;
        case HOURS:
            duration = millisDouble / FTimeUnit.MILLISECONDS_IN_HOUR + picosDouble / FTimeUnit.PICOSECONDS_IN_HOUR;
            break;
        case MINUTES:
            duration = millisDouble / FTimeUnit.MILLISECONDS_IN_MINUTE + picosDouble / FTimeUnit.PICOSECONDS_IN_MINUTE;
            break;
        case SECONDS:
            duration = millisDouble / FTimeUnit.MILLISECONDS_IN_SECOND + picosDouble / FTimeUnit.PICOSECONDS_IN_SECOND;
            break;
        case MILLISECONDS:
            duration = millisDouble + picosDouble / FTimeUnit.PICOSECONDS_IN_MILLISECOND;
            break;
        case MICROSECONDS:
            duration = millisDouble * FTimeUnit.MICROSECONDS_IN_MILLISECOND
                    + picosDouble / FTimeUnit.PICOSECONDS_IN_MICROSECOND;
            break;
        case NANOSECONDS:
            duration = millisDouble * FTimeUnit.NANOSECONDS_IN_MILLISECOND
                    + picosDouble / FTimeUnit.PICOSECONDS_IN_NANOSECOND;
            break;
        case PICOSECONDS:
            duration = millisDouble * FTimeUnit.PICOSECONDS_IN_MILLISECOND + picosDouble;
            break;
        default:
            throw UnknownArgumentException.newInstance(FTimeUnit.class, timeUnit);
        }
        return duration;
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
    public String toString(final FTimeUnit smallestTimeUnit) {
        int picoseconds = Integers.abs(picosValue());
        final long microseconds = FTimeUnit.PICOSECONDS.toMicros(picoseconds);
        picoseconds -= FTimeUnit.MICROSECONDS.toPicos(microseconds);
        final long nanoseconds = FTimeUnit.PICOSECONDS.toNanos(picoseconds);
        picoseconds -= FTimeUnit.NANOSECONDS.toPicos(nanoseconds);

        long milliseconds = Longs.abs(millisValue());
        final long years = FTimeUnit.MILLISECONDS.toYears(milliseconds);
        milliseconds -= FTimeUnit.YEARS.toMillis(years);
        final long months = FTimeUnit.MILLISECONDS.toMonths(milliseconds);
        milliseconds -= FTimeUnit.MONTHS.toMillis(months);
        final long weeks = FTimeUnit.MILLISECONDS.toWeeks(milliseconds);
        milliseconds -= FTimeUnit.WEEKS.toMillis(weeks);
        final long days = FTimeUnit.MILLISECONDS.toDays(milliseconds);
        milliseconds -= FTimeUnit.DAYS.toMillis(days);
        final long hours = FTimeUnit.MILLISECONDS.toHours(milliseconds);
        milliseconds -= FTimeUnit.HOURS.toMillis(hours);
        final long minutes = FTimeUnit.MILLISECONDS.toMinutes(milliseconds);
        milliseconds -= FTimeUnit.MINUTES.toMillis(minutes);
        final long seconds = FTimeUnit.MILLISECONDS.toSeconds(milliseconds);
        milliseconds -= FTimeUnit.SECONDS.toMillis(seconds);

        final StringBuilder sb = new StringBuilder();
        switch (smallestTimeUnit) {
        case PICOSECONDS:
            if (picoseconds > 0) {
                sb.insert(0, Strings.leftPad(picoseconds, 3, "0"));
                sb.insert(0, ".");
            }
            // fall through
        case NANOSECONDS:
            if (nanoseconds + picoseconds > 0) {
                sb.insert(0, Strings.leftPad(nanoseconds, 3, "0"));
                sb.insert(0, ".");
            }
            // fall through
        case MICROSECONDS:
            if (microseconds + nanoseconds + picoseconds > 0) {
                sb.insert(0, Strings.leftPad(microseconds, 3, "0"));
                sb.insert(0, ".");
            }
            // fall through
        case MILLISECONDS:
            if (milliseconds + microseconds + nanoseconds + picoseconds > 0) {
                sb.insert(0, Strings.leftPad(milliseconds, 3, "0"));
                sb.insert(0, ".");
            }
            // fall through
        case SECONDS:
            if (seconds + milliseconds + microseconds + nanoseconds + picoseconds > 0) {
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
        if ((millis < 0 || picos < 0) && !isP0(sb)) {
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
        final long addedMillis;
        final int addedPicos;
        switch (timeUnit) {
        case MILLENIA:
            addedMillis = millis + duration * FTimeUnit.MILLISECONDS_IN_MILLENIUM;
            addedPicos = picos;
            break;
        case CENTURIES:
            addedMillis = millis + duration * FTimeUnit.MILLISECONDS_IN_CENTURY;
            addedPicos = picos;
            break;
        case DECADES:
            addedMillis = millis + duration * FTimeUnit.MILLISECONDS_IN_DECADE;
            addedPicos = picos;
            break;
        case YEARS:
            addedMillis = millis + duration * FTimeUnit.MILLISECONDS_IN_YEAR;
            addedPicos = picos;
            break;
        case MONTHS:
            addedMillis = millis + duration * FTimeUnit.MILLISECONDS_IN_MONTH;
            addedPicos = picos;
            break;
        case WEEKS:
            addedMillis = millis + duration * FTimeUnit.MILLISECONDS_IN_WEEK;
            addedPicos = picos;
            break;
        case DAYS:
            addedMillis = millis + duration * FTimeUnit.MILLISECONDS_IN_DAY;
            addedPicos = picos;
            break;
        case HOURS:
            addedMillis = millis + duration * FTimeUnit.MILLISECONDS_IN_HOUR;
            addedPicos = picos;
            break;
        case MINUTES:
            addedMillis = millis + duration * FTimeUnit.MILLISECONDS_IN_MINUTE;
            addedPicos = picos;
            break;
        case SECONDS:
            addedMillis = millis + duration * FTimeUnit.MILLISECONDS_IN_SECOND;
            addedPicos = picos;
            break;
        case MILLISECONDS:
            addedMillis = millis + duration;
            addedPicos = picos;
            break;
        case MICROSECONDS: {
            final long milliseconds = duration / FTimeUnit.MICROSECONDS_IN_MILLISECOND;
            final long microseconds = duration % FTimeUnit.MICROSECONDS_IN_MILLISECOND;
            final long picosMaybeOverflow = FDatePicos.addMicrosecondsMaybeOverflow(0, microseconds);
            final long millisOverflow = FDatePicos.toMillisecondsOverflow(picosMaybeOverflow);
            addedMillis = milliseconds + millisOverflow;
            addedPicos = FDatePicos.toPicosWithoutOverflow(picosMaybeOverflow);
            break;
        }
        case NANOSECONDS: {
            final long milliseconds = duration / FTimeUnit.NANOSECONDS_IN_MILLISECOND;
            final long nanoseconds = duration % FTimeUnit.NANOSECONDS_IN_MILLISECOND;
            final long picosMaybeOverflow = FDatePicos.addNanosecondsMaybeOverflow(0, nanoseconds);
            final long millisOverflow = FDatePicos.toMillisecondsOverflow(picosMaybeOverflow);
            addedMillis = milliseconds + millisOverflow;
            addedPicos = FDatePicos.toPicosWithoutOverflow(picosMaybeOverflow);
            break;
        }
        case PICOSECONDS: {
            final long milliseconds = duration / FTimeUnit.PICOSECONDS_IN_MILLISECOND;
            final long picoseconds = duration % FTimeUnit.PICOSECONDS_IN_MILLISECOND;
            final long picosMaybeOverflow = FDatePicos.addPicosecondsMaybeOverflow(0, picoseconds);
            final long millisOverflow = FDatePicos.toMillisecondsOverflow(picosMaybeOverflow);
            addedMillis = milliseconds + millisOverflow;
            addedPicos = FDatePicos.toPicosWithoutOverflow(picosMaybeOverflow);
            break;
        }
        default:
            throw UnknownArgumentException.newInstance(FTimeUnit.class, timeUnit);
        }
        return new Duration(addedMillis, addedPicos, this.timeUnit);
    }

    public Duration subtract(final long duration, final FTimeUnit timeUnit) {
        final long subtrahendMillis;
        final int subtrahendPicos;
        switch (timeUnit) {
        case MILLENIA:
            subtrahendMillis = duration * FTimeUnit.MILLISECONDS_IN_MILLENIUM;
            subtrahendPicos = 0;
            break;
        case CENTURIES:
            subtrahendMillis = duration * FTimeUnit.MILLISECONDS_IN_CENTURY;
            subtrahendPicos = 0;
            break;
        case DECADES:
            subtrahendMillis = duration * FTimeUnit.MILLISECONDS_IN_DECADE;
            subtrahendPicos = 0;
            break;
        case YEARS:
            subtrahendMillis = duration * FTimeUnit.MILLISECONDS_IN_YEAR;
            subtrahendPicos = 0;
            break;
        case MONTHS:
            subtrahendMillis = duration * FTimeUnit.MILLISECONDS_IN_MONTH;
            subtrahendPicos = 0;
            break;
        case WEEKS:
            subtrahendMillis = duration * FTimeUnit.MILLISECONDS_IN_WEEK;
            subtrahendPicos = 0;
            break;
        case DAYS:
            subtrahendMillis = duration * FTimeUnit.MILLISECONDS_IN_DAY;
            subtrahendPicos = 0;
            break;
        case HOURS:
            subtrahendMillis = duration * FTimeUnit.MILLISECONDS_IN_HOUR;
            subtrahendPicos = 0;
            break;
        case MINUTES:
            subtrahendMillis = duration * FTimeUnit.MILLISECONDS_IN_MINUTE;
            subtrahendPicos = 0;
            break;
        case SECONDS:
            subtrahendMillis = duration * FTimeUnit.MILLISECONDS_IN_SECOND;
            subtrahendPicos = 0;
            break;
        case MILLISECONDS:
            subtrahendMillis = duration;
            subtrahendPicos = 0;
            break;
        case MICROSECONDS: {
            final long milliseconds = duration / FTimeUnit.MICROSECONDS_IN_MILLISECOND;
            final long microseconds = duration % FTimeUnit.MICROSECONDS_IN_MILLISECOND;
            final long picosMaybeOverflow = FDatePicos.addMicrosecondsMaybeOverflow(0, microseconds);
            final long millisOverflow = FDatePicos.toMillisecondsOverflow(picosMaybeOverflow);
            subtrahendMillis = milliseconds + millisOverflow;
            subtrahendPicos = FDatePicos.toPicosWithoutOverflow(picosMaybeOverflow);
            break;
        }
        case NANOSECONDS: {
            final long milliseconds = duration / FTimeUnit.NANOSECONDS_IN_MILLISECOND;
            final long nanoseconds = duration % FTimeUnit.NANOSECONDS_IN_MILLISECOND;
            final long picosMaybeOverflow = FDatePicos.addNanosecondsMaybeOverflow(0, nanoseconds);
            final long millisOverflow = FDatePicos.toMillisecondsOverflow(picosMaybeOverflow);
            subtrahendMillis = milliseconds + millisOverflow;
            subtrahendPicos = FDatePicos.toPicosWithoutOverflow(picosMaybeOverflow);
            break;
        }
        case PICOSECONDS: {
            final long milliseconds = duration / FTimeUnit.PICOSECONDS_IN_MILLISECOND;
            final long picoseconds = duration % FTimeUnit.PICOSECONDS_IN_MILLISECOND;
            final long picosMaybeOverflow = FDatePicos.addPicosecondsMaybeOverflow(0, picoseconds);
            final long millisOverflow = FDatePicos.toMillisecondsOverflow(picosMaybeOverflow);
            subtrahendMillis = milliseconds + millisOverflow;
            subtrahendPicos = FDatePicos.toPicosWithoutOverflow(picosMaybeOverflow);
            break;
        }
        default:
            throw UnknownArgumentException.newInstance(FTimeUnit.class, timeUnit);
        }
        return subtract(subtrahendMillis, subtrahendPicos);
    }

    public Duration divide(final Number divisor) {
        return divide(divisor.doubleValue());
    }

    public Duration divide(final double divisor) {
        if (divisor == 0.0) {
            return Duration.ZERO;
        }
        final double exactMillis = millisValue() / divisor;
        final long dividedMillis = (long) exactMillis;

        final double fractionalMillis = exactMillis - dividedMillis;
        final double carriedPicos = fractionalMillis * FTimeUnit.PICOSECONDS_IN_MILLISECOND;
        final double exactPicos = (picosValue() / divisor) + carriedPicos;

        long finalMillis = dividedMillis + (long) (exactPicos / FTimeUnit.PICOSECONDS_IN_MILLISECOND);
        int finalPicos = (int) (exactPicos % FTimeUnit.PICOSECONDS_IN_MILLISECOND);

        if (finalPicos < 0) {
            finalMillis--;
            finalPicos += FTimeUnit.PICOSECONDS_IN_MILLISECOND;
        }
        return new Duration(finalMillis, finalPicos, timeUnit);
    }

    public Duration divide(final Duration divisor) {
        if (divisor == null || divisor.isZero()) {
            return Duration.ZERO;
        }

        // 1. Convert both durations to a total millisecond decimal representation
        final double thisTotalMillis = this.millisValue()
                + ((double) this.picosValue() / FTimeUnit.PICOSECONDS_IN_MILLISECOND);

        final double divisorTotalMillis = divisor.millisValue()
                + ((double) divisor.picosValue() / FTimeUnit.PICOSECONDS_IN_MILLISECOND);

        // 2. Calculate the exact scalar factor between them
        final double factor = thisTotalMillis / divisorTotalMillis;

        // 3. Divide this instance by that factor to scale it precisely
        return this.divide(factor);
    }

    public Duration divide(final double divisorMillis, final double divisorPicos) {
        if (divisorMillis == 0.0 && divisorPicos == 0.0) {
            return Duration.ZERO;
        }

        // 1. Divide the whole milliseconds
        final double exactMillis = millisValue() / divisorMillis;
        final long dividedMillis = (long) exactMillis;

        // 2. Extract the fractional millisecond remainder and convert it to picoseconds
        final double fractionalMillis = exactMillis - dividedMillis;
        final double carriedPicos = fractionalMillis * FTimeUnit.PICOSECONDS_IN_MILLISECOND;

        // 3. Divide the existing picoseconds and add the carried amount
        final double exactPicos = (picosValue() / divisorPicos) + carriedPicos;

        // 4. Combine and normalize (in case exactPicos exceeds PICOS_PER_MILLI due to floating-point math)
        long finalMillis = dividedMillis + (long) (exactPicos / FTimeUnit.PICOSECONDS_IN_MILLISECOND);
        int finalPicos = (int) (exactPicos % FTimeUnit.PICOSECONDS_IN_MILLISECOND);

        // Handle negative adjustments cleanly if handling signed durations
        if (finalPicos < 0) {
            finalMillis--;
            finalPicos += FTimeUnit.PICOSECONDS_IN_MILLISECOND;
        }

        return new Duration(finalMillis, finalPicos, timeUnit);
    }

    public Duration multiply(final Number multiplier) {
        return multiply(multiplier.doubleValue());
    }

    public Duration multiply(final double multiplier) {
        return multiply(multiplier, multiplier);
    }

    public Duration multiply(final Duration multiplier) {
        return multiply(multiplier.millisValue(), multiplier.picosValue());
    }

    /**
     * Creates a new duration derived from this one by multipliying with the given factor.
     */
    public Duration multiply(final double multiplierMillis, final double multiplierPicos) {
        final double exactMillis = millisValue() * multiplierMillis;
        final long baseMillis = (long) exactMillis;

        // Carry fractional milliseconds down to picoseconds
        final double fractionalMillis = exactMillis - baseMillis;
        final long carriedPicos = (long) (fractionalMillis * FTimeUnit.PICOSECONDS_IN_MILLISECOND);

        final long totalPicos = (long) (picosValue() * multiplierPicos) + carriedPicos;

        final long finalMillis = baseMillis + FDatePicos.toMillisecondsOverflow(totalPicos);
        final int finalPicos = FDatePicos.toPicosWithoutOverflow(totalPicos);

        return new Duration(finalMillis, finalPicos, timeUnit);
    }

    /**
     * Creates a new duration derived from this one with the added duration in nanoseconds as timeunit.
     */
    public Duration add(final Duration duration) {
        if (duration == null) {
            return this;
        }
        return add(duration.millisValue(), duration.picosValue());
    }

    public Duration add(final long addendMillis, final int addendPicos) {
        final long picosMaybeOverflow = FDatePicos.addPicosecondsMaybeOverflow(picosValue(), addendPicos);
        final long millisOverflow = FDatePicos.toMillisecondsOverflow(picosMaybeOverflow);
        final int durationPicos = FDatePicos.toPicosWithoutOverflow(picosMaybeOverflow);
        final long durationMillis = millisValue() + addendMillis + millisOverflow;
        return new Duration(durationMillis, durationPicos, timeUnit);
    }

    public Duration subtract(final Duration duration) {
        if (duration == null) {
            return this;
        }
        return subtract(duration.millisValue(), duration.picosValue());
    }

    public Duration subtract(final long subtrahendMillis, final int subtrahendPicos) {
        final long picosMaybeOverflow = FDatePicos.addPicosecondsMaybeOverflow(picosValue(), -subtrahendPicos);
        final long millisOverflow = FDatePicos.toMillisecondsOverflow(picosMaybeOverflow);
        final int durationPicos = FDatePicos.toPicosWithoutOverflow(picosMaybeOverflow);
        final long durationMillis = millisValue() - subtrahendMillis + millisOverflow;
        return new Duration(durationMillis, durationPicos, timeUnit);
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
        return millisValue() == obj.millisValue() && picosValue() == obj.picosValue();
    }

    @Override
    public int hashCode() {
        return Long.hashCode(millisValue()) * 31 + Integer.hashCode(picosValue());
    }

    public FDate subtractFrom(final FDate date) {
        final long picosMaybeOverflow = FDatePicos.addPicosecondsMaybeOverflow(date.picosValue(), -picosValue());
        final long millisOverflow = FDatePicos.toMillisecondsOverflow(picosMaybeOverflow);
        final int durationPicos = FDatePicos.toPicosWithoutOverflow(picosMaybeOverflow);
        final long durationMillis = date.millisValue() - millisValue() + millisOverflow;
        return new FDate(durationMillis, durationPicos);
    }

    public FDate addTo(final FDate date) {
        final long picosMaybeOverflow = FDatePicos.addPicosecondsMaybeOverflow(date.picosValue(), picosValue());
        final long millisOverflow = FDatePicos.toMillisecondsOverflow(picosMaybeOverflow);
        final int durationPicos = FDatePicos.toPicosWithoutOverflow(picosMaybeOverflow);
        final long durationMillis = date.millisValue() + millisValue() + millisOverflow;
        return new FDate(durationMillis, durationPicos);
    }

    public long subtractFrom(final long millis) {
        return millis - millisValue();
    }

    public long addTo(final long millis) {
        return millis + millisValue();
    }

    public Duration abs() {
        return new Duration(Longs.abs(millis), Integers.abs(picos), timeUnit);
    }

    public boolean isExactMultipleOfPeriod(final Duration period) {
        if (period == null || period.isZero()) {
            return false;
        }
        if (this.isLessThan(period)) {
            return false;
        }
        if (this.equals(period)) {
            return true;
        }

        // 1. Safe-Guard: If either duration exceeds 106 days, bypass primitive math entirely.
        // This stops catastrophic wrap-arounds before they can trick our logic.
        final long thisMillis = this.millisValue();
        final long periodMillis = period.millisValue();
        if (thisMillis > MAX_SAFE_MILLIS || thisMillis < MIN_SAFE_MILLIS || periodMillis > MAX_SAFE_MILLIS
                || periodMillis < MIN_SAFE_MILLIS) {
            return isExactMultipleOfPeriodBigInterval(period);
        }

        // 2. Flatten the divisor (period) completely into picoseconds.
        // Guaranteed to fit perfectly in a long now (< 106 days)
        final long periodPicos = (periodMillis * FTimeUnit.PICOSECONDS_IN_MILLISECOND) + period.picosValue();

        // 3. Reduce components using modular arithmetic properties
        final long milliScaleMod = FTimeUnit.PICOSECONDS_IN_MILLISECOND % periodPicos;
        final long millisValueMod = thisMillis % periodPicos;

        // 4. Pre-emptive IF-check to avoid intermediate multiplication overflow
        if (milliScaleMod > 0 && (millisValueMod > Long.MAX_VALUE / milliScaleMod
                || millisValueMod < Long.MIN_VALUE / milliScaleMod)) {
            return isExactMultipleOfPeriodBigInterval(period);
        }

        // 5. Clean, allocation-free register math
        final long carriedPicosRemainder;
        try {
            carriedPicosRemainder = Longs.multiplyExact(millisValueMod, milliScaleMod) % periodPicos;
        } catch (final ArithmeticException overflow) {
            // High-protection fallback if the mid-way scalar multiplication clips the long boundary
            return isExactMultipleOfPeriodBigInterval(period);
        }
        final long totalRemainderPicos = (carriedPicosRemainder + this.picosValue()) % periodPicos;

        return totalRemainderPicos == 0;
    }

    /**
     * Allocation fallback strictly isolated for extreme macro intervals (e.g., centuries)
     */
    private boolean isExactMultipleOfPeriodBigInterval(final Duration period) {
        final BigInteger thisTotalPicos = BigInteger.valueOf(this.millisValue())
                .multiply(BigInteger.valueOf(FTimeUnit.PICOSECONDS_IN_MILLISECOND))
                .add(BigInteger.valueOf(this.picosValue()));
        final BigInteger periodTotalPicos = BigInteger.valueOf(period.millisValue())
                .multiply(BigInteger.valueOf(FTimeUnit.PICOSECONDS_IN_MILLISECOND))
                .add(BigInteger.valueOf(period.picosValue()));
        return thisTotalPicos.remainder(periodTotalPicos).equals(BigInteger.ZERO);
    }

    public double getNumMultipleOfPeriod(final Duration period) {
        if (period == null || period.isZero()) {
            return Double.NaN;
        }
        final double thisTotal = this.millisValue()
                + ((double) this.picosValue() / FTimeUnit.PICOSECONDS_IN_MILLISECOND);
        final double periodTotal = period.millisValue()
                + ((double) period.picosValue() / FTimeUnit.PICOSECONDS_IN_MILLISECOND);
        return thisTotal / periodTotal;
    }

    @Override
    public int compareTo(final Object o) {
        if (o == null || !(o instanceof Duration)) {
            return 1;
        }
        final Duration cO = (Duration) o;
        final int compareMillis = Long.compare(millisValue(), cO.millisValue());
        if (compareMillis != 0) {
            return compareMillis;
        }
        return Integer.compare(picosValue(), cO.picosValue());
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
        return millis == 0 && picos == 0;
    }

    public final boolean isNotZero() {
        return !isZero();
    }

    /**
     * Alias for isPositiveOrZero.
     */
    public boolean isPositive() {
        return isPositiveOrZero();
    }

    /**
     * 0 is counted as positive as well here to make things simpler.
     */
    public boolean isPositiveOrZero() {
        return millis > 0 || (millis == 0 && picos >= 0);
    }

    /**
     * This one excludes 0 from positive.
     */
    public boolean isPositiveNonZero() {
        return millis > 0 || (millis == 0 && picos > 0);
    }

    /**
     * Alias for isNegativeNonZero.
     */
    public boolean isNegative() {
        return isNegativeNonZero();
    }

    public boolean isNegativeNonZero() {
        return millis < 0 || (millis == 0 && picos < 0);
    }

    public boolean isNegativeOrZero() {
        return millis < 0 || (millis == 0 && picos <= 0);
    }

    public Duration negate() {
        return new Duration(-millis, -picos, timeUnit);
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
        return java.time.Duration.of(longValue(), timeUnit.javaTimeValue());
    }

    public org.joda.time.Duration jodaTimeValue() {
        return org.joda.time.Duration.millis(millisValue());
    }

    public static Duration valueOf(final FDate from, final FDate to) {
        if (from.equalsNotNullSafe(to)) {
            return ZERO;
        } else {
            return new Duration(from, to);
        }
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
        return longValue() + " " + timeUnit;
    }

    public void sleepNoInterrupt() {
        try {
            sleep();
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static Duration multiply(final Duration value1, final double value2) {
        if (value1 == null) {
            return null;
        } else {
            return value1.multiply(value2);
        }
    }

    public static Duration multiply(final Duration value1, final Double value2) {
        if (value1 == null) {
            return null;
        } else {
            return value1.multiply(value2);
        }
    }

    public static Duration multiply(final Duration value1, final Number value2) {
        if (value1 == null) {
            return null;
        } else {
            return value1.multiply(value2);
        }
    }

    public static Duration multiply(final Duration value1, final Duration value2) {
        if (value1 == null) {
            return null;
        } else {
            return value1.multiply(value2);
        }
    }

    public static Duration divide(final Duration value1, final double value2) {
        if (value1 == null) {
            return null;
        } else {
            return value1.divide(value2);
        }
    }

    public static Duration divide(final Duration value1, final Double value2) {
        if (value1 == null) {
            return null;
        } else {
            return value1.divide(value2);
        }
    }

    public static Duration divide(final Duration value1, final Number value2) {
        if (value1 == null) {
            return null;
        } else {
            return value1.divide(value2);
        }
    }

    public static Duration divide(final Duration value1, final Duration value2) {
        if (value1 == null) {
            return null;
        } else {
            return value1.divide(value2);
        }
    }

    public static Duration valueOfDifference(final long start, final long end, final FTimeUnit timeUnit) {
        final long difference = end - start;
        return new Duration(difference, timeUnit);
    }

    public static Duration valueOfDifferenceMillis(final long startMillis, final long endMillis) {
        final long differenceMillis = endMillis - startMillis;
        return new Duration(differenceMillis, 0, FTimeUnit.MILLISECONDS);
    }

}
