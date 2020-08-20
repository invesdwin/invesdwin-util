package de.invesdwin.util.math.expression.function;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.expression.ExpressionReturnType;
import de.invesdwin.util.math.expression.ExpressionType;
import de.invesdwin.util.math.expression.IExpression;
import de.invesdwin.util.math.expression.eval.IParsedExpression;
import de.invesdwin.util.math.expression.eval.function.BooleanFunctionCall;
import de.invesdwin.util.time.fdate.IFDateProvider;

@NotThreadSafe
public abstract class ABooleanFunction extends AFunction {

    public abstract boolean eval(IFDateProvider key, IExpression[] args);

    public abstract boolean eval(int key, IExpression[] args);

    public abstract boolean eval(IExpression[] args);

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
