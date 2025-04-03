package de.invesdwin.util.time.date;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.lang.comparator.ACriteriaComparator;
import de.invesdwin.util.lang.comparator.IComparator;
import de.invesdwin.util.lang.string.Strings;
import de.invesdwin.util.math.Bytes;
import de.invesdwin.util.math.Shorts;
import de.invesdwin.util.time.duration.Duration;
import de.invesdwin.util.time.range.day.IDayTimeData;

@Immutable
public class FDayTime extends Number implements Comparable<Object>, IDayTimeData {

    public static final IComparator<FDayTime> COMPARATOR = new ACriteriaComparator<FDayTime>() {
        @Override
        public Comparable<?> getCompareCriteriaNotNullSafe(final FDayTime e) {
            return e.intValue();
        }
    };

    public static final int MIN = 0;
    public static final int MAX_HOUR = 23;
    public static final int MAX_MINUTE = 59;
    public static final int MAX_SECOND = 59;
    public static final int MAX_MILLISECOND = 999;

    public static final FDayTime MIN_DAY_TIME = new FDayTime(MIN, MIN, MIN, MIN);
    public static final FDayTime MAX_DAY_TIME = new FDayTime(MAX_HOUR, MAX_MINUTE, MAX_SECOND, MAX_MILLISECOND);

    private final byte hour;
    private final byte minute;
    private final byte second;
    private final short millisecond;

    private transient Integer cachedIntValue;

    public FDayTime(final FDate date) {
        this(date.getHour(), date.getMinute(), date.getSecond(), date.getMillisecond());
    }

    public FDayTime(final int hour, final int minute, final int second, final int millisecond) {
        if (hour < MIN || hour > MAX_HOUR) {
            throw new IllegalArgumentException("hour should be between [" + MIN + "] and [" + MAX_HOUR + "]: " + hour);
        }
        if (minute < MIN || minute > MAX_MINUTE) {
            throw new IllegalArgumentException(
                    "minute should be between [" + MIN + "] and [" + MAX_MINUTE + "]: " + minute);
        }
        if (second < MIN || second > MAX_SECOND) {
            throw new IllegalArgumentException(
                    "second should be between [" + MIN + "] and [" + MAX_SECOND + "]: " + second);
        }
        if (millisecond < MIN || millisecond > MAX_MILLISECOND) {
            throw new IllegalArgumentException(
                    "millisecond should be between [" + MIN + "] and [" + MAX_MILLISECOND + "]: " + millisecond);
        }
        this.hour = Bytes.checkedCast(hour);
        this.minute = Bytes.checkedCast(minute);
        this.second = Bytes.checkedCast(second);
        this.millisecond = Shorts.checkedCast(millisecond);
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public byte getSecond() {
        return second;
    }

    public short getMillisecond() {
        return millisecond;
    }

    @Override
    public String toString() {
        return Strings.leftPad(hour, 2, '0') + ":" + Strings.leftPad(minute, 2, '0') + ":"
                + Strings.leftPad(second, 2, '0') + "." + Strings.leftPad(millisecond, 3, '0');
    }

    public String toNumberString() {
        return Strings.leftPad(hour, 2, '0') + Strings.leftPad(minute, 2, '0') + Strings.leftPad(second, 2, '0')
                + Strings.leftPad(millisecond, 3, '0');
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

    public boolean equalsNotNullSafe(final FDayTime obj) {
        return Objects.equals(obj.hour, hour) && Objects.equals(obj.minute, minute)
                && Objects.equals(obj.second, second) && Objects.equals(obj.millisecond, millisecond);
    }

    public boolean isBefore(final FDayTime other) {
        return other != null && intValue() < other.intValue();
    }

    public boolean isBeforeOrEqualTo(final FDayTime other) {
        return other != null && intValue() <= other.intValue();
    }

    public boolean isAfter(final FDayTime other) {
        return other != null && intValue() > other.intValue();
    }

    public boolean isAfterOrEqualTo(final FDayTime other) {
        return other != null && intValue() >= other.intValue();
    }

    public boolean isBeforeNotNullSafe(final FDayTime other) {
        return intValue() < other.intValue();
    }

    public boolean isBeforeOrEqualToNotNullSafe(final FDayTime other) {
        return intValue() <= other.intValue();
    }

    public boolean isAfterNotNullSafe(final FDayTime other) {
        return intValue() > other.intValue();
    }

    public boolean isAfterOrEqualToNotNullSafe(final FDayTime other) {
        return intValue() >= other.intValue();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(FDayTime.class, hour, minute, second, millisecond);
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

    public Duration durationValue() {
        return new Duration(hour, FTimeUnit.HOURS).add(minute, FTimeUnit.MINUTES)
                .add(second, FTimeUnit.SECONDS)
                .add(millisecond, FTimeUnit.MILLISECONDS);
    }

    public FDayTime subtract(final Duration duration) {
        return valueOf(durationValue().subtract(duration));
    }

    public FDayTime add(final Duration duration) {
        return valueOf(durationValue().add(duration));
    }

}
