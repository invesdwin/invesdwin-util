package de.invesdwin.util.math.expression.lambda;

import de.invesdwin.util.time.date.IFDateProvider;

@FunctionalInterface
public interface IEvaluateBooleanFDate {

    /**
     * Double.NaN is interpreted as false.
     */
    boolean evaluateBoolean(IFDateProvider key);

}
