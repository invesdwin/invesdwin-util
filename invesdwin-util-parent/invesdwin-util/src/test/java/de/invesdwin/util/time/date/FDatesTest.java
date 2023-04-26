package de.invesdwin.util.time.date;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.time.date.timezone.FTimeZone;
import de.invesdwin.util.time.date.timezone.TimeZones;

@NotThreadSafe
public class FDatesTest {

    @Test
    public void testBisect() {
        final FDate[] dates = { FDateBuilder.newDate(2000), FDateBuilder.newDate(2000, 6), FDateBuilder.newDate(2001),
                FDateBuilder.newDate(2002) };
        Assertions
                .assertThat(FDates.bisect(dates, FDateBuilder.newDate(2001).addDays(-1),
                        BisectDuplicateKeyHandling.UNDEFINED))
                .isEqualTo(1);
        Assertions.assertThat(FDates.bisect(dates, FDate.MAX_DATE, BisectDuplicateKeyHandling.UNDEFINED)).isEqualTo(3);
        Assertions.assertThat(FDates.bisect(dates, FDate.MIN_DATE, BisectDuplicateKeyHandling.UNDEFINED)).isEqualTo(0);
    }

    @Test
    public void testApplyTimeZone() {
        final FDate now = new FDate();
        final FDate utcTime = now.applyTimeZoneOffset(FTimeZone.UTC);
        //CHECKSTYLE:OFF
        System.out.println(
                now + " (" + now.getTimeZone().getId() + ") -> " + utcTime + " (" + TimeZones.UTC.getID() + ")");
        //CHECKSTYLE:ON
    }

    @Test
    public void testWithoutTimeUtc() {
        final FDate now = FDateBuilder.newDate(2021, 8, 1).withoutTime(FTimeZone.UTC);
        final FDate utcTime = now.applyTimeZoneOffset(FTimeZone.UTC);
        //CHECKSTYLE:OFF
        System.out.println(
                now + " (" + now.getTimeZone().getId() + ") -> " + utcTime + " (" + TimeZones.UTC.getID() + ")");
        //CHECKSTYLE:ON
        Assertions.checkEquals(0, now.getHour(FTimeZone.UTC));
        Assertions.checkEquals(2, now.getHour(FTimeZone.EUROPE_BERLIN));
        Assertions.checkEquals(now.getHour(), now.getHour(now.getTimeZone()));
    }

    @Test
    public void testWithoutTimeEuropeBerlin() {
        final FDate now = FDateBuilder.newDate(2021, 8, 1).withoutTime(FTimeZone.EUROPE_BERLIN);
        final FDate utcTime = now.applyTimeZoneOffset(FTimeZone.EUROPE_BERLIN);
        //CHECKSTYLE:OFF
        System.out.println(now + " (" + now.getTimeZone().getId() + ") -> " + utcTime + " ("
                + TimeZones.EUROPE_BERLIN.getID() + ")");
        //CHECKSTYLE:ON
        Assertions.checkEquals(0, now.getHour(FTimeZone.EUROPE_BERLIN));
        Assertions.checkEquals(22, now.getHour(FTimeZone.UTC));
        Assertions.checkEquals(now.getHour(), now.getHour(now.getTimeZone()));
    }

    @Test
    public void testMapIndexes() throws Exception {
        final FDate[] from = { FDateBuilder.newDate(1999), FDateBuilder.newDate(2000), FDateBuilder.newDate(2001),
                FDateBuilder.newDate(2002) };
        final FDate[] to = { FDateBuilder.newDate(2000), FDateBuilder.newDate(2000, 6), FDateBuilder.newDate(2001),
                FDateBuilder.newDate(2001, 6), FDateBuilder.newDate(2002).addDays(-1), FDateBuilder.newDate(2002, 6) };
        final int[] mapIndexes = FDates.mapIndexes(from, to);
        final int[] expectedIndexes = { -1, 0, 2, 4 };
        //CHECKSTYLE:OFF
        System.out.println(Arrays.toString(mapIndexes));
        //CHECKSTYLE:ON
        Assertions.assertThat(mapIndexes).isEqualTo(expectedIndexes);
    }

}
