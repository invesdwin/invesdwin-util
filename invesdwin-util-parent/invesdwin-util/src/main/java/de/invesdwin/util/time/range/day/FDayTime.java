package de.invesdwin.util.time.range.day;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.lang.comparator.ACriteriaComparator;
import de.invesdwin.util.lang.comparator.IComparator;
import de.invesdwin.util.lang.string.Strings;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.FDates;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.Duration;

@Immutable
public class FDayTime extends ADayTime<FDayTime> implements IDayTimeData {

    public static final IComparator<FDayTime> COMPARATOR = new ACriteriaComparator<FDayTime>() {
        @Override
        public Comparable<?> getCompareCriteriaNotNullSafe(final FDayTime e) {
            return e.longValue();
        }
    };

    public static final FDayTime MIN_DAY_TIME = new FDayTime(MIN, MIN, MIN, MIN, MIN, MIN, MIN);
    public static final FDayTime MAX_DAY_TIME = new FDayTime(MAX_HOUR, MAX_MINUTE, MAX_SECOND, MAX_MILLISECOND,
            MAX_MICROSECOND, MAX_NANOSECOND, MAX_PICOSECOND);

    public FDayTime(final FDate date) {
        this(date.getHour(), date.getMinute(), date.getSecond(), date.getMillisecond(), date.getMicrosecond(),
                date.getNanosecond(), date.getPicosecond());
    }

    public FDayTime(final int hour, final int minute, final int second, final int millisecond, final int microsecond,
            final int nanosecond, final int picosecond) {
        super(hour, minute, second, millisecond, microsecond, nanosecond, picosecond);
    }

    @Override
    public String toString() {
        return innerToString();
    }

    @Override
    public String toNumberString() {
        return innerToNumberString();
    }

