package de.invesdwin.util.time;

import java.util.concurrent.TimeUnit;

import javax.annotation.concurrent.Immutable;

import org.junit.Test;

import de.invesdwin.util.assertions.Assertions;

@Immutable
public class InstantTest {

    @Test
    public void testSleepLoop() throws InterruptedException {
        final int sleep = 1;
        final int iterations = 1000;
        final Instant start = new Instant();
        Instant lastIteration = new Instant();
        for (int i = 0; i < iterations; i++) {
            TimeUnit.MICROSECONDS.sleep(1);
            lastIteration.sleepRelative(sleep, TimeUnit.MILLISECONDS);
            lastIteration = new Instant();
        }
        final Instant ende = new Instant();
        System.out.println(String.format("Duration in millis: " //SUPPRESS CHECKSTYLE single line
                + new Duration(start, ende).toString(TimeUnit.MILLISECONDS)));

        Assertions.assertThat(new Duration(start, ende).isGreaterThan(iterations, TimeUnit.MILLISECONDS)).isTrue();
        Assertions.assertThat(new Duration(start, ende).isGreaterThan((iterations * 3), TimeUnit.MILLISECONDS))
        .isFalse();
    }

    @Test
    public void testSleep() throws InterruptedException {
        final Instant start = new Instant();
        TimeUnit.SECONDS.sleep(1);
        final Duration dauer1 = new Duration(start, TimeUnit.SECONDS);
        Assertions.assertThat(dauer1.intValue()).isEqualTo(1);
        Assertions.assertThat(dauer1.toString()).isEqualTo("PT1S");
        start.sleepRelative(2, TimeUnit.SECONDS);
        final Duration dauer2 = new Duration(start, TimeUnit.SECONDS);
        Assertions.assertThat(dauer2.intValue()).isEqualTo(2);
        Assertions.assertThat(dauer2.toString()).isEqualTo("PT2S");
    }

}
