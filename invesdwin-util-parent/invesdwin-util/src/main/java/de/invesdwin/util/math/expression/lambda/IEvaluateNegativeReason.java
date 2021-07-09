package de.invesdwin.util.math.expression.lambda;

import de.invesdwin.util.time.date.IFDateProvider;

@FunctionalInterface
public interface IEvaluateNegativeReason {

    /**
     * Double.NaN is interpreted as null.
     */
    Boolean evaluateBooleanNullable(IFDateProvider key);

}
