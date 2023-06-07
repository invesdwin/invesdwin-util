package de.invesdwin.util.time.date;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.lang.comparator.ACriteriaComparator;
import de.invesdwin.util.lang.comparator.IComparator;
import de.invesdwin.util.lang.string.Strings;

@Immutable
public class FWeekTime extends FDayTime {

    public static final IComparator<FWeekTime> COMPARATOR = new ACriteriaComparator<FWeekTime>() {
        @Override
        public Comparable<?> getCompareCriteriaNotNullSafe(final FWeekTime e) {
            return e.longValue();
        }
    };

    private final FWeekday weekday;

    private transient Long cachedLongValue;

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
        return weekday.jodaTimeValue() + "T" + super.toString();
    }

    @Override
    public String toNumberString() {
        return weekday.jodaTimeValue() + super.toNumberString();
    }

    @Override
    public int compareTo(final Object o) {
        return COMPARATOR.compare(this, o);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof FWeekTime) {
            final FWeekTime cObj = (FWeekTime) obj;
            return Objects.equals(cObj.weekday, weekday) && super.equals(obj);
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
        return Objects.hashCode(FWeekTime.class, weekday, super.hashCode());
    }

    @Deprecated
    @Override
    public int intValue() {
        throw new UnsupportedOperationException("value does not fit into Integer, use longValue() instead");
    }

    @Override
    public long longValue() {
        if (cachedLongValue == null) {
            final String concatNumber = toNumberString();
            cachedLongValue = Long.valueOf(concatNumber);
        }
        return cachedLongValue;
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

}
