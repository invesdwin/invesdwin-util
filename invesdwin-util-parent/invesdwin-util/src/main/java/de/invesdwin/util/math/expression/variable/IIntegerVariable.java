package de.invesdwin.util.math.expression.variable;

import de.invesdwin.util.math.expression.ExpressionReturnType;
import de.invesdwin.util.math.expression.ExpressionType;
import de.invesdwin.util.math.expression.eval.variable.AVariableReference;
import de.invesdwin.util.math.expression.eval.variable.IntegerVariableReference;
import de.invesdwin.util.math.expression.lambda.IEvaluateInteger;
import de.invesdwin.util.math.expression.lambda.IEvaluateIntegerFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateIntegerKey;

public interface IIntegerVariable extends IVariable {

    IEvaluateIntegerFDate newEvaluateIntegerFDate();

    IEvaluateIntegerKey newEvaluateIntegerKey();

    IEvaluateInteger newEvaluateInteger();

    @Override
    default ExpressionReturnType getReturnType() {
        return ExpressionReturnType.Integer;
    }

    @Override
    default ExpressionType getType() {
        return ExpressionType.Integer;
    }

    @Override
    default AVariableReference<?> newReference(final String context) {
        return new IntegerVariableReference(context, this);
    }

}
