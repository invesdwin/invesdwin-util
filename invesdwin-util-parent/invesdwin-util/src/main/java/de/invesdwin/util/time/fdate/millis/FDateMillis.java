package de.invesdwin.util.time.fdate.millis;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.annotation.concurrent.Immutable;

import org.joda.time.DateTime;
import org.joda.time.DateTimeField;
import org.joda.time.DurationField;
import org.joda.time.LocalDateTime;
import org.joda.time.ReadableDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.time.duration.Duration;
import de.invesdwin.util.time.fdate.FDate;
import de.invesdwin.util.time.fdate.FDateField;
import de.invesdwin.util.time.fdate.FDates;
import de.invesdwin.util.time.fdate.FDayTime;
import de.invesdwin.util.time.fdate.FHolidayManager;
import de.invesdwin.util.time.fdate.FMonth;
import de.invesdwin.util.time.fdate.FTimeUnit;
import de.invesdwin.util.time.fdate.FTimeZone;
import de.invesdwin.util.time.fdate.FWeekTime;
import de.invesdwin.util.time.fdate.FWeekday;

/**
 * FDate stands for an immutable Fast Date implementation by utilizing heavy caching.
 */
@Immutable
public final class FDateMillis {

    private FDateMillis() {
    }

    public static int getYear(final long millis, final FTimeZone timeZone) {
        return getYear(applyTimeZoneOffset(millis, timeZone));
    }

    public static int getYear(final long millis) {
        return FDates.getDefaultTimeZone().getDateTimeFieldYear().get(millis);
    }

    public static int getMonth(final long millis, final FTimeZone timeZone) {
        return getMonth(applyTimeZoneOffset(millis, timeZone));
    }

    public static int getMonth(final long millis) {
        //no conversion needed since joda time has same index
        return FDates.getDefaultTimeZone().getDateTimeFieldMonth().get(millis);
    }

    public static FMonth getFMonth(final long millis, final FTimeZone timeZone) {
        return getFMonth(applyTimeZoneOffset(millis, timeZone));
    }

    public static FMonth getFMonth(final long millis) {
        return FMonth.valueOfIndex(getMonth(millis));
    }

    public static int getDay(final long millis, final FTimeZone timeZone) {
        return getDay(applyTimeZoneOffset(millis, timeZone));
    }

    public static int getDay(final long millis) {
        return FDates.getDefaultTimeZone().getDateTimeFieldDay().get(millis);
    }

    public static int getWeekday(final long millis, final FTimeZone timeZone) {
        return getWeekday(applyTimeZoneOffset(millis, timeZone));
    }

    public static int getWeekday(final long millis) {
        //no conversion needed since joda time has same index
        return FDates.getDefaultTimeZone().getDateTimeFieldWeekday().get(millis);
    }

    public static FWeekday getFWeekday(final long millis, final FTimeZone timeZone) {
        return getFWeekday(applyTimeZoneOffset(millis, timeZone));
    }

    public static FWeekday getFWeekday(final long millis) {
        return FWeekday.valueOfIndex(getWeekday(millis));
    }

    public static int getHour(final long millis, final FTimeZone timeZone) {
        return getHour(applyTimeZoneOffset(millis, timeZone));
    }

    public static int getHour(final long millis) {
        return FDates.getDefaultTimeZone().getDateTimeFieldHour().get(millis);
    }

    public static int getMinute(final long millis) {
        return FDates.getDefaultTimeZone().getDateTimeFieldMinute().get(millis);
    }

    public static int getSecond(final long millis) {
        return FDates.getDefaultTimeZone().getDateTimeFieldSecond().get(millis);
    }

    public static int getMillisecond(final long millis) {
        return FDates.getDefaultTimeZone().getDateTimeFieldMillisecond().get(millis);
    }

    public static long setYear(final long millis, final int year, final FTimeZone timeZone) {
        final long offset = getTimeZoneOffsetMilliseconds(millis, timeZone);
        return revertTimeZoneOffset(setYear(applyTimeZoneOffset(millis, offset), year), offset);
    }

    public static long setYear(final long millis, final int year) {
        final long newMillis = FDates.getDefaultTimeZone().getDateTimeFieldYear().set(millis, year);
        return newMillis;
    }

    public static long setMonth(final long millis, final int month, final FTimeZone timeZone) {
        final long offset = getTimeZoneOffsetMilliseconds(millis, timeZone);
        return revertTimeZoneOffset(setMonth(applyTimeZoneOffset(millis, offset), month), offset);
    }

