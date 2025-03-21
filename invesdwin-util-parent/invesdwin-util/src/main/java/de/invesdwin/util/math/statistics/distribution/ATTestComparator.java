package de.invesdwin.util.math.statistics.distribution;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.math3.stat.inference.TTest;

import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.decimal.scaled.Percent;
import de.invesdwin.util.math.decimal.scaled.PercentScale;

@NotThreadSafe
public abstract class ATTestComparator<E> extends ADistributionComparator<E> {

    private static final TTest TTEST = new TTest();

    @Override
    public String getName() {
        return "T-Test";
    }

    @Override
    public String getRankName() {
        return "TTestRank";
    }

    @Override
    public String getStatisticName() {
        return "TTestStatistic";
    }

    @Override
    public String getConfidenceName() {
        return "TTestConfidence";
    }

    protected abstract double[] getValues(E element, int maxCount);

    protected abstract int getCount(E element);

    public int getMaxCount(final E o1, final E o2) {
        final int count1 = getCount(o1);
        final int count2 = getCount(o2);
        final int maxCount = Integers.min(count1, count2);
        return maxCount;
    }

    @Override
    public double getStatistic(final E o1, final E o2) {
        final int maxCount = getMaxCount(o1, o2);

        final double[] values1 = getValues(o1, maxCount);
        final double[] values2 = getValues(o2, maxCount);

        return newTStatistic(values1, values2);
    }

    @Override
    public Percent getProbabilityOfDifference(final E o1, final E o2) {
        final int maxCount = getMaxCount(o1, o2);

        final double[] values1 = getValues(o1, maxCount);
        final double[] values2 = getValues(o2, maxCount);

        return newProbabilityOfDifference(values1, values2);
    }

    public static double newTStatistic(final double[] values1, final double[] values2) {
        final double tStatistic = TTEST.pairedT(values1, values2);
        return Doubles.nanToZero(tStatistic);
    }

    /**
     * null-hypothesis that both samples are the same can be rejected at p-value 0.05 (5%)
     * 
     * probability of difference is the alternative-hypothesis which could be accepted at 95%
     */
    public static Percent newProbabilityOfDifference(final double[] values1, final double[] values2) {
        //TTest is performed two-sided
        final double pValue = TTEST.pairedTTest(values1, values2);
        return new Percent(1D - pValue, PercentScale.RATE);
    }

}
