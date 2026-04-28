package de.invesdwin.util.streams.buffer.memory.extend.internal;

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

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteOrder;

import javax.annotation.concurrent.NotThreadSafe;

import org.agrona.AsciiNumberFormatException;
import org.agrona.BufferUtil;
import org.agrona.DirectBuffer;
import org.agrona.IoUtil;
import org.agrona.LangUtil;
import org.agrona.MutableDirectBuffer;
import org.agrona.UnsafeApi;

import de.invesdwin.util.error.FastIndexOutOfBoundsException;
import de.invesdwin.util.lang.Files;
import de.invesdwin.util.lang.finalizer.AFinalizer;
import de.invesdwin.util.lang.string.Strings;
import de.invesdwin.util.lang.string.UniqueNameGenerator;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.Longs;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.file.IMemoryMappedFile;
import de.invesdwin.util.streams.buffer.file.MemoryMappedFile;
import de.invesdwin.util.streams.buffer.memory.MemoryBuffers;

/**
 * Extracted from org.agrona.ExpandableDirectByteBuffer
 */
@NotThreadSafe
public class MappedExpandableMemoryBufferBase implements Closeable {
    /**
     * Maximum length to which the underlying buffer can grow.
     */
    public static final long MAX_BUFFER_LENGTH = IMemoryMappedFile.MAX_SEGMENT_SIZE;

    /**
     * Initial capacity of the buffer from which it will expand.
     */
    public static final int INITIAL_CAPACITY = IoUtil.BLOCK_SIZE;

    /**
     * Should bounds-checks operations be done or not. Controlled by the {@link #DISABLE_BOUNDS_CHECKS_PROP_NAME} system
     * property.
     *
     * @see #DISABLE_BOUNDS_CHECKS_PROP_NAME
     */
    public static final boolean SHOULD_BOUNDS_CHECK = DirectBuffer.SHOULD_BOUNDS_CHECK;

    private static final UniqueNameGenerator UNIQUE_NAME_GENERATOR = new UniqueNameGenerator() {

        @Override
        protected long getInitialValue() {
            return 1;
        }
    };

    private static final class MappedExpandableMemoryBufferFinalizer extends AFinalizer {

        private final File file;
        private final boolean deleteOnClose;
        private MemoryMappedFile mappedFile;
        private long address;
        private long capacity;

        private MappedExpandableMemoryBufferFinalizer(final File file, final int initialCapacity,
                final boolean deleteOnClose) {
            this.file = file;
            this.deleteOnClose = deleteOnClose;
            try {
                Files.forceMkdirParent(file);
                mappedFile = new MemoryMappedFile(true, file, 0, IMemoryMappedFile.roundToBlockSize(initialCapacity),
                        false, deleteOnClose);
            } catch (final IOException e) {
                throw new UncheckedIOException(e);
            }
            address = mappedFile.addressOffset();
            capacity = mappedFile.capacity();
        }

        @Override
        protected void clean() {
            final IMemoryMappedFile mappedFileCopy = mappedFile;
            if (mappedFileCopy != null) {
                mappedFileCopy.close();
                mappedFile = null;
            }
        }

        @Override
        protected boolean isCleaned() {
            return mappedFile == null;
        }

        @Override
        public boolean isThreadLocal() {
            return false;
        }

    }

    private final MappedExpandableMemoryBufferFinalizer finalizer;

    public MappedExpandableMemoryBufferBase() {
        this(INITIAL_CAPACITY);
    }

    public MappedExpandableMemoryBufferBase(final int initialCapacity) {
        this(initialCapacity, MappedExpandableMemoryBufferBase.class.getSimpleName());
    }

    public MappedExpandableMemoryBufferBase(final int initialCapacity, final String name) {
        this(initialCapacity,
                new File(new File(Files.getTempDirectory(), MappedExpandableMemoryBufferBase.class.getSimpleName()),
                        Files.normalizeFilename(UNIQUE_NAME_GENERATOR.get(Strings.putSuffix(name, ".bin")))));
    }

    public MappedExpandableMemoryBufferBase(final int initialCapacity, final File file) {
        this(initialCapacity, file, true);
    }

    public MappedExpandableMemoryBufferBase(final int initialCapacity, final File file, final boolean deleteOnClose) {
        this.finalizer = new MappedExpandableMemoryBufferFinalizer(file, initialCapacity, deleteOnClose);
        this.finalizer.register(this);
    }

    @Override
    public void close() {
        finalizer.close();
    }

    public void wrap(final byte[] buffer) {
        throw new UnsupportedOperationException();
    }

    public void wrap(final byte[] buffer, final int offset, final int length) {
        throw new UnsupportedOperationException();
    }

    public void wrap(final java.nio.ByteBuffer buffer) {
        throw new UnsupportedOperationException();
    }

    public void wrap(final java.nio.ByteBuffer buffer, final int offset, final int length) {
        throw new UnsupportedOperationException();
    }

    public void wrap(final DirectBuffer buffer) {
        throw new UnsupportedOperationException();
    }

    public void wrap(final DirectBuffer buffer, final int offset, final int length) {
        throw new UnsupportedOperationException();
    }

    public void wrap(final long address, final int length) {
        throw new UnsupportedOperationException();
    }

