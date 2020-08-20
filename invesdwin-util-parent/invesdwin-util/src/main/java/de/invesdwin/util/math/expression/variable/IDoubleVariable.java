package de.invesdwin.util.math.expression.variable;

import de.invesdwin.util.math.expression.ExpressionReturnType;
import de.invesdwin.util.math.expression.ExpressionType;
import de.invesdwin.util.math.expression.eval.variable.AVariableReference;
import de.invesdwin.util.math.expression.eval.variable.DoubleVariableReference;
import de.invesdwin.util.time.fdate.IFDateProvider;

public interface IDoubleVariable extends IVariable {

    double getValue(IFDateProvider key);

    double getValue(int key);

    double getValue();

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
