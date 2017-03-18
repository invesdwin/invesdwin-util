package de.invesdwin.util.math.decimal.internal;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.stat.descriptive.rank.Median;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.Lists;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.iterable.WrapperCloseableIterable;
import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.math.decimal.IDecimalAggregate;
import de.invesdwin.util.math.decimal.config.BSplineInterpolationConfig;
import de.invesdwin.util.math.decimal.config.InterpolationConfig;
import de.invesdwin.util.math.decimal.config.LoessInterpolationConfig;
import de.invesdwin.util.math.decimal.stream.DecimalPoint;
import de.invesdwin.util.math.decimal.stream.DecimalStreamAvg;
import de.invesdwin.util.math.decimal.stream.DecimalStreamAvgWeightedAsc;
import de.invesdwin.util.math.decimal.stream.DecimalStreamGeomAvg;
import de.invesdwin.util.math.decimal.stream.DecimalStreamNormalization;
import de.invesdwin.util.math.decimal.stream.DecimalStreamRelativeDetrending;
import de.invesdwin.util.math.decimal.stream.DecimalStreamRemoveFlatSequences;
import de.invesdwin.util.math.decimal.stream.DecimalStreamSum;

@ThreadSafe
public class DecimalAggregate<E extends ADecimal<E>> implements IDecimalAggregate<E> {

    private E converter;
    private final List<E> values;
    private final DecimalAggregateRandomizers<E> bootstraps = new DecimalAggregateRandomizers<E>(this);

