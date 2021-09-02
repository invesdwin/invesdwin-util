package de.invesdwin.util.streams.buffer.pool;

import java.util.function.Supplier;

import javax.annotation.concurrent.NotThreadSafe;

import org.agrona.concurrent.ManyToManyConcurrentArrayQueue;

import de.invesdwin.util.concurrent.pool.AQueueObjectPool;
import de.invesdwin.util.streams.buffer.IByteBuffer;

@NotThreadSafe
public final class PooledByteBufferObjectPool extends AQueueObjectPool<IByteBuffer> {

    private final Supplier<IByteBuffer> factory;

    public PooledByteBufferObjectPool(final Supplier<IByteBuffer> factory, final int maxPoolSize) {
        super(new ManyToManyConcurrentArrayQueue<IByteBuffer>(maxPoolSize));
        this.factory = factory;
    }

    @Override
    protected IByteBuffer newObject() {
        return factory.get();
    }

}
