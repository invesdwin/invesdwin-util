package de.invesdwin.util.time.fdate;

import java.util.TimeZone;

import javax.annotation.concurrent.NotThreadSafe;

import org.joda.time.DateTimeZone;
import org.junit.BeforeClass;
import org.junit.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.time.TimeZones;
import de.jollyday.HolidayCalendar;

@NotThreadSafe
public class FDateTest {

    @BeforeClass
    public static void beforeClass() {
        final TimeZone newTimeZone = TimeZones.getTimeZone("UTC");
        FDates.setDefaultTimeZone(newTimeZone);
        final FDate curDate = new FDate();
        final String dateStr = curDate.toString();
        TimeZone.setDefault(newTimeZone);
        //joda needs another call explicitly since it might have cached the value too early...
        DateTimeZone.setDefault(DateTimeZone.forTimeZone(newTimeZone));
        Assertions.assertThat(curDate.toString()).isEqualTo(dateStr);
    }

    @Test
    public void testConversionDate() {

        final FDate today = FDate.today();
        Assertions.assertThat(new FDate(today.dateValue())).isEqualTo(today);
        final String dateStr = org.apache.commons.lang3.time.FastDateFormat.getInstance(FDate.FORMAT_ISO_DATE_TIME)
                .format(today.dateValue());
        final String fdateStr = today.toString(FDate.FORMAT_ISO_DATE_TIME);
        Assertions.assertThat(dateStr).isEqualTo(fdateStr);
        Assertions.assertThat(dateStr).endsWith("T00:00:00");
        System.out.println(new FDate().dateValue()); //SUPPRESS CHECKSTYLE single line
    }

    @Test
    public void testConversionCalendar() {
        final FDate today = FDate.today();
        Assertions.assertThat(new FDate(today.calendarValue())).isEqualTo(today);
        final String dateStr = org.apache.commons.lang3.time.FastDateFormat.getInstance(FDate.FORMAT_ISO_DATE_TIME)
                .format(today.calendarValue());
        final String fdateStr = today.toString(FDate.FORMAT_ISO_DATE_TIME);
        Assertions.assertThat(dateStr).isEqualTo(fdateStr);
        Assertions.assertThat(dateStr).endsWith("T00:00:00");
    }

    @Test
    public void testIsSameWeek() {
        final FDate wednesday = FDateBuilder.newDate(2015, 8, 12);
        Assertions.assertThat(wednesday.getFWeekday()).isEqualTo(FWeekday.Wednesday);

        final FDate monday = FDateBuilder.newDate(2015, 8, 10);
        Assertions.assertThat(monday.getFWeekday()).isEqualTo(FWeekday.Monday);
        Assertions.assertThat(FDates.isSameWeek(wednesday, monday)).isTrue();
        Assertions.assertThat(FDates.isSameWeek(wednesday, monday.addMilliseconds(-1))).isFalse();

        final FDate sunday = FDateBuilder.newDate(2015, 8, 16).addDays(1).addMilliseconds(-1);
        Assertions.assertThat(sunday.getFWeekday()).isEqualTo(FWeekday.Sunday);
        Assertions.assertThat(FDates.isSameWeek(wednesday, sunday)).isTrue();
        Assertions.assertThat(FDates.isSameWeek(wednesday, sunday.addMilliseconds(1))).isFalse();
    }

    @Test
    public void testIterable() {
        int iterations = 0;
        for (final FDate date : FDates.iterable(new FDate(), new FDate(), FTimeUnit.DAYS, 1)) {
            System.out.println(String.format("%s", date)); //SUPPRESS CHECKSTYLE single line
            iterations++;
        }
        Assertions.assertThat(iterations).isEqualTo(1);
    }

    @Test
    public void testIterableReverse() {
        int iterations = 0;
        for (final FDate date : FDates.iterable(new FDate(), new FDate(), FTimeUnit.DAYS, -1)) {
            System.out.println(String.format("%s", date)); //SUPPRESS CHECKSTYLE single line
            iterations++;
        }
        Assertions.assertThat(iterations).isEqualTo(1);
    }

