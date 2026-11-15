package de.invesdwin.util.math;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

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

    @Test
    public void testNonFiniteToZeroMapsNaNAndInfinitiesToPositiveZero() {
        //the replacement is the int literal 0 widened to double, so it is positive zero and never negative zero
        checkSameBits(Doubles.nonFiniteToZero(Double.NaN), 0D, "value=%s", Double.NaN);
        checkSameBits(Doubles.nonFiniteToZero(Double.POSITIVE_INFINITY), 0D, "value=%s", Double.POSITIVE_INFINITY);
        checkSameBits(Doubles.nonFiniteToZero(Double.NEGATIVE_INFINITY), 0D, "value=%s", Double.NEGATIVE_INFINITY);
    }

    @Test
    public void testNonFiniteToZeroKeepsFiniteBitsIncludingNegativeZero() {
        //negative zero is finite, so it is passed through with its sign bit instead of being replaced
        checkSameBits(Doubles.nonFiniteToZero(-0D), -0D, "negativeZero");
        checkSameBits(Doubles.nonFiniteToZero(0D), 0D, "positiveZero");
        checkSameBits(Doubles.nonFiniteToZero(1.5D), 1.5D, "value=1.5");
        checkSameBits(Doubles.nonFiniteToZero(-1.5D), -1.5D, "value=-1.5");
        checkSameBits(Doubles.nonFiniteToZero(Double.MAX_VALUE), Double.MAX_VALUE, "maxValue");
        checkSameBits(Doubles.nonFiniteToZero(Double.MIN_VALUE), Double.MIN_VALUE, "minValue");
    }

    @Test
    public void testApplySignFromReferenceFlipsOnlyWhenSignsDiffer() {
        checkSameBits(Doubles.applySignFromReference(3D, -7D), -3D, "value=3,reference=-7");
        checkSameBits(Doubles.applySignFromReference(-3D, 7D), 3D, "value=-3,reference=7");
        checkSameBits(Doubles.applySignFromReference(3D, 7D), 3D, "value=3,reference=7");
        checkSameBits(Doubles.applySignFromReference(-3D, -7D), -3D, "value=-3,reference=-7");
    }

    @Test
    public void testApplySignFromReferenceUsesTheComparisonSignNotTheSignBit() {
        //isPositive is value >= 0 and -0.0 >= 0 is true, so a positive zero gains a negative reference's sign ...
        checkSameBits(Doubles.applySignFromReference(0D, -7D), -0D, "value=0.0,reference=-7");
        //... while a negative zero counts as positive and a negative zero reference flips nothing
        checkSameBits(Doubles.applySignFromReference(-0D, 7D), -0D, "value=-0.0,reference=7");
        checkSameBits(Doubles.applySignFromReference(5D, -0D), 5D, "value=5,reference=-0.0");
    }

    @Test
    public void testApplySignFromReferenceOnNonFiniteValues() {
        //NaN is not positive either, so a negative reference does not flip it
        checkSameBits(Doubles.applySignFromReference(Double.NaN, -7D), Double.NaN, "value=NaN,reference=-7");
        checkSameBits(Doubles.applySignFromReference(Double.POSITIVE_INFINITY, -7D), Double.NEGATIVE_INFINITY,
                "value=Infinity,reference=-7");
        checkSameBits(Doubles.applySignFromReference(Double.NEGATIVE_INFINITY, 7D), Double.POSITIVE_INFINITY,
                "value=-Infinity,reference=7");
    }

    @Test
    public void testScaleByPowerOfTenNearReferenceReturnsValueForAnyZeroInput() {
        //the guard is value == 0D || reference == 0D, which is true for a negative zero as well
        checkSameBits(Doubles.scaleByPowerOfTenNearReference(0D, 100D), 0D, "value=0.0,reference=100");
        checkSameBits(Doubles.scaleByPowerOfTenNearReference(-0D, 100D), -0D, "value=-0.0,reference=100");
        checkSameBits(Doubles.scaleByPowerOfTenNearReference(5D, 0D), 5D, "value=5,reference=0.0");
        checkSameBits(Doubles.scaleByPowerOfTenNearReference(5D, -0D), 5D, "value=5,reference=-0.0");
    }

    @Test
    public void testScaleByPowerOfTenNearReferenceScalesUpWithoutOvershooting() {
        //nearest power of ten by ABSOLUTE distance to the reference: 10 is nearer to 7 than 1 is, 1 is nearer to 5
        checkSameBits(Doubles.scaleByPowerOfTenNearReference(1D, 7D), 10D, "value=1,reference=7");
        checkSameBits(Doubles.scaleByPowerOfTenNearReference(1D, 5D), 1D, "value=1,reference=5");
        checkSameBits(Doubles.scaleByPowerOfTenNearReference(2D, 300D), 200D, "value=2,reference=300");
        checkSameBits(Doubles.scaleByPowerOfTenNearReference(2D, 199D), 200D, "value=2,reference=199");
    }

    @Test
    public void testScaleByPowerOfTenNearReferenceScalesDownWhenValueExceedsReference() {
        checkSameBits(Doubles.scaleByPowerOfTenNearReference(1000D, 1D), 1D, "value=1000,reference=1");
        checkSameBits(Doubles.scaleByPowerOfTenNearReference(1000D, 100D), 100D, "value=1000,reference=100");
        //the ladder multiplies as well: a non-dyadic start still lands on the exact power of ten
        checkSameBits(Doubles.scaleByPowerOfTenNearReference(0.1D, 100D), 100D, "value=0.1,reference=100");
    }

    @Test
    public void testScaleByPowerOfTenNearReferenceTakesTheSignOfTheReference() {
        checkSameBits(Doubles.scaleByPowerOfTenNearReference(-1D, 7D), 10D, "value=-1,reference=7");
        checkSameBits(Doubles.scaleByPowerOfTenNearReference(1D, -7D), -10D, "value=1,reference=-7");
        checkSameBits(Doubles.scaleByPowerOfTenNearReference(-1D, -7D), -10D, "value=-1,reference=-7");
    }

    @Test
    public void testScaleByPowerOfTenNearReferenceNeverOverflowsToInfinity() {
        //the next step would overflow, and an infinite difference is no improvement, so the finite value is kept
        checkSameBits(Doubles.scaleByPowerOfTenNearReference(0x1p1023, Double.MAX_VALUE), 0x1p1023,
                "value=0x1p1023,reference=MAX_VALUE");
    }

    @Test
    public void testScaleByPowerOfTenNearReferenceIsCappedAtTwentySteps() {
        //the try cap bounds the ladder; 10^k is exactly representable up to 10^22, so both bounds are bit exact
        checkSameBits(Doubles.scaleByPowerOfTenNearReference(1D, 1e30), 1e20, "value=1,reference=1e30");
        checkSameBits(Doubles.scaleByPowerOfTenNearReference(1e20, 1e-30), 1D, "value=1e20,reference=1e-30");
    }

    @Test
    public void testScaleByPowerOfTenNearReferenceKeepsNonFiniteValuesNonFinite() {
        //the ladder never improves a non-finite value, nonFiniteToZero is the sanitizer the callers apply to it
        checkSameBits(Doubles.scaleByPowerOfTenNearReference(Double.POSITIVE_INFINITY, 5D), Double.POSITIVE_INFINITY,
                "value=Infinity,reference=5");
        Assertions.assertThat(Doubles.isNaN(Doubles.scaleByPowerOfTenNearReference(Double.NaN, 5D)))
                .as("value=NaN,reference=5").isTrue();
        checkSameBits(Doubles.nonFiniteToZero(Doubles.scaleByPowerOfTenNearReference(Double.NaN, 5D)), 0D,
                "nonFiniteToZero after the ladder");
        //the zero guard is reference == 0D, which is false for NaN, so a NaN reference runs the ladder to the cap
        checkSameBits(Doubles.scaleByPowerOfTenNearReference(1e20, Double.NaN), 1D, "value=1e20,reference=NaN");
    }

    /**
     * Compares bit identity rather than value: -0.0 and 0.0 compare equal but differ in their sign bit, and a one ulp
     * change in a ladder step would pass unnoticed otherwise.
     */
    private static void checkSameBits(final double actual, final double expected, final String where,
            final Object... args) {
        Assertions.assertThat(Double.doubleToRawLongBits(actual)).as(where, args)
                .isEqualTo(Double.doubleToRawLongBits(expected));
    }

}
