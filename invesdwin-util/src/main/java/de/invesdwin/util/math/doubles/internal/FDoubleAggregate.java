package de.invesdwin.util.math.doubles.internal;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.Lists;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.iterable.WrapperCloseableIterable;
import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.math.doubles.AFDouble;
import de.invesdwin.util.math.doubles.FDouble;
import de.invesdwin.util.math.doubles.IFDoubleAggregate;
import de.invesdwin.util.math.doubles.scaled.FPercent;
import de.invesdwin.util.math.statistics.RunningMedian;
import de.invesdwin.util.math.stream.doubles.FDoublePoint;
import de.invesdwin.util.math.stream.doubles.FDoubleStreamAvg;
import de.invesdwin.util.math.stream.doubles.FDoubleStreamAvgWeightedAsc;
import de.invesdwin.util.math.stream.doubles.FDoubleStreamGeomAvg;
import de.invesdwin.util.math.stream.doubles.FDoubleStreamNormalization;
import de.invesdwin.util.math.stream.doubles.FDoubleStreamProduct;
import de.invesdwin.util.math.stream.doubles.FDoubleStreamRelativeDetrending;
import de.invesdwin.util.math.stream.doubles.FDoubleStreamRemoveFlatSequences;
import de.invesdwin.util.math.stream.doubles.FDoubleStreamStandardDeviation;
import de.invesdwin.util.math.stream.doubles.FDoubleStreamSum;
import de.invesdwin.util.math.stream.doubles.FDoubleStreamVariance;

@ThreadSafe
public class FDoubleAggregate<E extends AFDouble<E>> implements IFDoubleAggregate<E> {

    private E converter;
    private final List<E> values;

    public FDoubleAggregate(final List<? extends E> values, final E converter) {
        this.values = Collections.unmodifiableList(values);
        this.converter = converter;
    }

    public E getConverter() {
        if (converter == null) {
            for (final E scaledValue : values) {
                if (scaledValue != null) {
                    converter = scaledValue;
                    break;
                }
            }
            Assertions.checkNotNull(converter, "Please provide a converter manually via the appropriate constructor "
                    + "or make sure there is at least one non null value in the list.");
        }
        return converter;

    }

    /**
     * All growth rates separately
     */
    @Override
    public IFDoubleAggregate<FPercent> growthRates() {
        final List<FPercent> growthRates = new ArrayList<FPercent>(size());
        E previousValue = null;
        for (final E value : values) {
            if (previousValue != null) {
                growthRates.add(previousValue.growthRate(value));
            }
            previousValue = value;
        }
        return new FDoubleAggregate<FPercent>(growthRates, FPercent.ZERO_PERCENT);
    }

    public IFDoubleAggregate<E> absoluteChanges() {
        final List<E> differences = new ArrayList<E>(size());
        E previousValue = null;
        for (final E value : values) {
            if (previousValue != null) {
                differences.add(value.subtract(previousValue));
            }
            previousValue = value;
        }
        return new FDoubleAggregate<E>(differences, getConverter());
    }

    /**
     * The average of all growthRates.
     */
    @Override
    public FPercent growthRate() {
        return growthRates().avg();
    }

    /**
     * The growthRate of the growthRates.
     */
    @Override
    public FPercent growthRatesTrend() {
        return growthRates().growthRate();
    }

    @Override
    public IFDoubleAggregate<E> reverse() {
        return new FDoubleAggregate<E>(Lists.reverse(values), getConverter());
    }

    /**
     * Returns a weighted average where the first value has the least weight and the last value has the highest weight.
     */
    @Override
    public E avgWeightedAsc() {
        final FDoubleStreamAvgWeightedAsc<E> avgWeightedAsc = new FDoubleStreamAvgWeightedAsc<E>(getConverter());
        for (final E value : values) {
            avgWeightedAsc.process(value);
        }
        return avgWeightedAsc.getAvgWeightedAsc();
    }

