package de.invesdwin.util.math.expression.function;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.expression.IExpression;
import de.invesdwin.util.math.expression.eval.IParsedExpression;
import de.invesdwin.util.math.expression.eval.function.DoubleFunctionCall;
import de.invesdwin.util.time.fdate.IFDateProvider;

@NotThreadSafe
public abstract class ADoubleFunction extends AFunction {

    public abstract double eval(IFDateProvider key, IExpression[] args);

    public abstract double eval(int key, IExpression[] args);

    public abstract double eval(IExpression[] args);

    @Override
    public final IParsedExpression newCall(final String context, final IParsedExpression[] parameters) {
        return new DoubleFunctionCall(context, this, parameters);
    }

    @Override
    public final FunctionType getType() {
        return FunctionType.Double;
    }

}
