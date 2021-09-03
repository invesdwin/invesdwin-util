package de.invesdwin.util.streams.buffer.extend.internal;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.agrona.AsciiEncoding.ASCII_DIGITS;
import static org.agrona.AsciiEncoding.MINUS_SIGN;
import static org.agrona.AsciiEncoding.MIN_INTEGER_VALUE;
import static org.agrona.AsciiEncoding.MIN_LONG_VALUE;
import static org.agrona.AsciiEncoding.ZERO;
import static org.agrona.AsciiEncoding.endOffset;
import static org.agrona.BitUtil.SIZE_OF_BYTE;
import static org.agrona.BitUtil.SIZE_OF_CHAR;
import static org.agrona.BitUtil.SIZE_OF_DOUBLE;
import static org.agrona.BitUtil.SIZE_OF_FLOAT;
import static org.agrona.BitUtil.SIZE_OF_INT;
import static org.agrona.BitUtil.SIZE_OF_LONG;
import static org.agrona.BitUtil.SIZE_OF_SHORT;
import static org.agrona.BufferUtil.ARRAY_BASE_OFFSET;
import static org.agrona.BufferUtil.NATIVE_BYTE_ORDER;
import static org.agrona.BufferUtil.NULL_BYTES;
import static org.agrona.BufferUtil.address;
import static org.agrona.BufferUtil.array;
import static org.agrona.BufferUtil.arrayOffset;
import static org.agrona.UnsafeAccess.UNSAFE;

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Arrays;

import javax.annotation.concurrent.NotThreadSafe;

import org.agrona.AsciiEncoding;
import org.agrona.AsciiNumberFormatException;
import org.agrona.BufferUtil;
import org.agrona.DirectBuffer;
import org.agrona.LangUtil;
import org.agrona.MutableDirectBuffer;

import de.invesdwin.util.streams.buffer.ByteBuffers;

@NotThreadSafe
@SuppressWarnings("restriction")
public class ExpandableUninitializedArrayBuffer implements MutableDirectBuffer {
    /**
     * Maximum length to which the underlying buffer can grow. Some JVMs store state in the last few bytes.
     */
    public static final int MAX_ARRAY_LENGTH = Integer.MAX_VALUE - 8;

    /**
     * Initial capacity of the buffer from which it will expand as necessary.
     */
    public static final int INITIAL_CAPACITY = 128;

    private byte[] byteArray;

    /**
     * Create an {@link ExpandableUninitializedArrayBuffer} with an initial length of {@link #INITIAL_CAPACITY}.
     */
    public ExpandableUninitializedArrayBuffer() {
        this(INITIAL_CAPACITY);
    }

    /**
     * Create an {@link ExpandableUninitializedArrayBuffer} with a provided initial length.
     *
     * @param initialCapacity
     *            of the buffer.
     */
    public ExpandableUninitializedArrayBuffer(final int initialCapacity) {
        byteArray = ByteBuffers.allocateByteArray(initialCapacity);
    }

