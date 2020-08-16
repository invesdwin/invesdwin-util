package de.invesdwin.util.math.expression.eval.function;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.expression.IExpression;
import de.invesdwin.util.math.expression.function.ADoubleFunction;
import de.invesdwin.util.time.fdate.IFDateProvider;

@NotThreadSafe
public abstract class ATernaryFunction extends ADoubleFunction {

    @Override
    public int getNumberOfArguments() {
        return 3;
    }

    @Override
    public double eval(final IFDateProvider key, final IExpression[] args) {
        final double a = args[0].evaluateDouble(key);
        final double b = args[1].evaluateDouble(key);
        final double c = args[2].evaluateDouble(key);
        return eval(a, b, c);
    }

    @Override
    public double eval(final int key, final IExpression[] args) {
        final double a = args[0].evaluateDouble(key);
        final double b = args[1].evaluateDouble(key);
        final double c = args[2].evaluateDouble(key);
        return eval(a, b, c);
    }

    @Override
    public double eval(final IExpression[] args) {
        final double a = args[0].evaluateDouble();
        final double b = args[1].evaluateDouble();
        final double c = args[2].evaluateDouble();
        return eval(a, b, c);
    }

    protected abstract double eval(double a, double b, double c);

    @Override
    public boolean isNaturalFunction(final IExpression[] args) {
        return true;
    }
}
