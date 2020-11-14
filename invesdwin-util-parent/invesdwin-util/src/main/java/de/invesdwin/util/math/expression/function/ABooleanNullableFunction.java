package de.invesdwin.util.math.expression.function;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.expression.ExpressionReturnType;
import de.invesdwin.util.math.expression.ExpressionType;
import de.invesdwin.util.math.expression.IExpression;
import de.invesdwin.util.math.expression.eval.IParsedExpression;
import de.invesdwin.util.math.expression.eval.function.BooleanNullableFunctionCall;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanNullable;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanNullableFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanNullableKey;

@NotThreadSafe
public abstract class ABooleanNullableFunction extends AFunction {

    public abstract IEvaluateBooleanNullableFDate newEvaluateBooleanNullableFDate(IExpression[] args);

    public abstract IEvaluateBooleanNullableKey newEvaluateBooleanNullableKey(IExpression[] args);

    public abstract IEvaluateBooleanNullable newEvaluateBooleanNullable(IExpression[] args);

    @Override
    public IParsedExpression newCall(final String context, final IParsedExpression[] parameters) {
        return new BooleanNullableFunctionCall(context, this, parameters);
    }

    @Override
    public ExpressionReturnType getReturnType() {
        return ExpressionReturnType.Boolean;
    }

    @Override
    public final ExpressionType getType() {
        return ExpressionType.BooleanNullable;
    }

}
