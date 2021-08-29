package de.invesdwin.util.marshallers.serde.basic;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.math.Bytes;
import de.invesdwin.util.streams.buffer.ByteBuffers;
import de.invesdwin.util.streams.buffer.IByteBuffer;

@Immutable
public class StringAsciiSerde implements ISerde<String> {

    public static final StringAsciiSerde GET = new StringAsciiSerde();

    @Override
    public String fromBytes(final byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        return ByteBuffers.newStringAscii(bytes);
    }

    @Override
    public byte[] toBytes(final String obj) {
        if (obj == null) {
            return Bytes.EMPTY_ARRAY;
        } else {
            return ByteBuffers.newStringAsciiBytes(obj);
        }
    }

    @Override
    public String fromBuffer(final IByteBuffer buffer, final int length) {
        return getStringAscii(buffer, 0, length);
    }

    @Override
    public int toBuffer(final IByteBuffer buffer, final String obj) {
        return putStringAscii(buffer, 0, obj);
    }

    public static int putStringAscii(final IByteBuffer buffer, final int index, final String string) {
        if (string == null || string.length() == 0) {
            return 0;
        }
        buffer.putStringAsciii(index, string);
        return ByteBuffers.newStringAsciiLength(string);
    }

    public static String getStringAscii(final IByteBuffer buffer, final int index, final int size) {
        if (size == 0) {
            return null;
        }
        return buffer.getStringAsciii(index, size);
    }

}
