package de.invesdwin.util.time.date;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.time.date.timezone.FTimeZone;
import de.invesdwin.util.time.range.week.FWeekTime;

@NotThreadSafe
public class FDateBuilder {

    private int years = 1;
    private int months = 1;
    private FWeekday weekday;
    private int days = 0;
    private int hours = 0;
    private int minutes = 0;
    private int seconds = 0;
    private int milliseconds = 0;
    private int microseconds = 0;
    private int nanoseconds = 0;
    private int picoseconds = 0;
    private FTimeZone timeZone = FDates.getDefaultTimeZone();

    public void reset() {
        years = 1;
        months = 1;
        weekday = null;
        days = 0;
        hours = 0;
        minutes = 0;
        seconds = 0;
        milliseconds = 0;
        microseconds = 0;
        nanoseconds = 0;
        picoseconds = 0;
        timeZone = FDates.getDefaultTimeZone();
    }

    public FDateBuilder setDate(final FDate date) {
        this.years = date.getYear();
        this.months = date.getMonth();
        this.days = date.getDay();
        return this;
    }

    public FDateBuilder setTime(final FDate time) {
        this.hours = time.getHour();
        this.minutes = time.getMinute();
        this.seconds = time.getSecond();
        this.milliseconds = time.getMillisecond();
        this.microseconds = time.getMicrosecond();
        this.nanoseconds = time.getNanosecond();
        this.picoseconds = time.getPicosecond();
        return this;
    }

    public FDateBuilder setWeekTime(final FWeekTime weekTime) {
        this.weekday = weekTime.getFWeekday();
        this.hours = weekTime.getHour();
        this.minutes = weekTime.getMinute();
        return this;
    }

    public FDateBuilder setYears(final int years) {
        this.years = years;
        return this;
    }

    public int getYears() {
        return years;
    }

    public FDateBuilder setMonths(final int months) {
        this.months = months;
        return this;
    }

    public int getMonths() {
        return months;
    }

    public FDateBuilder setFWeekday(final FWeekday weekday) {
        this.weekday = weekday;
        return this;
    }

    public FWeekday getFWeekday() {
        return weekday;
    }

    public FDateBuilder setWeekday(final int weekday) {
        this.weekday = FWeekday.valueOfIndex(weekday);
        return this;
    }

    public int getWeekday() {
        return weekday.indexValue();
    }

    public FDateBuilder setDays(final int days) {
        this.days = days;
        return this;
    }

    public int getDays() {
        return days;
    }

    public FDateBuilder setHours(final int hours) {
        this.hours = hours;
        return this;
    }

    public int getHours() {
        return hours;
    }

    public FDateBuilder setMinutes(final int minutes) {
        this.minutes = minutes;
        return this;
    }

    public int getMinutes() {
        return minutes;
    }

    public FDateBuilder setSeconds(final int seconds) {
        this.seconds = seconds;
        return this;
    }

    public int getSeconds() {
        return seconds;
    }

    public FDateBuilder setMilliseconds(final int milliseconds) {
        this.milliseconds = milliseconds;
        return this;
    }

    public int getMilliseconds() {
        return milliseconds;
    }

    public FDateBuilder setMicroseconds(final int microseconds) {
        this.microseconds = microseconds;
        return this;
    }

    public int getMicroseconds() {
        return microseconds;
    }

    public FDateBuilder setNanoseconds(final int nanoseconds) {
        this.nanoseconds = nanoseconds;
        return this;
    }

    public int getNanoseconds() {
        return nanoseconds;
    }

    public FDateBuilder setTimeZone(final FTimeZone timeZone) {
        this.timeZone = timeZone;
        return this;
    }

    public FTimeZone getTimeZone() {
        return timeZone;
    }

    public FDate getDate() {
        return new FDate(getMillis(), getPicos());
    }

    public long getMillis() {
        long dateTime = newMillis(years, months, days, hours, minutes, seconds, milliseconds, timeZone);
        if (weekday != null) {
            dateTime = timeZone.getDateTimeFieldWeekday().set(dateTime, weekday.jodaTimeValue());
        }
        return dateTime;
    }

    public int getPicos() {
        return newPicos(microseconds, nanoseconds, picoseconds);
    }

    public static FDate newDate(final int years) {
        return newDate(years, 1);
    }

    public static FDate newDate(final int years, final int months) {
        return newDate(years, months, 1);
    }

    public static FDate newDate(final int years, final int months, final int days) {
        return newDate(years, months, days, 0);
    }

    public static FDate newDate(final int years, final int months, final int days, final int hours) {
        return newDate(years, months, days, hours, 0);
    }

    public static FDate newDate(final int years, final int months, final int days, final int hours, final int minutes) {
        return newDate(years, months, days, hours, minutes, 0);
    }

    public static FDate newDate(final int years, final int months, final int days, final int hours, final int minutes,
            final int seconds) {
        return newDate(years, months, days, hours, minutes, seconds, 0);
    }

