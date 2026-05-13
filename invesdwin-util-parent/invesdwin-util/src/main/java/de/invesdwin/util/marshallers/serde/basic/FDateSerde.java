package de.invesdwin.util.marshallers.serde.basic;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.marshallers.serde.SerdeBaseMethods;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.time.date.FDate;

@Immutable
public class FDateSerde implements ISerde<FDate> {

    public static final FDateSerde GET = new FDateSerde();

    public static final int MILLIS_INDEX = 0;
    public static final int MILLIS_LENGTH = Long.BYTES;

    public static final int PICOS_INDEX = MILLIS_INDEX + MILLIS_LENGTH;
    public static final int PICOS_LENGTH = Integer.BYTES;

    public static final int FIXED_LENGTH = PICOS_INDEX + PICOS_LENGTH;

    @Override
    public FDate fromBytes(final byte[] bytes) {
        return SerdeBaseMethods.fromBytes(this, bytes);
    }

    @Override
    public byte[] toBytes(final FDate obj) {
        return SerdeBaseMethods.toBytes(this, obj);
    }

    @Override
    public FDate fromBuffer(final IByteBuffer buffer) {
        if (buffer.capacity() == 0) {
            return null;
        }
        final long millis = buffer.getLong(MILLIS_INDEX);
        final int picos = buffer.getInt(PICOS_INDEX);
        return FDate.valueOf(millis, picos);
    }

    @Override
    public int toBuffer(final IByteBuffer buffer, final FDate obj) {
        if (obj == null) {
            return 0;
        }
        buffer.putLong(MILLIS_INDEX, obj.millisValue());
        buffer.putInt(PICOS_INDEX, obj.picosValue());
        return FIXED_LENGTH;
    }

    public static void putFDate(final IByteBuffer buffer, final int index, final FDate time) {
        if (time == null) {
            buffer.putLong(index + MILLIS_INDEX, Long.MIN_VALUE);
            buffer.putInt(index + PICOS_INDEX, Integer.MIN_VALUE);
        } else {
            buffer.putLong(index + MILLIS_INDEX, time.millisValue());
            buffer.putInt(index + PICOS_INDEX, time.picosValue());
        }
    }

    public static FDate getFDate(final IByteBuffer buffer, final int index) {
        final long millis = buffer.getLong(MILLIS_INDEX + index);
        final int picos = buffer.getInt(PICOS_INDEX + index);
        return getFDate(millis, picos);
    }

    public static FDate getFDate(final long millis, final int picos) {
        if (millis == Long.MIN_VALUE && picos == Integer.MIN_VALUE) {
            return null;
        } else {
            return new FDate(millis, picos);
        }
    }

}
