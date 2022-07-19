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
import static org.agrona.collections.ArrayUtil.EMPTY_BYTE_ARRAY;

import java.io.IOException;
import java.nio.ByteOrder;

import javax.annotation.concurrent.NotThreadSafe;

import org.agrona.AsciiNumberFormatException;
import org.agrona.BufferUtil;
import org.agrona.DirectBuffer;
import org.agrona.LangUtil;
import org.agrona.MutableDirectBuffer;
import org.agrona.SystemUtil;

import de.invesdwin.util.lang.reflection.Reflections;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;

/**
 * Supports regular, byte ordered, and atomic (memory ordered) access to an underlying buffer. The buffer can be a
 * byte[], one of the various ByteBuffer implementations, or an off Java heap memory address.
 * <p>
 * {@link ByteOrder} of a wrapped buffer is not applied to the {@link UnsafeMemoryBase}. {@link UnsafeMemoryBase}s are
 * effectively stateless and can be used concurrently, the wrapping methods are an exception. To control
 * {@link ByteOrder} use the appropriate method with the {@link ByteOrder} overload.
 * <p>
 * <b>Note:</b> This class has a natural ordering that is inconsistent with equals. Types may be different but equal on
 * buffer contents.
 * <p>
 * <b>Note:</b> The wrap methods on this class are not thread safe. Concurrent access should only happen after a
 * successful wrap.
 */
@SuppressWarnings("restriction")
@NotThreadSafe
public class UnsafeMemoryBase {
    /**
     * Buffer alignment to ensure atomic word accesses.
     */
    public static final int ALIGNMENT = SIZE_OF_LONG;

    /**
     * Name of the system property that specify if the bounds checks should be disabled. To disable bounds checks set
     * this property to {@code true}.
     */
    public static final String DISABLE_BOUNDS_CHECKS_PROP_NAME = "agrona.disable.bounds.checks";

    /**
     * Should bounds-checks operations be done or not. Controlled by the {@link #DISABLE_BOUNDS_CHECKS_PROP_NAME} system
     * property.
     *
     * @see #DISABLE_BOUNDS_CHECKS_PROP_NAME
     */
    public static final boolean SHOULD_BOUNDS_CHECK = !"true"
            .equals(SystemUtil.getProperty(DISABLE_BOUNDS_CHECKS_PROP_NAME));

    private static final sun.misc.Unsafe UNSAFE = Reflections.getUnsafe();

    private long addressOffset;
    private long capacity;
    private byte[] byteArray;
    private java.nio.ByteBuffer byteBuffer;

    /**
     * Empty constructor for a reusable wrapper buffer.
     */
    public UnsafeMemoryBase() {
        wrap(EMPTY_BYTE_ARRAY);
    }

    /**
     * Attach a view to a byte[] for providing direct access.
     *
     * @param buffer
     *            to which the view is attached.
     */
    public UnsafeMemoryBase(final byte[] buffer) {
        wrap(buffer);
    }

    /**
     * Attach a view to a byte[] for providing direct access.
     *
     * @param buffer
     *            to which the view is attached.
     * @param offset
     *            within the buffer to begin.
     * @param length
     *            of the buffer to be included.
     */
    public UnsafeMemoryBase(final byte[] buffer, final int offset, final int length) {
        wrap(buffer, offset, length);
    }

    /**
     * Attach a view to a ByteBuffer for providing direct access, the ByteBuffer can be heap based or direct.
     *
     * @param buffer
     *            to which the view is attached.
     */
    public UnsafeMemoryBase(final java.nio.ByteBuffer buffer) {
        wrap(buffer);
    }

    /**
     * Attach a view to a ByteBuffer for providing direct access, the ByteBuffer can be heap based or direct.
     *
     * @param buffer
     *            to which the view is attached.
     * @param offset
     *            within the buffer to begin.
     * @param length
     *            of the buffer to be included.
     */
    public UnsafeMemoryBase(final java.nio.ByteBuffer buffer, final long offset, final long length) {
        wrap(buffer, offset, length);
    }

    /**
     * Attach a view to an existing {@link DirectBuffer}
     *
     * @param buffer
     *            to which the view is attached.
     */
    public UnsafeMemoryBase(final DirectBuffer buffer) {
        wrap(buffer);
    }

    /**
     * Attach a view to an existing {@link DirectBuffer}
     *
     * @param buffer
     *            to which the view is attached.
     * @param offset
     *            within the buffer to begin.
     * @param length
     *            of the buffer to be included.
     */
    public UnsafeMemoryBase(final DirectBuffer buffer, final long offset, final long length) {
        wrap(buffer, offset, length);
    }

    public UnsafeMemoryBase(final IMemoryBuffer buffer, final long offset, final long length) {
        wrap(buffer, offset, length);
    }

    public UnsafeMemoryBase(final UnsafeMemoryBase buffer, final long offset, final long length) {
        wrap(buffer, offset, length);
    }

    /**
     * Attach a view to an off-heap memory region by address. This is useful for interacting with native libraries.
     *
     * @param address
     *            where the memory begins off-heap
     * @param length
     *            of the buffer from the given address
     */
    public UnsafeMemoryBase(final long address, final long length) {
        wrap(address, length);
    }

    public void wrap(final byte[] buffer) {
        capacity = buffer.length;
        addressOffset = ARRAY_BASE_OFFSET;
        byteBuffer = null;

        if (buffer != byteArray) {
            byteArray = buffer;
        }
    }

