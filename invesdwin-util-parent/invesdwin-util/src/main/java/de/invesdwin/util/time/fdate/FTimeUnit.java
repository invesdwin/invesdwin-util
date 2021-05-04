// CHECKSTYLE:OFF file length

package de.invesdwin.util.time.fdate;

import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.concurrent.Immutable;

import org.joda.time.DurationField;
import org.joda.time.DurationFieldType;

import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.time.duration.Duration;

// CHECKSTYLE:ON
@Immutable
public enum FTimeUnit {

    MILLENIA("MILLENIUM") {
        @Override
        public int calendarValue() {
            throw new UnsupportedOperationException();
        }

        @Override
        public TimeUnit timeUnitValue() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ChronoUnit javaTimeValue() {
            return ChronoUnit.MILLENNIA;
        }

        @Override
        public DurationFieldType jodaTimeValue() {
            throw new UnsupportedOperationException();
        }

        @Override
        public DurationField durationFieldValue() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void sleep(final long timeout) throws InterruptedException {
            YEARS.sleep(YEARS_IN_MILLENIUM);
        }

        @Override
        public long convert(final long duration, final FTimeUnit timeUnit) {
            return YEARS.convert(duration, timeUnit) / YEARS_IN_MILLENIUM;
        }

        @Override
        public long toNanos(final long duration) {
            return YEARS.toNanos(duration) * YEARS_IN_MILLENIUM;
        }

        @Override
        public long toMicros(final long duration) {
            return YEARS.toMicros(duration) * YEARS_IN_MILLENIUM;
        }

        @Override
        public long toMillis(final long duration) {
            return YEARS.toMillis(duration) * YEARS_IN_MILLENIUM;
        }

        @Override
        public long toSeconds(final long duration) {
            return YEARS.toSeconds(duration) * YEARS_IN_MILLENIUM;
        }

        @Override
        public long toMinutes(final long duration) {
            return YEARS.toMinutes(duration) * YEARS_IN_MILLENIUM;
        }

        @Override
        public long toHours(final long duration) {
            return YEARS.toHours(duration) * YEARS_IN_MILLENIUM;
        }

        @Override
        public long toDays(final long duration) {
            return YEARS.toDays(duration) * YEARS_IN_MILLENIUM;
        }

        @Override
        public long toWeeks(final long duration) {
            return YEARS.toWeeks(duration) * YEARS_IN_MILLENIUM;
        }

        @Override
        public long toMonths(final long duration) {
            return YEARS.toMonths(duration * YEARS_IN_MILLENIUM);
        }

        @Override
        public long toYears(final long duration) {
            return duration * YEARS_IN_MILLENIUM;
        }
    },
    CENTURIES("CENTURY") {
        @Override
        public int calendarValue() {
            throw new UnsupportedOperationException();
        }

        @Override
        public TimeUnit timeUnitValue() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ChronoUnit javaTimeValue() {
            return ChronoUnit.CENTURIES;
        }

        @Override
        public DurationFieldType jodaTimeValue() {
            return DurationFieldType.centuries();
        }

        @Override
        public DurationField durationFieldValue() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void sleep(final long timeout) throws InterruptedException {
            YEARS.sleep(YEARS_IN_CENTURY);
        }

        @Override
        public long convert(final long duration, final FTimeUnit timeUnit) {
            return YEARS.convert(duration, timeUnit) / YEARS_IN_CENTURY;
        }

        @Override
        public long toNanos(final long duration) {
            return YEARS.toNanos(duration) * YEARS_IN_CENTURY;
        }

        @Override
        public long toMicros(final long duration) {
            return YEARS.toMicros(duration) * YEARS_IN_CENTURY;
        }

        @Override
        public long toMillis(final long duration) {
            return YEARS.toMillis(duration) * YEARS_IN_CENTURY;
        }

        @Override
        public long toSeconds(final long duration) {
            return YEARS.toSeconds(duration) * YEARS_IN_CENTURY;
        }

        @Override
        public long toMinutes(final long duration) {
            return YEARS.toMinutes(duration) * YEARS_IN_CENTURY;
        }

        @Override
        public long toHours(final long duration) {
            return YEARS.toHours(duration) * YEARS_IN_CENTURY;
        }

        @Override
        public long toDays(final long duration) {
            return YEARS.toDays(duration) * YEARS_IN_CENTURY;
        }

        @Override
        public long toWeeks(final long duration) {
            return YEARS.toWeeks(duration) * YEARS_IN_CENTURY;
        }

        @Override
        public long toMonths(final long duration) {
            return YEARS.toMonths(duration * YEARS_IN_CENTURY);
        }

        @Override
        public long toYears(final long duration) {
            return duration * YEARS_IN_CENTURY;
        }
    },
    DECADES("DECADE") {
        @Override
        public int calendarValue() {
            throw new UnsupportedOperationException();
        }

        @Override
        public TimeUnit timeUnitValue() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ChronoUnit javaTimeValue() {
            return ChronoUnit.DECADES;
        }

        @Override
        public DurationFieldType jodaTimeValue() {
            throw new UnsupportedOperationException();
        }

        @Override
        public DurationField durationFieldValue() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void sleep(final long timeout) throws InterruptedException {
            YEARS.sleep(YEARS_IN_DECADE);
        }

        @Override
        public long convert(final long duration, final FTimeUnit timeUnit) {
            return YEARS.convert(duration, timeUnit) / YEARS_IN_DECADE;
        }

        @Override
        public long toNanos(final long duration) {
            return YEARS.toNanos(duration) * YEARS_IN_DECADE;
        }

        @Override
        public long toMicros(final long duration) {
            return YEARS.toMicros(duration) * YEARS_IN_DECADE;
        }

        @Override
        public long toMillis(final long duration) {
            return YEARS.toMillis(duration) * YEARS_IN_DECADE;
        }

        @Override
        public long toSeconds(final long duration) {
            return YEARS.toSeconds(duration) * YEARS_IN_DECADE;
        }

        @Override
        public long toMinutes(final long duration) {
            return YEARS.toMinutes(duration) * YEARS_IN_DECADE;
        }

        @Override
        public long toHours(final long duration) {
            return YEARS.toHours(duration) * YEARS_IN_DECADE;
        }

        @Override
        public long toDays(final long duration) {
            return YEARS.toDays(duration) * YEARS_IN_DECADE;
        }

        @Override
        public long toWeeks(final long duration) {
            return YEARS.toWeeks(duration) * YEARS_IN_DECADE;
        }

        @Override
        public long toMonths(final long duration) {
            return YEARS.toMonths(duration * YEARS_IN_DECADE);
        }

        @Override
        public long toYears(final long duration) {
            return duration * YEARS_IN_DECADE;
        }
    },
    YEARS("YEAR", "Y") {
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
            throw new UnsupportedOperationException();
        }

