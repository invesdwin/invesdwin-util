package de.invesdwin.util.streams.buffer.pool;

import java.util.function.Supplier;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.concurrent.pool.IPoolableObjectFactory;
import de.invesdwin.util.streams.buffer.IByteBuffer;

@Immutable
public final class PooledByteBufferObjectFactory implements IPoolableObjectFactory<IByteBuffer> {

    private final Supplier<IByteBuffer> factory;

    public PooledByteBufferObjectFactory(final Supplier<IByteBuffer> factory) {
        this.factory = factory;
    }

    @Override
    public IByteBuffer makeObject() {
        return factory.get();
    }

    @Override
    public void destroyObject(final IByteBuffer obj) {
    }

    @Override
    public boolean validateObject(final IByteBuffer obj) {
        return true;
    }

    @Override
    public void activateObject(final IByteBuffer obj) {
    }

    @Override
    public void passivateObject(final IByteBuffer obj) {

    }

}
