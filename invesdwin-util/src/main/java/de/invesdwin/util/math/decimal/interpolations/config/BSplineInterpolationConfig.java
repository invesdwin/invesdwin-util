package de.invesdwin.util.math.decimal.interpolations.config;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.decimal.Decimal;

/**
 * https://www.ncbi.nlm.nih.gov/pmc/articles/PMC2923818/
 */
@NotThreadSafe
public class BSplineInterpolationConfig extends SplineInterpolationConfig {

    public static final int DEGREE_L_1 = 1;
    public static final int DEGREE_L_UNLIMITED = 2;
    private int degree = DEGREE_L_UNLIMITED;

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
    public BSplineInterpolationConfig withPunishEdges(final boolean punishEdges) {
        return (BSplineInterpolationConfig) super.withPunishEdges(punishEdges);
    }

    @Override
    public BSplineInterpolationConfig withHigherBetter(final boolean isHigherBetter) {
        return (BSplineInterpolationConfig) super.withHigherBetter(isHigherBetter);
    }

    @Override
    public BSplineInterpolationConfig withMaxPoints(final Integer maxPoints) {
        return (BSplineInterpolationConfig) super.withMaxPoints(maxPoints);
    }

    @Override
    public BSplineInterpolationConfig withValueMultiplicator(final Decimal valueMultiplicator) {
        return (BSplineInterpolationConfig) super.withValueMultiplicator(valueMultiplicator);
    }

}
