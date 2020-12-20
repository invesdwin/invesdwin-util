package de.invesdwin.util.math.expression.eval.function;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.expression.IExpression;
import de.invesdwin.util.math.expression.function.ADoubleFunction;
import de.invesdwin.util.math.expression.lambda.IEvaluateDouble;
import de.invesdwin.util.math.expression.lambda.IEvaluateDoubleFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateDoubleKey;

@Immutable
public abstract class ADoubleUnaryFunction extends ADoubleFunction {

    @Override
    public int getNumberOfArguments() {
        return 1;
    }

    @Override
    public IEvaluateDoubleFDate newEvaluateDoubleFDate(final String context, final IExpression[] args) {
        final IEvaluateDoubleFDate aF = args[0].newEvaluateDoubleFDate();
        return key -> {
            final double a = aF.evaluateDouble(key);
            return eval(a);
        };
    }

    @Override
    public IEvaluateDoubleKey newEvaluateDoubleKey(final String context, final IExpression[] args) {
        final IEvaluateDoubleKey aF = args[0].newEvaluateDoubleKey();
        return key -> {
            final double a = aF.evaluateDouble(key);
            return eval(a);
        };
    }

    @Override
    public IEvaluateDouble newEvaluateDouble(final String context, final IExpression[] args) {
        final IEvaluateDouble aF = args[0].newEvaluateDouble();
        return () -> {
            final double a = aF.evaluateDouble();
            return eval(a);
        };
    }

    protected abstract double eval(double a);

    @Override
    public boolean isNaturalFunction(final IExpression[] args) {
        return true;
    }
}
