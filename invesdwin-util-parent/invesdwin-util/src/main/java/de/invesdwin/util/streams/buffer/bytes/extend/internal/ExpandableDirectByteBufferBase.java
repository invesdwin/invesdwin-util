package de.invesdwin.util.streams.buffer.bytes.extend.internal;

import static java.nio.ByteOrder.LITTLE_ENDIAN;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.agrona.AsciiEncoding.ASCII_DIGITS;
import static org.agrona.AsciiEncoding.INTEGER_ABSOLUTE_MIN_VALUE;
import static org.agrona.AsciiEncoding.INT_MAX_DIGITS;
import static org.agrona.AsciiEncoding.LONG_MAX_DIGITS;
import static org.agrona.AsciiEncoding.LONG_MAX_VALUE_DIGITS;
import static org.agrona.AsciiEncoding.LONG_MIN_VALUE_DIGITS;
import static org.agrona.AsciiEncoding.MINUS_SIGN;
import static org.agrona.AsciiEncoding.MIN_INTEGER_VALUE;
import static org.agrona.AsciiEncoding.MIN_LONG_VALUE;
import static org.agrona.AsciiEncoding.ZERO;
import static org.agrona.AsciiEncoding.digitCount;
import static org.agrona.AsciiEncoding.isDigit;
import static org.agrona.AsciiEncoding.isEightDigitAsciiEncodedNumber;
import static org.agrona.AsciiEncoding.isFourDigitsAsciiEncodedNumber;
import static org.agrona.AsciiEncoding.parseEightDigitsLittleEndian;
import static org.agrona.AsciiEncoding.parseFourDigitsLittleEndian;
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
import static org.agrona.UnsafeAccess.MEMSET_HACK_REQUIRED;
import static org.agrona.UnsafeAccess.MEMSET_HACK_THRESHOLD;

import java.io.IOException;
import java.nio.ByteOrder;

import javax.annotation.concurrent.NotThreadSafe;

import org.agrona.AsciiNumberFormatException;
import org.agrona.BufferUtil;
import org.agrona.DirectBuffer;
import org.agrona.LangUtil;
import org.agrona.MutableDirectBuffer;

import de.invesdwin.util.lang.reflection.Reflections;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;

/**
 * Extracted from org.agrona.ExpandableDirectByteBuffer
 */
@NotThreadSafe
@SuppressWarnings("restriction")
public class ExpandableDirectByteBufferBase implements MutableDirectBuffer {
    /**
     * Maximum length to which the underlying buffer can grow.
     */
    public static final int MAX_BUFFER_LENGTH = 1024 * 1024 * 1024;

    /**
     * Initial capacity of the buffer from which it will expand.
     */
    public static final int INITIAL_CAPACITY = 128;

    private long address;
    private int capacity;
    private java.nio.ByteBuffer byteBuffer;

    /**
     * Create an {@link ExpandableDirectByteBufferBase} with an initial length of {@link #INITIAL_CAPACITY}.
     */
    public ExpandableDirectByteBufferBase() {
        this(INITIAL_CAPACITY);
    }

