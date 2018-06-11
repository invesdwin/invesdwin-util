package de.invesdwin.util.time.fdate;

import java.util.TimeZone;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.bean.AValueObject;
import de.invesdwin.util.lang.ADelegateComparator;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.lang.Strings;
import de.invesdwin.util.math.Bytes;

@Immutable
public class FWeekTime extends AValueObject {

    public static final ADelegateComparator<FWeekTime> COMPARATOR = new ADelegateComparator<FWeekTime>() {
        @Override
        protected Comparable<?> getCompareCriteria(final FWeekTime e) {
            return Integer.parseInt(e.toString());
        }
    };

    private static final int MIN = 0;
    private static final int MAX_HOUR = 24;
    private static final int MAX_MINUTE = 59;
    private final FWeekday weekday;
    private final byte hour;
    private final byte minute;

    public FWeekTime(final FWeekday weekday, final int hour, final int minute, final TimeZone timeZone) {
        this(new FDateBuilder().withTimeZone(timeZone)
                .withYears(FDate.MIN_YEAR)
                .withFWeekday(weekday)
                .withHours(hour)
                .withMinutes(minute)
                .get());
    }

    public FWeekTime(final FDate date) {
        this(date.getFWeekday(), Bytes.checkedCast(date.getHour()), Bytes.checkedCast(date.getMinute()));
    }

    public FWeekTime(final FWeekday weekday, final int hour, final int minute) {
        if (weekday == null) {
            throw new NullPointerException("weekday should not be null");
        }
        if (hour < MIN || hour > MAX_HOUR) {
            throw new IllegalArgumentException("hour should be between [" + MIN + "] and [" + MAX_HOUR + "]: " + hour);
        }
        if (minute < MIN || minute > MAX_MINUTE) {
            throw new IllegalArgumentException(
                    "minute should be between [" + MIN + "] and [" + MAX_HOUR + "]: " + hour);
        }
        this.weekday = weekday;
        this.hour = Bytes.checkedCast(hour);
        this.minute = Bytes.checkedCast(minute);
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

    @Override
    public String toString() {
        return weekday.jodaTimeValue() + Strings.leftPad(hour, 2, '0') + Strings.leftPad(minute, 2, '0');
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
                    && Objects.equals(cObj.minute, minute);
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
        return Objects.hashCode(FWeekTime.class, weekday, hour, minute);
    }

}
