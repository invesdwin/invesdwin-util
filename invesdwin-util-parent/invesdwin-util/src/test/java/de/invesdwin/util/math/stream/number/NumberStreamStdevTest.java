package de.invesdwin.util.math.stream.number;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;

@NotThreadSafe
public class NumberStreamStdevTest {

    @Test
    public void test() {
        final OverflowingNumberStreamStandardDeviation<Double> avg = new OverflowingNumberStreamStandardDeviation<>();
        final NumberStreamStdev<Double> avgEstimate = new NumberStreamStdev<>();

        for (double i = 0; i < 1000; i++) {
            avg.process(i);
            avgEstimate.process(i);
        }

        Assertions.assertThat(avg.getStandardDeviation()).isEqualTo(avgEstimate.getStandardDeviation());
        Assertions.assertThat(avg.getSampleStandardDeviation()).isEqualTo(avgEstimate.getSampleStandardDeviation());
    }

}