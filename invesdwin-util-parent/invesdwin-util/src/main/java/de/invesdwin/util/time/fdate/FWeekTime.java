package de.invesdwin.util.time.fdate;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.ADelegateComparator;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.lang.Strings;
import de.invesdwin.util.math.Bytes;
import de.invesdwin.util.math.Shorts;

@Immutable
public class FWeekTime extends Number implements Comparable<Object> {

    public static final ADelegateComparator<FWeekTime> COMPARATOR = new ADelegateComparator<FWeekTime>() {
        @Override
        protected Comparable<?> getCompareCriteria(final FWeekTime e) {
            return e.longValue();
        }
    };

    public static final int MIN = 0;
    public static final int MAX_HOUR = 24;
    public static final int MAX_MINUTE = 59;
    public static final int MAX_SECOND = 59;
    public static final int MAX_MILLISECOND = 999;

    private final FWeekday weekday;
    private final byte hour;
    private final byte minute;
    private final byte second;
    private final short millisecond;

    public FWeekTime(final FDate date) {
        this(date.getFWeekday(), date.getHour(), date.getMinute(), date.getSecond(), date.getMillisecond());
    }

    public FWeekTime(final FWeekday weekday, final int hour, final int minute, final int second,
            final int millisecond) {
        if (weekday == null) {
            throw new NullPointerException("weekday should not be null");
        }
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
        this.weekday = weekday;
        this.hour = Bytes.checkedCast(hour);
        this.minute = Bytes.checkedCast(minute);
        this.second = Bytes.checkedCast(second);
        this.millisecond = Shorts.checkedCast(millisecond);
    }

    public FWeekday getFWeekday() {
        return weekday;
    }

    public int getWeekday() {
        return weekday.jodaTimeValue();
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
        return weekday.jodaTimeValue() + "T" + Strings.leftPad(hour, 2, '0') + ":" + Strings.leftPad(minute, 2, '0')
                + ":" + Strings.leftPad(second, 2, '0') + "." + Strings.leftPad(millisecond, 3, '0');
    }

    @Override
    public int compareTo(final Object o) {
        return COMPARATOR.compare(this, o);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof FWeekTime) {
            final FWeekTime cObj = (FWeekTime) obj;
            return Objects.equals(cObj.weekday, weekday) && Objects.equals(cObj.hour, hour)
                    && Objects.equals(cObj.minute, minute) && Objects.equals(cObj.second, second)
                    && Objects.equals(cObj.millisecond, millisecond);
        } else {
            return false;
        }
    }

    public boolean isBefore(final FWeekTime other) {
        return other != null && compareTo(other) < 0;
    }

    public boolean isBeforeOrEqualTo(final FWeekTime other) {
        return other != null && !isAfter(other);
    }

    public boolean isAfter(final FWeekTime other) {
        return other != null && compareTo(other) > 0;
    }

    public boolean isAfterOrEqualTo(final FWeekTime other) {
        return other != null && !isBefore(other);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(FWeekTime.class, weekday, hour, minute, second, millisecond);
    }

    @Deprecated
    @Override
    public int intValue() {
        throw new UnsupportedOperationException("value does not fit into Integer, use longValue() instead");
    }

    @Override
    public long longValue() {
        final String concatNumber = weekday.jodaTimeValue() + Strings.leftPad(hour, 2, '0')
                + Strings.leftPad(minute, 2, '0') + Strings.leftPad(second, 2, '0')
                + Strings.leftPad(millisecond, 3, '0');
        return Long.parseLong(concatNumber);
    }

    @Override
    public float floatValue() {
        return longValue();
    }

    @Override
    public double doubleValue() {
        return longValue();
    }

    public static FWeekTime valueOf(final String value, final boolean max) {
        if (Strings.isNumeric(value)) {
            return valueOfNumeric(value, max);
        } else {
            return valueOfNumeric(value.replace("T", "").replace(":", "").replace(".", ""), max);
        }
    }

    //CHECKSTYLE:OFF
    private static FWeekTime valueOfNumeric(final String value, final boolean max) {
        //CHECKSTYLE:ON
        try {
            final int length = value.length();
            if (length != 1 && length != 3 && length != 10) {
                throw new IllegalArgumentException("Expecting between 1, 3, 5, 7 or 10 characters but got " + length);
            }
            final FWeekday weekday = FWeekday.valueOfJodaTime(Integer.parseInt(value.substring(0, 1)));
            final int hour;
            if (length > 1) {
                hour = Integer.parseInt(value.substring(1, 3));
            } else {
                if (max) {
                    hour = 23;
                } else {
                    hour = 0;
                }
            }
            final int minute;
            if (length > 3) {
                minute = Integer.parseInt(value.substring(3, 5));
            } else {
                if (max) {
                    minute = 59;
                } else {
                    minute = 0;
                }
            }
            final int second;
            if (length > 5) {
                second = Integer.parseInt(value.substring(5, 7));
            } else {
                if (max) {
                    second = 59;
                } else {
                    second = 0;
                }
            }
            final int millisecond;
            if (length > 7) {
                millisecond = Integer.parseInt(value.substring(7, 10));
            } else {
                if (max) {
                    millisecond = 999;
                } else {
                    millisecond = 0;
                }
            }
            return new FWeekTime(weekday, hour, minute, second, millisecond);
        } catch (final Throwable t) {
            throw new RuntimeException("Expected format D[1-7]HH[0-23]MM[0-59]SS[0-59]SSS[0-999] at: " + value, t);
        }
    }

    public static FWeekTime valueOf(final long value, final boolean max) {
        return valueOfNumeric(String.valueOf(value), max);
    }

}
