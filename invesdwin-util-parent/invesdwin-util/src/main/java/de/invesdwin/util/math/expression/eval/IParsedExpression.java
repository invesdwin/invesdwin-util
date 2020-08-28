package de.invesdwin.util.math.expression.eval;

import de.invesdwin.util.math.expression.ExpressionType;
import de.invesdwin.util.math.expression.IExpression;

public interface IParsedExpression extends IExpression {

    IParsedExpression[] EMPTY_EXPRESSIONS = new IParsedExpression[0];

    IParsedExpression simplify();

    ExpressionType getType();

    @Override
    default IParsedExpression asParsedExpression() {
        return this;
    }

}
