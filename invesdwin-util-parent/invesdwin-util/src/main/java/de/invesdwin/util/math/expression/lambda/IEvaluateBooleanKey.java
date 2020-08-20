package de.invesdwin.util.math.expression.lambda;

@FunctionalInterface
public interface IEvaluateBooleanKey {

    /**
     * Double.NaN is interpreted as false.
     */
    boolean evaluateBoolean(int key);

}
