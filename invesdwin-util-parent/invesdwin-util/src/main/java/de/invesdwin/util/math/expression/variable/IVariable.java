package de.invesdwin.util.math.expression.variable;

import de.invesdwin.util.math.expression.ExpressionReturnType;
import de.invesdwin.util.time.fdate.FDate;

public interface IVariable {

    double getValue(FDate key);

    double getValue(int key);

    double getValue();

    String getExpressionName();

    String getName();

    String getDescription();

    ExpressionReturnType getType();

    boolean isConstant();

}
