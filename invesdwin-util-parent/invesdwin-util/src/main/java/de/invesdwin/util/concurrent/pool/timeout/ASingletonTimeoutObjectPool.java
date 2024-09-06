package de.invesdwin.util.concurrent.pool.timeout;

import javax.annotation.concurrent.ThreadSafe;

import com.google.common.util.concurrent.ListenableScheduledFuture;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.concurrent.pool.ICloseableObjectPool;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.Duration;

@ThreadSafe
public abstract class ASingletonTimeoutObjectPool<E> implements ICloseableObjectPool<E> {

    protected E singleton;
    protected long referenceCount;
    protected long timeoutStartNanos;
    private final long timeoutMillis;
    private ListenableScheduledFuture<?> scheduledFuture;

    public ASingletonTimeoutObjectPool(final Duration timeout, final Duration checkInverval) {
        this.timeoutMillis = timeout.longValue(FTimeUnit.MILLISECONDS);

        ATimeoutObjectPool.ACTIVE_POOLS.incrementAndGet();
        this.scheduledFuture = ATimeoutObjectPool.getScheduledExecutor()
                .scheduleAtFixedRate(this::checkTimeout, 0, checkInverval.longValue(),
                        checkInverval.getTimeUnit().timeUnitValue());
    }

    protected synchronized void checkTimeout() {
        if (isTimeoutExceeded()) {
            final E element = singleton;
            if (element != null) {
                invalidateObject(element);
                singleton = null;
            }
        }
    }

    protected final boolean isTimeoutExceeded() {
        return singleton != null && referenceCount <= 0 && timeoutStartNanos != 0
                && System.nanoTime() - timeoutStartNanos >= timeoutMillis;
    }

    @Override
    public synchronized E borrowObject() {
        if (singleton == null) {
            singleton = newObject();
        }
        timeoutStartNanos = 0;
        referenceCount++;
        return singleton;
    }

    protected abstract E newObject();

    @Override
    public synchronized void returnObject(final E element) {
        if (element == null) {
            return;
        }
        if (element != singleton) {
            return;
        }
        if (passivateObject(element)) {
            referenceCount--;
            if (referenceCount <= 0) {
                referenceCount = 0;
                do {
                    timeoutStartNanos = System.nanoTime();
                } while (timeoutStartNanos == 0);
            }
        } else {
            innerClear();
        }
    }

    protected abstract boolean passivateObject(E element);

    @Override
    public synchronized void clear() {
        if (referenceCount <= 0 && singleton != null) {
            innerClear();
        }
    }

    private void innerClear() {
        invalidateObject(singleton);
        singleton = null;
        timeoutStartNanos = 0;
        referenceCount = 0;
    }

    @Override
    public void close() {
        Assertions.checkNotNull(scheduledFuture);
        clear();
        scheduledFuture.cancel(true);
        scheduledFuture = null;
        ATimeoutObjectPool.ACTIVE_POOLS.decrementAndGet();
        ATimeoutObjectPool.maybeCloseScheduledExecutor();
    }

    @Override
    public int size() {
        if (singleton == null) {
            return 0;
        } else {
            return 1;
        }
    }

}