    /**
     * Returns a weighted average where the first value has the highest weight and the last value has the least weight.
     */
    @Override
    public E avgWeightedDesc() {
        int sumOfWeights = 0;
        double sumOfWeightedValues = 0D;
        for (int i = 0, weight = size(); i < size(); i++, weight--) {
            final double weightedValue = values.get(i).getDefaultValue() * weight;
            sumOfWeights += weight;
            sumOfWeightedValues += weightedValue;
        }
        final double result = sumOfWeightedValues / sumOfWeights;
        return getConverter().fromDefaultValue(result);
    }

    @Override
    public E sum() {
        final FDoubleStreamSum<E> sum = new FDoubleStreamSum<E>(getConverter());
        for (final E value : values) {
            sum.process(value);
        }
        return sum.getSum();
    }

    /**
     * x_quer = (x_1 + x_2 + ... + x_n) / n
     * 
     * @see <a href="http://de.wikipedia.org/wiki/Arithmetisches_Mittel">Source</a>
     */
    @Override
    public E avg() {
        final FDoubleStreamAvg<E> avg = new FDoubleStreamAvg<E>(getConverter());
        for (final E value : values) {
            avg.process(value);
        }
        return avg.getAvg();
    }

    @Override
    public E median() {
        final RunningMedian runningMedian = new RunningMedian(values.size());
        for (int i = 0; i < values.size(); i++) {
            final E next = values.get(i);
            final double nextDouble = next.getDefaultValue();
            runningMedian.add(nextDouble);
        }
        final double median = runningMedian.getMedian();
        return getConverter().fromDefaultValue(median);
    }

    /**
     * Product = x_1 * x_2 * ... * x_n
     * 
     * @see <a href="http://de.wikipedia.org/wiki/Arithmetisches_Mittel">Source</a>
     */
    @Override
    public E product() {
        final FDoubleStreamProduct<E> product = new FDoubleStreamProduct<E>(getConverter());
        for (final E value : values) {
            product.process(value);
        }
        return product.getProduct();
    }

    /**
     * x_quer = (x_1 * x_2 * ... * x_n)^1/n
     * 
     * @see <a href="http://de.wikipedia.org/wiki/Geometrisches_Mittel">Source</a>
     * @see <a href="http://www.ee.ucl.ac.uk/~mflanaga/java/Stat.html#geom2">Source with BigDecimal</a>
     */
    @Override
    public E geomAvg() {
        final FDoubleStreamGeomAvg<E> geomAvg = new FDoubleStreamGeomAvg<E>(getConverter());
        for (final E value : values) {
            geomAvg.process(value);
        }
        return geomAvg.getGeomAvg();
    }

    @Override
    public E max() {
        E highest = null;
        for (final E value : values) {
            if (highest == null) {
                highest = value;
            } else if (value == null) {
                continue;
            } else if (highest.compareTo(value) < 0) {
                highest = value;
            }
        }
        return highest;
    }

    @Override
    public E min() {
        E lowest = null;
        for (final E value : values) {
            if (lowest == null) {
                lowest = value;
            } else if (value == null) {
                continue;
            } else if (value.compareTo(lowest) < 0) {
                lowest = value;
            }
        }
        return lowest;
    }

    @Override
    public E minMaxDistance() {
        final E min = min();
        if (min == null) {
            return null;
        }
        final E max = max();
        if (max == null) {
            return null;
        }
        return min.distance(max);
    }

    /**
     * s = (1/(n-1) * sum((x_i - x_quer)^2))^1/2
     */
    @Override
    public E sampleStandardDeviation() {
        final FDoubleStreamStandardDeviation<E> variance = new FDoubleStreamStandardDeviation<E>(getConverter());
        for (final E value : values) {
            variance.process(value);
        }
        return variance.getSampleStandardDeviation();
    }

