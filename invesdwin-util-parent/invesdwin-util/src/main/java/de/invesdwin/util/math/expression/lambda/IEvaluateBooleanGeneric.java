package de.invesdwin.util.math.expression.lambda;

@FunctionalInterface
public interface IEvaluateBooleanGeneric<K> {

    /**
     * Double.NaN is interpreted as false.
     */
    boolean evaluateBoolean(K key);

}
