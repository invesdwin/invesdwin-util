package de.invesdwin.util.math.expression.lambda;

import de.invesdwin.util.time.fdate.IFDateProvider;

@FunctionalInterface
public interface IEvaluateIntegerFDate {

    /**
     * Double.NaN is interpreted as 0.
     */
    int evaluateInteger(IFDateProvider key);

}
