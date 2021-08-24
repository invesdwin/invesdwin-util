package de.invesdwin.util.marshallers.serde.basic;

import java.nio.ByteBuffer;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.buffer.IByteBuffer;
import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.time.date.FDate;

@Immutable
public class FDateSerde implements ISerde<FDate> {

    public static final FDateSerde GET = new FDateSerde();
    public static final int FIXED_LENGTH = FDate.BYTES;

    @Override
    public FDate fromBytes(final byte[] bytes) {
        final ByteBuffer buffer = ByteBuffer.wrap(bytes);
        return FDate.valueOf(buffer.getLong(0));
    }

    @Override
    public byte[] toBytes(final FDate obj) {
        final ByteBuffer buffer = ByteBuffer.allocate(FIXED_LENGTH);
        buffer.putLong(0, obj.millisValue());
        return buffer.array();
    }

    @Override
    public FDate fromBuffer(final IByteBuffer buffer) {
        return FDate.valueOf(buffer.getLong(0));
    }

    @Override
    public int toBuffer(final FDate obj, final IByteBuffer buffer) {
        buffer.putLong(0, obj.millisValue());
        return FIXED_LENGTH;
    }

    public static void putFDate(final IByteBuffer buffer, final int index, final FDate time) {
        if (time == null) {
            buffer.putLong(index, Long.MIN_VALUE);
        } else {
            buffer.putLong(index, time.millisValue());
        }
    }

    public static FDate extractFDate(final IByteBuffer buffer, final int index) {
        final long time = buffer.getLong(index);
        return extractFDate(time);
    }

    public static FDate extractFDate(final long time) {
        if (time == Long.MIN_VALUE) {
            return null;
        } else {
            return new FDate(time);
        }
    }

}
