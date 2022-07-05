package de.invesdwin.util.math.decimal;

import java.math.RoundingMode;
import java.util.List;

import de.invesdwin.util.math.decimal.interpolations.IDecimalAggregateInterpolations;
import de.invesdwin.util.math.decimal.randomizers.IDecimalAggregateRandomizers;
import de.invesdwin.util.math.decimal.scaled.Percent;

public interface IDecimalAggregate<E extends ADecimal<E>> {

    /**
     * All growth rates separately
     */
    IDecimalAggregate<Percent> growthRates();

    /**
     * The average of all growthRates.
     */
    Percent growthRate();

    /**
     * The growthRate of the growthRates.
     */
    Percent growthRatesTrend();

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
     * 
     * Warning: normally one will use the sampleCoefficientOfVariation since it is hard to come by a complete set of
     * values representing the distribution of reality
     */
    @Deprecated
    E standardDeviation();

    /**
     * s^2 = 1/(n-1) * sum((x_i - x_quer)^2)
     * 
     * Warning: normally one will use the sampleCoefficientOfVariation since it is hard to come by a complete set of
     * values representing the distribution of reality
     */
    @Deprecated
    E variance();

    /**
     * s^2 = 1/(n) * sum((x_i - x_quer)^2)
     * 
     * <a href="http://de.wikipedia.org/wiki/Stichprobenvarianz">Source</a>
     */
    E sampleVariance();

    List<E> values();

    IDecimalAggregate<E> round();

    IDecimalAggregate<E> round(RoundingMode roundingMode);

    IDecimalAggregate<E> round(int scale);

    IDecimalAggregate<E> round(int scale, RoundingMode roundingMode);

    IDecimalAggregate<E> roundToStep(E step);

    IDecimalAggregate<E> roundToStep(E step, RoundingMode roundingMode);

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

    IDecimalAggregate<E> removeZeroValues();

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

    /**
     * CV(x) = stddev(x) / expectedValue(x)
     * 
     * Also known as relative standard deviation.
     * 
     * Warning: normally one will use the sampleCoefficientOfVariation since it is hard to come by a complete set of
     * values representing the distribution of reality
     */
    @Deprecated
    E coefficientOfVariation();

    @Deprecated
    default E relativeStandardDeviation() {
        return coefficientOfVariation();
    }

    /**
     * CV(x) = samplestddev(x) / expectedValue(x)
     * 
     * Also known as relative standard deviation.
     */
    E sampleCoefficientOfVariation();

    default E sampleRelativeStandardDeviation() {
        return sampleCoefficientOfVariation();
    }

    int size();

    Integer bestValueIndex(boolean isHigherBetter);

    E median();

    IDecimalAggregate<E> sortAscending();

    IDecimalAggregate<E> sortDescending();

    IDecimalAggregate<E> stopSequenceBeforeNegativeOrZero();

    IDecimalAggregate<Decimal> defaultValues();

    IDecimalAggregateInterpolations<E> interpolate();

    IDecimalAggregateRandomizers<E> randomize();

}
