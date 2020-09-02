package de.invesdwin.util.math.expression.lambda;

@FunctionalInterface
public interface IEvaluateDoubleGeneric<K> {

    /**
     * evaluates the expression using the current int key
     */
    double evaluateDouble(K key);

}
