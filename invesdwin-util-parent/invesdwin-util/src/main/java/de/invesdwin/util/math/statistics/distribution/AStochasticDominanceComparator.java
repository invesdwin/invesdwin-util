package de.invesdwin.util.math.statistics.distribution;

import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.lang.comparator.AComparator;
import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.decimal.scaled.Percent;

@Immutable
public abstract class AStochasticDominanceComparator<E> extends AComparator<E> {

    public enum StochasticDominanceOrder {
        //Descending is better
        _1(1),
        //Ascending is better
        _2(2),
        _3(3),
        _4(4);

        private final int order;

        StochasticDominanceOrder(final int order) {
            this.order = order;
        }

        public int getOrder() {
            return order;
        }

        public static StochasticDominanceOrder valueOfOrder(final int order) {
            switch (order) {
            case 1:
                return _1;
            case 2:
                return _2;
            case 3:
                return _3;
            case 4:
                return _4;
            default:
                throw UnknownArgumentException.newInstance(int.class, order);
            }
        }
    }

    protected abstract List<? extends Percent> getHoldingPeriodReturns(E element);

    protected abstract StochasticDominanceOrder getDominanceOrder();

    @Override
    public int compareTypedNotNullSafe(final E o1, final E o2) {
        return compare(getHoldingPeriodReturns(o1), getHoldingPeriodReturns(o2), getDominanceOrder());
    }

    private static double[] toReturns(final List<? extends Percent> hprs) {
        final double[] returns = new double[hprs.size()];
        for (int i = 0; i < returns.length; i++) {
            returns[i] = hprs.get(i).getRate();
        }
        return returns;
    }

    public static int compare(final List<? extends Percent> thisHoldingPeriodReturns,
            final List<? extends Percent> otherHoldingPeriodReturns, final StochasticDominanceOrder dominanceOrder) {
        final double[] thisReturns = toReturns(thisHoldingPeriodReturns);
        final double[] otherReturns = toReturns(otherHoldingPeriodReturns);
        final int order = dominanceOrder.getOrder();
        return compare(thisReturns, otherReturns, order);
    }

    /**
     * It does not matter if returns are given as 1.1 or 0.1 for 10%. It only needs to be consistent. Also x and y don't
     * require the same number of rows.
     */
    public static int compare(final double[] xReturns, final double[] yReturns, final int dominanceOrder) {
        if (Arrays.equals(xReturns, yReturns)) {
            return 0;
        }
        final double sd = calculateSd(xReturns, yReturns, dominanceOrder);
        return compareSd(sd);
    }

    static int compareSd(final double sdb) {
        //we have to invert the sign, thus compare 0 against sdb
        // if sdb > 0 => return -1 (x does not dominate y)
        // if sdb < 0 => return 1 (x dominates y)
        return Doubles.compare(0D, sdb);
    }

    static double calculateSd(final double[] xReturns, final double[] yReturns, final int dominanceOrder) {
        if (dominanceOrder < 1 || dominanceOrder > 4) {
            throw new IllegalArgumentException("dominanceOrder has to be between 1 and 4: " + dominanceOrder);
        }
        final int xyLength = xReturns.length + yReturns.length;
        final double[] xWeightedProbability = new double[xyLength];
        final double[] yWeightedProbability = new double[xyLength];
        final double[] xyDifference = new double[xyLength];
        calculateProbabilities(xReturns, yReturns, xyDifference, xWeightedProbability, yWeightedProbability);
        final double sdb = stochasticDominanceOrderLoop(xyDifference, xWeightedProbability, yWeightedProbability,
                dominanceOrder);
        return sdb;
    }

