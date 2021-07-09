package de.invesdwin.util.math.expression.lambda.throwing;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.math.expression.IExpression;
import de.invesdwin.util.math.expression.lambda.IEvaluateDoubleFDate;
import de.invesdwin.util.time.date.IFDateProvider;

@Immutable
public class ThrowingEvaluateDoubleFDate implements IEvaluateDoubleFDate {

    private final Throwable throwable;

    public ThrowingEvaluateDoubleFDate(final Throwable throwable) {
        this.throwable = throwable;
    }

    @Override
    public double evaluateDouble(final IFDateProvider key) {
        throw Throwables.propagate(throwable);
    }

    public static IEvaluateDoubleFDate maybeWrap(final IExpression expression) {
        try {
            return expression.newEvaluateDoubleFDate();
        } catch (final Throwable t) {
            return new ThrowingEvaluateDoubleFDate(t);
        }
    }

}
