// CHECKSTYLE:OFF file length

package de.invesdwin.util.time.date;

import javax.annotation.concurrent.Immutable;

// CHECKSTYLE:ON
@Immutable
public enum FTimeUnitFractional {

    MILLENIA {
        @Override
        public FTimeUnit asNonFractional() {
            return FTimeUnit.MILLENIA;
        }

        @Override
        public double convert(final double duration, final FTimeUnitFractional timeUnit) {
            return YEARS.convert(duration, timeUnit) / YEARS_IN_MILLENIUM;
        }

        @Override
        public double toNanos(final double duration) {
            return YEARS.toNanos(duration) * YEARS_IN_MILLENIUM;
        }

        @Override
        public double toMicros(final double duration) {
            return YEARS.toMicros(duration) * YEARS_IN_MILLENIUM;
        }

        @Override
        public double toMillis(final double duration) {
            return YEARS.toMillis(duration) * YEARS_IN_MILLENIUM;
        }

        @Override
        public double toSeconds(final double duration) {
            return YEARS.toSeconds(duration) * YEARS_IN_MILLENIUM;
        }

        @Override
        public double toMinutes(final double duration) {
            return YEARS.toMinutes(duration) * YEARS_IN_MILLENIUM;
        }

        @Override
        public double toHours(final double duration) {
            return YEARS.toHours(duration) * YEARS_IN_MILLENIUM;
        }

        @Override
        public double toDays(final double duration) {
            return YEARS.toDays(duration) * YEARS_IN_MILLENIUM;
        }

        @Override
        public double toWeeks(final double duration) {
            return YEARS.toWeeks(duration) * YEARS_IN_MILLENIUM;
        }

        @Override
        public double toMonths(final double duration) {
            return YEARS.toMonths(duration * YEARS_IN_MILLENIUM);
        }

        @Override
        public double toYears(final double duration) {
            return duration * YEARS_IN_MILLENIUM;
        }
    },
    CENTURIES {

        @Override
        public FTimeUnit asNonFractional() {
            return FTimeUnit.CENTURIES;
        }

        @Override
        public double convert(final double duration, final FTimeUnitFractional timeUnit) {
            return YEARS.convert(duration, timeUnit) / YEARS_IN_CENTURY;
        }

        @Override
        public double toNanos(final double duration) {
            return YEARS.toNanos(duration) * YEARS_IN_CENTURY;
        }

        @Override
        public double toMicros(final double duration) {
            return YEARS.toMicros(duration) * YEARS_IN_CENTURY;
        }

        @Override
        public double toMillis(final double duration) {
            return YEARS.toMillis(duration) * YEARS_IN_CENTURY;
        }

        @Override
        public double toSeconds(final double duration) {
            return YEARS.toSeconds(duration) * YEARS_IN_CENTURY;
        }

        @Override
        public double toMinutes(final double duration) {
            return YEARS.toMinutes(duration) * YEARS_IN_CENTURY;
        }

        @Override
        public double toHours(final double duration) {
            return YEARS.toHours(duration) * YEARS_IN_CENTURY;
        }

        @Override
        public double toDays(final double duration) {
            return YEARS.toDays(duration) * YEARS_IN_CENTURY;
        }

        @Override
        public double toWeeks(final double duration) {
            return YEARS.toWeeks(duration) * YEARS_IN_CENTURY;
        }

        @Override
        public double toMonths(final double duration) {
            return YEARS.toMonths(duration * YEARS_IN_CENTURY);
        }

        @Override
        public double toYears(final double duration) {
            return duration * YEARS_IN_CENTURY;
        }
    },
    DECADES {

        @Override
        public FTimeUnit asNonFractional() {
            return FTimeUnit.DECADES;
        }

        @Override
        public double convert(final double duration, final FTimeUnitFractional timeUnit) {
            return YEARS.convert(duration, timeUnit) / YEARS_IN_DECADE;
        }

        @Override
        public double toNanos(final double duration) {
            return YEARS.toNanos(duration) * YEARS_IN_DECADE;
        }

        @Override
        public double toMicros(final double duration) {
            return YEARS.toMicros(duration) * YEARS_IN_DECADE;
        }

        @Override
        public double toMillis(final double duration) {
            return YEARS.toMillis(duration) * YEARS_IN_DECADE;
        }

        @Override
        public double toSeconds(final double duration) {
            return YEARS.toSeconds(duration) * YEARS_IN_DECADE;
        }

        @Override
        public double toMinutes(final double duration) {
            return YEARS.toMinutes(duration) * YEARS_IN_DECADE;
        }

        @Override
        public double toHours(final double duration) {
            return YEARS.toHours(duration) * YEARS_IN_DECADE;
        }

        @Override
        public double toDays(final double duration) {
            return YEARS.toDays(duration) * YEARS_IN_DECADE;
        }

        @Override
        public double toWeeks(final double duration) {
            return YEARS.toWeeks(duration) * YEARS_IN_DECADE;
        }

        @Override
        public double toMonths(final double duration) {
            return YEARS.toMonths(duration * YEARS_IN_DECADE);
        }

        @Override
        public double toYears(final double duration) {
            return duration * YEARS_IN_DECADE;
        }
    },
    YEARS {

        @Override
        public FTimeUnit asNonFractional() {
            return FTimeUnit.YEARS;
        }

        @Override
        public double convert(final double duration, final FTimeUnitFractional timeUnit) {
            return timeUnit.toYears(duration);
        }

        @Override
        public double toNanos(final double duration) {
            return toMicros(duration) * NANOSECONDS_IN_MICROSECOND;
        }

        @Override
        public double toMicros(final double duration) {
            return toMillis(duration) * MICROSECONDS_IN_MILLISECOND;
        }

        @Override
        public double toMillis(final double duration) {
            return toSeconds(duration) * MILLISECONDS_IN_SECOND;
        }

        @Override
        public double toSeconds(final double duration) {
            return toMinutes(duration) * SECONDS_IN_MINUTE;
        }

        @Override
        public double toMinutes(final double duration) {
            return toHours(duration) * MINUTES_IN_HOUR;
        }

        @Override
        public double toHours(final double duration) {
            return toDays(duration) * HOURS_IN_DAY;
        }

        @Override
        public double toDays(final double duration) {
            return duration * DAYS_IN_YEAR;
        }

        @Override
        public double toWeeks(final double duration) {
            return duration * WEEKS_IN_YEAR;
        }

        @Override
        public double toMonths(final double duration) {
            return duration * MONTHS_IN_YEAR;
        }

        @Override
        public double toYears(final double duration) {
            return duration;
        }

    },
    MONTHS {

        @Override
        public FTimeUnit asNonFractional() {
            return FTimeUnit.MONTHS;
        }

        @Override
        public double convert(final double duration, final FTimeUnitFractional timeUnit) {
            return timeUnit.toMonths(duration);
        }

        @Override
        public double toNanos(final double duration) {
            return toMicros(duration) * NANOSECONDS_IN_MICROSECOND;
        }

        @Override
        public double toMicros(final double duration) {
            return toMillis(duration) * MICROSECONDS_IN_MILLISECOND;
        }

        @Override
        public double toMillis(final double duration) {
            return toSeconds(duration) * MILLISECONDS_IN_SECOND;
        }

        @Override
        public double toSeconds(final double duration) {
            return toMinutes(duration) * SECONDS_IN_MINUTE;
        }

        @Override
        public double toMinutes(final double duration) {
            return toHours(duration) * MINUTES_IN_HOUR;
        }

        @Override
        public double toHours(final double duration) {
            return toDays(duration) * HOURS_IN_DAY;
        }

        @Override
        public double toDays(final double duration) {
            return duration * DAYS_IN_MONTH;
        }

        @Override
        public double toWeeks(final double duration) {
            return duration * WEEKS_IN_MONTH;
        }

        @Override
        public double toMonths(final double duration) {
            return duration;
        }

        @Override
        public double toYears(final double duration) {
            return duration / MONTHS_IN_YEAR;
        }

    },
    WEEKS {

        @Override
        public FTimeUnit asNonFractional() {
            return FTimeUnit.WEEKS;
        }

        @Override
        public double convert(final double duration, final FTimeUnitFractional timeUnit) {
            return timeUnit.toWeeks(duration);
        }

        @Override
        public double toNanos(final double duration) {
            return toMicros(duration) * NANOSECONDS_IN_MICROSECOND;
        }

        @Override
        public double toMicros(final double duration) {
            return toMillis(duration) * MICROSECONDS_IN_MILLISECOND;
        }

        @Override
        public double toMillis(final double duration) {
            return toSeconds(duration) * MILLISECONDS_IN_SECOND;
        }

        @Override
        public double toSeconds(final double duration) {
            return toMinutes(duration) * SECONDS_IN_MINUTE;
        }

        @Override
        public double toMinutes(final double duration) {
            return toHours(duration) * MINUTES_IN_HOUR;
        }

        @Override
        public double toHours(final double duration) {
            return toDays(duration) * HOURS_IN_DAY;
        }

        @Override
        public double toDays(final double duration) {
            return toWeeks(duration) * DAYS_IN_WEEK;
        }

        @Override
        public double toWeeks(final double duration) {
            return duration;
        }

        @Override
        public double toMonths(final double duration) {
            return toDays(duration) * DAYS_IN_MONTH;
        }

        @Override
        public double toYears(final double duration) {
            return toDays(duration) * DAYS_IN_YEAR;
        }

    },
    DAYS {

        @Override
        public FTimeUnit asNonFractional() {
            return FTimeUnit.DAYS;
        }

        @Override
        public double convert(final double duration, final FTimeUnitFractional timeUnit) {
            return timeUnit.toDays(duration);
        }

        @Override
        public double toNanos(final double duration) {
            return toMicros(duration) * NANOSECONDS_IN_MICROSECOND;
        }

        @Override
        public double toMicros(final double duration) {
            return toMillis(duration) * MICROSECONDS_IN_MILLISECOND;
        }

        @Override
        public double toMillis(final double duration) {
            return toSeconds(duration) * MILLISECONDS_IN_SECOND;
        }

        @Override
        public double toSeconds(final double duration) {
            return toMinutes(duration) * SECONDS_IN_MINUTE;
        }

        @Override
        public double toMinutes(final double duration) {
            return toHours(duration) * MINUTES_IN_HOUR;
        }

        @Override
        public double toHours(final double duration) {
            return toDays(duration) * HOURS_IN_DAY;
        }

        @Override
        public double toDays(final double duration) {
            return duration;
        }

        @Override
        public double toWeeks(final double duration) {
            return toDays(duration) * DAYS_IN_WEEK;
        }

        @Override
        public double toMonths(final double duration) {
            return toDays(duration) * DAYS_IN_MONTH;
        }

        @Override
        public double toYears(final double duration) {
            return toDays(duration) * DAYS_IN_YEAR;
        }

    },
    HOURS {

        @Override
        public FTimeUnit asNonFractional() {
            return FTimeUnit.HOURS;
        }

        @Override
        public double convert(final double duration, final FTimeUnitFractional timeUnit) {
            return timeUnit.toHours(duration);
        }

        @Override
        public double toNanos(final double duration) {
            return toMicros(duration) * NANOSECONDS_IN_MICROSECOND;
        }

        @Override
        public double toMicros(final double duration) {
            return toMillis(duration) * MICROSECONDS_IN_MILLISECOND;
        }

        @Override
        public double toMillis(final double duration) {
            return toSeconds(duration) * MILLISECONDS_IN_SECOND;
        }

        @Override
        public double toSeconds(final double duration) {
            return toMinutes(duration) * SECONDS_IN_MINUTE;
        }

        @Override
        public double toMinutes(final double duration) {
            return toHours(duration) * MINUTES_IN_HOUR;
        }

        @Override
        public double toHours(final double duration) {
            return duration;
        }

        @Override
        public double toDays(final double duration) {
            return toHours(duration) / HOURS_IN_DAY;
        }

        @Override
        public double toWeeks(final double duration) {
            return toDays(duration) * DAYS_IN_WEEK;
        }

        @Override
        public double toMonths(final double duration) {
            return toDays(duration) * DAYS_IN_MONTH;
        }

        @Override
        public double toYears(final double duration) {
            return toDays(duration) * DAYS_IN_YEAR;
        }

    },
    MINUTES {

        @Override
        public FTimeUnit asNonFractional() {
            return FTimeUnit.MINUTES;
        }

        @Override
        public double convert(final double duration, final FTimeUnitFractional timeUnit) {
            return timeUnit.toMinutes(duration);
        }

        @Override
        public double toNanos(final double duration) {
            return toMicros(duration) * NANOSECONDS_IN_MICROSECOND;
        }

        @Override
        public double toMicros(final double duration) {
            return toMillis(duration) * MICROSECONDS_IN_MILLISECOND;
        }

        @Override
        public double toMillis(final double duration) {
            return toSeconds(duration) * MILLISECONDS_IN_SECOND;
        }

        @Override
        public double toSeconds(final double duration) {
            return toMinutes(duration) * SECONDS_IN_MINUTE;
        }

        @Override
        public double toMinutes(final double duration) {
            return duration;
        }

        @Override
        public double toHours(final double duration) {
            return toMinutes(duration) / MINUTES_IN_HOUR;
        }

        @Override
        public double toDays(final double duration) {
            return toHours(duration) / HOURS_IN_DAY;
        }

        @Override
        public double toWeeks(final double duration) {
            return toDays(duration) * DAYS_IN_WEEK;
        }

        @Override
        public double toMonths(final double duration) {
            return toDays(duration) * DAYS_IN_MONTH;
        }

        @Override
        public double toYears(final double duration) {
            return toDays(duration) * DAYS_IN_YEAR;
        }

    },
    SECONDS {

        @Override
        public FTimeUnit asNonFractional() {
            return FTimeUnit.SECONDS;
        }

        @Override
        public double convert(final double duration, final FTimeUnitFractional timeUnit) {
            return timeUnit.toSeconds(duration);
        }

        @Override
        public double toNanos(final double duration) {
            return toMicros(duration) * NANOSECONDS_IN_MICROSECOND;
        }

        @Override
        public double toMicros(final double duration) {
            return toMillis(duration) * MICROSECONDS_IN_MILLISECOND;
        }

        @Override
        public double toMillis(final double duration) {
            return toSeconds(duration) * MILLISECONDS_IN_SECOND;
        }

        @Override
        public double toSeconds(final double duration) {
            return duration;
        }

        @Override
        public double toMinutes(final double duration) {
            return toSeconds(duration) / SECONDS_IN_MINUTE;
        }

        @Override
        public double toHours(final double duration) {
            return toMinutes(duration) / MINUTES_IN_HOUR;
        }

        @Override
        public double toDays(final double duration) {
            return toHours(duration) / HOURS_IN_DAY;
        }

        @Override
        public double toWeeks(final double duration) {
            return toDays(duration) * DAYS_IN_WEEK;
        }

        @Override
        public double toMonths(final double duration) {
            return toDays(duration) * DAYS_IN_MONTH;
        }

        @Override
        public double toYears(final double duration) {
            return toDays(duration) * DAYS_IN_YEAR;
        }

    },
    MILLISECONDS {

        @Override
        public FTimeUnit asNonFractional() {
            return FTimeUnit.MILLISECONDS;
        }

        @Override
        public double convert(final double duration, final FTimeUnitFractional timeUnit) {
            return timeUnit.toMillis(duration);
        }

        @Override
        public double toNanos(final double duration) {
            return toMicros(duration) * NANOSECONDS_IN_MICROSECOND;
        }

        @Override
        public double toMicros(final double duration) {
            return toMillis(duration) * MICROSECONDS_IN_MILLISECOND;
        }

        @Override
        public double toMillis(final double duration) {
            return duration;
        }

        @Override
        public double toSeconds(final double duration) {
            return toMillis(duration) / MILLISECONDS_IN_SECOND;
        }

        @Override
        public double toMinutes(final double duration) {
            return toSeconds(duration) / SECONDS_IN_MINUTE;
        }

        @Override
        public double toHours(final double duration) {
            return toMinutes(duration) / MINUTES_IN_HOUR;
        }

        @Override
        public double toDays(final double duration) {
            return toHours(duration) / HOURS_IN_DAY;
        }

        @Override
        public double toWeeks(final double duration) {
            return toDays(duration) * DAYS_IN_WEEK;
        }

        @Override
        public double toMonths(final double duration) {
            return toDays(duration) * DAYS_IN_MONTH;
        }

        @Override
        public double toYears(final double duration) {
            return toDays(duration) * DAYS_IN_YEAR;
        }

    },
    MICROSECONDS {
        @Override
        public FTimeUnit asNonFractional() {
            return FTimeUnit.MICROSECONDS;
        }

        @Override
        public double convert(final double duration, final FTimeUnitFractional timeUnit) {
            return timeUnit.toMicros(duration);
        }

        @Override
        public double toNanos(final double duration) {
            return toMicros(duration) * NANOSECONDS_IN_MICROSECOND;
        }

        @Override
        public double toMicros(final double duration) {
            return duration;
        }

        @Override
        public double toMillis(final double duration) {
            return toMicros(duration) / MICROSECONDS_IN_MILLISECOND;
        }

        @Override
        public double toSeconds(final double duration) {
            return toMillis(duration) / MILLISECONDS_IN_SECOND;
        }

        @Override
        public double toMinutes(final double duration) {
            return toSeconds(duration) / SECONDS_IN_MINUTE;
        }

        @Override
        public double toHours(final double duration) {
            return toMinutes(duration) / MINUTES_IN_HOUR;
        }

        @Override
        public double toDays(final double duration) {
            return toHours(duration) / HOURS_IN_DAY;
        }

        @Override
        public double toWeeks(final double duration) {
            return toDays(duration) * DAYS_IN_WEEK;
        }

        @Override
        public double toMonths(final double duration) {
            return toDays(duration) * DAYS_IN_MONTH;
        }

        @Override
        public double toYears(final double duration) {
            return toDays(duration) * DAYS_IN_YEAR;
        }

    },
    NANOSECONDS {

        @Override
        public FTimeUnit asNonFractional() {
            return FTimeUnit.NANOSECONDS;
        }

        @Override
        public double convert(final double duration, final FTimeUnitFractional timeUnit) {
            return timeUnit.toNanos(duration);
        }

        @Override
        public double toNanos(final double duration) {
            return duration;
        }

        @Override
        public double toMicros(final double duration) {
            return toNanos(duration) / NANOSECONDS_IN_MICROSECOND;
        }

        @Override
        public double toMillis(final double duration) {
            return toMicros(duration) / MICROSECONDS_IN_MILLISECOND;
        }

        @Override
        public double toSeconds(final double duration) {
            return toMillis(duration) / MILLISECONDS_IN_SECOND;
        }

        @Override
        public double toMinutes(final double duration) {
            return toSeconds(duration) / SECONDS_IN_MINUTE;
        }

        @Override
        public double toHours(final double duration) {
            return toMinutes(duration) / MINUTES_IN_HOUR;
        }

        @Override
        public double toDays(final double duration) {
            return toHours(duration) / HOURS_IN_DAY;
        }

        @Override
        public double toWeeks(final double duration) {
            return toDays(duration) * DAYS_IN_WEEK;
        }

        @Override
        public double toMonths(final double duration) {
            return toDays(duration) * DAYS_IN_MONTH;
        }

        @Override
        public double toYears(final double duration) {
            return toDays(duration) * DAYS_IN_YEAR;
        }

    };