    public static long setMonth(final long millis, final int month) {
        final long newMillis = FDates.getDefaultTimeZone().getDateTimeFieldMonth().set(millis, month);
        return newMillis;
    }

    public static long setFMonth(final long millis, final FMonth month, final FTimeZone timeZone) {
        final long offset = getTimeZoneOffsetMilliseconds(millis, timeZone);
        return revertTimeZoneOffset(setFMonth(applyTimeZoneOffset(millis, offset), month), offset);
    }

    public static long setFMonth(final long millis, final FMonth month) {
        return setMonth(millis, month.jodaTimeValue());
    }

    public static long setDay(final long millis, final int day, final FTimeZone timeZone) {
        final long offset = getTimeZoneOffsetMilliseconds(millis, timeZone);
        return revertTimeZoneOffset(setDay(applyTimeZoneOffset(millis, offset), day), offset);
    }

    public static long setDay(final long millis, final int day) {
        final long newMillis = FDates.getDefaultTimeZone().getDateTimeFieldDay().set(millis, day);
        return newMillis;
    }

    public static long setWeekday(final long millis, final int weekday, final FTimeZone timeZone) {
        final long offset = getTimeZoneOffsetMilliseconds(millis, timeZone);
        return revertTimeZoneOffset(setWeekday(applyTimeZoneOffset(millis, offset), weekday), offset);
    }

    public static long setWeekday(final long millis, final int weekday) {
        final long newMillis = FDates.getDefaultTimeZone().getDateTimeFieldWeekday().set(millis, weekday);
        final long modified = newMillis;
        if (!FDatesMillis.isSameJulianDay(modified, millis) && isAfter(modified, millis)) {
            return addWeeks(modified, -1);
        } else {
            return modified;
        }
    }

    public static long setFWeekday(final long millis, final FWeekday weekday, final FTimeZone timeZone) {
        final long offset = getTimeZoneOffsetMilliseconds(millis, timeZone);
        return revertTimeZoneOffset(setFWeekday(applyTimeZoneOffset(millis, offset), weekday), offset);
    }

    public static long setFWeekday(final long millis, final FWeekday weekday) {
        return setWeekday(millis, weekday.jodaTimeValue());
    }

    public static long setFWeekTime(final long millis, final FWeekTime weekTime, final FTimeZone timeZone) {
        final long offset = getTimeZoneOffsetMilliseconds(millis, timeZone);
        return revertTimeZoneOffset(setFWeekTime(applyTimeZoneOffset(millis, offset), weekTime), offset);
    }

    public static long setFWeekTime(final long millis, final FWeekTime weekTime) {
        long newMillis = millis;
        newMillis = FDates.getDefaultTimeZone().getDateTimeFieldWeekday().set(newMillis, weekTime.getWeekday());
        newMillis = FDates.getDefaultTimeZone().getDateTimeFieldHour().set(newMillis, weekTime.getHour());
        newMillis = FDates.getDefaultTimeZone().getDateTimeFieldMinute().set(newMillis, weekTime.getMinute());
        newMillis = FDates.getDefaultTimeZone().getDateTimeFieldSecond().set(newMillis, weekTime.getSecond());
        newMillis = FDates.getDefaultTimeZone().getDateTimeFieldMillisecond().set(newMillis, weekTime.getMillisecond());
        final long modified = newMillis;
        if (!FDatesMillis.isSameJulianDay(modified, millis) && isAfter(modified, millis)) {
            return addWeeks(modified, -1);
        } else {
            return modified;
        }
    }

    public static long setFDayTime(final long millis, final FDayTime dayTime, final FTimeZone timeZone) {
        final long offset = getTimeZoneOffsetMilliseconds(millis, timeZone);
        return revertTimeZoneOffset(setFDayTime(applyTimeZoneOffset(millis, offset), dayTime), offset);
    }

    public static long setFDayTime(final long millis, final FDayTime dayTime) {
        long newMillis = millis;
        newMillis = FDates.getDefaultTimeZone().getDateTimeFieldHour().set(newMillis, dayTime.getHour());
        newMillis = FDates.getDefaultTimeZone().getDateTimeFieldMinute().set(newMillis, dayTime.getMinute());
        newMillis = FDates.getDefaultTimeZone().getDateTimeFieldSecond().set(newMillis, dayTime.getSecond());
        newMillis = FDates.getDefaultTimeZone().getDateTimeFieldMillisecond().set(newMillis, dayTime.getMillisecond());
        return newMillis;
    }

