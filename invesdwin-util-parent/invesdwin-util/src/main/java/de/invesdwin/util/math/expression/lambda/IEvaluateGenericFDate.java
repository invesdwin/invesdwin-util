package de.invesdwin.util.math.expression.lambda;

import de.invesdwin.util.time.date.IFDateProvider;

@FunctionalInterface
public interface IEvaluateGenericFDate<E> {

    /**
     * evaluates the expression using the current time key
     */
    E evaluateGeneric(IFDateProvider key);

}
