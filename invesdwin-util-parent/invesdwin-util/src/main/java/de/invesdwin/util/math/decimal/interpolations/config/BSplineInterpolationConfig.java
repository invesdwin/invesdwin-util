package de.invesdwin.util.math.decimal.interpolations.config;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.decimal.Decimal;

/**
 * https://www.ncbi.nlm.nih.gov/pmc/articles/PMC2923818/
 */
@NotThreadSafe
public class BSplineInterpolationConfig extends SplineInterpolationConfig {

    /**
     * This value allows more outliers
     */
    public static final int DEGREE_L_1 = 1;
    /**
     * This is a high enough value for two dimensional spaces to be responsive enough against outliers
     */
    public static final int DEGREE_L_UNLIMITED = 2;
    private int degree = DEGREE_L_UNLIMITED;

    public int getDegree() {
        return degree;
    }

    /**
     * If degree is greater than values size, then the degree is automatically shrinked to the size.
     */
    public BSplineInterpolationConfig setDegree(final int degree) {
        this.degree = degree;
        return this;
    }

    @Override
    public BSplineInterpolationConfig setPunishEdges(final boolean punishEdges) {
        return (BSplineInterpolationConfig) super.setPunishEdges(punishEdges);
    }

    @Override
    public BSplineInterpolationConfig setHigherBetter(final boolean isHigherBetter) {
        return (BSplineInterpolationConfig) super.setHigherBetter(isHigherBetter);
    }

    @Override
    public BSplineInterpolationConfig setMaxPoints(final Integer maxPoints) {
        return (BSplineInterpolationConfig) super.setMaxPoints(maxPoints);
    }

    @Override
    public BSplineInterpolationConfig setValueMultiplicator(final Decimal valueMultiplicator) {
        return (BSplineInterpolationConfig) super.setValueMultiplicator(valueMultiplicator);
    }

}
