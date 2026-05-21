package de.invesdwin.util.time.date;

import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.TimeZone;

import javax.annotation.concurrent.NotThreadSafe;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.time.date.holiday.HolidayManagers;
import de.invesdwin.util.time.date.millis.WeekAdjustment;
import de.invesdwin.util.time.date.timezone.FTimeZone;
import de.invesdwin.util.time.date.timezone.TimeZones;
import de.invesdwin.util.time.range.week.FWeekTime;

@NotThreadSafe
public class FDateTest {

    @BeforeAll
    public static void beforeClass() {
        final TimeZone newTimeZone = TimeZones.getTimeZone("UTC");
        FDates.setDefaultTimeZone(new FTimeZone(newTimeZone));
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
        final FDate time = new FDate();
        for (final FDate date : FDates.iterable(time, time, FTimeUnit.DAYS, 1)) {
            System.out.println(String.format("%s", date)); //SUPPRESS CHECKSTYLE single line
            iterations++;
        }
        Assertions.assertThat(iterations).isEqualTo(1);
    }

    @Test
    public void testIterableReverse() {
        int iterations = 0;
        final FDate time = new FDate();
        for (final FDate date : FDates.iterable(time, time, FTimeUnit.DAYS, -1)) {
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
        Assertions.assertThat(saturday.addWorkdays(2, HolidayManagers.GERMANY))
                .isEqualTo(FDateBuilder.newDate(2016, 5, 3));
        Assertions.assertThat(saturday.addWorkdays(-2, null)).isEqualTo(FDateBuilder.newDate(2016, 4, 28));
        Assertions.assertThat(saturday.addWorkdays(-2, HolidayManagers.GERMANY))
                .isEqualTo(FDateBuilder.newDate(2016, 4, 28));

        final FDate wednesday = FDateBuilder.newDate(2016, 5, 4);
        Assertions.assertThat(wednesday.addWorkdays(5, null)).isEqualTo(FDateBuilder.newDate(2016, 5, 11));
        Assertions.assertThat(wednesday.addWorkdays(5, HolidayManagers.GERMANY))
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

    @Test
    public void testSetWeekday() {
        final FDate date = FDateBuilder.newDate(2017, 1, 1);
        final FDate monday = date.setFWeekday(FWeekday.Monday, WeekAdjustment.PREVIOUS);
        Assertions.assertThat(monday).isEqualTo(FDateBuilder.newDate(2016, 12, 26));

        final FDate sunday = date.setFWeekday(FWeekday.Sunday, WeekAdjustment.PREVIOUS);
        Assertions.assertThat(sunday).isEqualTo(FDateBuilder.newDate(2017, 1, 1));
    }

    @Test
    public void testSetWeekdayStays() {
        final FDate date = FDateBuilder.newDate(2017, 1, 2);
        final FDate monday = date.setFWeekday(FWeekday.Monday, WeekAdjustment.PREVIOUS);
        Assertions.assertThat(monday).isEqualTo(FDateBuilder.newDate(2017, 1, 2));

        final FDate sunday = date.setFWeekday(FWeekday.Sunday, WeekAdjustment.PREVIOUS);
        Assertions.assertThat(sunday).isEqualTo(FDateBuilder.newDate(2017, 1, 1));
    }

    @Test
    public void testSetFWeekTime() {
        final FDate date = FDateBuilder.newDate(2017, 1, 1);
        final FDate monday = date.setFWeekTime(new FWeekTime(FWeekday.Monday, 1, 1, 0, 0, 0, 0, 0),
                WeekAdjustment.PREVIOUS);
        Assertions.assertThat(monday).isEqualTo(FDateBuilder.newDate(2016, 12, 26, 1, 1));

        final FDate sunday = date.setFWeekTime(new FWeekTime(FWeekday.Sunday, 1, 1, 0, 0, 0, 0, 0),
                WeekAdjustment.PREVIOUS);
        Assertions.assertThat(sunday).isEqualTo(FDateBuilder.newDate(2017, 1, 1, 1, 1));
    }

    @Test
    public void testSetFWeekTimeStays() {
        final FDate date = FDateBuilder.newDate(2017, 1, 2);
        final FDate monday = date.setFWeekTime(new FWeekTime(FWeekday.Monday, 1, 1, 0, 0, 0, 0, 0),
                WeekAdjustment.PREVIOUS);
        Assertions.assertThat(monday).isEqualTo(FDateBuilder.newDate(2017, 1, 2, 1, 1));

        final FDate sunday = date.setFWeekTime(new FWeekTime(FWeekday.Sunday, 1, 1, 0, 0, 0, 0, 0),
                WeekAdjustment.PREVIOUS);
        Assertions.assertThat(sunday).isEqualTo(FDateBuilder.newDate(2017, 1, 1, 1, 1));
    }

    @Test
    public void testTruncate() {
        final FDate reference = FDateBuilder.newDate(2001, 12, 31, 23, 59, 59, 999);
        Assertions.assertThat(reference.truncate(FDateField.Year))
                .isEqualTo(FDateBuilder.newDate(2001, 1, 1, 0, 0, 0, 0));
    }

    @Test
    public void testWeeknumberOfMonth() {
        Assertions.assertThat(FDateBuilder.newDate(1998, 4, 7).getWeekNumberOfMonth()).isEqualTo(2);
        Assertions.assertThat(DateTimeFormatter.ofPattern("W").format(FDateBuilder.newDate(1998, 4, 7).javaTimeValue()))
                .isEqualTo("2");

        final FDate date = FDateBuilder.newDate(2020, 10, 1);
        Assertions.assertThat(date.getWeekNumberOfMonth()).isEqualTo(1);
        Assertions.assertThat(date.addWeeks(1).getWeekNumberOfMonth()).isEqualTo(2);
        Assertions.assertThat(date.addDays(-1).getWeekNumberOfMonth()).isEqualTo(5);

        Assertions.assertThat(DateTimeFormatter.ofPattern("W").format(date.javaTimeValue())).isEqualTo("1");
        Assertions.assertThat(DateTimeFormatter.ofPattern("W").format(date.addWeeks(1).javaTimeValue())).isEqualTo("2");
        Assertions.assertThat(DateTimeFormatter.ofPattern("W").format(date.addDays(-1).javaTimeValue())).isEqualTo("5");
    }

    @Test
    public void testWekkNumberOfMonth199804() {
        //        April 1998 Nr.   Mo  Di  Mi  Do  Fr  Sa  So
        //                   14             1   2   3   4   5
        //                   15     6   7   8   9   10  11  12
        //                   16     13  14  15  16  17  18  19
        //                   17     20  21  22  23  24  25  26
        //                   18     27  28  29  30
        for (int i = 1; i <= 5; i++) {
            testWeekNumberOfMonth(1998, 4, i, 1, 1);
        }
        for (int i = 6; i <= 12; i++) {
            testWeekNumberOfMonth(1998, 4, i, 2, 2);
        }
        for (int i = 13; i <= 19; i++) {
            testWeekNumberOfMonth(1998, 4, i, 3, 3);
        }
        for (int i = 20; i <= 16; i++) {
            testWeekNumberOfMonth(1998, 4, i, 4, 4);
        }
        for (int i = 27; i <= 30; i++) {
            testWeekNumberOfMonth(1998, 4, i, 5, 5);
        }
    }

    @Test
    public void testWeekOfMonth202406() {
        //        Juni 2024 Nr.    Mo  Di  Mi  Do  Fr  Sa  So
        //                  22                         1   2
        //                  23     3   4   5   6   7   8   9
        //                  24     10  11  12  13  14  15  16
        //                  25     17  18  19  20  21  22  23
        //                  26     24  25  26  27  28  29  30
        for (int i = 1; i <= 2; i++) {
            testWeekNumberOfMonth(2024, 6, i, 1, 0);
        }
        for (int i = 3; i <= 9; i++) {
            testWeekNumberOfMonth(2024, 6, i, 2, 1);
        }
        for (int i = 10; i <= 16; i++) {
            testWeekNumberOfMonth(2024, 6, i, 3, 2);
        }
        for (int i = 17; i <= 23; i++) {
            testWeekNumberOfMonth(2024, 6, i, 4, 3);
        }
        for (int i = 26; i <= 30; i++) {
            testWeekNumberOfMonth(2024, 6, i, 5, 4);
        }
    }

    private void testWeekNumberOfMonth(final int year, final int month, final int day, final int expectedJoda,
            final int expectedCalendar) {
        final FDate date = FDateBuilder.newDate(year, month, day);
        Assertions.assertThat(date.calendarValue().get(Calendar.WEEK_OF_MONTH)).isEqualTo(expectedCalendar);
        Assertions.assertThat(getWeekOfMonthJoda(date.jodaTimeValueZoned())).isEqualTo(expectedJoda);
        Assertions.assertThat(date.getWeekNumberOfMonth()).as("%s", date).isEqualTo(expectedJoda);
    }

    private int getWeekOfMonthJoda(final DateTime date) {
        final DateTime.Property dayOfWeeks = date.dayOfWeek();
        return (int) (Doubles.ceil((date.dayOfMonth().get() - dayOfWeeks.get()) / 7.0)) + 1;
    }

    @Test
    public void testParseZoneIdZZ() {
        final String format = "yyyy-MM-dd'T'HH:mm:ss.SSS.UUU.NNN.PPP ZZ";
        final FDate time = new FDate();
        final String joda = time.toString(format);
        final String java = FDate.valueOf(time.toString(format, (FTimeZone) null), (FTimeZone) null, null, format)
                .toString(format);
        Assertions.assertThat(java).isEqualTo(joda);
    }

    @Test
    public void testParseZoneId() {
        final FDate time = new FDate();
        final String joda = time.toString();
        final String java = FDate
                .valueOf(time.toString(FDate.FORMAT_GERMAN_DATE_TIME_MS, (FTimeZone) null), (FTimeZone) null, null,
                        FDate.FORMAT_GERMAN_DATE_TIME_MS)
                .toString();
        Assertions.assertThat(java).isEqualTo(joda);
    }

    @Test
    public void testTimeZoneSwitching() {
        //CHECKSTYLE:OFF
        final FTimeZone zoneBerlin = new FTimeZone(TimeZone.getTimeZone("Europe/Berlin"));
        final FTimeZone zoneUtc = new FTimeZone(TimeZone.getTimeZone("UTC"));
        //CHECKSTYLE:ON

        // 1. Snapshot an absolute instant (e.g., current time)
        final FDate curDate = new FDate();

        // 2. Get the string representations explicitly for both zones
        final String berlinStr = curDate.toString(FDate.FORMAT_ISO_DATE_TIME_PS, zoneBerlin);
        final String utcStr = curDate.toString(FDate.FORMAT_ISO_DATE_TIME_PS, zoneUtc);

        // 3. Parse the Berlin string specifying the Berlin zone
        final FDate parsedFromBerlin = FDate.valueOf(berlinStr, zoneBerlin, FDate.FORMAT_ISO_DATE_TIME_PS);

        // 4. Parse the UTC string specifying the UTC zone
        final FDate parsedFromUtc = FDate.valueOf(utcStr, zoneUtc, FDate.FORMAT_ISO_DATE_TIME_PS);

        // Under the Absolute UTC Paradigm, both objects must hold the exact same internal universal millis!
        Assertions.assertThat(parsedFromBerlin.millisValue()).isEqualTo(parsedFromUtc.millisValue());

        // 5. Cross-printing assertions should match perfectly without environmental drift
        final String berlinAsUtcStr = parsedFromBerlin.toString(FDate.FORMAT_ISO_DATE_TIME_PS, zoneUtc);
        Assertions.assertThat(berlinAsUtcStr).isEqualTo(utcStr);
        final String utcAsBerlinStr = parsedFromUtc.toString(FDate.FORMAT_ISO_DATE_TIME_PS, zoneBerlin);
        Assertions.assertThat(utcAsBerlinStr).isEqualTo(berlinStr);

        /*
         * We can also shift the internal millis to make calculations in different time zones easier, but that should be
         * an explicit operation with an explicit result, not something that happens implicitly in the background when
         * printing or parsing
         */
        final long berlinOffsetMillis = parsedFromBerlin.getTimeZoneOffsetMilliseconds(zoneBerlin);
        final FDate parsedFromBerlinApplyFixed = parsedFromBerlin.applyTimeZoneOffset(berlinOffsetMillis);
        final String parsedFromBerlinApplyFixedStr = parsedFromBerlinApplyFixed.toString();
        Assertions.assertThat(parsedFromBerlinApplyFixedStr).isEqualTo(berlinStr);
        Assertions.assertThat(parsedFromBerlinApplyFixed.millisValue()).isNotEqualTo(parsedFromBerlin.millisValue());
        Assertions.assertThat(parsedFromBerlinApplyFixed.millisValue()).isNotEqualTo(curDate.millisValue());
        final FDate parsedFromBerlinRevertFixed = parsedFromBerlinApplyFixed.revertTimeZoneOffset(berlinOffsetMillis);
        final String parsedFromBerlinRevertFixedStr = parsedFromBerlinRevertFixed.toString();
        Assertions.assertThat(parsedFromBerlinRevertFixedStr).isEqualTo(utcStr);
        Assertions.assertThat(parsedFromBerlinRevertFixed.millisValue()).isEqualTo(parsedFromBerlin.millisValue());
        Assertions.assertThat(parsedFromBerlinRevertFixed.millisValue()).isEqualTo(curDate.millisValue());

        final FDate parsedFromBerlinApply = parsedFromBerlin.applyTimeZoneOffset(zoneBerlin);
        final String parsedFromBerlinApplyStr = parsedFromBerlinApply.toString();
        Assertions.assertThat(parsedFromBerlinApplyStr).isEqualTo(berlinStr);
        Assertions.assertThat(parsedFromBerlinApply.millisValue()).isNotEqualTo(parsedFromBerlin.millisValue());
        Assertions.assertThat(parsedFromBerlinApply.millisValue()).isNotEqualTo(curDate.millisValue());
        @SuppressWarnings("deprecation")
        //risky on dailight saving time changes, but should work in general since we are using the same date for parsing and applying the offset
        final FDate parsedFromBerlinRevert = parsedFromBerlinApply.revertTimeZoneOffset(zoneBerlin);
        final String parsedFromBerlinRevertStr = parsedFromBerlinRevert.toString();
        Assertions.assertThat(parsedFromBerlinRevertStr).isEqualTo(utcStr);
        Assertions.assertThat(parsedFromBerlinRevert.millisValue()).isEqualTo(parsedFromBerlin.millisValue());
        Assertions.assertThat(parsedFromBerlinRevert.millisValue()).isEqualTo(curDate.millisValue());
    }

}
