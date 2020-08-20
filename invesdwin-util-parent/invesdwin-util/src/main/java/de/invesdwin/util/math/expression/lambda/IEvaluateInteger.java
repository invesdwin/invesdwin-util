package de.invesdwin.util.math.expression.lambda;

@FunctionalInterface
public interface IEvaluateInteger {

    /**
     * Double.NaN is interpreted as 0.
     */
    int evaluateInteger();

}
