package de.invesdwin.util.streams.buffer.memory.delegate;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import javax.annotation.concurrent.NotThreadSafe;

import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;

import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.delegate.OrderedDelegateByteBuffer;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.MemoryBuffers;
import de.invesdwin.util.streams.buffer.memory.delegate.slice.mutable.factory.IMutableSlicedDelegateMemoryBufferFactory;

/**
 * This wrapper can be used for remote communication where a fixed endianness should be used.
 */
@NotThreadSafe
public class OrderedDelegateMemoryBuffer implements IMemoryBuffer {

    protected final IMemoryBuffer delegate;
    protected final ByteOrder order;
    protected IMutableSlicedDelegateMemoryBufferFactory mutableSliceFactory;

    protected OrderedDelegateMemoryBuffer(final IMemoryBuffer delegate, final ByteOrder order) {
        this.delegate = delegate;
        this.order = order;
    }

    @Override
    public int getId() {
        return delegate.getId();
    }

    @Override
    public boolean isReadOnly() {
        return delegate.isReadOnly();
    }

    @Override
    public ByteOrder getOrder() {
        return order;
    }

    @Override
    public void putChar(final long index, final char value) {
        delegate.putCharReverse(index, value);
    }

    @Override
    public void putCharReverse(final long index, final char value) {
        delegate.putChar(index, value);
    }

    @Override
    public void putDouble(final long index, final double value) {
        delegate.putDoubleReverse(index, value);
    }

    @Override
    public void putDoubleReverse(final long index, final double value) {
        delegate.putDouble(index, value);
    }

    @Override
    public void putFloat(final long index, final float value) {
        delegate.putFloatReverse(index, value);
    }

    @Override
    public void putFloatReverse(final long index, final float value) {
        delegate.putFloat(index, value);
    }

    @Override
    public void putInt(final long index, final int value) {
        delegate.putIntReverse(index, value);
    }

    /**
     * Byte-order reversal wrapper: the barrier access stays on the delegate's volatile path while the byte
     * order flip is applied to the value, so plain and volatile encodings at an index agree in this wrapper's
     * order - parity with the plain sibling, which delegates to the delegate's Reverse method. The float and
     * double variants use the raw-bits recipe over the barrier-protected int/long primitive.
     */
    @Override
    public void putIntVolatile(final long index, final int value) {
        delegate.putIntVolatile(index, Integer.reverseBytes(value));
    }

    @Override
    public void putByteVolatile(final long index, final byte value) {
        delegate.putByteVolatile(index, value);
    }

    /**
     * Byte-order reversal wrapper: the barrier access stays on the delegate's volatile path while the byte
     * order flip is applied to the value, so plain and volatile encodings at an index agree in this wrapper's
     * order - parity with the plain sibling, which delegates to the delegate's Reverse method. The float and
     * double variants use the raw-bits recipe over the barrier-protected int/long primitive.
     */
    @Override
    public void putCharVolatile(final long index, final char value) {
        delegate.putCharVolatile(index, (char) Short.reverseBytes((short) value));
    }

    /**
     * Byte-order reversal wrapper: the barrier access stays on the delegate's volatile path while the byte
     * order flip is applied to the value, so plain and volatile encodings at an index agree in this wrapper's
     * order - parity with the plain sibling, which delegates to the delegate's Reverse method. The float and
     * double variants use the raw-bits recipe over the barrier-protected int/long primitive.
     */
    @Override
    public void putShortVolatile(final long index, final short value) {
        delegate.putShortVolatile(index, Short.reverseBytes(value));
    }

    /**
     * Byte-order reversal wrapper: the barrier access stays on the delegate's volatile path while the byte
     * order flip is applied to the value, so plain and volatile encodings at an index agree in this wrapper's
     * order - parity with the plain sibling, which delegates to the delegate's Reverse method. The float and
     * double variants use the raw-bits recipe over the barrier-protected int/long primitive.
     */
    @Override
    public void putLongVolatile(final long index, final long value) {
        delegate.putLongVolatile(index, Long.reverseBytes(value));
    }

