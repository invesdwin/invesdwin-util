package de.invesdwin.util.collections.array.primitive.queue.large;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.array.large.queue.IByteLargeQueue;
import de.invesdwin.util.collections.array.primitive.queue.IBytePrimitiveQueue;
import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;

/**
 * Adapter for using a {@link IByteLargeQueue} where a {@link IBytePrimitiveQueue} is expected, throws if the queue
 * grows beyond the maximum size of an int.
 */
@NotThreadSafe
public class ByteLargeAsPrimitiveQueue implements IBytePrimitiveQueue {

    private final IByteLargeQueue queue;

    public ByteLargeAsPrimitiveQueue(final IByteLargeQueue queue) {
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
