package de.invesdwin.util.collections.array.primitive.queue.large;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.array.large.queue.IDoubleLargeQueue;
import de.invesdwin.util.collections.array.primitive.queue.IDoublePrimitiveQueue;
import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;

/**
 * Adapter for using a {@link IDoubleLargeQueue} where a {@link IDoublePrimitiveQueue} is expected, throws if
 * the queue grows beyond the maximum size of an int.
 */
@NotThreadSafe
public class DoubleLargeAsPrimitiveQueue implements IDoublePrimitiveQueue {

    private final IDoubleLargeQueue queue;

    public DoubleLargeAsPrimitiveQueue(final IDoubleLargeQueue queue) {
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
