package de.invesdwin.util.collections.array.primitive.queue.large;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.array.large.queue.IBooleanLargeQueue;
import de.invesdwin.util.collections.array.primitive.queue.IBooleanPrimitiveQueue;
import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;

/**
 * Adapter for using a {@link IBooleanLargeQueue} where a {@link IBooleanPrimitiveQueue} is expected, throws if the
 * queue grows beyond the maximum size of an int.
 */
@NotThreadSafe
public class BooleanLargeAsPrimitiveQueue implements IBooleanPrimitiveQueue {

    private final IBooleanLargeQueue queue;

    public BooleanLargeAsPrimitiveQueue(final IBooleanLargeQueue queue) {
        this.queue = queue;
        if (Throwables.isDebugStackTraceEnabled()) {
            ByteBuffers.checkedCast(queue.size());
        }
    }

    @Override
    public int size() {
        return ByteBuffers.checkedCastNoOverflow(queue.size());
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
