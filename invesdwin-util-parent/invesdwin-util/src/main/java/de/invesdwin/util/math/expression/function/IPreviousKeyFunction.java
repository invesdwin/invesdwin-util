package de.invesdwin.util.math.expression.function;

import de.invesdwin.util.math.expression.eval.IParsedExpression;
import de.invesdwin.util.time.fdate.FDate;

public interface IPreviousKeyFunction {

    FDate getPreviousKey(FDate key, int index);

    int getPreviousKey(int key, int index);

    default double evaluateDouble(final IParsedExpression expression, final FDate previousKey) {
        return expression.evaluateDouble(previousKey);
    }

    default double evaluateDouble(final IParsedExpression expression, final int previousKey) {
        return expression.evaluateDouble(previousKey);
    }

    default Boolean evaluateBooleanNullable(final IParsedExpression expression, final FDate previousKey) {
        return expression.evaluateBooleanNullable(previousKey);
    }

    default Boolean evaluateBooleanNullable(final IParsedExpression expression, final int previousKey) {
        return expression.evaluateBooleanNullable(previousKey);
    }

}
