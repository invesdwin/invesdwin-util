package de.invesdwin.util.math.expression;

import de.invesdwin.util.math.expression.eval.IParsedExpression;

@FunctionalInterface
public interface IParsedExpressionProvider {

    IParsedExpression asParsedExpression();

}