        @Override
        public DurationField durationFieldValue() {
            return FDates.getDefaultTimeZone().getDurationFieldYears();
        }

        @Override
        public long convert(final long duration, final FTimeUnit timeUnit) {
            return timeUnit.toYears(duration);
        }

        @Override
        public long toNanos(final long duration) {
            return TimeUnit.DAYS.toNanos(toDays(duration));
        }

        @Override
        public long toMicros(final long duration) {
            return TimeUnit.DAYS.toMicros(toDays(duration));
        }

        @Override
        public long toMillis(final long duration) {
            return TimeUnit.DAYS.toMillis(toDays(duration));
        }

        @Override
        public long toSeconds(final long duration) {
            return TimeUnit.DAYS.toSeconds(toDays(duration));
        }

        @Override
        public long toMinutes(final long duration) {
            return TimeUnit.DAYS.toMinutes(toDays(duration));
        }

        @Override
        public long toHours(final long duration) {
            return TimeUnit.DAYS.toHours(toDays(duration));
        }

        @Override
        public long toDays(final long duration) {
            return duration * DAYS_IN_YEAR;
        }

        @Override
        public long toWeeks(final long duration) {
            return duration * WEEKS_IN_YEAR;
        }

        @Override
        public long toMonths(final long duration) {
            return duration * MONTHS_IN_YEAR;
        }

