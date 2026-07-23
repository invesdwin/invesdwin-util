package de.invesdwin.util.concurrent.pool;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import javax.annotation.concurrent.ThreadSafe;

import org.agrona.concurrent.ManyToManyConcurrentArrayQueue;

import de.invesdwin.util.concurrent.Executors;
import de.invesdwin.util.math.Integers;

@ThreadSafe
public abstract class AAgronaObjectPool<E> extends AQueueObjectPool<E> {

    //normally buffers should not be nested too much in one thread
    public static final int DEFAULT_MAX_POOL_SIZE = Integers.max(2, Executors.getCpuThreadPoolCount());

    public AAgronaObjectPool() {
        this(DEFAULT_MAX_POOL_SIZE);
    }

    public AAgronaObjectPool(final int maxPoolSize) {
        super(newQueue(maxPoolSize));
    }

    private static <T> Queue<T> newQueue(final int maxPoolSize) {
        try {
            return new ManyToManyConcurrentArrayQueue<>(maxPoolSize);
        } catch (final Throwable e) {
            //fallback for restricted environments (e.g. Spark) where Unsafe is not available
            return new ArrayBlockingQueue<>(maxPoolSize);
        }
    }

}
