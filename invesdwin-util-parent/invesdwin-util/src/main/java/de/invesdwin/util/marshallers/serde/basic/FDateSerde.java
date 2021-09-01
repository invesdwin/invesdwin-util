package de.invesdwin.util.marshallers.serde.basic;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.marshallers.serde.SerdeBaseMethods;
import de.invesdwin.util.streams.buffer.IByteBuffer;
import de.invesdwin.util.time.date.FDate;

@Immutable
public class FDateSerde implements ISerde<FDate> {

    public static final FDateSerde GET = new FDateSerde();
    public static final int FIXED_LENGTH = FDate.BYTES;

    @Override
    public FDate fromBytes(final byte[] bytes) {
        return SerdeBaseMethods.fromBytes(this, bytes);
    }

    @Override
    public byte[] toBytes(final FDate obj) {
        return SerdeBaseMethods.toBytes(this, obj);
    }

    @Override
    public FDate fromBuffer(final IByteBuffer buffer, final int length) {
        if (length == 0) {
            return null;
        }
        return FDate.valueOf(buffer.getLong(0));
    }

    @Override
    public int toBuffer(final IByteBuffer buffer, final FDate obj) {
        if (obj == null) {
            return 0;
        }
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

    public static FDate getFDate(final IByteBuffer buffer, final int index) {
        final long time = buffer.getLong(index);
        return getFDate(time);
    }

    public static FDate getFDate(final long time) {
        if (time == Long.MIN_VALUE) {
            return null;
        } else {
            return new FDate(time);
        }
    }

}
