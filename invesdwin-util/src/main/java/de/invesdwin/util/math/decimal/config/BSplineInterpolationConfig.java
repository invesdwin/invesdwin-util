package de.invesdwin.util.math.decimal.config;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class BSplineInterpolationConfig extends AInterpolationConfig<BSplineInterpolationConfig> {

    private int degree = Integer.MAX_VALUE;

    public int getDegree() {
        return degree;
    }

    /**
     * If degree is greater than values size, then the degree is automatically shrinked to the size.
     */
    public BSplineInterpolationConfig withDegree(final int degree) {
        this.degree = degree;
        return this;
    }

}
