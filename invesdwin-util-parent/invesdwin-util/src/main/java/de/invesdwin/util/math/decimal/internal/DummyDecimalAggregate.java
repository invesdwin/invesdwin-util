package de.invesdwin.util.math.decimal.internal;

import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.math.decimal.IDecimalAggregate;
import de.invesdwin.util.math.decimal.internal.interpolations.DummyDecimalAggregateInterpolations;
import de.invesdwin.util.math.decimal.internal.randomizers.DummyDecimalAggregateRandomizers;
import de.invesdwin.util.math.decimal.interpolations.IDecimalAggregateInterpolations;
import de.invesdwin.util.math.decimal.randomizers.IDecimalAggregateRandomizers;
import de.invesdwin.util.math.decimal.scaled.Percent;

@Immutable
public final class DummyDecimalAggregate<E extends ADecimal<E>> implements IDecimalAggregate<E> {

    @SuppressWarnings("rawtypes")
    private static final DummyDecimalAggregate INSTANCE = new DummyDecimalAggregate();

    private DummyDecimalAggregate() {
    }

    @Override
    public IDecimalAggregate<Percent> growthRates() {
        return getInstance();
    }

    @Override
    public Percent growthRate() {
        return null;
    }

    @Override
    public Percent growthRatesTrend() {
        return null;
    }

    @Override
    public IDecimalAggregate<E> reverse() {
        return this;
    }

    @Override
    public E avgWeightedAsc() {
        return null;
    }

    @Override
    public E avgWeightedDesc() {
        return null;
    }

    @Override
    public E sum() {
        return null;
    }

    @Override
    public E avg() {
        return null;
    }

    @Override
    public E product() {
        return null;
    }

    @Override
    public E geomAvg() {
        return null;
    }

    @Override
    public E max() {
        return null;
    }

    @Override
    public E min() {
        return null;
    }

    @Override
    public E minMaxDistance() {
        return null;
    }

    @Override
    public E sampleStandardDeviation() {
        return null;
    }

    @Override
    public E standardDeviation() {
        return null;
    }

    @Override
    public E variance() {
        return null;
    }

    @Override
    public E sampleVariance() {
        return null;
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
    public IDecimalAggregate<E> removeZeroValues() {
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
    public int size() {
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
    public IDecimalAggregate<E> detrendRelative() {
        return this;
    }

    @Override
    public IDecimalAggregate<E> removeFlatSequences() {
        return this;
    }

    @Override
    public E median() {
        return null;
    }

    @Override
    public IDecimalAggregate<E> sortAscending() {
        return this;
    }

    @Override
    public IDecimalAggregate<E> sortDescending() {
        return this;
    }

    @Override
    public IDecimalAggregate<E> stopSequenceBeforeNegativeOrZero() {
        return this;
    }

    @Override
    public IDecimalAggregate<Decimal> defaultValues() {
        return getInstance();
    }

    @SuppressWarnings("unchecked")
    @Override
    public IDecimalAggregateRandomizers<E> randomize() {
        return DummyDecimalAggregateRandomizers.INSTANCE;
    }

    @Override
    public IDecimalAggregateInterpolations<E> interpolate() {
        return DummyDecimalAggregateInterpolations.INSTANCE;
    }

}