    //https://rdrr.io/cran/generalCorr/src/R/wtdpapb.R
    private static void calculateProbabilities(final double[] xReturns, final double[] yReturns,
            final double[] xyDifference, final double[] xWeightedProbability, final double[] yWeightedProbability) {
        final int xLength = xReturns.length;
        final int yLength = yReturns.length;
        final int xyLength = xLength + yLength;

        final double xProbability = probability(xLength);
        final double yProbability = probability(yLength);
        final double xyProbability = probability(xyLength);
        final double[][] xy_xProb_yProb = new double[xyLength][];
        for (int i = 0; i < xyLength; i++) {
            xy_xProb_yProb[i] = new double[3];
        }
        for (int i = 0; i < xLength; i++) {
            xy_xProb_yProb[i][0] = xReturns[i];
            xy_xProb_yProb[i][1] = xProbability;
        }
        for (int i = 0; i < yLength; i++) {
            xy_xProb_yProb[xLength + i][0] = yReturns[i];
            xy_xProb_yProb[xLength + i][2] = yProbability;
        }
        sortMatrixByColumn(xy_xProb_yProb, 0);
        for (int i = 0; i < xWeightedProbability.length; i++) {
            xWeightedProbability[i] = xy_xProb_yProb[i][1] * xyProbability;
        }
        for (int i = 0; i < yWeightedProbability.length; i++) {
            yWeightedProbability[i] = xy_xProb_yProb[i][2] * xyProbability;
        }
        final double min = xy_xProb_yProb[0][0];
        for (int i = 0; i < xyDifference.length; i++) {
            xyDifference[i] = xy_xProb_yProb[i][0] - min;
        }
    }

    //https://rdrr.io/cran/generalCorr/src/R/stochdom2.R
    private static double stochasticDominanceOrderLoop(final double[] xyDifference,
            final double[] xWeightedProbabilities, final double[] yWeightedProbabilities, final int dominanceOrder) {
        final int xyLength = xWeightedProbabilities.length;
        final double[] cumulativeWeightedProbDiff = new double[xyLength];
        cumulativeWeightedProbDiff[0] = xWeightedProbabilities[0] - yWeightedProbabilities[0];
        for (int i = 1; i < xyLength; i++) {
            cumulativeWeightedProbDiff[i] = xWeightedProbabilities[i] - yWeightedProbabilities[i]
                    + cumulativeWeightedProbDiff[i - 1];
        }
        double[] stochasticDominanceVector = cumulativeWeightedProbDiff;
        for (int i = 0; i < dominanceOrder; i++) {
            stochasticDominanceVector = trapezoidRuleIntegrationVector(xyDifference, stochasticDominanceVector);
        }
        final int lastIndex = xyLength - 1;
        return stochasticDominanceVector[lastIndex];
    }

    //https://rdrr.io/cran/generalCorr/src/R/bigfp.R
    private static double[] trapezoidRuleIntegrationVector(final double[] xyDifference, final double[] probability) {
        double cumulativeSum = 0D;
        final int xyLength = xyDifference.length;
        final double[] result = new double[xyLength];
        result[0] = halfDifferenceMulProbability(xyDifference[0], probability[0]);
        for (int i = 1; i < xyLength; i++) {
            cumulativeSum += (xyDifference[i - 1] + xyDifference[i]) * probability[i - 1];
            final double halfCumulativeSum = 0.5D * cumulativeSum;
            final double halfDifferenceMulProbability = halfDifferenceMulProbability(xyDifference[i], probability[i]);
            result[i] = halfDifferenceMulProbability + halfCumulativeSum;
        }
        return result;
    }

    private static double halfDifferenceMulProbability(final double difference, final double probability) {
        return 0.5D * (difference * probability);
    }

    //https://rdrr.io/cran/generalCorr/src/R/sort_matrix.R
    private static void sortMatrixByColumn(final double[][] x, final int j) {
        Arrays.sort(x, (o1, o2) -> Doubles.compare(o1[j], o2[j]));
    }

    //https://rdrr.io/cran/generalCorr/src/R/prelec2.R
    private static double probability(final int n) {
        //this is equivalent to the above formula, just without the intermediate arrays
        return Doubles.divide(1, n);
    }

}
