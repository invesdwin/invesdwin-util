package de.invesdwin.util.time;

import javax.annotation.concurrent.Immutable;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.time.duration.Duration;
import de.invesdwin.util.time.fdate.FTimeUnit;

@Immutable
public class InstantTest {

    @Test
    public void testSleepLoop() throws InterruptedException {
        final int sleep = 1;
        final int iterations = 1000;
        final Instant start = new Instant();
        Instant lastIteration = new Instant();
        for (int i = 0; i < iterations; i++) {
            FTimeUnit.MICROSECONDS.sleep(1);
            lastIteration.sleepRelative(sleep, FTimeUnit.MILLISECONDS);
            lastIteration = new Instant();
        }
        final Instant ende = new Instant();
        System.out.println(String.format("Duration in millis: " //SUPPRESS CHECKSTYLE single line
                + new Duration(start, ende).toString(FTimeUnit.MILLISECONDS)));

        Assertions.assertThat(new Duration(start, ende).isGreaterThan(iterations, FTimeUnit.MILLISECONDS)).isTrue();
        Assertions.assertThat(new Duration(start, ende).isGreaterThan((iterations * 3), FTimeUnit.MILLISECONDS))
                .isFalse();
    }

    @Test
    public void testSleep() throws InterruptedException {
        final Instant start = new Instant();
        FTimeUnit.SECONDS.sleep(1);
        final Duration dauer1 = new Duration(start, FTimeUnit.SECONDS);
        Assertions.assertThat(dauer1.intValue()).isEqualTo(1);
        Assertions.assertThat(dauer1.toString()).isEqualTo("PT1S");
        start.sleepRelative(2, FTimeUnit.SECONDS);
        final Duration dauer2 = new Duration(start, FTimeUnit.SECONDS);
        Assertions.assertThat(dauer2.intValue()).isEqualTo(2);
        Assertions.assertThat(dauer2.toString()).isEqualTo("PT2S");
    }

}