    public long addressOffset() {
        return finalizer.address;
    }

    public byte[] byteArray() {
        return null;
    }

    public java.nio.ByteBuffer byteBuffer() {
        return finalizer.mappedFile.getMappedByteBuffer();
    }

    public void setMemory(final long index, final long length, final byte value) {
        ensureCapacity(index, length);

        final long offset = finalizer.address + index;

        if (length < 100) {
            int i = 0;
            final long end = (length & ~7);
            //CHECKSTYLE:OFF
            final long mask = ((((long) value) << 56) | (((long) value & 0xff) << 48) | (((long) value & 0xff) << 40)
                    | (((long) value & 0xff) << 32) | (((long) value & 0xff) << 24) | (((long) value & 0xff) << 16)
                    | (((long) value & 0xff) << 8) | (((long) value & 0xff)));

            for (; i < end; i += 8) {
                UnsafeApi.putLong(null, offset + i, mask);
            }

            for (; i < length; i++) {
                UnsafeApi.putByte(null, offset + i, value);
            }
            //CHECKSTYLE:ON
        } else {
            UnsafeApi.setMemory(null, offset, length, value);
        }
    }

    public long capacity() {
        return finalizer.capacity;
    }

    public boolean isExpandable() {
        return true;
    }

    public void checkLimit(final long limit) {
        if (limit < 0) {
            throw FastIndexOutOfBoundsException.getInstance("limit cannot be negative: limit=%s", limit);
        }

        ensureCapacity(limit, SIZE_OF_BYTE);
    }

    //////

    public long getLong(final long index, final ByteOrder byteOrder) {
        boundsCheck0(index, SIZE_OF_LONG);

        long bits = UnsafeApi.getLong(null, finalizer.address + index);
        if (NATIVE_BYTE_ORDER != byteOrder) {
            bits = Long.reverseBytes(bits);
        }

        return bits;
    }

    public void putLong(final long index, final long value, final ByteOrder byteOrder) {
        ensureCapacity(index, SIZE_OF_LONG);

        long bits = value;
        if (NATIVE_BYTE_ORDER != byteOrder) {
            bits = Long.reverseBytes(bits);
        }

        UnsafeApi.putLong(null, finalizer.address + index, bits);
    }

    public long getLong(final long index) {
        boundsCheck0(index, SIZE_OF_LONG);

        return UnsafeApi.getLong(null, finalizer.address + index);
    }

    public void putLong(final long index, final long value) {
        ensureCapacity(index, SIZE_OF_LONG);

        UnsafeApi.putLong(null, finalizer.address + index, value);
    }

    //////

    public int getInt(final long index, final ByteOrder byteOrder) {
        boundsCheck0(index, SIZE_OF_INT);

        int bits = UnsafeApi.getInt(null, finalizer.address + index);
        if (NATIVE_BYTE_ORDER != byteOrder) {
            bits = Integer.reverseBytes(bits);
        }

        return bits;
    }

    public void putInt(final long index, final int value, final ByteOrder byteOrder) {
        ensureCapacity(index, SIZE_OF_INT);

        int bits = value;
        if (NATIVE_BYTE_ORDER != byteOrder) {
            bits = Integer.reverseBytes(bits);
        }

        UnsafeApi.putInt(null, finalizer.address + index, bits);
    }

    public int getInt(final long index) {
        boundsCheck0(index, SIZE_OF_INT);

        return UnsafeApi.getInt(null, finalizer.address + index);
    }

    //////

    public double getDouble(final long index, final ByteOrder byteOrder) {
        boundsCheck0(index, SIZE_OF_DOUBLE);

        if (NATIVE_BYTE_ORDER != byteOrder) {
            final long bits = UnsafeApi.getLong(null, finalizer.address + index);
            return Double.longBitsToDouble(Long.reverseBytes(bits));
        } else {
            return UnsafeApi.getDouble(null, finalizer.address + index);
        }
    }

    public void putDouble(final long index, final double value, final ByteOrder byteOrder) {
        ensureCapacity(index, SIZE_OF_DOUBLE);

        if (NATIVE_BYTE_ORDER != byteOrder) {
            final long bits = Long.reverseBytes(Double.doubleToRawLongBits(value));
            UnsafeApi.putLong(null, finalizer.address + index, bits);
        } else {
            UnsafeApi.putDouble(null, finalizer.address + index, value);
        }
    }

    public double getDouble(final long index) {
        boundsCheck0(index, SIZE_OF_DOUBLE);

        return UnsafeApi.getDouble(null, finalizer.address + index);
    }

    public void putDouble(final long index, final double value) {
        ensureCapacity(index, SIZE_OF_DOUBLE);

        UnsafeApi.putDouble(null, finalizer.address + index, value);
    }

    //////

    public float getFloat(final long index, final ByteOrder byteOrder) {
        boundsCheck0(index, SIZE_OF_FLOAT);

        if (NATIVE_BYTE_ORDER != byteOrder) {
            final int bits = UnsafeApi.getInt(null, finalizer.address + index);
            return Float.intBitsToFloat(Integer.reverseBytes(bits));
        } else {
            return UnsafeApi.getFloat(null, finalizer.address + index);
        }
    }

