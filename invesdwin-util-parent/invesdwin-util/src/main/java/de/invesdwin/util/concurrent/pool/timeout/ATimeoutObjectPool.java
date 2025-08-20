package de.invesdwin.util.concurrent.pool.timeout;

import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.concurrent.ThreadSafe;

import com.google.common.util.concurrent.ListenableScheduledFuture;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.iterable.buffer.IBufferingIterator;
import de.invesdwin.util.collections.iterable.buffer.NodeBufferingIterator;
import de.invesdwin.util.concurrent.Executors;
import de.invesdwin.util.concurrent.WrappedScheduledExecutorService;
import de.invesdwin.util.concurrent.pool.ICloseableObjectPool;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.Duration;

@ThreadSafe
public abstract class ATimeoutObjectPool<E> implements ICloseableObjectPool<E> {

    public static final AtomicLong ACTIVE_POOLS = new AtomicLong();
    private static WrappedScheduledExecutorService scheduledExecutor;

    protected IBufferingIterator<TimeoutReference<E>> bufferingIterator;
    private final long timeoutMillis;
    private ListenableScheduledFuture<?> scheduledFuture;

    public ATimeoutObjectPool(final Duration timeout, final Duration checkInverval) {
        this.bufferingIterator = new NodeBufferingIterator<>();
        this.timeoutMillis = timeout.longValue(FTimeUnit.MILLISECONDS);

        synchronized (ATimeoutObjectPool.class) {
            ACTIVE_POOLS.incrementAndGet();
            this.scheduledFuture = getScheduledExecutor().scheduleAtFixedRate(this::checkTimeouts, 0,
                    checkInverval.longValue(), checkInverval.getTimeUnit().timeUnitValue());
        }
    }

    protected int getMinimumSize() {
        return 0;
    }

    protected synchronized void checkTimeouts() {
        final int minimumSize = getMinimumSize();
        if (bufferingIterator.size() > minimumSize) {
            final ICloseableIterator<TimeoutReference<E>> iterator = bufferingIterator.iterator();
            try {
                while (true) {
                    final TimeoutReference<E> reference = iterator.next();
                    if (reference.isTimeoutExceeded(timeoutMillis)) {
                        iterator.remove();
                        final E element = reference.get();
                        if (element != null) {
                            invalidateObject(element);
                            reference.clear();
                        }
                        if (bufferingIterator.size() <= minimumSize) {
                            return;
                        }
                    }
                }
            } catch (final NoSuchElementException e) {
                //end reached
            }
        }
    }

    public static synchronized WrappedScheduledExecutorService getScheduledExecutor() {
        if (scheduledExecutor == null) {
            //reduce cpu load by using max 1 thread
            scheduledExecutor = Executors
                    .newScheduledThreadPool(ATimeoutObjectPool.class.getSimpleName() + "_SCHEDULER", 1)
                    .setDynamicThreadName(false);
        }
        return scheduledExecutor;
    }

    public static synchronized void maybeCloseScheduledExecutor() {
        if (ACTIVE_POOLS.get() == 0L) {
            if (scheduledExecutor != null) {
                scheduledExecutor.shutdownNow();
                scheduledExecutor = null;
            }
        }
    }

    @Override
    public synchronized E borrowObject() {
        try {
            final TimeoutReference<E> reference = bufferingIterator.next();
            if (reference != null) {
                final E element = reference.get();
                if (element == null) {
                    return newObject();
                } else {
                    reference.clear();
                    return element;
                }
            } else {
                return newObject();
            }
        } catch (final NoSuchElementException e) {
            return newObject();
        }
    }

    protected abstract E newObject();

    @Override
    public synchronized void returnObject(final E element) {
        if (element == null) {
            return;
        }
        if (passivateObject(element)) {
            final TimeoutReference<E> reference = TimeoutReferenceObjectPool.<E> getInstance().borrowObject();
            reference.set(element);
            bufferingIterator.add(reference);
        } else {
            invalidateObject(element);
        }
    }

    protected abstract boolean passivateObject(E element);

    @Override
    public synchronized void clear() {
        try {
            while (true) {
                final TimeoutReference<E> reference = bufferingIterator.next();
                final E element = reference.get();
                if (element != null) {
                    invalidateObject(element);
                    reference.clear();
                }
            }
        } catch (final NoSuchElementException e) {
            //end reached
        }
    }

    @Override
    public synchronized int size() {
        return bufferingIterator.size();
    }

    @Override
    public void close() {
        synchronized (ATimeoutObjectPool.class) {
            Assertions.checkNotNull(scheduledFuture);
            clear();
            scheduledFuture.cancel(true);
            scheduledFuture = null;
            ACTIVE_POOLS.decrementAndGet();
            maybeCloseScheduledExecutor();
        }
    }

}
