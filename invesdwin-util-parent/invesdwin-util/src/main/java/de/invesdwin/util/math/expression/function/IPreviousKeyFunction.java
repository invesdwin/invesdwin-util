package de.invesdwin.util.math.expression.function;

import de.invesdwin.util.math.expression.eval.IParsedExpression;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanKey;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanNullableFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanNullableKey;
import de.invesdwin.util.math.expression.lambda.IEvaluateDoubleFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateDoubleKey;
import de.invesdwin.util.math.expression.lambda.IEvaluateGenericFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateGenericKey;
import de.invesdwin.util.math.expression.lambda.IEvaluateIntegerFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateIntegerKey;
import de.invesdwin.util.time.fdate.IFDateProvider;

public interface IPreviousKeyFunction {

    IFDateProvider getPreviousKey(IFDateProvider key, int index);

    int getPreviousKey(int key, int index);

    default IEvaluateDoubleFDate newEvaluateDoubleFDate(final IParsedExpression expression) {
        return expression.newEvaluateDoubleFDate();
    }

    default IEvaluateDoubleKey newEvaluateDoubleKey(final IParsedExpression expression) {
        return expression.newEvaluateDoubleKey();
    }

    default IEvaluateBooleanNullableFDate newEvaluateBooleanNullableFDate(final IParsedExpression expression) {
        return expression.newEvaluateBooleanNullableFDate();
    }

    default IEvaluateBooleanNullableKey newEvaluateBooleanNullableKey(final IParsedExpression expression) {
        return expression.newEvaluateBooleanNullableKey();
    }

    default IEvaluateBooleanFDate newEvaluateBooleanFDate(final IParsedExpression expression) {
        return expression.newEvaluateBooleanFDate();
    }

    default IEvaluateBooleanKey newEvaluateBooleanKey(final IParsedExpression expression) {
        return expression.newEvaluateBooleanKey();
    }

    default IEvaluateGenericFDate<String> newEvaluateFalseReasonFDate(final IParsedExpression expression) {
        return expression.newEvaluateFalseReasonFDate();
    }

    default IEvaluateGenericKey<String> newEvaluateFalseReasonKey(final IParsedExpression expression) {
        return expression.newEvaluateFalseReasonKey();
    }

    default IEvaluateGenericFDate<String> newEvaluateTrueReasonFDate(final IParsedExpression expression) {
        return expression.newEvaluateTrueReasonFDate();
    }

    default IEvaluateGenericKey<String> newEvaluateTrueReasonKey(final IParsedExpression expression) {
        return expression.newEvaluateTrueReasonKey();
    }

    default IEvaluateGenericFDate<String> newEvaluateNullReasonFDate(final IParsedExpression expression) {
        return expression.newEvaluateNullReasonFDate();
    }

    default IEvaluateGenericKey<String> newEvaluateNullReasonKey(final IParsedExpression expression) {
        return expression.newEvaluateNullReasonKey();
    }

    default IEvaluateIntegerFDate newEvaluateIntegerFDate(final IParsedExpression expression) {
        return expression.newEvaluateIntegerFDate();
    }

    default IEvaluateIntegerKey newEvaluateIntegerKey(final IParsedExpression expression) {
        return expression.newEvaluateIntegerKey();
    }

}
