package de.invesdwin.util.math.expression.multiple.pool;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.concurrent.pool.AAgronaObjectPool;
import de.invesdwin.util.math.expression.multiple.NestedExpressionParser;

@NotThreadSafe
public final class NestedExpressionParserObjectPool extends AAgronaObjectPool<NestedExpressionParser> {

    private final String expression;

    public NestedExpressionParserObjectPool(final String expression) {
        this.expression = expression;
    }

    @Override
    protected NestedExpressionParser newObject() {
        return new NestedExpressionParser(expression);
    }

}
