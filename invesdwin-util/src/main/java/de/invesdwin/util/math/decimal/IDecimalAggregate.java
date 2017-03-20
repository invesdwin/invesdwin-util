package de.invesdwin.util.math.decimal;

import java.math.RoundingMode;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.math3.random.RandomGenerator;

import de.invesdwin.util.math.decimal.config.BSplineInterpolationConfig;
import de.invesdwin.util.math.decimal.config.InterpolationConfig;
import de.invesdwin.util.math.decimal.config.LoessInterpolationConfig;

public interface IDecimalAggregate<E extends ADecimal<E>> {

    /**
     * All growth rates separately
     */
    IDecimalAggregate<E> growthRates();

    /**
     * The average of all growthRates.
     */
    E growthRate();

    /**
     * The growthRate of the growthRates.
     */
    E growthRatesTrend();

    IDecimalAggregate<E> reverse();

    /**
     * Returns a weighted average where the first value has the least weight and the last value has the highest weight.
     */
    E avgWeightedAsc();

    /**
     * Returns a weighted average where the first value has the highest weight and the last value has the least weight.
     */
    E avgWeightedDesc();

    E sum();

    /**
     * x_quer = (x_1 + x_2 + ... + x_n) / n
     * 
     * @see <a href="http://de.wikipedia.org/wiki/Arithmetisches_Mittel">Source</a>
     */
    E avg();

    /**
     * Product = x_1 * x_2 * ... * x_n
     * 
     * @see <a href="http://de.wikipedia.org/wiki/Arithmetisches_Mittel">Source</a>
     */
    E product();

    /**
     * x_quer = (x_1 * x_2 * ... * x_n)^1/n
     * 
     * @see <a href="http://de.wikipedia.org/wiki/Geometrisches_Mittel">Source</a>
     * @see <a href="http://www.ee.ucl.ac.uk/~mflanaga/java/Stat.html#geom2">Source with BigDecimal</a>
     */
    E geomAvg();

    E max();

    E min();

    /**
     * distance = abs(max()-min())
     */
    E minMaxDistance();

    /**
     * s = (1/(n-1) * sum((x_i - x_quer)^2))^1/2
     */
    E sampleStandardDeviation();

    /**
     * s = (1/(n) * sum((x_i - x_quer)^2))^1/2
     */
    E standardDeviation();

    /**
     * s^2 = 1/(n-1) * sum((x_i - x_quer)^2)
     */
    E variance();

    /**
     * s^2 = 1/(n) * sum((x_i - x_quer)^2)
     * 
     * <a href="http://de.wikipedia.org/wiki/Stichprobenvarianz">Source</a>
     */
    E sampleVariance();

    List<E> values();

    IDecimalAggregate<E> round();

    IDecimalAggregate<E> round(final RoundingMode roundingMode);

    IDecimalAggregate<E> round(final int scale);

    IDecimalAggregate<E> round(final int scale, final RoundingMode roundingMode);

    IDecimalAggregate<E> roundToStep(final E step);

    IDecimalAggregate<E> roundToStep(final E step, final RoundingMode roundingMode);

    IDecimalAggregate<E> positiveValues();

    IDecimalAggregate<E> positiveNonZeroValues();

    IDecimalAggregate<E> negativeValues();

    IDecimalAggregate<E> negativeOrZeroValues();

    IDecimalAggregate<E> nonZeroValues();

    IDecimalAggregate<E> addEach(E augend);

    IDecimalAggregate<E> subtractEach(E subtrahend);

    IDecimalAggregate<E> multiplyEach(E multiplicant);

    IDecimalAggregate<E> divideEach(E divisor);

    IDecimalAggregate<E> nullToZeroEach();

    IDecimalAggregate<E> removeNullValues();

    /**
     * normalized(x) = (x-min(x))/(max(x)-min(x))
     */
    IDecimalAggregate<E> normalize();