    public void putFloat(final long index, final float value, final ByteOrder byteOrder) {
        ensureCapacity(index, SIZE_OF_FLOAT);

        if (NATIVE_BYTE_ORDER != byteOrder) {
            final int bits = Integer.reverseBytes(Float.floatToRawIntBits(value));
            UnsafeApi.putInt(null, finalizer.address + index, bits);
        } else {
            UnsafeApi.putFloat(null, finalizer.address + index, value);
        }
    }

    public float getFloat(final long index) {
        boundsCheck0(index, SIZE_OF_FLOAT);

        return UnsafeApi.getFloat(null, finalizer.address + index);
    }

    public void putFloat(final long index, final float value) {
        ensureCapacity(index, SIZE_OF_FLOAT);

        UnsafeApi.putFloat(null, finalizer.address + index, value);
    }

    //////

    public short getShort(final long index, final ByteOrder byteOrder) {
        boundsCheck0(index, SIZE_OF_SHORT);

        short bits = UnsafeApi.getShort(null, finalizer.address + index);
        if (NATIVE_BYTE_ORDER != byteOrder) {
            bits = Short.reverseBytes(bits);
        }

        return bits;
    }

    public void putShort(final long index, final short value, final ByteOrder byteOrder) {
        ensureCapacity(index, SIZE_OF_SHORT);

        short bits = value;
        if (NATIVE_BYTE_ORDER != byteOrder) {
            bits = Short.reverseBytes(bits);
        }

        UnsafeApi.putShort(null, finalizer.address + index, bits);
    }

    public short getShort(final long index) {
        boundsCheck0(index, SIZE_OF_SHORT);

        return UnsafeApi.getShort(null, finalizer.address + index);
    }

    public void putShort(final long index, final short value) {
        ensureCapacity(index, SIZE_OF_SHORT);

        UnsafeApi.putShort(null, finalizer.address + index, value);
    }

    //////

    public byte getByte(final long index) {
        boundsCheck0(index, SIZE_OF_BYTE);
        return UnsafeApi.getByte(null, finalizer.address + index);
    }

    public void putByte(final long index, final byte value) {
        ensureCapacity(index, SIZE_OF_BYTE);
        UnsafeApi.putByte(null, finalizer.address + index, value);
    }

    private void putByte0(final long index, final byte value) {
        ensureCapacity(index, SIZE_OF_BYTE);
        UnsafeApi.putByte(null, finalizer.address + index, value);
    }

    public void getBytes(final long index, final byte[] dst) {
        getBytes(index, dst, 0, dst.length);
    }

    public void getBytes(final long index, final byte[] dst, final int offset, final int length) {
        boundsCheck0(index, length);
        BufferUtil.boundsCheck(dst, offset, length);

        UnsafeApi.copyMemory(null, finalizer.address + index, dst, ARRAY_BASE_OFFSET + offset, length);
    }

    public void getBytes(final long index, final MutableDirectBuffer dstBuffer, final int dstIndex, final int length) {
        putBytesExplicit(dstBuffer, dstIndex, this, index, length);
    }

    private static void putBytesExplicit(final MutableDirectBuffer dstBuffer, final int dstIndex,
            final MappedExpandableMemoryBufferBase srcBuffer, final long srcIndex, final int length) {
        if (SHOULD_BOUNDS_CHECK) {
            dstBuffer.boundsCheck(dstIndex, length);
            srcBuffer.boundsCheck(srcIndex, length);
        }

        UnsafeApi.copyMemory(srcBuffer.byteArray(), srcBuffer.addressOffset() + srcIndex, dstBuffer.byteArray(),
                dstBuffer.addressOffset() + dstIndex, length);
    }

    public void getBytes(final long index, final java.nio.ByteBuffer dstBuffer, final int length) {
        final int dstOffset = dstBuffer.position();
        getBytes(index, dstBuffer, dstOffset, length);
        ByteBuffers.position(dstBuffer, dstOffset + length);
    }

    public void getBytes(final long index, final java.nio.ByteBuffer dstBuffer, final int dstOffset, final int length) {
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

        UnsafeApi.copyMemory(null, finalizer.address + index, dstByteArray, dstBaseOffset + dstOffset, length);
    }

    public void putBytes(final long index, final byte[] src) {
        putBytes(index, src, 0, src.length);
    }

    public void putBytes(final long index, final byte[] src, final int offset, final int length) {
        ensureCapacity(index, length);

        BufferUtil.boundsCheck(src, offset, length);

        UnsafeApi.copyMemory(src, ARRAY_BASE_OFFSET + offset, null, finalizer.address + index, length);
    }

    public void putBytes(final long index, final java.nio.ByteBuffer srcBuffer, final int length) {
        final int srcIndex = srcBuffer.position();
        putBytes(index, srcBuffer, srcIndex, length);
        ByteBuffers.position(srcBuffer, srcIndex + length);
    }

    public void putBytes(final long index, final java.nio.ByteBuffer srcBuffer, final int srcIndex, final int length) {
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

        UnsafeApi.copyMemory(srcByteArray, srcBaseOffset + srcIndex, null, finalizer.address + index, length);
    }

