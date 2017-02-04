package de.invesdwin.util.math.decimal.internal;

import java.math.RoundingMode;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.math3.random.RandomGenerator;

import de.invesdwin.util.collections.iterable.EmptyCloseableIterator;
import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.IDecimalAggregate;
import de.invesdwin.util.math.decimal.config.BSplineInterpolationConfig;
import de.invesdwin.util.math.decimal.config.InterpolationConfig;
import de.invesdwin.util.math.decimal.config.LoessInterpolationConfig;

@Immutable
public final class DummyDecimalAggregate<E extends ADecimal<E>> implements IDecimalAggregate<E> {

    @SuppressWarnings("rawtypes")
    private static final DummyDecimalAggregate INSTANCE = new DummyDecimalAggregate();

    private DummyDecimalAggregate() {}

    @Override
    public IDecimalAggregate<E> growthRates() {
        return this;
    }

    @Override
    public E growthRate() {
        return (E) null;
    }

    @Override
    public E growthRatesTrend() {
        return (E) null;
    }

    @Override
    public IDecimalAggregate<E> reverse() {
        return this;
    }

    @Override
    public E avgWeightedAsc() {
        return (E) null;
    }

    @Override
    public E avgWeightedDesc() {
        return (E) null;
    }

    @Override
    public E sum() {
        return (E) null;
    }

    @Override
    public E avg() {
        return (E) null;
    }

    @Override
    public E product() {
        return (E) null;
    }

    @Override
    public E geomAvg() {
        return (E) null;
    }

    @Override
    public E max() {
        return (E) null;
    }

    @Override
    public E min() {
        return (E) null;
    }

    @Override
    public E minMaxDistance() {
        return (E) null;
    }

    @Override
    public E sampleStandardDeviation() {
        return (E) null;
    }

    @Override
    public E standardDeviation() {
        return (E) null;
    }

    @Override
    public E variance() {
        return (E) null;
    }

    @Override
    public E sampleVariance() {
        return (E) null;
    }

    @SuppressWarnings("unchecked")
    public static <T extends ADecimal<T>> IDecimalAggregate<T> getInstance() {
        return INSTANCE;
    }

    @Override
    public List<E> values() {
        return Collections.emptyList();
    }

    @Override
    public IDecimalAggregate<E> round() {
        return this;
    }

    @Override
    public IDecimalAggregate<E> round(final RoundingMode roundingMode) {
        return this;
    }

    @Override
    public IDecimalAggregate<E> round(final int scale) {
        return this;
    }

    @Override
    public IDecimalAggregate<E> round(final int scale, final RoundingMode roundingMode) {
        return this;
    }

    @Override
    public IDecimalAggregate<E> roundToStep(final E step) {
        return this;
    }

    @Override
    public IDecimalAggregate<E> roundToStep(final E step, final RoundingMode roundingMode) {
        return this;
    }

    @Override
    public String toString() {
        return "[]";
    }

    @Override
    public IDecimalAggregate<E> positiveValues() {
        return this;
    }

    @Override
    public IDecimalAggregate<E> negativeValues() {
        return this;
    }

    @Override
    public IDecimalAggregate<E> positiveNonZeroValues() {
        return this;
    }

    @Override
    public IDecimalAggregate<E> negativeOrZeroValues() {
        return this;
    }

    @Override
    public IDecimalAggregate<E> nonZeroValues() {
        return this;
    }

    @Override
    public IDecimalAggregate<E> addEach(final E augend) {
        return this;
    }

    @Override
    public IDecimalAggregate<E> subtractEach(final E subtrahend) {
        return this;
    }

    @Override
    public IDecimalAggregate<E> multiplyEach(final E multiplicant) {
        return this;
    }

    @Override
    public IDecimalAggregate<E> divideEach(final E divisor) {
        return this;
    }

    @Override
    public boolean isStableOrRisingEach() {
        return false;
    }

    @Override
    public boolean isStableOrFallingEach() {
        return false;
    }

    @Override
    public IDecimalAggregate<E> nullToZeroEach() {
        return this;
    }

    @Override
    public IDecimalAggregate<E> removeNullValues() {
        return this;
    }

    @Override
    public IDecimalAggregate<E> loessInterpolation(final LoessInterpolationConfig config) {
        return this;
    }

    @Override
    public IDecimalAggregate<E> bSplineInterpolation(final BSplineInterpolationConfig config) {
        return this;
    }

    @Override
    public IDecimalAggregate<E> cubicBSplineInterpolation(final InterpolationConfig config) {
        return this;
    }

    @Override
    public IDecimalAggregate<E> bezierCurveInterpolation(final InterpolationConfig config) {
        return this;
    }

    @Override
    public E coefficientOfVariation() {
        return null;
    }

    @Override
    public E sampleCoefficientOfVariation() {
        return null;
    }

    @Override
    public int count() {
        return 0;
    }

    @Override
    public Integer bestValueIndex(final boolean isHigherBetter) {
        return null;
    }

    @Override
    public IDecimalAggregate<E> normalize() {
        return this;
    }

    @Override
    public IDecimalAggregate<E> detrendAbsolute() {
        return this;
    }

    @Override
    public Iterator<E> randomizeShuffle(final RandomGenerator random) {
        return EmptyCloseableIterator.getInstance();
    }

    @Override
    public Iterator<E> randomizeBootstrap(final RandomGenerator random) {
        return EmptyCloseableIterator.getInstance();
    }

    @Override
    public Iterator<E> randomizeCircularBlockBootstrap(final RandomGenerator random) {
        return EmptyCloseableIterator.getInstance();
    }

    @Override
    public Iterator<E> randomizeStationaryBootstrap(final RandomGenerator random) {
        return EmptyCloseableIterator.getInstance();
    }

}
