package de.invesdwin.util.marshallers.serde.basic;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.buffer.IByteBuffer;
import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.marshallers.serde.SerdeBaseMethods;

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
        return SerdeBaseMethods.toBytes(this, obj, FIXED_LENGTH);
    }

    @Override
    public Long fromBuffer(final IByteBuffer buffer) {
        return extractLong(buffer, 0);
    }

    @Override
    public int toBuffer(final Long obj, final IByteBuffer buffer) {
        putLong(buffer, 0, obj);
        return FIXED_LENGTH;
    }

    public static void putLong(final IByteBuffer buffer, final int index, final Long value) {
        if (value == null) {
            buffer.putLong(index, Long.MIN_VALUE);
        } else {
            buffer.putLong(index, value);
        }
    }

    public static Long extractLong(final IByteBuffer buffer, final int index) {
        final long value = buffer.getLong(index);
        return extractLong(value);
    }

    public static Long extractLong(final long value) {
        if (value == Long.MIN_VALUE) {
            return null;
        } else {
            return value;
        }
    }
}
