package de.invesdwin.util.math.expression.variable;

import de.invesdwin.util.math.expression.ExpressionReturnType;
import de.invesdwin.util.math.expression.ExpressionType;
import de.invesdwin.util.math.expression.eval.variable.AVariableReference;
import de.invesdwin.util.math.expression.eval.variable.DoubleVariableReference;
import de.invesdwin.util.math.expression.lambda.IEvaluateDouble;
import de.invesdwin.util.math.expression.lambda.IEvaluateDoubleFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateDoubleKey;

public interface IDoubleVariable extends IVariable {

    IEvaluateDoubleFDate newEvaluateDoubleFDate(String context);

    IEvaluateDoubleKey newEvaluateDoubleKey(String context);

    IEvaluateDouble newEvaluateDouble(String context);

    @Override
    default ExpressionReturnType getReturnType() {
        return ExpressionReturnType.Double;
    }

    @Override
    default ExpressionType getType() {
        return ExpressionType.Double;
    }

    @Override
    default AVariableReference<?> newReference(final String context) {
        return new DoubleVariableReference(context, this);
    }

}
