package de.invesdwin.util.marshallers.serde.basic;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@Immutable
public class LongSerde implements ISerde<Long> {

    public static final LongSerde GET = new LongSerde();
    public static final int FIXED_LENGTH = Long.BYTES;

    @Override
    public Long fromBuffer(final IByteBuffer buffer) {
        if (buffer.capacity() == 0) {
            return null;
        }
        return buffer.getLong(0);
    }

    @Override
    public int toBuffer(final IByteBuffer buffer, final Long obj) {
        if (obj == null) {
            return 0;
        }
        buffer.putLong(0, obj);
        return FIXED_LENGTH;
    }

    public static void putLong(final IByteBuffer buffer, final int index, final Long value) {
        if (value == null) {
            buffer.putLong(index, Long.MIN_VALUE);
        } else {
            putLongNotNullSafe(buffer, index, value);
        }
    }

    public static void putLongNotNullSafe(final IByteBuffer buffer, final int index, final long value) {
        buffer.putLong(index, value);
    }

    public static Long getLong(final IByteBuffer buffer, final int index) {
        final long value = getLongNotNullSafe(buffer, index);
        return getLong(value);
    }

    public static long getLongNotNullSafe(final IByteBuffer buffer, final int index) {
        return buffer.getLong(index);
    }

    public static Long getLong(final long value) {
        if (value == Long.MIN_VALUE) {
            return null;
        } else {
            return value;
        }
    }
}
