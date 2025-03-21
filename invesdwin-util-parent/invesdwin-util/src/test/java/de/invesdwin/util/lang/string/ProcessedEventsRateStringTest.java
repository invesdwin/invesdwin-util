package de.invesdwin.util.lang.string;

import javax.annotation.concurrent.Immutable;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.Duration;

@Immutable
public class ProcessedEventsRateStringTest {

    @Test
    public void test() {
        final ProcessedEventsRateString rateString = new ProcessedEventsRateString(10, Duration.TEN_SECONDS);
        final double rate = rateString.getRate(FTimeUnit.SECONDS);
        Assertions.assertThat(rate).isEqualTo(1D);
    }

    @Test
    public void testToString() {
        Assertions.assertThat(new ProcessedEventsRateString(10, new Duration(10, FTimeUnit.DAYS)).toString())
                .isEqualTo("0.04/h");

        Assertions.assertThat(
                new ProcessedEventsRateString(10, new Duration(10, FTimeUnit.DAYS)).setDecimalPlaces(5).toString())
                .isEqualTo("0.04167/h");

        Assertions.assertThat(new ProcessedEventsRateString(10, new Duration(10, FTimeUnit.HOURS)).toString())
                .isEqualTo("1/h");

        Assertions.assertThat(new ProcessedEventsRateString(10, new Duration(10, FTimeUnit.MINUTES)).toString())
                .isEqualTo("1/m");

        Assertions.assertThat(new ProcessedEventsRateString(10, new Duration(10, FTimeUnit.SECONDS)).toString())
                .isEqualTo("1/s");

        Assertions.assertThat(new ProcessedEventsRateString(10, new Duration(10, FTimeUnit.MILLISECONDS)).toString())
                .isEqualTo("1/ms");

        Assertions.assertThat(new ProcessedEventsRateString(10, new Duration(10, FTimeUnit.MICROSECONDS)).toString())
                .isEqualTo("1,000/ms");

        Assertions.assertThat(new ProcessedEventsRateString(10, new Duration(10, FTimeUnit.NANOSECONDS)).toString())
                .isEqualTo("1,000/µs");

        Assertions.assertThat(new ProcessedEventsRateString(10, new Duration(1, FTimeUnit.NANOSECONDS)).toString())
                .isEqualTo("10,000/µs");

        Assertions.assertThat(new ProcessedEventsRateString(10, new Duration(100, FTimeUnit.NANOSECONDS)).toString())
                .isEqualTo("100/µs");

        Assertions.assertThat(new ProcessedEventsRateString(100, new Duration(1, FTimeUnit.NANOSECONDS)).toString())
                .isEqualTo("100,000/µs");

        Assertions.assertThat(new ProcessedEventsRateString(1000, new Duration(1, FTimeUnit.NANOSECONDS)).toString())
                .isEqualTo("1,000,000/µs");

        Assertions.assertThat(new ProcessedEventsRateString(1000, new Duration(1, FTimeUnit.NANOSECONDS))
                .setFixedTimeUnit(FTimeUnit.SECONDS)
                .toString()).isEqualTo("1,000,000,000,000/s");
    }

}
