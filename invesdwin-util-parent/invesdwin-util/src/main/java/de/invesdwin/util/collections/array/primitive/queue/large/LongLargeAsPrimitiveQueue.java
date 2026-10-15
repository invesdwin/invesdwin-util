package de.invesdwin.util.collections.array.primitive.queue.large;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.array.large.queue.ILongLargeQueue;
import de.invesdwin.util.collections.array.primitive.queue.ILongPrimitiveQueue;
import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;

/**
 * Adapter for using a {@link ILongLargeQueue} where a {@link ILongPrimitiveQueue} is expected, throws if the
 * queue grows beyond the maximum size of an int.
 */
@NotThreadSafe
public class LongLargeAsPrimitiveQueue implements ILongPrimitiveQueue {

    private final ILongLargeQueue queue;

    public LongLargeAsPrimitiveQueue(final ILongLargeQueue queue) {
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