    /**
     * Byte-order reversal wrapper: the barrier access stays on the delegate's volatile path while the byte
     * order flip is applied to the value, so plain and volatile encodings at an index agree in this wrapper's
     * order - parity with the plain sibling, which delegates to the delegate's Reverse method. The float and
     * double variants use the raw-bits recipe over the barrier-protected int/long primitive.
     */
    @Override
    public void putFloatVolatile(final long index, final float value) {
        final int bits = Integer.reverseBytes(Float.floatToRawIntBits(value));
        delegate.putFloatVolatile(index, Float.intBitsToFloat(bits));
    }

    /**
     * Byte-order reversal wrapper: the barrier access stays on the delegate's volatile path while the byte
     * order flip is applied to the value, so plain and volatile encodings at an index agree in this wrapper's
     * order - parity with the plain sibling, which delegates to the delegate's Reverse method. The float and
     * double variants use the raw-bits recipe over the barrier-protected int/long primitive.
     */
    @Override
    public void putDoubleVolatile(final long index, final double value) {
        final long bits = Long.reverseBytes(Double.doubleToRawLongBits(value));
        delegate.putDoubleVolatile(index, Double.longBitsToDouble(bits));
    }

    /**
     * Byte-order reversal wrapper: the barrier access stays on the delegate's volatile path while the byte
     * order flip is applied to the value, so plain and volatile encodings at an index agree in this wrapper's
     * order - parity with the plain sibling, which delegates to the delegate's Reverse method. The float and
     * double variants use the raw-bits recipe over the barrier-protected int/long primitive.
     */
    @Override
    public void putIntRelease(final long index, final int value) {
        delegate.putIntRelease(index, Integer.reverseBytes(value));
    }

    /**
     * Byte-order reversal wrapper: the barrier access stays on the delegate's volatile path while the byte
     * order flip is applied to the value, so plain and volatile encodings at an index agree in this wrapper's
     * order - parity with the plain sibling, which delegates to the delegate's Reverse method. The float and
     * double variants use the raw-bits recipe over the barrier-protected int/long primitive.
     */
    @Override
    public void putIntOrdered(final long index, final int value) {
        delegate.putIntOrdered(index, Integer.reverseBytes(value));
    }

    /**
     * Byte-order reversal wrapper: the barrier access stays on the delegate's volatile path while the byte
     * order flip is applied to the value, so plain and volatile encodings at an index agree in this wrapper's
     * order - parity with the plain sibling, which delegates to the delegate's Reverse method. The float and
     * double variants use the raw-bits recipe over the barrier-protected int/long primitive.
     */
    @Override
    public void putLongRelease(final long index, final long value) {
        delegate.putLongRelease(index, Long.reverseBytes(value));
    }

    /**
     * Byte-order reversal wrapper: the barrier access stays on the delegate's volatile path while the byte
     * order flip is applied to the value, so plain and volatile encodings at an index agree in this wrapper's
     * order - parity with the plain sibling, which delegates to the delegate's Reverse method. The float and
     * double variants use the raw-bits recipe over the barrier-protected int/long primitive.
     */
    @Override
    public void putLongOrdered(final long index, final long value) {
        delegate.putLongOrdered(index, Long.reverseBytes(value));
    }

    /**
     * Byte-order reversal wrapper: for the little-endian twin of a composite the wrapper
     * inversion and the twin inversion cancel each other, so the value passes through the
     * delegate's barrier-protected volatile path unswapped - parity with the plain Reverse
     * sibling, which delegates to the delegate's plain method.
     */
    @Override
    public void putCharVolatileReverse(final long index, final char value) {
        delegate.putCharVolatile(index, value);
    }

    /**
     * Byte-order reversal wrapper: for the little-endian twin of a composite the wrapper
     * inversion and the twin inversion cancel each other, so the value passes through the
     * delegate's barrier-protected volatile path unswapped - parity with the plain Reverse
     * sibling, which delegates to the delegate's plain method.
     */
    @Override
    public void putShortVolatileReverse(final long index, final short value) {
        delegate.putShortVolatile(index, value);
    }

