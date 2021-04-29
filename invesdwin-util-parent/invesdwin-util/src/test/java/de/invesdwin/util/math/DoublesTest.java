package de.invesdwin.util.math;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.math.decimal.scaled.Percent;

@NotThreadSafe
public class DoublesTest {

    @Test
    public void testNegativeHprRising() {
        final double prevValue = -20;
        final double nextValue = -1;
        final double holdingPeriodReturn = Percent.newGrowthRate(prevValue, nextValue);
        final double in = holdingPeriodReturn;
        final double log = Doubles.log(in);
        final double exp = Doubles.round(Doubles.exp(log));
        Assertions.assertThat(exp).as("in=%s log=%s exp=%s", in, log, exp).isEqualTo(in);
    }

    @Test
    public void testNegativeHprFalling() {
        final double prevValue = -1;
        final double nextValue = -2;
        final double holdingPeriodReturn = Percent.newGrowthRate(prevValue, nextValue);
        final double in = holdingPeriodReturn;
        final double log = Doubles.log(in);
        final double exp = Doubles.round(Doubles.exp(log));
        Assertions.assertThat(exp).as("in=%s log=%s exp=%s", in, log, exp).isEqualTo(1D);
    }

    @Test
    public void testCompareNaN() {
        Assertions.assertThat(Doubles.compare(Double.NaN, Double.NaN)).isEqualTo(0);
        Assertions.assertThat(Doubles.compare(1, Double.NaN)).isEqualTo(1);
        Assertions.assertThat(Doubles.compare(-1, Double.NaN)).isEqualTo(-1);
        Assertions.assertThat(Doubles.compare(Double.NaN, 1)).isEqualTo(-1);
        Assertions.assertThat(Doubles.compare(Double.NaN, -1)).isEqualTo(1);
    }

    @Test
    public void testNegativeLogExactly1() {
        final double in = -1D;
        final double log = Doubles.log(in);
        final double exp = Doubles.exp(log);
        Assertions.assertThat(exp).as("in=%s log=%s exp=%s", in, log, exp).isEqualTo(1D);
    }

    @Test
    public void testPositiveLogExactly1() {
        final double in = 1D;
        final double log = Doubles.log(in);
        final double exp = Doubles.exp(log);
        Assertions.assertThat(exp).as("in=%s log=%s exp=%s", in, log, exp).isEqualTo(in);
    }

    @Test
    public void testNegativeLog10Exactly1() {
        final double in = -1D;
        final double log = Doubles.log10(in);
        final double exp = Doubles.exp10(log);
        Assertions.assertThat(exp).as("in=%s log=%s exp=%s", in, log, exp).isEqualTo(1D);
    }

    @Test
    public void testPositiveLog10Exactly1() {
        final double in = 1D;
        final double log = Doubles.log10(in);
        final double exp = Doubles.exp10(log);
        Assertions.assertThat(exp).as("in=%s log=%s exp=%s", in, log, exp).isEqualTo(in);
    }

    @Test
    public void testNegativeLogAbove1() {
        final double in = -1.1D;
        final double log = Doubles.log(in);
        final double exp = Doubles.exp(log);
        Assertions.assertThat(exp).as("in=%s log=%s exp=%s", in, log, exp).isEqualTo(1D);
    }

    @Test
    public void testPositiveLogAbove1() {
        final double in = 1.1D;
        final double log = Doubles.log(in);
        final double exp = Doubles.exp(log);
        Assertions.assertThat(exp).as("in=%s log=%s exp=%s", in, log, exp).isEqualTo(in);
    }

    @Test
    public void testNegativeLog10Above1() {
        final double in = -1.1D;
        final double log = Doubles.log10(in);
        final double exp = Doubles.exp10(log);
        Assertions.assertThat(exp).as("in=%s log=%s exp=%s", in, log, exp).isEqualTo(1D);
    }

    @Test
    public void testPositiveLog10Above1() {
        final double in = 1.1D;
        final double log = Doubles.log10(in);
        final double exp = Doubles.exp10(log);
        Assertions.assertThat(exp).as("in=%s log=%s exp=%s", in, log, exp).isEqualTo(in);
    }

    @Test
    public void testNegativeLogBelow1() {
        final double in = -0.9D;
        final double log = Doubles.log(in);
        final double exp = Doubles.exp(log);
        Assertions.assertThat(exp).as("in=%s log=%s exp=%s", in, log, exp).isEqualTo(1D);
    }

    @Test
    public void testPositiveLogBelow1() {
        final double in = 0.9D;
        final double log = Doubles.log(in);
        final double exp = Doubles.exp(log);
        Assertions.assertThat(exp).as("in=%s log=%s exp=%s", in, log, exp).isEqualTo(in);
    }

    @Test
    public void testNegativeLog10Below1() {
        final double in = -0.9D;
        final double log = Doubles.log10(in);
        final double exp = Doubles.exp10(log);
        Assertions.assertThat(exp).as("in=%s log=%s exp=%s", in, log, exp).isEqualTo(1D);
    }

    @Test
    public void testPositiveLog10Below1() {
        final double in = 0.9D;
        final double log = Doubles.log10(in);
        final double exp = Doubles.exp10(log);
        Assertions.assertThat(exp).as("in=%s log=%s exp=%s", in, log, exp).isEqualTo(in);
    }

}
