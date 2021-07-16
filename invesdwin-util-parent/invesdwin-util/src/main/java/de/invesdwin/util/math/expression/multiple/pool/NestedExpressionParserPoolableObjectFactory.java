package de.invesdwin.util.math.expression.multiple.pool;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.concurrent.pool.IPoolableObjectFactory;
import de.invesdwin.util.math.expression.multiple.NestedExpressionParser;

@Immutable
public final class NestedExpressionParserPoolableObjectFactory
        implements IPoolableObjectFactory<NestedExpressionParser> {

    private final String expression;

    public NestedExpressionParserPoolableObjectFactory(final String expression) {
        this.expression = expression;
    }

    @Override
    public NestedExpressionParser makeObject() {
        return new NestedExpressionParser(expression);
    }

    @Override
    public void destroyObject(final NestedExpressionParser obj) {
    }

    @Override
    public boolean validateObject(final NestedExpressionParser obj) {
        return true;
    }

    @Override
    public void activateObject(final NestedExpressionParser obj) {
    }

    @Override
    public void passivateObject(final NestedExpressionParser obj) {

    }

}
