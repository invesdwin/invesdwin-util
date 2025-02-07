package de.invesdwin.util.streams.buffer.bytes.timed;

import java.io.IOException;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.marshallers.serde.SerdeBaseMethods;
import de.invesdwin.util.marshallers.serde.basic.FDateSerde;
import de.invesdwin.util.marshallers.serde.basic.LongSerde;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@Immutable
public class IndexedByteBufferSerde implements ISerde<IndexedByteBuffer> {

    public static final IndexedByteBufferSerde GET = new IndexedByteBufferSerde();

    public static Integer getFixedLength(final Integer valueFixedLength) {
        if (valueFixedLength == null) {
            return null;
        } else {
            return FDateSerde.FIXED_LENGTH + valueFixedLength;
        }
    }

    @Override
    public IndexedByteBuffer fromBytes(final byte[] bytes) {
        return SerdeBaseMethods.fromBytes(this, bytes);
    }

    @Override
    public byte[] toBytes(final IndexedByteBuffer obj) {
        return SerdeBaseMethods.toBytes(this, obj);
    }

    @Override
    public IndexedByteBuffer fromBuffer(final IByteBuffer buffer) {
        final long index = LongSerde.getLong(buffer, 0);
        final byte[] value = buffer.asByteArrayCopyFrom(LongSerde.FIXED_LENGTH);
        return new IndexedByteBuffer(index, ByteBuffers.wrap(value));
    }

    @Override
    public int toBuffer(final IByteBuffer buffer, final IndexedByteBuffer obj) {
        LongSerde.putLong(buffer, 0, obj.getIndex());
        try {
            final IByteBuffer value = obj.getByteBuffer().asBuffer();
            buffer.putBytes(LongSerde.FIXED_LENGTH, value);
            return LongSerde.FIXED_LENGTH + value.capacity();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

}