        @Override
        public long toYears(final long duration) {
            return duration;
        }

        @Override
        public void sleep(final long timeout) throws InterruptedException {
            TimeUnit.DAYS.sleep(toDays(timeout));
        }

    },
    MONTHS("MONTH", "MON", "MONS") {
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
        public DurationField durationFieldValue() {
            return FDates.getDefaultTimeZone().getDurationFieldMonths();
        }

        @Override
        public TimeUnit timeUnitValue() {
            throw new UnsupportedOperationException();
        }

        @Override
        public long convert(final long duration, final FTimeUnit timeUnit) {
            return timeUnit.toMonths(duration);
        }

        @Override
        public long toNanos(final long duration) {
            return TimeUnit.DAYS.toNanos(toDays(duration));
        }

        @Override
        public long toMicros(final long duration) {
            return TimeUnit.DAYS.toMicros(toDays(duration));
        }

        @Override
        public long toMillis(final long duration) {
            return TimeUnit.DAYS.toMillis(toDays(duration));
        }

        @Override
        public long toSeconds(final long duration) {
            return TimeUnit.DAYS.toSeconds(toDays(duration));
        }

        @Override
        public long toMinutes(final long duration) {
            return TimeUnit.DAYS.toMinutes(toDays(duration));
        }

        @Override
        public long toHours(final long duration) {
            return TimeUnit.DAYS.toHours(toDays(duration));
        }

        @Override
        public long toDays(final long duration) {
            return duration * DAYS_IN_MONTH;
        }

        @Override
        public long toWeeks(final long duration) {
            return duration * WEEKS_IN_MONTH;
        }

        @Override
        public long toMonths(final long duration) {
            return duration;
        }

        @Override
        public long toYears(final long duration) {
            return duration / MONTHS_IN_YEAR;
        }

        @Override
        public void sleep(final long timeout) throws InterruptedException {
            TimeUnit.DAYS.sleep(toDays(timeout));
        }

    },
    WEEKS("WEEK", "W") {

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
        public DurationField durationFieldValue() {
            return FDates.getDefaultTimeZone().getDurationFieldWeeks();
        }

        @Override
        public TimeUnit timeUnitValue() {
            throw new UnsupportedOperationException();
        }

        @Override
        public long convert(final long duration, final FTimeUnit timeUnit) {
            return timeUnit.toWeeks(duration);
        }

        @Override
        public long toNanos(final long duration) {
            return TimeUnit.DAYS.toNanos(toDays(duration));
        }

        @Override
        public long toMicros(final long duration) {
            return TimeUnit.DAYS.toMicros(toDays(duration));
        }

        @Override
        public long toMillis(final long duration) {
            return TimeUnit.DAYS.toMillis(toDays(duration));
        }

        @Override
        public long toSeconds(final long duration) {
            return TimeUnit.DAYS.toSeconds(toDays(duration));
        }

        @Override
        public long toMinutes(final long duration) {
            return TimeUnit.DAYS.toMinutes(toDays(duration));
        }

        @Override
        public long toHours(final long duration) {
            return TimeUnit.DAYS.toHours(toDays(duration));
        }

        @Override
        public long toDays(final long duration) {
            return duration * DAYS_IN_WEEK;
        }

        @Override
        public long toWeeks(final long duration) {
            return duration;
        }

        @Override
        public long toMonths(final long duration) {
            return duration / WEEKS_IN_MONTH;
        }

        @Override
        public long toYears(final long duration) {
            return duration / WEEKS_IN_YEAR;
        }

        @Override
        public void sleep(final long timeout) throws InterruptedException {
            TimeUnit.DAYS.sleep(toDays(timeout));
        }

    },
    DAYS("DAY", "D") {
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
        public DurationField durationFieldValue() {
            return FDates.getDefaultTimeZone().getDurationFieldDays();
        }

        @Override
        public TimeUnit timeUnitValue() {
            return TimeUnit.DAYS;
        }

        @Override
        public long convert(final long duration, final FTimeUnit timeUnit) {
            return timeUnit.toDays(duration);
        }

        @Override
        public long toNanos(final long duration) {
            return timeUnitValue().toNanos(duration);
        }

        @Override
        public long toMicros(final long duration) {
            return timeUnitValue().toMicros(duration);
        }

        @Override
        public long toMillis(final long duration) {
            return timeUnitValue().toMillis(duration);
        }

        @Override
        public long toSeconds(final long duration) {
            return timeUnitValue().toSeconds(duration);
        }

        @Override
        public long toMinutes(final long duration) {
            return timeUnitValue().toMinutes(duration);
        }

        @Override
        public long toHours(final long duration) {
            return timeUnitValue().toHours(duration);
        }

        @Override
        public long toDays(final long duration) {
            return duration;
        }

        @Override
        public long toWeeks(final long duration) {
            return duration / DAYS_IN_WEEK;
        }

        @Override
        public long toMonths(final long duration) {
            return duration / DAYS_IN_MONTH;
        }

        @Override
        public long toYears(final long duration) {
            return duration / DAYS_IN_YEAR;
        }

        @Override
        public void sleep(final long timeout) throws InterruptedException {
            timeUnitValue().sleep(timeout);
        }

    },
    HOURS("HOUR", "H") {
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
        public DurationField durationFieldValue() {
            return FDates.getDefaultTimeZone().getDurationFieldHours();
        }

        @Override
        public TimeUnit timeUnitValue() {
            return TimeUnit.HOURS;
        }

        @Override
        public long convert(final long duration, final FTimeUnit timeUnit) {
            return timeUnit.toHours(duration);
        }

        @Override
        public long toNanos(final long duration) {
            return timeUnitValue().toNanos(duration);
        }

        @Override
        public long toMicros(final long duration) {
            return timeUnitValue().toMicros(duration);
        }

        @Override
        public long toMillis(final long duration) {
            return timeUnitValue().toMillis(duration);
        }

        @Override
        public long toSeconds(final long duration) {
            return timeUnitValue().toSeconds(duration);
        }

        @Override
        public long toMinutes(final long duration) {
            return timeUnitValue().toMinutes(duration);
        }

        @Override
        public long toHours(final long duration) {
            return duration;
        }

        @Override
        public long toDays(final long duration) {
            return timeUnitValue().toDays(duration);
        }

        @Override
        public long toWeeks(final long duration) {
            return DAYS.toWeeks(toDays(duration));
        }

        @Override
        public long toMonths(final long duration) {
            return DAYS.toMonths(toDays(duration));
        }

        @Override
        public long toYears(final long duration) {
            return DAYS.toYears(toDays(duration));
        }

        @Override
        public void sleep(final long timeout) throws InterruptedException {
            timeUnitValue().sleep(timeout);
        }

    },
    MINUTES("MINUTE", "MIN", "MINS", "M") {
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
        public DurationField durationFieldValue() {
            return FDates.getDefaultTimeZone().getDurationFieldMinutes();
        }

        @Override
        public TimeUnit timeUnitValue() {
            return TimeUnit.MINUTES;
        }

        @Override
        public long convert(final long duration, final FTimeUnit timeUnit) {
            return timeUnit.toMinutes(duration);
        }

        @Override
        public long toNanos(final long duration) {
            return timeUnitValue().toNanos(duration);
        }

        @Override
        public long toMicros(final long duration) {
            return timeUnitValue().toMicros(duration);
        }

        @Override
        public long toMillis(final long duration) {
            return timeUnitValue().toMillis(duration);
        }

        @Override
        public long toSeconds(final long duration) {
            return timeUnitValue().toSeconds(duration);
        }

        @Override
        public long toMinutes(final long duration) {
            return duration;
        }

        @Override
        public long toHours(final long duration) {
            return timeUnitValue().toHours(duration);
        }

        @Override
        public long toDays(final long duration) {
            return timeUnitValue().toDays(duration);
        }

        @Override
        public long toWeeks(final long duration) {
            return DAYS.toWeeks(toDays(duration));
        }

        @Override
        public long toMonths(final long duration) {
            return DAYS.toMonths(toDays(duration));
        }

        @Override
        public long toYears(final long duration) {
            return DAYS.toYears(toDays(duration));
        }

        @Override
        public void sleep(final long timeout) throws InterruptedException {
            timeUnitValue().sleep(timeout);
        }

    },
    SECONDS("SECOND", "SEC", "SECS", "S") {
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
        public DurationField durationFieldValue() {
            return FDates.getDefaultTimeZone().getDurationFieldSeconds();
        }

        @Override
        public TimeUnit timeUnitValue() {
            return TimeUnit.SECONDS;
        }

        @Override
        public long convert(final long duration, final FTimeUnit timeUnit) {
            return timeUnit.toSeconds(duration);
        }

        @Override
        public long toNanos(final long duration) {
            return timeUnitValue().toNanos(duration);
        }

        @Override
        public long toMicros(final long duration) {
            return timeUnitValue().toMicros(duration);
        }

        @Override
        public long toMillis(final long duration) {
            return timeUnitValue().toMillis(duration);
        }

        @Override
        public long toSeconds(final long duration) {
            return duration;
        }

        @Override
        public long toMinutes(final long duration) {
            return timeUnitValue().toMinutes(duration);
        }

        @Override
        public long toHours(final long duration) {
            return timeUnitValue().toHours(duration);
        }

        @Override
        public long toDays(final long duration) {
            return timeUnitValue().toDays(duration);
        }

        @Override
        public long toWeeks(final long duration) {
            return DAYS.toWeeks(toDays(duration));
        }

        @Override
        public long toMonths(final long duration) {
            return DAYS.toMonths(toDays(duration));
        }

        @Override
        public long toYears(final long duration) {
            return DAYS.toYears(toDays(duration));
        }

        @Override
        public void sleep(final long timeout) throws InterruptedException {
            timeUnitValue().sleep(timeout);
        }

    },
    MILLISECONDS("MILLISECOND", "MILLIS", "MS") {
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
        public DurationField durationFieldValue() {
            return FDates.getDefaultTimeZone().getDurationFieldMilliseconds();
        }

        @Override
        public TimeUnit timeUnitValue() {
            return TimeUnit.MILLISECONDS;
        }

        @Override
        public long convert(final long duration, final FTimeUnit timeUnit) {
            return timeUnit.toMillis(duration);
        }

        @Override
        public long toNanos(final long duration) {
            return timeUnitValue().toNanos(duration);
        }

        @Override
        public long toMicros(final long duration) {
            return timeUnitValue().toMicros(duration);
        }

        @Override
        public long toMillis(final long duration) {
            return duration;
        }

        @Override
        public long toSeconds(final long duration) {
            return timeUnitValue().toSeconds(duration);
        }

        @Override
        public long toMinutes(final long duration) {
            return timeUnitValue().toMinutes(duration);
        }

        @Override
        public long toHours(final long duration) {
            return timeUnitValue().toHours(duration);
        }

        @Override
        public long toDays(final long duration) {
            return timeUnitValue().toDays(duration);
        }

        @Override
        public long toWeeks(final long duration) {
            return DAYS.toWeeks(toDays(duration));
        }

        @Override
        public long toMonths(final long duration) {
            return DAYS.toMonths(toDays(duration));
        }

        @Override
        public long toYears(final long duration) {
            return DAYS.toYears(toDays(duration));
        }

        @Override
        public void sleep(final long timeout) throws InterruptedException {
            timeUnitValue().sleep(timeout);
        }

    },
    MICROSECONDS("MICROSECOND", "MICROS", "MICRO") {
        @Override
        public int calendarValue() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ChronoUnit javaTimeValue() {
            return ChronoUnit.MICROS;
        }

        @Override
        public DurationFieldType jodaTimeValue() {
            throw new UnsupportedOperationException();
        }

        @Override
        public DurationField durationFieldValue() {
            throw new UnsupportedOperationException();
        }

        @Override
        public TimeUnit timeUnitValue() {
            return TimeUnit.MICROSECONDS;
        }

        @Override
        public long convert(final long duration, final FTimeUnit timeUnit) {
            return timeUnit.toMicros(duration);
        }

        @Override
        public long toNanos(final long duration) {
            return timeUnitValue().toNanos(duration);
        }

        @Override
        public long toMicros(final long duration) {
            return duration;
        }

        @Override
        public long toMillis(final long duration) {
            return timeUnitValue().toMillis(duration);
        }

        @Override
        public long toSeconds(final long duration) {
            return timeUnitValue().toSeconds(duration);
        }

        @Override
        public long toMinutes(final long duration) {
            return timeUnitValue().toMinutes(duration);
        }

        @Override
        public long toHours(final long duration) {
            return timeUnitValue().toHours(duration);
        }

        @Override
        public long toDays(final long duration) {
            return timeUnitValue().toDays(duration);
        }

        @Override
        public long toWeeks(final long duration) {
            return DAYS.toWeeks(toDays(duration));
        }

        @Override
        public long toMonths(final long duration) {
            return DAYS.toMonths(toDays(duration));
        }

        @Override
        public long toYears(final long duration) {
            return DAYS.toYears(toDays(duration));
        }

        @Override
        public void sleep(final long timeout) throws InterruptedException {
            timeUnitValue().sleep(timeout);
        }

    },
    NANOSECONDS("NANOSECOND", "NANOS", "NS") {
        @Override
        public int calendarValue() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ChronoUnit javaTimeValue() {
            return ChronoUnit.NANOS;
        }

        @Override
        public DurationFieldType jodaTimeValue() {
            throw new UnsupportedOperationException();
        }

        @Override
        public DurationField durationFieldValue() {
            throw new UnsupportedOperationException();
        }

        @Override
        public TimeUnit timeUnitValue() {
            return TimeUnit.NANOSECONDS;
        }

        @Override
        public long convert(final long duration, final FTimeUnit timeUnit) {
            return timeUnit.toNanos(duration);
        }

        @Override
        public long toNanos(final long duration) {
            return duration;
        }

        @Override
        public long toMicros(final long duration) {
            return timeUnitValue().toMicros(duration);
        }

        @Override
        public long toMillis(final long duration) {
            return timeUnitValue().toMillis(duration);
        }

        @Override
        public long toSeconds(final long duration) {
            return timeUnitValue().toSeconds(duration);
        }

        @Override
        public long toMinutes(final long duration) {
            return timeUnitValue().toMinutes(duration);
        }

        @Override
        public long toHours(final long duration) {
            return timeUnitValue().toHours(duration);
        }

        @Override
        public long toDays(final long duration) {
            return timeUnitValue().toDays(duration);
        }

        @Override
        public long toWeeks(final long duration) {
            return DAYS.toWeeks(toDays(duration));
        }

        @Override
        public long toMonths(final long duration) {
            return DAYS.toMonths(toDays(duration));
        }

        @Override
        public long toYears(final long duration) {
            return DAYS.toYears(toDays(duration));
        }

        @Override
        public void sleep(final long timeout) throws InterruptedException {
            timeUnitValue().sleep(timeout);
        }

    };

    public static final int YEARS_IN_MILLENIUM = 1000;
    public static final int YEARS_IN_CENTURY = 100;
    public static final int YEARS_IN_DECADE = 10;
    public static final int CENTURIES_IN_MILLENIUM = YEARS_IN_MILLENIUM / YEARS_IN_CENTURY;
    public static final int DECADES_IN_CENTURY = YEARS_IN_CENTURY / YEARS_IN_DECADE;
    public static final int DAYS_IN_YEAR = 365;
    public static final int MONTHS_IN_YEAR = 12;
    public static final int DAYS_IN_WEEK = 7;
    public static final int DAYS_IN_MONTH = DAYS_IN_YEAR / MONTHS_IN_YEAR;
    public static final int WEEKS_IN_MONTH = DAYS_IN_MONTH / DAYS_IN_WEEK;
    public static final int WEEKS_IN_YEAR = DAYS_IN_YEAR / DAYS_IN_WEEK;
    public static final int HOURS_IN_DAY = 24;
    public static final int MINUTES_IN_HOUR = 60;
    public static final int SECONDS_IN_MINUTE = 60;
    public static final int MILLISECONDS_IN_SECOND = 1000;
    public static final int MICROSECONDS_IN_MILLISECOND = 1000;
    public static final int NANOSECONDS_IN_MICROSECOND = 1000;

    public static final int MILLISECONDS_IN_MINUTE = MILLISECONDS_IN_SECOND * SECONDS_IN_MINUTE;
    public static final int MILLISECONDS_IN_HOUR = MILLISECONDS_IN_MINUTE * MINUTES_IN_HOUR;
    public static final int MILLISECONDS_IN_DAY = MILLISECONDS_IN_HOUR * HOURS_IN_DAY;

    private static final Map<TimeUnit, FTimeUnit> TIME_UNIT_LOOKUP = new HashMap<TimeUnit, FTimeUnit>();
    private static final Map<Integer, FTimeUnit> CALENDAR_LOOKUP = new HashMap<Integer, FTimeUnit>();
    private static final Map<ChronoUnit, FTimeUnit> JAVA_TIME_LOOKUP = new HashMap<ChronoUnit, FTimeUnit>();
    private static final Map<DurationFieldType, FTimeUnit> JODA_TIME_LOOKUP = new HashMap<DurationFieldType, FTimeUnit>();
    private static final Map<Long, FTimeUnit> DURATION_NANOS_LOOKUP = new HashMap<Long, FTimeUnit>();
    private static final Map<String, FTimeUnit> ALIAS_LOOKUP = new HashMap<>();

    static {
        for (final FTimeUnit f : values()) {
            try {
                TIME_UNIT_LOOKUP.put(f.timeUnitValue(), f);
            } catch (final UnsupportedOperationException e) { //SUPPRESS CHECKSTYLE empty block
                //ignore
            }
            try {
                CALENDAR_LOOKUP.put(f.calendarValue(), f);
            } catch (final UnsupportedOperationException e) {//SUPPRESS CHECKSTYLE empty block
                //ignore
            }
            try {
                JAVA_TIME_LOOKUP.put(f.javaTimeValue(), f);
            } catch (final UnsupportedOperationException e) {//SUPPRESS CHECKSTYLE empty block
                //ignore
            }
            try {
                JODA_TIME_LOOKUP.put(f.jodaTimeValue(), f);
            } catch (final UnsupportedOperationException e) {//SUPPRESS CHECKSTYLE empty block
                //ignore
            }
            DURATION_NANOS_LOOKUP.put(f.toNanos(1), f);
            ALIAS_LOOKUP.put(f.name(), f);
            for (final String alias : f.getAliases()) {
                if (ALIAS_LOOKUP.put(alias, f) != null) {
                    throw new IllegalArgumentException("Duplicate alias: " + alias);
                }
            }
        }
    }

    private String[] aliases;

    FTimeUnit(final String... aliases) {
        this.aliases = aliases;
    }

    public String[] getAliases() {
        return aliases;
    }

    public abstract int calendarValue();

    public abstract TimeUnit timeUnitValue();

    public abstract ChronoUnit javaTimeValue();

    public abstract DurationFieldType jodaTimeValue();

    public Duration durationValue() {
        return new Duration(1, this);
    }

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

    public abstract void sleep(long timeout) throws InterruptedException;

    public abstract long convert(long duration, FTimeUnit timeUnit);

    public abstract long toNanos(long duration);

    public abstract long toMicros(long duration);

    public abstract long toMillis(long duration);

    public abstract long toSeconds(long duration);

    public abstract long toMinutes(long duration);

    public abstract long toHours(long duration);

    public abstract long toDays(long duration);

    public abstract long toWeeks(long duration);

    public abstract long toMonths(long duration);

    public abstract long toYears(long duration);

    public abstract DurationField durationFieldValue();

    public static FTimeUnit valueOf(final Duration duration) {
        return DURATION_NANOS_LOOKUP.get(duration.longValue(FTimeUnit.NANOSECONDS));
    }

    public static FTimeUnit valueOfAlias(final String nameOrAlias) {
        return ALIAS_LOOKUP.get(nameOrAlias.toUpperCase());
    }

}
