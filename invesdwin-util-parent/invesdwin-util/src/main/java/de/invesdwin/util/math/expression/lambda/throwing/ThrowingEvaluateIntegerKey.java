package de.invesdwin.util.math.expression.lambda.throwing;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.math.expression.IExpression;
import de.invesdwin.util.math.expression.lambda.IEvaluateIntegerKey;

@Immutable
public class ThrowingEvaluateIntegerKey implements IEvaluateIntegerKey {

    private final Throwable throwable;

    public ThrowingEvaluateIntegerKey(final Throwable throwable) {
        this.throwable = throwable;
    }

    @Override
    public int evaluateInteger(final int key) {
        throw Throwables.propagate(throwable);
    }

    public static IEvaluateIntegerKey maybeWrap(final IExpression expression) {
        try {
            return expression.newEvaluateIntegerKey();
        } catch (final Throwable t) {
            return new ThrowingEvaluateIntegerKey(t);
        }
    }

}
