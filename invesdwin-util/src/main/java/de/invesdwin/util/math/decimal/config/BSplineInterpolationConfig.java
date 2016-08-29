package de.invesdwin.util.math.decimal.config;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class BSplineInterpolationConfig extends InterpolationConfig {

    private int degree = 4;

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

    @Override
    public BSplineInterpolationConfig withPunishEdges(final boolean punishEdges, final boolean higherIsBetter) {
        return (BSplineInterpolationConfig) super.withPunishEdges(punishEdges, higherIsBetter);
    }

}