    /**
     * s = (1/(n) * sum((x_i - x_quer)^2))^1/2
     * 
     * Warning: normally one will use the sampleCoefficientOfVariation since it is hard to come by a complete set of
     * values representing the distribution of reality
     */
    @Deprecated
    @Override
    public E standardDeviation() {
        final FDoubleStreamStandardDeviation<E> variance = new FDoubleStreamStandardDeviation<E>(getConverter());
        for (final E value : values) {
            variance.process(value);
        }
        return variance.getStandardDeviation();
    }

    /**
     * s^2 = 1/(n) * sum((x_i - x_quer)^2)
     * 
     * <a href="http://de.wikipedia.org/wiki/Stichprobenvarianz">Source</a>
     * 
     * Warning: normally one will use the sampleCoefficientOfVariation since it is hard to come by a complete set of
     * values representing the distribution of reality
     */
    @Deprecated
    @Override
    public E variance() {
        final FDoubleStreamVariance<E> variance = new FDoubleStreamVariance<E>(getConverter());
        for (final E value : values) {
            variance.process(value);
        }
        return variance.getVariance();
    }

    /**
     * s^2 = 1/(n-1) * sum((x_i - x_quer)^2)
     */
    @Override
    public E sampleVariance() {
        final FDoubleStreamVariance<E> variance = new FDoubleStreamVariance<E>(getConverter());
        for (final E value : values) {
            variance.process(value);
        }
        return variance.getSampleVariance();
    }

    /**
     * Warning: normally one will use the sampleCoefficientOfVariation since it is hard to come by a complete set of
     * values representing the distribution of reality
     */
    @Deprecated
    @Override
    public E coefficientOfVariation() {
        return standardDeviation().divide(avg());
    }

    @Override
    public E sampleCoefficientOfVariation() {
        return sampleStandardDeviation().divide(avg());
    }

    @Override
    public int size() {
        return values.size();
    }

    @Override
    public List<E> values() {
        return values;
    }

    @Override
    public IFDoubleAggregate<E> round() {
        return round(Decimal.DEFAULT_ROUNDING_SCALE);
    }

    @Override
    public IFDoubleAggregate<E> round(final RoundingMode roundingMode) {
        return round(Decimal.DEFAULT_ROUNDING_SCALE, roundingMode);
    }

    @Override
    public IFDoubleAggregate<E> round(final int scale) {
        return round(scale, Decimal.DEFAULT_ROUNDING_MODE);
    }

    @Override
    public IFDoubleAggregate<E> round(final int scale, final RoundingMode roundingMode) {
        final List<E> rounded = new ArrayList<E>(size());
        for (final E value : values) {
            rounded.add(value.round(scale, roundingMode));
        }
        return new FDoubleAggregate<E>(rounded, getConverter());
    }

    @Override
    public IFDoubleAggregate<E> roundToStep(final E step) {
        return roundToStep(step, Decimal.DEFAULT_ROUNDING_MODE);
    }

    @Override
    public IFDoubleAggregate<E> roundToStep(final E step, final RoundingMode roundingMode) {
        final List<E> rounded = new ArrayList<E>(size());
        for (final E value : values) {
            rounded.add(value.roundToStep(step, roundingMode));
        }
        return new FDoubleAggregate<E>(rounded, getConverter());
    }

    @Override
    public String toString() {
        return values.toString();
    }

    @Override
    public IFDoubleAggregate<E> positiveValues() {
        final List<E> positives = new ArrayList<E>(size());
        for (final E value : values) {
            if (value.isPositive()) {
                positives.add(value);
            }
        }
        return new FDoubleAggregate<E>(positives, getConverter());
    }

    @Override
    public IFDoubleAggregate<E> positiveNonZeroValues() {
        final List<E> positives = new ArrayList<E>(size());
        for (final E value : values) {
            if (value.isPositiveNonZero()) {
                positives.add(value);
            }
        }
        return new FDoubleAggregate<E>(positives, getConverter());
    }

