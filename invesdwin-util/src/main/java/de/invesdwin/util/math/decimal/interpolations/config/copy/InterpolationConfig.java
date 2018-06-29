package de.invesdwin.util.math.decimal.interpolations.config.copy;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.decimal.Decimal;

@NotThreadSafe
public class InterpolationConfig {

    public static final Decimal SUGGESTED_RATE_VALUE_MULTIPLIER = Decimal.ONE_HUNDRED;
    public static final Integer SUGGESTED_MAX_POINTS = 100;

    private boolean isPunishEdges = false;
    private boolean isHigherBetter = true;
    private Integer maxPoints = null;
    private Decimal valueMultiplicator = null;

    public boolean isPunishEdges() {
        return isPunishEdges;
    }

    public boolean isHigherBetter() {
        return isHigherBetter;
    }

    /**
     * Edges will only get punished when the values size is >= 5, or else the data is insufficient to tell that the
     * edges are actually worse.
     */
    public InterpolationConfig withPunishEdges(final boolean isPunishEdges, final boolean isHigherBetter) {
        this.isPunishEdges = isPunishEdges;
        this.isHigherBetter = isHigherBetter;
        return this;
    }

    public Integer getMaxPoints() {
        return maxPoints;
    }

    /**
     * You can achieve a smoother curve by reducing the number of points, e.g. to a maximum of 100 or so.
     */
    public InterpolationConfig withMaxPoints(final Integer maxPoints) {
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
    public InterpolationConfig withValueMultiplicator(final Decimal valueMultiplicator) {
        this.valueMultiplicator = valueMultiplicator;
        return this;
    }

}
