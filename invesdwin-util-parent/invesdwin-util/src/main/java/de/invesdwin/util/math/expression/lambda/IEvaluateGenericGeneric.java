package de.invesdwin.util.math.expression.lambda;

@FunctionalInterface
public interface IEvaluateGenericGeneric<K, V> {

    /**
     * evaluates the expression using the current int key
     */
    V evaluateGeneric(K key);

}
