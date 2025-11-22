package de.invesdwin.util.streams.buffer.bytes.timed;

import java.io.IOException;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.marshallers.serde.SerdeBaseMethods;
import de.invesdwin.util.marshallers.serde.basic.FDateSerde;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.time.date.FDate;

@Immutable
public class TimedByteBufferSerde implements ISerde<TimedByteBuffer> {

    public static final TimedByteBufferSerde GET = new TimedByteBufferSerde();

    public static Integer getFixedLength(final Integer valueFixedLength) {
        if (valueFixedLength == null) {
            return null;
        } else {
            return FDateSerde.FIXED_LENGTH + valueFixedLength;
        }
    }

    @Override
    public TimedByteBuffer fromBytes(final byte[] bytes) {
        return SerdeBaseMethods.fromBytes(this, bytes);
    }

    @Override
    public byte[] toBytes(final TimedByteBuffer obj) {
        return SerdeBaseMethods.toBytes(this, obj);
    }

    @Override
    public TimedByteBuffer fromBuffer(final IByteBuffer buffer) {
        final FDate time = FDateSerde.getFDate(buffer, 0);
        final byte[] value = buffer.asByteArrayCopyFrom(FDateSerde.FIXED_LENGTH);
        return new TimedByteBuffer(time, ByteBuffers.wrap(value));
    }

    @Override
    public int toBuffer(final IByteBuffer buffer, final TimedByteBuffer obj) {
        FDateSerde.putFDate(buffer, 0, obj.getTime());
        try {
            final IByteBuffer value = obj.getByteBuffer().asBuffer();
            buffer.putBytes(FDateSerde.FIXED_LENGTH, value);
            return FDateSerde.FIXED_LENGTH + value.capacity();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

}
