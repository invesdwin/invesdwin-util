package de.invesdwin.util.collections.array.primitive.queue.large;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.array.large.queue.IIntegerLargeQueue;
import de.invesdwin.util.collections.array.primitive.queue.IIntegerPrimitiveQueue;
import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;

/**
 * Adapter for using a {@link IIntegerLargeQueue} where a {@link IIntegerPrimitiveQueue} is expected, throws if the
 * queue grows beyond the maximum size of an int.
 */
@NotThreadSafe
public class IntegerLargeAsPrimitiveQueue implements IIntegerPrimitiveQueue {

    private final IIntegerLargeQueue queue;

    public IntegerLargeAsPrimitiveQueue(final IIntegerLargeQueue queue) {
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
