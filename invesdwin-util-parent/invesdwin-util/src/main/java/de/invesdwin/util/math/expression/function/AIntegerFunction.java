package de.invesdwin.util.math.expression.function;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.expression.ExpressionReturnType;
import de.invesdwin.util.math.expression.ExpressionType;
import de.invesdwin.util.math.expression.IExpression;
import de.invesdwin.util.math.expression.eval.IParsedExpression;
import de.invesdwin.util.math.expression.eval.function.IntegerFunctionCall;
import de.invesdwin.util.time.fdate.IFDateProvider;

@NotThreadSafe
public abstract class AIntegerFunction extends AFunction {

    public abstract int eval(IFDateProvider key, IExpression[] args);

    public abstract int eval(int key, IExpression[] args);

    public abstract int eval(IExpression[] args);

    @Override
    public final IParsedExpression newCall(final String context, final IParsedExpression[] parameters) {
        return new IntegerFunctionCall(context, this, parameters);
    }

    @Override
    public ExpressionReturnType getReturnType() {
        return ExpressionReturnType.Integer;
    }

    @Override
    public final ExpressionType getType() {
        return ExpressionType.Integer;
    }

}
