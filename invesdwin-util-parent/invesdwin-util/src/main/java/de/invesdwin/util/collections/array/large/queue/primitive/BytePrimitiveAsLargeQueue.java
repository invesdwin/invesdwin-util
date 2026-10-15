package de.invesdwin.util.collections.array.large.queue.primitive;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.array.large.queue.IByteLargeQueue;
import de.invesdwin.util.collections.array.primitive.queue.IBytePrimitiveQueue;

/**
 * Adapter for using a {@link IBytePrimitiveQueue} where a {@link IByteLargeQueue} is expected, the int based size is
 * widened to a long, so nothing can overflow.
 */
@NotThreadSafe
public class BytePrimitiveAsLargeQueue implements IByteLargeQueue {

    private final IBytePrimitiveQueue queue;

    public BytePrimitiveAsLargeQueue(final IBytePrimitiveQueue queue) {
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
    public boolean offer(final byte e) {
        return queue.offer(e);
    }

    @Override
    public byte poll() {
        return queue.poll();
    }

    @Override
    public byte peek() {
        return queue.peek();
    }

    @Override
    public void clear() {
        queue.clear();
    }

}