    @Override
    public IFDoubleAggregate<E> negativeValues() {
        final List<E> negatives = new ArrayList<E>(size());
        for (final E value : values) {
            if (value.isNegative()) {
                negatives.add(value);
            }
        }
        return new FDoubleAggregate<E>(negatives, getConverter());
    }

    @Override
    public IFDoubleAggregate<E> negativeOrZeroValues() {
        final List<E> negatives = new ArrayList<E>(size());
        for (final E value : values) {
            if (value.isNegativeOrZero()) {
                negatives.add(value);
            }
        }
        return new FDoubleAggregate<E>(negatives, getConverter());
    }

    @Override
    public IFDoubleAggregate<E> nonZeroValues() {
        final List<E> nonZeros = new ArrayList<E>(size());
        for (final E value : values) {
            if (value.isNotZero()) {
                nonZeros.add(value);
            }
        }
        return new FDoubleAggregate<E>(nonZeros, getConverter());

    }

    @Override
    public IFDoubleAggregate<E> addEach(final E augend) {
        final List<E> added = new ArrayList<E>(size());
        for (final E value : values) {
            added.add(value.add(augend));
        }
        return new FDoubleAggregate<E>(added, getConverter());
    }

    @Override
    public IFDoubleAggregate<E> subtractEach(final E subtrahend) {
        final List<E> subtracted = new ArrayList<E>(size());
        for (final E value : values) {
            subtracted.add(value.subtract(subtrahend));
        }
        return new FDoubleAggregate<E>(subtracted, getConverter());
    }

    @Override
    public IFDoubleAggregate<E> multiplyEach(final E multiplicant) {
        final List<E> multiplied = new ArrayList<E>(size());
        for (final E value : values) {
            multiplied.add(value.add(multiplicant));
        }
        return new FDoubleAggregate<E>(multiplied, getConverter());
    }

    @Override
    public IFDoubleAggregate<E> divideEach(final E divisor) {
        final List<E> divided = new ArrayList<E>(size());
        for (final E value : values) {
            divided.add(value.add(divisor));
        }
        return new FDoubleAggregate<E>(divided, getConverter());
    }

    @Override
    public IFDoubleAggregate<E> nullToZeroEach() {
        final List<E> replaced = new ArrayList<E>(size());
        final E zero = getConverter().zero();
        for (final E value : values) {
            if (value != null) {
                replaced.add(value);
            } else {
                replaced.add(zero);
            }
        }
        return new FDoubleAggregate<E>(replaced, getConverter());
    }

    @Override
    public IFDoubleAggregate<E> removeNullValues() {
        final List<E> filtered = new ArrayList<E>(size());
        for (final E value : values) {
            if (value != null) {
                filtered.add(value);
            }
        }
        return new FDoubleAggregate<E>(filtered, getConverter());
    }

    @Override
    public boolean isStableOrRisingEach() {
        E prevValue = null;
        for (final E value : values) {
            if (prevValue != null) {
                if (value.isLessThan(prevValue)) {
                    return false;
                }
            }
            prevValue = value;
        }
        return true;
    }

    @Override
    public boolean isStableOrFallingEach() {
        E prevValue = null;
        for (final E value : values) {
            if (prevValue != null) {
                if (value.isGreaterThan(prevValue)) {
                    return false;
                }
            }
            prevValue = value;
        }
        return true;
    }

    @Override
    public Integer bestValueIndex(final boolean isHigherBetter) {
        E bestValue = null;
        Integer bestValueIndex = null;
        for (int i = 0; i < values.size(); i++) {
            final E value = values.get(i);
            if (bestValue == null) {
                bestValue = value;
                bestValueIndex = i;
            } else if (isHigherBetter) {
                if (value.isGreaterThan(bestValue)) {
                    bestValue = value;
                    bestValueIndex = i;
                }
            } else {
                if (value.isLessThan(bestValue)) {
                    bestValue = value;
                    bestValueIndex = i;
                }
            }
        }
        return bestValueIndex;
    }

