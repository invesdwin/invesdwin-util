package de.invesdwin.util.math.expression.lambda.throwing;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.math.expression.IExpression;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanKey;

@Immutable
public class ThrowingEvaluateBooleanKey implements IEvaluateBooleanKey {

    private final Throwable throwable;

    public ThrowingEvaluateBooleanKey(final Throwable throwable) {
        this.throwable = throwable;
    }

    @Override
    public boolean evaluateBoolean(final int key) {
        throw Throwables.propagate(throwable);
    }

    public static IEvaluateBooleanKey maybeWrap(final IExpression expression) {
        try {
            return expression.newEvaluateBooleanKey();
        } catch (final Throwable t) {
            return new ThrowingEvaluateBooleanKey(t);
        }
    }

}
