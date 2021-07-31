package de.invesdwin.util.math.expression.lambda.throwing;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.math.expression.IExpression;
import de.invesdwin.util.math.expression.lambda.IEvaluateIntegerFDate;
import de.invesdwin.util.time.date.IFDateProvider;

@Immutable
public class ThrowingEvaluateIntegerFDate implements IEvaluateIntegerFDate {

    private final Throwable throwable;

    public ThrowingEvaluateIntegerFDate(final Throwable throwable) {
        this.throwable = throwable;
    }

    @Override
    public int evaluateInteger(final IFDateProvider key) {
        throw Throwables.propagate(throwable);
    }

    public static IEvaluateIntegerFDate maybeWrap(final IExpression expression) {
        try {
            return expression.newEvaluateIntegerFDate();
        } catch (final Throwable t) {
            return new ThrowingEvaluateIntegerFDate(t);
        }
    }

}