    /**
     * Create an {@link ExpandableDirectByteBufferBase} with a provided initial capacity.
     *
     * @param initialCapacity
     *            of the backing array.
     */
    public ExpandableDirectByteBufferBase(final int initialCapacity) {
        byteBuffer = java.nio.ByteBuffer.allocateDirect(initialCapacity);
        capacity = initialCapacity;
        address = address(byteBuffer);
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
        return address;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] byteArray() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public java.nio.ByteBuffer byteBuffer() {
        return byteBuffer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMemory(final int index, final int length, final byte value) {
        ensureCapacity(index, length);

        final long offset = address + index;
        if (MEMSET_HACK_REQUIRED && length > MEMSET_HACK_THRESHOLD && 0 == (offset & 1)) {
            // This horrible filth is to encourage the JVM to call memset() when address is even.
            Reflections.getUnsafe().putByte(null, offset, value);
            Reflections.getUnsafe().setMemory(null, offset + 1, length - 1, value);
        } else {
            Reflections.getUnsafe().setMemory(null, offset, length, value);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int capacity() {
        return capacity;
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
        if (limit < 0) {
            throw new IndexOutOfBoundsException("limit cannot be negative: limit=" + limit);
        }

        ensureCapacity(limit, SIZE_OF_BYTE);
    }

    ///////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public long getLong(final int index, final ByteOrder byteOrder) {
        boundsCheck0(index, SIZE_OF_LONG);

        long bits = Reflections.getUnsafe().getLong(null, address + index);
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

        Reflections.getUnsafe().putLong(null, address + index, bits);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getLong(final int index) {
        boundsCheck0(index, SIZE_OF_LONG);

        return Reflections.getUnsafe().getLong(null, address + index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putLong(final int index, final long value) {
        ensureCapacity(index, SIZE_OF_LONG);

        Reflections.getUnsafe().putLong(null, address + index, value);
    }

    ///////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public int getInt(final int index, final ByteOrder byteOrder) {
        boundsCheck0(index, SIZE_OF_INT);

        int bits = Reflections.getUnsafe().getInt(null, address + index);
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

        Reflections.getUnsafe().putInt(null, address + index, bits);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getInt(final int index) {
        boundsCheck0(index, SIZE_OF_INT);

        return Reflections.getUnsafe().getInt(null, address + index);
    }

    ///////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public double getDouble(final int index, final ByteOrder byteOrder) {
        boundsCheck0(index, SIZE_OF_DOUBLE);

        if (NATIVE_BYTE_ORDER != byteOrder) {
            final long bits = Reflections.getUnsafe().getLong(null, address + index);
            return Double.longBitsToDouble(Long.reverseBytes(bits));
        } else {
            return Reflections.getUnsafe().getDouble(null, address + index);
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
            Reflections.getUnsafe().putLong(null, address + index, bits);
        } else {
            Reflections.getUnsafe().putDouble(null, address + index, value);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getDouble(final int index) {
        boundsCheck0(index, SIZE_OF_DOUBLE);

        return Reflections.getUnsafe().getDouble(null, address + index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putDouble(final int index, final double value) {
        ensureCapacity(index, SIZE_OF_DOUBLE);

        Reflections.getUnsafe().putDouble(null, address + index, value);
    }

    ///////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public float getFloat(final int index, final ByteOrder byteOrder) {
        boundsCheck0(index, SIZE_OF_FLOAT);

        if (NATIVE_BYTE_ORDER != byteOrder) {
            final int bits = Reflections.getUnsafe().getInt(null, address + index);
            return Float.intBitsToFloat(Integer.reverseBytes(bits));
        } else {
            return Reflections.getUnsafe().getFloat(null, address + index);
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
            Reflections.getUnsafe().putInt(null, address + index, bits);
        } else {
            Reflections.getUnsafe().putFloat(null, address + index, value);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getFloat(final int index) {
        boundsCheck0(index, SIZE_OF_FLOAT);

        return Reflections.getUnsafe().getFloat(null, address + index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putFloat(final int index, final float value) {
        ensureCapacity(index, SIZE_OF_FLOAT);

        Reflections.getUnsafe().putFloat(null, address + index, value);
    }

    ///////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public short getShort(final int index, final ByteOrder byteOrder) {
        boundsCheck0(index, SIZE_OF_SHORT);

        short bits = Reflections.getUnsafe().getShort(null, address + index);
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

        Reflections.getUnsafe().putShort(null, address + index, bits);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public short getShort(final int index) {
        boundsCheck0(index, SIZE_OF_SHORT);

        return Reflections.getUnsafe().getShort(null, address + index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putShort(final int index, final short value) {
        ensureCapacity(index, SIZE_OF_SHORT);

        Reflections.getUnsafe().putShort(null, address + index, value);
    }

    ///////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public byte getByte(final int index) {
        boundsCheck0(index, SIZE_OF_BYTE);
        return Reflections.getUnsafe().getByte(null, address + index);
    }

    private byte getByte0(final int index) {
        boundsCheck0(index, SIZE_OF_BYTE);
        return Reflections.getUnsafe().getByte(null, address + index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putByte(final int index, final byte value) {
        ensureCapacity(index, SIZE_OF_BYTE);
        Reflections.getUnsafe().putByte(null, address + index, value);
    }

    private void putByte0(final int index, final byte value) {
        ensureCapacity(index, SIZE_OF_BYTE);
        Reflections.getUnsafe().putByte(null, address + index, value);
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
        boundsCheck0(index, length);
        BufferUtil.boundsCheck(dst, offset, length);

        Reflections.getUnsafe().copyMemory(null, address + index, dst, ARRAY_BASE_OFFSET + offset, length);
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

        Reflections.getUnsafe().copyMemory(null, address + index, dstByteArray, dstBaseOffset + dstOffset, length);
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

        BufferUtil.boundsCheck(src, offset, length);

        Reflections.getUnsafe().copyMemory(src, ARRAY_BASE_OFFSET + offset, null, address + index, length);
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

        Reflections.getUnsafe().copyMemory(srcByteArray, srcBaseOffset + srcIndex, null, address + index, length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putBytes(final int index, final DirectBuffer srcBuffer, final int srcIndex, final int length) {
        ensureCapacity(index, length);
        srcBuffer.boundsCheck(srcIndex, length);

        Reflections.getUnsafe()
                .copyMemory(srcBuffer.byteArray(), srcBuffer.addressOffset() + srcIndex, null, address + index, length);
    }

    ///////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public char getChar(final int index, final ByteOrder byteOrder) {
        boundsCheck0(index, SIZE_OF_SHORT);

        char bits = Reflections.getUnsafe().getChar(null, address + index);
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

        Reflections.getUnsafe().putChar(null, address + index, bits);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public char getChar(final int index) {
        boundsCheck0(index, SIZE_OF_CHAR);

        return Reflections.getUnsafe().getChar(null, address + index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putChar(final int index, final char value) {
        ensureCapacity(index, SIZE_OF_CHAR);

        Reflections.getUnsafe().putChar(null, address + index, value);
    }

    ///////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStringAscii(final int index) {
        boundsCheck0(index, STR_HEADER_LEN);

        final int length = Reflections.getUnsafe().getInt(null, address + index);

        return getStringAscii(index, length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getStringAscii(final int index, final Appendable appendable) {
        boundsCheck0(index, STR_HEADER_LEN);

        final int length = Reflections.getUnsafe().getInt(null, address + index);

        return getStringAscii(index, length, appendable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStringAscii(final int index, final ByteOrder byteOrder) {
        boundsCheck0(index, STR_HEADER_LEN);

        int bits = Reflections.getUnsafe().getInt(null, address + index);
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

        int bits = Reflections.getUnsafe().getInt(null, address + index);
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
        boundsCheck0(index + STR_HEADER_LEN, length);

        final byte[] dst = new byte[length];
        Reflections.getUnsafe().copyMemory(null, address + index + STR_HEADER_LEN, dst, ARRAY_BASE_OFFSET, length);

        return new String(dst, US_ASCII);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getStringAscii(final int index, final int length, final Appendable appendable) {
        boundsCheck0(index, length + STR_HEADER_LEN);

        try {
            for (int i = index + STR_HEADER_LEN, limit = index + STR_HEADER_LEN + length; i < limit; i++) {
                final char c = (char) Reflections.getUnsafe().getByte(null, address + i);
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

        Reflections.getUnsafe().putInt(null, address + index, length);

        for (int i = 0; i < length; i++) {
            char c = value.charAt(i);
            if (c > 127) {
                c = '?';
            }

            Reflections.getUnsafe().putByte(null, address + STR_HEADER_LEN + index + i, (byte) c);
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

        Reflections.getUnsafe().putInt(null, address + index, length);

        for (int i = 0; i < length; i++) {
            char c = value.charAt(i);
            if (c > 127) {
                c = '?';
            }

            Reflections.getUnsafe().putByte(null, address + STR_HEADER_LEN + index + i, (byte) c);
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

        Reflections.getUnsafe().putInt(null, address + index, bits);

        for (int i = 0; i < length; i++) {
            char c = value.charAt(i);
            if (c > 127) {
                c = '?';
            }

            Reflections.getUnsafe().putByte(null, address + STR_HEADER_LEN + index + i, (byte) c);
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

        Reflections.getUnsafe().putInt(null, address + index, bits);

        for (int i = 0; i < length; i++) {
            char c = value.charAt(i);
            if (c > 127) {
                c = '?';
            }

            Reflections.getUnsafe().putByte(null, address + STR_HEADER_LEN + index + i, (byte) c);
        }

        return STR_HEADER_LEN + length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStringWithoutLengthAscii(final int index, final int length) {
        boundsCheck0(index, length);

        final byte[] dst = new byte[length];
        Reflections.getUnsafe().copyMemory(null, address + index, dst, ARRAY_BASE_OFFSET, length);

        return new String(dst, US_ASCII);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getStringWithoutLengthAscii(final int index, final int length, final Appendable appendable) {
        boundsCheck0(index, length);

        try {
            for (int i = index, limit = index + length; i < limit; i++) {
                final char c = (char) Reflections.getUnsafe().getByte(null, address + i);
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

            Reflections.getUnsafe().putByte(null, address + index + i, (byte) c);
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

            Reflections.getUnsafe().putByte(null, address + index + i, (byte) c);
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

            Reflections.getUnsafe().putByte(null, address + index + i, (byte) c);
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

            Reflections.getUnsafe().putByte(null, address + index + i, (byte) c);
        }

        return len;
    }

    ///////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStringUtf8(final int index) {
        boundsCheck0(index, STR_HEADER_LEN);

        final int length = Reflections.getUnsafe().getInt(null, address + index);

        return getStringUtf8(index, length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStringUtf8(final int index, final ByteOrder byteOrder) {
        boundsCheck0(index, STR_HEADER_LEN);

        int bits = Reflections.getUnsafe().getInt(null, address + index);
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
        boundsCheck0(index + STR_HEADER_LEN, length);

        final byte[] stringInBytes = new byte[length];
        Reflections.getUnsafe()
                .copyMemory(null, address + index + STR_HEADER_LEN, stringInBytes, ARRAY_BASE_OFFSET, length);

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

        Reflections.getUnsafe().putInt(null, address + index, bytes.length);
        Reflections.getUnsafe()
                .copyMemory(bytes, ARRAY_BASE_OFFSET, null, address + index + STR_HEADER_LEN, bytes.length);

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

        Reflections.getUnsafe().putInt(null, address + index, bits);
        Reflections.getUnsafe()
                .copyMemory(bytes, ARRAY_BASE_OFFSET, null, address + index + STR_HEADER_LEN, bytes.length);

        return STR_HEADER_LEN + bytes.length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStringWithoutLengthUtf8(final int index, final int length) {
        boundsCheck0(index, length);

        final byte[] stringInBytes = new byte[length];
        Reflections.getUnsafe().copyMemory(null, address + index, stringInBytes, ARRAY_BASE_OFFSET, length);

        return new String(stringInBytes, UTF_8);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int putStringWithoutLengthUtf8(final int index, final String value) {
        final byte[] bytes = value != null ? value.getBytes(UTF_8) : NULL_BYTES;
        ensureCapacity(index, bytes.length);

        Reflections.getUnsafe().copyMemory(bytes, ARRAY_BASE_OFFSET, null, address + index, bytes.length);

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

        if (length < INT_MAX_DIGITS) {
            return parsePositiveIntAscii(index, length, index, index + length);
        } else {
            final long tally = parsePositiveIntAsciiOverflowCheck(index, length, index, index + length);
            if (tally >= INTEGER_ABSOLUTE_MIN_VALUE) {
                throwParseIntOverflowError(index, length);
            }
            return (int) tally;
        }
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

        if (length < LONG_MAX_DIGITS) {
            return parsePositiveLongAscii(index, length, index, index + length);
        } else {
            return parseLongAsciiOverflowCheck(index, length, LONG_MAX_VALUE_DIGITS, index, index + length);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int parseIntAscii(final int index, final int length) {
        boundsCheck0(index, length);

        if (length <= 0) {
            throw new AsciiNumberFormatException("empty string: index=" + index + " length=" + length);
        }

        final boolean negative = MINUS_SIGN == Reflections.getUnsafe().getByte(null, address + index);
        int i = index;
        if (negative) {
            i++;
            if (1 == length) {
                throwParseIntError(index, length);
            }
        }

        final int end = index + length;
        if (end - i < INT_MAX_DIGITS) {
            final int tally = parsePositiveIntAscii(index, length, i, end);
            return negative ? -tally : tally;
        } else {
            final long tally = parsePositiveIntAsciiOverflowCheck(index, length, i, end);
            if (tally > INTEGER_ABSOLUTE_MIN_VALUE || INTEGER_ABSOLUTE_MIN_VALUE == tally && !negative) {
                throwParseIntOverflowError(index, length);
            }
            return (int) (negative ? -tally : tally);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long parseLongAscii(final int index, final int length) {
        boundsCheck0(index, length);

        if (length <= 0) {
            throw new AsciiNumberFormatException("empty string: index=" + index + " length=" + length);
        }

        final boolean negative = MINUS_SIGN == Reflections.getUnsafe().getByte(null, address + index);
        int i = index;
        if (negative) {
            i++;
            if (1 == length) {
                throwParseLongError(index, length);
            }
        }

        final int end = index + length;
        if (end - i < LONG_MAX_DIGITS) {
            final long tally = parsePositiveLongAscii(index, length, i, end);
            return negative ? -tally : tally;
        } else if (negative) {
            return -parseLongAsciiOverflowCheck(index, length, LONG_MIN_VALUE_DIGITS, i, end);
        } else {
            return parseLongAsciiOverflowCheck(index, length, LONG_MAX_VALUE_DIGITS, i, end);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putInt(final int index, final int value) {
        ensureCapacity(index, SIZE_OF_INT);

        Reflections.getUnsafe().putInt(null, address + index, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int putIntAscii(final int index, final int value) {
        if (0 == value) {
            putByte0(index, ZERO);
            return 1;
        }

        long offset;
        int quotient = value;
        final int digitCount, length;
        if (value < 0) {
            if (Integer.MIN_VALUE == value) {
                putBytes(index, MIN_INTEGER_VALUE);
                return MIN_INTEGER_VALUE.length;
            }

            quotient = -quotient;
            digitCount = digitCount(quotient);
            length = digitCount + 1;

            ensureCapacity(index, length);
            offset = address + index;

            Reflections.getUnsafe().putByte(null, offset, MINUS_SIGN);
            offset++;
        } else {
            digitCount = digitCount(quotient);
            length = digitCount;

            ensureCapacity(index, length);
            offset = address + index;
        }

        putPositiveIntAscii(offset, quotient, digitCount);

        return length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int putNaturalIntAscii(final int index, final int value) {
        if (0 == value) {
            putByte0(index, ZERO);
            return 1;
        }

        final int digitCount = digitCount(value);

        ensureCapacity(index, digitCount);

        putPositiveIntAscii(address + index, value, digitCount);

        return digitCount;
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
        if (0L == value) {
            putByte0(index, ZERO);
            return 1;
        }

        final int digitCount = digitCount(value);

        ensureCapacity(index, digitCount);

        putPositiveLongAscii(address + index, value, digitCount);

        return digitCount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int putLongAscii(final int index, final long value) {
        if (0L == value) {
            putByte0(index, ZERO);
            return 1;
        }

        long offset;
        long quotient = value;
        final int digitCount, length;
        if (value < 0) {
            if (Long.MIN_VALUE == value) {
                putBytes(index, MIN_LONG_VALUE);
                return MIN_LONG_VALUE.length;
            }

            quotient = -quotient;
            digitCount = digitCount(quotient);
            length = digitCount + 1;

            ensureCapacity(index, length);
            offset = address + index;

            Reflections.getUnsafe().putByte(null, offset, MINUS_SIGN);
            offset++;
        } else {
            digitCount = digitCount(quotient);
            length = digitCount;

            ensureCapacity(index, length);
            offset = address + index;
        }

        putPositiveLongAscii(offset, quotient, digitCount);

        return length;
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

        final ExpandableDirectByteBufferBase that = (ExpandableDirectByteBufferBase) obj;

        return compareTo(that) == 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int hashCode = 1;

        final long address = this.address;
        for (int i = 0, length = capacity; i < length; i++) {
            hashCode = 31 * hashCode + Reflections.getUnsafe().getByte(null, address + i);
        }

        return hashCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(final DirectBuffer that) {
        final int thisCapacity = this.capacity();
        final int thatCapacity = that.capacity();
        final byte[] thatByteArray = that.byteArray();
        final long thisOffset = this.addressOffset();
        final long thatOffset = that.addressOffset();

        for (int i = 0, length = Math.min(thisCapacity, thatCapacity); i < length; i++) {
            final int cmp = Byte.compare(Reflections.getUnsafe().getByte(null, thisOffset + i),
                    Reflections.getUnsafe().getByte(thatByteArray, thatOffset + i));

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
        return "ExpandableDirectByteBuffer{" + "address=" + address + ", capacity=" + capacity + ", byteBuffer="
                + byteBuffer + '}';
    }

    private void ensureCapacity(final int index, final int length) {
        if (index < 0 || length < 0) {
            throw new IndexOutOfBoundsException("negative value: index=" + index + " length=" + length);
        }

        final long resultingPosition = index + (long) length;
        final int currentCapacity = capacity;
        if (resultingPosition > currentCapacity) {
            if (resultingPosition > MAX_BUFFER_LENGTH) {
                throw new IndexOutOfBoundsException(
                        "index=" + index + " length=" + length + " maxCapacity=" + MAX_BUFFER_LENGTH);
            }

            final int newCapacity = calculateExpansion(currentCapacity, (int) resultingPosition);
            final java.nio.ByteBuffer newBuffer = java.nio.ByteBuffer.allocateDirect(newCapacity);

            getBytes(0, newBuffer, 0, capacity);

            address = address(newBuffer);
            capacity = newCapacity;
            byteBuffer = newBuffer;
        }
    }

    protected int calculateExpansion(final int currentLength, final int requiredLength) {
        final int value = ByteBuffers.calculateExpansion(requiredLength);
        if (value > MAX_BUFFER_LENGTH) {
            return MAX_BUFFER_LENGTH;
        } else {
            return value;
        }
    }

    private void boundsCheck0(final int index, final int length) {
        final int currentCapacity = capacity;
        final long resultingPosition = index + (long) length;
        if (index < 0 || length < 0 || resultingPosition > currentCapacity) {
            throw new IndexOutOfBoundsException(
                    "index=" + index + " length=" + length + " capacity=" + currentCapacity);
        }
    }

    //CHECKSTYLE:OFF
    private int parsePositiveIntAscii(final int index, final int length, final int startIndex, final int end) {
        final long offset = address;
        int i = startIndex;
        int tally = 0, quartet;
        while ((end - i) >= 4
                && isFourDigitsAsciiEncodedNumber(quartet = Reflections.getUnsafe().getInt(null, offset + i))) {
            if (NATIVE_BYTE_ORDER != LITTLE_ENDIAN) {
                quartet = Integer.reverseBytes(quartet);
            }

            tally = (tally * 10_000) + parseFourDigitsLittleEndian(quartet);
            i += 4;
        }

        byte digit;
        while (i < end && isDigit(digit = Reflections.getUnsafe().getByte(null, offset + i))) {
            tally = (tally * 10) + (digit - 0x30);
            i++;
        }

        if (i != end) {
            throwParseIntError(index, length);
        }

        return tally;
    }

    private long parsePositiveIntAsciiOverflowCheck(final int index, final int length, final int startIndex,
            final int end) {
        if ((end - startIndex) > INT_MAX_DIGITS) {
            throwParseIntOverflowError(index, length);
        }

        final long offset = address;
        int i = startIndex;
        long tally = 0;
        long octet = Reflections.getUnsafe().getLong(null, offset + i);
        if (isEightDigitAsciiEncodedNumber(octet)) {
            if (NATIVE_BYTE_ORDER != LITTLE_ENDIAN) {
                octet = Long.reverseBytes(octet);
            }
            tally = parseEightDigitsLittleEndian(octet);
            i += 8;

            byte digit;
            while (i < end && isDigit(digit = Reflections.getUnsafe().getByte(null, offset + i))) {
                tally = (tally * 10L) + (digit - 0x30);
                i++;
            }
        }

        if (i != end) {
            throwParseIntError(index, length);
        }

        return tally;
    }

    private void throwParseIntError(final int index, final int length) {
        throw new AsciiNumberFormatException("error parsing int: " + getStringWithoutLengthAscii(index, length));
    }

    private void throwParseIntOverflowError(final int index, final int length) {
        throw new AsciiNumberFormatException("int overflow parsing: " + getStringWithoutLengthAscii(index, length));
    }

    private long parsePositiveLongAscii(final int index, final int length, final int startIndex, final int end) {
        final long offset = address;
        int i = startIndex;
        long tally = 0, octet;
        while ((end - i) >= 8
                && isEightDigitAsciiEncodedNumber(octet = Reflections.getUnsafe().getLong(null, offset + i))) {
            if (NATIVE_BYTE_ORDER != LITTLE_ENDIAN) {
                octet = Long.reverseBytes(octet);
            }

            tally = (tally * 100_000_000L) + parseEightDigitsLittleEndian(octet);
            i += 8;
        }

        int quartet;
        while ((end - i) >= 4
                && isFourDigitsAsciiEncodedNumber(quartet = Reflections.getUnsafe().getInt(null, offset + i))) {
            if (NATIVE_BYTE_ORDER != LITTLE_ENDIAN) {
                quartet = Integer.reverseBytes(quartet);
            }

            tally = (tally * 10_000L) + parseFourDigitsLittleEndian(quartet);
            i += 4;
        }

        byte digit;
        while (i < end && isDigit(digit = Reflections.getUnsafe().getByte(null, offset + i))) {
            tally = (tally * 10) + (digit - 0x30);
            i++;
        }

        if (i != end) {
            throwParseLongError(index, length);
        }

        return tally;
    }

    private long parseLongAsciiOverflowCheck(final int index, final int length, final int[] maxValue,
            final int startIndex, final int end) {
        if ((end - startIndex) > LONG_MAX_DIGITS) {
            throwParseLongOverflowError(index, length);
        }

        final long offset = address;
        int i = startIndex, k = 0;
        boolean checkOverflow = true;
        long tally = 0, octet;
        while ((end - i) >= 8
                && isEightDigitAsciiEncodedNumber(octet = Reflections.getUnsafe().getLong(null, offset + i))) {
            if (NATIVE_BYTE_ORDER != LITTLE_ENDIAN) {
                octet = Long.reverseBytes(octet);
            }

            final int eightDigits = parseEightDigitsLittleEndian(octet);
            if (checkOverflow) {
                if (eightDigits > maxValue[k]) {
                    throwParseLongOverflowError(index, length);
                } else if (eightDigits < maxValue[k]) {
                    checkOverflow = false;
                }
                k++;
            }
            tally = (tally * 100_000_000L) + eightDigits;
            i += 8;
        }

        byte digit;
        int lastDigits = 0;
        while (i < end && isDigit(digit = Reflections.getUnsafe().getByte(null, offset + i))) {
            lastDigits = (lastDigits * 10) + (digit - 0x30);
            i++;
        }

        if (i != end) {
            throwParseLongError(index, length);
        } else if (checkOverflow && lastDigits > maxValue[k]) {
            throwParseLongOverflowError(index, length);
        }

        return (tally * 1000L) + lastDigits;
    }
    //CHECKSTYLE:ON

    private void throwParseLongError(final int index, final int length) {
        throw new AsciiNumberFormatException("error parsing long: " + getStringWithoutLengthAscii(index, length));
    }

    private void throwParseLongOverflowError(final int index, final int length) {
        throw new AsciiNumberFormatException("long overflow parsing: " + getStringWithoutLengthAscii(index, length));
    }

    private static void putPositiveIntAscii(final long offset, final int value, final int digitCount) {
        int quotient = value;
        int i = digitCount;
        while (quotient >= 10_000) {
            final int lastFourDigits = quotient % 10_000;
            quotient /= 10_000;

            final int p1 = (lastFourDigits / 100) << 1;
            final int p2 = (lastFourDigits % 100) << 1;

            i -= 4;

            Reflections.getUnsafe().putByte(null, offset + i, ASCII_DIGITS[p1]);
            Reflections.getUnsafe().putByte(null, offset + i + 1, ASCII_DIGITS[p1 + 1]);
            Reflections.getUnsafe().putByte(null, offset + i + 2, ASCII_DIGITS[p2]);
            Reflections.getUnsafe().putByte(null, offset + i + 3, ASCII_DIGITS[p2 + 1]);
        }

        if (quotient >= 100) {
            final int position = (quotient % 100) << 1;
            quotient /= 100;
            Reflections.getUnsafe().putByte(null, offset + i - 1, ASCII_DIGITS[position + 1]);
            Reflections.getUnsafe().putByte(null, offset + i - 2, ASCII_DIGITS[position]);
        }

        if (quotient >= 10) {
            final int position = quotient << 1;
            Reflections.getUnsafe().putByte(null, offset + 1, ASCII_DIGITS[position + 1]);
            Reflections.getUnsafe().putByte(null, offset, ASCII_DIGITS[position]);
        } else {
            Reflections.getUnsafe().putByte(null, offset, (byte) (ZERO + quotient));
        }
    }

    private static void putPositiveLongAscii(final long offset, final long value, final int digitCount) {
        long quotient = value;
        int i = digitCount;
        while (quotient >= 100_000_000) {
            final int lastEightDigits = (int) (quotient % 100_000_000);
            quotient /= 100_000_000;

            final int upperPart = lastEightDigits / 10_000;
            final int lowerPart = lastEightDigits % 10_000;

            final int u1 = (upperPart / 100) << 1;
            final int u2 = (upperPart % 100) << 1;
            final int l1 = (lowerPart / 100) << 1;
            final int l2 = (lowerPart % 100) << 1;

            i -= 8;

            Reflections.getUnsafe().putByte(null, offset + i, ASCII_DIGITS[u1]);
            Reflections.getUnsafe().putByte(null, offset + i + 1, ASCII_DIGITS[u1 + 1]);
            Reflections.getUnsafe().putByte(null, offset + i + 2, ASCII_DIGITS[u2]);
            Reflections.getUnsafe().putByte(null, offset + i + 3, ASCII_DIGITS[u2 + 1]);
            Reflections.getUnsafe().putByte(null, offset + i + 4, ASCII_DIGITS[l1]);
            Reflections.getUnsafe().putByte(null, offset + i + 5, ASCII_DIGITS[l1 + 1]);
            Reflections.getUnsafe().putByte(null, offset + i + 6, ASCII_DIGITS[l2]);
            Reflections.getUnsafe().putByte(null, offset + i + 7, ASCII_DIGITS[l2 + 1]);
        }

        putPositiveIntAscii(offset, (int) quotient, i);
    }
}
