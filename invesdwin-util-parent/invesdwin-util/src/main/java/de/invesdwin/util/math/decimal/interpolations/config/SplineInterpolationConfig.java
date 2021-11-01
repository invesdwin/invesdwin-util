package de.invesdwin.util.math.decimal.interpolations.config;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.decimal.Decimal;

@NotThreadSafe
public class SplineInterpolationConfig extends RobustPlateauInterpolationConfig {

    public static final Decimal SUGGESTED_RATE_VALUE_MULTIPLIER = Decimal.ONE_HUNDRED;
    public static final Integer SUGGESTED_MAX_POINTS = 100;

    private Integer maxPoints = null;
    private Decimal valueMultiplicator = null;

    /**
     * Edges will only get punished when the values size is >= 5, or else the data is insufficient to tell that the
     * edges are actually worse.
     */
    @Override
    public SplineInterpolationConfig setPunishEdges(final boolean isPunishEdges) {
        return (SplineInterpolationConfig) super.setPunishEdges(isPunishEdges);
    }

    @Override
    public SplineInterpolationConfig setHigherBetter(final boolean isHigherBetter) {
        return (SplineInterpolationConfig) super.setHigherBetter(isHigherBetter);
    }

    public Integer getMaxPoints() {
        return maxPoints;
    }

    /**
     * You can achieve a smoother curve by reducing the number of points, e.g. to a maximum of 100 or so.
     */
    public SplineInterpolationConfig setMaxPoints(final Integer maxPoints) {
        this.maxPoints = maxPoints;
        return this;
    }

    public Decimal getValueMultiplicator() {
        return valueMultiplicator;
    }

    /**
     * You can achieve a better curve by increasing the value scale from [0,1] to [0, 100] via multipliying by 100. With
     * a scale of [0,1] the difference in the values is not large enough to correctly detect peaks and valleys.
     */
    public SplineInterpolationConfig setValueMultiplicator(final Decimal valueMultiplicator) {
        this.valueMultiplicator = valueMultiplicator;
        return this;
    }

}
