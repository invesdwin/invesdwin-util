package de.invesdwin.util.math.expression.lambda.throwing;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.math.expression.IExpression;
import de.invesdwin.util.math.expression.lambda.IEvaluateInteger;

@Immutable
public class ThrowingEvaluateInteger implements IEvaluateInteger {

    private final Throwable throwable;

    public ThrowingEvaluateInteger(final Throwable throwable) {
        this.throwable = throwable;
    }

    @Override
    public int evaluateInteger() {
        throw Throwables.propagate(throwable);
    }

    public static IEvaluateInteger maybeWrap(final IExpression expression) {
        try {
            return expression.newEvaluateInteger();
        } catch (final Throwable t) {
            return new ThrowingEvaluateInteger(t);
        }
    }

}
