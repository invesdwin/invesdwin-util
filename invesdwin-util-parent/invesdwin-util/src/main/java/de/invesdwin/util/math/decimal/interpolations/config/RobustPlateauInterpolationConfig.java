package de.invesdwin.util.math.decimal.interpolations.config;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.assertions.Assertions;

@NotThreadSafe
public class RobustPlateauInterpolationConfig {

    /**
     * when choosing params it is best to use around 10 permutations (max 20), thus use that amount for segments
     */
    public static final int DEFAULT_MAX_SEGMENTS = 10;

    private boolean isPunishEdges = false;
    private boolean isHigherBetter = true;
    private int maxSegments = DEFAULT_MAX_SEGMENTS;

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

    public int getMaxSegments() {
        return maxSegments;
    }

    public RobustPlateauInterpolationConfig withMaxSegments(final int maxSegments) {
        Assertions.assertThat(maxSegments).isGreaterThanOrEqualTo(1);
        this.maxSegments = maxSegments;
        return this;
    }

}
