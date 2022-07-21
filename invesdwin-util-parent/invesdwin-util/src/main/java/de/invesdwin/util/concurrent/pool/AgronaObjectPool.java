package de.invesdwin.util.concurrent.pool;

import java.util.function.Supplier;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.Executors;
import de.invesdwin.util.math.Integers;

@ThreadSafe
public class AgronaObjectPool<E> extends AAgronaObjectPool<E> {

    //normally buffers should not be nested in one thread
    public static final int DEFAULT_MAX_POOL_SIZE = Integers.max(2, Executors.getCpuThreadPoolCount());

    private final Supplier<E> objectFactory;

    public AgronaObjectPool(final Supplier<E> objectFactory) {
        this(objectFactory, DEFAULT_MAX_POOL_SIZE);
    }

    public AgronaObjectPool(final Supplier<E> objectFactory, final int maxPoolSize) {
        super(maxPoolSize);
        this.objectFactory = objectFactory;
    }

    @Override
    protected E newObject() {
        return objectFactory.get();
    }

}
