package de.invesdwin.util.collections.array.large.queue.primitive;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.array.large.queue.IBooleanLargeQueue;
import de.invesdwin.util.collections.array.primitive.queue.IBooleanPrimitiveQueue;

/**
 * Adapter for using a {@link IBooleanPrimitiveQueue} where a {@link IBooleanLargeQueue} is expected, the int based size
 * is widened to a long, so nothing can overflow.
 */
@NotThreadSafe
public class BooleanPrimitiveAsLargeQueue implements IBooleanLargeQueue {

    private final IBooleanPrimitiveQueue queue;

    public BooleanPrimitiveAsLargeQueue(final IBooleanPrimitiveQueue queue) {
        this.queue = queue;
    }

    @Override
    public long size() {
        return queue.size();
    }

    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    @Override
    public boolean offer(final boolean e) {
        return queue.offer(e);
    }

    @Override
    public boolean poll() {
        return queue.poll();
    }

    @Override
    public boolean peek() {
        return queue.peek();
    }

    @Override
    public void clear() {
        queue.clear();
    }

}