    public static final double YEARS_IN_MILLENIUM = FTimeUnit.YEARS_IN_MILLENIUM;
    public static final double YEARS_IN_CENTURY = FTimeUnit.YEARS_IN_CENTURY;
    public static final double YEARS_IN_DECADE = FTimeUnit.YEARS_IN_DECADE;
    public static final double CENTURIES_IN_MILLENIUM = FTimeUnit.CENTURIES_IN_MILLENIUM;
    public static final double DECADES_IN_CENTURY = FTimeUnit.DECADES_IN_CENTURY;
    public static final double DAYS_IN_YEAR = FTimeUnit.DAYS_IN_YEAR;
    public static final double MAX_DAYS_IN_YEAR = FTimeUnit.MAX_DAYS_IN_YEAR;
    public static final double MONTHS_IN_YEAR = FTimeUnit.MONTHS_IN_YEAR;
    public static final double DAYS_IN_WEEK = FTimeUnit.DAYS_IN_WEEK;
    public static final double DAYS_IN_MONTH = FTimeUnit.DAYS_IN_MONTH;
    public static final double WEEKS_IN_MONTH = FTimeUnit.WEEKS_IN_MONTH;
    public static final double WEEKS_IN_YEAR = FTimeUnit.WEEKS_IN_YEAR;
    public static final double HOURS_IN_DAY = FTimeUnit.HOURS_IN_DAY;
    public static final double MINUTES_IN_HOUR = FTimeUnit.MINUTES_IN_HOUR;
    public static final double SECONDS_IN_MINUTE = FTimeUnit.SECONDS_IN_MINUTE;
    public static final double MILLISECONDS_IN_SECOND = FTimeUnit.MILLISECONDS_IN_SECOND;
    public static final double MICROSECONDS_IN_MILLISECOND = FTimeUnit.MICROSECONDS_IN_MILLISECOND;
    public static final double NANOSECONDS_IN_MICROSECOND = FTimeUnit.NANOSECONDS_IN_MICROSECOND;

    public static final double MILLISECONDS_IN_MINUTE = FTimeUnit.MILLISECONDS_IN_MINUTE;
    public static final double MILLISECONDS_IN_HOUR = FTimeUnit.MILLISECONDS_IN_HOUR;
    public static final double MILLISECONDS_IN_DAY = FTimeUnit.MILLISECONDS_IN_DAY;

    public abstract FTimeUnit asNonFractional();

    public abstract double convert(double duration, FTimeUnitFractional timeUnit);

    public abstract double toNanos(double duration);

    public abstract double toMicros(double duration);

    public abstract double toMillis(double duration);

    public abstract double toSeconds(double duration);

    public abstract double toMinutes(double duration);

    public abstract double toHours(double duration);

    public abstract double toDays(double duration);

    public abstract double toWeeks(double duration);

    public abstract double toMonths(double duration);

    public abstract double toYears(double duration);

}
