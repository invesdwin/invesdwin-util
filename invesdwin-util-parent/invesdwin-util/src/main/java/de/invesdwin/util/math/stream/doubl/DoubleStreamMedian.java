package de.invesdwin.util.math.stream.doubl;

import javax.annotation.concurrent.NotThreadSafe;

import com.tdunning.math.stats.AVLTreeDigest;
import com.tdunning.math.stats.TDigest;

@NotThreadSafe
public class DoubleStreamMedian implements IDoubleStreamAlgorithm {

    //https://github.com/tdunning/t-digest/blob/main/benchmark/src/main/java/com/tdunning/TDigestBench.java
    public static final double DEFAULT_COMPRESSION = 100D;
    public static final double MEDIAN_QUANTILE = 0.5D;

    private final TDigest digest;

    public DoubleStreamMedian() {
        this(DEFAULT_COMPRESSION);
    }

    public DoubleStreamMedian(final double compression) {
        //AVLTreeDigest is a lot faster than MergingDigest (though MergingDigest saves lots of memory supposedly)
        this(new AVLTreeDigest(compression));
    }

    public DoubleStreamMedian(final TDigest digest) {
        this.digest = digest;
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