    public void putBytes(final long index, final DirectBuffer srcBuffer, final int srcIndex, final int length) {
        ensureCapacity(index, length);
        srcBuffer.boundsCheck(srcIndex, length);

        UnsafeApi.copyMemory(srcBuffer.byteArray(), srcBuffer.addressOffset() + srcIndex, null,
                finalizer.address + index, length);
    }

    //////

    public char getChar(final long index, final ByteOrder byteOrder) {
        boundsCheck0(index, SIZE_OF_SHORT);

        char bits = UnsafeApi.getChar(null, finalizer.address + index);
        if (NATIVE_BYTE_ORDER != byteOrder) {
            bits = (char) Short.reverseBytes((short) bits);
        }

        return bits;
    }

    public void putChar(final long index, final char value, final ByteOrder byteOrder) {
        ensureCapacity(index, SIZE_OF_CHAR);

        char bits = value;
        if (NATIVE_BYTE_ORDER != byteOrder) {
            bits = (char) Short.reverseBytes((short) bits);
        }

        UnsafeApi.putChar(null, finalizer.address + index, bits);
    }

    public char getChar(final long index) {
        boundsCheck0(index, SIZE_OF_CHAR);

        return UnsafeApi.getChar(null, finalizer.address + index);
    }

    public void putChar(final long index, final char value) {
        ensureCapacity(index, SIZE_OF_CHAR);

        UnsafeApi.putChar(null, finalizer.address + index, value);
    }

    //////

    public String getStringAscii(final long index) {
        boundsCheck0(index, DirectBuffer.STR_HEADER_LEN);

        final int length = UnsafeApi.getInt(null, finalizer.address + index);

        return getStringAscii(index, length);
    }

    public int getStringAscii(final long index, final Appendable appendable) {
        boundsCheck0(index, DirectBuffer.STR_HEADER_LEN);

        final int length = UnsafeApi.getInt(null, finalizer.address + index);

        return getStringAscii(index, length, appendable);
    }

    public String getStringAscii(final long index, final ByteOrder byteOrder) {
        boundsCheck0(index, DirectBuffer.STR_HEADER_LEN);

        int bits = UnsafeApi.getInt(null, finalizer.address + index);
        if (NATIVE_BYTE_ORDER != byteOrder) {
            bits = Integer.reverseBytes(bits);
        }

        final int length = bits;

        return getStringAscii(index, length);
    }

    public int getStringAscii(final long index, final Appendable appendable, final ByteOrder byteOrder) {
        boundsCheck0(index, DirectBuffer.STR_HEADER_LEN);

        int bits = UnsafeApi.getInt(null, finalizer.address + index);
        if (NATIVE_BYTE_ORDER != byteOrder) {
            bits = Integer.reverseBytes(bits);
        }

        final int length = bits;

        return getStringAscii(index, length, appendable);
    }

    public String getStringAscii(final long index, final int length) {
        boundsCheck0(index + DirectBuffer.STR_HEADER_LEN, length);

        final byte[] dst = new byte[length];
        UnsafeApi.copyMemory(null, finalizer.address + index + DirectBuffer.STR_HEADER_LEN, dst, ARRAY_BASE_OFFSET,
                length);

        return new String(dst, US_ASCII);
    }

    public int getStringAscii(final long index, final int length, final Appendable appendable) {
        boundsCheck0(index, length + DirectBuffer.STR_HEADER_LEN);

        try {
            for (long i = index + DirectBuffer.STR_HEADER_LEN,
                    limit = index + DirectBuffer.STR_HEADER_LEN + length; i < limit; i++) {
                final char c = (char) UnsafeApi.getByte(null, finalizer.address + i);
                appendable.append(c > 127 ? '?' : c);
            }
        } catch (final IOException ex) {
            LangUtil.rethrowUnchecked(ex);
        }

        return length;
    }

    public int putStringAscii(final long index, final String value) {
        final int length = value != null ? value.length() : 0;

        ensureCapacity(index, length + DirectBuffer.STR_HEADER_LEN);

        UnsafeApi.putInt(null, finalizer.address + index, length);

        for (int i = 0; i < length; i++) {
            char c = value.charAt(i);
            if (c > 127) {
                c = '?';
            }

            UnsafeApi.putByte(null, finalizer.address + DirectBuffer.STR_HEADER_LEN + index + i, (byte) c);
        }

        return DirectBuffer.STR_HEADER_LEN + length;
    }

    public int putStringAscii(final long index, final CharSequence value) {
        final int length = value != null ? value.length() : 0;

        ensureCapacity(index, length + DirectBuffer.STR_HEADER_LEN);

        UnsafeApi.putInt(null, finalizer.address + index, length);

        for (int i = 0; i < length; i++) {
            char c = value.charAt(i);
            if (c > 127) {
                c = '?';
            }

            UnsafeApi.putByte(null, finalizer.address + DirectBuffer.STR_HEADER_LEN + index + i, (byte) c);
        }

        return DirectBuffer.STR_HEADER_LEN + length;
    }

    public int putStringAscii(final long index, final String value, final ByteOrder byteOrder) {
        final int length = value != null ? value.length() : 0;

        ensureCapacity(index, length + DirectBuffer.STR_HEADER_LEN);

        int bits = length;
        if (NATIVE_BYTE_ORDER != byteOrder) {
            bits = Integer.reverseBytes(bits);
        }

        UnsafeApi.putInt(null, finalizer.address + index, bits);

        for (int i = 0; i < length; i++) {
            char c = value.charAt(i);
            if (c > 127) {
                c = '?';
            }

            UnsafeApi.putByte(null, finalizer.address + DirectBuffer.STR_HEADER_LEN + index + i, (byte) c);
        }

        return DirectBuffer.STR_HEADER_LEN + length;
    }

