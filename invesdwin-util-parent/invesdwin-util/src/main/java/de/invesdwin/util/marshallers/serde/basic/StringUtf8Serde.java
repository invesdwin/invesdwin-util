package de.invesdwin.util.marshallers.serde.basic;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.math.Bytes;
import de.invesdwin.util.streams.buffer.ByteBuffers;
import de.invesdwin.util.streams.buffer.IByteBuffer;

@Immutable
public class StringUtf8Serde implements ISerde<String> {

    public static final StringUtf8Serde GET = new StringUtf8Serde();

    @Override
    public String fromBytes(final byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        return ByteBuffers.newStringUtf8(bytes);
    }

    @Override
    public byte[] toBytes(final String obj) {
        if (obj == null) {
            return Bytes.EMPTY_ARRAY;
        } else {
            return ByteBuffers.newStringUtf8Bytes(obj);
        }
    }

    @Override
    public String fromBuffer(final IByteBuffer buffer, final int length) {
        return getStringUtf8(buffer, 0, length);
    }

    @Override
    public int toBuffer(final IByteBuffer buffer, final String obj) {
        return putStringUtf8(buffer, 0, obj);
    }

    public static int putStringUtf8(final IByteBuffer buffer, final int index, final String string) {
        if (string == null || string.length() == 0) {
            return 0;
        }
        return buffer.putStringUtf8(index, string);
    }

    public static String getStringUtf8(final IByteBuffer buffer, final int index, final int size) {
        if (size == 0) {
            return null;
        }
        return buffer.getStringUtf8(index, size);
    }

}
