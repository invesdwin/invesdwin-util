package de.invesdwin.util.math.expression.eval;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.expression.AFunction;
import de.invesdwin.util.math.expression.IExpression;
import de.invesdwin.util.time.fdate.FDate;

@NotThreadSafe
public abstract class ABinaryFunction extends AFunction {

    @Override
    public int getNumberOfArguments() {
        return 2;
    }

    @Override
    public double eval(final FDate key, final IExpression[] args) {
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
    public boolean isNaturalFunction() {
        return true;
    }
}
