package de.invesdwin.util.math.expression.multiple.pool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.concurrent.pool.AObjectPool;
import de.invesdwin.util.math.expression.multiple.NestedExpressionParser;

@NotThreadSafe
public final class NestedExpressionParserObjectPool extends AObjectPool<NestedExpressionParser> {

    private final List<NestedExpressionParser> nestedExpressionParserRotation = new ArrayList<NestedExpressionParser>();

    public NestedExpressionParserObjectPool(final String expression) {
        super(new NestedExpressionParserPoolableObjectFactory(expression));
    }

    @Override
    protected NestedExpressionParser internalBorrowObject() {
        if (nestedExpressionParserRotation.isEmpty()) {
            return factory.makeObject();
        }
        final NestedExpressionParser nestedExpressionParser = nestedExpressionParserRotation.remove(0);
        if (nestedExpressionParser != null) {
            return nestedExpressionParser;
        } else {
            return factory.makeObject();
        }
    }

    @Override
    public int getNumIdle() {
        return nestedExpressionParserRotation.size();
    }

    @Override
    public Collection<NestedExpressionParser> internalClear() {
        final Collection<NestedExpressionParser> removed = new ArrayList<NestedExpressionParser>();
        while (!nestedExpressionParserRotation.isEmpty()) {
            removed.add(nestedExpressionParserRotation.remove(0));
        }
        return removed;
    }

    @Override
    protected NestedExpressionParser internalAddObject() {
        final NestedExpressionParser pooled = factory.makeObject();
        nestedExpressionParserRotation.add(factory.makeObject());
        return pooled;
    }

    @Override
    protected void internalReturnObject(final NestedExpressionParser obj) {
        nestedExpressionParserRotation.add(obj);
    }

    @Override
    protected void internalInvalidateObject(final NestedExpressionParser obj) {
        //Nothing happens
    }

    @Override
    protected void internalRemoveObject(final NestedExpressionParser obj) {
        nestedExpressionParserRotation.remove(obj);
    }

}