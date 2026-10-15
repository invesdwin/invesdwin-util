package de.invesdwin.util.collections.array.large.queue.primitive;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.array.large.queue.IGenericLargeQueue;
import de.invesdwin.util.collections.array.primitive.queue.IGenericPrimitiveQueue;

/**
 * Adapter for using a {@link IGenericPrimitiveQueue} where a {@link IGenericLargeQueue} is expected, the int based size
 * is widened to a long, so nothing can overflow.
 */
@NotThreadSafe
public class GenericPrimitiveAsLargeQueue<E> implements IGenericLargeQueue<E> {

    private final IGenericPrimitiveQueue<E> queue;

    public GenericPrimitiveAsLargeQueue(final IGenericPrimitiveQueue<E> queue) {
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
