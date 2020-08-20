package de.invesdwin.util.math.expression.variable;

import de.invesdwin.util.math.expression.ExpressionReturnType;
import de.invesdwin.util.math.expression.ExpressionType;
import de.invesdwin.util.math.expression.eval.variable.AVariableReference;
import de.invesdwin.util.math.expression.eval.variable.BooleanNullableVariableReference;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanNullable;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanNullableFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanNullableKey;

public interface IBooleanNullableVariable extends IVariable {

    IEvaluateBooleanNullableFDate newEvaluateBooleanNullableFDate();

    IEvaluateBooleanNullableKey newEvaluateBooleanNullableKey();

    IEvaluateBooleanNullable newEvaluateBooleanNullable();

    @Override
    default ExpressionReturnType getReturnType() {
        return ExpressionReturnType.Boolean;
    }

    @Override
    default ExpressionType getType() {
        return ExpressionType.BooleanNullable;
    }

    @Override
    default AVariableReference<?> newReference(final String context) {
        return new BooleanNullableVariableReference(context, this);
    }

}
