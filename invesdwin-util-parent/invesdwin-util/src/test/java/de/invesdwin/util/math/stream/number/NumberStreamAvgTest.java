package de.invesdwin.util.math.stream.number;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.Test;

import de.invesdwin.util.assertions.Assertions;

@NotThreadSafe
public class NumberStreamAvgTest {

    @Test
    public void test() {
        final OverflowingNumberStreamAvg<Double> avg = new OverflowingNumberStreamAvg<>();
        final NumberStreamAvg<Double> avgEstimate = new NumberStreamAvg<>();

        for (double i = 0; i < 1000; i++) {
            avg.process(i);
            avgEstimate.process(i);
        }

        Assertions.assertThat(avg.getAvg()).isEqualTo(avgEstimate.getAvg());
    }

}