    public static long setTime(final long millis, final long time, final FTimeZone timeZone) {
        final long offset = getTimeZoneOffsetMilliseconds(millis, timeZone);
        return revertTimeZoneOffset(setTime(applyTimeZoneOffset(millis, offset), time), offset);
    }

    public static long setTime(final long millis, final long time) {
        return setMillisecond(setSecond(setMinute(setHour(millis, getHour(time)), getMinute(time)), getSecond(time)),
                getMillisecond(time));
    }

    public static long setHour(final long millis, final int hour, final FTimeZone timeZone) {
        final long offset = getTimeZoneOffsetMilliseconds(millis, timeZone);
        return revertTimeZoneOffset(setHour(applyTimeZoneOffset(millis, offset), hour), offset);
    }

    public static long setHour(final long millis, final int hour) {
        final long newMillis = FDates.getDefaultTimeZone().getDateTimeFieldHour().set(millis, hour);
        return newMillis;
    }

    public static long setMinute(final long millis, final int minute) {
        final long newMillis = FDates.getDefaultTimeZone().getDateTimeFieldMinute().set(millis, minute);
        return newMillis;
    }

    public static long setSecond(final long millis, final int second) {
        final long newMillis = FDates.getDefaultTimeZone().getDateTimeFieldSecond().set(millis, second);
        return newMillis;
    }

    public static long setMillisecond(final long millis, final int millisecond) {
        final long newMillis = FDates.getDefaultTimeZone().getDateTimeFieldMillisecond().set(millis, millisecond);
        return newMillis;
    }

    public static long addYears(final long millis, final int years, final FTimeZone timeZone) {
        final long offset = getTimeZoneOffsetMilliseconds(millis, timeZone);
        return revertTimeZoneOffset(addYears(applyTimeZoneOffset(millis, offset), years), offset);
    }

    public static long addYears(final long millis, final int years) {
        return add(millis, FTimeUnit.YEARS, years);
    }

    public static long addMonths(final long millis, final int months, final FTimeZone timeZone) {
        final long offset = getTimeZoneOffsetMilliseconds(millis, timeZone);
        return revertTimeZoneOffset(addMonths(applyTimeZoneOffset(millis, offset), months), offset);
    }

    public static long addMonths(final long millis, final int months) {
        return add(millis, FTimeUnit.MONTHS, months);
    }

    public static long addWeeks(final long millis, final int weeks) {
        return addDays(millis, weeks * FTimeUnit.DAYS_IN_WEEK);
    }

    public static long addDays(final long millis, final int days) {
        return addMilliseconds(millis, FDates.MILLISECONDS_IN_DAY * days);
    }

    public static long addHours(final long millis, final int hours) {
        return addMilliseconds(millis, FDates.MILLISECONDS_IN_HOUR * hours);
    }

    public static long addMinutes(final long millis, final int minutes) {
        return addMilliseconds(millis, FDates.MILLISECONDS_IN_MINUTE * minutes);
    }

    public static long addSeconds(final long millis, final int seconds) {
        return addMilliseconds(millis, FDates.MILLISECONDS_IN_SECOND * seconds);
    }

    public static long addMilliseconds(final long millis, final long milliseconds) {
        if (milliseconds == 0) {
            return millis;
        }
        return millis + milliseconds;
    }

    public static int get(final long millis, final FDateField field, final FTimeZone timeZone) {
        return get(applyTimeZoneOffset(millis, timeZone), field);
    }

    public static int get(final long millis, final FDateField field) {
        return field.dateTimeFieldValue().get(millis);
    }

    public static long set(final long millis, final FDateField field, final int value, final FTimeZone timeZone) {
        final long offset = getTimeZoneOffsetMilliseconds(millis, timeZone);
        return revertTimeZoneOffset(set(applyTimeZoneOffset(millis, offset), field, value), offset);
    }

    public static long set(final long millis, final FDateField field, final int value) {
        final long newMillis = field.dateTimeFieldValue().set(millis, value);
        return newMillis;
    }

    public static long add(final long millis, final FTimeUnit field, final int value, final FTimeZone timeZone) {
        final long offset = getTimeZoneOffsetMilliseconds(millis, timeZone);
        return revertTimeZoneOffset(add(applyTimeZoneOffset(millis, offset), field, value), offset);
    }

