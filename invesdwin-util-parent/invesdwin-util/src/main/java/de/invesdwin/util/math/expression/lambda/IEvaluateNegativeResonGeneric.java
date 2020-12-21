package de.invesdwin.util.math.expression.lambda;

@FunctionalInterface
public interface IEvaluateNegativeResonGeneric<K> {

    /**
     * Double.NaN is interpreted as null.
     */
    Boolean evaluateBooleanNullable(K key);

}