    @Test
    public void testIterableYearly() {
        int iterations = 0;
        final FDate endDate = FDateBuilder.newDate(2012, 1, 27);
        final FDate startDate = FDateBuilder.newDate(1990, 1, 1);
        FDate lastDate = null;
        for (final FDate date : FDates.iterable(startDate, endDate, FTimeUnit.YEARS, 1)) {
            if (lastDate == null) {
                Assertions.assertThat(FDates.isSameMillisecond(startDate, date)).isTrue();
            }
            lastDate = date;
            System.out.println(String.format("%s", date)); //SUPPRESS CHECKSTYLE single line
            iterations++;
        }
        Assertions.assertThat(iterations).isEqualTo(24);
        Assertions.assertThat(FDates.isSameMillisecond(endDate, lastDate)).isTrue();
    }

    @Test
    public void testIterableYearlyReverse() {
        int iterations = 0;
        final FDate endDate = FDateBuilder.newDate(2012, 1, 27);
        final FDate startDate = FDateBuilder.newDate(1990, 1, 1);
        FDate lastDate = null;
        for (final FDate date : FDates.iterable(endDate, startDate, FTimeUnit.YEARS, -1)) {
            if (lastDate == null) {
                Assertions.assertThat(FDates.isSameMillisecond(endDate, date)).isTrue();
            }
            lastDate = date;
            System.out.println(String.format("%s", date)); //SUPPRESS CHECKSTYLE single line
            iterations++;
        }
        Assertions.assertThat(iterations).isEqualTo(24);
        Assertions.assertThat(FDates.isSameMillisecond(startDate, lastDate)).isTrue();
    }

    @Test
    public void testFirstWeekdayOfMonthFromSundayFirst() {
        final FDate saturdayLast = FDateBuilder.newDate(2016, 5, 1);
        Assertions.assertThat(saturdayLast.getFWeekday()).isEqualTo(FWeekday.Sunday);
        final FDate mondayInMay = saturdayLast.getFirstWeekdayOfMonth(FWeekday.Monday);
        Assertions.assertThat(mondayInMay).isEqualTo(FDateBuilder.newDate(2016, 5, 2));
    }

    @Test
    public void testFirstWeekdayOfMonthFromSaturdayLast() {
        final FDate sundayFirst = FDateBuilder.newDate(2016, 4, 30);
        Assertions.assertThat(sundayFirst.getFWeekday()).isEqualTo(FWeekday.Saturday);
        final FDate mondayInMay = sundayFirst.getFirstWeekdayOfMonth(FWeekday.Monday);
        Assertions.assertThat(mondayInMay).isEqualTo(FDateBuilder.newDate(2016, 4, 4));
    }

    @Test
    public void testAddWorkdays() {
        final FDate saturday = FDateBuilder.newDate(2016, 4, 30);
        Assertions.assertThat(saturday.addWorkdays(2, null)).isEqualTo(FDateBuilder.newDate(2016, 5, 3));
        Assertions.assertThat(saturday.addWorkdays(2, HolidayCalendar.GERMANY))
                .isEqualTo(FDateBuilder.newDate(2016, 5, 3));
        Assertions.assertThat(saturday.addWorkdays(-2, null)).isEqualTo(FDateBuilder.newDate(2016, 4, 28));
        Assertions.assertThat(saturday.addWorkdays(-2, HolidayCalendar.GERMANY))
                .isEqualTo(FDateBuilder.newDate(2016, 4, 28));

        final FDate wednesday = FDateBuilder.newDate(2016, 5, 4);
        Assertions.assertThat(wednesday.addWorkdays(5, null)).isEqualTo(FDateBuilder.newDate(2016, 5, 11));
        Assertions.assertThat(wednesday.addWorkdays(5, HolidayCalendar.GERMANY))
                .isEqualTo(FDateBuilder.newDate(2016, 5, 12));
    }

    @Test
    public void testIterateDays() {
        final FDate fromDate = FDateBuilder.newDate(2000, 1, 1);
        final FDate toDate = fromDate.addDays(1).addMilliseconds(-1);
        final ICloseableIterator<FDate> iterator = FDates.iterable(fromDate, toDate, FTimeUnit.DAYS, 1).iterator();
        final FDate next = iterator.next();
        Assertions.assertThat(next).isEqualTo(fromDate);
        final FDate nextNext = iterator.next();
        Assertions.assertThat(nextNext).isEqualTo(toDate);
        Assertions.assertThat(iterator.hasNext()).isFalse();
    }

}
