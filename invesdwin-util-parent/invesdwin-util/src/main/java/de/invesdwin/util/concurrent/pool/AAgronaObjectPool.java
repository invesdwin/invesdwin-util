package de.invesdwin.util.concurrent.pool;

import javax.annotation.concurrent.ThreadSafe;

import org.agrona.concurrent.ManyToManyConcurrentArrayQueue;

import de.invesdwin.util.concurrent.Executors;
import de.invesdwin.util.math.Integers;

@ThreadSafe
public abstract class AAgronaObjectPool<E> extends AQueueObjectPool<E> {

    //normally buffers should not be nested in one thread
    public static final int DEFAULT_MAX_POOL_SIZE = Integers.max(2, Executors.getCpuThreadPoolCount());

    public AAgronaObjectPool() {
        this(DEFAULT_MAX_POOL_SIZE);
    }

    public AAgronaObjectPool(final int maxPoolSize) {
        super(new ManyToManyConcurrentArrayQueue<>(maxPoolSize));
    }

}
