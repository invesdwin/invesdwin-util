package de.invesdwin.util.math.statistics;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import de.invesdwin.util.math.statistics.MacKinnonP.RegressionMethod;

/**
 * https://github.com/Netflix/Surus/blob/master/src/main/java/org/surus/math/AugmentedDickeyFuller.java
 *
 */
@NotThreadSafe
public class AugmentedDickeyFuller {

    public static final double L2PENALTY = .0001;
    private final double[] ts;
    private final int lag;
    private double testStatistic;
    private double pValue;

    /**
     * Uses the Augmented Dickey Fuller test to determine if ts is a stationary time series
     * 
     * @param ts
     * @param lag
     */
    public AugmentedDickeyFuller(final double[] ts, final int lag) {
        this.ts = ts;
        this.lag = lag;
        computeADFStatistics();
    }

    /**
     * Uses the Augmented Dickey Fuller test to determine if ts is a stationary time series
     * 
     * @param ts
     */
    public AugmentedDickeyFuller(final double[] ts) {
        this.ts = ts;
        this.lag = (int) Math.floor(Math.cbrt((ts.length - 1)));
        computeADFStatistics();
    }

    private void computeADFStatistics() {
        final double[] y = diff(ts);
        RealMatrix designMatrix = null;
        final int k = lag + 1;
        final int n = ts.length - 1;

        final RealMatrix z = MatrixUtils.createRealMatrix(laggedMatrix(y, k)); //has rows length(ts) - 1 - k + 1
        final RealVector zcol1 = z.getColumnVector(0); //has length length(ts) - 1 - k + 1
        final double[] xt1 = subsetArray(ts, k - 1, n - 1); //ts[k:(length(ts) - 1)], has length length(ts) - 1 - k + 1
        final double[] trend = sequence(k, n); //trend k:n, has length length(ts) - 1 - k + 1
        if (k > 1) {
            final RealMatrix yt1 = z.getSubMatrix(0, ts.length - 1 - k, 1, k - 1); //same as z but skips first column
            //build design matrix as cbind(xt1, 1, trend, yt1)
            designMatrix = MatrixUtils.createRealMatrix(ts.length - 1 - k + 1, 3 + k - 1);
            designMatrix.setColumn(0, xt1);
            designMatrix.setColumn(1, ones(ts.length - 1 - k + 1));
            designMatrix.setColumn(2, trend);
            designMatrix.setSubMatrix(yt1.getData(), 0, 3);
        } else {
            //build design matrix as cbind(xt1, 1, tt)
            designMatrix = MatrixUtils.createRealMatrix(ts.length - 1 - k + 1, 3);
            designMatrix.setColumn(0, xt1);
            designMatrix.setColumn(1, ones(ts.length - 1 - k + 1));
            designMatrix.setColumn(2, trend);
        }
        /*
         * OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression(); regression.setNoIntercept(true);
         * regression.newSampleData(zcol1.toArray(), designMatrix.getData()); double[] beta =
         * regression.estimateRegressionParameters(); double[] sd =
         * regression.estimateRegressionParametersStandardErrors();
         */
        final RidgeRegression regression = new RidgeRegression(designMatrix.getData(), zcol1.toArray());
        regression.updateCoefficients(L2PENALTY);
        final double[] beta = regression.getCoefficients();
        final double[] sd = regression.getStandarderrors();

        this.testStatistic = beta[0] / sd[0];
        this.pValue = MacKinnonP.macKinnonP(testStatistic, RegressionMethod.c, 1);
    }

    /**
     * Takes finite differences of x
     * 
     * @param x
     * @return Returns an array of length x.length-1 of the first differences of x
     */
    private double[] diff(final double[] x) {
        final double[] diff = new double[x.length - 1];
        for (int i = 0; i < diff.length; i++) {
            final double diff_i = x[i + 1] - x[i];
            diff[i] = diff_i;
        }
        return diff;
    }

    /**
     * Equivalent to matlab and python ones
     * 
     * @param n
     * @return an array of doubles of length n that are initialized to 1
     */
    private double[] ones(final int n) {
        final double[] ones = new double[n];
        for (int i = 0; i < n; i++) {
            ones[i] = 1;
        }
        return ones;
    }

    /**
     * Equivalent to R's embed function
     * 
     * @param x
     *            time series vector
     * @param lag
     *            number of lags, where lag=1 is the same as no lags
     * @return a matrix that has x.length - lag + 1 rows by lag columns.
     */
    private double[][] laggedMatrix(final double[] x, final int lag) {
        final double[][] laggedMatrix = new double[x.length - lag + 1][lag];
        for (int j = 0; j < lag; j++) { //loop through columns
            for (int i = 0; i < laggedMatrix.length; i++) {
                laggedMatrix[i][j] = x[lag - j - 1 + i];
            }
        }
        return laggedMatrix;
    }

    /**
     * Takes x[start] through x[end - 1]
     * 
     * @param x
     * @param start
     * @param end
     * @return
     */
    private double[] subsetArray(final double[] x, final int start, final int end) {
        final double[] subset = new double[end - start + 1];
        System.arraycopy(x, start, subset, 0, end - start + 1);
        return subset;
    }

    /**
     * Generates a sequence of ints [start, end]
     * 
     * @param start
     * @param end
     * @return
     */
    private double[] sequence(final int start, final int end) {
        final double[] sequence = new double[end - start + 1];
        for (int i = start; i <= end; i++) {
            sequence[i - start] = i;
        }
        return sequence;
    }

    public double getTestStatisic() {
        return testStatistic;
    }

    public double getPValue() {
        return pValue;
    }
}
