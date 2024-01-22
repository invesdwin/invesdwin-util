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

}
