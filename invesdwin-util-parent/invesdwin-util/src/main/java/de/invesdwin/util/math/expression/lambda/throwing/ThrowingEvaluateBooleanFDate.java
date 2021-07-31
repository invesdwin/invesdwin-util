package de.invesdwin.util.math.expression.lambda.throwing;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.math.expression.IExpression;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanFDate;
import de.invesdwin.util.time.date.IFDateProvider;

@Immutable
public class ThrowingEvaluateBooleanFDate implements IEvaluateBooleanFDate {

    private final Throwable throwable;

    public ThrowingEvaluateBooleanFDate(final Throwable throwable) {
        this.throwable = throwable;
    }

    @Override
    public boolean evaluateBoolean(final IFDateProvider key) {
        throw Throwables.propagate(throwable);
    }

    public static IEvaluateBooleanFDate maybeWrap(final IExpression expression) {
        try {
            return expression.newEvaluateBooleanFDate();
        } catch (final Throwable t) {
            return new ThrowingEvaluateBooleanFDate(t);
        }
    }

}
