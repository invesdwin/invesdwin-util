package de.invesdwin.util.streams.buffer.pool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Queue;
import java.util.function.Supplier;

import javax.annotation.concurrent.NotThreadSafe;

import org.agrona.concurrent.ManyToManyConcurrentArrayQueue;

import de.invesdwin.util.concurrent.pool.AObjectPool;
import de.invesdwin.util.streams.buffer.IByteBuffer;

@NotThreadSafe
public final class PooledByteBufferObjectPool extends AObjectPool<IByteBuffer> {

    private final int maxPoolSize;
    private final Queue<IByteBuffer> pooledLZ4BlockInputStreamRotation;

    public PooledByteBufferObjectPool(final Supplier<IByteBuffer> factory, final int maxPoolSize) {
        super(new PooledByteBufferObjectFactory(factory));
        this.maxPoolSize = maxPoolSize;
        pooledLZ4BlockInputStreamRotation = new ManyToManyConcurrentArrayQueue<IByteBuffer>(maxPoolSize);
    }

    @Override
    protected IByteBuffer internalBorrowObject() {
        if (pooledLZ4BlockInputStreamRotation.isEmpty()) {
            return factory.makeObject();
        }
        final IByteBuffer poledLZ4BlockInputStream = pooledLZ4BlockInputStreamRotation.poll();
        if (poledLZ4BlockInputStream != null) {
            return poledLZ4BlockInputStream;
        } else {
            return factory.makeObject();
        }
    }

    @Override
    public int getNumIdle() {
        return pooledLZ4BlockInputStreamRotation.size();
    }

    @Override
    public Collection<IByteBuffer> internalClear() {
        final Collection<IByteBuffer> removed = new ArrayList<IByteBuffer>();
        while (!pooledLZ4BlockInputStreamRotation.isEmpty()) {
            removed.add(pooledLZ4BlockInputStreamRotation.poll());
        }
        return removed;
    }

    @Override
    protected IByteBuffer internalAddObject() {
        final IByteBuffer pooled = factory.makeObject();
        pooledLZ4BlockInputStreamRotation.offer(factory.makeObject());
        return pooled;
    }

    @Override
    protected void internalReturnObject(final IByteBuffer obj) {
        if (pooledLZ4BlockInputStreamRotation.size() < maxPoolSize) {
            pooledLZ4BlockInputStreamRotation.offer(obj);
        }
    }

    @Override
    protected void internalInvalidateObject(final IByteBuffer obj) {
        //Nothing happens
    }

    @Override
    protected void internalRemoveObject(final IByteBuffer obj) {
        pooledLZ4BlockInputStreamRotation.poll();
    }

}
