package de.invesdwin.util.math.expression.variable;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.expression.lambda.IEvaluateBoolean;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanKey;

@Immutable
public abstract class ABooleanConstant extends AConstant implements IBooleanVariable {

    private final boolean value;

    public ABooleanConstant(final boolean value) {
        this.value = value;
    }

    @Override
    public final IEvaluateBoolean newEvaluateBoolean(final String context) {
        return () -> value;
    }

    @Override
    public final IEvaluateBooleanFDate newEvaluateBooleanFDate(final String context) {
        return key -> value;
    }

    @Override
    public final IEvaluateBooleanKey newEvaluateBooleanKey(final String context) {
        return key -> value;
    }

    @Override
    public String toString() {
        return getExpressionName() + ": " + value;
    }

}
