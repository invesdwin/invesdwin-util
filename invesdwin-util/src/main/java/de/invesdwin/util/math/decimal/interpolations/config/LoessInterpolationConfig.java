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

    public LoessInterpolationConfig withSmoothness(final Percent smoothness) {
        this.smoothness = smoothness;
        return this;
    }

    @Override
    public LoessInterpolationConfig withPunishEdges(final boolean punishEdges) {
        return (LoessInterpolationConfig) super.withPunishEdges(punishEdges);
    }

    @Override
    public LoessInterpolationConfig withHigherBetter(final boolean isHigherBetter) {
        return (LoessInterpolationConfig) super.withHigherBetter(isHigherBetter);
    }

    @Override
    public LoessInterpolationConfig withMaxPoints(final Integer maxPoints) {
        return (LoessInterpolationConfig) super.withMaxPoints(maxPoints);
    }

    @Override
    public LoessInterpolationConfig withValueMultiplicator(final Decimal valueMultiplicator) {
        return (LoessInterpolationConfig) super.withValueMultiplicator(valueMultiplicator);
    }

}
