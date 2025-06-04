package de.invesdwin.util.math.statistics.distribution;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.math3.stat.inference.KolmogorovSmirnovTest;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.decimal.scaled.Percent;
import de.invesdwin.util.math.decimal.scaled.PercentScale;

@NotThreadSafe
public abstract class AKolmogorovSmirnovTestComparator<E> extends ADistributionComparator<E> {

    private static final KolmogorovSmirnovTest KSTEST = new KolmogorovSmirnovTest();

    @Override
    public String getName() {
        return "Kolmogorov Smirnov Test";
    }

    @Override
    public String getRankName() {
        return "KSTestRank";
    }

    @Override
    public String getStatisticName() {
        return "KSTestStatistic";
    }

    @Override
    public String getConfidenceName() {
        return "KSTestConfidence";
    }

    protected abstract double[] getValues(E element);

    @Override
    public double getStatistic(final E o1, final E o2) {
        final double[] values1 = getValues(o1);
        final double[] values2 = getValues(o2);

        return newKolmogorovSmirnovStatistic(values1, values2);
    }

    @Override
    public Percent getProbabilityOfDifference(final E o1, final E o2) {
        final double[] values1 = getValues(o1);
        final double[] values2 = getValues(o2);

        return newProbabilityOfDifference(values1, values2);
    }

    /**
     * http://homework.uoregon.edu/pub/class/es202/ztest.html
     */
    public static double newKolmogorovSmirnovStatistic(final double[] values1, final double[] values2) {
        final double ksStatistic = comparableKolmogorovSmirnovStatistic(values1, values2);
        return Doubles.nonFiniteToZero(ksStatistic);
    }

    private static double comparableKolmogorovSmirnovStatistic(final double[] x, final double[] y) {
        return comparableIntegralKolmogorovSmirnovStatistic(x, y) / ((double) (x.length * (long) y.length));
    }

    /**
     * adapted algorithm to be able to compare the value, the abs(curD) is removed from the returned value
     */
    private static long comparableIntegralKolmogorovSmirnovStatistic(final double[] x, final double[] y) {
        // Copy and sort the sample arrays
        final double[] sx = Arrays.copyOf(x);
        final double[] sy = Arrays.copyOf(y);
        Arrays.sort(sx);
        Arrays.sort(sy);
        final int n = sx.length;
        final int m = sy.length;

        int rankX = 0;
        int rankY = 0;
        long curD = 0L;

        // Find the max difference between cdf_x and cdf_y
        long supD = 0L;
        long retD = 0L;
        do {
            final double z = Double.compare(sx[rankX], sy[rankY]) <= 0 ? sx[rankX] : sy[rankY];
            while (rankX < n && Double.compare(sx[rankX], z) == 0) {
                rankX += 1;
                curD += m;
            }
            while (rankY < m && Double.compare(sy[rankY], z) == 0) {
                rankY += 1;
                curD -= n;
            }
            if (curD > supD) {
                supD = curD;
                retD = curD;
            } else if (-curD > supD) {
                supD = -curD;
                retD = curD;
            }
        } while (rankX < n && rankY < m);
        return -retD;
    }

    /**
     * null-hypothesis that both samples are the same can be rejected at p-value 0.05 (5%)
     * 
     * probability of difference is the alternative-hypothesis which could be accepted at 95%
     */
    public static Percent newProbabilityOfDifference(final double[] values1, final double[] values2) {
        //        final double pValue = new KolmogorovSmirnov2Samples(values1, values2, Side.TWO_SIDED).pValue();
        //commons-math also tests two-sided
        final double pValue = KSTEST.kolmogorovSmirnovTest(values1, values2);
        return new Percent(1D - pValue, PercentScale.RATE);
    }

}
