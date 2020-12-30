package de.invesdwin.util.time.fdate;

import java.util.TimeZone;

import javax.annotation.concurrent.NotThreadSafe;

import org.joda.time.Chronology;
import org.joda.time.DateTimeZone;
import org.joda.time.chrono.ISOChronology;

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
    private Chronology timeZone = FDates.getDefaultChronology();

    public void reset() {
        years = 1;
        months = 1;
        weekday = null;
        days = 0;
        hours = 0;
        minutes = 0;
        seconds = 0;
        milliseconds = 0;
        timeZone = FDates.getDefaultChronology();
    }

    public FDateBuilder withDate(final FDate date) {
        this.years = date.getYear();
        this.months = date.getMonth();
        this.days = date.getDay();
        return this;
    }

    public FDateBuilder withTime(final FDate time) {
        this.hours = time.getHour();
        this.minutes = time.getMinute();
        this.seconds = time.getSecond();
        this.milliseconds = time.getMillisecond();
        return this;
    }

    public FDateBuilder withWeekTime(final FWeekTime weekTime) {
        this.weekday = weekTime.getFWeekday();
        this.hours = weekTime.getHour();
        this.minutes = weekTime.getMinute();
        return this;
    }

    public FDateBuilder withYears(final int years) {
        this.years = years;
        return this;
    }

    public FDateBuilder withMonths(final int months) {
        this.months = months;
        return this;
    }

    public FDateBuilder withFWeekday(final FWeekday weekday) {
        this.weekday = weekday;
        return this;
    }

    public FDateBuilder withWeekday(final int weekday) {
        this.weekday = FWeekday.valueOfIndex(weekday);
        return this;
    }

    public FDateBuilder withDays(final int days) {
        this.days = days;
        return this;
    }

    public FDateBuilder withHours(final int hours) {
        this.hours = hours;
        return this;
    }

    public FDateBuilder withMinutes(final int minutes) {
        this.minutes = minutes;
        return this;
    }

    public FDateBuilder withSeconds(final int seconds) {
        this.seconds = seconds;
        return this;
    }

    public FDateBuilder withMilliseconds(final int milliseconds) {
        this.milliseconds = milliseconds;
        return this;
    }

    public FDateBuilder withTimeZone(final TimeZone timeZone) {
        this.timeZone = ISOChronology.getInstance(DateTimeZone.forTimeZone(timeZone));
        return this;
    }

    public FDateBuilder withTimeZone(final Chronology timeZone) {
        this.timeZone = timeZone;
        return this;
    }

    public FDate getDate() {
        return new FDate(getMillis());
    }

    public long getMillis() {
        final long dateTime = timeZone.getDateTimeMillis(years, months, days, hours, minutes, seconds, milliseconds);
        if (weekday == null) {
            return dateTime;
        } else {
            final long dateTimeWeekday = FDateField.Weekday.jodaTimeValue()
                    .getField(timeZone)
                    .set(dateTime, weekday.jodaTimeValue());
            return dateTimeWeekday;
        }
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
        return newDate(years, months, days, hours, minutes, seconds, milliseconds, FDates.getDefaultChronology());
    }

    //CHECKSTYLE:OFF
    public static FDate newDate(final int years, final int months, final int days, final int hours, final int minutes,
            final int seconds, final int milliseconds, final TimeZone timeZone) {
        //CHECKSTYLE:ON
        final ISOChronology chronology = ISOChronology.getInstance(DateTimeZone.forTimeZone(timeZone));
        return newDate(years, months, days, hours, minutes, seconds, milliseconds, chronology);
    }

    //CHECKSTYLE:OFF
    public static FDate newDate(final int years, final int months, final int days, final int hours, final int minutes,
            final int seconds, final int milliseconds, final Chronology timeZone) {
        //CHECKSTYLE:ON
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
        return newMillis(years, months, days, hours, minutes, seconds, milliseconds, FDates.getDefaultChronology());
    }

    //CHECKSTYLE:OFF
    public static long newMillis(final int years, final int months, final int days, final int hours, final int minutes,
            final int seconds, final int milliseconds, final TimeZone timeZone) {
        //CHECKSTYLE:ON
        final ISOChronology chronology = ISOChronology.getInstance(DateTimeZone.forTimeZone(timeZone));
        return newMillis(years, months, days, hours, minutes, seconds, milliseconds, chronology);
    }

    //CHECKSTYLE:OFF
    public static long newMillis(final int years, final int months, final int days, final int hours, final int minutes,
            final int seconds, final int milliseconds, final Chronology timeZone) {
        //CHECKSTYLE:ON
        final long dateTime = timeZone.getDateTimeMillis(years, months, days, hours, minutes, seconds, milliseconds);
        return dateTime;
    }

}