    public int putStringAscii(final long index, final CharSequence value, final ByteOrder byteOrder) {
        final int length = value != null ? value.length() : 0;

        ensureCapacity(index, length + DirectBuffer.STR_HEADER_LEN);

        int bits = length;
        if (NATIVE_BYTE_ORDER != byteOrder) {
            bits = Integer.reverseBytes(bits);
        }

        UnsafeApi.putInt(null, finalizer.address + index, bits);

        for (int i = 0; i < length; i++) {
            char c = value.charAt(i);
            if (c > 127) {
                c = '?';
            }

            UnsafeApi.putByte(null, finalizer.address + DirectBuffer.STR_HEADER_LEN + index + i, (byte) c);
        }

        return DirectBuffer.STR_HEADER_LEN + length;
    }

    public String getStringWithoutLengthAscii(final long index, final int length) {
        boundsCheck0(index, length);

        final byte[] dst = new byte[length];
        UnsafeApi.copyMemory(null, finalizer.address + index, dst, ARRAY_BASE_OFFSET, length);

        return new String(dst, US_ASCII);
    }

    public int getStringWithoutLengthAscii(final long index, final int length, final Appendable appendable) {
        boundsCheck0(index, length);

        try {
            for (long i = index, limit = index + length; i < limit; i++) {
                final char c = (char) UnsafeApi.getByte(null, finalizer.address + i);
                appendable.append(c > 127 ? '?' : c);
            }
        } catch (final IOException ex) {
            LangUtil.rethrowUnchecked(ex);
        }

        return length;
    }

    public int putStringWithoutLengthAscii(final long index, final String value) {
        final int length = value != null ? value.length() : 0;

        ensureCapacity(index, length);

        for (int i = 0; i < length; i++) {
            char c = value.charAt(i);
            if (c > 127) {
                c = '?';
            }

            UnsafeApi.putByte(null, finalizer.address + index + i, (byte) c);
        }

        return length;
    }

    public int putStringWithoutLengthAscii(final long index, final CharSequence value) {
        final int length = value != null ? value.length() : 0;

        ensureCapacity(index, length);

        for (int i = 0; i < length; i++) {
            char c = value.charAt(i);
            if (c > 127) {
                c = '?';
            }

            UnsafeApi.putByte(null, finalizer.address + index + i, (byte) c);
        }

        return length;
    }

    public int putStringWithoutLengthAscii(final long index, final String value, final int valueOffset,
            final int length) {
        final int len = value != null ? Integers.min(value.length() - valueOffset, length) : 0;

        ensureCapacity(index, len);

        for (int i = 0; i < len; i++) {
            char c = value.charAt(valueOffset + i);
            if (c > 127) {
                c = '?';
            }

            UnsafeApi.putByte(null, finalizer.address + index + i, (byte) c);
        }

        return len;
    }

    public int putStringWithoutLengthAscii(final long index, final CharSequence value, final int valueOffset,
            final int length) {
        final int len = value != null ? Integers.min(value.length() - valueOffset, length) : 0;

        ensureCapacity(index, len);

        for (int i = 0; i < len; i++) {
            char c = value.charAt(valueOffset + i);
            if (c > 127) {
                c = '?';
            }

            UnsafeApi.putByte(null, finalizer.address + index + i, (byte) c);
        }

        return len;
    }

    //////

    public String getStringUtf8(final long index) {
        boundsCheck0(index, DirectBuffer.STR_HEADER_LEN);

        final int length = UnsafeApi.getInt(null, finalizer.address + index);

        return getStringUtf8(index, length);
    }

    public String getStringUtf8(final long index, final ByteOrder byteOrder) {
        boundsCheck0(index, DirectBuffer.STR_HEADER_LEN);

        int bits = UnsafeApi.getInt(null, finalizer.address + index);
        if (NATIVE_BYTE_ORDER != byteOrder) {
            bits = Integer.reverseBytes(bits);
        }

        final int length = bits;

        return getStringUtf8(index, length);
    }

    public String getStringUtf8(final long index, final int length) {
        boundsCheck0(index + DirectBuffer.STR_HEADER_LEN, length);

        final byte[] stringInBytes = new byte[length];
        UnsafeApi.copyMemory(null, finalizer.address + index + DirectBuffer.STR_HEADER_LEN, stringInBytes,
                ARRAY_BASE_OFFSET, length);

        return new String(stringInBytes, UTF_8);
    }

    public int putStringUtf8(final long index, final String value) {
        return putStringUtf8(index, value, Integer.MAX_VALUE);
    }

    public int putStringUtf8(final long index, final String value, final ByteOrder byteOrder) {
        return putStringUtf8(index, value, byteOrder, Integer.MAX_VALUE);
    }

