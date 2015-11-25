package de.invesdwin.util.time.fdate;

import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.concurrent.Immutable;

import org.joda.time.DurationFieldType;

import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.time.duration.Duration;

@Immutable
public enum FTimeUnit {

    Years {
        @Override
        public int calendarValue() {
            return Calendar.YEAR;
        }

        @Override
        public ChronoUnit javaTimeValue() {
            return ChronoUnit.YEARS;
        }

        @Override
        public DurationFieldType jodaTimeValue() {
            return DurationFieldType.years();
        }

        @Override
        public TimeUnit timeUnitValue() {
            return null;
        }

        @Override
        public Duration getDuration() {
            return new Duration(Duration.DAYS_IN_YEAR, TimeUnit.DAYS);
        }
    },
    Months {
        @Override
        public int calendarValue() {
            return Calendar.MONTH;
        }

        @Override
        public ChronoUnit javaTimeValue() {
            return ChronoUnit.MONTHS;
        }

        @Override
        public DurationFieldType jodaTimeValue() {
            return DurationFieldType.months();
        }

        @Override
        public TimeUnit timeUnitValue() {
            return null;
        }

        @Override
        public Duration getDuration() {
            return new Duration(Duration.DAYS_IN_MONTH, TimeUnit.DAYS);
        }
    },
    Weeks {

        @Override
        public int calendarValue() {
            return Calendar.WEEK_OF_YEAR;
        }

        @Override
        public ChronoUnit javaTimeValue() {
            return ChronoUnit.WEEKS;
        }

        @Override
        public DurationFieldType jodaTimeValue() {
            return DurationFieldType.weeks();
        }

        @Override
        public TimeUnit timeUnitValue() {
            return null;
        }

        @Override
        public Duration getDuration() {
            return new Duration(Duration.DAYS_IN_WEEK, TimeUnit.DAYS);
        }

    },
    Days {
        @Override
        public int calendarValue() {
            return Calendar.DAY_OF_MONTH;
        }

        @Override
        public ChronoUnit javaTimeValue() {
            return ChronoUnit.DAYS;
        }

        @Override
        public DurationFieldType jodaTimeValue() {
            return DurationFieldType.days();
        }

        @Override
        public TimeUnit timeUnitValue() {
            return TimeUnit.DAYS;
        }

        @Override
        public Duration getDuration() {
            return new Duration(1, TimeUnit.DAYS);
        }
    },
    Hours {
        @Override
        public int calendarValue() {
            return Calendar.HOUR_OF_DAY;
        }

        @Override
        public ChronoUnit javaTimeValue() {
            return ChronoUnit.HOURS;
        }

        @Override
        public DurationFieldType jodaTimeValue() {
            return DurationFieldType.hours();
        }

        @Override
        public TimeUnit timeUnitValue() {
            return TimeUnit.HOURS;
        }

        @Override
        public Duration getDuration() {
            return new Duration(1, TimeUnit.HOURS);
        }
    },
    Minutes {
        @Override
        public int calendarValue() {
            return Calendar.MINUTE;
        }

        @Override
        public ChronoUnit javaTimeValue() {
            return ChronoUnit.MINUTES;
        }

        @Override
        public DurationFieldType jodaTimeValue() {
            return DurationFieldType.minutes();
        }

        @Override
        public TimeUnit timeUnitValue() {
            return TimeUnit.MINUTES;
        }

        @Override
        public Duration getDuration() {
            return new Duration(1, TimeUnit.MINUTES);
        }
    },
    Seconds {
        @Override
        public int calendarValue() {
            return Calendar.SECOND;
        }

        @Override
        public ChronoUnit javaTimeValue() {
            return ChronoUnit.SECONDS;
        }

        @Override
        public DurationFieldType jodaTimeValue() {
            return DurationFieldType.seconds();
        }

        @Override
        public TimeUnit timeUnitValue() {
            return TimeUnit.SECONDS;
        }

        @Override
        public Duration getDuration() {
            return new Duration(1, TimeUnit.SECONDS);
        }
    },
    Milliseconds {
        @Override
        public int calendarValue() {
            return Calendar.MILLISECOND;
        }

        @Override
        public ChronoUnit javaTimeValue() {
            return ChronoUnit.MILLIS;
        }

        @Override
        public DurationFieldType jodaTimeValue() {
            return DurationFieldType.millis();
        }

        @Override
        public TimeUnit timeUnitValue() {
            return TimeUnit.MILLISECONDS;
        }

        @Override
        public Duration getDuration() {
            return new Duration(1, TimeUnit.MILLISECONDS);
        }
    };

    private static final Map<TimeUnit, FTimeUnit> TIME_UNIT_LOOKUP = new HashMap<TimeUnit, FTimeUnit>();
    private static final Map<Integer, FTimeUnit> CALENDAR_LOOKUP = new HashMap<Integer, FTimeUnit>();
    private static final Map<ChronoUnit, FTimeUnit> JAVA_TIME_LOOKUP = new HashMap<ChronoUnit, FTimeUnit>();
    private static final Map<DurationFieldType, FTimeUnit> JODA_TIME_LOOKUP = new HashMap<DurationFieldType, FTimeUnit>();

    static {
        for (final FTimeUnit f : FTimeUnit.values()) {
            if (f.timeUnitValue() != null) {
                TIME_UNIT_LOOKUP.put(f.timeUnitValue(), f);
            }
            CALENDAR_LOOKUP.put(f.calendarValue(), f);
            JAVA_TIME_LOOKUP.put(f.javaTimeValue(), f);
            JODA_TIME_LOOKUP.put(f.jodaTimeValue(), f);
        }
    }

    public abstract int calendarValue();

    public abstract TimeUnit timeUnitValue();

    public abstract ChronoUnit javaTimeValue();

    public abstract DurationFieldType jodaTimeValue();

    public static FTimeUnit valueOfTimeUnit(final TimeUnit timeUnit) {
        return lookup(TIME_UNIT_LOOKUP, timeUnit);
    }

    public static FTimeUnit valueOfCalendar(final int timeUnit) {
        return lookup(CALENDAR_LOOKUP, timeUnit);
    }

    public static FTimeUnit valueOfJavaTime(final ChronoUnit timeUnit) {
        return lookup(JAVA_TIME_LOOKUP, timeUnit);
    }

    public static FTimeUnit valueOfJodaTime(final DurationFieldType timeUnit) {
        return lookup(JODA_TIME_LOOKUP, timeUnit);
    }

    @SuppressWarnings("unchecked")
    private static <T> FTimeUnit lookup(final Map<T, FTimeUnit> map, final T timeUnit) {
        if (timeUnit == null) {
            throw new NullPointerException("parameter field should not be null");
        }
        final FTimeUnit value = map.get(timeUnit);
        if (value == null) {
            throw UnknownArgumentException.newInstance((Class<T>) timeUnit.getClass(), timeUnit);
        } else {
            return value;
        }
    }

    public abstract Duration getDuration();

}
