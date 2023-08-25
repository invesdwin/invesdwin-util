package de.invesdwin.util.marshallers.serde;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@Immutable
public class FromBufferDelegateSerde<E> implements ISerde<E>, IFlyweightSerdeProvider<E> {

    private final ISerde<E> delegate;

    public FromBufferDelegateSerde(final ISerde<E> delegate) {
        this.delegate = delegate;
    }

    @Override
    public E fromBytes(final byte[] bytes) {
        return delegate.fromBytes(bytes);
    }

    @Override
    public E fromBuffer(final IByteBuffer buffer) {
        return delegate.fromBuffer(buffer);
    }

    @Override
    public int toBuffer(final IByteBuffer buffer, final E obj) {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte[] toBytes(final E obj) {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unchecked")
    @Override
    public ISerde<E> asFlyweightSerde() {
        if (delegate instanceof IFlyweightSerdeProvider) {
            final IFlyweightSerdeProvider<E> flyweightSerdeProvider = (IFlyweightSerdeProvider<E>) delegate;
            final ISerde<E> flyweightSerde = flyweightSerdeProvider.asFlyweightSerde();
            if (flyweightSerde != null) {
                return new ISerde<E>() {
                    @Override
                    public E fromBytes(final byte[] bytes) {
                        return flyweightSerde.fromBytes(bytes);
                    }

                    @Override
                    public E fromBuffer(final IByteBuffer buffer) {
                        return flyweightSerde.fromBuffer(buffer);
                    }

                    @Override
                    public int toBuffer(final IByteBuffer buffer, final E obj) {
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public byte[] toBytes(final E obj) {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).addValue(delegate).toString();
    }
}