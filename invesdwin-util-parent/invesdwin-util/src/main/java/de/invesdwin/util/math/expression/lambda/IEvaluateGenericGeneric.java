package de.invesdwin.util.math.expression.lambda;

@FunctionalInterface
public interface IEvaluateGenericGeneric<K, V> {

    /**
     * evaluates the expression using the given key
     */
    V evaluateGeneric(K key);

}
