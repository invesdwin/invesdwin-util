package de.invesdwin.util.math.expression.lambda;

@FunctionalInterface
public interface IEvaluateDoubleKey {

    /**
     * evaluates the expression using the current int key
     */
    double evaluateDouble(int key);

}