    public static long add(final long millis, final FTimeUnit field, final int amount) {
        if (amount == 0) {
            return millis;
        }
        final int usedAmount;
        final DurationField usedField;
        switch (field) {
        case MILLENIA:
            usedField = FTimeUnit.YEARS.durationFieldValue();
            usedAmount = amount * FTimeUnit.YEARS_IN_MILLENIUM;
            break;
        case CENTURIES:
            usedField = FTimeUnit.YEARS.durationFieldValue();
            usedAmount = amount * FTimeUnit.YEARS_IN_CENTURY;
            break;
        case DECADES:
            usedField = FTimeUnit.YEARS.durationFieldValue();
            usedAmount = amount * FTimeUnit.YEARS_IN_DECADE;
            break;
        default:
            usedField = field.durationFieldValue();
            usedAmount = amount;
            break;
        }
        final long newMillis = usedField.add(millis, usedAmount);
        return newMillis;
    }

    public static long add(final long millis, final Duration duration) {
        return duration.addTo(millis);
    }

    public static long subtract(final long millis, final Duration duration) {
        return duration.subtractFrom(millis);
    }

    public static int getWeekNumberOfYear(final long millis, final FTimeZone timeZone) {
        return getWeekNumberOfYear(applyTimeZoneOffset(millis, timeZone));
    }

    public static int getWeekNumberOfYear(final long millis) {
        return FDates.getDefaultTimeZone().getDateTimeFieldWeekNumberOfYear().get(millis);
    }

    public static int getWeekNumberOfMonth(final long millis, final FTimeZone timeZone) {
        return getWeekNumberOfMonth(applyTimeZoneOffset(millis, timeZone));
    }

    /**
     * https://stackoverflow.com/questions/24280370/get-week-of-month-with-joda-time
     */
    public static int getWeekNumberOfMonth(final long millis) {
        return (getDay(millis) / 7) + 1;
    }

    public static long truncate(final long millis, final FDateField field, final FTimeZone timeZone) {
        final long offset = getTimeZoneOffsetMilliseconds(millis, timeZone);
        return revertTimeZoneOffset(truncate(applyTimeZoneOffset(millis, offset), field), offset);
    }

    public static long truncate(final long millis, final FDateField field) {
        final DateTimeField jodaField = field.dateTimeFieldValue();
        final long newMillis = jodaField.roundFloor(millis);
        return newMillis;
    }

    public static long truncate(final long millis, final FTimeUnit timeUnit, final FTimeZone timeZone) {
        final long offset = getTimeZoneOffsetMilliseconds(millis, timeZone);
        return revertTimeZoneOffset(truncate(applyTimeZoneOffset(millis, offset), timeUnit), offset);
    }

    public static long truncate(final long millis, final FTimeUnit timeUnit) {
        switch (timeUnit) {
        case MILLENIA:
            return addYears(truncate(millis, FDateField.Year), -getYear(millis) % FTimeUnit.YEARS_IN_MILLENIUM);
        case CENTURIES:
            return addYears(truncate(millis, FDateField.Year), -getYear(millis) % FTimeUnit.YEARS_IN_CENTURY);
        case DECADES:
            return addYears(truncate(millis, FDateField.Year), -getYear(millis) % FTimeUnit.YEARS_IN_DECADE);
        case YEARS:
            return truncate(millis, FDateField.Year);
        case MONTHS:
            return truncate(millis, FDateField.Month);
        case WEEKS:
            return setFWeekday(withoutTime(millis), FWeekday.Monday);
        case DAYS:
            return truncate(millis, FDateField.Day);
        case HOURS:
            return truncate(millis, FDateField.Hour);
        case MINUTES:
            return truncate(millis, FDateField.Minute);
        case SECONDS:
            return truncate(millis, FDateField.Second);
        case MILLISECONDS:
            return truncate(millis, FDateField.Millisecond);
        default:
            throw UnknownArgumentException.newInstance(FTimeUnit.class, timeUnit);
        }
    }

    /**
     * sets hour, minute, second and millisecond each to 0.
     */
    public static long withoutTime(final long millis) {
        return truncate(millis, FDateField.Day);
    }

    public static long withoutTime(final long millis, final FTimeZone timeZone) {
        final long offset = getTimeZoneOffsetMilliseconds(millis, timeZone);
        return revertTimeZoneOffset(withoutTime(applyTimeZoneOffset(millis, offset)), offset);
    }

    /**
     * Pretend that this date is inside the given timezone. E.g. UTC will add 2 hours for becoming EET.
     */
    public static long applyTimeZoneOffset(final long millis, final FTimeZone timeZone) {
        final long offset = getTimeZoneOffsetMilliseconds(millis, timeZone);
        return applyTimeZoneOffset(millis, offset);
    }

