package de.invesdwin.util.concurrent.pool.timeout;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import com.google.common.util.concurrent.ListenableScheduledFuture;

import de.invesdwin.util.concurrent.WrappedScheduledExecutorService;
import de.invesdwin.util.concurrent.pool.ICloseableObjectPool;
import de.invesdwin.util.concurrent.pool.IObjectPool;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.Duration;

@ThreadSafe
public abstract class ASingletonTimeoutObjectPool<E> implements ICloseableObjectPool<E> {

    private static final IObjectPool<WrappedScheduledExecutorService> SCHEDULED_EXECUTOR_POOL = ATimeoutObjectPool.SCHEDULED_EXECUTOR_POOL;

    @GuardedBy("this")
    protected volatile E singleton;
    @GuardedBy("this")
    protected long referenceCount;
    @GuardedBy("this")
    protected long timeoutStartNanos;
    private final long timeoutMillis;
    private WrappedScheduledExecutorService scheduledExecutor;
    private ListenableScheduledFuture<?> scheduledFuture;

    public ASingletonTimeoutObjectPool(final Duration timeout, final Duration checkInverval) {
        this.timeoutMillis = timeout.longValue(FTimeUnit.MILLISECONDS);

        this.scheduledExecutor = SCHEDULED_EXECUTOR_POOL.borrowObject();
        this.scheduledFuture = scheduledExecutor.scheduleAtFixedRate(this::checkTimeout, 0, checkInverval.longValue(),
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
    public synchronized void close() {
        final ListenableScheduledFuture<?> scheduledFutureCopy = scheduledFuture;
        if (scheduledFutureCopy != null) {
            clear();
            scheduledFutureCopy.cancel(true);
            scheduledFuture = null;
        }
        final WrappedScheduledExecutorService scheduledExecutorCopy = scheduledExecutor;
        if (scheduledExecutorCopy != null) {
            SCHEDULED_EXECUTOR_POOL.returnObject(scheduledExecutorCopy);
            scheduledExecutor = null;
        }
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
