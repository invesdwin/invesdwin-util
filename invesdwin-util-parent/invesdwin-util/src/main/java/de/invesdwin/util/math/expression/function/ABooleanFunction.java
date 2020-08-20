package de.invesdwin.util.math.expression.function;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.expression.ExpressionReturnType;
import de.invesdwin.util.math.expression.ExpressionType;
import de.invesdwin.util.math.expression.IExpression;
import de.invesdwin.util.math.expression.eval.IParsedExpression;
import de.invesdwin.util.math.expression.eval.function.BooleanFunctionCall;
import de.invesdwin.util.math.expression.lambda.IEvaluateBoolean;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanKey;

@NotThreadSafe
public abstract class ABooleanFunction extends AFunction {

    public abstract IEvaluateBooleanFDate newEvaluateBooleanFDate(IExpression[] args);

    public abstract IEvaluateBooleanKey newEvaluateBooleanKey(IExpression[] args);

    public abstract IEvaluateBoolean newEvaluateBoolean(IExpression[] args);

    @Override
    public final IParsedExpression newCall(final String context, final IParsedExpression[] parameters) {
        return new BooleanFunctionCall(context, this, parameters);
    }

    @Override
    public ExpressionReturnType getReturnType() {
        return ExpressionReturnType.Boolean;
    }

    @Override
    public final ExpressionType getType() {
        return ExpressionType.Boolean;
    }

}
