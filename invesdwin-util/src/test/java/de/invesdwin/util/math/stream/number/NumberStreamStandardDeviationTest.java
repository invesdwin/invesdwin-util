package de.invesdwin.util.math.stream.number;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.Test;

import de.invesdwin.util.assertions.Assertions;

@NotThreadSafe
public class NumberStreamStandardDeviationTest {

    @Test
    public void test() {
        final OverflowingNumberStreamStandardDeviation<Double> avg = new OverflowingNumberStreamStandardDeviation<>();
        final NumberStreamStandardDeviation<Double> avgEstimate = new NumberStreamStandardDeviation<>();

        for (double i = 0; i < 1000; i++) {
            avg.process(i);
            avgEstimate.process(i);
        }

        Assertions.assertThat(avg.getStandardDeviation()).isEqualTo(avgEstimate.getStandardDeviation());
        Assertions.assertThat(avg.getSampleStandardDeviation()).isEqualTo(avgEstimate.getSampleStandardDeviation());
    }

}