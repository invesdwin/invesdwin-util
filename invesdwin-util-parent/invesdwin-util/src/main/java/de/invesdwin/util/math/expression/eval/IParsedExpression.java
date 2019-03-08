package de.invesdwin.util.math.expression.eval;

import de.invesdwin.util.math.expression.IExpression;

public interface IParsedExpression extends IExpression {

    IParsedExpression simplify();

}
