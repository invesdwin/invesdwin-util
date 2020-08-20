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
import de.invesdwin.util.math.expression.lambda.IEvaluateInteger;
import de.invesdwin.util.math.expression.lambda.IEvaluateIntegerFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateIntegerKey;

public interface IExpression extends IEvaluateDoubleFDate, IEvaluateDoubleKey, IEvaluateDouble, IEvaluateIntegerFDate,
        IEvaluateIntegerKey, IEvaluateInteger, IEvaluateBooleanNullableFDate, IEvaluateBooleanNullableKey,
        IEvaluateBooleanNullable, IEvaluateBooleanFDate, IEvaluateBooleanKey, IEvaluateBoolean {

    IExpression[] EMPTY_EXPRESSIONS = new IExpression[0];

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
