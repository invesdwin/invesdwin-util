package de.invesdwin.util.collections.array.large.queue.primitive;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.array.large.queue.IIntegerLargeQueue;
import de.invesdwin.util.collections.array.primitive.queue.IIntegerPrimitiveQueue;

/**
 * Adapter for using a {@link IIntegerPrimitiveQueue} where a {@link IIntegerLargeQueue} is expected, the int
 * based size is widened to a long, so nothing can overflow.
 */
@NotThreadSafe
public class IntegerPrimitiveAsLargeQueue implements IIntegerLargeQueue {

    private final IIntegerPrimitiveQueue queue;

    public IntegerPrimitiveAsLargeQueue(final IIntegerPrimitiveQueue queue) {
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
    public boolean offer(final int e) {
        return queue.offer(e);
    }

    @Override
    public int poll() {
        return queue.poll();
    }

    @Override
    public int peek() {
        return queue.peek();
    }

    @Override
    public void clear() {
        queue.clear();
    }

}