    public DecimalAggregate(final List<? extends E> values, final E converter) {
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
    public IDecimalAggregate<E> growthRates() {
        final List<E> growthRates = new ArrayList<E>(size());
        E previousValue = null;
        for (final E value : values) {
            if (previousValue != null) {
                growthRates.add(previousValue.growthRate(value));
            }
            previousValue = value;
        }
        return new DecimalAggregate<E>(growthRates, getConverter());
    }

    public IDecimalAggregate<E> absoluteChanges() {
        final List<E> differences = new ArrayList<E>(size());
        E previousValue = null;
        for (final E value : values) {
            if (previousValue != null) {
                differences.add(value.subtract(previousValue));
            }
            previousValue = value;
        }
        return new DecimalAggregate<E>(differences, getConverter());
    }

    /**
     * The average of all growthRates.
     */
    @Override
    public E growthRate() {
        return growthRates().avg();
    }

    /**
     * The growthRate of the growthRates.
     */
    @Override
    public E growthRatesTrend() {
        return growthRates().growthRate();
    }

    @Override
    public IDecimalAggregate<E> reverse() {
        return new DecimalAggregate<E>(Lists.reverse(values), getConverter());
    }

    /**
     * Returns a weighted average where the first value has the least weight and the last value has the highest weight.
     */
    @Override
    public E avgWeightedAsc() {
        final DecimalStreamAvgWeightedAsc<E> avgWeightedAsc = new DecimalStreamAvgWeightedAsc<E>(getConverter());
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
        Decimal sumOfWeightedValues = Decimal.ZERO;
        for (int i = 0, weight = size(); i < size(); i++, weight--) {
            final Decimal weightedValue = values.get(i).getDefaultValue().multiply(weight);
            sumOfWeights += weight;
            sumOfWeightedValues = sumOfWeightedValues.add(weightedValue);
        }
        return getConverter().fromDefaultValue(sumOfWeightedValues.divide(sumOfWeights));
    }

    @Override
    public E sum() {
        final DecimalStreamSum<E> sum = new DecimalStreamSum<E>(getConverter());
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
        final DecimalStreamAvg<E> sum = new DecimalStreamAvg<E>(getConverter());
        for (final E value : values) {
            sum.process(value);
        }
        return sum.getAvg();
    }

    @Override
    public E median() {
        final double[] doubleValues = new double[values.size()];
        for (int i = 0; i < values.size(); i++) {
            final E next = values.get(i);
            doubleValues[i] = next.getDefaultValue().doubleValueRaw();
        }
        final Median medianAlgo = new Median();
        final double median = medianAlgo.evaluate(doubleValues);
        return getConverter().fromDefaultValue(new Decimal(median));
    }

    /**
     * Product = x_1 * x_2 * ... * x_n
     * 
     * @see <a href="http://de.wikipedia.org/wiki/Arithmetisches_Mittel">Source</a>
     */
    @Override
    public E product() {
        Decimal product = Decimal.ONE;
        for (final E value : values) {
            product = product.multiply(value.getDefaultValue());
        }
        return getConverter().fromDefaultValue(product);
    }

    /**
     * x_quer = (x_1 * x_2 * ... * x_n)^1/n
     * 
     * @see <a href="http://de.wikipedia.org/wiki/Geometrisches_Mittel">Source</a>
     * @see <a href="http://www.ee.ucl.ac.uk/~mflanaga/java/Stat.html#geom2">Source with BigDecimal</a>
     */
    @Override
    public E geomAvg() {
        final DecimalStreamGeomAvg<E> geomAvg = new DecimalStreamGeomAvg<E>(getConverter());
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
        final E avg = avg();
        Decimal sum = Decimal.ZERO;
        for (final E value : values) {
            sum = sum.add(value.subtract(avg).getDefaultValue().pow(2));
        }
        return getConverter().fromDefaultValue(sum.divide(size() - 1).sqrt());
    }

    /**
     * s = (1/(n) * sum((x_i - x_quer)^2))^1/2
     */
    @Override
    public E standardDeviation() {
        final E avg = avg();
        Decimal sum = Decimal.ZERO;
        for (final E value : values) {
            sum = sum.add(value.subtract(avg).getDefaultValue().pow(2));
        }
        return getConverter().fromDefaultValue(sum.divide(size()).sqrt());
    }

    /**
     * s^2 = 1/(n-1) * sum((x_i - x_quer)^2)
     */
    @Override
    public E variance() {
        final E avg = avg();
        Decimal sum = Decimal.ZERO;
        for (final E value : values) {
            sum = sum.add(value.subtract(avg).getDefaultValue().pow(2));
        }
        return getConverter().fromDefaultValue(sum.divide(size() - 1));
    }

    /**
     * s^2 = 1/(n) * sum((x_i - x_quer)^2)
     * 
     * <a href="http://de.wikipedia.org/wiki/Stichprobenvarianz">Source</a>
     */
    @Override
    public E sampleVariance() {
        final E avg = avg();
        Decimal sum = Decimal.ZERO;
        for (final E value : values) {
            sum = sum.add(value.subtract(avg).getDefaultValue().pow(2));
        }
        return getConverter().fromDefaultValue(sum.divide(size()));
    }

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
    public IDecimalAggregate<E> bSplineInterpolation(final BSplineInterpolationConfig config) {
        return new DecimalAggregateInterpolations<E>(this).bSplineInterpolation(config);
    }

    @Override
    public IDecimalAggregate<E> loessInterpolation(final LoessInterpolationConfig config) {
        return new DecimalAggregateInterpolations<E>(this).loessInterpolation(config);
    }

    @Override
    public IDecimalAggregate<E> cubicBSplineInterpolation(final InterpolationConfig config) {
        return new DecimalAggregateInterpolations<E>(this).cubicBSplineInterpolation(config);
    }

    @Override
    public IDecimalAggregate<E> bezierCurveInterpolation(final InterpolationConfig config) {
        return new DecimalAggregateInterpolations<E>(this).bezierCurveInterpolation(config);
    }

    @Override
    public List<E> values() {
        return values;
    }

    @Override
    public IDecimalAggregate<E> round() {
        return round(Decimal.DEFAULT_ROUNDING_SCALE);
    }

    @Override
    public IDecimalAggregate<E> round(final RoundingMode roundingMode) {
        return round(Decimal.DEFAULT_ROUNDING_SCALE, roundingMode);
    }

    @Override
    public IDecimalAggregate<E> round(final int scale) {
        return round(scale, Decimal.DEFAULT_ROUNDING_MODE);
    }

    @Override
    public IDecimalAggregate<E> round(final int scale, final RoundingMode roundingMode) {
        final List<E> rounded = new ArrayList<E>(size());
        for (final E value : values) {
            rounded.add(value.round(scale, roundingMode));
        }
        return new DecimalAggregate<E>(rounded, getConverter());
    }

    @Override
    public IDecimalAggregate<E> roundToStep(final E step) {
        return roundToStep(step, Decimal.DEFAULT_ROUNDING_MODE);
    }

    @Override
    public IDecimalAggregate<E> roundToStep(final E step, final RoundingMode roundingMode) {
        final List<E> rounded = new ArrayList<E>(size());
        for (final E value : values) {
            rounded.add(value.roundToStep(step, roundingMode));
        }
        return new DecimalAggregate<E>(rounded, getConverter());
    }

    @Override
    public String toString() {
        return values.toString();
    }

    @Override
    public IDecimalAggregate<E> positiveValues() {
        final List<E> positives = new ArrayList<E>(size());
        for (final E value : values) {
            if (value.isPositive()) {
                positives.add(value);
            }
        }
        return new DecimalAggregate<E>(positives, getConverter());
    }

    @Override
    public IDecimalAggregate<E> positiveNonZeroValues() {
        final List<E> positives = new ArrayList<E>(size());
        for (final E value : values) {
            if (value.isPositiveNonZero()) {
                positives.add(value);
            }
        }
        return new DecimalAggregate<E>(positives, getConverter());
    }

    @Override
    public IDecimalAggregate<E> negativeValues() {
        final List<E> negatives = new ArrayList<E>(size());
        for (final E value : values) {
            if (value.isNegative()) {
                negatives.add(value);
            }
        }
        return new DecimalAggregate<E>(negatives, getConverter());
    }

    @Override
    public IDecimalAggregate<E> negativeOrZeroValues() {
        final List<E> negatives = new ArrayList<E>(size());
        for (final E value : values) {
            if (value.isNegativeOrZero()) {
                negatives.add(value);
            }
        }
        return new DecimalAggregate<E>(negatives, getConverter());
    }

    @Override
    public IDecimalAggregate<E> nonZeroValues() {
        final List<E> nonZeros = new ArrayList<E>(size());
        for (final E value : values) {
            if (value.isNotZero()) {
                nonZeros.add(value);
            }
        }
        return new DecimalAggregate<E>(nonZeros, getConverter());

    }

    @Override
    public IDecimalAggregate<E> addEach(final E augend) {
        final List<E> added = new ArrayList<E>(size());
        for (final E value : values) {
            added.add(value.add(augend));
        }
        return new DecimalAggregate<E>(added, getConverter());
    }

    @Override
    public IDecimalAggregate<E> subtractEach(final E subtrahend) {
        final List<E> subtracted = new ArrayList<E>(size());
        for (final E value : values) {
            subtracted.add(value.subtract(subtrahend));
        }
        return new DecimalAggregate<E>(subtracted, getConverter());
    }

    @Override
    public IDecimalAggregate<E> multiplyEach(final E multiplicant) {
        final List<E> multiplied = new ArrayList<E>(size());
        for (final E value : values) {
            multiplied.add(value.add(multiplicant));
        }
        return new DecimalAggregate<E>(multiplied, getConverter());
    }

    @Override
    public IDecimalAggregate<E> divideEach(final E divisor) {
        final List<E> divided = new ArrayList<E>(size());
        for (final E value : values) {
            divided.add(value.add(divisor));
        }
        return new DecimalAggregate<E>(divided, getConverter());
    }

    @Override
    public IDecimalAggregate<E> nullToZeroEach() {
        final List<E> replaced = new ArrayList<E>(size());
        final E zero = getConverter().zero();
        for (final E value : values) {
            if (value != null) {
                replaced.add(value);
            } else {
                replaced.add(zero);
            }
        }
        return new DecimalAggregate<E>(replaced, getConverter());
    }

    @Override
    public IDecimalAggregate<E> removeNullValues() {
        final List<E> filtered = new ArrayList<E>(size());
        for (final E value : values) {
            if (value != null) {
                filtered.add(value);
            }
        }
        return new DecimalAggregate<E>(filtered, getConverter());
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
    public IDecimalAggregate<E> normalize() {
        if (size() < 2) {
            return this;
        }
        final DecimalStreamNormalization<E> normalization = new DecimalStreamNormalization<E>(min(), max());
        final List<E> results = new ArrayList<E>(size());
        for (final E value : values) {
            results.add(normalization.process(value));
        }
        return new DecimalAggregate<E>(results, getConverter());
    }

    @Override
    public IDecimalAggregate<E> detrendAbsolute() {
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
        return new DecimalAggregate<E>(detrendedValues, getConverter());
    }

    @Override
    public IDecimalAggregate<E> detrendRelative() {
        if (size() < 3) {
            return this;
        }
        final DecimalPoint<Decimal, E> from = new DecimalPoint<Decimal, E>(Decimal.ZERO, values.get(0));
        final DecimalPoint<Decimal, E> to = new DecimalPoint<Decimal, E>(new Decimal(size()),
                values.get(values.size() - 1));
        final DecimalStreamRelativeDetrending<E> detrending = new DecimalStreamRelativeDetrending<E>(from, to);
        final List<E> results = new ArrayList<E>(size());
        for (int i = 0; i < size(); i++) {
            final DecimalPoint<Decimal, E> value = new DecimalPoint<Decimal, E>(new Decimal(i), values.get(i));
            final DecimalPoint<Decimal, E> detrendedValue = detrending.process(value);
            results.add(detrendedValue.getY());
        }
        return new DecimalAggregate<E>(results, getConverter());
    }

    @Override
    public IDecimalAggregate<E> removeFlatSequences() {
        final ICloseableIterator<E> removeFlatSequences = new DecimalStreamRemoveFlatSequences<E>()
                .asIterator(WrapperCloseableIterable.maybeWrap(values).iterator());
        final List<E> deflattened = Lists.toListWithoutHasNext(removeFlatSequences, new ArrayList<E>(size()));
        return new DecimalAggregate<E>(deflattened, getConverter());
    }

    @Override
    public IDecimalAggregate<E> sortAscending() {
        final List<E> sorted = new ArrayList<E>(values);
        Decimal.COMPARATOR.sortAscending(sorted);
        return new DecimalAggregate<E>(sorted, getConverter());
    }

    @Override
    public IDecimalAggregate<E> sortDescending() {
        final List<E> sorted = new ArrayList<E>(values);
        Decimal.COMPARATOR.sortDescending(sorted);
        return new DecimalAggregate<E>(sorted, getConverter());
    }

    @Override
    public Iterator<E> randomizeShuffle(final RandomGenerator random) {
        return bootstraps.randomizeShuffle(random);
    }

    @Override
    public Iterator<E> randomizeWeightedChunksAscending(final RandomGenerator random, final int chunkCount) {
        return bootstraps.randomizeWeightedChunksAscending(random, chunkCount);
    }

    @Override
    public Iterator<E> randomizeWeightedChunksDescending(final RandomGenerator random, final int chunkCount) {
        return bootstraps.randomizeWeightedChunksDescending(random, chunkCount);
    }

    @Override
    public Iterator<E> randomizeBootstrap(final RandomGenerator random) {
        return bootstraps.randomizeBootstrap(random);
    }

    @Override
    public Iterator<E> randomizeCircularBlockBootstrap(final RandomGenerator random) {
        return bootstraps.randomizeCircularBootstrap(random);
    }

    @Override
    public Iterator<E> randomizeStationaryBootstrap(final RandomGenerator random) {
        return bootstraps.randomizeStationaryBootstrap(random);
    }

}
