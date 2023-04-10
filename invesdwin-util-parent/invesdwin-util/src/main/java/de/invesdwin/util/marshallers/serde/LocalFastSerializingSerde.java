package de.invesdwin.util.marshallers.serde;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.lang3.SerializationException;

import de.invesdwin.norva.beanpath.IDeepCloneProvider;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.ICloseableByteBuffer;

/**
 * This serializing serde is only suitable inside the current JVM
 */
@Immutable
public class LocalFastSerializingSerde<E extends Serializable> implements ISerde<E>, IDeepCloneProvider {

    @SuppressWarnings("rawtypes")
    private static final LocalFastSerializingSerde INSTANCE = new LocalFastSerializingSerde<>();

    private RemoteFastSerializingSerde<Object> delegate = new RemoteFastSerializingSerde<>(true);

    public void setClassRegistry(final List<Class<?>> classRegistry) {
        this.delegate = new RemoteFastSerializingSerde<>(true, classRegistry);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Serializable> LocalFastSerializingSerde<T> get() {
        return INSTANCE;
    }

    @Override
    public E fromBytes(final byte[] bytes) {
        return deserialize(bytes);
    }

    @Override
    public byte[] toBytes(final E obj) {
        return serialize(obj);
    }

    @Override
    public E fromBuffer(final IByteBuffer buffer) {
        return deserialize(buffer);
    }

    @Override
    public int toBuffer(final IByteBuffer buffer, final E obj) {
        return serialize(buffer, obj);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T deepClone(final T obj) {
        if (obj == null) {
            return null;
        }
        try (ICloseableByteBuffer buffer = ByteBuffers.EXPANDABLE_POOL.borrowObject()) {
            final int length = serialize(buffer, (Serializable) obj);
            return (T) deserialize(buffer.sliceTo(length));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T deserialize(final byte[] objectData) {
        return (T) delegate.fromBytes(objectData);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T deserialize(final InputStream in) {
        //FST is unreliable regarding input streams and not zero allocation
        try (ICloseableByteBuffer buffer = ByteBuffers.EXPANDABLE_POOL.borrowObject()) {
            final int length = ByteBuffers.readExpandable(in, buffer, 0);
            return (T) deserialize(buffer.sliceTo(length));
        } catch (final Throwable t) {
            throw new SerializationException(t);
        }
    }

    @Override
    public byte[] serialize(final Serializable obj) {
        return delegate.toBytes(obj);
    }

    @Override
    public int serialize(final OutputStream out, final Serializable obj) {
        try (ICloseableByteBuffer buffer = ByteBuffers.EXPANDABLE_POOL.borrowObject()) {
            final int length = serialize(buffer, obj);
            buffer.getBytesTo(0, out, length);
            return length;
        } catch (final Throwable t) {
            throw new SerializationException(t);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T deserialize(final IByteBuffer buffer) {
        return (T) delegate.fromBuffer(buffer);
    }

    public <T> int serialize(final IByteBuffer buffer, final T obj) {
        return delegate.toBuffer(buffer, obj);
    }

}
