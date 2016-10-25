package de.invesdwin.util.math.decimal.config;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class InterpolationConfig {

    private boolean isPunishEdges = false;
    private boolean isHigherBetter = true;
    private Integer maxPoints = null;

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

}
