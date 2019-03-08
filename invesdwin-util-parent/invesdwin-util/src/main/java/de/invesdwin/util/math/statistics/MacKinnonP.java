package de.invesdwin.util.math.statistics;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.math3.distribution.NormalDistribution;

/**
 * https://github.com/sryza/spark-timeseries/blob/master/src/main/scala/com/cloudera/sparkts/stats/TimeSeriesStatisticalTests.scala
 */
@Immutable
public final class MacKinnonP {

    /**
     * The method of regression that was used. Following MacKinnonP's notation, this can be "c" for constant, "nc" for
     * no constant, "ct" for constant and trend, and "ctt" for constant, trend, and trend-squared.
     * 
     * See also: http://web.sgh.waw.pl/~mrubas/EP/TabliceStatystyczneDF.doc
     */
    public enum RegressionMethod {
        /**
         * no constant
         */
        nc,
        /**
         * constant
         */
        c,
        /**
         * constant and trend
         */
        ct,
        /**
         * constant, trend and trend-squared
         */
        ctt;
    }

    private static final Map<RegressionMethod, double[]> ADF_TAU_STAR = new HashMap<RegressionMethod, double[]>() {
        {
            put(RegressionMethod.nc, new double[] { -1.04, -1.53, -2.68, -3.09, -3.07, -3.77 });
            put(RegressionMethod.c, new double[] { -1.61, -2.62, -3.13, -3.47, -3.78, -3.93 });
            put(RegressionMethod.ct, new double[] { -2.89, -3.19, -3.50, -3.65, -3.80, -4.36 });
            put(RegressionMethod.ctt, new double[] { -3.21, -3.51, -3.81, -3.83, -4.12, -4.63 });
        }
    };

    private static final Map<RegressionMethod, double[]> ADF_TAU_MIN = new HashMap<RegressionMethod, double[]>() {
        {
            put(RegressionMethod.nc, new double[] { -19.04, -19.62, -21.21, -23.25, -21.63, -25.74 });
            put(RegressionMethod.c, new double[] { -18.83, -18.86, -23.48, -28.07, -25.96, -23.27 });
            put(RegressionMethod.ct, new double[] { -16.18, -21.15, -25.37, -26.63, -26.53, -26.18 });
            put(RegressionMethod.ctt, new double[] { -17.17, -21.1, -24.33, -24.03, -24.33, -28.22 });
        }
    };

    private static final Map<RegressionMethod, double[]> ADF_TAU_MAX = new HashMap<RegressionMethod, double[]>() {
        {
            put(RegressionMethod.nc, new double[] { Double.POSITIVE_INFINITY, 1.51, 0.86, 0.88, 1.05, 1.24 });
            put(RegressionMethod.c, new double[] { 2.74, 0.92, 0.55, 0.61, 0.79, 1 });
            put(RegressionMethod.ct, new double[] { 0.7, 0.63, 0.71, 0.93, 1.19, 1.42 });
            put(RegressionMethod.ctt, new double[] { 0.54, 0.79, 1.08, 1.43, 3.49, 1.92 });
        }
    };

    private static final Map<RegressionMethod, double[][]> ADF_TAU_SMALLP = new HashMap<RegressionMethod, double[][]>() {
        {
            put(RegressionMethod.nc,
                    new double[][] { { 0.6344, 1.2378, 3.2496 * 1e-2 }, { 1.9129, 1.3857, 3.5322 * 1e-2 },
                            { 2.7648, 1.4502, 3.4186 * 1e-2 }, { 3.4336, 1.4835, 3.19 * 1e-2 },
                            { 4.0999, 1.5533, 3.59 * 1e-2 }, { 4.5388, 1.5344, 2.9807 * 1e-2 } });
            put(RegressionMethod.c,
                    new double[][] { { 2.1659, 1.4412, 3.8269 * 1e-2 }, { 2.92, 1.5012, 3.9796 * 1e-2 },
                            { 3.4699, 1.4856, 3.164 * 1e-2 }, { 3.9673, 1.4777, 2.6315 * 1e-2 },
                            { 4.5509, 1.5338, 2.9545 * 1e-2 }, { 5.1399, 1.6036, 3.4445 * 1e-2 } });
            put(RegressionMethod.ct,
                    new double[][] { { 3.2512, 1.6047, 4.9588 * 1e-2 }, { 3.6646, 1.5419, 3.6448 * 1e-2 },
                            { 4.0983, 1.5173, 2.9898 * 1e-2 }, { 4.5844, 1.5338, 2.8796 * 1e-2 },
                            { 5.0722, 1.5634, 2.9472 * 1e-2 }, { 5.53, 1.5914, 3.0392 * 1e-2 } });
            put(RegressionMethod.ctt,
                    new double[][] { { 4.0003, 1.658, 4.8288 * 1e-2 }, { 4.3534, 1.6016, 3.7947 * 1e-2 },
                            { 4.7343, 1.5768, 3.2396 * 1e-2 }, { 5.214, 1.6077, 3.3449 * 1e-2 },
                            { 5.6481, 1.6274, 3.3455 * 1e-2 }, { 5.9296, 1.5929, 2.8223 * 1e-2 } });
        }
    };

