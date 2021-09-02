package de.invesdwin.util.math.expression.multiple.pool;

import javax.annotation.concurrent.NotThreadSafe;

import org.agrona.concurrent.ManyToManyConcurrentArrayQueue;

import de.invesdwin.util.concurrent.pool.AQueueObjectPool;
import de.invesdwin.util.math.expression.multiple.NestedExpressionParser;
import de.invesdwin.util.streams.buffer.ByteBuffers;

@NotThreadSafe
public final class NestedExpressionParserObjectPool extends AQueueObjectPool<NestedExpressionParser> {

    private final String expression;

    public NestedExpressionParserObjectPool(final String expression) {
        super(new ManyToManyConcurrentArrayQueue<>(ByteBuffers.MAX_POOL_SIZE));
        this.expression = expression;
    }

    @Override
    protected NestedExpressionParser newObject() {
        return new NestedExpressionParser(expression);
    }

}
