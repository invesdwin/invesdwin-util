package de.invesdwin.util.math.expression.variable;

import de.invesdwin.util.math.expression.eval.variable.AVariableReference;

public interface IVariable extends IVariableDefinition {

    boolean isConstant();

    Object getProperty(String property);

    AVariableReference<?> newReference(String context);

}