    /**
     * Byte-order reversal wrapper: for the little-endian twin of a composite the wrapper
     * inversion and the twin inversion cancel each other, so the value passes through the
     * delegate's barrier-protected volatile path unswapped - parity with the plain Reverse
     * sibling, which delegates to the delegate's plain method.
     */
    @Override
    public void putIntVolatileReverse(final long index, final int value) {
        delegate.putIntVolatile(index, value);
    }

    /**
     * Byte-order reversal wrapper: for the little-endian twin of a composite the wrapper
     * inversion and the twin inversion cancel each other, so the value passes through the
     * delegate's barrier-protected volatile path unswapped - parity with the plain Reverse
     * sibling, which delegates to the delegate's plain method.
     */
    @Override
    public void putLongVolatileReverse(final long index, final long value) {
        delegate.putLongVolatile(index, value);
    }

    /**
     * Byte-order reversal wrapper: for the little-endian twin of a composite the wrapper
     * inversion and the twin inversion cancel each other, so the value passes through the
     * delegate's barrier-protected volatile path unswapped - parity with the plain Reverse
     * sibling, which delegates to the delegate's plain method.
     */
    @Override
    public void putFloatVolatileReverse(final long index, final float value) {
        delegate.putFloatVolatile(index, value);
    }

    /**
     * Byte-order reversal wrapper: for the little-endian twin of a composite the wrapper
     * inversion and the twin inversion cancel each other, so the value passes through the
     * delegate's barrier-protected volatile path unswapped - parity with the plain Reverse
     * sibling, which delegates to the delegate's plain method.
     */
    @Override
    public void putDoubleVolatileReverse(final long index, final double value) {
        delegate.putDoubleVolatile(index, value);
    }

    /**
     * Byte-order reversal wrapper: for the little-endian twin of a composite the wrapper
     * inversion and the twin inversion cancel each other, so the value passes through the
     * delegate's barrier-protected volatile path unswapped - parity with the plain Reverse
     * sibling, which delegates to the delegate's plain method.
     */
    @Override
    public void putIntReleaseReverse(final long index, final int value) {
        delegate.putIntRelease(index, value);
    }

    /**
     * Byte-order reversal wrapper: for the little-endian twin of a composite the wrapper
     * inversion and the twin inversion cancel each other, so the value passes through the
     * delegate's barrier-protected volatile path unswapped - parity with the plain Reverse
     * sibling, which delegates to the delegate's plain method.
     */
    @Override
    public void putLongReleaseReverse(final long index, final long value) {
        delegate.putLongRelease(index, value);
    }

    /**
     * Byte-order reversal wrapper: for the little-endian twin of a composite the wrapper
     * inversion and the twin inversion cancel each other, so the value passes through the
     * delegate's barrier-protected volatile path unswapped - parity with the plain Reverse
     * sibling, which delegates to the delegate's plain method.
     */
    @Override
    public void putIntOrderedReverse(final long index, final int value) {
        delegate.putIntOrdered(index, value);
    }

    /**
     * Byte-order reversal wrapper: for the little-endian twin of a composite the wrapper
     * inversion and the twin inversion cancel each other, so the value passes through the
     * delegate's barrier-protected volatile path unswapped - parity with the plain Reverse
     * sibling, which delegates to the delegate's plain method.
     */
    @Override
    public void putLongOrderedReverse(final long index, final long value) {
        delegate.putLongOrdered(index, value);
    }

    @Override
    public void putIntReverse(final long index, final int value) {
        delegate.putInt(index, value);
    }

    @Override
    public void putLong(final long index, final long value) {
        delegate.putLongReverse(index, value);
    }

    @Override
    public void putLongReverse(final long index, final long value) {
        delegate.putLong(index, value);
    }

    @Override
    public void putShort(final long index, final short value) {
        delegate.putShortReverse(index, value);
    }

    @Override
    public void putShortReverse(final long index, final short value) {
        delegate.putShort(index, value);
    }

    @Override
    public char getChar(final long index) {
        return delegate.getCharReverse(index);
    }

    @Override
    public char getCharReverse(final long index) {
        return delegate.getChar(index);
    }

