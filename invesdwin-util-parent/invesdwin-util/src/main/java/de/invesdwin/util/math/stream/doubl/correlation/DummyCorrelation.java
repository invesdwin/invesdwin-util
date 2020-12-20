package de.invesdwin.util.math.stream.doubl.correlation;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.decimal.scaled.Percent;

@Immutable
public final class DummyCorrelation implements ICorrelation {

    public static final DummyCorrelation INSTANCE = new DummyCorrelation();

    private DummyCorrelation() {
    }

    @Override
    public Percent getCorrelation() {
        return null;
    }

    @Override
    public Percent getCoefficientOfDetermination() {
        return null;
    }

    @Override
    public CorrelationType getCorrelationType() {
        return null;
    }

}
