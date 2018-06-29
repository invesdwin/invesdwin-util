package de.invesdwin.util.math.decimal.interpolations.config.copy;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.decimal.Decimal;

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

    @Override
    public BSplineInterpolationConfig withMaxPoints(final Integer maxPoints) {
        return (BSplineInterpolationConfig) super.withMaxPoints(maxPoints);
    }

    @Override
    public BSplineInterpolationConfig withValueMultiplicator(final Decimal valueMultiplicator) {
        return (BSplineInterpolationConfig) super.withValueMultiplicator(valueMultiplicator);
    }

}