    public ExpandableUninitializedArrayBuffer(final byte[] byteArray) {
        this.byteArray = byteArray;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void wrap(final byte[] buffer) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void wrap(final byte[] buffer, final int offset, final int length) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void wrap(final java.nio.ByteBuffer buffer) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void wrap(final java.nio.ByteBuffer buffer, final int offset, final int length) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void wrap(final DirectBuffer buffer) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void wrap(final DirectBuffer buffer, final int offset, final int length) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void wrap(final long address, final int length) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long addressOffset() {
        return ARRAY_BASE_OFFSET;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] byteArray() {
        return byteArray;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public java.nio.ByteBuffer byteBuffer() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMemory(final int index, final int length, final byte value) {
        ensureCapacity(index, length);
        Arrays.fill(byteArray, index, index + length, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int capacity() {
        return byteArray.length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isExpandable() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void checkLimit(final int limit) {
        ensureCapacity(limit, SIZE_OF_BYTE);
    }

    ///////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public long getLong(final int index, final ByteOrder byteOrder) {
        boundsCheck0(index, SIZE_OF_LONG);

        long bits = UNSAFE.getLong(byteArray, ARRAY_BASE_OFFSET + index);
        if (NATIVE_BYTE_ORDER != byteOrder) {
            bits = Long.reverseBytes(bits);
        }

        return bits;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putLong(final int index, final long value, final ByteOrder byteOrder) {
        ensureCapacity(index, SIZE_OF_LONG);

        long bits = value;
        if (NATIVE_BYTE_ORDER != byteOrder) {
            bits = Long.reverseBytes(bits);
        }

        UNSAFE.putLong(byteArray, ARRAY_BASE_OFFSET + index, bits);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getLong(final int index) {
        boundsCheck0(index, SIZE_OF_LONG);

        return UNSAFE.getLong(byteArray, ARRAY_BASE_OFFSET + index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putLong(final int index, final long value) {
        ensureCapacity(index, SIZE_OF_LONG);

        UNSAFE.putLong(byteArray, ARRAY_BASE_OFFSET + index, value);
    }

    ///////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public int getInt(final int index, final ByteOrder byteOrder) {
        boundsCheck0(index, SIZE_OF_INT);

        int bits = UNSAFE.getInt(byteArray, ARRAY_BASE_OFFSET + index);
        if (NATIVE_BYTE_ORDER != byteOrder) {
            bits = Integer.reverseBytes(bits);
        }

        return bits;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putInt(final int index, final int value, final ByteOrder byteOrder) {
        ensureCapacity(index, SIZE_OF_INT);

        int bits = value;
        if (NATIVE_BYTE_ORDER != byteOrder) {
            bits = Integer.reverseBytes(bits);
        }

        UNSAFE.putInt(byteArray, ARRAY_BASE_OFFSET + index, bits);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getInt(final int index) {
        boundsCheck0(index, SIZE_OF_INT);

        return UNSAFE.getInt(byteArray, ARRAY_BASE_OFFSET + index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putInt(final int index, final int value) {
        ensureCapacity(index, SIZE_OF_INT);

        UNSAFE.putInt(byteArray, ARRAY_BASE_OFFSET + index, value);
    }

    ///////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public double getDouble(final int index, final ByteOrder byteOrder) {
        boundsCheck0(index, SIZE_OF_DOUBLE);

        if (NATIVE_BYTE_ORDER != byteOrder) {
            final long bits = UNSAFE.getLong(byteArray, ARRAY_BASE_OFFSET + index);
            return Double.longBitsToDouble(Long.reverseBytes(bits));
        } else {
            return UNSAFE.getDouble(byteArray, ARRAY_BASE_OFFSET + index);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putDouble(final int index, final double value, final ByteOrder byteOrder) {
        ensureCapacity(index, SIZE_OF_DOUBLE);

        if (NATIVE_BYTE_ORDER != byteOrder) {
            final long bits = Long.reverseBytes(Double.doubleToRawLongBits(value));
            UNSAFE.putLong(byteArray, ARRAY_BASE_OFFSET + index, bits);
        } else {
            UNSAFE.putDouble(byteArray, ARRAY_BASE_OFFSET + index, value);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getDouble(final int index) {
        boundsCheck0(index, SIZE_OF_DOUBLE);

        return UNSAFE.getDouble(byteArray, ARRAY_BASE_OFFSET + index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putDouble(final int index, final double value) {
        ensureCapacity(index, SIZE_OF_DOUBLE);

        UNSAFE.putDouble(byteArray, ARRAY_BASE_OFFSET + index, value);
    }

    ///////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public float getFloat(final int index, final ByteOrder byteOrder) {
        boundsCheck0(index, SIZE_OF_FLOAT);

        if (NATIVE_BYTE_ORDER != byteOrder) {
            final int bits = UNSAFE.getInt(byteArray, ARRAY_BASE_OFFSET + index);
            return Float.intBitsToFloat(Integer.reverseBytes(bits));
        } else {
            return UNSAFE.getFloat(byteArray, ARRAY_BASE_OFFSET + index);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putFloat(final int index, final float value, final ByteOrder byteOrder) {
        ensureCapacity(index, SIZE_OF_FLOAT);

        if (NATIVE_BYTE_ORDER != byteOrder) {
            final int bits = Integer.reverseBytes(Float.floatToRawIntBits(value));
            UNSAFE.putInt(byteArray, ARRAY_BASE_OFFSET + index, bits);
        } else {
            UNSAFE.putFloat(byteArray, ARRAY_BASE_OFFSET + index, value);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getFloat(final int index) {
        boundsCheck0(index, SIZE_OF_FLOAT);

        return UNSAFE.getFloat(byteArray, ARRAY_BASE_OFFSET + index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putFloat(final int index, final float value) {
        ensureCapacity(index, SIZE_OF_FLOAT);

        UNSAFE.putFloat(byteArray, ARRAY_BASE_OFFSET + index, value);
    }

    ///////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public short getShort(final int index, final ByteOrder byteOrder) {
        boundsCheck0(index, SIZE_OF_SHORT);

        short bits = UNSAFE.getShort(byteArray, ARRAY_BASE_OFFSET + index);
        if (NATIVE_BYTE_ORDER != byteOrder) {
            bits = Short.reverseBytes(bits);
        }

        return bits;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putShort(final int index, final short value, final ByteOrder byteOrder) {
        ensureCapacity(index, SIZE_OF_SHORT);

        short bits = value;
        if (NATIVE_BYTE_ORDER != byteOrder) {
            bits = Short.reverseBytes(bits);
        }

        UNSAFE.putShort(byteArray, ARRAY_BASE_OFFSET + index, bits);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public short getShort(final int index) {
        boundsCheck0(index, SIZE_OF_SHORT);

        return UNSAFE.getShort(byteArray, ARRAY_BASE_OFFSET + index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putShort(final int index, final short value) {
        ensureCapacity(index, SIZE_OF_SHORT);

        UNSAFE.putShort(byteArray, ARRAY_BASE_OFFSET + index, value);
    }

    ///////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public byte getByte(final int index) {
        return byteArray[index];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putByte(final int index, final byte value) {
        ensureCapacity(index, SIZE_OF_BYTE);
        byteArray[index] = value;
    }

    private void putByte0(final int index, final byte value) {
        ensureCapacity(index, SIZE_OF_BYTE);
        byteArray[index] = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getBytes(final int index, final byte[] dst) {
        getBytes(index, dst, 0, dst.length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getBytes(final int index, final byte[] dst, final int offset, final int length) {
        System.arraycopy(byteArray, index, dst, offset, length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getBytes(final int index, final MutableDirectBuffer dstBuffer, final int dstIndex, final int length) {
        dstBuffer.putBytes(dstIndex, this, index, length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getBytes(final int index, final java.nio.ByteBuffer dstBuffer, final int length) {
        final int dstOffset = dstBuffer.position();
        getBytes(index, dstBuffer, dstOffset, length);
        dstBuffer.position(dstOffset + length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getBytes(final int index, final java.nio.ByteBuffer dstBuffer, final int dstOffset, final int length) {
        boundsCheck0(index, length);
        BufferUtil.boundsCheck(dstBuffer, dstOffset, length);

        final byte[] dstByteArray;
        final long dstBaseOffset;
        if (dstBuffer.isDirect()) {
            dstByteArray = null;
            dstBaseOffset = address(dstBuffer);
        } else {
            dstByteArray = array(dstBuffer);
            dstBaseOffset = ARRAY_BASE_OFFSET + arrayOffset(dstBuffer);
        }

        UNSAFE.copyMemory(byteArray, ARRAY_BASE_OFFSET + index, dstByteArray, dstBaseOffset + dstOffset, length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putBytes(final int index, final byte[] src) {
        putBytes(index, src, 0, src.length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putBytes(final int index, final byte[] src, final int offset, final int length) {
        ensureCapacity(index, length);
        System.arraycopy(src, offset, byteArray, index, length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putBytes(final int index, final java.nio.ByteBuffer srcBuffer, final int length) {
        final int srcIndex = srcBuffer.position();
        putBytes(index, srcBuffer, srcIndex, length);
        srcBuffer.position(srcIndex + length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putBytes(final int index, final java.nio.ByteBuffer srcBuffer, final int srcIndex, final int length) {
        ensureCapacity(index, length);
        BufferUtil.boundsCheck(srcBuffer, srcIndex, length);

        final byte[] srcByteArray;
        final long srcBaseOffset;
        if (srcBuffer.isDirect()) {
            srcByteArray = null;
            srcBaseOffset = address(srcBuffer);
        } else {
            srcByteArray = array(srcBuffer);
            srcBaseOffset = ARRAY_BASE_OFFSET + arrayOffset(srcBuffer);
        }

        UNSAFE.copyMemory(srcByteArray, srcBaseOffset + srcIndex, byteArray, ARRAY_BASE_OFFSET + index, length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putBytes(final int index, final DirectBuffer srcBuffer, final int srcIndex, final int length) {
        ensureCapacity(index, length);
        srcBuffer.boundsCheck(srcIndex, length);

        UNSAFE.copyMemory(srcBuffer.byteArray(), srcBuffer.addressOffset() + srcIndex, byteArray,
                ARRAY_BASE_OFFSET + index, length);
    }

    ///////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public char getChar(final int index, final ByteOrder byteOrder) {
        boundsCheck0(index, SIZE_OF_SHORT);

        char bits = UNSAFE.getChar(byteArray, ARRAY_BASE_OFFSET + index);
        if (NATIVE_BYTE_ORDER != byteOrder) {
            bits = (char) Short.reverseBytes((short) bits);
        }

        return bits;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putChar(final int index, final char value, final ByteOrder byteOrder) {
        ensureCapacity(index, SIZE_OF_CHAR);

        char bits = value;
        if (NATIVE_BYTE_ORDER != byteOrder) {
            bits = (char) Short.reverseBytes((short) bits);
        }

        UNSAFE.putChar(byteArray, ARRAY_BASE_OFFSET + index, bits);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public char getChar(final int index) {
        boundsCheck0(index, SIZE_OF_CHAR);

        return UNSAFE.getChar(byteArray, ARRAY_BASE_OFFSET + index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putChar(final int index, final char value) {
        ensureCapacity(index, SIZE_OF_CHAR);

        UNSAFE.putChar(byteArray, ARRAY_BASE_OFFSET + index, value);
    }

    ///////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStringAscii(final int index) {
        boundsCheck0(index, STR_HEADER_LEN);

        final int length = UNSAFE.getInt(byteArray, ARRAY_BASE_OFFSET + index);

        return getStringAscii(index, length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getStringAscii(final int index, final Appendable appendable) {
        boundsCheck0(index, STR_HEADER_LEN);

        final int length = UNSAFE.getInt(byteArray, ARRAY_BASE_OFFSET + index);

        return getStringAscii(index, length, appendable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStringAscii(final int index, final ByteOrder byteOrder) {
        boundsCheck0(index, STR_HEADER_LEN);

        int bits = UNSAFE.getInt(byteArray, ARRAY_BASE_OFFSET + index);
        if (NATIVE_BYTE_ORDER != byteOrder) {
            bits = Integer.reverseBytes(bits);
        }

        final int length = bits;

        return getStringAscii(index, length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getStringAscii(final int index, final Appendable appendable, final ByteOrder byteOrder) {
        boundsCheck0(index, STR_HEADER_LEN);

        int bits = UNSAFE.getInt(byteArray, ARRAY_BASE_OFFSET + index);
        if (NATIVE_BYTE_ORDER != byteOrder) {
            bits = Integer.reverseBytes(bits);
        }

        final int length = bits;

        return getStringAscii(index, length, appendable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStringAscii(final int index, final int length) {
        final byte[] stringInBytes = ByteBuffers.allocateByteArray(length);
        System.arraycopy(byteArray, index + STR_HEADER_LEN, stringInBytes, 0, length);

        return new String(stringInBytes, US_ASCII);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getStringAscii(final int index, final int length, final Appendable appendable) {
        try {
            for (int i = index + STR_HEADER_LEN, limit = index + STR_HEADER_LEN + length; i < limit; i++) {
                final char c = (char) (byteArray[i]);
                appendable.append(c > 127 ? '?' : c);
            }
        } catch (final IOException ex) {
            LangUtil.rethrowUnchecked(ex);
        }

        return length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int putStringAscii(final int index, final String value) {
        final int length = value != null ? value.length() : 0;

        ensureCapacity(index, length + STR_HEADER_LEN);

        UNSAFE.putInt(byteArray, ARRAY_BASE_OFFSET + index, length);

        for (int i = 0; i < length; i++) {
            char c = value.charAt(i);
            if (c > 127) {
                c = '?';
            }

            UNSAFE.putByte(byteArray, ARRAY_BASE_OFFSET + STR_HEADER_LEN + index + i, (byte) c);
        }

        return STR_HEADER_LEN + length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int putStringAscii(final int index, final CharSequence value) {
        final int length = value != null ? value.length() : 0;

        ensureCapacity(index, length + STR_HEADER_LEN);

        UNSAFE.putInt(byteArray, ARRAY_BASE_OFFSET + index, length);

        for (int i = 0; i < length; i++) {
            char c = value.charAt(i);
            if (c > 127) {
                c = '?';
            }

            UNSAFE.putByte(byteArray, ARRAY_BASE_OFFSET + STR_HEADER_LEN + index + i, (byte) c);
        }

        return STR_HEADER_LEN + length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int putStringAscii(final int index, final String value, final ByteOrder byteOrder) {
        final int length = value != null ? value.length() : 0;

        ensureCapacity(index, length + STR_HEADER_LEN);

        int bits = length;
        if (NATIVE_BYTE_ORDER != byteOrder) {
            bits = Integer.reverseBytes(bits);
        }

        UNSAFE.putInt(byteArray, ARRAY_BASE_OFFSET + index, bits);

        for (int i = 0; i < length; i++) {
            char c = value.charAt(i);
            if (c > 127) {
                c = '?';
            }

            UNSAFE.putByte(byteArray, ARRAY_BASE_OFFSET + STR_HEADER_LEN + index + i, (byte) c);
        }

        return STR_HEADER_LEN + length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int putStringAscii(final int index, final CharSequence value, final ByteOrder byteOrder) {
        final int length = value != null ? value.length() : 0;

        ensureCapacity(index, length + STR_HEADER_LEN);

        int bits = length;
        if (NATIVE_BYTE_ORDER != byteOrder) {
            bits = Integer.reverseBytes(bits);
        }

        UNSAFE.putInt(byteArray, ARRAY_BASE_OFFSET + index, bits);

        for (int i = 0; i < length; i++) {
            char c = value.charAt(i);
            if (c > 127) {
                c = '?';
            }

            UNSAFE.putByte(byteArray, ARRAY_BASE_OFFSET + STR_HEADER_LEN + index + i, (byte) c);
        }

        return STR_HEADER_LEN + length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStringWithoutLengthAscii(final int index, final int length) {
        final byte[] stringInBytes = ByteBuffers.allocateByteArray(length);
        System.arraycopy(byteArray, index, stringInBytes, 0, length);

        return new String(stringInBytes, US_ASCII);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getStringWithoutLengthAscii(final int index, final int length, final Appendable appendable) {
        try {
            for (int i = index, limit = index + length; i < limit; i++) {
                final char c = (char) (byteArray[i]);
                appendable.append(c > 127 ? '?' : c);
            }
        } catch (final IOException ex) {
            LangUtil.rethrowUnchecked(ex);
        }

        return length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int putStringWithoutLengthAscii(final int index, final String value) {
        final int length = value != null ? value.length() : 0;

        ensureCapacity(index, length);

        for (int i = 0; i < length; i++) {
            char c = value.charAt(i);
            if (c > 127) {
                c = '?';
            }

            UNSAFE.putByte(byteArray, ARRAY_BASE_OFFSET + index + i, (byte) c);
        }

        return length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int putStringWithoutLengthAscii(final int index, final CharSequence value) {
        final int length = value != null ? value.length() : 0;

        ensureCapacity(index, length);

        for (int i = 0; i < length; i++) {
            char c = value.charAt(i);
            if (c > 127) {
                c = '?';
            }

            UNSAFE.putByte(byteArray, ARRAY_BASE_OFFSET + index + i, (byte) c);
        }

        return length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int putStringWithoutLengthAscii(final int index, final String value, final int valueOffset,
            final int length) {
        final int len = value != null ? Math.min(value.length() - valueOffset, length) : 0;

        ensureCapacity(index, len);

        for (int i = 0; i < len; i++) {
            char c = value.charAt(valueOffset + i);
            if (c > 127) {
                c = '?';
            }

            UNSAFE.putByte(byteArray, ARRAY_BASE_OFFSET + index + i, (byte) c);
        }

        return len;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int putStringWithoutLengthAscii(final int index, final CharSequence value, final int valueOffset,
            final int length) {
        final int len = value != null ? Math.min(value.length() - valueOffset, length) : 0;

        ensureCapacity(index, len);

        for (int i = 0; i < len; i++) {
            char c = value.charAt(valueOffset + i);
            if (c > 127) {
                c = '?';
            }

            UNSAFE.putByte(byteArray, ARRAY_BASE_OFFSET + index + i, (byte) c);
        }

        return len;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStringUtf8(final int index) {
        boundsCheck0(index, STR_HEADER_LEN);

        final int length = UNSAFE.getInt(byteArray, ARRAY_BASE_OFFSET + index);

        return getStringUtf8(index, length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStringUtf8(final int index, final ByteOrder byteOrder) {
        boundsCheck0(index, STR_HEADER_LEN);

        int bits = UNSAFE.getInt(byteArray, ARRAY_BASE_OFFSET + index);
        if (NATIVE_BYTE_ORDER != byteOrder) {
            bits = Integer.reverseBytes(bits);
        }

        final int length = bits;

        return getStringUtf8(index, length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStringUtf8(final int index, final int length) {
        final byte[] stringInBytes = ByteBuffers.allocateByteArray(length);
        System.arraycopy(byteArray, index + STR_HEADER_LEN, stringInBytes, 0, length);

        return new String(stringInBytes, UTF_8);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int putStringUtf8(final int index, final String value) {
        return putStringUtf8(index, value, Integer.MAX_VALUE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int putStringUtf8(final int index, final String value, final ByteOrder byteOrder) {
        return putStringUtf8(index, value, byteOrder, Integer.MAX_VALUE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int putStringUtf8(final int index, final String value, final int maxEncodedLength) {
        final byte[] bytes = value != null ? value.getBytes(UTF_8) : NULL_BYTES;
        if (bytes.length > maxEncodedLength) {
            throw new IllegalArgumentException("Encoded string larger than maximum size: " + maxEncodedLength);
        }

        ensureCapacity(index, STR_HEADER_LEN + bytes.length);

        UNSAFE.putInt(byteArray, ARRAY_BASE_OFFSET + index, bytes.length);
        System.arraycopy(bytes, 0, byteArray, index + STR_HEADER_LEN, bytes.length);

        return STR_HEADER_LEN + bytes.length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int putStringUtf8(final int index, final String value, final ByteOrder byteOrder,
            final int maxEncodedLength) {
        final byte[] bytes = value != null ? value.getBytes(UTF_8) : NULL_BYTES;
        if (bytes.length > maxEncodedLength) {
            throw new IllegalArgumentException("Encoded string larger than maximum size: " + maxEncodedLength);
        }

        ensureCapacity(index, STR_HEADER_LEN + bytes.length);

        int bits = bytes.length;
        if (NATIVE_BYTE_ORDER != byteOrder) {
            bits = Integer.reverseBytes(bits);
        }

        UNSAFE.putInt(byteArray, ARRAY_BASE_OFFSET + index, bits);
        System.arraycopy(bytes, 0, byteArray, index + STR_HEADER_LEN, bytes.length);

        return STR_HEADER_LEN + bytes.length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStringWithoutLengthUtf8(final int index, final int length) {
        final byte[] stringInBytes = ByteBuffers.allocateByteArray(length);
        System.arraycopy(byteArray, index, stringInBytes, 0, length);

        return new String(stringInBytes, UTF_8);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int putStringWithoutLengthUtf8(final int index, final String value) {
        final byte[] bytes = value != null ? value.getBytes(UTF_8) : NULL_BYTES;
        ensureCapacity(index, bytes.length);
        System.arraycopy(bytes, 0, byteArray, index, bytes.length);

        return bytes.length;
    }

    ///////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public int parseNaturalIntAscii(final int index, final int length) {
        boundsCheck0(index, length);

        if (length <= 0) {
            throw new AsciiNumberFormatException("empty string: index=" + index + " length=" + length);
        }

        final int end = index + length;
        int tally = 0;
        for (int i = index; i < end; i++) {
            tally = (tally * 10) + AsciiEncoding.getDigit(i, byteArray[i]);
        }

        return tally;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long parseNaturalLongAscii(final int index, final int length) {
        boundsCheck0(index, length);

        if (length <= 0) {
            throw new AsciiNumberFormatException("empty string: index=" + index + " length=" + length);
        }

        final int end = index + length;
        long tally = 0;
        for (int i = index; i < end; i++) {
            tally = (tally * 10) + AsciiEncoding.getDigit(i, byteArray[i]);
        }

        return tally;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int parseIntAscii(final int index, final int length) {
        boundsCheck0(index, length);

        if (length <= 0) {
            throw new AsciiNumberFormatException("empty string: index=" + index + " length=" + length);
        } else if (1 == length) {
            return AsciiEncoding.getDigit(index, byteArray[index]);
        }

        final int endExclusive = index + length;
        final int first = byteArray[index];
        int i = index;

        if (first == MINUS_SIGN) {
            i++;
        }

        int tally = 0;
        //CHECKSTYLE:OFF
        for (; i < endExclusive; i++) {
            //CHECKSTYLE:ON
            tally = (tally * 10) + AsciiEncoding.getDigit(i, byteArray[i]);
        }

        if (first == MINUS_SIGN) {
            tally = -tally;
        }

        return tally;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long parseLongAscii(final int index, final int length) {
        boundsCheck0(index, length);

        if (length <= 0) {
            throw new AsciiNumberFormatException("empty string: index=" + index + " length=" + length);
        } else if (1 == length) {
            return AsciiEncoding.getDigit(index, byteArray[index]);
        }

        final int endExclusive = index + length;
        final int first = byteArray[index];
        int i = index;

        if (first == MINUS_SIGN) {
            i++;
        }

        long tally = 0;
        //CHECKSTYLE:OFF
        for (; i < endExclusive; i++) {
            //CHECKSTYLE:ON
            tally = (tally * 10) + AsciiEncoding.getDigit(i, byteArray[i]);
        }

        if (first == MINUS_SIGN) {
            tally = -tally;
        }

        return tally;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int putIntAscii(final int index, final int value) {
        if (value == 0) {
            putByte0(index, ZERO);
            return 1;
        }

        if (value == Integer.MIN_VALUE) {
            putBytes(index, MIN_INTEGER_VALUE);
            return MIN_INTEGER_VALUE.length;
        }

        int start = index;
        int quotient = value;
        int length = 1;
        if (value < 0) {
            putByte0(index, MINUS_SIGN);
            start++;
            length++;
            quotient = -quotient;
        }

        int i = endOffset(quotient);
        length += i;

        ensureCapacity(index, length);

        final byte[] dest = byteArray;
        while (quotient >= 100) {
            final int position = (quotient % 100) << 1;
            quotient /= 100;
            dest[i + start] = ASCII_DIGITS[position + 1];
            dest[i - 1 + start] = ASCII_DIGITS[position];
            i -= 2;
        }

        if (quotient < 10) {
            dest[i + start] = (byte) (ZERO + quotient);
        } else {
            final int position = quotient << 1;
            dest[i + start] = ASCII_DIGITS[position + 1];
            dest[i - 1 + start] = ASCII_DIGITS[position];
        }

        return length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int putNaturalIntAscii(final int index, final int value) {
        if (value == 0) {
            putByte0(index, ZERO);
            return 1;
        }

        int i = endOffset(value);
        final int length = i + 1;

        ensureCapacity(index, length);

        int quotient = value;
        final byte[] dest = byteArray;
        while (quotient >= 100) {
            final int position = (quotient % 100) << 1;
            quotient /= 100;
            dest[i + index] = ASCII_DIGITS[position + 1];
            dest[i - 1 + index] = ASCII_DIGITS[position];
            i -= 2;
        }

        if (quotient < 10) {
            dest[i + index] = (byte) (ZERO + quotient);
        } else {
            final int position = quotient << 1;
            dest[i + index] = ASCII_DIGITS[position + 1];
            dest[i - 1 + index] = ASCII_DIGITS[position];
        }

        return length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putNaturalPaddedIntAscii(final int offset, final int length, final int value) {
        final int end = offset + length;
        int remainder = value;
        for (int index = end - 1; index >= offset; index--) {
            final int digit = remainder % 10;
            remainder = remainder / 10;
            putByte0(index, (byte) (ZERO + digit));
        }

        if (remainder != 0) {
            throw new NumberFormatException("Cannot write " + value + " in " + length + " bytes");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int putNaturalIntAsciiFromEnd(final int value, final int endExclusive) {
        int remainder = value;
        int index = endExclusive;
        while (remainder > 0) {
            index--;
            final int digit = remainder % 10;
            remainder = remainder / 10;
            putByte0(index, (byte) (ZERO + digit));
        }

        return index;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int putNaturalLongAscii(final int index, final long value) {
        if (value == 0) {
            putByte0(index, ZERO);
            return 1;
        }

        int i = endOffset(value);
        final int length = i + 1;

        ensureCapacity(index, length);

        long quotient = value;
        final byte[] dest = byteArray;
        while (quotient >= 100000000) {
            final int lastEightDigits = (int) (quotient % 100000000);
            quotient /= 100000000;

            final int upperPart = lastEightDigits / 10000;
            final int lowerPart = lastEightDigits % 10000;

            final int u1 = (upperPart / 100) << 1;
            final int u2 = (upperPart % 100) << 1;
            final int l1 = (lowerPart / 100) << 1;
            final int l2 = (lowerPart % 100) << 1;

            i -= 8;

            dest[index + i + 1] = ASCII_DIGITS[u1];
            dest[index + i + 2] = ASCII_DIGITS[u1 + 1];
            dest[index + i + 3] = ASCII_DIGITS[u2];
            dest[index + i + 4] = ASCII_DIGITS[u2 + 1];
            dest[index + i + 5] = ASCII_DIGITS[l1];
            dest[index + i + 6] = ASCII_DIGITS[l1 + 1];
            dest[index + i + 7] = ASCII_DIGITS[l2];
            dest[index + i + 8] = ASCII_DIGITS[l2 + 1];
        }

        while (quotient >= 100) {
            final int position = (int) ((quotient % 100) << 1);
            quotient /= 100;
            dest[index + i] = ASCII_DIGITS[position + 1];
            dest[index + i - 1] = ASCII_DIGITS[position];
            i -= 2;
        }

        if (quotient < 10) {
            dest[index + i] = (byte) (ZERO + quotient);
        } else {
            final int position = (int) (quotient << 1);
            dest[index + i] = ASCII_DIGITS[position + 1];
            dest[index + i - 1] = ASCII_DIGITS[position];
        }

        return length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int putLongAscii(final int index, final long value) {
        if (value == 0) {
            putByte0(index, ZERO);
            return 1;
        }

        if (value == Long.MIN_VALUE) {
            putBytes(index, MIN_LONG_VALUE);
            return MIN_LONG_VALUE.length;
        }

        int start = index;
        long quotient = value;
        int length = 1;
        if (value < 0) {
            putByte0(index, MINUS_SIGN);
            start++;
            length++;
            quotient = -quotient;
        }

        int i = endOffset(quotient);
        length += i;

        ensureCapacity(index, length);

        final byte[] dest = byteArray;
        while (quotient >= 100000000) {
            final int lastEightDigits = (int) (quotient % 100000000);
            quotient /= 100000000;

            final int upperPart = lastEightDigits / 10000;
            final int lowerPart = lastEightDigits % 10000;

            final int u1 = (upperPart / 100) << 1;
            final int u2 = (upperPart % 100) << 1;
            final int l1 = (lowerPart / 100) << 1;
            final int l2 = (lowerPart % 100) << 1;

            i -= 8;

            dest[start + i + 1] = ASCII_DIGITS[u1];
            dest[start + i + 2] = ASCII_DIGITS[u1 + 1];
            dest[start + i + 3] = ASCII_DIGITS[u2];
            dest[start + i + 4] = ASCII_DIGITS[u2 + 1];
            dest[start + i + 5] = ASCII_DIGITS[l1];
            dest[start + i + 6] = ASCII_DIGITS[l1 + 1];
            dest[start + i + 7] = ASCII_DIGITS[l2];
            dest[start + i + 8] = ASCII_DIGITS[l2 + 1];
        }

        while (quotient >= 100) {
            final int position = (int) ((quotient % 100) << 1);
            quotient /= 100;
            dest[start + i] = ASCII_DIGITS[position + 1];
            dest[start + i - 1] = ASCII_DIGITS[position];
            i -= 2;
        }

        if (quotient < 10) {
            dest[start + i] = (byte) (ZERO + quotient);
        } else {
            final int position = (int) (quotient << 1);
            dest[start + i] = ASCII_DIGITS[position + 1];
            dest[start + i - 1] = ASCII_DIGITS[position];
        }

        return length;
    }

    ///////////////////////////////////////////////////////////////////////////

    private void ensureCapacity(final int index, final int length) {
        if (index < 0 || length < 0) {
            throw new IndexOutOfBoundsException("negative value: index=" + index + " length=" + length);
        }

        final long resultingPosition = index + (long) length;
        final int currentArrayLength = byteArray.length;
        if (resultingPosition > currentArrayLength) {
            if (resultingPosition > MAX_ARRAY_LENGTH) {
                throw new IndexOutOfBoundsException(
                        "index=" + index + " length=" + length + " maxCapacity=" + MAX_ARRAY_LENGTH);
            }

            byteArray = Arrays.copyOf(byteArray, calculateExpansion(currentArrayLength, resultingPosition));
        }
    }

    private int calculateExpansion(final int currentLength, final long requiredLength) {
        long value = Math.max(currentLength, INITIAL_CAPACITY);

        while (value < requiredLength) {
            value = value + (value >> 1);

            if (value > MAX_ARRAY_LENGTH) {
                value = MAX_ARRAY_LENGTH;
            }
        }

        return (int) value;
    }

    private void boundsCheck0(final int index, final int length) {
        final int currentArrayLength = byteArray.length;
        final long resultingPosition = index + (long) length;
        if (index < 0 || length < 0 || resultingPosition > currentArrayLength) {
            throw new IndexOutOfBoundsException(
                    "index=" + index + " length=" + length + " capacity=" + currentArrayLength);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void boundsCheck(final int index, final int length) {
        boundsCheck0(index, length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int wrapAdjustment() {
        return 0;
    }

    ///////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final ExpandableUninitializedArrayBuffer that = (ExpandableUninitializedArrayBuffer) obj;

        return Arrays.equals(this.byteArray, that.byteArray);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(byteArray);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(final DirectBuffer that) {
        final int thisCapacity = this.capacity();
        final int thatCapacity = that.capacity();
        final byte[] thisByteArray = this.byteArray;
        final byte[] thatByteArray = that.byteArray();
        final long thisOffset = this.addressOffset();
        final long thatOffset = that.addressOffset();

        for (int i = 0, length = Math.min(thisCapacity, thatCapacity); i < length; i++) {
            final int cmp = Byte.compare(UNSAFE.getByte(thisByteArray, thisOffset + i),
                    UNSAFE.getByte(thatByteArray, thatOffset + i));

            if (0 != cmp) {
                return cmp;
            }
        }

        if (thisCapacity != thatCapacity) {
            return thisCapacity - thatCapacity;
        }

        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "ExpandableArrayBuffer{" + "byteArray=" + byteArray + // lgtm [java/print-array]
                " byteArray.length" + (null == byteArray ? 0 : byteArray.length) + '}';
    }
}
