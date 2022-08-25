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
    public byte[][] fromBuffer(final IByteBuffer buffer, final int length) {
        final int arrayCount;
        int lengthPosition = 0;
        if (fixedArrayCount == null) {
            arrayCount = buffer.getInt(lengthPosition);
            lengthPosition += Integer.BYTES;
        } else {
            arrayCount = fixedArrayCount;
        }
        int payloadPosition = lengthPosition + Integer.BYTES * arrayCount;
        final byte[][] result = new byte[arrayCount][];
        for (int i = 0; i < arrayCount; i++) {
            final int curLength = buffer.getInt(lengthPosition);
            lengthPosition += Integer.BYTES;
            final byte[] payload = ByteBuffers.allocateByteArray(curLength);
            buffer.getBytes(payloadPosition, payload);
            payloadPosition += curLength;
            result[i] = payload;
        }
        return result;
    }

    @Override
    public int toBuffer(final IByteBuffer buffer, final byte[][] obj) {
        final int arrayCount = obj.length;
        int lengthPosition = 0;
        if (fixedArrayCount == null) {
            buffer.putInt(lengthPosition, arrayCount);
            lengthPosition += Integer.BYTES;
        } else {
            if (arrayCount != fixedArrayCount) {
                throw new IllegalArgumentException(
                        "arrayCount[" + arrayCount + "] != fixedArrayCount[" + fixedArrayCount + "]");
            }
        }
        int payloadPosition = lengthPosition + Integer.BYTES * arrayCount;
        for (int i = 0; i < arrayCount; i++) {
            final int curLength = obj[i].length;
            buffer.putInt(lengthPosition, curLength);
            lengthPosition += Integer.BYTES;
            buffer.putBytes(payloadPosition, obj[i]);
            payloadPosition += curLength;
        }

        return payloadPosition;
    }

    public static ByteArraySerde getInstance(final Integer fixedArrayCount) {
        return INSTANCE.get(fixedArrayCount);
    }
}
