package de.invesdwin.util.math.stream.number;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;

@NotThreadSafe
public class NumberStreamVarianceTest {

    @SuppressWarnings("deprecation")
    @Test
    public void test() {
        final OverflowingNumberStreamVariance<Double> avg = new OverflowingNumberStreamVariance<>();
        final NumberStreamVariance<Double> avgEstimate = new NumberStreamVariance<>();

        for (double i = 0; i < 1000; i++) {
            avg.process(i);
            avgEstimate.process(i);
        }

        Assertions.assertThat(avg.getVariance()).isEqualTo(avgEstimate.getVariance());
        Assertions.assertThat(avg.getSampleVariance()).isEqualTo(avgEstimate.getSampleVariance());
    }

}