    private static final double[] ADF_LARGE_SCALING = new double[] { 1.0, 1e-1, 1e-1, 1e-2 };

    private static final Map<RegressionMethod, double[][]> ADF_TAU_LARGEP = new HashMap<RegressionMethod, double[][]>() {
        {
            put(RegressionMethod.nc,
                    new double[][] { { 0.4797, 9.3557, -0.6999, 3.3066 }, { 1.5578, 8.558, -2.083, -3.3549 },
                            { 2.2268, 6.8093, -3.2362, -5.4448 }, { 2.7654, 6.4502, -3.0811, -4.4946 },
                            { 3.2684, 6.8051, -2.6778, -3.4972 }, { 3.7268, 7.167, -2.3648, -2.8288 } });
            put(RegressionMethod.c,
                    new double[][] { { 1.7339, 9.3202, -1.2745, -1.0368 }, { 2.1945, 6.4695, -2.9198, -4.2377 },
                            { 2.5893, 4.5168, -3.6529, -5.0074 }, { 3.0387, 4.5452, -3.3666, -4.1921 },
                            { 3.5049, 5.2098, -2.9158, -3.3468 }, { 3.9489, 5.8933, -2.5359, -2.721 } });
            put(RegressionMethod.ct,
                    new double[][] { { 2.5261, 6.1654, -3.7956, -6.0285 }, { 2.85, 5.272, -3.6622, -5.1695 },
                            { 3.221, 5.255, -3.2685, -4.1501 }, { 3.652, 5.9758, -2.7483, -3.2081 },
                            { 4.0712, 6.6428, -2.3464, -2.546 }, { 4.4735, 7.1757, -2.0681, -2.1196 } });
            put(RegressionMethod.ctt,
                    new double[][] { { 3.0778, 4.9529, -4.1477, -5.9359 }, { 3.4713, 5.967, -3.2507, -4.2286 },
                            { 3.8637, 6.7852, -2.6286, -3.1381 }, { 4.2736, 7.6199, -2.1534, -2.4026 },
                            { 4.6679, 8.2618, -1.822, -1.9147 }, { 5.0009, 8.3735, -1.6994, -1.6928 } });

            for (final RegressionMethod key : keySet()) {
                final double[][] arr = get(key);
                for (final double[] subarr : arr) {
                    for (int i = 0; i < 4; i++) {
                        subarr[i] = ADF_LARGE_SCALING[i] * subarr[i];
                    }
                }
            }
        }
    };

    private MacKinnonP() {}

    /**
     * Returns MacKinnonP's approximate p-value for the given test statistic.
     *
     * MacKinnonP, J.G. 1994 "Approximate Asymptotic Distribution Functions for Unit-Root and Cointegration Tests."
     * Journal of Business & Economics Statistics, 12.2, 167-76.
     *
     * @param testStat
     *            "T-value" from an Augmented Dickey-Fuller regression.
     * @param regressionMethod
     *            The method of regression that was used. Following MacKinnonP's notation, this can be "c" for constant,
     *            "nc" for no constant, "ct" for constant and trend, and "ctt" for constant, trend, and trend-squared.
     * @param n
     *            The number of series believed to be I(1). For (Augmented) Dickey-Fuller n = 1.
     * @return The p-value for the ADF statistic using MacKinnonP 1994.
     */
    public static double macKinnonP(final double testStat, final RegressionMethod regressionMethod, final int n) {
        final double[] maxStat = ADF_TAU_MAX.get(regressionMethod);
        if (testStat > maxStat[n - 1]) {
            return 1.0;
        }
        final double[] minStat = ADF_TAU_MIN.get(regressionMethod);
        if (testStat < minStat[n - 1]) {
            return 0.0;
        }
        final double[] starStat = ADF_TAU_STAR.get(regressionMethod);
        final double[] tauCoef;
        if (testStat <= starStat[n - 1]) {
            tauCoef = ADF_TAU_SMALLP.get(regressionMethod)[n - 1];
        } else {
            tauCoef = ADF_TAU_LARGEP.get(regressionMethod)[n - 1];
        }
        return new NormalDistribution().cumulativeProbability(polyVal(tauCoef, testStat));
    }

    /**
     * Computes the polynomial P(x) with coefficients given in the passed in array. coefs(i) is the coef for the x_i
     * term.
     */
    private static double polyVal(final double[] coefs, final double x) {
        int i = coefs.length - 1;
        double p = coefs[i];
        while (i > 0) {
            i -= 1;
            p = p * x + coefs[i];
        }
        return p;
    }

}
