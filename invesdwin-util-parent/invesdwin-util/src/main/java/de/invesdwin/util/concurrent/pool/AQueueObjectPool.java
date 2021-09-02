package de.invesdwin.util.concurrent.pool;

import java.util.Queue;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public abstract class AQueueObjectPool<E> implements IObjectPool<E> {

    protected final Queue<E> queue;

    public AQueueObjectPool(final Queue<E> queue) {
        this.queue = queue;
    }

    @Override
    public E borrowObject() {
        final E element = queue.poll();
        if (element != null) {
            return element;
        } else {
            return newObject();
        }
    }

    protected abstract E newObject();

    @Override
    public void returnObject(final E element) {
        queue.offer(element);
    }

    @Override
    public void clear() {
        queue.clear();
    }

}
