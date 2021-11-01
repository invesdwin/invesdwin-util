package de.invesdwin.util.time.date.timezone;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.FDateBuilder;

@NotThreadSafe
public class TimeZonesTest {

    @Test
    public void testOffsets() {
        showTimeZoneInfo(TimeZones.getFTimeZone("UTC+1"));
    }

    @Test
    public void testTimeZones() {
        showTimeZoneInfo(TimeZones.getFTimeZone("Europe/London"));
        showTimeZoneInfo(TimeZones.getFTimeZone("America/New_York"));
    }

    @Test
    public void testWrongTimeZone() {
        try {
            TimeZones.getTimeZone("America/New York");
            Assertions.failBecauseExceptionWasNotThrown(AssertionError.class);
        } catch (final Exception e) {
            Assertions.assertThat(e.getMessage()).contains("America/New York");
        }
    }

    private void showTimeZoneInfo(final FTimeZone targetTimeZone) {
        final FDateBuilder db = new FDateBuilder().setHours(8).setTimeZone(targetTimeZone);
        final FDate inDst = db.setDate(FDateBuilder.newDate(2013, 8, 1)).getDate();
        System.out.println(String.format("%s: %s dst=%s", targetTimeZone.getId(), inDst, //SUPPRESS CHECKSTYLE single line
                targetTimeZone.isDST(inDst.millisValue())));
        final FTimeZone universalTimeZone = FTimeZone.UTC;
        System.out.println(String.format("%s: %s dst=%s", universalTimeZone.getId(), inDst.toString(universalTimeZone), //SUPPRESS CHECKSTYLE single line
                targetTimeZone.isDST(inDst.millisValue())));
        final FDate noDst = db.setDate(FDateBuilder.newDate(2013, 3, 1)).getDate();
        System.out.println(String.format("%s: %s dst=%s", targetTimeZone.getId(), noDst, //SUPPRESS CHECKSTYLE single line
                targetTimeZone.isDST(noDst.millisValue())));
        System.out.println(String.format("%s: %s dst=%s", universalTimeZone.getId(), noDst.toString(universalTimeZone), //SUPPRESS CHECKSTYLE single line
                targetTimeZone.isDST(noDst.millisValue())));
    }

}