    @Override
    public int compareTo(final Object o) {
        return COMPARATOR.compare(this, o);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof FDayTime) {
            final FDayTime cObj = (FDayTime) obj;
            return equalsNotNullSafe(cObj);
        } else {
            return false;
        }
    }

    @Override
    public boolean equalsNotNullSafe(final FDayTime obj) {
        return innerEqualsNotNullSafe(obj);
    }

    @Override
    public boolean isBefore(final FDayTime other) {
        return other != null && longValue() < other.longValue();
    }

    @Override
    public boolean isBeforeOrEqualTo(final FDayTime other) {
        return other != null && longValue() <= other.longValue();
    }

    @Override
    public boolean isAfter(final FDayTime other) {
        return other != null && longValue() > other.longValue();
    }

    @Override
    public boolean isAfterOrEqualTo(final FDayTime other) {
        return other != null && longValue() >= other.longValue();
    }

    @Override
    public boolean isBeforeNotNullSafe(final FDayTime other) {
        return longValue() < other.longValue();
    }

    @Override
    public boolean isBeforeOrEqualToNotNullSafe(final FDayTime other) {
        return longValue() <= other.longValue();
    }

    @Override
    public boolean isAfterNotNullSafe(final FDayTime other) {
        return longValue() > other.longValue();
    }

    @Override
    public boolean isAfterOrEqualToNotNullSafe(final FDayTime other) {
        return longValue() >= other.longValue();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(FDayTime.class, innerHashCode());
    }

    @Deprecated
    @Override
    public int intValue() {
        throw new UnsupportedOperationException("value does not fit into Integer, use longValue() instead");
    }

    @Override
    public long longValue() {
        return innerLongValue();
    }

    @Override
    public float floatValue() {
        return longValue();
    }

    @Override
    public double doubleValue() {
        return longValue();
    }

    public static FDayTime valueOf(final String value, final boolean max) {
        if (Strings.isNumeric(value)) {
            return valueOfNumeric(value, max);
        } else {
            return valueOfNumeric(value.replace(":", "").replace(".", ""), max);
        }
    }

    private static FDayTime valueOfNumeric(final String value, final boolean max) {
        try {
            final int length = value.length();
            if (isValidLengthEven(length)) {
                final int hour = parseField(value, 0, 2, length, MAX_HOUR, max);
                final int minute = parseField(value, 2, 4, length, MAX_MINUTE, max);
                final int second = parseField(value, 4, 6, length, MAX_SECOND, max);
                final int millisecond = parseField(value, 6, 9, length, MAX_MILLISECOND, max);
                final int microsecond = parseField(value, 9, 12, length, MAX_MICROSECOND, max);
                final int nanosecond = parseField(value, 12, 15, length, MAX_NANOSECOND, max);
                final int picosecond = parseField(value, 15, 18, length, MAX_PICOSECOND, max);
                return new FDayTime(hour, minute, second, millisecond, microsecond, nanosecond, picosecond);
            } else if (isValidLengthOdd(length)) {
                final int hour = parseField(value, 0, 1, length, MAX_HOUR, max);
                final int minute = parseField(value, 1, 3, length, MAX_MINUTE, max);
                final int second = parseField(value, 3, 5, length, MAX_SECOND, max);
                final int millisecond = parseField(value, 5, 8, length, MAX_MILLISECOND, max);
                final int microsecond = parseField(value, 8, 11, length, MAX_MICROSECOND, max);
                final int nanosecond = parseField(value, 11, 14, length, MAX_NANOSECOND, max);
                final int picosecond = parseField(value, 14, 17, length, MAX_PICOSECOND, max);
                return new FDayTime(hour, minute, second, millisecond, microsecond, nanosecond, picosecond);
            } else {
                throw new IllegalArgumentException("Expecting between 1 to 18 characters but got " + length);
            }
        } catch (final Throwable t) {
            throw new RuntimeException(
                    "Expected format H[0-9] or HH[0-23]MM[0-59]SS[0-59]SSS[0-999]UUU[0-999]NNN[0-999]PPP[0-999] at: "
                            + value,
                    t);
        }
    }

    private static boolean isValidLengthOdd(final int length) {
        switch (length) {
        case 1:
        case 3:
        case 5:
        case 8:
        case 11:
        case 14:
        case 17:
            return true;
        default:
            return false;
        }
    }

    private static boolean isValidLengthEven(final int length) {
        switch (length) {
        case 2:
        case 4:
        case 6:
        case 9:
        case 12:
        case 15:
        case 18:
            return true;
        default:
            return false;
        }
    }

    public static FDayTime valueOf(final int value, final boolean max) {
        return valueOfNumeric(String.valueOf(value), max);
    }

    public static FDayTime valueOf(final long value, final boolean max) {
        return valueOfNumeric(String.valueOf(value), max);
    }

    public static FDayTime valueOf(final IDayTimeData value) {
        if (value == null) {
            return null;
        } else if (value instanceof FDayTime) {
            return (FDayTime) value;
        } else {
            return valueOf(value.longValue(), false);
        }
    }

    public static FDayTime valueOf(final Duration value) {
        return new FDayTime(FDates.getDefaultTimeZone().getMinDate().add(value));
    }

    public static FDayTime valueOf(final FDate value) {
        if (value == null) {
            return null;
        } else {
            return new FDayTime(value);
        }
    }

    @Override
    public Duration durationValue() {
        return new Duration(hour, FTimeUnit.HOURS).add(minute, FTimeUnit.MINUTES)
                .add(second, FTimeUnit.SECONDS)
                .add(millisecond, FTimeUnit.MILLISECONDS)
                .add(microsecond, FTimeUnit.MICROSECONDS)
                .add(nanosecond, FTimeUnit.NANOSECONDS)
                .add(picosecond, FTimeUnit.PICOSECONDS);
    }

    @Override
    public FDayTime subtract(final Duration duration) {
        return valueOf(durationValue().subtract(duration));
    }

    @Override
    public FDayTime add(final Duration duration) {
        return valueOf(durationValue().add(duration));
    }

}
