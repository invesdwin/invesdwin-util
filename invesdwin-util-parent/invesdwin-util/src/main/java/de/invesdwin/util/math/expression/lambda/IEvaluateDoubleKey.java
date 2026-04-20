package de.invesdwin.util.math.expression.lambda;

@FunctionalInterface
public interface IEvaluateDoubleKey {

    /**
     * evaluates the expression using the given long key
     */
    double evaluateDouble(long key);

}
