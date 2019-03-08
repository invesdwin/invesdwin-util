package de.invesdwin.util.time.fdate;

import java.time.DayOfWeek;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.concurrent.Immutable;

import org.joda.time.DateTimeConstants;

import de.invesdwin.util.error.UnknownArgumentException;

@Immutable
public enum FWeekday {
    Monday {
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
    Tuesday {
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
    Wednesday {
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
    Thursday {
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
    Friday {
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
    Saturday {
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
    Sunday {
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

    private static final Map<Integer, FWeekday> CALENDAR_LOOKUP = new HashMap<Integer, FWeekday>();
    private static final Map<DayOfWeek, FWeekday> JAVA_TIME_LOOKUP = new HashMap<DayOfWeek, FWeekday>();
    private static final Map<Integer, FWeekday> JODA_TIME_LOOKUP = new HashMap<Integer, FWeekday>();

    static {
        for (final FWeekday f : FWeekday.values()) {
            CALENDAR_LOOKUP.put(f.calendarValue(), f);
            JAVA_TIME_LOOKUP.put(f.javaTimeValue(), f);
            JODA_TIME_LOOKUP.put(f.jodaTimeValue(), f);
        }
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

    public abstract int calendarValue();

    public abstract DayOfWeek javaTimeValue();

    public abstract int jodaTimeValue();

    public int indexValue() {
        return jodaTimeValue();
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

}
