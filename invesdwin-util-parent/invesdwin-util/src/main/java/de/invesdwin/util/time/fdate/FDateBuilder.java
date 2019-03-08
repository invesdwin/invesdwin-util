package de.invesdwin.util.time.fdate;

import java.util.TimeZone;

import javax.annotation.concurrent.NotThreadSafe;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.MutableDateTime;

import de.invesdwin.util.lang.Objects;

@NotThreadSafe
public class FDateBuilder {

    private Integer years;
    private Integer months;
    private FWeekday weekday;
    private Integer days;
    private Integer hours;
    private Integer minutes;
    private Integer seconds;
    private Integer milliseconds;
    private TimeZone timeZone;

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
        this.hours = (int) weekTime.getHour();
        this.minutes = (int) weekTime.getMinute();
        return this;
    }

    public FDateBuilder withYears(final Integer years) {
        this.years = years;
        return this;
    }

    public FDateBuilder withMonths(final Integer months) {
        this.months = months;
        return this;
    }

    public FDateBuilder withFWeekday(final FWeekday weekday) {
        this.weekday = weekday;
        return this;
    }

    public FDateBuilder withWeekday(final Integer weekday) {
        if (weekday == null) {
            this.weekday = null;
        } else {
            this.weekday = FWeekday.valueOfIndex(weekday);
        }
        return this;
    }

    public FDateBuilder withDays(final Integer days) {
        this.days = days;
        return this;
    }

    public FDateBuilder withHours(final Integer hours) {
        this.hours = hours;
        return this;
    }

    public FDateBuilder withMinutes(final Integer minutes) {
        this.minutes = minutes;
        return this;
    }

    public FDateBuilder withSeconds(final Integer seconds) {
        this.seconds = seconds;
        return this;
    }

    public FDateBuilder withMilliseconds(final Integer milliseconds) {
        this.milliseconds = milliseconds;
        return this;
    }

    public FDateBuilder withTimeZone(final TimeZone timeZone) {
        this.timeZone = timeZone;
        return this;
    }

    public FDate get() {
        final int year = Objects.defaultIfNull(years, 1);
        final int monthOfYear = Objects.defaultIfNull(months, 1);
        final int dayOfMonth = Objects.defaultIfNull(days, 0);
        final int hourOfDay = Objects.defaultIfNull(hours, 0);
        final int minuteOfHour = Objects.defaultIfNull(minutes, 0);
        final int secondOfMinute = Objects.defaultIfNull(seconds, 0);
        final int millisOfSecond = Objects.defaultIfNull(milliseconds, 0);
        final DateTimeZone zone;
        if (timeZone != null) {
            zone = DateTimeZone.forTimeZone(timeZone);
        } else {
            zone = DateTimeZone.getDefault();
        }
        final DateTime dateTime = new DateTime(year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour, secondOfMinute,
                millisOfSecond, zone);
        if (weekday == null) {
            return new FDate(dateTime);
        } else {
            final MutableDateTime mutableDateTime = dateTime.toMutableDateTime();
            mutableDateTime.set(FDateField.Weekday.jodaTimeValue(), weekday.jodaTimeValue());
            return new FDate(mutableDateTime);
        }
    }

    public static FDate newDate(final Integer years) {
        return newDate(years, 1);
    }

    public static FDate newDate(final Integer years, final Integer months) {
        return newDate(years, months, 1);
    }

    public static FDate newDate(final Integer years, final Integer months, final Integer days) {
        return newDate(years, months, days, 0);
    }

    public static FDate newDate(final Integer years, final Integer months, final Integer days, final Integer hours) {
        return newDate(years, months, days, hours, 0);
    }

    public static FDate newDate(final Integer years, final Integer months, final Integer days, final Integer hours,
            final Integer minutes) {
        return newDate(years, months, days, hours, minutes, 0);
    }

    public static FDate newDate(final Integer years, final Integer months, final Integer days, final Integer hours,
            final Integer minutes, final Integer seconds) {
        return newDate(years, months, days, hours, minutes, seconds, 0);
    }

    public static FDate newDate(final Integer years, final Integer months, final Integer days, final Integer hours,
            final Integer minutes, final Integer seconds, final Integer milliseconds) {
        final FDateBuilder db = new FDateBuilder();
        db.withYears(years);
        db.withMonths(months);
        db.withDays(days);
        db.withHours(hours);
        db.withMinutes(minutes);
        db.withSeconds(seconds);
        db.withMilliseconds(milliseconds);
        return db.get();
    }

}