    /**
     * avgChangeYperX = (toY - fromY) / (toX - fromX)
     * 
     * detrendedY(x,y) = y - ((x - fromX) * avgChangeYperX)
     */
    IDecimalAggregate<E> detrendAbsolute();

    IDecimalAggregate<E> detrendRelative();

    /**
     * From: 0,1,1,1,2,2,3
     * 
     * To: 0,1,2,3
     */
    IDecimalAggregate<E> removeFlatSequences();

    /**
     * True when each element is >= its previous element
     */
    boolean isStableOrRisingEach();

    /**
     * True when each element is <= its previous element
     */
    boolean isStableOrFallingEach();

    IDecimalAggregate<E> loessInterpolation(LoessInterpolationConfig config);

    IDecimalAggregate<E> bSplineInterpolation(BSplineInterpolationConfig config);

    IDecimalAggregate<E> cubicBSplineInterpolation(InterpolationConfig config);

    /**
     * bezier is fast O(n) but cannot calculate value sizes larger than 1030. You might want to consider to fallback to
     * an equivalent variation of BSpline with degree n, which sadly is exponentially slower with O(2^n) and might thus
     * not even complete in your lifetime. So we are automatically reducing the points by an averaging algorithm as a
     * preprocessing filter to get down to a maximum of 1000 points.
     */
    IDecimalAggregate<E> bezierCurveInterpolation(InterpolationConfig config);

    /**
     * CV(x) = stddev(x) / expectedValue(x)
     * 
     * Also known as relative standard deviation.
     */
    E coefficientOfVariation();

    /**
     * CV(x) = samplestddev(x) / expectedValue(x)
     * 
     * Also known as relative standard deviation.
     */
    E sampleCoefficientOfVariation();

    int size();

    Integer bestValueIndex(boolean isHigherBetter);

    /**
     * Randomized the values without replacement
     */
    Iterator<E> randomizeShuffle(RandomGenerator random);

    /**
     * Randomized the values with replacement, thus can draw the same values multiple times
     */
    Iterator<E> randomizeBootstrap(RandomGenerator random);

    /**
     * Randomize the values with replacement blockwise (for dependent data). Since the random generator is used less
     * often here (only per block), the actual performance here is better than that of the normal bootstrap.
     */
    Iterator<E> randomizeCircularBlockBootstrap(RandomGenerator random);

    /**
     * Randomize the values with replacement blockwise with randomized block length (for time series). Since the random
     * generator is used less often here (only per block), the actual performance here is better than that of the normal
     * bootstrap.
     */
    Iterator<E> randomizeStationaryBootstrap(RandomGenerator random);

    /**
     * Divides the given values into chunks (e.g. 1000 values in 4 chunks results in each chunk having 250 values).
     * These chunks will get an descending weight for being chosen as the basis for the next sample being taken (e.g.
     * with 40% probability it is chunk1, with 30% probability it is chunk2, with 20% probability it is chunk3 and with
     * 10% probability it is chunk4). The probabilities of the chunks with varying chunkCount is proportional to the
     * given example.
     */
    Iterator<E> randomizeWeightedChunksDescending(RandomGenerator random, int chunkCount);

    /**
     * Divides the given values into chunks (e.g. 1000 values in 4 chunks results in each chunk having 250 values).
     * These chunks will get an ascending weight for being chosen as the basis for the next sample being taken (e.g.
     * with 10% probability it is chunk1, with 20% probability it is chunk2, with 30% probability it is chunk3 and with
     * 40% probability it is chunk4). The probabilities of the chunks with varying chunkCount is proportional to the
     * given example.
     */
    Iterator<E> randomizeWeightedChunksAscending(RandomGenerator random, int chunkCount);

    E median();

    IDecimalAggregate<E> sortAscending();

    IDecimalAggregate<E> sortDescending();

    IDecimalAggregate<E> stopSequenceBeforeNegativeOrZero();

}
