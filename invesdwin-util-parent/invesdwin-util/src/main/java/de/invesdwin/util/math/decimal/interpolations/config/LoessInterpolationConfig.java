package de.invesdwin.util.math.decimal.interpolations.config;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.math.decimal.scaled.Percent;

@NotThreadSafe
public class LoessInterpolationConfig extends SplineInterpolationConfig {

    private Percent smoothness = Percent.FIFTY_PERCENT;

    public Percent getSmoothness() {
        return smoothness;
    }

    public LoessInterpolationConfig setSmoothness(final Percent smoothness) {
        this.smoothness = smoothness;
        return this;
    }

    @Override
    public LoessInterpolationConfig setPunishEdges(final boolean punishEdges) {
        return (LoessInterpolationConfig) super.setPunishEdges(punishEdges);
    }

    @Override
    public LoessInterpolationConfig setHigherBetter(final boolean isHigherBetter) {
        return (LoessInterpolationConfig) super.setHigherBetter(isHigherBetter);
    }

    @Override
    public LoessInterpolationConfig setMaxPoints(final Integer maxPoints) {
        return (LoessInterpolationConfig) super.setMaxPoints(maxPoints);
    }

    @Override
    public LoessInterpolationConfig setValueMultiplicator(final Decimal valueMultiplicator) {
        return (LoessInterpolationConfig) super.setValueMultiplicator(valueMultiplicator);
    }

}
