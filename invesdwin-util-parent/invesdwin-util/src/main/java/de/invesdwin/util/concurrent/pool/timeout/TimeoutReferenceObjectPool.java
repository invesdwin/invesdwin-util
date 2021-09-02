package de.invesdwin.util.concurrent.pool.timeout;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.iterable.buffer.NodeBufferingIterator;
import de.invesdwin.util.concurrent.pool.ASynchronizedBufferingIteratorObjectPool;

@ThreadSafe
public final class TimeoutReferenceObjectPool<E> extends ASynchronizedBufferingIteratorObjectPool<TimeoutReference<E>> {

    private static final int MAX_POOL_SIZE = 10_000;
    @SuppressWarnings("rawtypes")
    private static final TimeoutReferenceObjectPool INSTANCE = new TimeoutReferenceObjectPool<>();

    private TimeoutReferenceObjectPool() {
        super(new NodeBufferingIterator<>());
    }

    @Override
    protected TimeoutReference<E> newObject() {
        return new TimeoutReference<E>();
    }

    @Override
    public synchronized void returnObject(final TimeoutReference<E> element) {
        if (bufferingIterator.size() < MAX_POOL_SIZE) {
            super.returnObject(element);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> TimeoutReferenceObjectPool<T> getInstance() {
        return INSTANCE;
    }

}
