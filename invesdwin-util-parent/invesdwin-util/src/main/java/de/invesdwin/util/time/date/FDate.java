package de.invesdwin.util.time.date;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.concurrent.ThreadSafe;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.ReadableDateTime;
import org.joda.time.ReadablePartial;

import de.invesdwin.norva.marker.IDate;
import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.loadingcache.historical.IHistoricalEntry;
import de.invesdwin.util.collections.loadingcache.historical.IHistoricalValue;
import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.lang.comparator.IComparator;
import de.invesdwin.util.lang.string.Strings;
import de.invesdwin.util.marshallers.serde.basic.FDateSerde;
import de.invesdwin.util.math.Floats;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.LongPair;
import de.invesdwin.util.math.decimal.scaled.Percent;
import de.invesdwin.util.time.date.format.FDateTimeFormatter;
import de.invesdwin.util.time.date.format.FDateTimeFormatters;
import de.invesdwin.util.time.date.holiday.IHolidayManager;
import de.invesdwin.util.time.date.millis.FDateMillis;
import de.invesdwin.util.time.date.millis.FDatePicos;
import de.invesdwin.util.time.date.millis.WeekAdjustment;
import de.invesdwin.util.time.date.timezone.FTimeZone;
import de.invesdwin.util.time.date.timezone.TimeZoneRange;
import de.invesdwin.util.time.duration.Duration;
import de.invesdwin.util.time.range.TimeRange;
import de.invesdwin.util.time.range.day.FDayTime;
import de.invesdwin.util.time.range.week.FWeekTime;
import jakarta.persistence.Transient;

/**
 * FDate stands for an immutable Fast Date implementation by utilizing heavy caching.
 */
