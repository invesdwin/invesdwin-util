package de.invesdwin.util.math.expression.lambda;

@FunctionalInterface
public interface IEvaluateBooleanNullable {

    /**
     * Double.NaN is interpreted as null.
     */
    Boolean evaluateBooleanNullable();

}
