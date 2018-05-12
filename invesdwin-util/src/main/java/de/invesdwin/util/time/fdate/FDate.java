package de.invesdwin.util.time.fdate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.annotation.concurrent.ThreadSafe;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.DurationFieldType;
import org.joda.time.LocalDateTime;
import org.joda.time.MutableDateTime;
import org.joda.time.ReadableDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import de.invesdwin.norva.marker.IDate;
import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.lang.ADelegateComparator;
import de.invesdwin.util.lang.Strings;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.decimal.scaled.Percent;
import de.invesdwin.util.time.duration.Duration;
import de.jollyday.HolidayCalendar;
import de.jollyday.HolidayManager;
import de.jollyday.HolidayType;
import de.jollyday.ManagerParameters;

/**
 * FDate stands for an immutable Fast Date implementation by utilizing heavy caching.
 */
@ThreadSafe
public class FDate implements IDate, Serializable, Cloneable, Comparable<Object> {

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
    public static final String FORMAT_TIMESTAMP_NUMBER = "yyyyMMddHHmmssSSS";
    public static final String FORMAT_TIMESTAMP_UNDERSCORE = "yyyy_MM_dd_HH_mm_ss_SSS";
    public static final String FORMAT_GERMAN_DATE = "dd.MM.yyyy";
    public static final String FORMAT_GERMAN_DATE_TIME = FORMAT_GERMAN_DATE + " " + FORMAT_ISO_TIME;

    public static final ADelegateComparator<FDate> DATE_COMPARATOR = new ADelegateComparator<FDate>() {
        @Override
        protected Comparable<?> getCompareCriteria(final FDate e) {
            return e;
        }
    };

    private final long millis;

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

    public FDate(final Calendar calendar) {
        this(calendar.getTime());
    }

    public FDate(final Date date) {
        this(date.getTime());
    }

    public int getYear() {
        return get(FDateField.Year);
    }

    public int getMonth() {
        //no conversion needed since joda time has same index
        return get(FDateField.Month);
    }

    public FMonth getFMonth() {
        return FMonth.valueOfIndex(getMonth());
    }

    public int getDay() {
        return get(FDateField.Day);
    }

    public int getWeekday() {
        //no conversion needed since joda time has same index
        return get(FDateField.Weekday);
    }

    public FWeekday getFWeekday() {
        return FWeekday.valueOfIndex(getWeekday());
    }

    public int getHour() {
        return get(FDateField.Hour);
    }

    public int getMinute() {
        return get(FDateField.Minute);
    }

    public int getSecond() {
        return get(FDateField.Second);
    }

    public int getMillisecond() {
        return get(FDateField.Millisecond);
    }

    public TimeZone getTimeZone() {
        return TimeZone.getDefault();
    }

    public FDate setYear(final int year) {
        return set(FDateField.Year, year);
    }

    public FDate setMonth(final int month) {
        return set(FDateField.Month, month);
    }

    public FDate setFMonth(final FMonth month) {
        return setMonth(month.jodaTimeValue());
    }

    public FDate setDay(final int day) {
        return set(FDateField.Day, day);
    }

    public FDate setWeekday(final int weekday) {
        return set(FDateField.Weekday, weekday);
    }

    public FDate setFWeekday(final FWeekday weekday) {
        return setWeekday(weekday.jodaTimeValue());
    }

    public FDate setHour(final int hour) {
        return set(FDateField.Hour, hour);
    }

    public FDate setMinute(final int minute) {
        return set(FDateField.Minute, minute);
    }

    public FDate setSecond(final int second) {
        return set(FDateField.Second, second);
    }

    public FDate setMillisecond(final int millisecond) {
        return set(FDateField.Millisecond, millisecond);
    }

    public FDate addYears(final int years) {
        return add(FTimeUnit.YEARS, years);
    }

    public FDate addMonths(final int months) {
        return add(FTimeUnit.MONTHS, months);
    }

    public FDate addDays(final int days) {
        return add(FTimeUnit.DAYS, days);
    }

