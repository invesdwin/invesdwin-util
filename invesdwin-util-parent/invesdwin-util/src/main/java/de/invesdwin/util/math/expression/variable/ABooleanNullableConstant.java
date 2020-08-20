package de.invesdwin.util.math.expression.variable;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanNullable;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanNullableFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanNullableKey;

@Immutable
public abstract class ABooleanNullableConstant extends AConstant implements IBooleanNullableVariable {

    private final Boolean value;

    public ABooleanNullableConstant(final Boolean value) {
        this.value = value;
    }

    @Override
    public final IEvaluateBooleanNullable newEvaluateBooleanNullable() {
        return () -> value;
    }

    @Override
    public final IEvaluateBooleanNullableFDate newEvaluateBooleanNullableFDate() {
        return key -> value;
    }

    @Override
    public final IEvaluateBooleanNullableKey newEvaluateBooleanNullableKey() {
        return key -> value;
    }

    @Override
    public String toString() {
        return getExpressionName() + ": " + value;
    }

}
