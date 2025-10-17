package de.invesdwin.util.time.range.week;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.lang.comparator.ACriteriaComparator;
import de.invesdwin.util.lang.comparator.IComparator;
import de.invesdwin.util.lang.string.Strings;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.FDates;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.date.FWeekday;
import de.invesdwin.util.time.date.millis.WeekAdjustment;
import de.invesdwin.util.time.duration.Duration;
import de.invesdwin.util.time.range.day.ADayTime;

@Immutable
public class FWeekTime extends ADayTime<FWeekTime> implements IWeekTimeData {

    public static final IComparator<FWeekTime> COMPARATOR = new ACriteriaComparator<FWeekTime>() {
        @Override
        public Comparable<?> getCompareCriteriaNotNullSafe(final FWeekTime e) {
            return e.longValue();
        }
    };

    private final FWeekday weekday;

    public FWeekTime(final FDate date) {
        this(date.getFWeekday(), date.getHour(), date.getMinute(), date.getSecond(), date.getMillisecond());
    }

    public FWeekTime(final FWeekday weekday, final int hour, final int minute, final int second,
            final int millisecond) {
        super(hour, minute, second, millisecond);
        if (weekday == null) {
            throw new NullPointerException("weekday should not be null");
        }
        this.weekday = weekday;
    }

    public FWeekday getFWeekday() {
        return weekday;
    }

    public int getWeekday() {
        return weekday.jodaTimeValue();
    }

    @Override
    public String toString() {
        return weekday.jodaTimeValue() + "T" + innerToString();
    }

    @Override
    public String toNumberString() {
        return weekday.jodaTimeValue() + innerToNumberString();
    }

    @Override
    public int compareTo(final Object o) {
        return COMPARATOR.compare(this, o);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof FWeekTime) {
            final FWeekTime cObj = (FWeekTime) obj;
            return equalsNotNullSafe(cObj);
        } else {
            return false;
        }
    }

    @Override
    public boolean equalsNotNullSafe(final FWeekTime obj) {
        return Objects.equals(obj.weekday, weekday) && innerEqualsNotNullSafe(obj);
    }

    @Override
    public boolean isBefore(final FWeekTime other) {
        return other != null && longValue() < other.longValue();
    }

    @Override
    public boolean isBeforeOrEqualTo(final FWeekTime other) {
        return other != null && longValue() <= other.longValue();
    }

    @Override
    public boolean isAfter(final FWeekTime other) {
        return other != null && longValue() > other.longValue();
    }

    @Override
    public boolean isAfterOrEqualTo(final FWeekTime other) {
        return other != null && longValue() >= other.longValue();
    }

    @Override
    public boolean isBeforeNotNullSafe(final FWeekTime other) {
        return longValue() < other.longValue();
    }

    @Override
    public boolean isBeforeOrEqualToNotNullSafe(final FWeekTime other) {
        return longValue() <= other.longValue();
    }

    @Override
    public boolean isAfterNotNullSafe(final FWeekTime other) {
        return longValue() > other.longValue();
    }

    @Override
    public boolean isAfterOrEqualToNotNullSafe(final FWeekTime other) {
        return longValue() >= other.longValue();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(FWeekTime.class, weekday, innerHashCode());
    }

    @Deprecated
    @Override
    public int intValue() {
        throw new UnsupportedOperationException("value does not fit into Integer, use longValue() instead");
    }

    /**
     * WhhmmssSSS
     */
    @Override
    public long longValue() {
        //weekday * 1_hh_mm_ss_SSS + hour * 1_mm_ss_SSS + minute * 1_ss_SSS + second * 1_SSS + millisecond
        return weekday.jodaTimeValue() * 1_00_00_00_000 + innerIntValue();
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

    private static FWeekTime valueOfNumeric(final String value, final boolean max) {
        try {
            final int length = value.length();
            if (length != 1 && length != 3 && length != 5 && length != 7 && length != 10) {
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

    public static FWeekTime valueOf(final IWeekTimeData value) {
        if (value == null) {
            return null;
        } else if (value instanceof FWeekTime) {
            return (FWeekTime) value;
        } else {
            return valueOf(value.longValue(), false);
        }
    }

    public static FWeekTime valueOf(final Duration value) {
        final FWeekday weekDay = FWeekday.valueOfDuration(value);
        return new FWeekTime(
                FDates.getDefaultTimeZone().getMinDate().setFWeekday(weekDay, WeekAdjustment.UNADJUSTED).add(value));
    }

    public static FWeekTime valueOf(final FDate value) {
        if (value == null) {
            return null;
        } else {
            return new FWeekTime(value);
        }
    }

    @Override
    public Duration durationValue() {
        return weekday.durationValue()
                .add(hour, FTimeUnit.HOURS)
                .add(minute, FTimeUnit.MINUTES)
                .add(second, FTimeUnit.SECONDS)
                .add(millisecond, FTimeUnit.MILLISECONDS);
    }

    @Override
    public FWeekTime subtract(final Duration duration) {
        return valueOf(durationValue().subtract(duration));
    }

    @Override
    public FWeekTime add(final Duration duration) {
        return valueOf(durationValue().add(duration));
    }

}
