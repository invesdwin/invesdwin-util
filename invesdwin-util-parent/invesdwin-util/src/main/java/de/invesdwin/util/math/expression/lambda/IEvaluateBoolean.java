package de.invesdwin.util.math.expression.lambda;

@FunctionalInterface
public interface IEvaluateBoolean {

    IEvaluateBoolean TRUE = () -> true;
    IEvaluateBoolean FALSE = () -> false;

    /**
     * Double.NaN is interpreted as false.
     */
    boolean evaluateBoolean();

}
