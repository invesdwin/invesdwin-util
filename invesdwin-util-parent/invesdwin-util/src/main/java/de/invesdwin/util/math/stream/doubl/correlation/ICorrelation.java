package de.invesdwin.util.math.stream.doubl.correlation;

import de.invesdwin.util.math.decimal.scaled.Percent;

public interface ICorrelation {

    Percent getCorrelation();

    /**
     * R^2
     * 
     * CorrCoef: Correlation Coefficient is a measure of how linear an equity curve is. If it jumps around or is
     * non-linear in shape then this value will be low, but if it progress with a slope close to 1 then the value shall
     * be close to 1. The higher this value the more linear the account growth or equity curve is.
     * 
     * We use the correlation positive/negative sign also for the coefficient of determination.
     */
    default Percent getCoefficientOfDetermination() {
        final Percent correlation = getCorrelation();
        if (correlation == null) {
            return null;
        } else if (correlation.isNegative()) {
            return correlation.square().negate();
        } else {
            return correlation.square();
        }
    }

    default CorrelationType getCorrelationType() {
        if (getCorrelation().isPositive()) {
            return CorrelationType.UpToUpAndDownToDown;
        } else {
            return CorrelationType.UpToDownAndDownToUp;
        }
    }

}