    /**
     * Pretend that this date is inside the given timezone. E.g. UTC will add 2 hours for becoming EET.
     */
    public static long applyTimeZoneOffset(final long millis, final long timeZoneOffsetMilliseconds) {
        return addMilliseconds(millis, timeZoneOffsetMilliseconds);
    }

    public static long getTimeZoneOffsetMilliseconds(final long millis, final FTimeZone timeZone) {
        if (timeZone == null) {
            return 0;
        }
        long milliseconds = timeZone.getOffsetMilliseconds(millis);
        if (!FDates.getDefaultTimeZone().isUTC()) {
            milliseconds -= FDates.getDefaultTimeZone().getOffsetMilliseconds(millis);
        }
        return milliseconds;
    }

    /**
     * Go back to the default timezone for a data that was converted into another timezone.
     */
    public static long revertTimeZoneOffset(final long millis, final FTimeZone timeZone) {
        final long offset = getTimeZoneOffsetMilliseconds(millis, timeZone);
        return revertTimeZoneOffset(millis, offset);
    }

    /**
     * Go back to the default timezone for a data that was converted into another timezone.
     */
    public static long revertTimeZoneOffset(final long millis, final long timeZoneOffsetMilliseconds) {
        return addMilliseconds(millis, -timeZoneOffsetMilliseconds);
    }

    /**
     * sets hour, minute, second and millisecond each to 23:59:999.
     */
    public static long atEndOfDay(final long millis) {
        return addMilliseconds(addDays(withoutTime(millis), 1), -1);
    }

    public static long atEndOfDay(final long millis, final FTimeZone timeZone) {
        final long offset = getTimeZoneOffsetMilliseconds(millis, timeZone);
        return revertTimeZoneOffset(atEndOfDay(applyTimeZoneOffset(millis, offset)), offset);
    }

    public static Date dateValue(final long millis) {
        return calendarValue(millis).getTime();
    }

    public static long longValue(final long millis, final FTimeUnit timeUnit) {
        return timeUnit.convert(millis, FTimeUnit.MILLISECONDS);
    }

    public static Calendar calendarValue(final long millis) {
        final Calendar cal = FDates.getDefaultTimeZone().newCalendar();
        cal.setTimeInMillis(millis);
        return cal;
    }

    public static LocalDateTime jodaTimeValue(final long millis) {
        return new LocalDateTime(millis);
    }

    public static DateTime jodaTimeValueZoned(final long millis) {
        return new DateTime(millis, FDates.getDefaultTimeZone().getChronology());
    }

    public static java.time.ZonedDateTime javaTimeValueZoned(final long millis) {
        return java.time.Instant.ofEpochMilli(millis).atZone(FDates.getDefaultTimeZone().getZoneId());
    }

    public static java.time.LocalDateTime javaTimeValue(final long millis) {
        return javaTimeValueZoned(millis).toLocalDateTime();
    }

    public static long valueOf(final long value, final FTimeUnit timeUnit) {
        return timeUnit.toMillis(value);
    }

    public static long valueOf(final Date date) {
        return date.getTime();
    }

    public static long valueOf(final Calendar calendar) {
        return calendar.getTimeInMillis();
    }

    public static long valueOf(final ReadableDateTime jodaTime) {
        return jodaTime.getMillis();
    }

    public static long valueOf(final LocalDateTime jodaTime) {
        return jodaTime.toDateTime().getMillis();
    }

    public static long valueOf(final String str, final String... parsePatterns) {
        return valueOf(str, (FTimeZone) null, null, parsePatterns);
    }

    public static long valueOf(final String str, final String parsePattern) {
        return valueOf(str, (FTimeZone) null, null, parsePattern);
    }

    public static long valueOf(final String str, final Locale locale, final String... parsePatterns) {
        return valueOf(str, (FTimeZone) null, locale, parsePatterns);
    }

    public static long valueOf(final String str, final FTimeZone timeZone, final String... parsePatterns) {
        return valueOf(str, timeZone, null, parsePatterns);
    }

