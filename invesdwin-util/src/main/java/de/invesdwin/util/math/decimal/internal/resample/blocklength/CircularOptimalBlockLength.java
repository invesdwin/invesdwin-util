package de.invesdwin.util.math.decimal.internal.resample.blocklength;

import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.IDecimalAggregate;

/**
 * Politis, N. Dimitris, White Halbert, "Automatic Block-Length Selection for the Dependent Bootstrap", Econometric
 * Reviews , 2004
 * 
 * Politis, D., White, H., Patton Andrew,"CORRECTION TO 'Automatic Block-Length Selection for the Dependent Bootstrap'",
 * Econometric Reviews, 28(4):372–375, 2009
 * 
 * http://www.math.ucsd.edu/~politis/SOFT/PPW/ppw.R
 * 
 */
@NotThreadSafe
public class CircularOptimalBlockLength<E extends ADecimal<E>> {
    private static final double DISTRIBUTION_CONSTANT = 1.959964D;
    private static final int MIN_CHECK_LAG_INTERVAL = 5;
    private static final double ONE_THIRD = 1D / 3D;
    private static final double MULTIPLICATOR_ONE_AND_A_THIRD = 1D + ONE_THIRD;

    private final List<E> sample;
    private final double sampleAvg;
    private final double sampleAutoCovariance0;

    public CircularOptimalBlockLength(final IDecimalAggregate<E> parent) {
        this.sample = parent.values();
        this.sampleAvg = parent.avg().doubleValueRaw();
        this.sampleAutoCovariance0 = sampleAutoCovariance(0);
    }

    private long determineOptimalLag() {
        final int length = sample.size();
        final int checkLag = determineOptimalLag_checkLagInterval(length);
        final long maxLag = determineOptimalLag_maxlag(length);
        final double correlationThreshold = determineOptimalLag_correlationThreshold(length);

        int prevHalfLag = 0;
        int halfLag = 0;
        int lagIncrements = 0;
        int curLagIdx = 1;
        while (curLagIdx <= maxLag) {
            final double absCorrelation = Math.abs(sampleAutoCorrelation(curLagIdx));
            if (absCorrelation > correlationThreshold) {
                prevHalfLag = curLagIdx;
                lagIncrements = 0;
            } else {
                lagIncrements++;
                if (lagIncrements == checkLag) {
                    halfLag = curLagIdx - checkLag + 1;
                    break;
                }
            }
            curLagIdx++;
        }
        if (halfLag == 0) {
            halfLag = prevHalfLag;
        }
        final long usedLag = 2 * halfLag;
        if (usedLag > maxLag) {
            return maxLag;
        } else {
            return usedLag;
        }
    }

    private int determineOptimalLag_checkLagInterval(final int length) {
        final double logLength = Math.log10(length);
        final double sqrtLogLength = Math.sqrt(logLength);
        final int roundedSqrtLogLength = (int) Math.ceil(sqrtLogLength);
        return Math.max(MIN_CHECK_LAG_INTERVAL, roundedSqrtLogLength);
    }

    private long determineOptimalLag_maxlag(final int length) {
        final double sqrtLength = Math.sqrt(length);
        final long roundedSqrtLength = (long) Math.ceil(sqrtLength);
        final int checkLagInterval = determineOptimalLag_checkLagInterval(length);
        final long maxLag = roundedSqrtLength + checkLagInterval;
        return maxLag;
    }

    private double determineOptimalLag_correlationThreshold(final int length) {
        final double logLengthPerLength = Math.log10(length) / length;
        final double sqrtLogLengthPerLength = Math.sqrt(logLengthPerLength);
        return DISTRIBUTION_CONSTANT * sqrtLogLengthPerLength;
    }

    private double determineOptimalBlockLength_lagMultiplicator(final double value) {
        final double absValue = Math.abs(value);
        if (absValue <= 0.5D) {
            return 1D;
        } else if (absValue <= 1D) {
            return 2D * (1D - absValue);
        } else {
            return 0D;
        }
    }

    private long determineOptimalBlockLength_maxBlockLength(final int length) {
        final double sqrtLength = Math.sqrt(length);
        final double threeTimesSqrtLength = 3D * sqrtLength;
        final double oneThirdLength = length / 3D;
        final double min = Math.min(threeTimesSqrtLength, oneThirdLength);
        final long rounded = (long) Math.ceil(min);
        return rounded;
    }

    public long getBlockLength() {
        final int length = sample.size();
        final long optimalLag = determineOptimalLag();
        double sumTwoLagMultiCovar = sampleAutoCovariance0;
        double sumTwoLagMultiLagCovar = 0D;
        for (int curLag = 1; curLag <= optimalLag; curLag++) {
            final double lagMultiplicator = determineOptimalBlockLength_lagMultiplicator(1D * curLag / optimalLag);
            final double covariance = sampleAutoCovariance(curLag);
            sumTwoLagMultiCovar += 2D * lagMultiplicator * covariance;
            sumTwoLagMultiLagCovar += 2D * lagMultiplicator * curLag * covariance;
        }
        final double blockLengthDivisor = sumTwoLagMultiCovar * sumTwoLagMultiCovar
                * determineOptimalBlockLength_blockLengthMultiplicator();
        double blockLength = Math
                .pow(2D * sumTwoLagMultiLagCovar * sumTwoLagMultiLagCovar * length / blockLengthDivisor, ONE_THIRD);
        final double maxBlockLength = determineOptimalBlockLength_maxBlockLength(length);

        blockLength = Doubles.between(blockLength, 1D, maxBlockLength);
        return Math.round(blockLength);
    }

    protected double determineOptimalBlockLength_blockLengthMultiplicator() {
        return MULTIPLICATOR_ONE_AND_A_THIRD;
    }

    private double sampleAutoCorrelation(final int index) {
        return sampleAutoCovariance(index) / sampleAutoCovariance0;
    }

    private double sampleAutoCovariance(final int index) {
        Assertions.checkTrue(index < sample.size());
        final int length = sample.size();
        double sum = 0;
        final int maxIdx = length - index;
        for (int i = 1; i <= maxIdx; ++i) {
            final double curAdj = sample.get(i).doubleValueRaw() - sampleAvg;
            final double nextAdj = sample.get(i + index).doubleValueRaw() - sampleAvg;
            sum += curAdj * nextAdj;
        }
        return sum / length;
    }

}
