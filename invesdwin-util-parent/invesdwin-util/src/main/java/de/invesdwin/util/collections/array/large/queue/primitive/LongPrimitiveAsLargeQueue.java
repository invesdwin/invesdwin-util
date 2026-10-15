package de.invesdwin.util.collections.array.large.queue.primitive;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.array.large.queue.ILongLargeQueue;
import de.invesdwin.util.collections.array.primitive.queue.ILongPrimitiveQueue;

/**
 * Adapter for using a {@link ILongPrimitiveQueue} where a {@link ILongLargeQueue} is expected, the int based
 * size is widened to a long, so nothing can overflow.
 */
@NotThreadSafe
public class LongPrimitiveAsLargeQueue implements ILongLargeQueue {

    private final ILongPrimitiveQueue queue;

    public LongPrimitiveAsLargeQueue(final ILongPrimitiveQueue queue) {
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
    public boolean offer(final long e) {
        return queue.offer(e);
    }

    @Override
    public long poll() {
        return queue.poll();
    }

    @Override
    public long peek() {
        return queue.peek();
    }

    @Override
    public void clear() {
        queue.clear();
    }

}
