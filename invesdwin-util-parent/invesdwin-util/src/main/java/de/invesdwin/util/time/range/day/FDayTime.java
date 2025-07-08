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
            return e.intValue();
        }
    };

    public static final FDayTime MIN_DAY_TIME = new FDayTime(MIN, MIN, MIN, MIN);
    public static final FDayTime MAX_DAY_TIME = new FDayTime(MAX_HOUR, MAX_MINUTE, MAX_SECOND, MAX_MILLISECOND);

    private transient Integer cachedIntValue;

    public FDayTime(final FDate date) {
        this(date.getHour(), date.getMinute(), date.getSecond(), date.getMillisecond());
    }

    public FDayTime(final int hour, final int minute, final int second, final int millisecond) {
        super(hour, minute, second, millisecond);
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
        return other != null && intValue() < other.intValue();
    }

    @Override
    public boolean isBeforeOrEqualTo(final FDayTime other) {
        return other != null && intValue() <= other.intValue();
    }

    @Override
    public boolean isAfter(final FDayTime other) {
        return other != null && intValue() > other.intValue();
    }

    @Override
    public boolean isAfterOrEqualTo(final FDayTime other) {
        return other != null && intValue() >= other.intValue();
    }

    @Override
    public boolean isBeforeNotNullSafe(final FDayTime other) {
        return intValue() < other.intValue();
    }

    @Override
    public boolean isBeforeOrEqualToNotNullSafe(final FDayTime other) {
        return intValue() <= other.intValue();
    }

    @Override
    public boolean isAfterNotNullSafe(final FDayTime other) {
        return intValue() > other.intValue();
    }

    @Override
    public boolean isAfterOrEqualToNotNullSafe(final FDayTime other) {
        return intValue() >= other.intValue();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(FDayTime.class, innerHashCode());
    }

    @Override
    public int intValue() {
        if (cachedIntValue == null) {
            final String concatNumber = toNumberString();
            cachedIntValue = Integer.valueOf(concatNumber);
        }
        return cachedIntValue;
    }

    @Override
    public long longValue() {
        return intValue();
    }

    @Override
    public float floatValue() {
        return intValue();
    }

    @Override
    public double doubleValue() {
        return intValue();
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
            if (length == 2 || length == 4 || length == 6 || length == 9) {
                final int hour = Integer.parseInt(value.substring(0, 2));
                final int minute;
                if (length > 2) {
                    minute = Integer.parseInt(value.substring(2, 4));
                } else {
                    if (max) {
                        minute = 59;
                    } else {
                        minute = 0;
                    }
                }
                final int second;
                if (length > 4) {
                    second = Integer.parseInt(value.substring(4, 6));
                } else {
                    if (max) {
                        second = 59;
                    } else {
                        second = 0;
                    }
                }
                final int millisecond;
                if (length > 6) {
                    millisecond = Integer.parseInt(value.substring(6, 9));
                } else {
                    if (max) {
                        millisecond = 999;
                    } else {
                        millisecond = 0;
                    }
                }
                return new FDayTime(hour, minute, second, millisecond);
            } else if (length == 1 || length == 3 || length == 5 || length == 8) {
                final int hour = Integer.parseInt(value.substring(0, 1));
                final int minute;
                if (length > 1) {
                    minute = Integer.parseInt(value.substring(1, 3));
                } else {
                    if (max) {
                        minute = 59;
                    } else {
                        minute = 0;
                    }
                }
                final int second;
                if (length > 3) {
                    second = Integer.parseInt(value.substring(3, 5));
                } else {
                    if (max) {
                        second = 59;
                    } else {
                        second = 0;
                    }
                }
                final int millisecond;
                if (length > 5) {
                    millisecond = Integer.parseInt(value.substring(5, 8));
                } else {
                    if (max) {
                        millisecond = 999;
                    } else {
                        millisecond = 0;
                    }
                }
                return new FDayTime(hour, minute, second, millisecond);
            } else {
                throw new IllegalArgumentException("Expecting between 1 to 9 characters but got " + length);
            }
        } catch (final Throwable t) {
            throw new RuntimeException("Expected format H[0-9] or HH[0-23]MM[0-59]SS[0-59]SSS[0-999] at: " + value, t);
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
            return valueOf(value.intValue(), false);
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
                .add(millisecond, FTimeUnit.MILLISECONDS);
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
