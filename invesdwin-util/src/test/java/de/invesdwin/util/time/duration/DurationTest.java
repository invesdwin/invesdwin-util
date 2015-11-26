package de.invesdwin.util.time.duration;

import javax.annotation.concurrent.Immutable;

import org.junit.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.time.Instant;
import de.invesdwin.util.time.fdate.FDate;
import de.invesdwin.util.time.fdate.FTimeUnit;

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
                .isTrue();
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
    }
}
