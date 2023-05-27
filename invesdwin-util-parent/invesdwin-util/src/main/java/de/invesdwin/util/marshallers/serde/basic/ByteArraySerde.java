package de.invesdwin.util.marshallers.serde.basic;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.loadingcache.ALoadingCache;
import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.marshallers.serde.SerdeBaseMethods;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@Immutable
public final class ByteArraySerde implements ISerde<byte[][]> {

    private static final ALoadingCache<Integer, ByteArraySerde> INSTANCE = new ALoadingCache<Integer, ByteArraySerde>() {

        @Override
        protected ByteArraySerde loadValue(final Integer key) {
            return new ByteArraySerde(key);
        }

        @Override
        protected Integer getInitialMaximumSize() {
            return 1000;
        }
    };

    private final Integer fixedArrayCount;

    private ByteArraySerde(final Integer fixedArrayCount) {
        this.fixedArrayCount = fixedArrayCount;
    }

    @Override
    public byte[][] fromBytes(final byte[] bytes) {
        return SerdeBaseMethods.fromBytes(this, bytes);
    }

    @Override
    public byte[] toBytes(final byte[][] obj) {
        return SerdeBaseMethods.toBytes(this, obj);
    }

    @Override
    public byte[][] fromBuffer(final IByteBuffer buffer) {
        final int arrayCount;
        int position = 0;
        if (fixedArrayCount == null) {
            arrayCount = buffer.getInt(position);
            position += Integer.BYTES;
        } else {
            arrayCount = fixedArrayCount;
        }
        final byte[][] result = new byte[arrayCount][];
        for (int i = 0; i < arrayCount; i++) {
            final int curLength = buffer.getInt(position);
            position += Integer.BYTES;
            final byte[] payload = ByteBuffers.allocateByteArray(curLength);
            buffer.getBytes(position, payload);
            position += curLength;
            result[i] = payload;
        }
        return result;
    }

    @Override
    public int toBuffer(final IByteBuffer buffer, final byte[][] obj) {
        final int arrayCount = obj.length;
        int position = 0;
        if (fixedArrayCount == null) {
            buffer.putInt(position, arrayCount);
            position += Integer.BYTES;
        } else {
            if (arrayCount != fixedArrayCount) {
                throw new IllegalArgumentException(
                        "arrayCount[" + arrayCount + "] != fixedArrayCount[" + fixedArrayCount + "]");
            }
        }
        for (int i = 0; i < arrayCount; i++) {
            final int curLength = obj[i].length;
            buffer.putInt(position, curLength);
            position += Integer.BYTES;
            buffer.putBytes(position, obj[i]);
            position += curLength;
        }

        return position;
    }

    public static ByteArraySerde getInstance(final Integer fixedArrayCount) {
        return INSTANCE.get(fixedArrayCount);
    }
}
