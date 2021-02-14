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

    /**
     * Return true if values are only availble point in time without history. E.g. dependant on active orders and thus
     * should be persisted for charts.
     */
    boolean shouldPersist();

    /**
     * Return true if this expression can be drawn. This might be false for command expressions that always return NaN.
     * In that case the children might be drawn.
     */
    boolean shouldDraw();

    /**
     * Return true if this variable does not depend on an outside context, e.g. current trade information, strategy
     * samples or values from other indicators.
     * 
     * When this is true, results might be cached and compressed as double/integer/boolean arrays or bitsets.
     */
    boolean shouldCompress();

    AVariableReference<?> newReference(String context);

}