    public void wrap(final byte[] buffer, final int offset, final int length) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheckWrap(offset, length, buffer.length);
        }

        capacity = length;
        addressOffset = ARRAY_BASE_OFFSET + offset;
        byteBuffer = null;

        if (buffer != byteArray) {
            byteArray = buffer;
        }
    }

    public void wrap(final java.nio.ByteBuffer buffer) {
        capacity = buffer.capacity();

        if (buffer != byteBuffer) {
            byteBuffer = buffer;
        }

        if (buffer.isDirect()) {
            byteArray = null;
            addressOffset = address(buffer);
        } else {
            byteArray = array(buffer);
            addressOffset = ARRAY_BASE_OFFSET + arrayOffset(buffer);
        }
    }

    public void wrap(final java.nio.ByteBuffer buffer, final long offset, final long length) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheckWrap(offset, length, buffer.capacity());
        }

        capacity = length;

        if (buffer != byteBuffer) {
            byteBuffer = buffer;
        }

        if (buffer.isDirect()) {
            byteArray = null;
            addressOffset = address(buffer) + offset;
        } else {
            byteArray = array(buffer);
            addressOffset = ARRAY_BASE_OFFSET + arrayOffset(buffer) + offset;
        }
    }

    public void wrap(final DirectBuffer buffer) {
        capacity = buffer.capacity();
        addressOffset = buffer.addressOffset();

        final byte[] byteArray = buffer.byteArray();
        if (byteArray != this.byteArray) {
            this.byteArray = byteArray;
        }

        final java.nio.ByteBuffer byteBuffer = buffer.byteBuffer();
        if (byteBuffer != this.byteBuffer) {
            this.byteBuffer = byteBuffer;
        }
    }

    public void wrap(final DirectBuffer buffer, final long offset, final long length) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheckWrap(offset, length, buffer.capacity());
        }

        capacity = length;
        addressOffset = buffer.addressOffset() + offset;

        final byte[] byteArray = buffer.byteArray();
        if (byteArray != this.byteArray) {
            this.byteArray = byteArray;
        }

        final java.nio.ByteBuffer byteBuffer = buffer.byteBuffer();
        if (byteBuffer != this.byteBuffer) {
            this.byteBuffer = byteBuffer;
        }
    }

    public void wrap(final IMemoryBuffer buffer, final long offset, final long length) {
        if (buffer instanceof UnsafeMemoryBase) {
            final UnsafeMemoryBase cBuffer = (UnsafeMemoryBase) buffer;
            wrap(cBuffer, offset, length);
        } else {
            if (SHOULD_BOUNDS_CHECK) {
                boundsCheckWrap(offset, length, buffer.capacity());
            }

            capacity = length;
            addressOffset = buffer.addressOffset() + offset;
        }
    }

    public void wrap(final UnsafeMemoryBase buffer, final long offset, final long length) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheckWrap(offset, length, buffer.capacity());
        }

        capacity = length;
        addressOffset = buffer.addressOffset() + offset;

        final byte[] byteArray = buffer.byteArray();
        if (byteArray != this.byteArray) {
            this.byteArray = byteArray;
        }

        final java.nio.ByteBuffer byteBuffer = buffer.byteBuffer();
        if (byteBuffer != this.byteBuffer) {
            this.byteBuffer = byteBuffer;
        }
    }

    public void wrap(final long address, final long length) {
        capacity = length;
        addressOffset = address;
        byteArray = null;
        byteBuffer = null;
    }

    public long addressOffset() {
        return addressOffset;
    }

    public byte[] byteArray() {
        return byteArray;
    }

    public java.nio.ByteBuffer byteBuffer() {
        return byteBuffer;
    }

    public void setMemory(final long index, final long length, final byte value) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, length);
        }

        final long offset = addressOffset + index;
        if (MEMSET_HACK_REQUIRED && length > MEMSET_HACK_THRESHOLD && 0 == (offset & 1)) {
            // This horrible filth is to encourage the JVM to call memset() when address is even.
            UNSAFE.putByte(byteArray, offset, value);
            UNSAFE.setMemory(byteArray, offset + 1, length - 1, value);
        } else {
            UNSAFE.setMemory(byteArray, offset, length, value);
        }
    }

    public long capacity() {
        return capacity;
    }

    public void checkLimit(final long limit) {
        if (limit > capacity) {
            throw new IndexOutOfBoundsException("limit=" + limit + " is beyond capacity=" + capacity);
        }
    }

    public boolean isExpandable() {
        return false;
    }

    public void verifyAlignment() {
        if (0 != (addressOffset & (ALIGNMENT - 1))) {
            throw new IllegalStateException("AtomicBuffer is not correctly aligned: addressOffset=" + addressOffset
                    + " is not divisible by " + ALIGNMENT);
        }
    }

    ///////////////////////////////////////////////////////////////////////////

    public long getLong(final long index, final ByteOrder byteOrder) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, SIZE_OF_LONG);
        }

        long bits = UNSAFE.getLong(byteArray, addressOffset + index);
        if (NATIVE_BYTE_ORDER != byteOrder) {
            bits = Long.reverseBytes(bits);
        }

        return bits;
    }

    public void putLong(final long index, final long value, final ByteOrder byteOrder) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, SIZE_OF_LONG);
        }

        long bits = value;
        if (NATIVE_BYTE_ORDER != byteOrder) {
            bits = Long.reverseBytes(bits);
        }

        UNSAFE.putLong(byteArray, addressOffset + index, bits);
    }

    public long getLong(final long index) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, SIZE_OF_LONG);
        }

        return UNSAFE.getLong(byteArray, addressOffset + index);
    }

    public void putLong(final long index, final long value) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, SIZE_OF_LONG);
        }

        UNSAFE.putLong(byteArray, addressOffset + index, value);
    }

    public long getLongVolatile(final long index) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, SIZE_OF_LONG);
        }

        return UNSAFE.getLongVolatile(byteArray, addressOffset + index);
    }

    public void putLongVolatile(final long index, final long value) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, SIZE_OF_LONG);
        }

        UNSAFE.putLongVolatile(byteArray, addressOffset + index, value);
    }

    public void putLongOrdered(final long index, final long value) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, SIZE_OF_LONG);
        }

        UNSAFE.putOrderedLong(byteArray, addressOffset + index, value);
    }

    public long addLongOrdered(final long index, final long increment) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, SIZE_OF_LONG);
        }

        final long offset = addressOffset + index;
        final byte[] byteArray = this.byteArray;
        final long value = UNSAFE.getLong(byteArray, offset);
        UNSAFE.putOrderedLong(byteArray, offset, value + increment);

        return value;
    }

    public boolean compareAndSetLong(final long index, final long expectedValue, final long updateValue) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, SIZE_OF_LONG);
        }

        return UNSAFE.compareAndSwapLong(byteArray, addressOffset + index, expectedValue, updateValue);
    }

    public long getAndSetLong(final long index, final long value) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, SIZE_OF_LONG);
        }

        return UNSAFE.getAndSetLong(byteArray, addressOffset + index, value);
    }

    public long getAndAddLong(final long index, final long delta) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, SIZE_OF_LONG);
        }

        return UNSAFE.getAndAddLong(byteArray, addressOffset + index, delta);
    }

    ///////////////////////////////////////////////////////////////////////////

    public int getInt(final long index, final ByteOrder byteOrder) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, SIZE_OF_INT);
        }

        int bits = UNSAFE.getInt(byteArray, addressOffset + index);
        if (NATIVE_BYTE_ORDER != byteOrder) {
            bits = Integer.reverseBytes(bits);
        }

        return bits;
    }

    public void putInt(final long index, final int value, final ByteOrder byteOrder) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, SIZE_OF_INT);
        }

        int bits = value;
        if (NATIVE_BYTE_ORDER != byteOrder) {
            bits = Integer.reverseBytes(bits);
        }

        UNSAFE.putInt(byteArray, addressOffset + index, bits);
    }

    public int getInt(final long index) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, SIZE_OF_INT);
        }

        return UNSAFE.getInt(byteArray, addressOffset + index);
    }

    public void putInt(final long index, final int value) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, SIZE_OF_INT);
        }

        UNSAFE.putInt(byteArray, addressOffset + index, value);
    }

    public int getIntVolatile(final long index) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, SIZE_OF_INT);
        }

        return UNSAFE.getIntVolatile(byteArray, addressOffset + index);
    }

    public void putIntVolatile(final long index, final int value) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, SIZE_OF_INT);
        }

        UNSAFE.putIntVolatile(byteArray, addressOffset + index, value);
    }

    public void putIntOrdered(final long index, final int value) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, SIZE_OF_INT);
        }

        UNSAFE.putOrderedInt(byteArray, addressOffset + index, value);
    }

    public int addIntOrdered(final long index, final int increment) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, SIZE_OF_INT);
        }

        final long offset = addressOffset + index;
        final byte[] byteArray = this.byteArray;
        final int value = UNSAFE.getInt(byteArray, offset);
        UNSAFE.putOrderedInt(byteArray, offset, value + increment);

        return value;
    }

    public boolean compareAndSetInt(final long index, final int expectedValue, final int updateValue) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, SIZE_OF_INT);
        }

        return UNSAFE.compareAndSwapInt(byteArray, addressOffset + index, expectedValue, updateValue);
    }

    public int getAndSetInt(final long index, final int value) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, SIZE_OF_INT);
        }

        return UNSAFE.getAndSetInt(byteArray, addressOffset + index, value);
    }

    public int getAndAddInt(final long index, final int delta) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, SIZE_OF_INT);
        }

        return UNSAFE.getAndAddInt(byteArray, addressOffset + index, delta);
    }

    ///////////////////////////////////////////////////////////////////////////

    public double getDouble(final long index, final ByteOrder byteOrder) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, SIZE_OF_DOUBLE);
        }

        if (NATIVE_BYTE_ORDER != byteOrder) {
            final long bits = UNSAFE.getLong(byteArray, addressOffset + index);
            return Double.longBitsToDouble(Long.reverseBytes(bits));
        } else {
            return UNSAFE.getDouble(byteArray, addressOffset + index);
        }
    }

    public void putDouble(final long index, final double value, final ByteOrder byteOrder) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, SIZE_OF_DOUBLE);
        }

        if (NATIVE_BYTE_ORDER != byteOrder) {
            final long bits = Long.reverseBytes(Double.doubleToRawLongBits(value));
            UNSAFE.putLong(byteArray, addressOffset + index, bits);
        } else {
            UNSAFE.putDouble(byteArray, addressOffset + index, value);
        }
    }

    public double getDouble(final long index) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, SIZE_OF_DOUBLE);
        }

        return UNSAFE.getDouble(byteArray, addressOffset + index);
    }

    public void putDouble(final long index, final double value) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, SIZE_OF_DOUBLE);
        }

        UNSAFE.putDouble(byteArray, addressOffset + index, value);
    }

    ///////////////////////////////////////////////////////////////////////////

    public float getFloat(final long index, final ByteOrder byteOrder) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, SIZE_OF_FLOAT);
        }

        if (NATIVE_BYTE_ORDER != byteOrder) {
            final int bits = UNSAFE.getInt(byteArray, addressOffset + index);
            return Float.intBitsToFloat(Integer.reverseBytes(bits));
        } else {
            return UNSAFE.getFloat(byteArray, addressOffset + index);
        }
    }

    public void putFloat(final long index, final float value, final ByteOrder byteOrder) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, SIZE_OF_FLOAT);
        }

        if (NATIVE_BYTE_ORDER != byteOrder) {
            final int bits = Integer.reverseBytes(Float.floatToRawIntBits(value));
            UNSAFE.putInt(byteArray, addressOffset + index, bits);
        } else {
            UNSAFE.putFloat(byteArray, addressOffset + index, value);
        }
    }

    public float getFloat(final long index) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, SIZE_OF_FLOAT);
        }

        return UNSAFE.getFloat(byteArray, addressOffset + index);
    }

    public void putFloat(final long index, final float value) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, SIZE_OF_FLOAT);
        }

        UNSAFE.putFloat(byteArray, addressOffset + index, value);
    }

    ///////////////////////////////////////////////////////////////////////////

    public short getShort(final long index, final ByteOrder byteOrder) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, SIZE_OF_SHORT);
        }

        short bits = UNSAFE.getShort(byteArray, addressOffset + index);
        if (NATIVE_BYTE_ORDER != byteOrder) {
            bits = Short.reverseBytes(bits);
        }

        return bits;
    }

    public void putShort(final long index, final short value, final ByteOrder byteOrder) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, SIZE_OF_SHORT);
        }

        short bits = value;
        if (NATIVE_BYTE_ORDER != byteOrder) {
            bits = Short.reverseBytes(bits);
        }

        UNSAFE.putShort(byteArray, addressOffset + index, bits);
    }

    public short getShort(final long index) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, SIZE_OF_SHORT);
        }

        return UNSAFE.getShort(byteArray, addressOffset + index);
    }

    public void putShort(final long index, final short value) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, SIZE_OF_SHORT);
        }

        UNSAFE.putShort(byteArray, addressOffset + index, value);
    }

    public short getShortVolatile(final long index) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, SIZE_OF_SHORT);
        }

        return UNSAFE.getShortVolatile(byteArray, addressOffset + index);
    }

    public void putShortVolatile(final long index, final short value) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, SIZE_OF_SHORT);
        }

        UNSAFE.putShortVolatile(byteArray, addressOffset + index, value);
    }

    ///////////////////////////////////////////////////////////////////////////

    public byte getByte(final long index) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck(index);
        }

        return UNSAFE.getByte(byteArray, addressOffset + index);
    }

    public void putByte(final long index, final byte value) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck(index);
        }

        UNSAFE.putByte(byteArray, addressOffset + index, value);
    }

    public byte getByteVolatile(final long index) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck(index);
        }

        return UNSAFE.getByteVolatile(byteArray, addressOffset + index);
    }

    public void putByteVolatile(final long index, final byte value) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck(index);
        }

        UNSAFE.putByteVolatile(byteArray, addressOffset + index, value);
    }

    public void getBytes(final long index, final byte[] dst) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, dst.length);
        }

        UNSAFE.copyMemory(byteArray, addressOffset + index, dst, ARRAY_BASE_OFFSET, dst.length);
    }

    public void getBytes(final long index, final byte[] dst, final int offset, final int length) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, length);
            BufferUtil.boundsCheck(dst, offset, length);
        }

        UNSAFE.copyMemory(byteArray, addressOffset + index, dst, ARRAY_BASE_OFFSET + offset, length);
    }

    public void getBytes(final long index, final MutableDirectBuffer dstBuffer, final int dstIndex, final int length) {
        putBytesExplicit(dstBuffer, dstIndex, this, index, length);
    }

    private static void putBytesExplicit(final MutableDirectBuffer dstBuffer, final int dstIndex,
            final UnsafeMemoryBase srcBuffer, final long srcIndex, final int length) {
        if (SHOULD_BOUNDS_CHECK) {
            dstBuffer.boundsCheck(dstIndex, length);
            srcBuffer.boundsCheck(srcIndex, length);
        }

        Reflections.getUnsafe()
                .copyMemory(srcBuffer.byteArray(), srcBuffer.addressOffset() + srcIndex, dstBuffer.byteArray(),
                        dstBuffer.addressOffset() + dstIndex, length);
    }

    public void getBytes(final long index, final java.nio.ByteBuffer dstBuffer, final int length) {
        final int dstOffset = dstBuffer.position();
        getBytes(index, dstBuffer, dstOffset, length);
        ByteBuffers.position(dstBuffer, dstOffset + length);
    }

    public void getBytes(final long index, final java.nio.ByteBuffer dstBuffer, final int dstOffset, final int length) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, length);
            BufferUtil.boundsCheck(dstBuffer, dstOffset, length);
        }

        final byte[] dstByteArray;
        final long dstBaseOffset;
        if (dstBuffer.isDirect()) {
            dstByteArray = null;
            dstBaseOffset = address(dstBuffer);
        } else {
            dstByteArray = array(dstBuffer);
            dstBaseOffset = ARRAY_BASE_OFFSET + arrayOffset(dstBuffer);
        }

        Reflections.getUnsafe()
                .copyMemory(byteArray, addressOffset + index, dstByteArray, dstBaseOffset + dstOffset, length);
    }

    public void putBytes(final long index, final byte[] src) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, src.length);
        }

        UNSAFE.copyMemory(src, ARRAY_BASE_OFFSET, byteArray, addressOffset + index, src.length);
    }

    public void putBytes(final long index, final byte[] src, final int offset, final int length) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, length);
            BufferUtil.boundsCheck(src, offset, length);
        }

        UNSAFE.copyMemory(src, ARRAY_BASE_OFFSET + offset, byteArray, addressOffset + index, length);
    }

    public void putBytes(final long index, final java.nio.ByteBuffer srcBuffer, final int length) {
        final int srcIndex = srcBuffer.position();
        putBytes(index, srcBuffer, srcIndex, length);
        ByteBuffers.position(srcBuffer, srcIndex + length);
    }

    public void putBytes(final long index, final java.nio.ByteBuffer srcBuffer, final int srcIndex, final int length) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, length);
            BufferUtil.boundsCheck(srcBuffer, srcIndex, length);
        }

        final byte[] srcByteArray;
        final long srcBaseOffset;
        if (srcBuffer.isDirect()) {
            srcByteArray = null;
            srcBaseOffset = address(srcBuffer);
        } else {
            srcByteArray = array(srcBuffer);
            srcBaseOffset = ARRAY_BASE_OFFSET + arrayOffset(srcBuffer);
        }

        Reflections.getUnsafe()
                .copyMemory(srcByteArray, srcBaseOffset + srcIndex, byteArray, addressOffset + index, length);
    }

    public void putBytes(final long index, final DirectBuffer srcBuffer, final int srcIndex, final int length) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, length);
            srcBuffer.boundsCheck(srcIndex, length);
        }

        Reflections.getUnsafe()
                .copyMemory(srcBuffer.byteArray(), srcBuffer.addressOffset() + srcIndex, byteArray,
                        addressOffset + index, length);
    }

    ///////////////////////////////////////////////////////////////////////////

    public char getChar(final long index, final ByteOrder byteOrder) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, SIZE_OF_CHAR);
        }

        char bits = UNSAFE.getChar(byteArray, addressOffset + index);
        if (NATIVE_BYTE_ORDER != byteOrder) {
            bits = (char) Short.reverseBytes((short) bits);
        }

        return bits;
    }

    public void putChar(final long index, final char value, final ByteOrder byteOrder) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, SIZE_OF_CHAR);
        }

        char bits = value;
        if (NATIVE_BYTE_ORDER != byteOrder) {
            bits = (char) Short.reverseBytes((short) bits);
        }

        UNSAFE.putChar(byteArray, addressOffset + index, bits);
    }

    public char getChar(final long index) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, SIZE_OF_CHAR);
        }

        return UNSAFE.getChar(byteArray, addressOffset + index);
    }

    public void putChar(final long index, final char value) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, SIZE_OF_CHAR);
        }

        UNSAFE.putChar(byteArray, addressOffset + index, value);
    }

    public char getCharVolatile(final long index) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, SIZE_OF_CHAR);
        }

        return UNSAFE.getCharVolatile(byteArray, addressOffset + index);
    }

    public void putCharVolatile(final long index, final char value) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, SIZE_OF_CHAR);
        }

        UNSAFE.putCharVolatile(byteArray, addressOffset + index, value);
    }

    ///////////////////////////////////////////////////////////////////////////

    public String getStringAscii(final long index) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, DirectBuffer.STR_HEADER_LEN);
        }

        final int length = UNSAFE.getInt(byteArray, addressOffset + index);

        return getStringAscii(index, length);
    }

    public int getStringAscii(final long index, final Appendable appendable) {
        boundsCheck0(index, DirectBuffer.STR_HEADER_LEN);

        final int length = UNSAFE.getInt(byteArray, addressOffset + index);

        return getStringAscii(index, length, appendable);
    }

    public String getStringAscii(final long index, final ByteOrder byteOrder) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, DirectBuffer.STR_HEADER_LEN);
        }

        int bits = UNSAFE.getInt(byteArray, addressOffset + index);
        if (NATIVE_BYTE_ORDER != byteOrder) {
            bits = Integer.reverseBytes(bits);
        }

        final int length = bits;

        return getStringAscii(index, length);
    }

    public int getStringAscii(final long index, final Appendable appendable, final ByteOrder byteOrder) {
        boundsCheck0(index, DirectBuffer.STR_HEADER_LEN);

        int bits = UNSAFE.getInt(byteArray, addressOffset + index);
        if (NATIVE_BYTE_ORDER != byteOrder) {
            bits = Integer.reverseBytes(bits);
        }

        final int length = bits;

        return getStringAscii(index, length, appendable);
    }

    public String getStringAscii(final long index, final int length) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index + DirectBuffer.STR_HEADER_LEN, length);
        }

        final byte[] dst = new byte[length];
        Reflections.getUnsafe()
                .copyMemory(byteArray, addressOffset + index + DirectBuffer.STR_HEADER_LEN, dst, ARRAY_BASE_OFFSET,
                        length);

        return new String(dst, US_ASCII);
    }

    public int getStringAscii(final long index, final int length, final Appendable appendable) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, length + DirectBuffer.STR_HEADER_LEN);
        }

        try {
            for (long i = index + DirectBuffer.STR_HEADER_LEN,
                    limit = index + DirectBuffer.STR_HEADER_LEN + length; i < limit; i++) {
                final char c = (char) UNSAFE.getByte(byteArray, addressOffset + i);
                appendable.append(c > 127 ? '?' : c);
            }
        } catch (final IOException ex) {
            LangUtil.rethrowUnchecked(ex);
        }

        return length;
    }

    public int putStringAscii(final long index, final String value) {
        final int length = value != null ? value.length() : 0;

        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, length + DirectBuffer.STR_HEADER_LEN);
        }

        UNSAFE.putInt(byteArray, addressOffset + index, length);

        for (int i = 0; i < length; i++) {
            char c = value.charAt(i);
            if (c > 127) {
                c = '?';
            }

            Reflections.getUnsafe()
                    .putByte(byteArray, addressOffset + DirectBuffer.STR_HEADER_LEN + index + i, (byte) c);
        }

        return DirectBuffer.STR_HEADER_LEN + length;
    }

    public int putStringAscii(final long index, final CharSequence value) {
        final int length = value != null ? value.length() : 0;

        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, length + DirectBuffer.STR_HEADER_LEN);
        }

        UNSAFE.putInt(byteArray, addressOffset + index, length);

        for (int i = 0; i < length; i++) {
            char c = value.charAt(i);
            if (c > 127) {
                c = '?';
            }

            Reflections.getUnsafe()
                    .putByte(byteArray, addressOffset + DirectBuffer.STR_HEADER_LEN + index + i, (byte) c);
        }

        return DirectBuffer.STR_HEADER_LEN + length;
    }

    public int putStringAscii(final long index, final String value, final ByteOrder byteOrder) {
        final int length = value != null ? value.length() : 0;

        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, length + DirectBuffer.STR_HEADER_LEN);
        }

        int bits = length;
        if (NATIVE_BYTE_ORDER != byteOrder) {
            bits = Integer.reverseBytes(bits);
        }

        UNSAFE.putInt(byteArray, addressOffset + index, bits);

        for (int i = 0; i < length; i++) {
            char c = value.charAt(i);
            if (c > 127) {
                c = '?';
            }

            Reflections.getUnsafe()
                    .putByte(byteArray, addressOffset + DirectBuffer.STR_HEADER_LEN + index + i, (byte) c);
        }

        return DirectBuffer.STR_HEADER_LEN + length;
    }

    public int putStringAscii(final long index, final CharSequence value, final ByteOrder byteOrder) {
        final int length = value != null ? value.length() : 0;

        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, length + DirectBuffer.STR_HEADER_LEN);
        }

        int bits = length;
        if (NATIVE_BYTE_ORDER != byteOrder) {
            bits = Integer.reverseBytes(bits);
        }

        UNSAFE.putInt(byteArray, addressOffset + index, bits);

        for (int i = 0; i < length; i++) {
            char c = value.charAt(i);
            if (c > 127) {
                c = '?';
            }

            Reflections.getUnsafe()
                    .putByte(byteArray, addressOffset + DirectBuffer.STR_HEADER_LEN + index + i, (byte) c);
        }

        return DirectBuffer.STR_HEADER_LEN + length;
    }

    public String getStringWithoutLengthAscii(final long index, final int length) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, length);
        }

        final byte[] dst = new byte[length];
        UNSAFE.copyMemory(byteArray, addressOffset + index, dst, ARRAY_BASE_OFFSET, length);

        return new String(dst, US_ASCII);
    }

    public int getStringWithoutLengthAscii(final long index, final int length, final Appendable appendable) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, length);
        }

        try {
            for (long i = index, limit = index + length; i < limit; i++) {
                final char c = (char) UNSAFE.getByte(byteArray, addressOffset + i);
                appendable.append(c > 127 ? '?' : c);
            }
        } catch (final IOException ex) {
            LangUtil.rethrowUnchecked(ex);
        }

        return length;
    }

    public int putStringWithoutLengthAscii(final long index, final String value) {
        final int length = value != null ? value.length() : 0;

        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, length);
        }

        for (int i = 0; i < length; i++) {
            char c = value.charAt(i);
            if (c > 127) {
                c = '?';
            }

            UNSAFE.putByte(byteArray, addressOffset + index + i, (byte) c);
        }

        return length;
    }

    public int putStringWithoutLengthAscii(final long index, final CharSequence value) {
        final int length = value != null ? value.length() : 0;

        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, length);
        }

        for (int i = 0; i < length; i++) {
            char c = value.charAt(i);
            if (c > 127) {
                c = '?';
            }

            UNSAFE.putByte(byteArray, addressOffset + index + i, (byte) c);
        }

        return length;
    }

    public int putStringWithoutLengthAscii(final long index, final String value, final int valueOffset,
            final int length) {
        final int len = value != null ? Math.min(value.length() - valueOffset, length) : 0;

        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, len);
        }

        for (int i = 0; i < len; i++) {
            char c = value.charAt(valueOffset + i);
            if (c > 127) {
                c = '?';
            }

            UNSAFE.putByte(byteArray, addressOffset + index + i, (byte) c);
        }

        return len;
    }

    public int putStringWithoutLengthAscii(final long index, final CharSequence value, final int valueOffset,
            final int length) {
        final int len = value != null ? Math.min(value.length() - valueOffset, length) : 0;

        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, len);
        }

        for (int i = 0; i < len; i++) {
            char c = value.charAt(valueOffset + i);
            if (c > 127) {
                c = '?';
            }

            UNSAFE.putByte(byteArray, addressOffset + index + i, (byte) c);
        }

        return len;
    }

    ///////////////////////////////////////////////////////////////////////////

    public String getStringUtf8(final long index) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, DirectBuffer.STR_HEADER_LEN);
        }

        final int length = UNSAFE.getInt(byteArray, addressOffset + index);

        return getStringUtf8(index, length);
    }

    public String getStringUtf8(final long index, final ByteOrder byteOrder) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, DirectBuffer.STR_HEADER_LEN);
        }

        int bits = UNSAFE.getInt(byteArray, addressOffset + index);
        if (NATIVE_BYTE_ORDER != byteOrder) {
            bits = Integer.reverseBytes(bits);
        }

        final int length = bits;

        return getStringUtf8(index, length);
    }

    public String getStringUtf8(final long index, final int length) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index + DirectBuffer.STR_HEADER_LEN, length);
        }

        final byte[] stringInBytes = new byte[length];
        Reflections.getUnsafe()
                .copyMemory(byteArray, addressOffset + index + DirectBuffer.STR_HEADER_LEN, stringInBytes,
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

        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, DirectBuffer.STR_HEADER_LEN + bytes.length);
        }

        UNSAFE.putInt(byteArray, addressOffset + index, bytes.length);
        Reflections.getUnsafe()
                .copyMemory(bytes, ARRAY_BASE_OFFSET, byteArray, addressOffset + index + DirectBuffer.STR_HEADER_LEN,
                        bytes.length);

        return DirectBuffer.STR_HEADER_LEN + bytes.length;
    }

    public int putStringUtf8(final long index, final String value, final ByteOrder byteOrder,
            final int maxEncodedLength) {
        final byte[] bytes = value != null ? value.getBytes(UTF_8) : NULL_BYTES;
        if (bytes.length > maxEncodedLength) {
            throw new IllegalArgumentException("Encoded string larger than maximum size: " + maxEncodedLength);
        }

        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, DirectBuffer.STR_HEADER_LEN + bytes.length);
        }

        int bits = bytes.length;
        if (NATIVE_BYTE_ORDER != byteOrder) {
            bits = Integer.reverseBytes(bits);
        }

        UNSAFE.putInt(byteArray, addressOffset + index, bits);
        Reflections.getUnsafe()
                .copyMemory(bytes, ARRAY_BASE_OFFSET, byteArray, addressOffset + index + DirectBuffer.STR_HEADER_LEN,
                        bytes.length);

        return DirectBuffer.STR_HEADER_LEN + bytes.length;
    }

    public String getStringWithoutLengthUtf8(final long index, final int length) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, length);
        }

        final byte[] stringInBytes = new byte[length];
        UNSAFE.copyMemory(byteArray, addressOffset + index, stringInBytes, ARRAY_BASE_OFFSET, length);

        return new String(stringInBytes, UTF_8);
    }

    public int putStringWithoutLengthUtf8(final long index, final String value) {
        final byte[] bytes = value != null ? value.getBytes(UTF_8) : NULL_BYTES;
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, bytes.length);
        }

        UNSAFE.copyMemory(bytes, ARRAY_BASE_OFFSET, byteArray, addressOffset + index, bytes.length);

        return bytes.length;
    }

    ///////////////////////////////////////////////////////////////////////////

    public int parseNaturalIntAscii(final long index, final int length) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, length);
        }

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
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, length);
        }

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
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, length);
        }

        if (length <= 0) {
            throw new AsciiNumberFormatException("empty string: index=" + index + " length=" + length);
        }

        final boolean negative = MINUS_SIGN == UNSAFE.getByte(byteArray, addressOffset + index);
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
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, length);
        }

        if (length <= 0) {
            throw new AsciiNumberFormatException("empty string: index=" + index + " length=" + length);
        }

        final boolean negative = MINUS_SIGN == UNSAFE.getByte(byteArray, addressOffset + index);
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

    public int putIntAscii(final long index, final int value) {
        if (0 == value) {
            putByte(index, ZERO);
            return 1;
        }

        final byte[] dest = byteArray;
        long offset = addressOffset + index;
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

            if (SHOULD_BOUNDS_CHECK) {
                boundsCheck0(index, length);
            }

            UNSAFE.putByte(dest, offset, MINUS_SIGN);
            offset++;
        } else {
            digitCount = digitCount(quotient);
            length = digitCount;

            if (SHOULD_BOUNDS_CHECK) {
                boundsCheck0(index, length);
            }
        }

        putPositiveIntAscii(dest, offset, quotient, digitCount);

        return length;
    }

    public int putNaturalIntAscii(final long index, final int value) {
        if (0 == value) {
            putByte(index, ZERO);
            return 1;
        }

        final int digitCount = digitCount(value);

        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, digitCount);
        }

        putPositiveIntAscii(byteArray, addressOffset + index, value, digitCount);

        return digitCount;
    }

    public void putNaturalPaddedIntAscii(final int offset, final int length, final int value) {
        final int end = offset + length;
        int remainder = value;
        for (long index = end - 1; index >= offset; index--) {
            final int digit = remainder % 10;
            remainder = remainder / 10;
            putByte(index, (byte) (ZERO + digit));
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
            putByte(index, (byte) (ZERO + digit));
        }

        return index;
    }

    public int putNaturalLongAscii(final long index, final long value) {
        if (value == 0) {
            putByte(index, ZERO);
            return 1;
        }

        final int digitCount = digitCount(value);

        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, digitCount);
        }

        putPositiveLongAscii(byteArray, addressOffset + index, value, digitCount);

        return digitCount;
    }

    public int putLongAscii(final long index, final long value) {
        if (0L == value) {
            putByte(index, ZERO);
            return 1;
        }

        final byte[] dest = byteArray;
        long offset = addressOffset + index;
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

            if (SHOULD_BOUNDS_CHECK) {
                boundsCheck0(index, length);
            }

            UNSAFE.putByte(dest, offset, MINUS_SIGN);
            offset++;
        } else {
            digitCount = digitCount(quotient);
            length = digitCount;

            if (SHOULD_BOUNDS_CHECK) {
                boundsCheck0(index, length);
            }
        }

        putPositiveLongAscii(dest, offset, quotient, digitCount);

        return length;
    }

    public void boundsCheck(final long index, final long length) {
        boundsCheck0(index, length);
    }

    public long wrapAdjustment() {
        final long offset = byteArray != null ? ARRAY_BASE_OFFSET : BufferUtil.address(byteBuffer);

        return addressOffset - offset;
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

        final UnsafeMemoryBase that = (UnsafeMemoryBase) obj;

        if (capacity != that.capacity) {
            return false;
        }

        final byte[] thisByteArray = this.byteArray;
        final byte[] thatByteArray = that.byteArray;
        final long thisOffset = this.addressOffset;
        final long thatOffset = that.addressOffset;

        for (long i = 0, length = capacity; i < length; i++) {
            if (UNSAFE.getByte(thisByteArray, thisOffset + i) != Reflections.getUnsafe()
                    .getByte(thatByteArray, thatOffset + i)) {
                return false;
            }
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int hashCode = 1;

        final byte[] byteArray = this.byteArray;
        final long addressOffset = this.addressOffset;
        for (long i = 0, length = capacity; i < length; i++) {
            hashCode = 31 * hashCode + UNSAFE.getByte(byteArray, addressOffset + i);
        }

        return hashCode;
    }

    public int compareTo(final UnsafeMemoryBase that) {
        final long thisCapacity = this.capacity;
        final long thatCapacity = that.capacity();
        final byte[] thisByteArray = this.byteArray;
        final byte[] thatByteArray = that.byteArray();
        final long thisOffset = this.addressOffset;
        final long thatOffset = that.addressOffset();

        for (long i = 0, length = Math.min(thisCapacity, thatCapacity); i < length; i++) {
            final int cmp = Byte.compare(UNSAFE.getByte(thisByteArray, thisOffset + i),
                    UNSAFE.getByte(thatByteArray, thatOffset + i));

            if (0 != cmp) {
                return cmp;
            }
        }

        if (thisCapacity != thatCapacity) {
            return (int) (thisCapacity - thatCapacity);
        }

        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "UnsafeMemoryBase{" + "addressOffset=" + addressOffset + ", capacity=" + capacity + ", byteArray="
                + byteArray + // lgtm [java/print-array]
                ", byteBuffer=" + byteBuffer + '}';
    }

    private void boundsCheck(final long index) {
        if (index < 0 || index >= capacity) {
            throw new IndexOutOfBoundsException("index=" + index + " capacity=" + capacity);
        }
    }

    private void boundsCheck0(final long index, final long length) {
        final long resultingPosition = index + length;
        if (index < 0 || length < 0 || resultingPosition > capacity) {
            throw new IndexOutOfBoundsException("index=" + index + " length=" + length + " capacity=" + capacity);
        }
    }

    //CHECKSTYLE:OFF
    private int parsePositiveIntAscii(final long index, final int length, final long startIndex, final long end) {
        final long offset = addressOffset;
        final byte[] src = byteArray;
        long i = startIndex;
        int tally = 0, quartet;
        while ((end - i) >= 4 && isFourDigitsAsciiEncodedNumber(quartet = UNSAFE.getInt(src, offset + i))) {
            if (NATIVE_BYTE_ORDER != LITTLE_ENDIAN) {
                quartet = Integer.reverseBytes(quartet);
            }

            tally = (tally * 10_000) + parseFourDigitsLittleEndian(quartet);
            i += 4;
        }

        byte digit;
        while (i < end && isDigit(digit = UNSAFE.getByte(src, offset + i))) {
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

        final long offset = addressOffset;
        final byte[] src = byteArray;
        long i = startIndex;
        long tally = 0;
        long octet = UNSAFE.getLong(src, offset + i);
        if (isEightDigitAsciiEncodedNumber(octet)) {
            if (NATIVE_BYTE_ORDER != LITTLE_ENDIAN) {
                octet = Long.reverseBytes(octet);
            }
            tally = parseEightDigitsLittleEndian(octet);
            i += 8;

            byte digit;
            while (i < end && isDigit(digit = UNSAFE.getByte(src, offset + i))) {
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
        final long offset = addressOffset;
        final byte[] src = byteArray;
        long i = startIndex;
        long tally = 0, octet;
        while ((end - i) >= 8 && isEightDigitAsciiEncodedNumber(octet = UNSAFE.getLong(src, offset + i))) {
            if (NATIVE_BYTE_ORDER != LITTLE_ENDIAN) {
                octet = Long.reverseBytes(octet);
            }

            tally = (tally * 100_000_000L) + parseEightDigitsLittleEndian(octet);
            i += 8;
        }

        int quartet;
        while ((end - i) >= 4 && isFourDigitsAsciiEncodedNumber(quartet = UNSAFE.getInt(src, offset + i))) {
            if (NATIVE_BYTE_ORDER != LITTLE_ENDIAN) {
                quartet = Integer.reverseBytes(quartet);
            }

            tally = (tally * 10_000L) + parseFourDigitsLittleEndian(quartet);
            i += 4;
        }

        byte digit;
        while (i < end && isDigit(digit = UNSAFE.getByte(src, offset + i))) {
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

        final long offset = addressOffset;
        final byte[] src = byteArray;
        long i = startIndex;
        int k = 0;
        boolean checkOverflow = true;
        long tally = 0, octet;
        while ((end - i) >= 8 && isEightDigitAsciiEncodedNumber(octet = UNSAFE.getLong(src, offset + i))) {
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
        while (i < end && isDigit(digit = UNSAFE.getByte(src, offset + i))) {
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

    private static void boundsCheckWrap(final long offset, final long length, final long capacity) {
        if (offset < 0) {
            throw new IllegalArgumentException("invalid offset: " + offset);
        }

        if (length < 0) {
            throw new IllegalArgumentException("invalid length: " + length);
        }

        if ((offset > capacity - length) || (length > capacity - offset)) {
            throw new IllegalArgumentException(
                    "offset=" + offset + " length=" + length + " not valid for capacity=" + capacity);
        }
    }

    private static void putPositiveIntAscii(final byte[] dest, final long offset, final int value,
            final int digitCount) {
        int i = digitCount;
        int quotient = value;
        while (quotient >= 10_000) {
            final int lastFourDigits = quotient % 10_000;
            quotient /= 10_000;

            final int p1 = (lastFourDigits / 100) << 1;
            final int p2 = (lastFourDigits % 100) << 1;

            i -= 4;

            UNSAFE.putByte(dest, offset + i, ASCII_DIGITS[p1]);
            UNSAFE.putByte(dest, offset + i + 1, ASCII_DIGITS[p1 + 1]);
            UNSAFE.putByte(dest, offset + i + 2, ASCII_DIGITS[p2]);
            UNSAFE.putByte(dest, offset + i + 3, ASCII_DIGITS[p2 + 1]);
        }

        if (quotient >= 100) {
            final int position = (quotient % 100) << 1;
            quotient /= 100;
            UNSAFE.putByte(dest, offset + i - 1, ASCII_DIGITS[position + 1]);
            UNSAFE.putByte(dest, offset + i - 2, ASCII_DIGITS[position]);
        }

        if (quotient >= 10) {
            final int position = quotient << 1;
            UNSAFE.putByte(dest, offset + 1, ASCII_DIGITS[position + 1]);
            UNSAFE.putByte(dest, offset, ASCII_DIGITS[position]);
        } else {
            UNSAFE.putByte(dest, offset, (byte) (ZERO + quotient));
        }
    }

    private static void putPositiveLongAscii(final byte[] dest, final long offset, final long value,
            final int digitCount) {
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

            UNSAFE.putByte(dest, offset + i, ASCII_DIGITS[u1]);
            UNSAFE.putByte(dest, offset + i + 1, ASCII_DIGITS[u1 + 1]);
            UNSAFE.putByte(dest, offset + i + 2, ASCII_DIGITS[u2]);
            UNSAFE.putByte(dest, offset + i + 3, ASCII_DIGITS[u2 + 1]);
            UNSAFE.putByte(dest, offset + i + 4, ASCII_DIGITS[l1]);
            UNSAFE.putByte(dest, offset + i + 5, ASCII_DIGITS[l1 + 1]);
            UNSAFE.putByte(dest, offset + i + 6, ASCII_DIGITS[l2]);
            UNSAFE.putByte(dest, offset + i + 7, ASCII_DIGITS[l2 + 1]);
        }

        putPositiveIntAscii(dest, offset, (int) quotient, i);
    }
}