    public int putStringUtf8(final long index, final String value, final int maxEncodedLength) {
        final byte[] bytes = value != null ? value.getBytes(UTF_8) : NULL_BYTES;
        if (bytes.length > maxEncodedLength) {
            throw new IllegalArgumentException("Encoded string larger than maximum size: " + maxEncodedLength);
        }

        ensureCapacity(index, DirectBuffer.STR_HEADER_LEN + bytes.length);

        UnsafeApi.putInt(null, finalizer.address + index, bytes.length);
        UnsafeApi.copyMemory(bytes, ARRAY_BASE_OFFSET, null, finalizer.address + index + DirectBuffer.STR_HEADER_LEN,
                bytes.length);

        return DirectBuffer.STR_HEADER_LEN + bytes.length;
    }

    public int putStringUtf8(final long index, final String value, final ByteOrder byteOrder,
            final int maxEncodedLength) {
        final byte[] bytes = value != null ? value.getBytes(UTF_8) : NULL_BYTES;
        if (bytes.length > maxEncodedLength) {
            throw new IllegalArgumentException("Encoded string larger than maximum size: " + maxEncodedLength);
        }

        ensureCapacity(index, DirectBuffer.STR_HEADER_LEN + bytes.length);

        int bits = bytes.length;
        if (NATIVE_BYTE_ORDER != byteOrder) {
            bits = Integer.reverseBytes(bits);
        }

        UnsafeApi.putInt(null, finalizer.address + index, bits);
        UnsafeApi.copyMemory(bytes, ARRAY_BASE_OFFSET, null, finalizer.address + index + DirectBuffer.STR_HEADER_LEN,
                bytes.length);

        return DirectBuffer.STR_HEADER_LEN + bytes.length;
    }

    public String getStringWithoutLengthUtf8(final long index, final int length) {
        boundsCheck0(index, length);

        final byte[] stringInBytes = new byte[length];
        UnsafeApi.copyMemory(null, finalizer.address + index, stringInBytes, ARRAY_BASE_OFFSET, length);

        return new String(stringInBytes, UTF_8);
    }

    public int putStringWithoutLengthUtf8(final long index, final String value) {
        final byte[] bytes = value != null ? value.getBytes(UTF_8) : NULL_BYTES;
        ensureCapacity(index, bytes.length);

        UnsafeApi.copyMemory(bytes, ARRAY_BASE_OFFSET, null, finalizer.address + index, bytes.length);

        return bytes.length;
    }

    //////

    public int parseNaturalIntAscii(final long index, final int length) {
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

    public long parseNaturalLongAscii(final long index, final int length) {
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

    public int parseIntAscii(final long index, final int length) {
        boundsCheck0(index, length);

        if (length <= 0) {
            throw new AsciiNumberFormatException("empty string: index=" + index + " length=" + length);
        }

        final boolean negative = MINUS_SIGN == UnsafeApi.getByte(null, finalizer.address + index);
        long i = index;
        if (negative) {
            i++;
            if (1 == length) {
                throwParseIntError(index, length);
            }
        }

        final long end = index + length;
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

    public long parseLongAscii(final long index, final int length) {
        boundsCheck0(index, length);

        if (length <= 0) {
            throw new AsciiNumberFormatException("empty string: index=" + index + " length=" + length);
        }

        final boolean negative = MINUS_SIGN == UnsafeApi.getByte(null, finalizer.address + index);
        long i = index;
        if (negative) {
            i++;
            if (1 == length) {
                throwParseLongError(index, length);
            }
        }

        final long end = index + length;
        if (end - i < LONG_MAX_DIGITS) {
            final long tally = parsePositiveLongAscii(index, length, i, end);
            return negative ? -tally : tally;
        } else if (negative) {
            return -parseLongAsciiOverflowCheck(index, length, LONG_MIN_VALUE_DIGITS, i, end);
        } else {
            return parseLongAsciiOverflowCheck(index, length, LONG_MAX_VALUE_DIGITS, i, end);
        }
    }

    public void putInt(final long index, final int value) {
        ensureCapacity(index, SIZE_OF_INT);

        UnsafeApi.putInt(null, finalizer.address + index, value);
    }

    public int putIntAscii(final long index, final int value) {
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
            offset = finalizer.address + index;

            UnsafeApi.putByte(null, offset, MINUS_SIGN);
            offset++;
        } else {
            digitCount = digitCount(quotient);
            length = digitCount;

            ensureCapacity(index, length);
            offset = finalizer.address + index;
        }

        putPositiveIntAscii(offset, quotient, digitCount);

        return length;
    }

    public int putNaturalIntAscii(final long index, final int value) {
        if (0 == value) {
            putByte0(index, ZERO);
            return 1;
        }

        final int digitCount = digitCount(value);

        ensureCapacity(index, digitCount);

        putPositiveIntAscii(finalizer.address + index, value, digitCount);

        return digitCount;
    }

    public void putNaturalPaddedIntAscii(final int offset, final int length, final int value) {
        final int end = offset + length;
        int remainder = value;
        for (long index = end - 1; index >= offset; index--) {
            final int digit = remainder % 10;
            remainder = remainder / 10;
            putByte0(index, (byte) (ZERO + digit));
        }

        if (remainder != 0) {
            throw new NumberFormatException("Cannot write " + value + " in " + length + " bytes");
        }
    }

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

    public int putNaturalLongAscii(final long index, final long value) {
        if (0L == value) {
            putByte0(index, ZERO);
            return 1;
        }

        final int digitCount = digitCount(value);

        ensureCapacity(index, digitCount);

        putPositiveLongAscii(finalizer.address + index, value, digitCount);

        return digitCount;
    }

    public int putLongAscii(final long index, final long value) {
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
            offset = finalizer.address + index;

            UnsafeApi.putByte(null, offset, MINUS_SIGN);
            offset++;
        } else {
            digitCount = digitCount(quotient);
            length = digitCount;

            ensureCapacity(index, length);
            offset = finalizer.address + index;
        }

        putPositiveLongAscii(offset, quotient, digitCount);

        return length;
    }

    public void boundsCheck(final long index, final int length) {
        boundsCheck0(index, length);
    }

    public long wrapAdjustment() {
        return 0;
    }

    //////

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final MappedExpandableMemoryBufferBase that = (MappedExpandableMemoryBufferBase) obj;

        return compareTo(that) == 0;
    }

