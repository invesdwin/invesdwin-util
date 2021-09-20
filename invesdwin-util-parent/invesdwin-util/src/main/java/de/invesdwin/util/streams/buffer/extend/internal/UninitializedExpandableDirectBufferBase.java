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

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteOrder;

import javax.annotation.concurrent.NotThreadSafe;

import org.agrona.AsciiEncoding;
import org.agrona.AsciiNumberFormatException;
import org.agrona.BufferUtil;
import org.agrona.DirectBuffer;
import org.agrona.LangUtil;
import org.agrona.MutableDirectBuffer;

import de.invesdwin.util.lang.finalizer.AFinalizer;
import de.invesdwin.util.streams.buffer.UninitializedDirectByteBuffers;

/**
 * Extracted from org.agrona.ExpandableDirectByteBuffer
 */
@NotThreadSafe
@SuppressWarnings("restriction")
public class UninitializedExpandableDirectBufferBase implements MutableDirectBuffer, Closeable {
    /**
     * Maximum length to which the underlying buffer can grow.
     */
    public static final int MAX_BUFFER_LENGTH = 1024 * 1024 * 1024;

    /**
     * Initial capacity of the buffer from which it will expand.
     */
    public static final int INITIAL_CAPACITY = 128;

    private static final class UninitializedExpandableDirectBufferFinalizer extends AFinalizer {

        private long address;
        private int capacity;
        private java.nio.ByteBuffer byteBuffer;

        private UninitializedExpandableDirectBufferFinalizer(final int initialCapacity) {
            byteBuffer = UninitializedDirectByteBuffers.allocateDirectByteBufferNoCleaner(initialCapacity);
            capacity = initialCapacity;
            address = address(byteBuffer);
        }

        @Override
        protected void clean() {
            UninitializedDirectByteBuffers.freeDirectByteBufferNoCleaner(byteBuffer);
            byteBuffer = null;
        }

        @Override
        protected boolean isCleaned() {
            return byteBuffer == null;
        }

        @Override
        public boolean isThreadLocal() {
            return false;
        }

    }

    private final UninitializedExpandableDirectBufferFinalizer finalizer;

    /**
     * Create an {@link UninitializedExpandableDirectBufferBase} with an initial length of {@link #INITIAL_CAPACITY}.
     */
    public UninitializedExpandableDirectBufferBase() {
        this(INITIAL_CAPACITY);
    }

    /**
     * Create an {@link UninitializedExpandableDirectBufferBase} with a provided initial capacity.
     *
     * @param initialCapacity
     *            of the backing array.
     */
    public UninitializedExpandableDirectBufferBase(final int initialCapacity) {
        this.finalizer = new UninitializedExpandableDirectBufferFinalizer(initialCapacity);
        this.finalizer.register(this);
    }

