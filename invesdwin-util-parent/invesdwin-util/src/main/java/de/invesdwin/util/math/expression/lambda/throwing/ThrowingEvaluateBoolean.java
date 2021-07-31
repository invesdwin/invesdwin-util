package de.invesdwin.util.math.expression.lambda.throwing;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.math.expression.IExpression;
import de.invesdwin.util.math.expression.lambda.IEvaluateBoolean;

@Immutable
public class ThrowingEvaluateBoolean implements IEvaluateBoolean {

    private final Throwable throwable;

    public ThrowingEvaluateBoolean(final Throwable throwable) {
        this.throwable = throwable;
    }

    @Override
    public boolean evaluateBoolean() {
        throw Throwables.propagate(throwable);
    }

    public static IEvaluateBoolean maybeWrap(final IExpression expression) {
        try {
            return expression.newEvaluateBoolean();
        } catch (final Throwable t) {
            return new ThrowingEvaluateBoolean(t);
        }
    }

}
