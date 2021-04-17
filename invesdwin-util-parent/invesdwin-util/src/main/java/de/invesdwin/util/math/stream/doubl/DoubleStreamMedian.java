package de.invesdwin.util.math.stream.doubl;

import javax.annotation.concurrent.NotThreadSafe;

import com.tdunning.math.stats.MergingDigest;

@NotThreadSafe
public class DoubleStreamMedian implements IDoubleStreamAlgorithm {

    public static final double DEFAULT_COMPRESSION = 100D;
    public static final double MEDIAN_QUANTILE = 0.5D;

    private final MergingDigest digest;

    public DoubleStreamMedian() {
        this(DEFAULT_COMPRESSION);
    }

    public DoubleStreamMedian(final double compression) {
        this.digest = new MergingDigest(compression);
    }

    @Override
    public double process(final double value) {
        digest.add(value);
        return Double.NaN;
    }

    public double getMedian() {
        return digest.quantile(MEDIAN_QUANTILE);
    }

    public double getQuantile(final double quantile) {
        return digest.quantile(quantile);
    }

}
