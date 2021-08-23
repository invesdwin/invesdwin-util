package de.invesdwin.util.marshallers.serde.basic;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.Charsets;
import de.invesdwin.util.lang.buffer.IByteBuffer;
import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.marshallers.serde.SerdeBaseMethods;
import de.invesdwin.util.math.Bytes;

@Immutable
public class StringSerde implements ISerde<String> {

    public static final StringSerde GET = new StringSerde();

    @Override
    public String fromBytes(final byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        return new String(bytes, Charsets.UTF_8);
    }

    @Override
    public byte[] toBytes(final String obj) {
        if (obj == null) {
            return Bytes.EMPTY_ARRAY;
        } else {
            return obj.getBytes(Charsets.UTF_8);
        }
    }

    @Override
    public String fromBuffer(final IByteBuffer buffer) {
        return SerdeBaseMethods.fromBuffer(this, buffer);
    }

    @Override
    public int toBuffer(final String obj, final IByteBuffer buffer) {
        return SerdeBaseMethods.toBuffer(this, obj, buffer);
    }

    public static int putString(final IByteBuffer buffer, final int index, final String string) {
        if (string == null || string.length() == 0) {
            return 0;
        }
        return buffer.putBytes(index, string.getBytes(Charsets.UTF_8));
    }

    public static String extractString(final IByteBuffer buffer, final int index, final int size) {
        if (size == 0) {
            return null;
        }
        final byte[] bytes = new byte[size];
        buffer.getBytes(index, bytes);
        final String string = new String(bytes, Charsets.UTF_8);
        return string;
    }

    public static int calculateBytesCount(final String string) {
        return string.getBytes().length;
    }

}
