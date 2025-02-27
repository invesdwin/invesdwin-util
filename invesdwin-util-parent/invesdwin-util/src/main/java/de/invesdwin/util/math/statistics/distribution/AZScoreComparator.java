package de.invesdwin.util.math.statistics.distribution;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.math3.distribution.NormalDistribution;

import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.decimal.scaled.Percent;
import de.invesdwin.util.math.decimal.scaled.PercentScale;

@NotThreadSafe
public abstract class AZScoreComparator<E> extends ADistributionComparator<E> {

    private static final NormalDistribution ND = new NormalDistribution();

    @Override
    public String getName() {
        return "Z-Score";
    }

    @Override
    public String getRankName() {
        return "ZScoreRank";
    }

    @Override
    public String getStatisticName() {
        return "ZScoreStatistic";
    }

    @Override
    public String getConfidenceName() {
        return "ZScoreConfidence";
    }

    protected abstract int getCount(E element);

    protected abstract double getAvg(E element);

    protected abstract double getStdev(E element);

    @Override
    public double getStatistic(final E o1, final E o2) {
        final int count1 = getCount(o1);
        final int count2 = getCount(o2);
        final double mean1 = getAvg(o1);
        final double mean2 = getAvg(o2);
        final double stdev1 = getStdev(o1);
        final double stdev2 = getStdev(o2);
        return newZScore(count1, count2, mean1, mean2, stdev1, stdev2);
    }

    @Override
    public Percent getProbabilityOfDifference(final E o1, final E o2) {
        return newProbabilityOfDifference(getStatistic(o1, o2));
    }

    /**
     * http://homework.uoregon.edu/pub/class/es202/ztest.html
     */
    public static double newZScore(final int count1, final int count2, final double mean1, final double mean2,
            final double stdev1, final double stdev2) {
        //sigma1 = stdev1/sqrt(count1)
        final double sigma1 = Doubles.divide(stdev1, Doubles.sqrt(count1));
        //sigma2 = stdev2/sqrt(count2)
        final double sigma2 = Doubles.divide(stdev2, Doubles.sqrt(count2));
        //Z = (mean1 - mean2) / sqrt( sigma1^2 + sigma2^2  )
        final double meanDiff = mean1 - mean2;
        final double divisor = Doubles.sqrt(sigma1 * sigma1 + sigma2 * sigma2);
        final double z = Doubles.divide(meanDiff, divisor);
        return z;
    }

    /**
     * null-hypothesis that both samples are the same can be rejected at p-value 0.05 (5%)
     * 
     * probability of difference is the alternative-hypothesis which could be accepted at 95%
     */
    public static Percent newProbabilityOfDifference(final double zScore) {
        //we use a two sided test so that -3 and 3 result in the same probability of dependence which is 99.73
        final double probability = ND.probability(Doubles.abs(zScore) * -1, Doubles.abs(zScore));
        return new Percent(probability, PercentScale.RATE);
    }

}
