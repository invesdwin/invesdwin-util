package de.invesdwin.util.collections.array.large.queue.primitive;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.array.large.queue.IDoubleLargeQueue;
import de.invesdwin.util.collections.array.primitive.queue.IDoublePrimitiveQueue;

/**
 * Adapter for using a {@link IDoublePrimitiveQueue} where a {@link IDoubleLargeQueue} is expected, the int
 * based size is widened to a long, so nothing can overflow.
 */
@NotThreadSafe
public class DoublePrimitiveAsLargeQueue implements IDoubleLargeQueue {

    private final IDoublePrimitiveQueue queue;

    public DoublePrimitiveAsLargeQueue(final IDoublePrimitiveQueue queue) {
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
    public boolean offer(final double e) {
        return queue.offer(e);
    }

    @Override
    public double poll() {
        return queue.poll();
    }

    @Override
    public double peek() {
        return queue.peek();
    }

    @Override
    public void clear() {
        queue.clear();
    }

}
