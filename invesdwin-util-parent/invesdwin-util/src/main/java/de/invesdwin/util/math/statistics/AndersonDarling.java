package de.invesdwin.util.math.statistics;

import static java.lang.Math.exp;
import static java.lang.Math.log;
import static java.lang.Math.sqrt;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.math3.distribution.NormalDistribution;

import de.invesdwin.util.collections.Arrays;

@Immutable
public final class AndersonDarling {

    private static final NormalDistribution ND = new NormalDistribution();

    private AndersonDarling() {
    }

    public static double statistic(final double[] x) {
        final int n = x.length;
        double sum = 0, sumSq = 0;

        for (int i = 0; i < n; i++) {
            final double value = x[i];
            sum += value;
            sumSq += value * value;
        }

        final double mean = sum / n, sd = sqrt((sumSq - sum * mean) / (n - 1));
        final double[] y = new double[n];

        // Standardize X
        for (int i = 0; i < n; i++) {
            y[i] = (x[i] - mean) / sd;
        }
        Arrays.sort(y);

        // Get corresponding normal CDF
        for (int i = 0; i < n; i++) {
            y[i] = ND.cumulativeProbability(y[i]);
        }

        sum = 0;
        for (int i = 1; i <= n; i++) {
            sum += (2 * i - 1) * (log(y[i - 1]) + log(1 - y[n - i]));
        }

        return -n - sum / n;
    }

    public static double pvalue(final double value, final int n) {
        final double aa = value * (1 + 0.75 / n + 2.25 / (n * n)), aasq = aa * aa;
        if (aa < 0.2) {
            return 1 - exp(-13.436 + 101.14 * aa - 223.73 * aasq);
        } else if (aa < 0.34) {
            return 1 - exp(-8.318 + 42.796 * aa - 59.938 * aasq);
        } else if (aa < 0.6) {
            return exp(0.9177 - 4.279 * aa - 1.38 * aasq);
        }
        return exp(1.2937 - 5.709 * aa + 0.0186 * aasq);
    }

}
