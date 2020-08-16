package de.invesdwin.util.math.expression.eval.function;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.expression.IExpression;
import de.invesdwin.util.math.expression.function.ADoubleFunction;
import de.invesdwin.util.time.fdate.IFDateProvider;

@NotThreadSafe
public abstract class ABinaryFunction extends ADoubleFunction {

    @Override
    public int getNumberOfArguments() {
        return 2;
    }

    @Override
    public double eval(final IFDateProvider key, final IExpression[] args) {
        final double a = args[0].evaluateDouble(key);
        final double b = args[1].evaluateDouble(key);
        return eval(a, b);
    }

    @Override
    public double eval(final int key, final IExpression[] args) {
        final double a = args[0].evaluateDouble(key);
        final double b = args[1].evaluateDouble(key);
        return eval(a, b);
    }

    @Override
    public double eval(final IExpression[] args) {
        final double a = args[0].evaluateDouble();
        final double b = args[1].evaluateDouble();
        return eval(a, b);
    }

    protected abstract double eval(double a, double b);

    @Override
    public boolean isNaturalFunction(final IExpression[] args) {
        return true;
    }
}