    @Override
    public double getDouble(final long index) {
        return delegate.getDoubleReverse(index);
    }

    @Override
    public double getDoubleReverse(final long index) {
        return delegate.getDouble(index);
    }

    @Override
    public float getFloat(final long index) {
        return delegate.getFloatReverse(index);
    }

    @Override
    public float getFloatReverse(final long index) {
        return delegate.getFloat(index);
    }

    @Override
    public int getInt(final long index) {
        return delegate.getIntReverse(index);
    }

    /**
     * Byte-order reversal wrapper: the barrier access stays on the delegate's volatile path while the byte
     * order flip is applied to the value, so plain and volatile encodings at an index agree in this wrapper's
     * order - parity with the plain sibling, which delegates to the delegate's Reverse method. The float and
     * double variants use the raw-bits recipe over the barrier-protected int/long primitive.
     */
    @Override
    public int getIntVolatile(final long index) {
        return Integer.reverseBytes(delegate.getIntVolatile(index));
    }

    @Override
    public byte getByteVolatile(final long index) {
        return delegate.getByteVolatile(index);
    }

    /**
     * Byte-order reversal wrapper: the barrier access stays on the delegate's volatile path while the byte
     * order flip is applied to the value, so plain and volatile encodings at an index agree in this wrapper's
     * order - parity with the plain sibling, which delegates to the delegate's Reverse method. The float and
     * double variants use the raw-bits recipe over the barrier-protected int/long primitive.
     */
    @Override
    public char getCharVolatile(final long index) {
        return (char) Short.reverseBytes((short) delegate.getCharVolatile(index));
    }

    /**
     * Byte-order reversal wrapper: the barrier access stays on the delegate's volatile path while the byte
     * order flip is applied to the value, so plain and volatile encodings at an index agree in this wrapper's
     * order - parity with the plain sibling, which delegates to the delegate's Reverse method. The float and
     * double variants use the raw-bits recipe over the barrier-protected int/long primitive.
     */
    @Override
    public short getShortVolatile(final long index) {
        return Short.reverseBytes(delegate.getShortVolatile(index));
    }

    /**
     * Byte-order reversal wrapper: the barrier access stays on the delegate's volatile path while the byte
     * order flip is applied to the value, so plain and volatile encodings at an index agree in this wrapper's
     * order - parity with the plain sibling, which delegates to the delegate's Reverse method. The float and
     * double variants use the raw-bits recipe over the barrier-protected int/long primitive.
     */
    @Override
    public long getLongVolatile(final long index) {
        return Long.reverseBytes(delegate.getLongVolatile(index));
    }

    /**
     * Byte-order reversal wrapper: the barrier access stays on the delegate's volatile path while the byte
     * order flip is applied to the value, so plain and volatile encodings at an index agree in this wrapper's
     * order - parity with the plain sibling, which delegates to the delegate's Reverse method. The float and
     * double variants use the raw-bits recipe over the barrier-protected int/long primitive.
     */
    @Override
    public float getFloatVolatile(final long index) {
        final int bits = Float.floatToRawIntBits(delegate.getFloatVolatile(index));
        return Float.intBitsToFloat(Integer.reverseBytes(bits));
    }

    /**
     * Byte-order reversal wrapper: the barrier access stays on the delegate's volatile path while the byte
     * order flip is applied to the value, so plain and volatile encodings at an index agree in this wrapper's
     * order - parity with the plain sibling, which delegates to the delegate's Reverse method. The float and
     * double variants use the raw-bits recipe over the barrier-protected int/long primitive.
     */
    @Override
    public double getDoubleVolatile(final long index) {
        final long bits = Double.doubleToRawLongBits(delegate.getDoubleVolatile(index));
        return Double.longBitsToDouble(Long.reverseBytes(bits));
    }

    /**
     * Byte-order reversal wrapper: the barrier access stays on the delegate's volatile path while the byte
     * order flip is applied to the value, so plain and volatile encodings at an index agree in this wrapper's
     * order - parity with the plain sibling, which delegates to the delegate's Reverse method. The float and
     * double variants use the raw-bits recipe over the barrier-protected int/long primitive.
     */
    @Override
    public int getIntAcquire(final long index) {
        return Integer.reverseBytes(delegate.getIntAcquire(index));
    }

