package de.invesdwin.util.math.expression.eval;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.expression.ExpressionType;

@Immutable
public final class BooleanConstantExpression extends ConstantExpression {

    public static final BooleanConstantExpression FALSE = new BooleanConstantExpression(false);
    public static final BooleanConstantExpression TRUE = new BooleanConstantExpression(true);

    private BooleanConstantExpression(final boolean value) {
        super(Doubles.checkedCast(value), ExpressionType.Boolean);
    }

    @Override
    public String toString() {
        return String.valueOf(newEvaluateBoolean().evaluateBoolean());
    }

    public static BooleanConstantExpression valueOf(final boolean value) {
        if (value) {
            return TRUE;
        } else {
            return FALSE;
        }
    }

}
