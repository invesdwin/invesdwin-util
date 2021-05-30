package de.invesdwin.util.math.expression.eval.function;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.expression.IExpression;
import de.invesdwin.util.math.expression.function.ADoubleFunction;
import de.invesdwin.util.math.expression.lambda.IEvaluateDouble;
import de.invesdwin.util.math.expression.lambda.IEvaluateDoubleFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateDoubleKey;

@Immutable
public abstract class ADoubleNullaryFunction extends ADoubleFunction {

    @Override
    public int getNumberOfArguments() {
        return 0;
    }

    @Override
    public IEvaluateDoubleFDate newEvaluateDoubleFDate(final String context, final IExpression[] args) {
        return key -> {
            return eval();
        };
    }

    @Override
    public IEvaluateDoubleKey newEvaluateDoubleKey(final String context, final IExpression[] args) {
        return key -> {
            return eval();
        };
    }

    @Override
    public IEvaluateDouble newEvaluateDouble(final String context, final IExpression[] args) {
        return () -> {
            return eval();
        };
    }

    protected abstract double eval();

    @Override
    public boolean isNaturalFunction(final IExpression[] args) {
        return true;
    }
}