    public FDate addWeeks(final int weeks) {
        return addDays(weeks * FTimeUnit.DAYS_IN_WEEK);
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

    public int get(final FDateField field) {
        final MutableDateTime delegate = newMutableDateTime();
        return delegate.get(field.jodaTimeValue());
    }

    public FDate set(final FDateField field, final int value) {
        final MutableDateTime delegate = newMutableDateTime();
        delegate.set(field.jodaTimeValue(), value);
        return new FDate(delegate);
    }

    public FDate add(final FTimeUnit field, final int amount) {
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

    public FDate truncate(final FDateField field) {
        final MutableDateTime delegate = newMutableDateTime();
        delegate.setRounding(field.jodaTimeValue().getField(delegate.getChronology()));
        final FDate truncated = new FDate(delegate);
        return truncated;
    }

    public FDate truncate(final FTimeUnit timeUnit) {
        switch (timeUnit) {
        case MILLENIA:
            return truncate(FDateField.Year).addYears(getYear() % FTimeUnit.YEARS_IN_MILLENIUM);
        case CENTURIES:
            return truncate(FDateField.Year).addYears(getYear() % FTimeUnit.YEARS_IN_CENTURY);
        case DECADES:
            return truncate(FDateField.Year).addYears(getYear() % FTimeUnit.YEARS_IN_DECADE);
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

    /**
     * sets hour, minute, second and millisecond each to 23:59:999.
     */
    public FDate atEndOfDay() {
        return withoutTime().addDays(1).addMilliseconds(-1);
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
        return valueOf(str, null, null, parsePatterns);
    }

    public static FDate valueOf(final String str, final String parsePattern) {
        return valueOf(str, null, null, parsePattern);
    }

    public static FDate valueOf(final String str, final Locale locale, final String... parsePatterns) {
        return valueOf(str, null, locale, parsePatterns);
    }

    public static FDate valueOf(final String str, final TimeZone timeZone, final String... parsePatterns) {
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

    public static FDate valueOf(final String str, final TimeZone timeZone, final String parsePattern) {
        return valueOf(str, timeZone, null, parsePattern);
    }

    public static FDate valueOf(final String str, final Locale locale, final String parsePattern) {
        return valueOf(str, null, locale, parsePattern);
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
            return Long.compare(millis, cO.millis);
        } else {
            return 1;
        }
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof FDate) {
            final FDate cObj = (FDate) obj;
            return millis == cObj.millis;
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

    public String toString(final String format) {
        return toString(format, null);
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

    private MutableDateTime newMutableDateTime() {
        return new MutableDateTime(millis, FDates.getDefaultDateTimeZone());
    }

    public boolean isBefore(final FDate other) {
        return other != null && compareTo(other) < 0;
    }

    public boolean isBeforeOrEqualTo(final FDate other) {
        return other != null && !isAfter(other);
    }

    public boolean isAfter(final FDate other) {
        return other != null && compareTo(other) > 0;
    }

    public boolean isAfterOrEqualTo(final FDate other) {
        return other != null && !isBefore(other);
    }

    //CHECKSTYLE:OFF
    @Override
    public FDate clone() {
        return new FDate(millis);
    }
    //CHECKSTYLE:ON

    public FDate getFirstWeekdayOfMonth(final FWeekday weekday) {
        final FDate firstWeekDay = withoutTime().setDay(1).setFWeekday(weekday);
        if (!FDates.isSameMonth(this, firstWeekDay)) {
            return firstWeekDay.addDays(FTimeUnit.DAYS_IN_WEEK);
        } else {
            return firstWeekDay;
        }
    }

    public FDate getFirstWorkdayOfMonth(final FDate key, final HolidayCalendar holidayCalendar) {
        FDate firstWorkdayDay = withoutTime().setDay(1);
        while (firstWorkdayDay.getFWeekday().isWeekend() || firstWorkdayDay.isHoliday(holidayCalendar)) {
            firstWorkdayDay = firstWorkdayDay.addDays(1);
        }
        return firstWorkdayDay;
    }

    public boolean isHoliday(final HolidayCalendar holidayCalendar) {
        if (holidayCalendar != null) {
            final HolidayManager holidayManager = HolidayManager.getInstance(ManagerParameters.create(holidayCalendar));
            return holidayManager.isHoliday(calendarValue(), HolidayType.OFFICIAL_HOLIDAY);
        } else {
            return false;
        }
    }

    public FDate addWorkdays(final int workdays, final HolidayCalendar holidayCalendar) {
        int workdaysToShift = Math.abs(workdays);
        if (getFWeekday().isWeekend() || isHoliday(holidayCalendar)) {
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
            if (!cur.getFWeekday().isWeekend() && !cur.isHoliday(holidayCalendar)) {
                workdaysShifted++;
            }
            cur = cur.addDays(shiftUnit);
        }
        return cur;
    }

}