    public static FDate newDate(final int years, final int months, final int days, final int hours, final int minutes,
            final int seconds, final int milliseconds) {
        return newDate(years, months, days, hours, minutes, seconds, milliseconds, FDates.getDefaultTimeZone());
    }

    public static FDate newDate(final int years, final int months, final int days, final int hours, final int minutes,
            final int seconds, final int milliseconds, final FTimeZone timeZone) {
        return new FDate(newMillis(years, months, days, hours, minutes, seconds, milliseconds, timeZone), 0);
    }

    public static FDate newDate(final int years, final int months, final int days, final int hours, final int minutes,
            final int seconds, final int milliseconds, final int microseconds) {
        return newDate(years, months, days, hours, minutes, seconds, milliseconds, microseconds, 0);
    }

    public static FDate newDate(final int years, final int months, final int days, final int hours, final int minutes,
            final int seconds, final int milliseconds, final int microseconds, final int nanoseconds) {
        return newDate(years, months, days, hours, minutes, seconds, milliseconds, microseconds, nanoseconds, 0);
    }

    public static FDate newDate(final int years, final int months, final int days, final int hours, final int minutes,
            final int seconds, final int milliseconds, final int microseconds, final int nanoseconds,
            final int picoseconds) {
        return newDate(years, months, days, hours, minutes, seconds, milliseconds, microseconds, nanoseconds,
                picoseconds, FDates.getDefaultTimeZone());
    }

    public static FDate newDate(final int years, final int months, final int days, final int hours, final int minutes,
            final int seconds, final int milliseconds, final int microseconds, final int nanoseconds,
            final int picoseconds, final FTimeZone timeZone) {
        return new FDate(newMillis(years, months, days, hours, minutes, seconds, milliseconds, timeZone),
                newPicos(microseconds, nanoseconds, picoseconds));
    }

    public static long newMillis(final int years) {
        return newMillis(years, 1);
    }

    public static long newMillis(final int years, final int months) {
        return newMillis(years, months, 1);
    }

    public static long newMillis(final int years, final int months, final int days) {
        return newMillis(years, months, days, 0);
    }

    public static long newMillis(final int years, final int months, final int days, final int hours) {
        return newMillis(years, months, days, hours, 0);
    }

    public static long newMillis(final int years, final int months, final int days, final int hours,
            final int minutes) {
        return newMillis(years, months, days, hours, minutes, 0);
    }

    public static long newMillis(final int years, final int months, final int days, final int hours, final int minutes,
            final int seconds) {
        return newMillis(years, months, days, hours, minutes, seconds, 0);
    }

    public static long newMillis(final int years, final int months, final int days, final int hours, final int minutes,
            final int seconds, final int milliseconds) {
        return newMillis(years, months, days, hours, minutes, seconds, milliseconds, FDates.getDefaultTimeZone());
    }

    public static long newMillis(final int years, final int months, final int days, final int hours, final int minutes,
            final int seconds, final int milliseconds, final FTimeZone timeZone) {
        final long dateTime = timeZone.getChronology()
                .getDateTimeMillis(years, months, days, hours, minutes, seconds, milliseconds);
        return dateTime;
    }

    public static int newPicos(final int microseconds) {
        assertValidField("microseconds", microseconds, FTimeUnit.MICROSECONDS_IN_MILLISECOND);
        return microseconds * FTimeUnit.PICOSECONDS_IN_MICROSECOND;
    }

    public static int newPicos(final int microseconds, final int nanoseconds) {
        assertValidField("microseconds", microseconds, FTimeUnit.MICROSECONDS_IN_MILLISECOND);
        assertValidField("nanoseconds", nanoseconds, FTimeUnit.NANOSECONDS_IN_MICROSECOND);
        return microseconds * FTimeUnit.PICOSECONDS_IN_MICROSECOND + nanoseconds * FTimeUnit.PICOSECONDS_IN_NANOSECOND;
    }

    public static int newPicos(final int microseconds, final int nanoseconds, final int picoseconds) {
        assertValidField("microseconds", microseconds, FTimeUnit.MICROSECONDS_IN_MILLISECOND);
        assertValidField("nanoseconds", nanoseconds, FTimeUnit.NANOSECONDS_IN_MICROSECOND);
        assertValidField("picoseconds", picoseconds, FTimeUnit.PICOSECONDS_IN_NANOSECOND);
        return microseconds * FTimeUnit.PICOSECONDS_IN_MICROSECOND + nanoseconds * FTimeUnit.PICOSECONDS_IN_NANOSECOND
                + picoseconds;
    }

    private static void assertValidField(final String field, final int value, final int max) {
        if (value < 0 || value >= max) {
            throw new IllegalArgumentException(field + " must be between 0 and " + (max - 1) + ": " + value);
        }
    }

}
