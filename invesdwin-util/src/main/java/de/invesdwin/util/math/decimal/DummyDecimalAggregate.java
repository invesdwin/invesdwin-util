package de.invesdwin.util.math.decimal;

import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;

import javax.annotation.concurrent.Immutable;

@Immutable
final class DummyDecimalAggregate<E extends ADecimal<E>> implements IDecimalAggregate<E> {

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
    public List<? extends E> values() {
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

}
