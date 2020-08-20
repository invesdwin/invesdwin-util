package de.invesdwin.util.math.expression.function;

import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanKey;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanNullableFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanNullableKey;
import de.invesdwin.util.math.expression.lambda.IEvaluateDoubleFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateDoubleKey;
import de.invesdwin.util.math.expression.lambda.IEvaluateIntegerFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateIntegerKey;
import de.invesdwin.util.time.fdate.IFDateProvider;

public interface IPreviousKeyFunction {

    IFDateProvider getPreviousKey(IFDateProvider key, int index);

    int getPreviousKey(int key, int index);

    default double evaluateDouble(final IEvaluateDoubleFDate expression, final IFDateProvider previousKey) {
        return expression.evaluateDouble(previousKey);
    }

    default double evaluateDouble(final IEvaluateDoubleKey expression, final int previousKey) {
        return expression.evaluateDouble(previousKey);
    }

    default Boolean evaluateBooleanNullable(final IEvaluateBooleanNullableFDate expression,
            final IFDateProvider previousKey) {
        return expression.evaluateBooleanNullable(previousKey);
    }

    default Boolean evaluateBooleanNullable(final IEvaluateBooleanNullableKey expression, final int previousKey) {
        return expression.evaluateBooleanNullable(previousKey);
    }

    default boolean evaluateBoolean(final IEvaluateBooleanFDate expression, final IFDateProvider previousKey) {
        return expression.evaluateBoolean(previousKey);
    }

    default boolean evaluateBoolean(final IEvaluateBooleanKey expression, final int previousKey) {
        return expression.evaluateBoolean(previousKey);
    }

    default int evaluateInteger(final IEvaluateIntegerFDate expression, final IFDateProvider previousKey) {
        return expression.evaluateInteger(previousKey);
    }

    default int evaluateInteger(final IEvaluateIntegerKey expression, final int previousKey) {
        return expression.evaluateInteger(previousKey);
    }

}
