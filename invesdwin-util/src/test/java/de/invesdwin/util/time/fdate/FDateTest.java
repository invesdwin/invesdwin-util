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

}