    @Override
    public int hashCode() {
        int hashCode = 1;

        final long address = finalizer.address;
        for (long i = 0, length = finalizer.capacity; i < length; i++) {
            hashCode = 31 * hashCode + UnsafeApi.getByte(null, address + i);
        }

        return hashCode;
    }

    public int compareTo(final MappedExpandableMemoryBufferBase that) {
        final long thisCapacity = this.capacity();
        final long thatCapacity = that.capacity();
        final byte[] thatByteArray = that.byteArray();
        final long thisOffset = this.addressOffset();
        final long thatOffset = that.addressOffset();

        for (long i = 0, length = Longs.min(thisCapacity, thatCapacity); i < length; i++) {
            final int cmp = Byte.compare(UnsafeApi.getByte(null, thisOffset + i),
                    UnsafeApi.getByte(thatByteArray, thatOffset + i));

            if (0 != cmp) {
                return cmp;
            }
        }

        if (thisCapacity != thatCapacity) {
            return (int) (thisCapacity - thatCapacity);
        }

        return 0;
    }

    @Override
    public String toString() {
        return MappedExpandableMemoryBufferBase.class.getSimpleName() + "{" + "address=" + finalizer.address
                + ", capacity=" + finalizer.capacity + ", mappedFile=" + finalizer.file + '}';
    }

    private void ensureCapacity(final long index, final long length) {
        if (index < 0 || length < 0) {
            throw FastIndexOutOfBoundsException.getInstance("negative value: index=%s length=%s", index, length);
        }

        final long resultingPosition = index + length;
        final long currentCapacity = finalizer.capacity;
        if (resultingPosition > currentCapacity) {
            if (resultingPosition > MAX_BUFFER_LENGTH) {
                throw FastIndexOutOfBoundsException.getInstance("index=%s length=%s maxCapacity=%s", index, length,
                        MAX_BUFFER_LENGTH);
            }

            final long newCapacity = calculateExpansion(currentCapacity, resultingPosition);
            finalizer.mappedFile.setDeleteOnClose(false);
            finalizer.mappedFile.close();
            try {
                finalizer.mappedFile = new MemoryMappedFile(true, finalizer.file, 0,
                        IMemoryMappedFile.roundToBlockSize(newCapacity), false, finalizer.deleteOnClose);
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }

            //copy not needed
            //            getBytes(0, newBuffer, 0, finalizer.capacity);

            finalizer.address = finalizer.mappedFile.addressOffset();
            finalizer.capacity = finalizer.mappedFile.capacity();
        }
    }

    protected long calculateExpansion(final long currentLength, final long requiredLength) {
        final long value = MemoryBuffers.calculateExpansion(requiredLength);
        if (value > MAX_BUFFER_LENGTH) {
            return MAX_BUFFER_LENGTH;
        } else {
            return value;
        }
    }

    private void boundsCheck0(final long index, final long length) {
        final long currentCapacity = finalizer.capacity;
        final long resultingPosition = index + length;
        if (index < 0 || length < 0 || resultingPosition > currentCapacity) {
            throw FastIndexOutOfBoundsException.getInstance("index=%s length=%s capacity=%s", index, length,
                    currentCapacity);
        }
    }

    //CHECKSTYLE:OFF
    private int parsePositiveIntAscii(final long index, final int length, final long startIndex, final long end) {
        final long offset = finalizer.address;
        long i = startIndex;
        int tally = 0, quartet;
        while ((end - i) >= 4 && isFourDigitsAsciiEncodedNumber(quartet = UnsafeApi.getInt(null, offset + i))) {
            if (NATIVE_BYTE_ORDER != LITTLE_ENDIAN) {
                quartet = Integer.reverseBytes(quartet);
            }

            tally = (tally * 10_000) + parseFourDigitsLittleEndian(quartet);
            i += 4;
        }

        byte digit;
        while (i < end && isDigit(digit = UnsafeApi.getByte(null, offset + i))) {
            tally = (tally * 10) + (digit - 0x30);
            i++;
        }

        if (i != end) {
            throwParseIntError(index, length);
        }

        return tally;
    }

