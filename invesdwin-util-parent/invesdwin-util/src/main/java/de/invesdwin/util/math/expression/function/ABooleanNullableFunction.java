package de.invesdwin.util.math.expression.function;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.expression.IExpression;
import de.invesdwin.util.math.expression.eval.IParsedExpression;
import de.invesdwin.util.math.expression.eval.call.BooleanNullableFunctionCall;
import de.invesdwin.util.time.fdate.FDate;

@NotThreadSafe
public abstract class ABooleanNullableFunction extends AFunction {

    public abstract Boolean eval(FDate key, IExpression[] args);

    public abstract Boolean eval(int key, IExpression[] args);

    public abstract Boolean eval(IExpression[] args);

    @Override
    public final IParsedExpression newCall(final String context, final IParsedExpression[] parameters) {
        return new BooleanNullableFunctionCall(context, this, parameters);
    }

    @Override
    public final FunctionType getType() {
        return FunctionType.BooleanNullable;
    }

}
