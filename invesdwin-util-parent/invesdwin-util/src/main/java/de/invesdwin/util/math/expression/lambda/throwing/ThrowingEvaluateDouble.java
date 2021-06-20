package de.invesdwin.util.math.expression.lambda.throwing;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.math.expression.IExpression;
import de.invesdwin.util.math.expression.lambda.IEvaluateDouble;

@Immutable
public class ThrowingEvaluateDouble implements IEvaluateDouble {

    private final Throwable throwable;

    public ThrowingEvaluateDouble(final Throwable throwable) {
        this.throwable = throwable;
    }

    @Override
    public double evaluateDouble() {
        throw Throwables.propagate(throwable);
    }

    public static IEvaluateDouble maybeWrap(final IExpression expression) {
        try {
            return expression.newEvaluateDouble();
        } catch (final Throwable t) {
            return new ThrowingEvaluateDouble(t);
        }
    }

}
