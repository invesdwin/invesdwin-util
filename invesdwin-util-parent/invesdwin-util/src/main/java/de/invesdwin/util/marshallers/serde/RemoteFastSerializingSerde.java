package de.invesdwin.util.marshallers.serde;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.lang3.SerializationException;
import org.nustaq.serialization.simpleapi.FSTBufferTooSmallException;
import org.nustaq.serialization.simpleapi.OffHeapCoder;
import org.nustaq.serialization.simpleapi.OnHeapCoder;

import de.invesdwin.util.concurrent.pool.AgronaObjectPool;
import de.invesdwin.util.concurrent.pool.IObjectPool;
import de.invesdwin.util.math.Bytes;
import de.invesdwin.util.streams.buffer.IByteBuffer;

/**
 * This serializing serde is suitable for IPC
 */
@Immutable
public class RemoteFastSerializingSerde<E> implements ISerde<E> {

    private static final Class<?>[] CLASS_EMPTY_ARRAY = new Class[0];

    private static final double EXPANSION_FACTOR = 2D;

    private final boolean shared;
    private final Class<?>[] filteredTypes;
    private transient IObjectPool<OnHeapCoder> onHeapCoderPool;
    private transient IObjectPool<OffHeapCoder> offHeapCoderPool;

    public RemoteFastSerializingSerde(final boolean shared, final List<Class<?>> types) {
        this.shared = shared;
        this.filteredTypes = filter(types);
    }

    /**
     * Use shared=true when the object tree can have multiple object references to the same object. This also solves
     * circular dependencies. Though shared=false is faster for flat objects.
     */
    public RemoteFastSerializingSerde(final boolean shared, final Class<?>... types) {
        this.shared = shared;
        this.filteredTypes = filter(types);
    }

    private Class<?>[] filter(final Class<?>[] types) {
        if (types == null || types.length == 0) {
            return CLASS_EMPTY_ARRAY;
        } else {
            return filter(new ArrayList<>(Arrays.asList(types)));
        }
    }

    private static Class<?>[] filter(final List<Class<?>> types) {
        if (types == null || types.isEmpty()) {
            return CLASS_EMPTY_ARRAY;
        }
        for (int i = 0; i < types.size(); i++) {
            final Class<?> type = types.get(i);
            if (type == null || !Serializable.class.isAssignableFrom(type)) {
                types.remove(i);
                i--;
            }
        }
        return types.toArray(CLASS_EMPTY_ARRAY);
    }

    public IObjectPool<OnHeapCoder> getOnHeapCoderPool() {
        if (onHeapCoderPool == null) {
            onHeapCoderPool = new AgronaObjectPool<>(() -> new OnHeapCoder(shared, filteredTypes));
        }
        return onHeapCoderPool;
    }

    public IObjectPool<OffHeapCoder> getOffHeapCoderPool() {
        if (offHeapCoderPool == null) {
            offHeapCoderPool = new AgronaObjectPool<>(() -> new OffHeapCoder(shared, filteredTypes));
        }
        return offHeapCoderPool;
    }

    @SuppressWarnings("unchecked")
    @Override
    public E fromBytes(final byte[] bytes) {
        if (bytes.length == 0) {
            return null;
        }
        final OnHeapCoder coder = getOnHeapCoderPool().borrowObject();
        try {
            return (E) coder.toObject(bytes);
        } catch (final Throwable t) {
            throw new SerializationException(t);
        } finally {
            getOnHeapCoderPool().returnObject(coder);
        }
    }

    @Override
    public byte[] toBytes(final E obj) {
        if (obj == null) {
            return Bytes.EMPTY_ARRAY;
        }
        final OnHeapCoder coder = getOnHeapCoderPool().borrowObject();
        try {
            return coder.toByteArray(obj);
        } catch (final Throwable t) {
            throw new SerializationException(t);
        } finally {
            getOnHeapCoderPool().returnObject(coder);
        }
    }

    @Override
    public E fromBuffer(final IByteBuffer buffer, final int length) {
        if (length == 0) {
            return null;
        }
        try {
            final byte[] byteArray = buffer.byteArray();
            if (byteArray != null) {
                return fromBuffer(byteArray, buffer.wrapAdjustment(), length);
            } else {
                return fromBuffer(buffer.addressOffset(), length);
            }
        } catch (final Throwable t) {
            throw new SerializationException(t);
        }
    }

    @SuppressWarnings("unchecked")
    private E fromBuffer(final byte[] byteArray, final int index, final int length) {
        final OnHeapCoder coder = getOnHeapCoderPool().borrowObject();
        try {
            return (E) coder.toObject(byteArray, index, length);
        } finally {
            getOnHeapCoderPool().returnObject(coder);
        }
    }

    @SuppressWarnings("unchecked")
    private E fromBuffer(final long addressOffset, final int length) throws ClassNotFoundException, IOException {
        final OffHeapCoder coder = getOffHeapCoderPool().borrowObject();
        try {
            return (E) coder.toObject(addressOffset, length);
        } finally {
            getOffHeapCoderPool().returnObject(coder);
        }
    }

    @Override
    public int toBuffer(final IByteBuffer buffer, final E obj) {
        if (obj == null) {
            return 0;
        }
        return toBufferExpandable(buffer, obj);
    }

    private int toBufferExpandable(final IByteBuffer buffer, final E obj) {
        try {
            final byte[] byteArray = buffer.byteArray();
            if (byteArray != null) {
                return toBuffer(obj, byteArray, buffer.wrapAdjustment(), buffer.capacity());
            } else {
                return toBuffer(obj, buffer.addressOffset(), buffer.capacity());
            }
        } catch (final FSTBufferTooSmallException e) {
            if (buffer.isExpandable()) {
                final int newCapacity = (int) (buffer.capacity() * EXPANSION_FACTOR);
                buffer.ensureCapacity(newCapacity);
                return toBufferExpandable(buffer, obj);
            } else {
                throw e;
            }
        } catch (final Throwable t) {
            throw new SerializationException(t);
        }
    }

    private int toBuffer(final Object obj, final byte[] byteArray, final int index, final int availableSize)
            throws FSTBufferTooSmallException {
        final OnHeapCoder coder = getOnHeapCoderPool().borrowObject();
        try {
            return coder.toByteArray(obj, byteArray, index, availableSize);
        } finally {
            getOnHeapCoderPool().returnObject(coder);
        }
    }

    private int toBuffer(final Object obj, final long addressOffset, final int length)
            throws FSTBufferTooSmallException, IOException {
        final OffHeapCoder coder = getOffHeapCoderPool().borrowObject();
        try {
            return coder.toMemory(obj, addressOffset, length);
        } finally {
            getOffHeapCoderPool().returnObject(coder);
        }
    }

}
