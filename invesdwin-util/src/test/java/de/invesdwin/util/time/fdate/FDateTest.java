package de.invesdwin.util.time.fdate;

import java.util.TimeZone;

import javax.annotation.concurrent.NotThreadSafe;

import org.joda.time.DateTimeZone;
import org.junit.BeforeClass;
import org.junit.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.time.TimeZones;

@NotThreadSafe
public class FDateTest {

    @BeforeClass
    public static void beforeClass() {
        final TimeZone newTimeZone = TimeZones.getTimeZone("UTC");
        TimeZone.setDefault(newTimeZone);
        //joda needs another call explicitly since it might have cached the value too early...
        DateTimeZone.setDefault(DateTimeZone.forTimeZone(newTimeZone));
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
        Assertions.assertThat(FDate.isSameWeek(wednesday, monday)).isTrue();
        Assertions.assertThat(FDate.isSameWeek(wednesday, monday.addMilliseconds(-1))).isFalse();

        final FDate sunday = FDateBuilder.newDate(2015, 8, 16).addDays(1).addMilliseconds(-1);
        Assertions.assertThat(sunday.getFWeekday()).isEqualTo(FWeekday.Sunday);
        Assertions.assertThat(FDate.isSameWeek(wednesday, sunday)).isTrue();
        Assertions.assertThat(FDate.isSameWeek(wednesday, sunday.addMilliseconds(1))).isFalse();
    }

    @Test
    public void testIterable() {
        int iterationen = 0;
        for (final FDate date : FDate.iterable(new FDate(), new FDate(), FTimeUnit.Days, 1)) {
            System.out.println(String.format("%s", date)); //SUPPRESS CHECKSTYLE single line
            iterationen++;
        }
        Assertions.assertThat(iterationen).isEqualTo(1);
    }

    @Test
    public void testIterableYearly() {
        int iterations = 0;
        final FDate endDate = FDateBuilder.newDate(2012, 1, 27);
        final FDate startDate = FDateBuilder.newDate(1990, 1, 1);
        FDate lastDate = null;
        for (final FDate date : FDate.iterable(startDate, endDate, FTimeUnit.Years, 1)) {
            if (lastDate == null) {
                Assertions.assertThat(FDate.isSameMillisecond(startDate, date)).isTrue();
            }
            lastDate = date;
            System.out.println(String.format("%s", date)); //SUPPRESS CHECKSTYLE single line
            iterations++;
        }
        Assertions.assertThat(iterations).isEqualTo(24);
        Assertions.assertThat(FDate.isSameMillisecond(endDate, lastDate)).isTrue();
    }

}