    /**
     * Byte-order reversal wrapper: the barrier access stays on the delegate's volatile path while the byte
     * order flip is applied to the value, so plain and volatile encodings at an index agree in this wrapper's
     * order - parity with the plain sibling, which delegates to the delegate's Reverse method. The float and
     * double variants use the raw-bits recipe over the barrier-protected int/long primitive.
     */
    @Override
    public long getLongAcquire(final long index) {
        return Long.reverseBytes(delegate.getLongAcquire(index));
    }

    /**
     * Byte-order reversal wrapper: for the little-endian twin of a composite the wrapper
     * inversion and the twin inversion cancel each other, so the value passes through the
     * delegate's barrier-protected volatile path unswapped - parity with the plain Reverse
     * sibling, which delegates to the delegate's plain method.
     */
    @Override
    public char getCharVolatileReverse(final long index) {
        return delegate.getCharVolatile(index);
    }

    /**
     * Byte-order reversal wrapper: for the little-endian twin of a composite the wrapper
     * inversion and the twin inversion cancel each other, so the value passes through the
     * delegate's barrier-protected volatile path unswapped - parity with the plain Reverse
     * sibling, which delegates to the delegate's plain method.
     */
    @Override
    public short getShortVolatileReverse(final long index) {
        return delegate.getShortVolatile(index);
    }

    /**
     * Byte-order reversal wrapper: for the little-endian twin of a composite the wrapper
     * inversion and the twin inversion cancel each other, so the value passes through the
     * delegate's barrier-protected volatile path unswapped - parity with the plain Reverse
     * sibling, which delegates to the delegate's plain method.
     */
    @Override
    public int getIntVolatileReverse(final long index) {
        return delegate.getIntVolatile(index);
    }

    /**
     * Byte-order reversal wrapper: for the little-endian twin of a composite the wrapper
     * inversion and the twin inversion cancel each other, so the value passes through the
     * delegate's barrier-protected volatile path unswapped - parity with the plain Reverse
     * sibling, which delegates to the delegate's plain method.
     */
    @Override
    public long getLongVolatileReverse(final long index) {
        return delegate.getLongVolatile(index);
    }

    /**
     * Byte-order reversal wrapper: for the little-endian twin of a composite the wrapper
     * inversion and the twin inversion cancel each other, so the value passes through the
     * delegate's barrier-protected volatile path unswapped - parity with the plain Reverse
     * sibling, which delegates to the delegate's plain method.
     */
    @Override
    public float getFloatVolatileReverse(final long index) {
        return delegate.getFloatVolatile(index);
    }

    /**
     * Byte-order reversal wrapper: for the little-endian twin of a composite the wrapper
     * inversion and the twin inversion cancel each other, so the value passes through the
     * delegate's barrier-protected volatile path unswapped - parity with the plain Reverse
     * sibling, which delegates to the delegate's plain method.
     */
    @Override
    public double getDoubleVolatileReverse(final long index) {
        return delegate.getDoubleVolatile(index);
    }

    /**
     * Byte-order reversal wrapper: for the little-endian twin of a composite the wrapper
     * inversion and the twin inversion cancel each other, so the value passes through the
     * delegate's barrier-protected volatile path unswapped - parity with the plain Reverse
     * sibling, which delegates to the delegate's plain method.
     */
    @Override
    public int getIntAcquireReverse(final long index) {
        return delegate.getIntAcquire(index);
    }

    /**
     * Byte-order reversal wrapper: for the little-endian twin of a composite the wrapper
     * inversion and the twin inversion cancel each other, so the value passes through the
     * delegate's barrier-protected volatile path unswapped - parity with the plain Reverse
     * sibling, which delegates to the delegate's plain method.
     */
    @Override
    public long getLongAcquireReverse(final long index) {
        return delegate.getLongAcquire(index);
    }

