package de.invesdwin.util.math.expression.lambda;

@FunctionalInterface
public interface IEvaluateNegativeReasonKey {

    /**
     * Double.NaN is interpreted as null.
     */
    Boolean evaluateBooleanNullable(int key);

}
