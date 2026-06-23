package de.invesdwin.util.streams.buffer.bytes.timed;

import java.io.IOException;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.marshallers.serde.basic.FDateSerde;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.time.date.FDate;

@Immutable
public class TimedByteBufferSerde implements ISerde<TimedByteBuffer> {

    public static final TimedByteBufferSerde GET = new TimedByteBufferSerde();

    private static final int TIME_INDEX = 0;
    private static final int TIME_LENGTH = FDate.BYTES;

    private static final int VALUE_INDEX = TIME_INDEX + TIME_LENGTH;

    private static final int FIXED_LENGTH = VALUE_INDEX;

    public static Integer getFixedLength(final Integer valueFixedLength) {
        if (valueFixedLength == null) {
            return null;
        } else {
            return FIXED_LENGTH + valueFixedLength;
        }
    }

    @Override
    public TimedByteBuffer fromBuffer(final IByteBuffer buffer) {
        final FDate time = FDateSerde.getFDateNotNullSafe(buffer, TIME_INDEX);
        final byte[] value = buffer.asByteArrayCopyFrom(VALUE_INDEX);
        return new TimedByteBuffer(time, ByteBuffers.wrap(value));
    }

    @Override
    public int toBuffer(final IByteBuffer buffer, final TimedByteBuffer obj) {
        FDateSerde.putFDateNotNullSafe(buffer, TIME_INDEX, obj.getTime());
        try {
            final IByteBuffer value = obj.getByteBuffer().asBuffer();
            buffer.putBytes(VALUE_INDEX, value);
            return FIXED_LENGTH + value.capacity();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

}