    public static long valueOf(final String str, final FTimeZone timeZone, final Locale locale,
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

    public static long valueOf(final String str, final FTimeZone timeZone, final String parsePattern) {
        return valueOf(str, timeZone, null, parsePattern);
    }

    public static long valueOf(final String str, final Locale locale, final String parsePattern) {
        return valueOf(str, (FTimeZone) null, locale, parsePattern);
    }

    public static long valueOf(final String str, final FTimeZone timeZone, final Locale locale,
            final String parsePattern) {
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
        return date.getMillis();
    }

    public static long now() {
        return System.currentTimeMillis();
    }

    public static long today(final FTimeZone timeZone) {
        return withoutTime(now(), timeZone);
    }

    public static long today() {
        return withoutTime(now());
    }

    public static String toString(final long millis) {
        return toString(millis, FDate.FORMAT_ISO_DATE_TIME_MS);
    }

    public static String toString(final long millis, final FTimeZone timeZone) {
        return toString(millis, FDate.FORMAT_ISO_DATE_TIME_MS, timeZone);
    }

    public static String toString(final long millis, final String format) {
        return toString(millis, format, null);
    }

    public static String toString(final long millis, final String format, final FTimeZone timeZone) {
        final DateTime delegate = new DateTime(millis, FDates.getDefaultTimeZone().getChronology());
        DateTimeFormatter df = DateTimeFormat.forPattern(format);
        if (timeZone != null) {
            df = df.withZone(timeZone.getDateTimeZone());
        } else {
            df = df.withZone(FDates.getDefaultTimeZone().getDateTimeZone());
        }
        return df.print(delegate);
    }

    public static boolean isBefore(final long millis, final long other) {
        return millis < other;
    }

    public static boolean isBeforeOrEqualTo(final long millis, final long other) {
        return millis <= other;
    }

    public static boolean isAfter(final long millis, final long other) {
        return millis > other;
    }

    public static boolean isAfterOrEqualTo(final long millis, final long other) {
        return millis >= other;
    }

    public static long getFirstWeekdayOfMonth(final long millis, final FWeekday weekday, final FTimeZone timeZone) {
        final long offset = getTimeZoneOffsetMilliseconds(millis, timeZone);
        return revertTimeZoneOffset(getFirstWeekdayOfMonth(applyTimeZoneOffset(millis, offset), weekday), offset);
    }

    public static long getFirstWeekdayOfMonth(final long millis, final FWeekday weekday) {
        final long firstWeekDay = setFWeekday(setDay(withoutTime(millis), 1), weekday);
        if (!FDatesMillis.isSameMonth(millis, firstWeekDay)) {
            return addWeeks(firstWeekDay, 1);
        } else {
            return firstWeekDay;
        }
    }

    public static long getFirstWorkdayOfMonth(final long millis, final FHolidayManager holidayManager,
            final FTimeZone timeZone) {
        final long offset = getTimeZoneOffsetMilliseconds(millis, timeZone);
        return revertTimeZoneOffset(getFirstWorkdayOfMonth(applyTimeZoneOffset(millis, offset), holidayManager),
                offset);
    }

    public static long getFirstWorkdayOfMonth(final long millis, final FHolidayManager holidayManager) {
        long firstWorkdayDay = setDay(withoutTime(millis), 1);
        while (getFWeekday(firstWorkdayDay).isWeekend() || isHoliday(firstWorkdayDay, holidayManager)) {
            firstWorkdayDay = addDays(firstWorkdayDay, 1);
        }
        return firstWorkdayDay;
    }

    public static boolean isHoliday(final long millis, final FHolidayManager holidayManager) {
        if (holidayManager == null) {
            return false;
        }
        return holidayManager.isHoliday(millis);
    }

    public static boolean isHoliday(final long millis, final FHolidayManager holidayManager, final FTimeZone timeZone) {
        return isHoliday(applyTimeZoneOffset(millis, timeZone), holidayManager);
    }

    public static long addWorkdays(final long millis, final int workdays, final FHolidayManager holidayManager,
            final FTimeZone timeZone) {
        final long offset = getTimeZoneOffsetMilliseconds(millis, timeZone);
        return revertTimeZoneOffset(addWorkdays(applyTimeZoneOffset(millis, offset), workdays, holidayManager), offset);
    }

    public static long addWorkdays(final long millis, final int workdays, final FHolidayManager holidayManager) {
        int workdaysToShift = Integers.abs(workdays);
        if (getFWeekday(millis).isWeekend() || isHoliday(millis, holidayManager)) {
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
        long cur = millis;
        while (workdaysShifted < workdaysToShift) {
            if (!getFWeekday(cur).isWeekend() && !isHoliday(cur, holidayManager)) {
                workdaysShifted++;
            }
            cur = addDays(cur, shiftUnit);
        }
        return cur;
    }

}
