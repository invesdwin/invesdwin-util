package de.invesdwin.util.time.range.day;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.lang.string.Strings;
import de.invesdwin.util.math.Bytes;
import de.invesdwin.util.math.Shorts;
import de.invesdwin.util.time.duration.Duration;

@Immutable
public abstract class ADayTime<E extends ADayTime<E>> extends Number implements Comparable<Object> {

    public static final int MIN = 0;
    public static final int MAX_HOUR = 23;
    public static final int MAX_MINUTE = 59;
    public static final int MAX_SECOND = 59;
    public static final int MAX_MILLISECOND = 999;

    protected final byte hour;
    protected final byte minute;
    protected final byte second;
    protected final short millisecond;

    protected ADayTime(final int hour, final int minute, final int second, final int millisecond) {
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
    public abstract String toString();

    protected String innerToString() {
        return Strings.leftPad(hour, 2, '0') + ":" + Strings.leftPad(minute, 2, '0') + ":"
                + Strings.leftPad(second, 2, '0') + "." + Strings.leftPad(millisecond, 3, '0');
    }

    public abstract String toNumberString();

    protected String innerToNumberString() {
        return Strings.leftPad(hour, 2, '0') + Strings.leftPad(minute, 2, '0') + Strings.leftPad(second, 2, '0')
                + Strings.leftPad(millisecond, 3, '0');
    }

    /**
     * hhmmssSSS
     */
    protected int innerIntValue() {
        //hour * 1_mm_ss_SSS + minute * 1_ss_SSS + second * 1_SSS + millisecond
        return getHour() * 1_00_00_000 + getMinute() * 1_00_000 + getSecond() * 1_000 + getMillisecond();
    }

    @Override
    public abstract int compareTo(Object o);

    @Override
    public abstract boolean equals(Object obj);

    public abstract boolean equalsNotNullSafe(E obj);

    protected boolean innerEqualsNotNullSafe(final ADayTime<E> obj) {
        return Objects.equals(obj.hour, hour) && Objects.equals(obj.minute, minute)
                && Objects.equals(obj.second, second) && Objects.equals(obj.millisecond, millisecond);
    }

    public abstract boolean isBefore(E other);

    public abstract boolean isBeforeOrEqualTo(E other);

    public abstract boolean isAfter(E other);

    public abstract boolean isAfterOrEqualTo(E other);

    public abstract boolean isBeforeNotNullSafe(E other);

    public abstract boolean isBeforeOrEqualToNotNullSafe(E other);

    public abstract boolean isAfterNotNullSafe(E other);

    public abstract boolean isAfterOrEqualToNotNullSafe(E other);

    @Override
    public abstract int hashCode();

    protected int innerHashCode() {
        return Objects.hashCode(hour, minute, second, millisecond);
    }

    public abstract Duration durationValue();

    public abstract E subtract(Duration duration);

    public abstract E add(Duration duration);

}