    @Override
    public IFDoubleAggregate<E> normalize() {
        if (size() < 2) {
            return this;
        }
        final FDoubleStreamNormalization<E> normalization = new FDoubleStreamNormalization<E>(min(), max());
        final List<E> results = new ArrayList<E>(size());
        for (final E value : values) {
            results.add(normalization.process(value));
        }
        return new FDoubleAggregate<E>(results, getConverter());
    }

    @Override
    public IFDoubleAggregate<E> detrendAbsolute() {
        if (size() < 3) {
            return this;
        }
        final E firstValue = values.get(0);
        final E lastValue = values.get(size() - 1);
        final E avgChange = lastValue.subtract(firstValue).divide(size() - 1);
        final List<E> detrendedValues = new ArrayList<E>(size());
        for (int i = 0; i < values.size(); i++) {
            final E value = values.get(i);
            final E detrendedValue = value.subtract(avgChange.multiply(i));
            detrendedValues.add(detrendedValue);
        }
        return new FDoubleAggregate<E>(detrendedValues, getConverter());
    }

    @Override
    public IFDoubleAggregate<E> detrendRelative() {
        if (size() < 3) {
            return this;
        }
        final FDoublePoint<FDouble, E> from = new FDoublePoint<FDouble, E>(FDouble.ZERO, values.get(0));
        final FDoublePoint<FDouble, E> to = new FDoublePoint<FDouble, E>(new FDouble(size()),
                values.get(values.size() - 1));
        final FDoubleStreamRelativeDetrending<E> detrending = new FDoubleStreamRelativeDetrending<E>(from, to);
        final List<E> results = new ArrayList<E>(size());
        for (int i = 0; i < size(); i++) {
            final FDoublePoint<FDouble, E> value = new FDoublePoint<FDouble, E>(new FDouble(i), values.get(i));
            final FDoublePoint<FDouble, E> detrendedValue = detrending.process(value);
            results.add(detrendedValue.getY());
        }
        return new FDoubleAggregate<E>(results, getConverter());
    }

    @Override
    public IFDoubleAggregate<E> removeFlatSequences() {
        final ICloseableIterator<E> removeFlatSequences = new FDoubleStreamRemoveFlatSequences<E>()
                .asIterator(WrapperCloseableIterable.maybeWrap(values).iterator());
        final List<E> deflattened = Lists.toListWithoutHasNext(removeFlatSequences, new ArrayList<E>(size()));
        return new FDoubleAggregate<E>(deflattened, getConverter());
    }

    @Override
    public IFDoubleAggregate<E> stopSequenceBeforeNegativeOrZero() {
        for (int i = 0; i < values.size(); i++) {
            final E event = values.get(i);
            if (event.isNegativeOrZero()) {
                final List<E> subList = values.subList(0, i - 1);
                return new FDoubleAggregate<E>(subList, getConverter());
            }
        }
        return this;
    }

    @Override
    public IFDoubleAggregate<E> sortAscending() {
        final List<E> sorted = new ArrayList<E>(values);
        FDouble.COMPARATOR.sortAscending(sorted);
        return new FDoubleAggregate<E>(sorted, getConverter());
    }

    @Override
    public IFDoubleAggregate<E> sortDescending() {
        final List<E> sorted = new ArrayList<E>(values);
        FDouble.COMPARATOR.sortDescending(sorted);
        return new FDoubleAggregate<E>(sorted, getConverter());
    }

    @Override
    public IFDoubleAggregate<FDouble> defaultValues() {
        final List<FDouble> defaultValues = new ArrayList<FDouble>(values.size());
        for (int i = 0; i < size(); i++) {
            defaultValues.add(FDouble.valueOf(values.get(i).getDefaultValue()));
        }
        return new FDoubleAggregate<FDouble>(defaultValues, FDouble.ZERO);
    }

}
