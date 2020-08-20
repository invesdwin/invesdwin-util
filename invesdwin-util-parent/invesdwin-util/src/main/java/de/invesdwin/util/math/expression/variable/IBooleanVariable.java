package de.invesdwin.util.math.expression.variable;

import de.invesdwin.util.math.expression.ExpressionReturnType;
import de.invesdwin.util.math.expression.ExpressionType;
import de.invesdwin.util.math.expression.eval.variable.AVariableReference;
import de.invesdwin.util.math.expression.eval.variable.BooleanVariableReference;
import de.invesdwin.util.time.fdate.IFDateProvider;

public interface IBooleanVariable extends IVariable {

    boolean getValue(IFDateProvider key);

    boolean getValue(int key);

    boolean getValue();

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
