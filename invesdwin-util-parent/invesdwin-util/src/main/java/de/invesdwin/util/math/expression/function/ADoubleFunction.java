package de.invesdwin.util.math.expression.function;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.expression.ExpressionReturnType;
import de.invesdwin.util.math.expression.ExpressionType;
import de.invesdwin.util.math.expression.IExpression;
import de.invesdwin.util.math.expression.eval.IParsedExpression;
import de.invesdwin.util.math.expression.eval.function.DoubleFunctionCall;
import de.invesdwin.util.math.expression.lambda.IEvaluateDouble;
import de.invesdwin.util.math.expression.lambda.IEvaluateDoubleFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateDoubleKey;

@NotThreadSafe
public abstract class ADoubleFunction extends AFunction {

    public abstract IEvaluateDoubleFDate newEvaluateDoubleFDate(IExpression[] args);

    public abstract IEvaluateDoubleKey newEvaluateDoubleKey(IExpression[] args);

    public abstract IEvaluateDouble newEvaluateDouble(IExpression[] args);

    @Override
    public IParsedExpression newCall(final String context, final IParsedExpression[] parameters) {
        return new DoubleFunctionCall(context, this, parameters);
    }

    @Override
    public ExpressionReturnType getReturnType() {
        return ExpressionReturnType.Double;
    }

    @Override
    public final ExpressionType getType() {
        return ExpressionType.Double;
    }

}
