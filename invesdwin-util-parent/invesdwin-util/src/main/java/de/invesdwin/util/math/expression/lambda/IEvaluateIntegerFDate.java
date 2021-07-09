package de.invesdwin.util.math.expression.lambda;

import de.invesdwin.util.time.date.IFDateProvider;

@FunctionalInterface
public interface IEvaluateIntegerFDate {

    /**
     * Double.NaN is interpreted as 0.
     */
    int evaluateInteger(IFDateProvider key);

}
