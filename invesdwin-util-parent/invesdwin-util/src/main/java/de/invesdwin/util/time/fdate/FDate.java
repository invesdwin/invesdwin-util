package de.invesdwin.util.time.fdate;

import java.io.Serializable;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.annotation.concurrent.ThreadSafe;
import javax.persistence.Transient;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.DurationFieldType;
import org.joda.time.LocalDateTime;
import org.joda.time.MutableDateTime;
import org.joda.time.ReadableDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import de.invesdwin.norva.marker.IDate;
import de.invesdwin.util.collections.loadingcache.historical.IHistoricalEntry;
import de.invesdwin.util.collections.loadingcache.historical.IHistoricalValue;
import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.lang.ADelegateComparator;
import de.invesdwin.util.lang.Strings;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.decimal.scaled.Percent;
import de.invesdwin.util.time.TimeZones;
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

    public int getYear(final ZoneId timeZone) {
        return revertTimeZoneOffset(timeZone).getYear();
    }

    public int getYear() {
        return FDates.getDefaultChronology().year().get(millis);
    }

    public int getMonth(final ZoneId timeZone) {
        return revertTimeZoneOffset(timeZone).getMonth();
    }

    public int getMonth() {
        //no conversion needed since joda time has same index
        return FDates.getDefaultChronology().monthOfYear().get(millis);
    }

    public FMonth getFMonth(final ZoneId timeZone) {
        return revertTimeZoneOffset(timeZone).getFMonth();
    }

    public FMonth getFMonth() {
        return FMonth.valueOfIndex(getMonth());
    }

    public int getDay(final ZoneId timeZone) {
        return revertTimeZoneOffset(timeZone).getDay();
    }

    public int getDay() {
        return FDates.getDefaultChronology().dayOfMonth().get(millis);
    }

    public int getWeekday(final ZoneId timeZone) {
        return revertTimeZoneOffset(timeZone).getWeekday();
    }

    public int getWeekday() {
        //no conversion needed since joda time has same index
        return FDates.getDefaultChronology().dayOfWeek().get(millis);
    }

    public FWeekday getFWeekday(final ZoneId timeZone) {
        return revertTimeZoneOffset(timeZone).getFWeekday();
    }

    public FWeekday getFWeekday() {
        return FWeekday.valueOfIndex(getWeekday());
    }

    public int getHour(final ZoneId timeZone) {
        return revertTimeZoneOffset(timeZone).getHour();
    }

    public int getHour() {
        return FDates.getDefaultChronology().hourOfDay().get(millis);
    }

    public int getMinute() {
        return FDates.getDefaultChronology().minuteOfHour().get(millis);
    }

    public int getSecond() {
        return FDates.getDefaultChronology().secondOfMinute().get(millis);
    }

    public int getMillisecond() {
        return FDates.getDefaultChronology().millisOfSecond().get(millis);
    }

    public TimeZone getTimeZone() {
        return FDates.getDefaultTimeZone();
    }

    public ZoneId getZoneId() {
        return FDates.getDefaultZoneId();
    }

    public DateTimeZone getDateTimeZone() {
        return FDates.getDefaultDateTimeZone();
    }

    public FDate setYear(final int year, final ZoneId timeZone) {
        return revertTimeZoneOffset(timeZone).setYear(year).applyTimeZoneOffset(timeZone);
    }

    public FDate setYear(final int year) {
        final long newMillis = FDates.getDefaultChronology().year().set(millis, year);
        return new FDate(newMillis);
    }

    public FDate setMonth(final int month, final ZoneId timeZone) {
        return revertTimeZoneOffset(timeZone).setMonth(month).applyTimeZoneOffset(timeZone);
    }

    public FDate setMonth(final int month) {
        final long newMillis = FDates.getDefaultChronology().monthOfYear().set(millis, month);
        return new FDate(newMillis);
    }

    public FDate setFMonth(final FMonth month, final ZoneId timeZone) {
        return revertTimeZoneOffset(timeZone).setFMonth(month).applyTimeZoneOffset(timeZone);
    }

    public FDate setFMonth(final FMonth month) {
        return setMonth(month.jodaTimeValue());
    }

    public FDate setDay(final int day, final ZoneId timeZone) {
        return revertTimeZoneOffset(timeZone).setDay(day).applyTimeZoneOffset(timeZone);
    }

    public FDate setDay(final int day) {
        final long newMillis = FDates.getDefaultChronology().dayOfMonth().set(millis, day);
        return new FDate(newMillis);
    }

    public FDate setWeekday(final int weekday, final ZoneId timeZone) {
        return revertTimeZoneOffset(timeZone).setWeekday(weekday).applyTimeZoneOffset(timeZone);
    }

    public FDate setWeekday(final int weekday) {
        final long newMillis = FDates.getDefaultChronology().dayOfWeek().set(millis, weekday);
        final FDate modified = new FDate(newMillis);
        if (!FDates.isSameJulianDay(modified, this) && modified.isAfter(this)) {
            return modified.addWeeks(-1);
        } else {
            return modified;
        }
    }

    public FDate setFWeekday(final FWeekday weekday, final ZoneId timeZone) {
        return revertTimeZoneOffset(timeZone).setFWeekday(weekday).applyTimeZoneOffset(timeZone);
    }

    public FDate setFWeekday(final FWeekday weekday) {
        return setWeekday(weekday.jodaTimeValue());
    }

    public FDate setFWeekTime(final FWeekTime weekTime, final ZoneId timeZone) {
        return revertTimeZoneOffset(timeZone).setFWeekTime(weekTime).applyTimeZoneOffset(timeZone);
    }

    public FDate setFWeekTime(final FWeekTime weekTime) {
        final MutableDateTime delegate = newMutableDateTime();
        delegate.set(FDateField.Weekday.jodaTimeValue(), weekTime.getWeekday());
        delegate.set(FDateField.Hour.jodaTimeValue(), weekTime.getHour());
        delegate.set(FDateField.Minute.jodaTimeValue(), weekTime.getMinute());
        delegate.set(FDateField.Second.jodaTimeValue(), weekTime.getSecond());
        delegate.set(FDateField.Millisecond.jodaTimeValue(), weekTime.getMillisecond());
        final FDate modified = new FDate(delegate);
        if (!FDates.isSameJulianDay(modified, this) && modified.isAfter(this)) {
            return modified.addWeeks(-1);
        } else {
            return modified;
        }
    }

    public FDate setTime(final FDate time, final ZoneId timeZone) {
        return revertTimeZoneOffset(timeZone).setTime(time).applyTimeZoneOffset(timeZone);
    }

    public FDate setTime(final FDate time) {
        return setHour(time.getHour()).setMinute(time.getMinute())
                .setSecond(time.getSecond())
                .setMillisecond(time.getMillisecond());
    }

    public FDate setHour(final int hour, final ZoneId timeZone) {
        return revertTimeZoneOffset(timeZone).setHour(hour).applyTimeZoneOffset(timeZone);
    }

    public FDate setHour(final int hour) {
        final long newMillis = FDates.getDefaultChronology().hourOfDay().set(millis, hour);
        return new FDate(newMillis);
    }

    public FDate setMinute(final int minute) {
        final long newMillis = FDates.getDefaultChronology().minuteOfHour().set(millis, minute);
        return new FDate(newMillis);
    }

    public FDate setSecond(final int second) {
        final long newMillis = FDates.getDefaultChronology().secondOfMinute().set(millis, second);
        return new FDate(newMillis);
    }

    public FDate setMillisecond(final int millisecond) {
        final long newMillis = FDates.getDefaultChronology().millisOfSecond().set(millis, millisecond);
        return new FDate(newMillis);
    }

    public FDate addYears(final int years, final ZoneId timeZone) {
        return revertTimeZoneOffset(timeZone).addYears(years).applyTimeZoneOffset(timeZone);
    }

    public FDate addYears(final int years) {
        return add(FTimeUnit.YEARS, years);
    }

    public FDate addMonths(final int months, final ZoneId timeZone) {
        return revertTimeZoneOffset(timeZone).addMonths(months).applyTimeZoneOffset(timeZone);
    }

    public FDate addMonths(final int months) {
        return add(FTimeUnit.MONTHS, months);
    }

    public FDate addDays(final int days, final ZoneId timeZone) {
        return revertTimeZoneOffset(timeZone).addDays(days).applyTimeZoneOffset(timeZone);
    }

    public FDate addDays(final int days) {
        return add(FTimeUnit.DAYS, days);
    }

    public FDate addWeeks(final int weeks, final ZoneId timeZone) {
        return revertTimeZoneOffset(timeZone).addWeeks(weeks).applyTimeZoneOffset(timeZone);
    }

    public FDate addWeeks(final int weeks) {
        return addDays(weeks * FTimeUnit.DAYS_IN_WEEK);
    }

    public FDate addHours(final int hours, final ZoneId timeZone) {
        return revertTimeZoneOffset(timeZone).addHours(hours).applyTimeZoneOffset(timeZone);
    }

    public FDate addHours(final int hours) {
        return add(FTimeUnit.HOURS, hours);
    }

    public FDate addMinutes(final int minutes) {
        return add(FTimeUnit.MINUTES, minutes);
    }

    public FDate addSeconds(final int seconds) {
        return add(FTimeUnit.SECONDS, seconds);
    }

    public FDate addMilliseconds(final long milliseconds) {
        return new FDate(millis + milliseconds);
    }

    public int get(final FDateField field, final ZoneId timeZone) {
        return revertTimeZoneOffset(timeZone).get(field);
    }

    public int get(final FDateField field) {
        return field.jodaTimeValue().getField(FDates.getDefaultChronology()).get(millis);
    }

    public FDate set(final FDateField field, final int value, final ZoneId timeZone) {
        return revertTimeZoneOffset(timeZone).set(field, value).applyTimeZoneOffset(timeZone);
    }

    public FDate set(final FDateField field, final int value) {
        final MutableDateTime delegate = newMutableDateTime();
        delegate.set(field.jodaTimeValue(), value);
        return new FDate(delegate);
    }

    public FDate add(final FTimeUnit field, final int value, final ZoneId timeZone) {
        return revertTimeZoneOffset(timeZone).add(field, value).applyTimeZoneOffset(timeZone);
    }

    public FDate add(final FTimeUnit field, final int amount) {
        if (amount == 0) {
            return this;
        }
        final MutableDateTime delegate = newMutableDateTime();
        final int usedAmount;
        final DurationFieldType usedField;
        switch (field) {
        case MILLENIA:
            usedField = FTimeUnit.YEARS.jodaTimeValue();
            usedAmount = amount * FTimeUnit.YEARS_IN_MILLENIUM;
            break;
        case CENTURIES:
            usedField = FTimeUnit.YEARS.jodaTimeValue();
            usedAmount = amount * FTimeUnit.YEARS_IN_CENTURY;
            break;
        case DECADES:
            usedField = FTimeUnit.YEARS.jodaTimeValue();
            usedAmount = amount * FTimeUnit.YEARS_IN_DECADE;
            break;
        default:
            usedField = field.jodaTimeValue();
            usedAmount = amount;
            break;
        }
        delegate.add(usedField, usedAmount);
        return new FDate(delegate);
    }

    public FDate add(final Duration duration) {
        return duration.addTo(this);
    }

    public FDate subtract(final Duration duration) {
        return duration.subtractFrom(this);
    }

    public int getWeekNumberOfYear(final ZoneId timeZone) {
        return revertTimeZoneOffset(timeZone).getWeekNumberOfYear();
    }

    public int getWeekNumberOfYear() {
        final MutableDateTime delegate = newMutableDateTime();
        return delegate.getWeekOfWeekyear();
    }

    public int getWeekNumberOfMonth(final ZoneId timeZone) {
        return revertTimeZoneOffset(timeZone).getWeekNumberOfMonth();
    }

    /**
     * https://stackoverflow.com/questions/24280370/get-week-of-month-with-joda-time
     */
    public int getWeekNumberOfMonth() {
        return (getDay() / 7) + 1;
    }

    public FDate truncate(final FDateField field, final ZoneId timeZone) {
        return revertTimeZoneOffset(timeZone).truncate(field).applyTimeZoneOffset(timeZone);
    }

    public FDate truncate(final FDateField field) {
        final MutableDateTime delegate = newMutableDateTime();
        delegate.setRounding(field.jodaTimeValue().getField(delegate.getChronology()));
        final FDate truncated = new FDate(delegate);
        return truncated;
    }

    public FDate truncate(final FTimeUnit timeUnit, final ZoneId timeZone) {
        return revertTimeZoneOffset(timeZone).truncate(timeUnit).applyTimeZoneOffset(timeZone);
    }

    public FDate truncate(final FTimeUnit timeUnit) {
        switch (timeUnit) {
        case MILLENIA:
            return truncate(FDateField.Year).addYears(-getYear() % FTimeUnit.YEARS_IN_MILLENIUM);
        case CENTURIES:
            return truncate(FDateField.Year).addYears(-getYear() % FTimeUnit.YEARS_IN_CENTURY);
        case DECADES:
            return truncate(FDateField.Year).addYears(-getYear() % FTimeUnit.YEARS_IN_DECADE);
        case YEARS:
            return truncate(FDateField.Year);
        case MONTHS:
            return truncate(FDateField.Month);
        case WEEKS:
            return withoutTime().setFWeekday(FWeekday.Monday);
        case DAYS:
            return truncate(FDateField.Day);
        case HOURS:
            return truncate(FDateField.Hour);
        case MINUTES:
            return truncate(FDateField.Minute);
        case SECONDS:
            return truncate(FDateField.Second);
        case MILLISECONDS:
            return truncate(FDateField.Millisecond);
        default:
            throw UnknownArgumentException.newInstance(FTimeUnit.class, timeUnit);
        }
    }

    /**
     * sets hour, minute, second and millisecond each to 0.
     */
    public FDate withoutTime() {
        return truncate(FDateField.Day);
    }

    public FDate withoutTime(final ZoneId timeZone) {
        return revertTimeZoneOffset(timeZone).withoutTime().applyTimeZoneOffset(timeZone);
    }

    public FDate applyTimeZoneOffset(final TimeZone timeZone) {
        return applyTimeZoneOffset(timeZone.toZoneId());
    }

    public FDate applyTimeZoneOffset(final ZoneId timeZone) {
        if (timeZone == null) {
            return this;
        }
        int seconds = TimeZones.getOffsetSeconds(timeZone, millis);
        if (!FDates.isDefaultTimeZoneUTC()) {
            seconds -= TimeZones.getOffsetSeconds(FDates.getDefaultZoneId(), millis);
        }
        return addSeconds(seconds);
    }

    public FDate revertTimeZoneOffset(final TimeZone timeZone) {
        return revertTimeZoneOffset(timeZone.toZoneId());
    }

    public FDate revertTimeZoneOffset(final ZoneId timeZone) {
        if (timeZone == null) {
            return this;
        }
        int seconds = TimeZones.getOffsetSeconds(timeZone, millis);
        if (!FDates.isDefaultTimeZoneUTC()) {
            seconds -= TimeZones.getOffsetSeconds(FDates.getDefaultZoneId(), millis);
        }
        return addSeconds(-seconds);
    }

    /**
     * sets hour, minute, second and millisecond each to 23:59:999.
     */
    public FDate atEndOfDay() {
        return withoutTime().addDays(1).addMilliseconds(-1);
    }

    public FDate atEndOfDay(final ZoneId timeZone) {
        return revertTimeZoneOffset(timeZone).atEndOfDay().applyTimeZoneOffset(timeZone);
    }

    /**
     * Returns the number of milliseconds since January 1, 1970, 00:00:00 GMT.
     */
    public long millisValue() {
        return millis;
    }

    public Date dateValue() {
        return calendarValue().getTime();
    }

    public long longValue(final FTimeUnit timeUnit) {
        return timeUnit.convert(millis, FTimeUnit.MILLISECONDS);
    }

    public Calendar calendarValue() {
        final Calendar cal = FDates.newCalendar();
        cal.setTimeInMillis(millis);
        return cal;
    }

    public LocalDateTime jodaTimeValue() {
        return new LocalDateTime(millis);
    }

    public DateTime jodaTimeValueZoned() {
        return new DateTime(millis, FDates.getDefaultChronology());
    }

    public java.time.ZonedDateTime javaTimeValueZoned() {
        return java.time.Instant.ofEpochMilli(millis).atZone(FDates.getDefaultZoneId());
    }

    public java.time.LocalDateTime javaTimeValue() {
        return javaTimeValueZoned().toLocalDateTime();
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
        return valueOf(str, (TimeZone) null, null, parsePatterns);
    }

    public static FDate valueOf(final String str, final String parsePattern) {
        return valueOf(str, (TimeZone) null, null, parsePattern);
    }

    public static FDate valueOf(final String str, final Locale locale, final String... parsePatterns) {
        return valueOf(str, (TimeZone) null, locale, parsePatterns);
    }

    public static FDate valueOf(final String str, final TimeZone timeZone, final String... parsePatterns) {
        return valueOf(str, timeZone, null, parsePatterns);
    }

    public static FDate valueOf(final String str, final ZoneId timeZone, final String... parsePatterns) {
        return valueOf(str, timeZone, null, parsePatterns);
    }

    public static FDate valueOf(final String str, final TimeZone timeZone, final Locale locale,
            final String... parsePatterns) {
        if (parsePatterns == null || parsePatterns.length == 0) {
            throw new IllegalArgumentException("atleast one parsePattern is needed");
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

    public static FDate valueOf(final String str, final ZoneId timeZone, final Locale locale,
            final String... parsePatterns) {
        if (parsePatterns == null || parsePatterns.length == 0) {
            throw new IllegalArgumentException("atleast one parsePattern is needed");
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

    public static FDate valueOf(final String str, final TimeZone timeZone, final String parsePattern) {
        return valueOf(str, timeZone, null, parsePattern);
    }

    public static FDate valueOf(final String str, final ZoneId timeZone, final String parsePattern) {
        return valueOf(str, timeZone, null, parsePattern);
    }

    public static FDate valueOf(final String str, final Locale locale, final String parsePattern) {
        return valueOf(str, (TimeZone) null, locale, parsePattern);
    }

    public static FDate valueOf(final String str, final TimeZone timeZone, final Locale locale,
            final String parsePattern) {
        if (Strings.isBlank(str)) {
            return null;
        }
        DateTimeFormatter df = DateTimeFormat.forPattern(parsePattern);
        if (timeZone != null) {
            df = df.withZone(DateTimeZone.forTimeZone(timeZone));
        }
        if (locale != null) {
            df = df.withLocale(locale);
        }
        final DateTime date = df.parseDateTime(str);
        return new FDate(date);
    }

    public static FDate valueOf(final String str, final ZoneId timeZone, final Locale locale,
            final String parsePattern) {
        if (Strings.isBlank(str)) {
            return null;
        }
        java.time.format.DateTimeFormatter df = java.time.format.DateTimeFormatter.ofPattern(parsePattern);
        if (timeZone != null) {
            df = df.withZone(timeZone);
        } else {
            df = df.withZone(FDates.getDefaultZoneId());
        }
        if (locale != null) {
            df = df.withLocale(locale);
        }
        final java.time.ZonedDateTime date = java.time.ZonedDateTime.parse(str, df);
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

    public static FDate today(final ZoneId timeZone) {
        return now().withoutTime(timeZone);
    }

    public static FDate today() {
        return now().withoutTime();
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
        return toString(FORMAT_ISO_DATE_TIME_MS);
    }

    public String toString(final TimeZone timeZone) {
        return toString(FORMAT_ISO_DATE_TIME_MS, timeZone);
    }

    public String toString(final ZoneId timeZone) {
        return toString(FORMAT_ISO_DATE_TIME_MS, timeZone);
    }

    public String toString(final String format) {
        return toString(format, (TimeZone) null);
    }

    public String toString(final String format, final TimeZone timeZone) {
        final MutableDateTime delegate = newMutableDateTime();
        DateTimeFormatter df = DateTimeFormat.forPattern(format);
        if (timeZone != null) {
            df = df.withZone(DateTimeZone.forTimeZone(timeZone));
        } else {
            df = df.withZone(FDates.getDefaultDateTimeZone());
        }
        return df.print(delegate);
    }

    public String toString(final String format, final ZoneId timeZone) {
        final java.time.ZonedDateTime delegate = javaTimeValueZoned();
        java.time.format.DateTimeFormatter df = java.time.format.DateTimeFormatter.ofPattern(format);
        if (timeZone != null) {
            df = df.withZone(timeZone);
        } else {
            df = df.withZone(FDates.getDefaultZoneId());
        }
        return df.format(delegate);
    }

    private MutableDateTime newMutableDateTime() {
        return new MutableDateTime(millis, FDates.getDefaultChronology());
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

    public FDate getFirstWeekdayOfMonth(final FWeekday weekday, final ZoneId timeZone) {
        return revertTimeZoneOffset(timeZone).getFirstWeekdayOfMonth(weekday).applyTimeZoneOffset(timeZone);
    }

    public FDate getFirstWeekdayOfMonth(final FWeekday weekday) {
        final FDate firstWeekDay = withoutTime().setDay(1).setFWeekday(weekday);
        if (!FDates.isSameMonth(this, firstWeekDay)) {
            return firstWeekDay.addWeeks(1);
        } else {
            return firstWeekDay;
        }
    }

    public FDate getFirstWorkdayOfMonth(final FHolidayManager holidayManager, final ZoneId timeZone) {
        return revertTimeZoneOffset(timeZone).getFirstWorkdayOfMonth(holidayManager).applyTimeZoneOffset(timeZone);
    }

    public FDate getFirstWorkdayOfMonth(final FHolidayManager holidayManager) {
        FDate firstWorkdayDay = withoutTime().setDay(1);
        while (firstWorkdayDay.getFWeekday().isWeekend() || firstWorkdayDay.isHoliday(holidayManager)) {
            firstWorkdayDay = firstWorkdayDay.addDays(1);
        }
        return firstWorkdayDay;
    }

    public boolean isHoliday(final FHolidayManager holidayManager) {
        if (holidayManager == null) {
            return false;
        }
        return holidayManager.isHoliday(this);
    }

    public boolean isHoliday(final FHolidayManager holidayManager, final ZoneId timeZone) {
        return revertTimeZoneOffset(timeZone).isHoliday(holidayManager);
    }

    public FDate addWorkdays(final int workdays, final FHolidayManager holidayManager, final ZoneId timeZone) {
        return revertTimeZoneOffset(timeZone).addWorkdays(workdays, holidayManager).applyTimeZoneOffset(timeZone);
    }

    public FDate addWorkdays(final int workdays, final FHolidayManager holidayManager) {
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

}
