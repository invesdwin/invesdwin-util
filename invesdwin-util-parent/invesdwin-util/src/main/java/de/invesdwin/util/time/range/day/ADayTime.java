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
    public static final int MAX_MICROSECOND = 999;
    public static final int MAX_NANOSECOND = 999;
    public static final int MAX_PICOSECOND = 999;

    protected final byte hour;
    protected final byte minute;
    protected final byte second;
    protected final short millisecond;
    protected final short microsecond;
    protected final short nanosecond;
    protected final short picosecond;

    protected ADayTime(final int hour, final int minute, final int second, final int millisecond, final int microsecond,
            final int nanosecond, final int picosecond) {
        assertValidField("hour", hour, MIN, MAX_HOUR);
        assertValidField("minute", minute, MIN, MAX_MINUTE);
        assertValidField("second", second, MIN, MAX_SECOND);
        assertValidField("millisecond", millisecond, MIN, MAX_MILLISECOND);
        assertValidField("microsecond", microsecond, MIN, MAX_MICROSECOND);
        assertValidField("nanosecond", nanosecond, MIN, MAX_NANOSECOND);
        assertValidField("picosecond", picosecond, MIN, MAX_PICOSECOND);
        this.hour = Bytes.checkedCast(hour);
        this.minute = Bytes.checkedCast(minute);
        this.second = Bytes.checkedCast(second);
        this.millisecond = Shorts.checkedCast(millisecond);
        this.microsecond = Shorts.checkedCast(microsecond);
        this.nanosecond = Shorts.checkedCast(nanosecond);
        this.picosecond = Shorts.checkedCast(picosecond);
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

    public short getMicrosecond() {
        return microsecond;
    }

    public short getNanosecond() {
        return nanosecond;
    }

    public short getPicosecond() {
        return picosecond;
    }

    @Override
    public abstract String toString();

    protected String innerToString() {
        return Strings.leftPad(hour, 2, '0') + ":" + Strings.leftPad(minute, 2, '0') + ":"
                + Strings.leftPad(second, 2, '0') + "." + Strings.leftPad(millisecond, 3, '0') + "."
                + Strings.leftPad(microsecond, 3, '0') + "." + Strings.leftPad(nanosecond, 3, '0') + "."
                + Strings.leftPad(picosecond, 3, '0');
    }

    public abstract String toNumberString();

    protected String innerToNumberString() {
        return Strings.leftPad(hour, 2, '0') + Strings.leftPad(minute, 2, '0') + Strings.leftPad(second, 2, '0')
                + Strings.leftPad(millisecond, 3, '0') + Strings.leftPad(microsecond, 3, '0')
                + Strings.leftPad(nanosecond, 3, '0') + Strings.leftPad(picosecond, 3, '0');
    }

    /**
     * hhmmssSSSUUUNNNPPP
     * 
     * hour * 1_mm_ss_SSS_UUU_NNN_PPP + minute * 1_ss_SSS_UUU_NNN_PPP + second * 1_SSS_UUU_NNN_PPP + millisecond *
     * 1_UUU_NNN_PPP + microsecond * 1_NNN_PPP + nanosecond * 1_PPP + picosecond
     */
    protected long innerLongValue() {
        return getHour() * 1_00_00_000_000_000_000L + getMinute() * 1_00_000_000_000_000L
                + getSecond() * 1_000_000_000_000L + getMillisecond() * 1_000_000_000L + getMicrosecond() * 1_000_000L
                + getNanosecond() * 1_000L + getPicosecond();
    }

    @Override
    public abstract int compareTo(Object o);

    @Override
    public abstract boolean equals(Object obj);

    public abstract boolean equalsNotNullSafe(E obj);

    protected boolean innerEqualsNotNullSafe(final ADayTime<E> obj) {
        //CHECKSTYLE:OFF
        return Objects.equals(obj.hour, hour) && Objects.equals(obj.minute, minute)
                && Objects.equals(obj.second, second) && Objects.equals(obj.millisecond, millisecond)
                && Objects.equals(obj.microsecond, microsecond) && Objects.equals(obj.nanosecond, nanosecond)
                && Objects.equals(obj.picosecond, picosecond);
        //CHECKSTYLE:ON
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

    protected static void assertValidField(final String name, final int value, final int min, final int max) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(name + " should be between [" + min + "] and [" + max + "]: " + value);
        }
    }

    protected static int parseField(final String value, final int startIndex, final int endIndex, final int length,
            final int maxValue, final boolean max) {
        if (length > startIndex) {
            return Integer.parseInt(value.substring(startIndex, endIndex));
        } else {
            if (max) {
                return maxValue;
            } else {
                return 0;
            }
        }
    }

}
