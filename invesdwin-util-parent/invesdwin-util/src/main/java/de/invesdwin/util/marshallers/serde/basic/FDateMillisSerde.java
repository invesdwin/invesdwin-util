package de.invesdwin.util.marshallers.serde.basic;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.time.date.FDate;

@Immutable
public class FDateMillisSerde implements ISerde<FDate> {

    public static final FDateMillisSerde GET = new FDateMillisSerde();

    public static final int MILLIS_INDEX = 0;
    public static final int MILLIS_LENGTH = Long.BYTES;

    public static final int FIXED_LENGTH = MILLIS_INDEX + MILLIS_LENGTH;

    @Override
    public FDate fromBuffer(final IByteBuffer buffer) {
        if (buffer.capacity() == 0) {
            return null;
        }
        final long millis = buffer.getLong(MILLIS_INDEX);
        return getFDateNotNullSafe(millis);
    }

    @Override
    public int toBuffer(final IByteBuffer buffer, final FDate obj) {
        if (obj == null) {
            return 0;
        }
        buffer.putLong(MILLIS_INDEX, obj.millisValue());
        return FIXED_LENGTH;
    }

    public static void putFDate(final IByteBuffer buffer, final int index, final FDate time) {
        if (time == null) {
            buffer.putLong(index + MILLIS_INDEX, Long.MIN_VALUE);
        } else {
            putFDateNotNullSafe(buffer, index, time);
        }
    }

    public static void putFDateNotNullSafe(final IByteBuffer buffer, final int index, final FDate time) {
        buffer.putLong(index + MILLIS_INDEX, time.millisValue());
    }

    public static FDate getFDate(final IByteBuffer buffer, final int index) {
        final long millis = buffer.getLong(MILLIS_INDEX + index);
        return getFDate(millis);
    }

    public static FDate getFDate(final long millis) {
        if (millis == Long.MIN_VALUE) {
            return null;
        } else {
            return getFDateNotNullSafe(millis);
        }
    }

    public static FDate getFDateNotNullSafe(final IByteBuffer buffer, final int index) {
        final long millis = buffer.getLong(MILLIS_INDEX + index);
        return getFDateNotNullSafe(millis);
    }

    private static FDate getFDateNotNullSafe(final long millis) {
        return new FDate(millis, 0);
    }

}
