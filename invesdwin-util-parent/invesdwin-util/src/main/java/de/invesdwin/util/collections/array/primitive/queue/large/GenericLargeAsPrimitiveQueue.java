package de.invesdwin.util.collections.array.primitive.queue.large;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.array.large.queue.IGenericLargeQueue;
import de.invesdwin.util.collections.array.primitive.queue.IGenericPrimitiveQueue;
import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;

/**
 * Adapter for using a {@link IGenericLargeQueue} where a {@link IGenericPrimitiveQueue} is expected, throws if the
 * queue grows beyond the maximum size of an int.
 */
@NotThreadSafe
public class GenericLargeAsPrimitiveQueue<E> implements IGenericPrimitiveQueue<E> {

    private final IGenericLargeQueue<E> queue;

    public GenericLargeAsPrimitiveQueue(final IGenericLargeQueue<E> queue) {
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
    public boolean offer(final E e) {
        return queue.offer(e);
    }

    @Override
    public E poll() {
        return queue.poll();
    }

    @Override
    public E peek() {
        return queue.peek();
    }

    @Override
    public void clear() {
        queue.clear();
    }

}
