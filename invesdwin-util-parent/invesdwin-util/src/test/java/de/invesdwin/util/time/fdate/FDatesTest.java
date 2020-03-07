package de.invesdwin.util.time.fdate;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.time.TimeZones;

@NotThreadSafe
public class FDatesTest {

    @Test
    public void testBisect() {
        final FDate[] dates = { FDateBuilder.newDate(2000), FDateBuilder.newDate(2001), FDateBuilder.newDate(2002) };
        Assertions.assertThat(FDates.bisect(dates, FDate.MAX_DATE)).isEqualTo(2);
        Assertions.assertThat(FDates.bisect(dates, FDate.MIN_DATE)).isEqualTo(0);
    }

    @Test
    public void testRevertTimeZone() {
        final FDate now = new FDate();
        final FDate utcTime = now.applyTimeZoneOffset(TimeZones.UTC);
        //CHECKSTYLE:OFF
        System.out.println(
                now + " (" + now.getTimeZone().getID() + ") -> " + utcTime + " (" + TimeZones.UTC.getID() + ")");
        //CHECKSTYLE:ON
    }

}