    @Override
    public void close() throws IOException {
        finalizer.close();
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
        return finalizer.address;
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
        return finalizer.byteBuffer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMemory(final int index, final int length, final byte value) {
        lengthCheck(length);
        ensureCapacity(index, length);

        final long indexOffset = finalizer.address + index;
        if (0 == (indexOffset & 1) && length > 64) {
            // This horrible filth is to encourage the JVM to call memset() when address is even.
            // TODO: check if this still applies when Java 9 is out!!!
            UNSAFE.putByte(null, indexOffset, value);
            UNSAFE.setMemory(null, indexOffset + 1, length - 1, value);
        } else {
            UNSAFE.setMemory(null, indexOffset, length, value);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int capacity() {
        return finalizer.capacity;
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

        long bits = UNSAFE.getLong(null, finalizer.address + index);
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

        UNSAFE.putLong(null, finalizer.address + index, bits);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getLong(final int index) {
        boundsCheck0(index, SIZE_OF_LONG);

        return UNSAFE.getLong(null, finalizer.address + index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putLong(final int index, final long value) {
        ensureCapacity(index, SIZE_OF_LONG);

        UNSAFE.putLong(null, finalizer.address + index, value);
    }

    ///////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public int getInt(final int index, final ByteOrder byteOrder) {
        boundsCheck0(index, SIZE_OF_INT);

        int bits = UNSAFE.getInt(null, finalizer.address + index);
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

        UNSAFE.putInt(null, finalizer.address + index, bits);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getInt(final int index) {
        boundsCheck0(index, SIZE_OF_INT);

        return UNSAFE.getInt(null, finalizer.address + index);
    }

    ///////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public double getDouble(final int index, final ByteOrder byteOrder) {
        boundsCheck0(index, SIZE_OF_DOUBLE);

        if (NATIVE_BYTE_ORDER != byteOrder) {
            final long bits = UNSAFE.getLong(null, finalizer.address + index);
            return Double.longBitsToDouble(Long.reverseBytes(bits));
        } else {
            return UNSAFE.getDouble(null, finalizer.address + index);
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
            UNSAFE.putLong(null, finalizer.address + index, bits);
        } else {
            UNSAFE.putDouble(null, finalizer.address + index, value);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getDouble(final int index) {
        boundsCheck0(index, SIZE_OF_DOUBLE);

        return UNSAFE.getDouble(null, finalizer.address + index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putDouble(final int index, final double value) {
        ensureCapacity(index, SIZE_OF_DOUBLE);

        UNSAFE.putDouble(null, finalizer.address + index, value);
    }

    ///////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public float getFloat(final int index, final ByteOrder byteOrder) {
        boundsCheck0(index, SIZE_OF_FLOAT);

        if (NATIVE_BYTE_ORDER != byteOrder) {
            final int bits = UNSAFE.getInt(null, finalizer.address + index);
            return Float.intBitsToFloat(Integer.reverseBytes(bits));
        } else {
            return UNSAFE.getFloat(null, finalizer.address + index);
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
            UNSAFE.putInt(null, finalizer.address + index, bits);
        } else {
            UNSAFE.putFloat(null, finalizer.address + index, value);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getFloat(final int index) {
        boundsCheck0(index, SIZE_OF_FLOAT);

        return UNSAFE.getFloat(null, finalizer.address + index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putFloat(final int index, final float value) {
        ensureCapacity(index, SIZE_OF_FLOAT);

        UNSAFE.putFloat(null, finalizer.address + index, value);
    }

    ///////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public short getShort(final int index, final ByteOrder byteOrder) {
        boundsCheck0(index, SIZE_OF_SHORT);

        short bits = UNSAFE.getShort(null, finalizer.address + index);
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

        UNSAFE.putShort(null, finalizer.address + index, bits);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public short getShort(final int index) {
        boundsCheck0(index, SIZE_OF_SHORT);

        return UNSAFE.getShort(null, finalizer.address + index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putShort(final int index, final short value) {
        ensureCapacity(index, SIZE_OF_SHORT);

        UNSAFE.putShort(null, finalizer.address + index, value);
    }

    ///////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public byte getByte(final int index) {
        boundsCheck0(index, SIZE_OF_BYTE);
        return UNSAFE.getByte(null, finalizer.address + index);
    }

    private byte getByte0(final int index) {
        boundsCheck0(index, SIZE_OF_BYTE);
        return UNSAFE.getByte(null, finalizer.address + index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putByte(final int index, final byte value) {
        ensureCapacity(index, SIZE_OF_BYTE);
        UNSAFE.putByte(null, finalizer.address + index, value);
    }

    private void putByte0(final int index, final byte value) {
        ensureCapacity(index, SIZE_OF_BYTE);
        UNSAFE.putByte(null, finalizer.address + index, value);
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
        lengthCheck(length);
        boundsCheck0(index, length);
        BufferUtil.boundsCheck(dst, offset, length);

        UNSAFE.copyMemory(null, finalizer.address + index, dst, ARRAY_BASE_OFFSET + offset, length);
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

        UNSAFE.copyMemory(null, finalizer.address + index, dstByteArray, dstBaseOffset + dstOffset, length);
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

        lengthCheck(length);
        BufferUtil.boundsCheck(src, offset, length);

        UNSAFE.copyMemory(src, ARRAY_BASE_OFFSET + offset, null, finalizer.address + index, length);
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

        UNSAFE.copyMemory(srcByteArray, srcBaseOffset + srcIndex, null, finalizer.address + index, length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putBytes(final int index, final DirectBuffer srcBuffer, final int srcIndex, final int length) {
        ensureCapacity(index, length);
        srcBuffer.boundsCheck(srcIndex, length);

        UNSAFE.copyMemory(srcBuffer.byteArray(), srcBuffer.addressOffset() + srcIndex, null, finalizer.address + index,
                length);
    }

    ///////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public char getChar(final int index, final ByteOrder byteOrder) {
        boundsCheck0(index, SIZE_OF_SHORT);

        char bits = UNSAFE.getChar(null, finalizer.address + index);
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

        UNSAFE.putChar(null, finalizer.address + index, bits);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public char getChar(final int index) {
        boundsCheck0(index, SIZE_OF_CHAR);

        return UNSAFE.getChar(null, finalizer.address + index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putChar(final int index, final char value) {
        ensureCapacity(index, SIZE_OF_CHAR);

        UNSAFE.putChar(null, finalizer.address + index, value);
    }

    ///////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStringAscii(final int index) {
        boundsCheck0(index, STR_HEADER_LEN);

        final int length = UNSAFE.getInt(null, finalizer.address + index);

        return getStringAscii(index, length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getStringAscii(final int index, final Appendable appendable) {
        boundsCheck0(index, STR_HEADER_LEN);

        final int length = UNSAFE.getInt(null, finalizer.address + index);

        return getStringAscii(index, length, appendable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStringAscii(final int index, final ByteOrder byteOrder) {
        boundsCheck0(index, STR_HEADER_LEN);

        int bits = UNSAFE.getInt(null, finalizer.address + index);
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

        int bits = UNSAFE.getInt(null, finalizer.address + index);
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
        UNSAFE.copyMemory(null, finalizer.address + index + STR_HEADER_LEN, dst, ARRAY_BASE_OFFSET, length);

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
                final char c = (char) UNSAFE.getByte(null, finalizer.address + i);
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

        UNSAFE.putInt(null, finalizer.address + index, length);

        for (int i = 0; i < length; i++) {
            char c = value.charAt(i);
            if (c > 127) {
                c = '?';
            }

            UNSAFE.putByte(null, finalizer.address + STR_HEADER_LEN + index + i, (byte) c);
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

        UNSAFE.putInt(null, finalizer.address + index, length);

        for (int i = 0; i < length; i++) {
            char c = value.charAt(i);
            if (c > 127) {
                c = '?';
            }

            UNSAFE.putByte(null, finalizer.address + STR_HEADER_LEN + index + i, (byte) c);
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

        UNSAFE.putInt(null, finalizer.address + index, bits);

        for (int i = 0; i < length; i++) {
            char c = value.charAt(i);
            if (c > 127) {
                c = '?';
            }

            UNSAFE.putByte(null, finalizer.address + STR_HEADER_LEN + index + i, (byte) c);
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

        UNSAFE.putInt(null, finalizer.address + index, bits);

        for (int i = 0; i < length; i++) {
            char c = value.charAt(i);
            if (c > 127) {
                c = '?';
            }

            UNSAFE.putByte(null, finalizer.address + STR_HEADER_LEN + index + i, (byte) c);
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
        UNSAFE.copyMemory(null, finalizer.address + index, dst, ARRAY_BASE_OFFSET, length);

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
                final char c = (char) UNSAFE.getByte(null, finalizer.address + i);
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

            UNSAFE.putByte(null, finalizer.address + index + i, (byte) c);
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

            UNSAFE.putByte(null, finalizer.address + index + i, (byte) c);
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

            UNSAFE.putByte(null, finalizer.address + index + i, (byte) c);
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

            UNSAFE.putByte(null, finalizer.address + index + i, (byte) c);
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

        final int length = UNSAFE.getInt(null, finalizer.address + index);

        return getStringUtf8(index, length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStringUtf8(final int index, final ByteOrder byteOrder) {
        boundsCheck0(index, STR_HEADER_LEN);

        int bits = UNSAFE.getInt(null, finalizer.address + index);
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
        UNSAFE.copyMemory(null, finalizer.address + index + STR_HEADER_LEN, stringInBytes, ARRAY_BASE_OFFSET, length);

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

        UNSAFE.putInt(null, finalizer.address + index, bytes.length);
        UNSAFE.copyMemory(bytes, ARRAY_BASE_OFFSET, null, finalizer.address + index + STR_HEADER_LEN, bytes.length);

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

        UNSAFE.putInt(null, finalizer.address + index, bits);
        UNSAFE.copyMemory(bytes, ARRAY_BASE_OFFSET, null, finalizer.address + index + STR_HEADER_LEN, bytes.length);

        return STR_HEADER_LEN + bytes.length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStringWithoutLengthUtf8(final int index, final int length) {
        boundsCheck0(index, length);

        final byte[] stringInBytes = new byte[length];
        UNSAFE.copyMemory(null, finalizer.address + index, stringInBytes, ARRAY_BASE_OFFSET, length);

        return new String(stringInBytes, UTF_8);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int putStringWithoutLengthUtf8(final int index, final String value) {
        final byte[] bytes = value != null ? value.getBytes(UTF_8) : NULL_BYTES;
        ensureCapacity(index, bytes.length);

        UNSAFE.copyMemory(bytes, ARRAY_BASE_OFFSET, null, finalizer.address + index, bytes.length);

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
            tally = (tally * 10) + AsciiEncoding.getDigit(i, UNSAFE.getByte(null, finalizer.address + i));
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
        long tally = 0L;
        for (int i = index; i < end; i++) {
            tally = (tally * 10) + AsciiEncoding.getDigit(i, UNSAFE.getByte(null, finalizer.address + i));
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
            return AsciiEncoding.getDigit(index, UNSAFE.getByte(null, finalizer.address + index));
        }

        final int endExclusive = index + length;
        final int first = getByte0(index);
        int i = index;
        if (first == MINUS_SIGN) {
            i++;
        }

        int tally = 0;
        //CHECKSTYLE:OFF
        for (; i < endExclusive; i++) {
            //CHECKSTYLE:ON
            tally = (tally * 10) + AsciiEncoding.getDigit(i, UNSAFE.getByte(null, finalizer.address + i));
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
            return AsciiEncoding.getDigit(index, UNSAFE.getByte(null, finalizer.address + index));
        }

        final int endExclusive = index + length;
        final int first = getByte0(index);
        int i = index;
        if (first == MINUS_SIGN) {
            i++;
        }

        long tally = 0;
        //CHECKSTYLE:OFF
        for (; i < endExclusive; i++) {
            //CHECKSTYLE:ON
            tally = (tally * 10) + AsciiEncoding.getDigit(i, UNSAFE.getByte(null, finalizer.address + i));
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
    public void putInt(final int index, final int value) {
        ensureCapacity(index, SIZE_OF_INT);

        UNSAFE.putInt(null, finalizer.address + index, value);
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

        final java.nio.ByteBuffer dest = finalizer.byteBuffer;
        while (quotient >= 100) {
            final int position = (quotient % 100) << 1;
            quotient /= 100;
            dest.put(i + start, ASCII_DIGITS[position + 1]);
            dest.put(i - 1 + start, ASCII_DIGITS[position]);
            i -= 2;
        }

        if (quotient < 10) {
            dest.put(i + start, (byte) (ZERO + quotient));
        } else {
            final int position = quotient << 1;
            dest.put(i + start, ASCII_DIGITS[position + 1]);
            dest.put(i - 1 + start, ASCII_DIGITS[position]);
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
        final java.nio.ByteBuffer dest = finalizer.byteBuffer;
        while (quotient >= 100) {
            final int position = (quotient % 100) << 1;
            quotient /= 100;
            dest.put(i + index, ASCII_DIGITS[position + 1]);
            dest.put(i - 1 + index, ASCII_DIGITS[position]);
            i -= 2;
        }

        if (quotient < 10) {
            dest.put(i + index, (byte) (ZERO + quotient));
        } else {
            final int position = quotient << 1;
            dest.put(i + index, ASCII_DIGITS[position + 1]);
            dest.put(i - 1 + index, ASCII_DIGITS[position]);
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
        if (value == 0L) {
            putByte0(index, ZERO);
            return 1;
        }

        int i = endOffset(value);
        final int length = i + 1;

        ensureCapacity(index, length);

        long quotient = value;
        final java.nio.ByteBuffer dest = finalizer.byteBuffer;
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

            dest.put(index + i + 1, ASCII_DIGITS[u1]);
            dest.put(index + i + 2, ASCII_DIGITS[u1 + 1]);
            dest.put(index + i + 3, ASCII_DIGITS[u2]);
            dest.put(index + i + 4, ASCII_DIGITS[u2 + 1]);
            dest.put(index + i + 5, ASCII_DIGITS[l1]);
            dest.put(index + i + 6, ASCII_DIGITS[l1 + 1]);
            dest.put(index + i + 7, ASCII_DIGITS[l2]);
            dest.put(index + i + 8, ASCII_DIGITS[l2 + 1]);
        }

        while (quotient >= 100) {
            final int position = (int) ((quotient % 100) << 1);
            quotient /= 100;
            dest.put(index + i, ASCII_DIGITS[position + 1]);
            dest.put(index + i - 1, ASCII_DIGITS[position]);
            i -= 2;
        }

        if (quotient < 10) {
            dest.put(index + i, (byte) (ZERO + quotient));
        } else {
            final int position = (int) (quotient << 1);
            dest.put(index + i, ASCII_DIGITS[position + 1]);
            dest.put(index + i - 1, ASCII_DIGITS[position]);
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

        final java.nio.ByteBuffer dest = finalizer.byteBuffer;
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

            dest.put(start + i + 1, ASCII_DIGITS[u1]);
            dest.put(start + i + 2, ASCII_DIGITS[u1 + 1]);
            dest.put(start + i + 3, ASCII_DIGITS[u2]);
            dest.put(start + i + 4, ASCII_DIGITS[u2 + 1]);
            dest.put(start + i + 5, ASCII_DIGITS[l1]);
            dest.put(start + i + 6, ASCII_DIGITS[l1 + 1]);
            dest.put(start + i + 7, ASCII_DIGITS[l2]);
            dest.put(start + i + 8, ASCII_DIGITS[l2 + 1]);
        }

        while (quotient >= 100) {
            final int position = (int) ((quotient % 100) << 1);
            quotient /= 100;
            dest.put(start + i, ASCII_DIGITS[position + 1]);
            dest.put(start + i - 1, ASCII_DIGITS[position]);
            i -= 2;
        }

        if (quotient < 10) {
            dest.put(start + i, (byte) (ZERO + quotient));
        } else {
            final int position = (int) (quotient << 1);
            dest.put(start + i, ASCII_DIGITS[position + 1]);
            dest.put(start + i - 1, ASCII_DIGITS[position]);
        }

        return length;
    }

    ///////////////////////////////////////////////////////////////////////////

    private void ensureCapacity(final int index, final int length) {
        if (index < 0 || length < 0) {
            throw new IndexOutOfBoundsException("negative value: index=" + index + " length=" + length);
        }

        final long resultingPosition = index + (long) length;
        final int currentCapacity = finalizer.capacity;
        if (resultingPosition > currentCapacity) {
            if (resultingPosition > MAX_BUFFER_LENGTH) {
                throw new IndexOutOfBoundsException(
                        "index=" + index + " length=" + length + " maxCapacity=" + MAX_BUFFER_LENGTH);
            }

            final int newCapacity = calculateExpansion(currentCapacity, resultingPosition);
            final java.nio.ByteBuffer newBuffer = UninitializedDirectByteBuffers
                    .reallocateDirectByteBufferNoCleaner(finalizer.byteBuffer, newCapacity);

            //copy not needed
            //            getBytes(0, newBuffer, 0, finalizer.capacity);

            finalizer.address = address(newBuffer);
            finalizer.capacity = newCapacity;
            finalizer.byteBuffer = newBuffer;
        }
    }

    private int calculateExpansion(final int currentLength, final long requiredLength) {
        long value = Math.max(currentLength, INITIAL_CAPACITY);

        while (value < requiredLength) {
            value = value + (value >> 1);

            if (value > MAX_BUFFER_LENGTH) {
                value = MAX_BUFFER_LENGTH;
            }
        }

        return (int) value;
    }

    private void boundsCheck0(final int index, final int length) {
        final int currentCapacity = finalizer.capacity;
        final long resultingPosition = index + (long) length;
        if (index < 0 || length < 0 || resultingPosition > currentCapacity) {
            throw new IndexOutOfBoundsException(
                    "index=" + index + " length=" + length + " capacity=" + currentCapacity);
        }
    }

    private void lengthCheck(final int length) {
        if (length < 0) {
            throw new IllegalArgumentException("negative length: " + length);
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

        final UninitializedExpandableDirectBufferBase that = (UninitializedExpandableDirectBufferBase) obj;

        return compareTo(that) == 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int hashCode = 1;

        final long address = finalizer.address;
        for (int i = 0, length = finalizer.capacity; i < length; i++) {
            hashCode = 31 * hashCode + UNSAFE.getByte(null, address + i);
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
            final int cmp = Byte.compare(UNSAFE.getByte(null, thisOffset + i),
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
        return "UninitializedExpandableDirectBuffer{" + "address=" + finalizer.address + ", capacity="
                + finalizer.capacity + ", byteBuffer=" + finalizer.byteBuffer + '}';
    }
}
