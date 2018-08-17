package de.invesdwin.util.math.decimal.interpolations.config;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class RobustPlateauInterpolationConfig {

    private boolean isPunishEdges = false;
    private boolean isHigherBetter = true;

    public boolean isPunishEdges() {
        return isPunishEdges;
    }

    public boolean isHigherBetter() {
        return isHigherBetter;
    }

    public RobustPlateauInterpolationConfig withHigherBetter(final boolean isHigherBetter) {
        this.isHigherBetter = isHigherBetter;
        return this;
    }

    /**
     * Edges will only get punished when the values size is >= 5, or else the data is insufficient to tell that the
     * edges are actually worse.
     */
    public RobustPlateauInterpolationConfig withPunishEdges(final boolean isPunishEdges) {
        this.isPunishEdges = isPunishEdges;
        return this;
    }

}
