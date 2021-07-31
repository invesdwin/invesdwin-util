package de.invesdwin.util.math.expression.lambda.throwing;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.math.expression.IExpression;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanNullable;

@Immutable
public class ThrowingEvaluateBooleanNullable implements IEvaluateBooleanNullable {

    private final Throwable throwable;

    public ThrowingEvaluateBooleanNullable(final Throwable throwable) {
        this.throwable = throwable;
    }

    @Override
    public Boolean evaluateBooleanNullable() {
        throw Throwables.propagate(throwable);
    }

    public static IEvaluateBooleanNullable maybeWrap(final IExpression expression) {
        try {
            return expression.newEvaluateBooleanNullable();
        } catch (final Throwable t) {
            return new ThrowingEvaluateBooleanNullable(t);
        }
    }

}
