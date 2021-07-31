package de.invesdwin.util.math.expression.lambda.throwing;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.math.expression.IExpression;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanNullableKey;

@Immutable
public class ThrowingEvaluateBooleanNullableKey implements IEvaluateBooleanNullableKey {

    private final Throwable throwable;

    public ThrowingEvaluateBooleanNullableKey(final Throwable throwable) {
        this.throwable = throwable;
    }

    @Override
    public Boolean evaluateBooleanNullable(final int key) {
        throw Throwables.propagate(throwable);
    }

    public static IEvaluateBooleanNullableKey maybeWrap(final IExpression expression) {
        try {
            return expression.newEvaluateBooleanNullableKey();
        } catch (final Throwable t) {
            return new ThrowingEvaluateBooleanNullableKey(t);
        }
    }

}
