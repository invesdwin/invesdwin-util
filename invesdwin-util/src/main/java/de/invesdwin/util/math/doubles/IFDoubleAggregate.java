package de.invesdwin.util.math.doubles;

import java.math.RoundingMode;
import java.util.List;

import de.invesdwin.util.math.doubles.scaled.FPercent;

public interface IFDoubleAggregate<E extends AFDouble<E>> {

    /**
     * All growth rates separately
     */
    IFDoubleAggregate<FPercent> growthRates();

    /**
     * The average of all growthRates.
     */
    FPercent growthRate();

    /**
     * The growthRate of the growthRates.
     */
    FPercent growthRatesTrend();

    IFDoubleAggregate<E> reverse();

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

    IFDoubleAggregate<E> round();

    IFDoubleAggregate<E> round(RoundingMode roundingMode);

    IFDoubleAggregate<E> round(int scale);

    IFDoubleAggregate<E> round(int scale, RoundingMode roundingMode);

    IFDoubleAggregate<E> roundToStep(E step);

    IFDoubleAggregate<E> roundToStep(E step, RoundingMode roundingMode);

    IFDoubleAggregate<E> positiveValues();

    IFDoubleAggregate<E> positiveNonZeroValues();

    IFDoubleAggregate<E> negativeValues();

    IFDoubleAggregate<E> negativeOrZeroValues();

    IFDoubleAggregate<E> nonZeroValues();

    IFDoubleAggregate<E> addEach(E augend);

    IFDoubleAggregate<E> subtractEach(E subtrahend);

    IFDoubleAggregate<E> multiplyEach(E multiplicant);

    IFDoubleAggregate<E> divideEach(E divisor);

    IFDoubleAggregate<E> nullToZeroEach();

    IFDoubleAggregate<E> removeNullValues();

    /**
     * normalized(x) = (x-min(x))/(max(x)-min(x))
     */
    IFDoubleAggregate<E> normalize();

    /**
     * avgChangeYperX = (toY - fromY) / (toX - fromX)
     * 
     * detrendedY(x,y) = y - ((x - fromX) * avgChangeYperX)
     */
    IFDoubleAggregate<E> detrendAbsolute();

    IFDoubleAggregate<E> detrendRelative();

    /**
     * From: 0,1,1,1,2,2,3
     * 
     * To: 0,1,2,3
     */
    IFDoubleAggregate<E> removeFlatSequences();

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

    /**
     * CV(x) = samplestddev(x) / expectedValue(x)
     * 
     * Also known as relative standard deviation.
     */
    E sampleCoefficientOfVariation();

    int size();

    Integer bestValueIndex(boolean isHigherBetter);

    E median();

    IFDoubleAggregate<E> sortAscending();

    IFDoubleAggregate<E> sortDescending();

    IFDoubleAggregate<E> stopSequenceBeforeNegativeOrZero();

    IFDoubleAggregate<FDouble> defaultValues();

}
