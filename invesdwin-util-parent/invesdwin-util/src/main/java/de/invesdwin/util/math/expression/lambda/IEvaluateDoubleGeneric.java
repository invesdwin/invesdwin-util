package de.invesdwin.util.math.expression.lambda;

@FunctionalInterface
public interface IEvaluateDoubleGeneric<K> {

    /**
     * evaluates the expression using the given key
     */
    double evaluateDouble(K key);

}
