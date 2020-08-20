package de.invesdwin.util.math.expression.lambda;

@FunctionalInterface
public interface IEvaluateBooleanNullableKey {

    /**
     * Double.NaN is interpreted as null.
     */
    Boolean evaluateBooleanNullable(int key);

}
