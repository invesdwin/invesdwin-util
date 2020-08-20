package de.invesdwin.util.math.expression.lambda;

@FunctionalInterface
public interface IEvaluateBoolean {

    /**
     * Double.NaN is interpreted as false.
     */
    boolean evaluateBoolean();

}
