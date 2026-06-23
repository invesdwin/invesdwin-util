package de.invesdwin.util.marshallers.serde.basic;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.math.Bytes;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.IByteBufferProvider;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.IMemoryBufferProvider;

@Immutable
public class NullSerde<O> implements ISerde<O> {

    public static final int FIXED_LENGTH = 0;
    @SuppressWarnings("rawtypes")
    private static final NullSerde INSTANCE = new NullSerde<>();

    @SuppressWarnings("unchecked")
    public static <T> NullSerde<T> get() {
        return INSTANCE;
    }

    @Override
    public O fromBytes(final byte[] bytes) {
        return null;
    }

    @Override
    public byte[] toBytes(final O obj) {
        return Bytes.EMPTY_ARRAY;
    }

    @Override
    public O fromBuffer(final IByteBuffer buffer) {
        return null;
    }

    @Override
    public int toBuffer(final IByteBuffer buffer, final O obj) {
        return 0;
    }

    @Override
    public O fromBuffer(final IMemoryBuffer buffer) {
        return null;
    }

    @Override
    public long toBuffer(final IMemoryBuffer buffer, final O obj) {
        return 0;
    }

    @Override
    public O fromBuffer(final IByteBufferProvider buffer) {
        return null;
    }

    @Override
    public int toBuffer(final IByteBufferProvider buffer, final O obj) {
        return 0;
    }

    @Override
    public O fromBuffer(final IMemoryBufferProvider buffer) {
        return null;
    }

    @Override
    public long toBuffer(final IMemoryBufferProvider buffer, final O obj) {
        return 0;
    }

}
