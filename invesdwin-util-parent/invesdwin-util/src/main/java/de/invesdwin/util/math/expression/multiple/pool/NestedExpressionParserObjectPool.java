package de.invesdwin.util.math.expression.multiple.pool;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.buffer.NodeBufferingIterator;
import de.invesdwin.util.concurrent.pool.commons.ACommonsObjectPool;
import de.invesdwin.util.math.expression.multiple.NestedExpressionParser;

@NotThreadSafe
public final class NestedExpressionParserObjectPool extends ACommonsObjectPool<NestedExpressionParser> {

    private final NodeBufferingIterator<NestedExpressionParser> nestedExpressionParserRotation = new NodeBufferingIterator<NestedExpressionParser>();

    public NestedExpressionParserObjectPool(final String expression) {
        super(new NestedExpressionParserPoolableObjectFactory(expression));
    }

    @Override
    protected NestedExpressionParser internalBorrowObject() {
        if (nestedExpressionParserRotation.isEmpty()) {
            return factory.makeObject();
        }
        final NestedExpressionParser nestedExpressionParser = nestedExpressionParserRotation.next();
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
            removed.add(nestedExpressionParserRotation.next());
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
        nestedExpressionParserRotation.next();
    }

}
