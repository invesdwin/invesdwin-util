package de.invesdwin.util.math.expression.eval.function;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.expression.IExpression;
import de.invesdwin.util.math.expression.function.ADoubleFunction;
import de.invesdwin.util.math.expression.lambda.IEvaluateDouble;
import de.invesdwin.util.math.expression.lambda.IEvaluateDoubleFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateDoubleKey;

@NotThreadSafe
public abstract class ADoubleBinaryFunction extends ADoubleFunction {

    @Override
    public int getNumberOfArguments() {
        return 2;
    }

    @Override
    public IEvaluateDoubleFDate newEvaluateDoubleFDate(final String context, final IExpression[] args) {
        final IEvaluateDoubleFDate aF = args[0].newEvaluateDoubleFDate();
        final IEvaluateDoubleFDate bF = args[1].newEvaluateDoubleFDate();
        return key1 -> {
            final double a = aF.evaluateDouble(key1);
            final double b = bF.evaluateDouble(key1);
            return eval(a, b);
        };
    }

    @Override
    public IEvaluateDoubleKey newEvaluateDoubleKey(final String context, final IExpression[] args) {
        final IEvaluateDoubleKey aF = args[0].newEvaluateDoubleKey();
        final IEvaluateDoubleKey bF = args[1].newEvaluateDoubleKey();
        return key -> {
            final double a = aF.evaluateDouble(key);
            final double b = bF.evaluateDouble(key);
            return eval(a, b);
        };
    }

    @Override
    public IEvaluateDouble newEvaluateDouble(final String context, final IExpression[] args) {
        final IEvaluateDouble aF = args[0].newEvaluateDouble();
        final IEvaluateDouble bF = args[1].newEvaluateDouble();
        return () -> {
            final double a = aF.evaluateDouble();
            final double b = bF.evaluateDouble();
            return eval(a, b);
        };
    }

    protected abstract double eval(double a, double b);

    @Override
    public boolean isNaturalFunction(final IExpression[] args) {
        return true;
    }
}
