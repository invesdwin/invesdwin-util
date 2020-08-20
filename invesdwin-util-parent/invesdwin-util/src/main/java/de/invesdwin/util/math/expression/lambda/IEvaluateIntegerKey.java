package de.invesdwin.util.math.expression.lambda;

@FunctionalInterface
public interface IEvaluateIntegerKey {

    /**
     * Double.NaN is interpreted as 0.
     */
    int evaluateInteger(int key);

}
