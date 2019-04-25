package de.invesdwin.util.math.expression.eval;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.expression.AFunction;
import de.invesdwin.util.math.expression.IExpression;
import de.invesdwin.util.time.fdate.FDate;

@Immutable
public abstract class AUnaryFunction extends AFunction {

    @Override
    public int getNumberOfArguments() {
        return 1;
    }

    @Override
    public double eval(final FDate key, final IExpression[] args) {
        final double a = args[0].evaluateDouble(key);
        return eval(a);
    }

    @Override
    public double eval(final int key, final IExpression[] args) {
        final double a = args[0].evaluateDouble(key);
        return eval(a);
    }

    @Override
    public double eval(final IExpression[] args) {
        final double a = args[0].evaluateDouble();
        return eval(a);
    }

    protected abstract double eval(double a);

    @Override
    public boolean isNaturalFunction() {
        return true;
    }
}