@ThreadSafe
public class FDate extends Number implements IDate, Serializable, Cloneable, Comparable<Object>,
        IHistoricalValue<FDate>, IFDateProvider, IFDateData {

    public static final IComparator<FDate> COMPARATOR = IComparator.getDefaultInstance();

    public static final int BYTES = FDateSerde.FIXED_LENGTH;

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
    public static final String FORMAT_ISO_TIME_US = FORMAT_ISO_TIME_MS + ".UUU";
    public static final String FORMAT_ISO_TIME_NS = FORMAT_ISO_TIME_US + ".NNN";
    public static final String FORMAT_ISO_TIME_PS = FORMAT_ISO_TIME_NS + ".PPP";
    public static final String FORMAT_ISO_DATE_TIME = FORMAT_ISO_DATE + "'T'" + FORMAT_ISO_TIME;
    public static final String FORMAT_ISO_DATE_TIME_SPACE = FORMAT_ISO_DATE + " " + FORMAT_ISO_TIME;
    public static final String FORMAT_ISO_DATE_TIME_MS = FORMAT_ISO_DATE + "'T'" + FORMAT_ISO_TIME_MS;
    public static final int FORMAT_ISO_DATE_TIME_MS_LENGTH = 23;
    public static final int FORMAT_ISO_DATE_TIME_US_LENGTH = FORMAT_ISO_DATE_TIME_MS_LENGTH + 4;
    public static final int FORMAT_ISO_DATE_TIME_NS_LENGTH = FORMAT_ISO_DATE_TIME_US_LENGTH + 4;
    public static final int FORMAT_ISO_DATE_TIME_PS_LENGTH = FORMAT_ISO_DATE_TIME_NS_LENGTH + 4;
    public static final String FORMAT_ISO_DATE_TIME_US = FORMAT_ISO_DATE + "'T'" + FORMAT_ISO_TIME_US;
    public static final String FORMAT_ISO_DATE_TIME_NS = FORMAT_ISO_DATE + "'T'" + FORMAT_ISO_TIME_NS;
    public static final String FORMAT_ISO_DATE_TIME_PS = FORMAT_ISO_DATE + "'T'" + FORMAT_ISO_TIME_PS;
    public static final String FORMAT_ISO_DATE_TIME_MS_SPACE = FORMAT_ISO_DATE + " " + FORMAT_ISO_TIME_MS;
    public static final String FORMAT_ISO_DATE_TIME_US_SPACE = FORMAT_ISO_DATE + " " + FORMAT_ISO_TIME_US;
    public static final String FORMAT_ISO_DATE_TIME_NS_SPACE = FORMAT_ISO_DATE + " " + FORMAT_ISO_TIME_NS;
    public static final String FORMAT_ISO_DATE_TIME_PS_SPACE = FORMAT_ISO_DATE + " " + FORMAT_ISO_TIME_PS;

    public static final String FORMAT_NUMBER_DATE = "yyyyMMdd";
    public static final String FORMAT_NUMBER_TIME = "HHmmss";
    public static final String FORMAT_NUMBER_TIME_MS = FORMAT_NUMBER_TIME + "SSS";
    public static final String FORMAT_NUMBER_TIME_US = FORMAT_NUMBER_TIME_MS + "UUU";
    public static final String FORMAT_NUMBER_TIME_NS = FORMAT_NUMBER_TIME_US + "NNN";
    public static final String FORMAT_NUMBER_TIME_PS = FORMAT_NUMBER_TIME_NS + "PPP";
    public static final String FORMAT_NUMBER_DATE_TIME = FORMAT_NUMBER_DATE + FORMAT_NUMBER_TIME;
    public static final String FORMAT_NUMBER_DATE_TIME_MS = FORMAT_NUMBER_DATE + FORMAT_NUMBER_TIME_MS;
    public static final String FORMAT_NUMBER_DATE_TIME_US = FORMAT_NUMBER_DATE + FORMAT_NUMBER_TIME_US;
    public static final String FORMAT_NUMBER_DATE_TIME_NS = FORMAT_NUMBER_DATE + FORMAT_NUMBER_TIME_NS;
    public static final String FORMAT_NUMBER_DATE_TIME_PS = FORMAT_NUMBER_DATE + FORMAT_NUMBER_TIME_PS;

    public static final String FORMAT_UNDERSCORE_DATE = "yyyy_MM_dd";
    public static final String FORMAT_UNDERSCORE_TIME_MINUTE = "HH_mm";
    public static final String FORMAT_UNDERSCORE_DATE_TIME_MINUTE = FORMAT_UNDERSCORE_DATE + "_"
            + FORMAT_UNDERSCORE_TIME_MINUTE;
    public static final String FORMAT_UNDERSCORE_TIME = FORMAT_UNDERSCORE_TIME_MINUTE + "_ss";
    public static final String FORMAT_UNDERSCORE_TIME_MS = FORMAT_UNDERSCORE_TIME + "_SSS";
    public static final String FORMAT_UNDERSCORE_TIME_US = FORMAT_UNDERSCORE_TIME_MS + "_UUU";
    public static final String FORMAT_UNDERSCORE_TIME_NS = FORMAT_UNDERSCORE_TIME_US + "_NNN";
    public static final String FORMAT_UNDERSCORE_TIME_PS = FORMAT_UNDERSCORE_TIME_NS + "_PPP";
    public static final String FORMAT_UNDERSCORE_DATE_TIME = FORMAT_UNDERSCORE_DATE + "_" + FORMAT_UNDERSCORE_TIME;
    public static final String FORMAT_UNDERSCORE_DATE_TIME_MS = FORMAT_UNDERSCORE_DATE + "_"
            + FORMAT_UNDERSCORE_TIME_MS;
    public static final String FORMAT_UNDERSCORE_DATE_TIME_US = FORMAT_UNDERSCORE_DATE + "_"
            + FORMAT_UNDERSCORE_TIME_US;
    public static final String FORMAT_UNDERSCORE_DATE_TIME_NS = FORMAT_UNDERSCORE_DATE + "_"
            + FORMAT_UNDERSCORE_TIME_NS;
    public static final String FORMAT_UNDERSCORE_DATE_TIME_PS = FORMAT_UNDERSCORE_DATE + "_"
            + FORMAT_UNDERSCORE_TIME_PS;

    public static final String FORMAT_GERMAN_DATE = "dd.MM.yyyy";
    public static final String FORMAT_GERMAN_DATE_TIME = FORMAT_GERMAN_DATE + " " + FORMAT_ISO_TIME;
    public static final String FORMAT_GERMAN_DATE_TIME_MS = FORMAT_GERMAN_DATE + " " + FORMAT_ISO_TIME_MS;
    public static final String FORMAT_GERMAN_DATE_TIME_US = FORMAT_GERMAN_DATE + " " + FORMAT_ISO_TIME_US;
    public static final String FORMAT_GERMAN_DATE_TIME_NS = FORMAT_GERMAN_DATE + " " + FORMAT_ISO_TIME_NS;
    public static final String FORMAT_GERMAN_DATE_TIME_PS = FORMAT_GERMAN_DATE + " " + FORMAT_ISO_TIME_PS;

    public static final FDate[] EMPTY_ARRAY = new FDate[0];

    private final long millis;
    private final int picos;
    @Transient
    private transient Object extension;

    public FDate(final long millis) {
        this(millis, 0);
    }

    public FDate(final double millisFractionalPicos) {
        this.millis = (long) millisFractionalPicos;
        this.picos = (int) ((millisFractionalPicos - millis) * FTimeUnit.PICOSECONDS_IN_MILLISECOND);
        assert FDatePicos.isValidPicos(picos) : "Picos value out of range [0 to " + FDatePicos.END_OF_DAY_PICOS + "]: "
                + picos;
    }

    public FDate(final double timeUnitFractional, final FTimeUnit timeUnit) {
        this(timeUnitFractional, timeUnit.asFractional());
    }

    public FDate(final double timeUnitFractional, final FTimeUnitFractional timeUnit) {
        final double millisFractional = timeUnit.toMillis(timeUnitFractional);
        this.millis = (long) millisFractional;
        this.picos = (int) ((millisFractional - millis) * FTimeUnit.PICOSECONDS_IN_MILLISECOND);
        assert FDatePicos.isValidPicos(picos) : "Picos value out of range [0 to " + FDatePicos.END_OF_DAY_PICOS + "]: "
                + picos;
    }

    public FDate(final long millis, final int picos) {
        this.millis = millis;
        this.picos = picos;
        assert FDatePicos.isValidPicos(picos) : "Picos value out of range [0 to " + FDatePicos.END_OF_DAY_PICOS + "]: "
                + picos;
    }

    public FDate(final IFDateData date) {
        this.millis = date.millisValue();
        this.picos = date.picosValue();
        assert FDatePicos.isValidPicos(picos) : "Picos value out of range [0 to " + FDatePicos.END_OF_DAY_PICOS + "]: "
                + picos;
    }

    public FDate(final long duration, final FTimeUnit timeUnit) {
        switch (timeUnit) {
        case MILLENIA:
            millis = duration * FTimeUnit.MILLISECONDS_IN_MILLENIUM;
            picos = 0;
            break;
        case CENTURIES:
            millis = duration * FTimeUnit.MILLISECONDS_IN_CENTURY;
            picos = 0;
            break;
        case DECADES:
            millis = duration * FTimeUnit.MILLISECONDS_IN_DECADE;
            picos = 0;
            break;
        case YEARS:
            millis = duration * FTimeUnit.MILLISECONDS_IN_YEAR;
            picos = 0;
            break;
        case MONTHS:
            millis = duration * FTimeUnit.MILLISECONDS_IN_MONTH;
            picos = 0;
            break;
        case WEEKS:
            millis = duration * FTimeUnit.MILLISECONDS_IN_WEEK;
            picos = 0;
            break;
        case DAYS:
            millis = duration * FTimeUnit.MILLISECONDS_IN_DAY;
            picos = 0;
            break;
        case HOURS:
            millis = duration * FTimeUnit.MILLISECONDS_IN_HOUR;
            picos = 0;
            break;
        case MINUTES:
            millis = duration * FTimeUnit.MILLISECONDS_IN_MINUTE;
            picos = 0;
            break;
        case SECONDS:
            millis = duration * FTimeUnit.MILLISECONDS_IN_SECOND;
            picos = 0;
            break;
        case MILLISECONDS:
            millis = duration;
            picos = 0;
            break;
        case MICROSECONDS: {
            final long milliseconds = duration / FTimeUnit.MICROSECONDS_IN_MILLISECOND;
            final long microseconds = duration % FTimeUnit.MICROSECONDS_IN_MILLISECOND;
            final long picosMaybeOverflow = FDatePicos.addMicrosecondsMaybeOverflow(0, microseconds);
            final long millisOverflow = FDatePicos.toMillisecondsOverflow(picosMaybeOverflow);
            millis = milliseconds + millisOverflow;
            picos = FDatePicos.toPicosWithoutOverflow(picosMaybeOverflow);
            break;
        }
        case NANOSECONDS: {
            final long milliseconds = duration / FTimeUnit.NANOSECONDS_IN_MILLISECOND;
            final long nanoseconds = duration % FTimeUnit.NANOSECONDS_IN_MILLISECOND;
            final long picosMaybeOverflow = FDatePicos.addNanosecondsMaybeOverflow(0, nanoseconds);
            final long millisOverflow = FDatePicos.toMillisecondsOverflow(picosMaybeOverflow);
            millis = milliseconds + millisOverflow;
            picos = FDatePicos.toPicosWithoutOverflow(picosMaybeOverflow);
            break;
        }
        case PICOSECONDS: {
            final long milliseconds = duration / FTimeUnit.PICOSECONDS_IN_MILLISECOND;
            final long picoseconds = duration % FTimeUnit.PICOSECONDS_IN_MILLISECOND;
            final long picosMaybeOverflow = FDatePicos.addPicosecondsMaybeOverflow(0, picoseconds);
            final long millisOverflow = FDatePicos.toMillisecondsOverflow(picosMaybeOverflow);
            millis = milliseconds + millisOverflow;
            picos = FDatePicos.toPicosWithoutOverflow(picosMaybeOverflow);
            break;
        }
        default:
            throw UnknownArgumentException.newInstance(FTimeUnit.class, timeUnit);
        }
        assert FDatePicos.isValidPicos(picos) : "Picos value out of range [0 to " + FDatePicos.END_OF_DAY_PICOS + "]: "
                + picos;
    }

    public FDate(final ReadableDateTime jodaTime) {
        this(FDateMillis.valueOfNotNullSafe(jodaTime), 0);
    }

    public FDate(final LocalDateTime jodaTime) {
        this(FDateMillis.valueOfNotNullSafe(jodaTime), 0);
    }

    public FDate(final ReadablePartial jodaDate) {
        this(FDateMillis.valueOfNotNullSafe(jodaDate), 0);
    }

    public FDate(final LocalDate jodaDate) {
        this(FDateMillis.valueOfNotNullSafe(jodaDate), 0);
    }

    public FDate(final java.time.ZonedDateTime javaTime) {
        this(FDateMillis.valueOfNotNullSafe(javaTime), 0);
    }

    public FDate(final java.time.LocalDate javaDate) {
        this(FDateMillis.valueOfNotNullSafe(javaDate), 0);
    }

    public FDate(final java.time.LocalDateTime javaTime) {
        this(FDateMillis.valueOfNotNullSafe(javaTime), 0);
    }

    public FDate(final Calendar calendar) {
        this(FDateMillis.valueOfNotNullSafe(calendar), 0);
    }

    public FDate(final Date date) {
        this(FDateMillis.valueOfNotNullSafe(date), 0);
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

    public int getMicrosecond() {
        return FDatePicos.getMicrosecond(picos);
    }

    public int getNanosecond() {
        return FDatePicos.getNanosecond(picos);
    }

    public int getPicosecond() {
        return FDatePicos.getPicosecond(picos);
    }

    public FTimeZone getTimeZone() {
        return FDates.getDefaultTimeZone();
    }

    public FDate setYear(final int year, final FTimeZone timeZone) {
        return new FDate(FDateMillis.setYear(millis, year, timeZone), picos);
    }

    public FDate setYear(final int year) {
        return new FDate(FDateMillis.setYear(millis, year), picos);
    }

    public FDate setMonth(final int month, final FTimeZone timeZone) {
        return new FDate(FDateMillis.setMonth(millis, month, timeZone), picos);
    }

    public FDate setMonth(final int month) {
        return new FDate(FDateMillis.setMonth(millis, month), picos);
    }

    public FDate setFMonth(final FMonth month, final FTimeZone timeZone) {
        return new FDate(FDateMillis.setFMonth(millis, month, timeZone), picos);
    }

    public FDate setFMonth(final FMonth month) {
        return new FDate(FDateMillis.setFMonth(millis, month), picos);
    }

    public FDate setDay(final int day, final FTimeZone timeZone) {
        return new FDate(FDateMillis.setDay(millis, day, timeZone), picos);
    }

    public FDate setDay(final int day) {
        return new FDate(FDateMillis.setDay(millis, day), picos);
    }

    public FDate setWeekday(final int weekday, final WeekAdjustment adjustment, final FTimeZone timeZone) {
        return new FDate(FDateMillis.setWeekday(millis, weekday, adjustment, timeZone), picos);
    }

    public FDate setWeekday(final int weekday, final WeekAdjustment adjustment) {
        return new FDate(FDateMillis.setWeekday(millis, weekday, adjustment), picos);
    }

    public FDate setFWeekday(final FWeekday weekday, final WeekAdjustment adjustment, final FTimeZone timeZone) {
        return new FDate(FDateMillis.setFWeekday(millis, weekday, adjustment, timeZone), picos);
    }

    public FDate setFWeekday(final FWeekday weekday, final WeekAdjustment adjustment) {
        return new FDate(FDateMillis.setFWeekday(millis, weekday, adjustment), picos);
    }

    public FDate setFWeekTime(final FWeekTime weekTime, final WeekAdjustment adjustment, final FTimeZone timeZone) {
        return new FDate(FDateMillis.setFWeekTime(millis, weekTime, adjustment, timeZone), 0);
    }

    public FDate setFWeekTime(final FWeekTime weekTime, final WeekAdjustment adjustment) {
        return new FDate(FDateMillis.setFWeekTime(millis, weekTime, adjustment), 0);
    }

    public FDate setFDayTime(final FDayTime dayTime, final FTimeZone timeZone) {
        return new FDate(FDateMillis.setFDayTime(millis, dayTime, timeZone), 0);
    }

    public FDate setFDayTime(final FDayTime dayTime) {
        return new FDate(FDateMillis.setFDayTime(millis, dayTime), 0);
    }

    public FDate setTime(final FDate time, final FTimeZone timeZone) {
        return new FDate(FDateMillis.setTime(millis, time.millisValue(), timeZone), time.picos);
    }

    public FDate setTime(final FDate time) {
        return new FDate(FDateMillis.setTime(millis, time.millisValue()), time.picos);
    }

    public FDate setHour(final int hour, final FTimeZone timeZone) {
        return new FDate(FDateMillis.setHour(millis, hour, timeZone), picos);
    }

    public FDate setHour(final int hour) {
        return new FDate(FDateMillis.setHour(millis, hour), picos);
    }

    public FDate setMinute(final int minute) {
        return new FDate(FDateMillis.setMinute(millis, minute), picos);
    }

    public FDate setSecond(final int second) {
        return new FDate(FDateMillis.setSecond(millis, second), picos);
    }

    public FDate setMillisecond(final int millisecond) {
        return new FDate(FDateMillis.setMillisecond(millis, millisecond), picos);
    }

    public FDate addYears(final long years, final FTimeZone timeZone) {
        if (years == 0) {
            return this;
        }
        return new FDate(FDateMillis.addYears(millis, years, timeZone), picos);
    }

    public FDate addYears(final long years) {
        if (years == 0) {
            return this;
        }
        return new FDate(FDateMillis.addYears(millis, years), picos);
    }

    public FDate addMonths(final long months, final FTimeZone timeZone) {
        if (months == 0) {
            return this;
        }
        return new FDate(FDateMillis.addMonths(millis, months, timeZone), picos);
    }

    public FDate addMonths(final long months) {
        if (months == 0) {
            return this;
        }
        return new FDate(FDateMillis.addMonths(millis, months), picos);
    }

    public FDate addWeeks(final long weeks) {
        if (weeks == 0) {
            return this;
        }
        return new FDate(FDateMillis.addWeeks(millis, weeks), picos);
    }

    public FDate addDays(final long days) {
        if (days == 0) {
            return this;
        }
        return new FDate(FDateMillis.addDays(millis, days), picos);
    }

    public FDate addHours(final long hours) {
        if (hours == 0) {
            return this;
        }
        return new FDate(FDateMillis.addHours(millis, hours), picos);
    }

    public FDate addMinutes(final long minutes) {
        if (minutes == 0) {
            return this;
        }
        return new FDate(FDateMillis.addMinutes(millis, minutes), picos);
    }

    public FDate addSeconds(final long seconds) {
        if (seconds == 0) {
            return this;
        }
        return new FDate(FDateMillis.addSeconds(millis, seconds), picos);
    }

    public FDate addMilliseconds(final long milliseconds) {
        if (milliseconds == 0) {
            return this;
        }
        return new FDate(FDateMillis.addMilliseconds(millis, milliseconds), picos);
    }

    public FDate addMicroseconds(final long microseconds) {
        if (microseconds == 0) {
            return this;
        }
        final long milliseconds = microseconds / FTimeUnit.MICROSECONDS_IN_MILLISECOND;
        final long remainingMicroseconds = microseconds % FTimeUnit.MICROSECONDS_IN_MILLISECOND;
        final long picosMaybeOverflow = FDatePicos.addMicrosecondsMaybeOverflow(picos, remainingMicroseconds);
        final int picosWithoutOverflow = FDatePicos.toPicosWithoutOverflow(picosMaybeOverflow);
        final long millisecondsOverflow = FDatePicos.toMillisecondsOverflow(picosMaybeOverflow);
        final long millisecondsWithOverflow = milliseconds + millisecondsOverflow;
        return new FDate(FDateMillis.addMilliseconds(millis, millisecondsWithOverflow), picosWithoutOverflow);
    }

    public FDate addNanoseconds(final long nanoseconds) {
        if (nanoseconds == 0) {
            return this;
        }
        final long milliseconds = nanoseconds / FTimeUnit.NANOSECONDS_IN_MILLISECOND;
        final long remainingNanoseconds = nanoseconds % FTimeUnit.NANOSECONDS_IN_MILLISECOND;
        final long picosMaybeOverflow = FDatePicos.addNanosecondsMaybeOverflow(picos, remainingNanoseconds);
        final int picosWithoutOverflow = FDatePicos.toPicosWithoutOverflow(picosMaybeOverflow);
        final long millisecondsOverflow = FDatePicos.toMillisecondsOverflow(picosMaybeOverflow);
        final long millisecondsWithOverflow = milliseconds + millisecondsOverflow;
        return new FDate(FDateMillis.addMilliseconds(millis, millisecondsWithOverflow), picosWithoutOverflow);
    }

    public FDate addPicoseconds(final long picoseconds) {
        if (picoseconds == 0) {
            return this;
        }

        final long milliseconds = picoseconds / FTimeUnit.PICOSECONDS_IN_MILLISECOND;
        final long remainingPicoseconds = picoseconds % FTimeUnit.PICOSECONDS_IN_MILLISECOND;
        final long picosMaybeOverflow = FDatePicos.addPicosecondsMaybeOverflow(picos, remainingPicoseconds);
        final int picosWithoutOverflow = FDatePicos.toPicosWithoutOverflow(picosMaybeOverflow);
        final long millisecondsOverflow = FDatePicos.toMillisecondsOverflow(picosMaybeOverflow);
        final long millisecondsWithOverflow = milliseconds + millisecondsOverflow;
        return new FDate(FDateMillis.addMilliseconds(millis, millisecondsWithOverflow), picosWithoutOverflow);
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
        return new FDate(FDateMillis.set(millis, field, value, timeZone), picos);
    }

    public FDate set(final FDateField field, final int value) {
        if (value == 0) {
            return this;
        }
        return new FDate(FDateMillis.set(millis, field, value), picos);
    }

    public FDate add(final FTimeUnit field, final long value, final FTimeZone timeZone) {
        if (value == 0) {
            return this;
        }
        final long picosMaybeOverflow = FDatePicos.addMaybeOverflow(picos, field, value);
        final int picosWithoutOverflow = FDatePicos.toPicosWithoutOverflow(picosMaybeOverflow);
        final long millisecondsOverflow = FDatePicos.toMillisecondsOverflow(picosMaybeOverflow);
        return new FDate(FDateMillis.add(millis + millisecondsOverflow, field, value, timeZone), picosWithoutOverflow);
    }

    public FDate add(final FTimeUnit field, final long value) {
        if (value == 0) {
            return this;
        }
        final long picosMaybeOverflow = FDatePicos.addMaybeOverflow(picos, field, value);
        final int picosWithoutOverflow = FDatePicos.toPicosWithoutOverflow(picosMaybeOverflow);
        final long millisecondsOverflow = FDatePicos.toMillisecondsOverflow(picosMaybeOverflow);
        return new FDate(FDateMillis.add(millis + millisecondsOverflow, field, value), picosWithoutOverflow);
    }

    public FDate add(final Duration duration) {
        return duration.addTo(this);
    }

    public FDate addMaybeAsTimeUnit(final Duration duration) {
        final FTimeUnit timeUnit = FTimeUnit.valueOf(duration);
        if (timeUnit == null) {
            for (final FTimeUnit potentialTimeUnit : FTimeUnit.values()) {
                if (duration.isExactMultipleOfPeriod(potentialTimeUnit.durationValue())) {
                    final int multiples = (int) duration.getNumMultipleOfPeriod(potentialTimeUnit.durationValue());
                    if (multiples > 0) {
                        return add(potentialTimeUnit, multiples);
                    }
                }
            }
            return add(duration);
        } else {
            if (duration.isPositiveOrZero()) {
                return add(timeUnit, 1);
            } else {
                return add(timeUnit, -1);
            }
        }
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
        return new FDate(FDateMillis.truncate(millis, field, timeZone), 0);
    }

    public FDate truncate(final FDateField field) {
        return new FDate(FDateMillis.truncate(millis, field), 0);
    }

    public FDate truncate(final FTimeUnit timeUnit, final FTimeZone timeZone) {
        return new FDate(FDateMillis.truncate(millis, timeUnit, timeZone), FDatePicos.truncate(picos, timeUnit));
    }

    public FDate truncate(final FTimeUnit timeUnit) {
        return new FDate(FDateMillis.truncate(millis, timeUnit), FDatePicos.truncate(picos, timeUnit));
    }

    /**
     * sets hour, minute, second and millisecond each to 0.
     */
    //CHECKSTYLE:OFF
    public FDate withoutTime() {
        //CHECKSTYLE:ON
        return new FDate(FDateMillis.withoutTime(millis), 0);
    }

    //CHECKSTYLE:OFF
    public FDate withoutTime(final FTimeZone timeZone) {
        //CHECKSTYLE:ON
        return new FDate(FDateMillis.withoutTime(millis, timeZone), 0);
    }

    /**
     * Pretend that this date is inside the given timezone. E.g. UTC will add 2 hours for becoming EET.
     */
    public FDate applyTimeZoneOffset(final FTimeZone timeZone) {
        if (timeZone == null || timeZone.equals(getTimeZone())) {
            return this;
        }
        return new FDate(FDateMillis.applyTimeZoneOffset(millis, timeZone), picos);
    }

    public TimeRange applyTimeZoneOffset(final TimeZoneRange timeZone) {
        if (timeZone == null || timeZone.equals(getTimeZone())) {
            return new TimeRange(this, this);
        } else {
            return timeZone.applyTimeZoneOffset(this);
        }
    }

    /**
     * Pretend that this date is inside the given timezone. E.g. UTC will add 2 hours for becoming EET.
     */
    public FDate applyTimeZoneOffset(final long timeZoneOffsetMilliseconds) {
        if (timeZoneOffsetMilliseconds == 0) {
            return this;
        }
        return new FDate(FDateMillis.applyTimeZoneOffset(millis, timeZoneOffsetMilliseconds), picos);
    }

    public TimeRange applyTimeZoneOffset(final LongPair timeZoneOffsetMilliseconds) {
        if (timeZoneOffsetMilliseconds.getFirstValue() == 0 && timeZoneOffsetMilliseconds.getSecondValue() == 0) {
            return new TimeRange(this, this);
        } else if (timeZoneOffsetMilliseconds.getFirstValue() == timeZoneOffsetMilliseconds.getSecondValue()) {
            final FDate fromAndTo = new FDate(
                    FDateMillis.applyTimeZoneOffset(millis, timeZoneOffsetMilliseconds.getFirstValue()), picos);
            return new TimeRange(fromAndTo, fromAndTo);
        } else {
            final FDate from = new FDate(
                    FDateMillis.applyTimeZoneOffset(millis, timeZoneOffsetMilliseconds.getFirstValue()), picos);
            final FDate to = new FDate(
                    FDateMillis.applyTimeZoneOffset(millis, timeZoneOffsetMilliseconds.getSecondValue()), picos);
            return new TimeRange(from, to);
        }
    }

    public long getTimeZoneOffsetMilliseconds(final FTimeZone timeZone) {
        return FDateMillis.getTimeZoneOffsetMilliseconds(millis, timeZone);
    }

    public LongPair getTimeZoneOffsetMilliseconds(final TimeZoneRange timeZone) {
        if (timeZone == null) {
            return LongPair.empty();
        } else {
            return timeZone.getTimeZoneOffsetMilliseconds(this);
        }
    }

    /**
     * Go back to the default timezone for a data that was converted into another timezone.
     * 
     * FromGivenTimeZoneToUTC: Converts from the given TimeZone to default TimeZone (normally UTC).
     * 
     * WARNING: this can cause issues when apply/revert is used with offsetTimeZone because right at daylight saving
     * time switch the reference changes and can cause 1 hour difference. So better use getTimeZoneOffset as a long and
     * use apply/revert with that long value instead of this dynamic version.
     */
    @Deprecated
    public FDate revertTimeZoneOffset(final FTimeZone timeZone) {
        if (timeZone == null || timeZone.equals(getTimeZone())) {
            return this;
        }
        return new FDate(FDateMillis.revertTimeZoneOffset(millis, timeZone), picos);
    }

    /**
     * Go back to the default timezone for a data that was converted into another timezone.
     * 
     * FromUTCToGivenTimeZone: Converts from default TimeZone (normally UTC) to the given TimeZone.
     */
    public FDate revertTimeZoneOffset(final long timeZoneOffsetMilliseconds) {
        if (timeZoneOffsetMilliseconds == 0) {
            return this;
        }
        return new FDate(FDateMillis.revertTimeZoneOffset(millis, timeZoneOffsetMilliseconds), picos);
    }

    /**
     * sets hour, minute, second and millisecond each to 23:59:999.
     */
    public FDate atEndOfDay() {
        return new FDate(FDateMillis.atEndOfDay(millis), FDatePicos.END_OF_DAY_PICOS);
    }

    public FDate atEndOfDay(final FTimeZone timeZone) {
        return new FDate(FDateMillis.atEndOfDay(millis, timeZone), FDatePicos.END_OF_DAY_PICOS);
    }

    /**
     * Returns the number of milliseconds since January 1, 1970, 00:00:00 GMT.
     */
    @Override
    public long millisValue() {
        return millis;
    }

    /**
     * Returns the number of picoseconds within the millisecond, i.e. the fraction of the millisecond. E.g. 1
     * microsecond is 1,000,000 picoseconds and 1 nanosecond is 1,000 picoseconds.
     */
    @Override
    public int picosValue() {
        return picos;
    }

    @Override
    public double doubleValue() {
        return millis + (double) picos / FTimeUnit.PICOSECONDS_IN_MILLISECOND;
    }

    public double doubleValue(final FTimeUnit timeUnit) {
        final double millisDouble = millis;
        final double picosDouble = picos;
        final double duration;
        switch (timeUnit) {
        case MILLENIA:
            duration = millisDouble / FTimeUnit.MILLISECONDS_IN_MILLENIUM
                    + picosDouble / FTimeUnit.PICOSECONDS_IN_MILLENIUM;
            break;
        case CENTURIES:
            duration = millisDouble / FTimeUnit.MILLISECONDS_IN_CENTURY
                    + picosDouble / FTimeUnit.PICOSECONDS_IN_CENTURY;
            break;
        case DECADES:
            duration = millisDouble / FTimeUnit.MILLISECONDS_IN_DECADE + picosDouble / FTimeUnit.PICOSECONDS_IN_DECADE;
            break;
        case YEARS:
            duration = millisDouble / FTimeUnit.MILLISECONDS_IN_YEAR + picosDouble / FTimeUnit.PICOSECONDS_IN_YEAR;
            break;
        case MONTHS:
            duration = millisDouble / FTimeUnit.MILLISECONDS_IN_MONTH + picosDouble / FTimeUnit.PICOSECONDS_IN_MONTH;
            break;
        case WEEKS:
            duration = millisDouble / FTimeUnit.MILLISECONDS_IN_WEEK + picosDouble / FTimeUnit.PICOSECONDS_IN_WEEK;
            break;
        case DAYS:
            duration = millisDouble / FTimeUnit.MILLISECONDS_IN_DAY + picosDouble / FTimeUnit.PICOSECONDS_IN_DAY;
            break;
        case HOURS:
            duration = millisDouble / FTimeUnit.MILLISECONDS_IN_HOUR + picosDouble / FTimeUnit.PICOSECONDS_IN_HOUR;
            break;
        case MINUTES:
            duration = millisDouble / FTimeUnit.MILLISECONDS_IN_MINUTE + picosDouble / FTimeUnit.PICOSECONDS_IN_MINUTE;
            break;
        case SECONDS:
            duration = millisDouble / FTimeUnit.MILLISECONDS_IN_SECOND + picosDouble / FTimeUnit.PICOSECONDS_IN_SECOND;
            break;
        case MILLISECONDS:
            duration = millisDouble + picosDouble / FTimeUnit.PICOSECONDS_IN_MILLISECOND;
            break;
        case MICROSECONDS:
            duration = millisDouble * FTimeUnit.MICROSECONDS_IN_MILLISECOND
                    + picosDouble / FTimeUnit.PICOSECONDS_IN_MICROSECOND;
            break;
        case NANOSECONDS:
            duration = millisDouble * FTimeUnit.NANOSECONDS_IN_MILLISECOND
                    + picosDouble / FTimeUnit.PICOSECONDS_IN_NANOSECOND;
            break;
        case PICOSECONDS:
            return doubleValue();
        default:
            throw UnknownArgumentException.newInstance(FTimeUnit.class, timeUnit);
        }
        return duration;
    }

    /**
     * WARNING: use longValue() instead
     */
    @Deprecated
    @Override
    public int intValue() {
        return Integers.checkedCast(millis);
    }

    /**
     * WARNING: use millisValue() or doubleValue() instead
     */
    @Deprecated
    @Override
    public long longValue() {
        return millis;
    }

    public long longValue(final FTimeUnit timeUnit) {
        final long duration;
        switch (timeUnit) {
        case MILLENIA:
            duration = millis / FTimeUnit.MILLISECONDS_IN_MILLENIUM;
            break;
        case CENTURIES:
            duration = millis / FTimeUnit.MILLISECONDS_IN_CENTURY;
            break;
        case DECADES:
            duration = millis / FTimeUnit.MILLISECONDS_IN_DECADE;
            break;
        case YEARS:
            duration = millis / FTimeUnit.MILLISECONDS_IN_YEAR;
            break;
        case MONTHS:
            duration = millis / FTimeUnit.MILLISECONDS_IN_MONTH;
            break;
        case WEEKS:
            duration = millis / FTimeUnit.MILLISECONDS_IN_WEEK;
            break;
        case DAYS:
            duration = millis / FTimeUnit.MILLISECONDS_IN_DAY;
            break;
        case HOURS:
            duration = millis / FTimeUnit.MILLISECONDS_IN_HOUR;
            break;
        case MINUTES:
            duration = millis / FTimeUnit.MILLISECONDS_IN_MINUTE;
            break;
        case SECONDS:
            duration = millis / FTimeUnit.MILLISECONDS_IN_SECOND;
            break;
        case MILLISECONDS:
            return longValue();
        case MICROSECONDS:
            duration = millis * FTimeUnit.MICROSECONDS_IN_MILLISECOND + picos / FTimeUnit.PICOSECONDS_IN_MICROSECOND;
            break;
        case NANOSECONDS:
            duration = millis * FTimeUnit.NANOSECONDS_IN_MILLISECOND + picos / FTimeUnit.PICOSECONDS_IN_NANOSECOND;
            break;
        case PICOSECONDS:
            duration = millis * FTimeUnit.PICOSECONDS_IN_MILLISECOND + picos;
            break;
        default:
            throw UnknownArgumentException.newInstance(FTimeUnit.class, timeUnit);
        }
        return duration;
    }

    /**
     * WARNING: use doubleValue() instead
     */
    @Deprecated
    @Override
    public float floatValue() {
        return Floats.checkedCast(doubleValue());
    }

    public Duration toDuration() {
        return toDuration(now());
    }

    public Duration toDuration(final FDate now) {
        return new Duration(this, now(), FTimeUnit.MILLISECONDS);
    }

    public boolean isGreaterThan(final Duration duration) {
        return duration.isLessThanOrEqualTo(this);
    }

    public boolean isGreaterThanOrEqualTo(final Duration duration) {
        return duration.isLessThan(this);
    }

    public boolean isLessThan(final Duration duration) {
        return duration.isGreaterThanOrEqualTo(this);
    }

    public boolean isLessThanOrEqualTo(final Duration duration) {
        return duration.isGreaterThan(this);
    }

    public Date dateValue() {
        return FDateMillis.dateValue(millis);
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

    public java.time.ZonedDateTime javaTimeValueZoned(final FTimeZone timeZone) {
        return FDateMillis.javaTimeValueZoned(millis, timeZone);
    }

    public LocalDate jodaDateValue() {
        return FDateMillis.jodaDateValue(millis);
    }

    public LocalDate jodaDateValue(final FTimeZone timeZone) {
        return FDateMillis.jodaDateValue(millis, timeZone);
    }

    public java.time.LocalDateTime javaTimeValue() {
        return FDateMillis.javaTimeValue(millis);
    }

    public java.time.LocalDateTime javaTimeValue(final FTimeZone timeZone) {
        return FDateMillis.javaTimeValue(millis, timeZone);
    }

    public java.time.LocalDate javaDateValue() {
        return FDateMillis.javaDateValue(millis);
    }

    public java.time.LocalDate javaDateValue(final FTimeZone timeZone) {
        return FDateMillis.javaDateValue(millis, timeZone);
    }

    public static FDate valueOf(final java.time.Instant instant) {
        return valueOfEpochSeconds(instant.getEpochSecond(), instant.getNano());
    }

    public static FDate valueOfEpochSeconds(final long seconds) {
        final long millis = seconds * FTimeUnit.MILLISECONDS_IN_SECOND;
        return new FDate(millis, 0);
    }

    public static FDate valueOfEpochSeconds(final long seconds, final long nanos) {
        final long picosMaybeOverflow = nanos * FTimeUnit.PICOSECONDS_IN_NANOSECOND;
        final long millisOverflow = FDatePicos.toMillisecondsOverflow(picosMaybeOverflow);
        final int picos = FDatePicos.toPicosWithoutOverflow(picosMaybeOverflow);
        final long millis = seconds * FTimeUnit.MILLISECONDS_IN_SECOND + millisOverflow;
        return new FDate(millis, picos);
    }

    public static FDate valueOf(final long millis) {
        return new FDate(millis, 0);
    }

    public static FDate valueOf(final long millis, final int picos) {
        return new FDate(millis, picos);
    }

    public static FDate valueOf(final Long millis) {
        if (millis != null) {
            return new FDate(millis, 0);
        } else {
            return null;
        }
    }

    public static FDate valueOf(final Long millis, final Integer picos) {
        if (millis != null) {
            return new FDate(millis, Integers.nullToZero(picos));
        } else {
            return null;
        }
    }

    public static FDate valueOf(final Long millis, final int picos) {
        if (millis != null) {
            return new FDate(millis, picos);
        } else {
            return null;
        }
    }

    public static FDate valueOf(final Long value, final FTimeUnit timeUnit) {
        if (value != null) {
            return new FDate(timeUnit.toMillis(value), 0);
        } else {
            return null;
        }
    }

    public static FDate valueOf(final double millisFractionalPicos) {
        return new FDate(millisFractionalPicos);
    }

    public static FDate valueOf(final Double millisFractionalPicos) {
        if (millisFractionalPicos != null) {
            return new FDate(millisFractionalPicos);
        } else {
            return null;
        }
    }

    public static FDate valueOf(final double timeUnitFractional, final FTimeUnit timeUnit) {
        return new FDate(timeUnitFractional, timeUnit);
    }

    public static FDate valueOf(final double timeUnitFractional, final FTimeUnitFractional timeUnit) {
        return new FDate(timeUnitFractional, timeUnit);
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

    public static FDate valueOf(final ReadablePartial jodaDate) {
        if (jodaDate != null) {
            return new FDate(jodaDate);
        } else {
            return null;
        }
    }

    public static FDate valueOf(final LocalDate jodaDate) {
        if (jodaDate != null) {
            return new FDate(jodaDate);
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

    public static FDate valueOf(final java.time.LocalDateTime javaTime) {
        if (javaTime != null) {
            return new FDate(javaTime);
        } else {
            return null;
        }
    }

    public static FDate valueOf(final java.time.LocalDate javaDate) {
        if (javaDate != null) {
            return new FDate(javaDate);
        } else {
            return null;
        }
    }

    public static FDate valueOf(final java.time.ZonedDateTime javaTime) {
        if (javaTime == null) {
            return null;
        } else {
            return new FDate(javaTime);
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
        final FDateTimeFormatter df = FDateTimeFormatter.forPattern(parsePattern);
        if (timeZone != null) {
            return df.parse(str, timeZone, locale);
        } else {
            return df.parse(str, FDates.getDefaultTimeZone(), locale);
        }
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
        return FDates.getDefaultClock().now();
    }

    public static FDate nowMillis() {
        return new FDate(FDateMillis.nowMillis(), 0);
    }

    public static FDate today(final FTimeZone timeZone) {
        return new FDate(FDateMillis.today(timeZone));
    }

    public static FDate today() {
        return new FDate(FDateMillis.today());
    }

    @Override
    public int hashCode() {
        return Long.hashCode(millis) + 31 * Integer.hashCode(picos);
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

    public int compareTo(final Number o) {
        if (o == null) {
            return 1;
        }
        return compareToNotNullSafe(o);
    }

    public int compareToNotNullSafe(final Number o) {
        if (o instanceof FDate) {
            final FDate cO = (FDate) o;
            return compareToNotNullSafe(cO);
        } else {
            return compareTo(o.doubleValue());
        }
    }

    public int compareToNotNullSafe(final FDate o) {
        final int millisCompare = Long.compare(millis, o.millis);
        if (millisCompare != 0) {
            return millisCompare;
        }
        return Integer.compare(picos, o.picos);
    }

    public int compareTo(final double obj) {
        return Double.compare(doubleValue(), obj);
    }

    public boolean equals(final FDate obj) {
        return obj != null && equalsNotNullSafe(obj);
    }

    public boolean equalsNotNullSafe(final FDate obj) {
        return millis == obj.millis && picos == obj.picos;
    }

    public boolean equals(final Number obj) {
        return obj != null && equalsNotNullSafe(obj);
    }

    public boolean equalsNotNullSafe(final Number obj) {
        if (obj instanceof FDate) {
            return equalsNotNullSafe((FDate) obj);
        } else {
            return equalsNotNullSafe(obj.doubleValue());
        }
    }

    public boolean equals(final long millis) {
        return millisValue() == millis && picosValue() == 0;
    }

    public boolean equals(final double obj) {
        return doubleValue() == obj;
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
        return FDateTimeFormatters.toString(millis, picos);
    }

    public String toString(final FTimeZone timeZone) {
        return FDateTimeFormatters.toString(millis, picos, timeZone);
    }

    public String toString(final String format) {
        return FDateTimeFormatters.toString(millis, picos, format);
    }

    public String toString(final String format, final FTimeZone timeZone) {
        return FDateTimeFormatters.toString(millis, picos, format, timeZone);
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
        return millisValue() < other.millisValue()
                || (millisValue() == other.millisValue() && picosValue() < other.picosValue());
    }

    public boolean isBeforeOrEqualToNotNullSafe(final FDate other) {
        return millisValue() < other.millisValue()
                || (millisValue() == other.millisValue() && picosValue() <= other.picosValue());
    }

    public boolean isAfterNotNullSafe(final FDate other) {
        return millisValue() > other.millisValue()
                || (millisValue() == other.millisValue() && picosValue() > other.picosValue());
    }

    public boolean isAfterOrEqualToNotNullSafe(final FDate other) {
        return millisValue() > other.millisValue()
                || (millisValue() == other.millisValue() && picosValue() >= other.picosValue());
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
        return new FDate(FDateMillis.getFirstWeekdayOfMonth(millis, weekday, timeZone), 0);
    }

    public FDate getFirstWeekdayOfMonth(final FWeekday weekday) {
        return new FDate(FDateMillis.getFirstWeekdayOfMonth(millis, weekday), 0);
    }

    public FDate getFirstWorkdayOfMonth(final IHolidayManager holidayManager, final FTimeZone timeZone) {
        return new FDate(FDateMillis.getFirstWorkdayOfMonth(millis, holidayManager, timeZone), 0);
    }

    public FDate getFirstWorkdayOfMonth(final IHolidayManager holidayManager) {
        return new FDate(FDateMillis.getFirstWorkdayOfMonth(millis, holidayManager), 0);
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

    public boolean isBetweenInclusive(final FDate min, final FDate max) {
        return FDates.isBetweenInclusive(this, min, max);
    }

    public boolean isBetweenInclusiveNotNullSafe(final FDate min, final FDate max) {
        return FDates.isBetweenInclusiveNotNullSafe(this, min, max);
    }

    public boolean isBetweenExclusive(final FDate min, final FDate max) {
        return FDates.isBetweenExclusive(this, min, max);
    }

    public boolean isBetweenExclusiveNotNullSafe(final FDate min, final FDate max) {
        return FDates.isBetweenExclusiveNotNullSafe(this, min, max);
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

    public boolean isWithoutTime() {
        return millis % FTimeUnit.MILLISECONDS_IN_DAY == 0 && picos == 0;
    }

}
