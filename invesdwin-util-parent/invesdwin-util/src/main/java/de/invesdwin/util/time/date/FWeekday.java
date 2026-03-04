package de.invesdwin.util.time.date;

import java.time.DayOfWeek;
import java.util.Calendar;
import java.util.Map;

import javax.annotation.concurrent.Immutable;

import org.joda.time.DateTimeConstants;

import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.time.duration.Duration;

@Immutable
public enum FWeekday {
    Monday("Mon") {
        @Override
        public int calendarValue() {
            return Calendar.MONDAY;
        }

        @Override
        public DayOfWeek javaTimeValue() {
            return DayOfWeek.MONDAY;
        }

        @Override
        public int jodaTimeValue() {
            return DateTimeConstants.MONDAY;
        }

        @Override
        public boolean isWeekend() {
            return false;
        }
    },
    Tuesday("Tues", "Tue") {
        @Override
        public int calendarValue() {
            return Calendar.TUESDAY;
        }

        @Override
        public DayOfWeek javaTimeValue() {
            return DayOfWeek.TUESDAY;
        }

        @Override
        public int jodaTimeValue() {
            return DateTimeConstants.TUESDAY;
        }

        @Override
        public boolean isWeekend() {
            return false;
        }
    },
    Wednesday("Wed") {
        @Override
        public int calendarValue() {
            return Calendar.WEDNESDAY;
        }

        @Override
        public DayOfWeek javaTimeValue() {
            return DayOfWeek.WEDNESDAY;
        }

        @Override
        public int jodaTimeValue() {
            return DateTimeConstants.WEDNESDAY;
        }

        @Override
        public boolean isWeekend() {
            return false;
        }
    },
    Thursday("Thur", "Thu") {
        @Override
        public int calendarValue() {
            return Calendar.THURSDAY;
        }

        @Override
        public DayOfWeek javaTimeValue() {
            return DayOfWeek.THURSDAY;
        }

        @Override
        public int jodaTimeValue() {
            return DateTimeConstants.THURSDAY;
        }

        @Override
        public boolean isWeekend() {
            return false;
        }
    },
    Friday("Fri") {
        @Override
        public int calendarValue() {
            return Calendar.FRIDAY;
        }

        @Override
        public DayOfWeek javaTimeValue() {
            return DayOfWeek.FRIDAY;
        }

        @Override
        public int jodaTimeValue() {
            return DateTimeConstants.FRIDAY;
        }

        @Override
        public boolean isWeekend() {
            return false;
        }
    },
    Saturday("Sat") {
        @Override
        public int calendarValue() {
            return Calendar.SATURDAY;
        }

        @Override
        public DayOfWeek javaTimeValue() {
            return DayOfWeek.SATURDAY;
        }

        @Override
        public int jodaTimeValue() {
            return DateTimeConstants.SATURDAY;
        }

        @Override
        public boolean isWeekend() {
            return true;
        }
    },
    Sunday("Sun") {
        @Override
        public int calendarValue() {
            return Calendar.SUNDAY;
        }

        @Override
        public DayOfWeek javaTimeValue() {
            return DayOfWeek.SUNDAY;
        }

        @Override
        public int jodaTimeValue() {
            return DateTimeConstants.SUNDAY;
        }

        @Override
        public boolean isWeekend() {
            return true;
        }
    };

    private static final Map<Integer, FWeekday> CALENDAR_LOOKUP = ILockCollectionFactory.getInstance(false).newMap();
    private static final Map<DayOfWeek, FWeekday> JAVA_TIME_LOOKUP = ILockCollectionFactory.getInstance(false).newMap();
    private static final Map<Integer, FWeekday> JODA_TIME_LOOKUP = ILockCollectionFactory.getInstance(false).newMap();
    private static final Map<String, FWeekday> ALIAS_LOOKUP = ILockCollectionFactory.getInstance(false).newMap();

