package de.invesdwin.util.streams.buffer.bytes.timed;

import java.io.IOException;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.marshallers.serde.basic.LongSerde;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@Immutable
public class IndexedByteBufferSerde implements ISerde<IndexedByteBuffer> {

    public static final IndexedByteBufferSerde GET = new IndexedByteBufferSerde();

    private static final int INDEX_INDEX = 0;
    private static final int INDEX_LENGTH = Long.BYTES;

    private static final int VALUE_INDEX = INDEX_INDEX + INDEX_LENGTH;

    private static final int FIXED_LENGTH = VALUE_INDEX;

    public static Integer getFixedLength(final Integer valueFixedLength) {
        if (valueFixedLength == null) {
            return null;
        } else {
            return FIXED_LENGTH + valueFixedLength;
        }
    }

    @Override
    public IndexedByteBuffer fromBuffer(final IByteBuffer buffer) {
        final long index = LongSerde.getLongNotNullSafe(buffer, INDEX_INDEX);
        final byte[] value = buffer.asByteArrayCopyFrom(VALUE_INDEX);
        return new IndexedByteBuffer(index, ByteBuffers.wrap(value));
    }

    @Override
    public int toBuffer(final IByteBuffer buffer, final IndexedByteBuffer obj) {
        LongSerde.putLongNotNullSafe(buffer, INDEX_INDEX, obj.getIndex());
        try {
            final IByteBuffer value = obj.getByteBuffer().asBuffer();
            buffer.putBytes(VALUE_INDEX, value);
            return FIXED_LENGTH + value.capacity();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

}
