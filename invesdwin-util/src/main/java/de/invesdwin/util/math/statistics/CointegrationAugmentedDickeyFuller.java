package de.invesdwin.util.math.statistics;

import javax.annotation.concurrent.NotThreadSafe;

/**
 * https://www.quantstart.com/articles/Cointegrated-Augmented-Dickey-Fuller-Test-for-Pairs-Trading-Evaluation-in-R
 */
@NotThreadSafe
public class CointegrationAugmentedDickeyFuller {

    private final double testStatistic;
    private final double pValue;

    public CointegrationAugmentedDickeyFuller(final double[] ts1, final double[] ts2) {
        final double[] residuals = calculateResiduals(ts1, ts2);
        final AugmentedDickeyFuller adf = new AugmentedDickeyFuller(residuals);
        this.testStatistic = adf.getTestStatisic();
        this.pValue = adf.getPValue();
    }

    private double[] calculateResiduals(final double[] ts1, final double[] ts2) {
        final RidgeRegression regression = new RidgeRegression(new double[][] { ts2 }, ts1);
        regression.updateCoefficients(AugmentedDickeyFuller.L2PENALTY);
        return regression.getResiduals();
    }

    public double getTestStatistic() {
        return testStatistic;
    }

    public double getPValue() {
        return pValue;
    }

}
