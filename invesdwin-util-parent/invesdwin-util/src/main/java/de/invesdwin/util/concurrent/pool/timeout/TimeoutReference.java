package de.invesdwin.util.concurrent.pool.timeout;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.buffer.NodeBufferingIterator.INode;
import de.invesdwin.util.lang.Closeables;

@NotThreadSafe
public final class TimeoutReference<E> implements INode<TimeoutReference<E>> {

    private E value;
    private long timeoutStartMillis;

    private TimeoutReference<E> next;
    private TimeoutReference<E> prev;

    public void set(final E value) {
        if (value == null) {
            throw new IllegalStateException("value should not be set to null");
        }
        this.value = value;
        this.timeoutStartMillis = System.currentTimeMillis();
    }

    public void clear() {
        if (value != null) {
            Closeables.closeQuietly(value);
            value = null;
            TimeoutReferenceObjectPool.<E> getInstance().returnObject(this);
        } else {
            throw new IllegalStateException("value already cleared");
        }
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

    @Override
    public TimeoutReference<E> getPrev() {
        return prev;
    }

    @Override
    public void setPrev(final TimeoutReference<E> prev) {
        this.prev = prev;
    }

}
