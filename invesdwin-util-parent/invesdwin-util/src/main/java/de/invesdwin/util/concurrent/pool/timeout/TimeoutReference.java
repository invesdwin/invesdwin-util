package de.invesdwin.util.concurrent.pool.timeout;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.buffer.NodeBufferingIterator.INode;

@NotThreadSafe
public final class TimeoutReference<E> implements INode<TimeoutReference<E>> {

    private E value;
    private long timeoutStartMillis;

    private TimeoutReference<E> next;

    public void set(final E value) {
        this.value = value;
        this.timeoutStartMillis = System.currentTimeMillis();
    }

    public void clear() {
        value = null;
        TimeoutReferenceObjectPool.<E> getInstance().returnObject(this);
    }

    public boolean isTimeoutExceeded(final long timeoutMillis) {
        return System.currentTimeMillis() - timeoutStartMillis >= timeoutMillis;
    }

    public E get() {
        return value;
    }

    @Override
    public TimeoutReference<E> getNext() {
        return next;
    }

    @Override
    public void setNext(final TimeoutReference<E> next) {
        this.next = next;
    }

}
