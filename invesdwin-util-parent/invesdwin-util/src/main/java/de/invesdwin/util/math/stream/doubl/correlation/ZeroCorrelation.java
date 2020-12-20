package de.invesdwin.util.math.stream.doubl.correlation;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.decimal.scaled.Percent;

@Immutable
public final class ZeroCorrelation implements ICorrelation {

    public static final ZeroCorrelation INSTANCE = new ZeroCorrelation();

    private ZeroCorrelation() {
    }

    @Override
    public Percent getCorrelation() {
        return Percent.ZERO_PERCENT;
    }

}
