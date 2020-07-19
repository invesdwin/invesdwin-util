package de.invesdwin.util.math.expression.eval;

import de.invesdwin.util.math.expression.IExpression;

public interface IParsedExpression extends IExpression {

    IParsedExpression[] EMPTY_EXPRESSIONS = new IParsedExpression[0];

    IParsedExpression simplify();

}
