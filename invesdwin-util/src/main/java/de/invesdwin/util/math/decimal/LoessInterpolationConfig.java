package de.invesdwin.util.math.decimal;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.decimal.scaled.Percent;

@NotThreadSafe
public class LoessInterpolationConfig {

    private Percent smoothness = Percent.FIFTY_PERCENT;
    private boolean punishEdges = false;

    public Percent getSmoothness() {
        return smoothness;
    }

    public LoessInterpolationConfig withSmoothness(final Percent smoothness) {
        this.smoothness = smoothness;
        return this;
    }

    public boolean isPunishEdges() {
        return punishEdges;
    }

    public LoessInterpolationConfig withPunishEdges(final boolean punishEdges) {
        this.punishEdges = punishEdges;
        return this;
    }

}
