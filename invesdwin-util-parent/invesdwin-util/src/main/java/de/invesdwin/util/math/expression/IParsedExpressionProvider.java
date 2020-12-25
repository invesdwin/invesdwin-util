package de.invesdwin.util.math.expression;

import de.invesdwin.util.math.expression.eval.IParsedExpression;

@FunctionalInterface
public interface IParsedExpressionProvider {

    IParsedExpression asParsedExpression();

    static IParsedExpression toParsedExpression(final IParsedExpressionProvider provider) {
        if (provider == null) {
            return null;
        } else {
            return provider.asParsedExpression();
        }
    }

}
