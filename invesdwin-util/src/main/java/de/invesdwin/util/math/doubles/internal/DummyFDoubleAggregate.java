package de.invesdwin.util.math.doubles.internal;

import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.doubles.AFDouble;
import de.invesdwin.util.math.doubles.FDouble;
import de.invesdwin.util.math.doubles.IFDoubleAggregate;
import de.invesdwin.util.math.doubles.scaled.FPercent;

@Immutable
public final class DummyFDoubleAggregate<E extends AFDouble<E>> implements IFDoubleAggregate<E> {

    @SuppressWarnings("rawtypes")
    private static final DummyFDoubleAggregate INSTANCE = new DummyFDoubleAggregate();

    private DummyFDoubleAggregate() {}

    @Override
    public IFDoubleAggregate<FPercent> growthRates() {
        return getInstance();
    }

    @Override
    public FPercent growthRate() {
        return null;
    }

    @Override
    public FPercent growthRatesTrend() {
        return null;
    }

    @Override
    public IFDoubleAggregate<E> reverse() {
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
    public static <T extends AFDouble<T>> IFDoubleAggregate<T> getInstance() {
        return INSTANCE;
    }

    @Override
    public List<E> values() {
        return Collections.emptyList();
    }

    @Override
    public IFDoubleAggregate<E> round() {
        return this;
    }

    @Override
    public IFDoubleAggregate<E> round(final RoundingMode roundingMode) {
        return this;
    }

    @Override
    public IFDoubleAggregate<E> round(final int scale) {
        return this;
    }

    @Override
    public IFDoubleAggregate<E> round(final int scale, final RoundingMode roundingMode) {
        return this;
    }

    @Override
    public IFDoubleAggregate<E> roundToStep(final E step) {
        return this;
    }

    @Override
    public IFDoubleAggregate<E> roundToStep(final E step, final RoundingMode roundingMode) {
        return this;
    }

    @Override
    public String toString() {
        return "[]";
    }

    @Override
    public IFDoubleAggregate<E> positiveValues() {
        return this;
    }

    @Override
    public IFDoubleAggregate<E> negativeValues() {
        return this;
    }

    @Override
    public IFDoubleAggregate<E> positiveNonZeroValues() {
        return this;
    }

    @Override
    public IFDoubleAggregate<E> negativeOrZeroValues() {
        return this;
    }

    @Override
    public IFDoubleAggregate<E> nonZeroValues() {
        return this;
    }

    @Override
    public IFDoubleAggregate<E> addEach(final E augend) {
        return this;
    }

    @Override
    public IFDoubleAggregate<E> subtractEach(final E subtrahend) {
        return this;
    }

    @Override
    public IFDoubleAggregate<E> multiplyEach(final E multiplicant) {
        return this;
    }

    @Override
    public IFDoubleAggregate<E> divideEach(final E divisor) {
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
    public IFDoubleAggregate<E> nullToZeroEach() {
        return this;
    }

    @Override
    public IFDoubleAggregate<E> removeNullValues() {
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
    public IFDoubleAggregate<E> normalize() {
        return this;
    }

    @Override
    public IFDoubleAggregate<E> detrendAbsolute() {
        return this;
    }

    @Override
    public IFDoubleAggregate<E> detrendRelative() {
        return this;
    }

    @Override
    public IFDoubleAggregate<E> removeFlatSequences() {
        return this;
    }

    @Override
    public E median() {
        return null;
    }

    @Override
    public IFDoubleAggregate<E> sortAscending() {
        return this;
    }

    @Override
    public IFDoubleAggregate<E> sortDescending() {
        return this;
    }

    @Override
    public IFDoubleAggregate<E> stopSequenceBeforeNegativeOrZero() {
        return this;
    }

    @Override
    public IFDoubleAggregate<FDouble> defaultValues() {
        return getInstance();
    }

}
