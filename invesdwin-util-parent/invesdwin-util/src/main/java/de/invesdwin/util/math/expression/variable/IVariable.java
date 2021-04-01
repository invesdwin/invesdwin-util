package de.invesdwin.util.math.expression.variable;

import de.invesdwin.util.math.expression.ExpressionReturnType;
import de.invesdwin.util.math.expression.ExpressionType;
import de.invesdwin.util.math.expression.eval.variable.AVariableReference;

public interface IVariable {

    String getExpressionName();

    String getName();

    String getDescription();

    ExpressionReturnType getReturnType();

    ExpressionType getType();

    boolean isConstant();

    Object getProperty(String property);

    AVariableReference<?> newReference(String context);

}