    @Override
    public int getIntReverse(final long index) {
        return delegate.getInt(index);
    }

    @Override
    public long getLong(final long index) {
        return delegate.getLongReverse(index);
    }

    @Override
    public long getLongReverse(final long index) {
        return delegate.getLong(index);
    }

    @Override
    public short getShort(final long index) {
        return delegate.getShortReverse(index);
    }

    @Override
    public short getShortReverse(final long index) {
        return delegate.getShort(index);
    }

    /////////////////// delegates ////////////////////////////

    @Override
    public void putByte(final long index, final byte value) {
        delegate.putByte(index, value);
    }

    @Override
    public byte getByte(final long index) {
        return delegate.getByte(index);
    }

    @Override
    public long addressOffset() {
        return delegate.addressOffset();
    }

    @Override
    public long capacity() {
        return delegate.capacity();
    }

    @Override
    public void getBytes(final long index, final byte[] dst, final int srcIndex, final int length) {
        delegate.getBytes(index, dst, srcIndex, length);
    }

    @Override
    public void getBytes(final long index, final MutableDirectBuffer dstBuffer, final int dstIndex, final int length) {
        delegate.getBytes(index, dstBuffer, dstIndex, length);
    }

    @Override
    public void getBytes(final long index, final IByteBuffer dstBuffer, final int dstIndex, final int length) {
        delegate.getBytes(index, dstBuffer, dstIndex, length);
    }

    @Override
    public void getBytes(final long index, final IMemoryBuffer dstBuffer, final long dstIndex, final long length) {
        delegate.getBytes(index, dstBuffer, dstIndex, length);
    }

    @Override
    public void getBytes(final long index, final java.nio.ByteBuffer dstBuffer, final int dstIndex, final int length) {
        delegate.getBytes(index, dstBuffer, dstIndex, length);
    }

    @Override
    public long wrapAdjustment() {
        return delegate.wrapAdjustment();
    }

    @Override
    public boolean isExpandable() {
        return delegate.isExpandable();
    }

    @Override
    public void putBytes(final long index, final byte[] src, final int srcIndex, final int length) {
        delegate.putBytes(index, src, srcIndex, length);
    }

    @Override
    public void putBytes(final long index, final java.nio.ByteBuffer srcBuffer, final int srcIndex, final int length) {
        delegate.putBytes(srcIndex, srcBuffer, srcIndex, length);
    }

    @Override
    public void putBytes(final long index, final DirectBuffer srcBuffer, final int srcIndex, final int length) {
        delegate.putBytes(srcIndex, srcBuffer, srcIndex, length);
    }

    @Override
    public void putBytes(final long index, final IByteBuffer srcBuffer, final int srcIndex, final int length) {
        delegate.putBytes(index, srcBuffer, srcIndex, length);
    }

    @Override
    public void putBytes(final long index, final IMemoryBuffer srcBuffer, final long srcIndex, final long length) {
        delegate.putBytes(index, srcBuffer, srcIndex, length);
    }

    @Override
    public InputStream asInputStream(final long index, final long length) {
        return delegate.asInputStream(index, length);
    }

    @Override
    public OutputStream asOutputStream(final long index, final long length) {
        return delegate.asOutputStream(index, length);
    }

    @Override
    public byte[] asByteArrayCopy(final long index, final int length) {
        return delegate.asByteArrayCopy(index, length);
    }

    protected IMutableSlicedDelegateMemoryBufferFactory getMutableSliceFactory() {
        if (mutableSliceFactory == null) {
            mutableSliceFactory = IMutableSlicedDelegateMemoryBufferFactory.newInstance(this, order);
        }
        return mutableSliceFactory;
    }

    @Override
    public IMemoryBuffer sliceFrom(final long index) {
        return getMutableSliceFactory().sliceFrom(index);
    }

    @Override
    public IMemoryBuffer slice(final long index, final long length) {
        return getMutableSliceFactory().slice(index, length);
    }

    @Override
    public IMemoryBuffer newSliceFrom(final long index) {
        if (index == 0) {
            return this;
        } else {
            return maybeWrap(delegate.newSliceFrom(index), order);
        }
    }

