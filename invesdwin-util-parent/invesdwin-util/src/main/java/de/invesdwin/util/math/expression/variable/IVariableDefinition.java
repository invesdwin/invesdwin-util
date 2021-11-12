package de.invesdwin.util.math.expression.variable;

import de.invesdwin.util.math.expression.ExpressionReturnType;
import de.invesdwin.util.math.expression.ExpressionType;

public interface IVariableDefinition {

    String getExpressionName();

    String getName();

    String getDescription();

    ExpressionReturnType getReturnType();

    ExpressionType getType();

}
