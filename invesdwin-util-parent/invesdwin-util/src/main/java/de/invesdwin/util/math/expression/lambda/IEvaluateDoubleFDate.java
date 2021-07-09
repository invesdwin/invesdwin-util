package de.invesdwin.util.math.expression.lambda;

import de.invesdwin.util.time.date.IFDateProvider;

@FunctionalInterface
public interface IEvaluateDoubleFDate {

    /**
     * evaluates the expression using the current time key
     */
    double evaluateDouble(IFDateProvider key);

}
