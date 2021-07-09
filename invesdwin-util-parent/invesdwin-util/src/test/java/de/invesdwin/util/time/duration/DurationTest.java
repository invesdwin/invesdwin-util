package de.invesdwin.util.time.duration;

import javax.annotation.concurrent.Immutable;

import org.junit.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.time.Instant;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.FTimeUnit;

@Immutable
public class DurationTest {

    @Test
    public void testDuration() {
        final Instant now = new Instant();
        Assertions.assertThat(new Duration(now, now, FTimeUnit.NANOSECONDS).intValue()).isZero();

        final Instant earlier = new Instant(0, FTimeUnit.NANOSECONDS);
        final Instant later = new Instant(1, FTimeUnit.NANOSECONDS);
        Assertions.assertThat(new Duration(earlier, later, FTimeUnit.NANOSECONDS).intValue()).isEqualTo(1);
        Assertions.assertThat(new Duration(later, earlier, FTimeUnit.NANOSECONDS).intValue()).isEqualTo(-1);
    }

    @Test
    public void testIsGreaterThan() throws InterruptedException {
        final Instant start = new Instant();
        start.sleepRelative(10, FTimeUnit.MILLISECONDS);
        Assertions.assertThat(new Duration(start).isGreaterThan(5, FTimeUnit.MILLISECONDS)).isTrue();
        Assertions.assertThat(
                new Duration(start, new Instant(0, FTimeUnit.MILLISECONDS)).isGreaterThan(5, FTimeUnit.MILLISECONDS))
                .isFalse();
    }

    @Test
    public void testIsGreaterThanFDate() throws InterruptedException {
        final FDate start = new FDate().addMinutes(-1);
        Assertions.assertThat(new Duration(start).isGreaterThan(55, FTimeUnit.SECONDS)).isTrue();
        Assertions.assertThat(new Duration(start).isGreaterThan(65, FTimeUnit.SECONDS)).isFalse();
    }

    @Test
    public void testEquals() {
        new Duration(1, FTimeUnit.DAYS).equals(new Duration(1, FTimeUnit.DAYS));
    }

    @Test
    public void testToString() {
        Assertions.assertThat(new Duration(0, FTimeUnit.DAYS).toString()).isEqualTo("P0");
        final FDate date = new FDate();
        Assertions.assertThat(new Duration(date, date.addDays(1)).toString()).isEqualTo("P1D");
        Assertions.assertThat(new Duration(date.addDays(1), date).toString()).isEqualTo("-P1D");
        Assertions.assertThat(new Duration(66, FTimeUnit.MINUTES).toString(FTimeUnit.HOURS)).isEqualTo("PT1H");
        Assertions.assertThat(new Duration(-66, FTimeUnit.MINUTES).toString(FTimeUnit.HOURS)).isEqualTo("-PT1H");
        Assertions.assertThat(new Duration(-66, FTimeUnit.MINUTES).toString(FTimeUnit.DAYS)).isEqualTo("P0");
        Assertions.assertThat(new Duration(10, FTimeUnit.MINUTES).toString()).isEqualTo("PT10M");
        Assertions.assertThat(new Duration(new FDate(0),
                new FDate(0).addYears(1)
                        .addDays(FTimeUnit.DAYS_IN_MONTH)
                        .addWeeks(1)
                        .addDays(1)
                        .addHours(1)
                        .addMinutes(1)
                        .addSeconds(1)
                        .addMilliseconds(1)).toString())
                .isEqualTo("P1Y1M1W1DT1H1M1.001S");
    }

    @Test
    public void testDurationParser() {
        final String durationStr = "P1Y1M1W1DT1H1M1.001.001.001S";
        final Duration duration = Duration.valueOf(durationStr);
        Assertions.assertThat(duration.toString()).isEqualTo(durationStr);
    }

    @Test
    public void testStringValue() {
        final String durationStr = "1 YEARS";
        final Duration duration = Duration.valueOf(durationStr);
        Assertions.assertThat(duration.stringValue()).isEqualTo(durationStr);
    }

    @Test
    public void testStringValueNoSpace() {
        final String durationStr = " 1YEAR ";
        final Duration duration = Duration.valueOf(durationStr);
        Assertions.assertThat(duration.stringValue()).isEqualTo("1 YEARS");
    }

    @Test
    public void testStringValueNoSpaceDurationParser() {
        final String durationStr = " P1Y ";
        final Duration duration = Duration.valueOf(durationStr);
        Assertions.assertThat(duration.stringValue()).isEqualTo("1 YEARS");
    }

    @Test
    public void testConvertToSeconds() {
        final String[] durations = new String[] { //
                "PT2H2M22.517.800S", //
                "PT33M14.970.100S", //
                "PT9M16.168.200S", //
                "PT2M49.191.700S", //
                "PT1M17.103.900S", //
                "PT56.499.700S" //
        };
        for (int i = 0; i < durations.length; i++) {
            //CHECKSTYLE:OFF
            System.out.println(Duration.valueOf(durations[i]).doubleValue(FTimeUnit.MILLISECONDS)
                    / FTimeUnit.MILLISECONDS_IN_SECOND);
            //CHECKSTYLE:ON
        }
    }
}
