package de.invesdwin.util.math.expression.variable;

import de.invesdwin.util.math.expression.ExpressionReturnType;
import de.invesdwin.util.math.expression.ExpressionType;
import de.invesdwin.util.math.expression.eval.variable.AVariableReference;
import de.invesdwin.util.math.expression.eval.variable.BooleanVariableReference;
import de.invesdwin.util.math.expression.lambda.IEvaluateBoolean;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanKey;

public interface IBooleanVariable extends IVariable {

    IEvaluateBooleanFDate newEvaluateBooleanFDate(String context);

    IEvaluateBooleanKey newEvaluateBooleanKey(String context);

    IEvaluateBoolean newEvaluateBoolean(String context);

    @Override
    default ExpressionReturnType getReturnType() {
        return ExpressionReturnType.Boolean;
    }

    @Override
    default ExpressionType getType() {
        return ExpressionType.Boolean;
    }

    @Override
    default AVariableReference<?> newReference(final String context) {
        return new BooleanVariableReference(context, this);
    }

}
