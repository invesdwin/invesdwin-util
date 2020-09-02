package de.invesdwin.util.math.expression.lambda;

@FunctionalInterface
public interface IEvaluateIntegerGeneric<K> {

    /**
     * Double.NaN is interpreted as 0.
     */
    int evaluateInteger(K key);

}
