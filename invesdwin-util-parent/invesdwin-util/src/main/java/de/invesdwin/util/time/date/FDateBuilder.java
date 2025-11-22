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

    public FDateBuilder setMonths(final int months) {
        this.months = months;
        return this;
    }

    public FDateBuilder setFWeekday(final FWeekday weekday) {
        this.weekday = weekday;
        return this;
    }

    public FDateBuilder setWeekday(final int weekday) {
        this.weekday = FWeekday.valueOfIndex(weekday);
        return this;
    }

    public FDateBuilder setDays(final int days) {
        this.days = days;
        return this;
    }

    public FDateBuilder setHours(final int hours) {
        this.hours = hours;
        return this;
    }

    public FDateBuilder setMinutes(final int minutes) {
        this.minutes = minutes;
        return this;
    }

    public FDateBuilder setSeconds(final int seconds) {
        this.seconds = seconds;
        return this;
    }

    public FDateBuilder setMilliseconds(final int milliseconds) {
        this.milliseconds = milliseconds;
        return this;
    }

    public FDateBuilder setTimeZone(final FTimeZone timeZone) {
        this.timeZone = timeZone;
        return this;
    }

    public FDate getDate() {
        return new FDate(getMillis());
    }

    public long getMillis() {
        long dateTime = newMillis(years, months, days, hours, minutes, seconds, milliseconds, timeZone);
        if (weekday != null) {
            dateTime = timeZone.getDateTimeFieldWeekday().set(dateTime, weekday.jodaTimeValue());
        }
        return dateTime;
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
        return new FDate(newMillis(years, months, days, hours, minutes, seconds, milliseconds, timeZone));
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

}
