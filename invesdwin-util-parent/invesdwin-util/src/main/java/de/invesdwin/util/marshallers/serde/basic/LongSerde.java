package de.invesdwin.util.marshallers.serde.basic;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.marshallers.serde.SerdeBaseMethods;
import de.invesdwin.util.streams.buffer.IByteBuffer;

@Immutable
public class LongSerde implements ISerde<Long> {

    public static final LongSerde GET = new LongSerde();
    public static final int FIXED_LENGTH = Long.BYTES;

    @Override
    public Long fromBytes(final byte[] bytes) {
        return SerdeBaseMethods.fromBytes(this, bytes);
    }

    @Override
    public byte[] toBytes(final Long obj) {
        return SerdeBaseMethods.toBytes(this, obj);
    }

    @Override
    public Long fromBuffer(final IByteBuffer buffer, final int length) {
        return buffer.getLong(0);
    }

    @Override
    public int toBuffer(final IByteBuffer buffer, final Long obj) {
        buffer.putLong(0, obj);
        return FIXED_LENGTH;
    }

    public static void putLong(final IByteBuffer buffer, final int index, final Long value) {
        if (value == null) {
            buffer.putLong(index, Long.MIN_VALUE);
        } else {
            buffer.putLong(index, value);
        }
    }

    public static Long getLong(final IByteBuffer buffer, final int index) {
        final long value = buffer.getLong(index);
        return getLong(value);
    }

    public static Long getLong(final long value) {
        if (value == Long.MIN_VALUE) {
            return null;
        } else {
            return value;
        }
    }
}
