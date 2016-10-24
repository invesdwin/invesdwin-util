package de.invesdwin.util.math.decimal.config;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class InterpolationConfig {

    private boolean isPunishEdges = false;
    private boolean isHigherBetter = true;

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

}