    @Override
    public IMemoryBuffer newSlice(final long index, final long length) {
        if (index == 0 && length == capacity()) {
            return this;
        } else {
            return maybeWrap(delegate.newSlice(index, length), order);
        }
    }

    public static IMemoryBuffer maybeWrap(final IMemoryBuffer buffer, final ByteOrder order) {
        if (order != buffer.getOrder()) {
            if (buffer instanceof OrderedDelegateMemoryBuffer) {
                final OrderedDelegateMemoryBuffer cBuffer = (OrderedDelegateMemoryBuffer) buffer;
                return cBuffer.delegate;
            } else {
                return new OrderedDelegateMemoryBuffer(buffer, order);
            }
        } else {
            //no conversion needed, already uses default
            return buffer;
        }
    }

    @Override
    public String getStringAsciii(final long index, final int size) {
        return delegate.getStringAsciii(index, size);
    }

    @Override
    public void getStringAsciii(final long index, final int length, final Appendable dst) {
        delegate.getStringAsciii(index, length, dst);
    }

    @Override
    public void putStringAsciii(final long index, final CharSequence value, final int valueIndex, final int length) {
        delegate.putStringAsciii(index, value, valueIndex, length);
    }

    @Override
    public String getStringUtf8(final long index, final int length) {
        return delegate.getStringUtf8(index, length);
    }

    @Override
    public int putStringUtf8(final long index, final String value) {
        return delegate.putStringUtf8(index, value);
    }

    @Override
    public void getStringUtf8(final long index, final int length, final Appendable dst) {
        delegate.getStringUtf8(index, length, dst);
    }

    @Override
    public void getBytesTo(final long index, final DataOutput dst, final long length) throws IOException {
        delegate.getBytesTo(index, dst, length);
    }

    @Override
    public void getBytesTo(final long index, final OutputStream dst, final long length) throws IOException {
        delegate.getBytesTo(index, dst, length);
    }

    @Override
    public void putBytesTo(final long index, final DataInput src, final long length) throws IOException {
        delegate.putBytesTo(index, src, length);
    }

    @Override
    public void putBytesTo(final long index, final InputStream src, final long length) throws IOException {
        delegate.putBytesTo(index, src, length);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T unwrap(final Class<T> type) {
        if (getClass().isAssignableFrom(type)) {
            return (T) this;
        }
        return delegate.unwrap(type);
    }

    @Override
    public IByteBuffer asByteBuffer(final long index, final int length) {
        return OrderedDelegateByteBuffer.maybeWrap(delegate.asByteBuffer(index, length), order);
    }

    @Override
    public MutableDirectBuffer asDirectBuffer(final long index, final int length) {
        return delegate.asDirectBuffer(index, length);
    }

    @Override
    public java.nio.ByteBuffer asNioByteBuffer(final long index, final int length) {
        return delegate.asNioByteBuffer(index, length);
    }

    @Override
    public void getBytesTo(final long index, final WritableByteChannel dst, final long length) throws IOException {
        delegate.getBytesTo(index, dst, length);
    }

    @Override
    public void putBytesTo(final long index, final ReadableByteChannel src, final long length) throws IOException {
        delegate.putBytesTo(index, src, length);
    }

    @Override
    public String toString() {
        return MemoryBuffers.toString(this);
    }

    @Override
    public IMemoryBuffer ensureCapacity(final long desiredCapacity) {
        delegate.ensureCapacity(desiredCapacity);
        return this;
    }

    @Override
    public void clear(final byte value, final long index, final long length) {
        delegate.clear(value, index, length);
    }

    @Override
    public IMemoryBuffer clone(final long index, final int length) {
        return maybeWrap(MemoryBuffers.wrap(asByteArrayCopy(index, length)), order);
    }

    @Override
    public IMemoryBuffer asImmutableSlice() {
        final IMemoryBuffer asImmutableSlice = delegate.asImmutableSlice();
        if (asImmutableSlice == delegate) {
            return this;
        } else {
            return maybeWrap(asImmutableSlice, order);
        }
    }
}
