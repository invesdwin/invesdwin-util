package de.invesdwin.util.time.date;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.concurrent.ThreadSafe;
import javax.persistence.Transient;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.ReadableDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import de.invesdwin.norva.marker.IDate;
import de.invesdwin.util.collections.loadingcache.historical.IHistoricalEntry;
import de.invesdwin.util.collections.loadingcache.historical.IHistoricalValue;
import de.invesdwin.util.lang.ADelegateComparator;
import de.invesdwin.util.lang.Strings;
import de.invesdwin.util.lang.buffer.IByteBuffer;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.decimal.scaled.Percent;
import de.invesdwin.util.time.date.holiday.IHolidayManager;
import de.invesdwin.util.time.date.millis.FDateMillis;
import de.invesdwin.util.time.date.timezone.FTimeZone;
import de.invesdwin.util.time.duration.Duration;

/**
 * FDate stands for an immutable Fast Date implementation by utilizing heavy caching.
 */
@ThreadSafe
public class FDate
        implements IDate, Serializable, Cloneable, Comparable<Object>, IHistoricalValue<FDate>, IFDateProvider {

    public static final ADelegateComparator<FDate> COMPARATOR = new ADelegateComparator<FDate>() {
        @Override
        protected Comparable<?> getCompareCriteria(final FDate e) {
            return e;
        }
    };

    public static final int BYTES = Long.BYTES;
    /**
     * Somehow leveldb-jni does not like going higher than this year...
     */
    public static final int MAX_YEAR = 5555;
    public static final int MIN_YEAR = 1;

    public static final FDate MIN_DATE = FDateBuilder.newDate(MIN_YEAR);
    public static final FDate MAX_DATE = FDateBuilder.newDate(MAX_YEAR);

    public static final int COUNT_NANOSECONDS_IN_MILLISECOND = Integers.checkedCast(FTimeUnit.MILLISECONDS.toNanos(1));
    public static final int COUNT_NANOSECONDS_IN_MICROSECOND = Integers.checkedCast(FTimeUnit.MICROSECONDS.toNanos(1));
    public static final int COUNT_WEEKEND_DAYS_IN_WEEK = 2;
    public static final int COUNT_WORKDAYS_IN_YEAR = FTimeUnit.DAYS_IN_YEAR
            - FTimeUnit.WEEKS_IN_YEAR * COUNT_WEEKEND_DAYS_IN_WEEK;
    public static final int COUNT_WORKDAYS_IN_MONTH = COUNT_WORKDAYS_IN_YEAR / FTimeUnit.MONTHS_IN_YEAR;
    public static final int COUNT_WORKDAYS_IN_WEEK = COUNT_WORKDAYS_IN_MONTH / FTimeUnit.WEEKS_IN_MONTH;

    /**
     * https://en.wikipedia.org/wiki/Trading_day
     */
    public static final int COUNT_TRADING_HOLIDAYS_PER_YEAR = 9;
    public static final int COUNT_TRADING_DAYS_IN_YEAR = COUNT_WORKDAYS_IN_YEAR - COUNT_TRADING_HOLIDAYS_PER_YEAR;
    public static final int COUNT_DAYS_WITHOUT_TRADING_IN_YEAR = FTimeUnit.DAYS_IN_YEAR - COUNT_TRADING_DAYS_IN_YEAR;

    public static final Percent PERCENT_DAYS_WITHOUT_TRADING_IN_YEAR = new Percent(
            FDate.COUNT_DAYS_WITHOUT_TRADING_IN_YEAR, FTimeUnit.DAYS_IN_YEAR);
    public static final Percent PERCENT_TRADING_DAYS_IN_YEAR = new Percent(FDate.COUNT_TRADING_DAYS_IN_YEAR,
            FTimeUnit.DAYS_IN_YEAR);

    /*
     * ISO 8601 date-time format, example: "2003-04-01T13:01:02"
     */
    public static final String FORMAT_ISO_DATE = "yyyy-MM-dd";
    public static final String FORMAT_ISO_TIME = "HH:mm:ss";
    public static final String FORMAT_ISO_TIME_MS = FORMAT_ISO_TIME + ".SSS";
    public static final String FORMAT_ISO_DATE_TIME = FORMAT_ISO_DATE + "'T'" + FORMAT_ISO_TIME;
    public static final String FORMAT_ISO_DATE_TIME_SPACE = FORMAT_ISO_DATE + " " + FORMAT_ISO_TIME;
    public static final String FORMAT_ISO_DATE_TIME_MS = FORMAT_ISO_DATE + "'T'" + FORMAT_ISO_TIME_MS;
    public static final String FORMAT_ISO_DATE_TIME_MS_SPACE = FORMAT_ISO_DATE + " " + FORMAT_ISO_TIME_MS;

    public static final String FORMAT_NUMBER_DATE = "yyyyMMdd";
    public static final String FORMAT_NUMBER_TIME = "HHmmss";
    public static final String FORMAT_NUMBER_TIME_MS = FORMAT_NUMBER_TIME + "SSS";
    public static final String FORMAT_NUMBER_DATE_TIME = FORMAT_NUMBER_DATE + FORMAT_NUMBER_TIME;
    public static final String FORMAT_NUMBER_DATE_TIME_MS = FORMAT_NUMBER_DATE + FORMAT_NUMBER_TIME_MS;

    public static final String FORMAT_UNDERSCORE_DATE_TIME_MS = "yyyy_MM_dd_HH_mm_ss_SSS";

    public static final String FORMAT_GERMAN_DATE = "dd.MM.yyyy";
    public static final String FORMAT_GERMAN_DATE_TIME = FORMAT_GERMAN_DATE + " " + FORMAT_ISO_TIME;
    public static final String FORMAT_GERMAN_DATE_TIME_MS = FORMAT_GERMAN_DATE + " " + FORMAT_ISO_TIME_MS;

    public static final ADelegateComparator<FDate> DATE_COMPARATOR = new ADelegateComparator<FDate>() {
        @Override
        protected Comparable<?> getCompareCriteria(final FDate e) {
            return e;
        }
    };

    private final long millis;
    @Transient
    private transient Object extension;

    public FDate() {
        this(System.currentTimeMillis());
    }

    public FDate(final long millis) {
        this.millis = millis;
    }

    protected FDate(final FDate date) {
        this.millis = date.millis;
    }

    public FDate(final ReadableDateTime jodaTime) {
        this(jodaTime.getMillis());
    }

    public FDate(final LocalDateTime jodaTime) {
        this(jodaTime.toDateTime().getMillis());
    }

    public FDate(final java.time.ZonedDateTime javaTime) {
        this(javaTime.toInstant().toEpochMilli());
    }

    public FDate(final java.time.Instant javaTime) {
        this(javaTime.toEpochMilli());
    }

    public FDate(final Calendar calendar) {
        this(calendar.getTime());
    }

    public FDate(final Date date) {
        this(date.getTime());
    }

    public int getYear(final FTimeZone timeZone) {
        return FDateMillis.getYear(millis, timeZone);
    }

    public int getYear() {
        return FDateMillis.getYear(millis);
    }

    public int getMonth(final FTimeZone timeZone) {
        return FDateMillis.getMonth(millis, timeZone);
    }

    public int getMonth() {
        return FDateMillis.getMonth(millis);
    }

    public FMonth getFMonth(final FTimeZone timeZone) {
        return FDateMillis.getFMonth(millis, timeZone);
    }

    public FMonth getFMonth() {
        return FDateMillis.getFMonth(millis);
    }

    public int getDay(final FTimeZone timeZone) {
        return FDateMillis.getDay(millis, timeZone);
    }

    public int getDay() {
        return FDateMillis.getDay(millis);
    }

    public int getWeekday(final FTimeZone timeZone) {
        return FDateMillis.getWeekday(millis, timeZone);
    }

    public int getWeekday() {
        return FDateMillis.getWeekday(millis);
    }

    public FWeekday getFWeekday(final FTimeZone timeZone) {
        return FDateMillis.getFWeekday(millis, timeZone);
    }

    public FWeekday getFWeekday() {
        return FDateMillis.getFWeekday(millis);
    }

    public int getHour(final FTimeZone timeZone) {
        return FDateMillis.getHour(millis, timeZone);
    }

    public int getHour() {
        return FDateMillis.getHour(millis);
    }

    public int getMinute() {
        return FDateMillis.getMinute(millis);
    }

    public int getSecond() {
        return FDateMillis.getSecond(millis);
    }

    public int getMillisecond() {
        return FDateMillis.getMillisecond(millis);
    }

    public FTimeZone getTimeZone() {
        return FDates.getDefaultTimeZone();
    }

    public FDate setYear(final int year, final FTimeZone timeZone) {
        return new FDate(FDateMillis.setYear(millis, year, timeZone));
    }

    public FDate setYear(final int year) {
        return new FDate(FDateMillis.setYear(millis, year));
    }

    public FDate setMonth(final int month, final FTimeZone timeZone) {
        return new FDate(FDateMillis.setMonth(millis, month, timeZone));
    }

    public FDate setMonth(final int month) {
        return new FDate(FDateMillis.setMonth(millis, month));
    }

    public FDate setFMonth(final FMonth month, final FTimeZone timeZone) {
        return new FDate(FDateMillis.setFMonth(millis, month, timeZone));
    }

    public FDate setFMonth(final FMonth month) {
        return new FDate(FDateMillis.setFMonth(millis, month));
    }

    public FDate setDay(final int day, final FTimeZone timeZone) {
        return new FDate(FDateMillis.setDay(millis, day, timeZone));
    }

    public FDate setDay(final int day) {
        return new FDate(FDateMillis.setDay(millis, day));
    }

    public FDate setWeekday(final int weekday, final FTimeZone timeZone) {
        return new FDate(FDateMillis.setWeekday(millis, weekday, timeZone));
    }

    public FDate setWeekday(final int weekday) {
        return new FDate(FDateMillis.setWeekday(millis, weekday));
    }

    public FDate setFWeekday(final FWeekday weekday, final FTimeZone timeZone) {
        return new FDate(FDateMillis.setFWeekday(millis, weekday, timeZone));
    }

    public FDate setFWeekday(final FWeekday weekday) {
        return new FDate(FDateMillis.setFWeekday(millis, weekday));
    }

    public FDate setFWeekTime(final FWeekTime weekTime, final FTimeZone timeZone) {
        return new FDate(FDateMillis.setFWeekTime(millis, weekTime, timeZone));
    }

    public FDate setFWeekTime(final FWeekTime weekTime) {
        return new FDate(FDateMillis.setFWeekTime(millis, weekTime));
    }

    public FDate setFDayTime(final FDayTime dayTime, final FTimeZone timeZone) {
        return new FDate(FDateMillis.setFDayTime(millis, dayTime, timeZone));
    }

    public FDate setFDayTime(final FDayTime dayTime) {
        return new FDate(FDateMillis.setFDayTime(millis, dayTime));
    }

    public FDate setTime(final FDate time, final FTimeZone timeZone) {
        return new FDate(FDateMillis.setTime(millis, time.millisValue(), timeZone));
    }

    public FDate setTime(final FDate time) {
        return new FDate(FDateMillis.setTime(millis, time.millisValue()));
    }

    public FDate setHour(final int hour, final FTimeZone timeZone) {
        return new FDate(FDateMillis.setHour(millis, hour, timeZone));
    }

    public FDate setHour(final int hour) {
        return new FDate(FDateMillis.setHour(millis, hour));
    }

    public FDate setMinute(final int minute) {
        return new FDate(FDateMillis.setMinute(millis, minute));
    }

    public FDate setSecond(final int second) {
        return new FDate(FDateMillis.setSecond(millis, second));
    }

    public FDate setMillisecond(final int millisecond) {
        return new FDate(FDateMillis.setMillisecond(millis, millisecond));
    }

    public FDate addYears(final int years, final FTimeZone timeZone) {
        if (years == 0) {
            return this;
        }
        return new FDate(FDateMillis.addYears(millis, years, timeZone));
    }

    public FDate addYears(final int years) {
        if (years == 0) {
            return this;
        }
        return new FDate(FDateMillis.addYears(millis, years));
    }

    public FDate addMonths(final int months, final FTimeZone timeZone) {
        if (months == 0) {
            return this;
        }
        return new FDate(FDateMillis.addMonths(millis, months, timeZone));
    }

    public FDate addMonths(final int months) {
        if (months == 0) {
            return this;
        }
        return new FDate(FDateMillis.addMonths(millis, months));
    }

    public FDate addWeeks(final int weeks) {
        if (weeks == 0) {
            return this;
        }
        return new FDate(FDateMillis.addWeeks(millis, weeks));
    }

    public FDate addDays(final int days) {
        if (days == 0) {
            return this;
        }
        return new FDate(FDateMillis.addDays(millis, days));
    }

    public FDate addHours(final int hours) {
        if (hours == 0) {
            return this;
        }
        return new FDate(FDateMillis.addHours(millis, hours));
    }

    public FDate addMinutes(final int minutes) {
        if (minutes == 0) {
            return this;
        }
        return new FDate(FDateMillis.addMinutes(millis, minutes));
    }

    public FDate addSeconds(final int seconds) {
        if (seconds == 0) {
            return this;
        }
        return new FDate(FDateMillis.addSeconds(millis, seconds));
    }

    public FDate addMilliseconds(final long milliseconds) {
        if (milliseconds == 0) {
            return this;
        }
        return new FDate(FDateMillis.addMilliseconds(millis, milliseconds));
    }

    public int get(final FDateField field, final FTimeZone timeZone) {
        return FDateMillis.get(millis, field, timeZone);
    }

    public int get(final FDateField field) {
        return FDateMillis.get(millis, field);
    }

    public FDate set(final FDateField field, final int value, final FTimeZone timeZone) {
        if (value == 0) {
            return this;
        }
        return new FDate(FDateMillis.set(millis, field, value, timeZone));
    }

    public FDate set(final FDateField field, final int value) {
        if (value == 0) {
            return this;
        }
        return new FDate(FDateMillis.set(millis, field, value));
    }

    public FDate add(final FTimeUnit field, final int value, final FTimeZone timeZone) {
        if (value == 0) {
            return this;
        }
        return new FDate(FDateMillis.add(millis, field, value, timeZone));
    }

    public FDate add(final FTimeUnit field, final int value) {
        if (value == 0) {
            return this;
        }
        return new FDate(FDateMillis.add(millis, field, value));
    }

    public FDate add(final Duration duration) {
        return duration.addTo(this);
    }

    public FDate subtract(final Duration duration) {
        return duration.subtractFrom(this);
    }

    public int getWeekNumberOfYear(final FTimeZone timeZone) {
        return FDateMillis.getWeekNumberOfYear(millis, timeZone);
    }

    public int getWeekNumberOfYear() {
        return FDateMillis.getWeekNumberOfYear(millis);
    }

    public int getWeekNumberOfMonth(final FTimeZone timeZone) {
        return FDateMillis.getWeekNumberOfMonth(millis, timeZone);
    }

    public int getWeekNumberOfMonth() {
        return FDateMillis.getWeekNumberOfMonth(millis);
    }

    public FDate truncate(final FDateField field, final FTimeZone timeZone) {
        return new FDate(FDateMillis.truncate(millis, field, timeZone));
    }

    public FDate truncate(final FDateField field) {
        return new FDate(FDateMillis.truncate(millis, field));
    }

    public FDate truncate(final FTimeUnit timeUnit, final FTimeZone timeZone) {
        return new FDate(FDateMillis.truncate(millis, timeUnit, timeZone));
    }

    public FDate truncate(final FTimeUnit timeUnit) {
        return new FDate(FDateMillis.truncate(millis, timeUnit));
    }

    /**
     * sets hour, minute, second and millisecond each to 0.
     */
    public FDate withoutTime() {
        return new FDate(FDateMillis.withoutTime(millis));
    }

    public FDate withoutTime(final FTimeZone timeZone) {
        return new FDate(FDateMillis.withoutTime(millis, timeZone));
    }

    /**
     * Pretend that this date is inside the given timezone. E.g. UTC will add 2 hours for becoming EET.
     */
    public FDate applyTimeZoneOffset(final FTimeZone timeZone) {
        if (timeZone == null || timeZone.equals(getTimeZone())) {
            return this;
        }
        return new FDate(FDateMillis.applyTimeZoneOffset(millis, timeZone));
    }

    /**
     * Pretend that this date is inside the given timezone. E.g. UTC will add 2 hours for becoming EET.
     */
    public FDate applyTimeZoneOffset(final long timeZoneOffsetMilliseconds) {
        if (timeZoneOffsetMilliseconds == 0) {
            return this;
        }
        return new FDate(FDateMillis.applyTimeZoneOffset(millis, timeZoneOffsetMilliseconds));
    }

    public long getTimeZoneOffsetMilliseconds(final FTimeZone timeZone) {
        return FDateMillis.getTimeZoneOffsetMilliseconds(millis, timeZone);
    }

    /**
     * Go back to the default timezone for a data that was converted into another timezone.
     */
    public FDate revertTimeZoneOffset(final FTimeZone timeZone) {
        if (timeZone == null || timeZone.equals(getTimeZone())) {
            return this;
        }
        return new FDate(FDateMillis.revertTimeZoneOffset(millis, timeZone));
    }

    /**
     * Go back to the default timezone for a data that was converted into another timezone.
     */
    public FDate revertTimeZoneOffset(final long timeZoneOffsetMilliseconds) {
        if (timeZoneOffsetMilliseconds == 0) {
            return this;
        }
        return new FDate(FDateMillis.revertTimeZoneOffset(millis, timeZoneOffsetMilliseconds));
    }

    /**
     * sets hour, minute, second and millisecond each to 23:59:999.
     */
    public FDate atEndOfDay() {
        return new FDate(FDateMillis.atEndOfDay(millis));
    }

    public FDate atEndOfDay(final FTimeZone timeZone) {
        return new FDate(FDateMillis.atEndOfDay(millis, timeZone));
    }

    /**
     * Returns the number of milliseconds since January 1, 1970, 00:00:00 GMT.
     */
    public long millisValue() {
        return millis;
    }

    public Date dateValue() {
        return FDateMillis.dateValue(millis);
    }

    public long longValue(final FTimeUnit timeUnit) {
        return FDateMillis.longValue(millis, timeUnit);
    }

    public Calendar calendarValue() {
        return FDateMillis.calendarValue(millis);
    }

    public LocalDateTime jodaTimeValue() {
        return FDateMillis.jodaTimeValue(millis);
    }

    public DateTime jodaTimeValueZoned() {
        return FDateMillis.jodaTimeValueZoned(millis);
    }

    public java.time.ZonedDateTime javaTimeValueZoned() {
        return FDateMillis.javaTimeValueZoned(millis);
    }

    public java.time.LocalDateTime javaTimeValue() {
        return FDateMillis.javaTimeValue(millis);
    }

    public java.time.LocalDate javaDateValue() {
        return FDateMillis.javaDateValue(millis);
    }

    public static FDate valueOf(final Long millis) {
        if (millis != null) {
            return new FDate(millis);
        } else {
            return null;
        }
    }

    public static FDate valueOf(final Long value, final FTimeUnit timeUnit) {
        if (value != null) {
            return new FDate(timeUnit.toMillis(value));
        } else {
            return null;
        }
    }

    public static FDate valueOf(final Date date) {
        if (date != null) {
            return new FDate(date);
        } else {
            return null;
        }
    }

    public static FDate valueOf(final Calendar calendar) {
        if (calendar != null) {
            return new FDate(calendar);
        } else {
            return null;
        }
    }

    public static FDate valueOf(final ReadableDateTime jodaTime) {
        if (jodaTime != null) {
            return new FDate(jodaTime);
        } else {
            return null;
        }
    }

    public static FDate valueOf(final LocalDateTime jodaTime) {
        if (jodaTime != null) {
            return new FDate(jodaTime);
        } else {
            return null;
        }
    }

    public static FDate valueOf(final String str, final String... parsePatterns) {
        return valueOf(str, (FTimeZone) null, null, parsePatterns);
    }

    public static FDate valueOf(final String str, final String parsePattern) {
        return valueOf(str, (FTimeZone) null, null, parsePattern);
    }

    public static FDate valueOf(final String str, final Locale locale, final String... parsePatterns) {
        return valueOf(str, (FTimeZone) null, locale, parsePatterns);
    }

    public static FDate valueOf(final String str, final FTimeZone timeZone, final String... parsePatterns) {
        return valueOf(str, timeZone, null, parsePatterns);
    }

    public static FDate valueOf(final String str, final FTimeZone timeZone, final Locale locale,
            final String... parsePatterns) {
        if (parsePatterns == null || parsePatterns.length == 0) {
            throw new IllegalArgumentException("at least one parsePattern is needed");
        }
        for (final String parsePattern : parsePatterns) {
            try {
                return valueOf(str, timeZone, locale, parsePattern);
            } catch (final IllegalArgumentException e) {
                continue;
            }
        }
        throw new IllegalArgumentException("None of the parsePatterns [" + Arrays.toString(parsePatterns)
                + "] matches the date string [" + str + "]");
    }

    public static FDate valueOf(final String str, final FTimeZone timeZone, final String parsePattern) {
        return valueOf(str, timeZone, null, parsePattern);
    }

    public static FDate valueOf(final String str, final Locale locale, final String parsePattern) {
        return valueOf(str, (FTimeZone) null, locale, parsePattern);
    }

    public static FDate valueOf(final String str, final FTimeZone timeZone, final Locale locale,
            final String parsePattern) {
        if (Strings.isBlank(str)) {
            return null;
        }
        DateTimeFormatter df = DateTimeFormat.forPattern(parsePattern);
        if (timeZone != null) {
            df = df.withZone(timeZone.getDateTimeZone());
        } else {
            df = df.withZone(FDates.getDefaultTimeZone().getDateTimeZone());
        }
        if (locale != null) {
            df = df.withLocale(locale);
        }
        final DateTime date = df.parseDateTime(str);
        return new FDate(date);
    }

    public static List<FDate> valueOf(final Collection<Date> list) {
        final List<FDate> dates = new ArrayList<FDate>(list.size());
        for (final Date e : list) {
            dates.add(valueOf(e));
        }
        return dates;
    }

    public static List<FDate> valueOf(final Date... list) {
        final List<FDate> dates = new ArrayList<FDate>(list.length);
        for (final Date e : list) {
            dates.add(valueOf(e));
        }
        return dates;
    }

    public static FDate now() {
        return new FDate();
    }

    public static FDate today(final FTimeZone timeZone) {
        return new FDate(FDateMillis.today(timeZone));
    }

    public static FDate today() {
        return new FDate(FDateMillis.today());
    }

    @Override
    public int hashCode() {
        return Long.hashCode(millis);
    }

    @Override
    public int compareTo(final Object o) {
        if (o instanceof FDate) {
            final FDate cO = (FDate) o;
            return compareToNotNullSafe(cO);
        } else {
            return 1;
        }
    }

    public int compareTo(final FDate o) {
        if (o == null) {
            return 1;
        }
        return compareToNotNullSafe(o);
    }

    public int compareToNotNullSafe(final FDate o) {
        return Long.compare(millis, o.millis);
    }

    public boolean equals(final FDate obj) {
        return obj != null && equalsNotNullSafe(obj);
    }

    public boolean equalsNotNullSafe(final FDate obj) {
        return millis == obj.millis;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof FDate) {
            final FDate cObj = (FDate) obj;
            return equalsNotNullSafe(cObj);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return FDateMillis.toString(millis);
    }

    public String toString(final FTimeZone timeZone) {
        return FDateMillis.toString(millis, timeZone);
    }

    public String toString(final String format) {
        return FDateMillis.toString(millis, format);
    }

    public String toString(final String format, final FTimeZone timeZone) {
        return FDateMillis.toString(millis, format, timeZone);
    }

    public boolean isBefore(final FDate other) {
        return other != null && isBeforeNotNullSafe(other);
    }

    public boolean isBeforeOrEqualTo(final FDate other) {
        return other != null && isBeforeOrEqualToNotNullSafe(other);
    }

    public boolean isAfter(final FDate other) {
        return other != null && isAfterNotNullSafe(other);
    }

    public boolean isAfterOrEqualTo(final FDate other) {
        return other != null && isAfterOrEqualToNotNullSafe(other);
    }

    public boolean isBeforeNotNullSafe(final FDate other) {
        return millis < other.millis;
    }

    public boolean isBeforeOrEqualToNotNullSafe(final FDate other) {
        return millis <= other.millis;
    }

    public boolean isAfterNotNullSafe(final FDate other) {
        return millis > other.millis;
    }

    public boolean isAfterOrEqualToNotNullSafe(final FDate other) {
        return millis >= other.millis;
    }

    @Override
    public FDate clone() {
        try {
            return (FDate) super.clone();
        } catch (final CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public FDate getFirstWeekdayOfMonth(final FWeekday weekday, final FTimeZone timeZone) {
        return new FDate(FDateMillis.getFirstWeekdayOfMonth(millis, weekday, timeZone));
    }

    public FDate getFirstWeekdayOfMonth(final FWeekday weekday) {
        return new FDate(FDateMillis.getFirstWeekdayOfMonth(millis, weekday));
    }

    public FDate getFirstWorkdayOfMonth(final IHolidayManager holidayManager, final FTimeZone timeZone) {
        return new FDate(FDateMillis.getFirstWorkdayOfMonth(millis, holidayManager, timeZone));
    }

    public FDate getFirstWorkdayOfMonth(final IHolidayManager holidayManager) {
        return new FDate(FDateMillis.getFirstWorkdayOfMonth(millis, holidayManager));
    }

    public FDate getNextHoliday(final IHolidayManager holidayManager, final FTimeZone timeZone) {
        final long offset = getTimeZoneOffsetMilliseconds(timeZone);
        return applyTimeZoneOffset(offset).getNextHoliday(holidayManager).revertTimeZoneOffset(offset);
    }

    public FDate getNextHoliday(final IHolidayManager holidayManager) {
        if (holidayManager == null) {
            return this;
        }
        FDate day = this.withoutTime();
        while (!holidayManager.isHoliday(day)) {
            day = day.addDays(1);
        }
        return day;
    }

    public FDate getNextNonHoliday(final IHolidayManager holidayManager, final FTimeZone timeZone) {
        final long offset = getTimeZoneOffsetMilliseconds(timeZone);
        return applyTimeZoneOffset(offset).getNextNonHoliday(holidayManager).revertTimeZoneOffset(offset);
    }

    public FDate getNextNonHoliday(final IHolidayManager holidayManager) {
        if (holidayManager == null) {
            return this;
        }
        FDate day = this.withoutTime();
        while (holidayManager.isHoliday(day)) {
            day = day.addDays(1);
        }
        return day;
    }

    public boolean isHoliday(final IHolidayManager holidayManager) {
        if (holidayManager == null) {
            return false;
        }
        return holidayManager.isHoliday(this);
    }

    public boolean isHoliday(final IHolidayManager holidayManager, final FTimeZone timeZone) {
        return applyTimeZoneOffset(timeZone).isHoliday(holidayManager);
    }

    public FDate addWorkdays(final int workdays, final IHolidayManager holidayManager, final FTimeZone timeZone) {
        final long offset = getTimeZoneOffsetMilliseconds(timeZone);
        return applyTimeZoneOffset(offset).addWorkdays(workdays, holidayManager).revertTimeZoneOffset(offset);
    }

    public FDate addWorkdays(final int workdays, final IHolidayManager holidayManager) {
        int workdaysToShift = Integers.abs(workdays);
        if (getFWeekday().isWeekend() || isHoliday(holidayManager)) {
            if (workdaysToShift > 1) {
                workdaysToShift--;
            }
        }
        final int shiftUnit;
        if (workdays >= 0) {
            shiftUnit = 1;
        } else {
            shiftUnit = -1;
        }
        int workdaysShifted = 0;
        FDate cur = this;
        while (workdaysShifted < workdaysToShift) {
            if (!cur.getFWeekday().isWeekend() && !cur.isHoliday(holidayManager)) {
                workdaysShifted++;
            }
            cur = cur.addDays(shiftUnit);
        }
        return cur;
    }

    public boolean isBetween(final FDate min, final FDate max) {
        return FDates.isBetween(this, min, max);
    }

    public FDate orHigher(final FDate other) {
        return FDates.max(this, other);
    }

    public FDate orLower(final FDate other) {
        return FDates.min(this, other);
    }

    /**
     * WARNING: for framework use only.
     */
    @Deprecated
    public Object getExtension() {
        return extension;
    }

    /**
     * This property allows to extend existing FDate instances and thus propagating meta information to external holders
     * of this instance. For example to carry the IndexedFDate information outside of the AHistoricalCache. The
     * extension and this instance should normally contain the same timestamp internally.
     * 
     * WARNING: for framework use only.
     */
    @Deprecated
    public void setExtension(final Object extension) {
        this.extension = extension;
    }

    @Override
    public IHistoricalEntry<FDate> asHistoricalEntry() {
        return new IHistoricalEntry<FDate>() {

            @Override
            public FDate getValue() {
                return FDate.this;
            }

            @Override
            public FDate getKey() {
                return FDate.this;
            }

            @Override
            public String toString() {
                return getKey() + " -> " + getValue();
            }
        };
    }

    @Override
    public FDate asFDate() {
        return this;
    }

    public static void putFDate(final IByteBuffer buffer, final int index, final FDate value) {
        if (value == null) {
            buffer.putLong(index, Long.MIN_VALUE);
        } else {
            buffer.putLong(index, value.millisValue());
        }
    }

    public static FDate extractFDate(final IByteBuffer buffer, final int index) {
        final long value = buffer.getLong(index);
        return extractFDate(value);
    }

    public static FDate extractFDate(final long value) {
        if (value == Long.MIN_VALUE) {
            return null;
        } else {
            return new FDate(value);
        }
    }

}
