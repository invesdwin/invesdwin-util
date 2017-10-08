package de.invesdwin.util.time;

import java.util.TimeZone;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.time.fdate.FDate;
import de.invesdwin.util.time.fdate.FDateBuilder;

@NotThreadSafe
public class TimeZonesTest {

    @Test
    public void testTimeZones() {
        showTimeZoneInfo(TimeZones.getTimeZone("Europe/London"));
        showTimeZoneInfo(TimeZones.getTimeZone("America/New_York"));
    }

    @Test
    public void testWrongTimeZone() {
        try {
            TimeZones.getTimeZone("America/New York");
            Assertions.failBecauseExceptionWasNotThrown(AssertionError.class);
        } catch (final AssertionError e) {
            Assertions.assertThat(e.getMessage()).contains("GMT");
        }
    }

    private void showTimeZoneInfo(final TimeZone targetTimeZone) {
        final FDateBuilder db = new FDateBuilder().withHours(8).withTimeZone(targetTimeZone);
        final FDate inDst = db.withDate(FDateBuilder.newDate(2013, 8, 1)).get();
        System.out.println(String.format("%s: %s dst=%s", targetTimeZone.getID(), inDst, //SUPPRESS CHECKSTYLE single line
                targetTimeZone.inDaylightTime(inDst.dateValue())));
        final TimeZone universalTimeZone = TimeZones.getTimeZone("UTC");
        System.out.println(String.format("%s: %s dst=%s", universalTimeZone.getID(), inDst.toString(universalTimeZone), //SUPPRESS CHECKSTYLE single line
                targetTimeZone.inDaylightTime(inDst.dateValue())));
        final FDate noDst = db.withDate(FDateBuilder.newDate(2013, 3, 1)).get();
        System.out.println(String.format("%s: %s dst=%s", targetTimeZone.getID(), noDst, //SUPPRESS CHECKSTYLE single line
                targetTimeZone.inDaylightTime(noDst.dateValue())));
        System.out.println(String.format("%s: %s dst=%s", universalTimeZone.getID(), noDst.toString(universalTimeZone), //SUPPRESS CHECKSTYLE single line
                targetTimeZone.inDaylightTime(noDst.dateValue())));
    }

}
