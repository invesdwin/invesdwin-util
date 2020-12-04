package de.invesdwin.util.math.stream.doubl.correlation;

import de.invesdwin.util.math.decimal.scaled.Percent;

public interface ICorrelation {

    Percent getCorrelation();

    Percent getCoefficientOfDetermination();

    default CorrelationType getCorrelationType() {
        if (getCorrelation().isPositive()) {
            return CorrelationType.UpToUpAndDownToDown;
        } else {
            return CorrelationType.UpToDownAndDownToUp;
        }
    }

}