    static {
        for (final FWeekday f : FWeekday.values()) {
            CALENDAR_LOOKUP.put(f.calendarValue(), f);
            JAVA_TIME_LOOKUP.put(f.javaTimeValue(), f);
            JODA_TIME_LOOKUP.put(f.jodaTimeValue(), f);
            ALIAS_LOOKUP.put(f.name().toLowerCase(), f);
            for (final String alias : f.getAliases()) {
                ALIAS_LOOKUP.put(alias.toLowerCase(), f);
            }
        }
    }

    private final String[] aliases;

    FWeekday(final String... aliases) {
        this.aliases = aliases;
    }

    public String[] getAliases() {
        return aliases;
    }

    public static FWeekday valueOfIndex(final int weekday) {
        return valueOfJodaTime(weekday);
    }

    public static FWeekday valueOfCalendar(final int weekday) {
        return lookup(CALENDAR_LOOKUP, weekday);
    }

    public static FWeekday valueOfJavaTime(final DayOfWeek weekday) {
        return lookup(JAVA_TIME_LOOKUP, weekday);
    }

    public static FWeekday valueOfJodaTime(final int weekday) {
        return lookup(JODA_TIME_LOOKUP, weekday);
    }

    @SuppressWarnings("unchecked")
    private static <T> FWeekday lookup(final Map<T, FWeekday> map, final T weekday) {
        if (weekday == null) {
            throw new NullPointerException("parameter field should not be null");
        }
        final FWeekday value = map.get(weekday);
        if (value == null) {
            throw UnknownArgumentException.newInstance((Class<T>) weekday.getClass(), weekday);
        } else {
            return value;
        }
    }

    /**
     * 1-7 from sunday to saturday
     */
    public abstract int calendarValue();

    public abstract DayOfWeek javaTimeValue();

    /**
     * 1-7 from monday to sunday
     */
    public abstract int jodaTimeValue();

    public int indexValue() {
        return jodaTimeValue();
    }

    public Duration durationValue() {
        return new Duration(indexValue(), FTimeUnit.DAYS);
    }

    public String toString3Letters() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this);
        sb.setLength(3);
        return sb.toString();
    }

    public String toString2Letters() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this);
        sb.setLength(2);
        return sb.toString();
    }

    public abstract boolean isWeekend();

    public boolean isWorkday() {
        return !isWeekend();
    }

    public static FWeekday valueOfJulian(final FDate date) {
        return valueOfJulian(date.millisValue());
    }

    public static FWeekday valueOfJulian(final long millis) {
        //no conversion needed since joda time has same index
        final int index = indexOfJulian(millis);
        return valueOfIndex(index);
    }

    public static int indexOfJulian(final FDate date) {
        return indexOfJulian(date.millisValue());
    }

    public static int indexOfJulian(final long millis) {
        //extracted from: final int index = FDates.getDefaultChronology().dayOfWeek().get(millis);
        // 1970-01-01 is day of week 4, Thursday.
        final long daysSince19700101 = millis / FTimeUnit.MILLISECONDS_IN_DAY;
        return 1 + (int) ((daysSince19700101 + 3) % 7);
    }

    public static boolean isJulianWeekend(final long millis) {
        final int indexOfMillis = indexOfJulian(millis);
        return indexOfMillis >= DateTimeConstants.SATURDAY;
    }

    public static boolean isJulianWeekend(final FDate date) {
        return isJulianWeekend(date.millisValue());
    }

    public static FWeekday valueOfAlias(final String alias) {
        final FWeekday value = ALIAS_LOOKUP.get(alias.toLowerCase());
        if (value != null) {
            return value;
        } else {
            throw UnknownArgumentException.newInstance(String.class, alias);
        }
    }

    public static FWeekday valueOfAliasNullable(final String alias) {
        final FWeekday value = ALIAS_LOOKUP.get(alias.toLowerCase());
        return value;
    }

    public static FWeekday valueOfDuration(final Duration value) {
        if (value.isLessThan(Duration.ONE_DAY)) {
            return FWeekday.Monday;
        }
        final int weekDay = (int) value.getNumMultipleOfPeriod(Duration.ONE_DAY) % FTimeUnit.DAYS_IN_WEEK;
        if (weekDay == 0) {
            return FWeekday.Sunday;
        } else {
            return valueOfIndex(weekDay);
        }
    }

}
