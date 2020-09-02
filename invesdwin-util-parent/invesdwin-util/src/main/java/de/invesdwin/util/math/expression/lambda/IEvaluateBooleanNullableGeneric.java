package de.invesdwin.util.math.expression.lambda;

@FunctionalInterface
public interface IEvaluateBooleanNullableGeneric<K> {

    /**
     * Double.NaN is interpreted as null.
     */
    Boolean evaluateBooleanNullable(K key);

}
