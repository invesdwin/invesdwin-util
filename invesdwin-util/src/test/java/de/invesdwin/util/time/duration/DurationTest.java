package de.invesdwin.util.time.duration;

import java.util.concurrent.TimeUnit;

import javax.annotation.concurrent.Immutable;

import org.junit.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.time.Instant;
import de.invesdwin.util.time.fdate.FDate;

@Immutable
public class DurationTest {

    @Test
    public void testDuration() {
        final Instant now = new Instant();
        Assertions.assertThat(new Duration(now, now, TimeUnit.NANOSECONDS).intValue()).isZero();

        final Instant earlier = new Instant(0, TimeUnit.NANOSECONDS);
        final Instant later = new Instant(1, TimeUnit.NANOSECONDS);
        Assertions.assertThat(new Duration(earlier, later, TimeUnit.NANOSECONDS).intValue()).isEqualTo(1);
        Assertions.assertThat(new Duration(later, earlier, TimeUnit.NANOSECONDS).intValue()).isEqualTo(-1);
    }

    @Test
    public void testIsGreaterThan() throws InterruptedException {
        final Instant start = new Instant();
        start.sleepRelative(10, TimeUnit.MILLISECONDS);
        Assertions.assertThat(new Duration(start).isGreaterThan(5, TimeUnit.MILLISECONDS)).isTrue();
        Assertions.assertThat(
                new Duration(start, new Instant(0, TimeUnit.MILLISECONDS)).isGreaterThan(5, TimeUnit.MILLISECONDS))
                .isTrue();
    }

    @Test
    public void testIsGreaterThanFDate() throws InterruptedException {
        final FDate start = new FDate().addMinutes(-1);
        Assertions.assertThat(new Duration(start).isGreaterThan(55, TimeUnit.SECONDS)).isTrue();
        Assertions.assertThat(new Duration(start).isGreaterThan(65, TimeUnit.SECONDS)).isFalse();
    }

    @Test
    public void testEquals() {
        new Duration(1, TimeUnit.DAYS).equals(new Duration(1, TimeUnit.DAYS));
    }

    @Test
    public void testToString() {
        Assertions.assertThat(new Duration(0, TimeUnit.DAYS).toString()).isEqualTo("P0");
        final FDate date = new FDate();
        Assertions.assertThat(new Duration(date, date.addDays(1)).toString()).isEqualTo("P1D");
        Assertions.assertThat(new Duration(date.addDays(1), date).toString()).isEqualTo("-P1D");
        Assertions.assertThat(new Duration(66, TimeUnit.MINUTES).toString(TimeUnit.HOURS)).isEqualTo("PT1H");
        Assertions.assertThat(new Duration(-66, TimeUnit.MINUTES).toString(TimeUnit.HOURS)).isEqualTo("-PT1H");
        Assertions.assertThat(new Duration(-66, TimeUnit.MINUTES).toString(TimeUnit.DAYS)).isEqualTo("P0");
        Assertions.assertThat(new Duration(10, TimeUnit.MINUTES).toString()).isEqualTo("PT10M");
    }
}