    private long parsePositiveIntAsciiOverflowCheck(final long index, final int length, final long startIndex,
            final long end) {
        if ((end - startIndex) > INT_MAX_DIGITS) {
            throwParseIntOverflowError(index, length);
        }

        final long offset = finalizer.address;
        long i = startIndex;
        long tally = 0;
        long octet = UnsafeApi.getLong(null, offset + i);
        if (isEightDigitAsciiEncodedNumber(octet)) {
            if (NATIVE_BYTE_ORDER != LITTLE_ENDIAN) {
                octet = Long.reverseBytes(octet);
            }
            tally = parseEightDigitsLittleEndian(octet);
            i += 8;

            byte digit;
            while (i < end && isDigit(digit = UnsafeApi.getByte(null, offset + i))) {
                tally = (tally * 10L) + (digit - 0x30);
                i++;
            }
        }

        if (i != end) {
            throwParseIntError(index, length);
        }

        return tally;
    }

    private void throwParseIntError(final long index, final int length) {
        throw new AsciiNumberFormatException("error parsing int: " + getStringWithoutLengthAscii(index, length));
    }

    private void throwParseIntOverflowError(final long index, final int length) {
        throw new AsciiNumberFormatException("int overflow parsing: " + getStringWithoutLengthAscii(index, length));
    }

    private long parsePositiveLongAscii(final long index, final int length, final long startIndex, final long end) {
        final long offset = finalizer.address;
        long i = startIndex;
        long tally = 0, octet;
        while ((end - i) >= 8 && isEightDigitAsciiEncodedNumber(octet = UnsafeApi.getLong(null, offset + i))) {
            if (NATIVE_BYTE_ORDER != LITTLE_ENDIAN) {
                octet = Long.reverseBytes(octet);
            }

            tally = (tally * 100_000_000L) + parseEightDigitsLittleEndian(octet);
            i += 8;
        }

        int quartet;
        while ((end - i) >= 4 && isFourDigitsAsciiEncodedNumber(quartet = UnsafeApi.getInt(null, offset + i))) {
            if (NATIVE_BYTE_ORDER != LITTLE_ENDIAN) {
                quartet = Integer.reverseBytes(quartet);
            }

            tally = (tally * 10_000L) + parseFourDigitsLittleEndian(quartet);
            i += 4;
        }

        byte digit;
        while (i < end && isDigit(digit = UnsafeApi.getByte(null, offset + i))) {
            tally = (tally * 10) + (digit - 0x30);
            i++;
        }

        if (i != end) {
            throwParseLongError(index, length);
        }

        return tally;
    }

    private long parseLongAsciiOverflowCheck(final long index, final int length, final int[] maxValue,
            final long startIndex, final long end) {
        if ((end - startIndex) > LONG_MAX_DIGITS) {
            throwParseLongOverflowError(index, length);
        }

        final long offset = finalizer.address;
        long i = startIndex;
        int k = 0;
        boolean checkOverflow = true;
        long tally = 0, octet;
        while ((end - i) >= 8 && isEightDigitAsciiEncodedNumber(octet = UnsafeApi.getLong(null, offset + i))) {
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
        while (i < end && isDigit(digit = UnsafeApi.getByte(null, offset + i))) {
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

    private void throwParseLongError(final long index, final int length) {
        throw new AsciiNumberFormatException("error parsing long: " + getStringWithoutLengthAscii(index, length));
    }

    private void throwParseLongOverflowError(final long index, final int length) {
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

            UnsafeApi.putByte(null, offset + i, ASCII_DIGITS[p1]);
            UnsafeApi.putByte(null, offset + i + 1, ASCII_DIGITS[p1 + 1]);
            UnsafeApi.putByte(null, offset + i + 2, ASCII_DIGITS[p2]);
            UnsafeApi.putByte(null, offset + i + 3, ASCII_DIGITS[p2 + 1]);
        }

        if (quotient >= 100) {
            final int position = (quotient % 100) << 1;
            quotient /= 100;
            UnsafeApi.putByte(null, offset + i - 1, ASCII_DIGITS[position + 1]);
            UnsafeApi.putByte(null, offset + i - 2, ASCII_DIGITS[position]);
        }

        if (quotient >= 10) {
            final int position = quotient << 1;
            UnsafeApi.putByte(null, offset + 1, ASCII_DIGITS[position + 1]);
            UnsafeApi.putByte(null, offset, ASCII_DIGITS[position]);
        } else {
            UnsafeApi.putByte(null, offset, (byte) (ZERO + quotient));
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

            UnsafeApi.putByte(null, offset + i, ASCII_DIGITS[u1]);
            UnsafeApi.putByte(null, offset + i + 1, ASCII_DIGITS[u1 + 1]);
            UnsafeApi.putByte(null, offset + i + 2, ASCII_DIGITS[u2]);
            UnsafeApi.putByte(null, offset + i + 3, ASCII_DIGITS[u2 + 1]);
            UnsafeApi.putByte(null, offset + i + 4, ASCII_DIGITS[l1]);
            UnsafeApi.putByte(null, offset + i + 5, ASCII_DIGITS[l1 + 1]);
            UnsafeApi.putByte(null, offset + i + 6, ASCII_DIGITS[l2]);
            UnsafeApi.putByte(null, offset + i + 7, ASCII_DIGITS[l2 + 1]);
        }

        putPositiveIntAscii(offset, (int) quotient, i);
    }
}
