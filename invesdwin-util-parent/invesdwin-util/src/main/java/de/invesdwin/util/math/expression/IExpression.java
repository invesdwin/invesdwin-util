package de.invesdwin.util.math.expression;

import de.invesdwin.util.math.expression.lambda.IEvaluateBoolean;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanKey;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanNullable;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanNullableFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanNullableKey;
import de.invesdwin.util.math.expression.lambda.IEvaluateDouble;
import de.invesdwin.util.math.expression.lambda.IEvaluateDoubleFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateDoubleKey;
import de.invesdwin.util.math.expression.lambda.IEvaluateGeneric;
import de.invesdwin.util.math.expression.lambda.IEvaluateGenericFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateGenericKey;
import de.invesdwin.util.math.expression.lambda.IEvaluateInteger;
import de.invesdwin.util.math.expression.lambda.IEvaluateIntegerFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateIntegerKey;

public interface IExpression extends IParsedExpressionProvider {

    IExpression[] EMPTY_EXPRESSIONS = new IExpression[0];

    /**
     * evaluates the expression using the current time key
     */
    IEvaluateDoubleFDate newEvaluateDoubleFDate();

    /**
     * evaluates the expression using the current int key
     */
    IEvaluateDoubleKey newEvaluateDoubleKey();

    /**
     * evaluates the expression using the current available time/int key
     */
    IEvaluateDouble newEvaluateDouble();

    /**
     * Double.NaN is interpreted as 0.
     */
    IEvaluateIntegerFDate newEvaluateIntegerFDate();

    /**
     * Double.NaN is interpreted as 0.
     */
    IEvaluateIntegerKey newEvaluateIntegerKey();

    /**
     * Double.NaN is interpreted as 0.
     */
    IEvaluateInteger newEvaluateInteger();

    /**
     * Double.NaN is interpreted as false.
     */
    IEvaluateBoolean newEvaluateBoolean();

    /**
     * Double.NaN is interpreted as false.
     */
    IEvaluateBooleanFDate newEvaluateBooleanFDate();

    /**
     * Double.NaN is interpreted as false.
     */
    IEvaluateBooleanKey newEvaluateBooleanKey();

    /**
     * Double.NaN is interpreted as null.
     */
    IEvaluateBooleanNullable newEvaluateBooleanNullable();

    /**
     * Double.NaN is interpreted as null.
     */
    IEvaluateBooleanNullableFDate newEvaluateBooleanNullableFDate();

    /**
     * Double.NaN is interpreted as null.
     */
    IEvaluateBooleanNullableKey newEvaluateBooleanNullableKey();

    /**
     * Double.NaN is interpreted as null.
     */
    IEvaluateGeneric<String> newEvaluateFalseReason();

    /**
     * Double.NaN is interpreted as null.
     */
    IEvaluateGenericFDate<String> newEvaluateFalseReasonFDate();

    /**
     * Double.NaN is interpreted as null.
     */
    IEvaluateGenericKey<String> newEvaluateFalseReasonKey();

    /**
     * Double.NaN is interpreted as null.
     */
    IEvaluateGeneric<String> newEvaluateTrueReason();

    /**
     * Double.NaN is interpreted as null.
     */
    IEvaluateGenericFDate<String> newEvaluateTrueReasonFDate();

    /**
     * Double.NaN is interpreted as null.
     */
    IEvaluateGenericKey<String> newEvaluateTrueReasonKey();

    /**
     * Double.NaN is interpreted as null.
     */
    IEvaluateGeneric<String> newEvaluateNullReason();

    /**
     * Double.NaN is interpreted as null.
     */
    IEvaluateGenericFDate<String> newEvaluateNullReasonFDate();

    /**
     * Double.NaN is interpreted as null.
     */
    IEvaluateGenericKey<String> newEvaluateNullReasonKey();

    boolean isConstant();

    String getContext();

    /**
     * Return true if values are only availble point in time without history. E.g. dependant on active orders and thus
     * should be persisted for charts.
     */
    boolean shouldPersist();

    /**
     * Return true if this expression can be drawn. This might be false for command expressions that always return NaN.
     * In that case the children might be drawn.
     */
    boolean shouldDraw();

    IExpression[] getChildren();

}
