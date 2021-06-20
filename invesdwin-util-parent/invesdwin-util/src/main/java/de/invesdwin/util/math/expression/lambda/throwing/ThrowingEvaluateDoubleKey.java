package de.invesdwin.util.math.expression.lambda.throwing;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.math.expression.IExpression;
import de.invesdwin.util.math.expression.lambda.IEvaluateDoubleKey;

@Immutable
public class ThrowingEvaluateDoubleKey implements IEvaluateDoubleKey {

    private final Throwable throwable;

    public ThrowingEvaluateDoubleKey(final Throwable throwable) {
        this.throwable = throwable;
    }

    @Override
    public double evaluateDouble(final int key) {
        throw Throwables.propagate(throwable);
    }

    public static IEvaluateDoubleKey maybeWrap(final IExpression expression) {
        try {
            return expression.newEvaluateDoubleKey();
        } catch (final Throwable t) {
            return new ThrowingEvaluateDoubleKey(t);
        }
    }

}
