package de.invesdwin.util.math.expression.lambda.throwing;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.math.expression.IExpression;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanNullableFDate;
import de.invesdwin.util.time.date.IFDateProvider;

@Immutable
public class ThrowingEvaluateBooleanNullableFDate implements IEvaluateBooleanNullableFDate {

    private final Throwable throwable;

    public ThrowingEvaluateBooleanNullableFDate(final Throwable throwable) {
        this.throwable = throwable;
    }

    @Override
    public Boolean evaluateBooleanNullable(final IFDateProvider key) {
        throw Throwables.propagate(throwable);
    }

    public static IEvaluateBooleanNullableFDate maybeWrap(final IExpression expression) {
        try {
            return expression.newEvaluateBooleanNullableFDate();
        } catch (final Throwable t) {
            return new ThrowingEvaluateBooleanNullableFDate(t);
        }
    }

}
