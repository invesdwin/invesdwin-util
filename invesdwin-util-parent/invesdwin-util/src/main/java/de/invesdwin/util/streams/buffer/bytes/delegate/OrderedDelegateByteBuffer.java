package de.invesdwin.util.streams.buffer.bytes.delegate;

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

import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.delegate.slice.mutable.factory.IMutableSlicedDelegateByteBufferFactory;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.delegate.OrderedDelegateMemoryBuffer;

/**
 * This wrapper can be used for remote communication where a fixed endianness should be used.
 */
@NotThreadSafe
public class OrderedDelegateByteBuffer implements IByteBuffer {

    protected final IByteBuffer delegate;
    protected final ByteOrder order;
    protected IMutableSlicedDelegateByteBufferFactory mutableSliceFactory;

    protected OrderedDelegateByteBuffer(final IByteBuffer delegate, final ByteOrder order) {
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































































    /////////////////// delegates ////////////////////////////


    @Override
    public byte getByte(final int index) {
        return delegate.getByte(index);
    }
    @Override
    public byte getByteVolatile(final int index) {
        return delegate.getByteVolatile(index);
    }
    @Override
    public void putByte(final int index, final byte value) {
        delegate.putByte(index, value);
    }
    @Override
    public void putByteVolatile(final int index, final byte value) {
        delegate.putByteVolatile(index, value);
    }
    @Override
    public char getChar(final int index) {
        return delegate.getCharReverse(index);
    }
    @Override
    public char getCharReverse(final int index) {
        return delegate.getChar(index);
    }
    /**
     * Byte-order reversal wrapper: the barrier access stays on the delegate's volatile path while the byte
     * order flip is applied to the value, so plain and volatile encodings at an index agree in this wrapper's
     * order - parity with the plain sibling, which delegates to the delegate's Reverse method. The float and
     * double variants use the raw-bits recipe over the barrier-protected int/long primitive.
     */
    @Override
    public char getCharVolatile(final int index) {
        return (char) Short.reverseBytes((short) delegate.getCharVolatile(index));
    }
    @Override
    public char getCharVolatileReverse(final int index) {
        return delegate.getCharVolatile(index);
    }
    @Override
    public void putChar(final int index, final char value) {
        delegate.putCharReverse(index, value);
    }
    @Override
    public void putCharReverse(final int index, final char value) {
        delegate.putChar(index, value);
    }
    /**
     * Byte-order reversal wrapper: the barrier access stays on the delegate's volatile path while the byte
     * order flip is applied to the value, so plain and volatile encodings at an index agree in this wrapper's
     * order - parity with the plain sibling, which delegates to the delegate's Reverse method. The float and
     * double variants use the raw-bits recipe over the barrier-protected int/long primitive.
     */
    @Override
    public void putCharVolatile(final int index, final char value) {
        delegate.putCharVolatile(index, (char) Short.reverseBytes((short) value));
    }
    @Override
    public void putCharVolatileReverse(final int index, final char value) {
        delegate.putCharVolatile(index, value);
    }
    @Override
    public short getShort(final int index) {
        return delegate.getShortReverse(index);
    }
    @Override
    public short getShortReverse(final int index) {
        return delegate.getShort(index);
    }
    /**
     * Byte-order reversal wrapper: the barrier access stays on the delegate's volatile path while the byte
     * order flip is applied to the value, so plain and volatile encodings at an index agree in this wrapper's
     * order - parity with the plain sibling, which delegates to the delegate's Reverse method. The float and
     * double variants use the raw-bits recipe over the barrier-protected int/long primitive.
     */
    @Override
    public short getShortVolatile(final int index) {
        return Short.reverseBytes(delegate.getShortVolatile(index));
    }
    @Override
    public short getShortVolatileReverse(final int index) {
        return delegate.getShortVolatile(index);
    }
    @Override
    public void putShort(final int index, final short value) {
        delegate.putShortReverse(index, value);
    }
    @Override
    public void putShortReverse(final int index, final short value) {
        delegate.putShort(index, value);
    }
    /**
     * Byte-order reversal wrapper: the barrier access stays on the delegate's volatile path while the byte
     * order flip is applied to the value, so plain and volatile encodings at an index agree in this wrapper's
     * order - parity with the plain sibling, which delegates to the delegate's Reverse method. The float and
     * double variants use the raw-bits recipe over the barrier-protected int/long primitive.
     */
    @Override
    public void putShortVolatile(final int index, final short value) {
        delegate.putShortVolatile(index, Short.reverseBytes(value));
    }
    @Override
    public void putShortVolatileReverse(final int index, final short value) {
        delegate.putShortVolatile(index, value);
    }
    @Override
    public int getInt(final int index) {
        return delegate.getIntReverse(index);
    }
    @Override
    public int getIntReverse(final int index) {
        return delegate.getInt(index);
    }
    /**
     * Byte-order reversal wrapper: the barrier access stays on the delegate's volatile path while the byte
     * order flip is applied to the value, so plain and volatile encodings at an index agree in this wrapper's
     * order - parity with the plain sibling, which delegates to the delegate's Reverse method. The float and
     * double variants use the raw-bits recipe over the barrier-protected int/long primitive.
     */
    @Override
    public int getIntVolatile(final int index) {
        return Integer.reverseBytes(delegate.getIntVolatile(index));
    }
    @Override
    public int getIntVolatileReverse(final int index) {
        return delegate.getIntVolatile(index);
    }
    /**
     * Byte-order reversal wrapper: the barrier access stays on the delegate's volatile path while the byte
     * order flip is applied to the value, so plain and volatile encodings at an index agree in this wrapper's
     * order - parity with the plain sibling, which delegates to the delegate's Reverse method. The float and
     * double variants use the raw-bits recipe over the barrier-protected int/long primitive.
     */
    @Override
    public int getIntAcquire(final int index) {
        return Integer.reverseBytes(delegate.getIntAcquire(index));
    }
    @Override
    public int getIntAcquireReverse(final int index) {
        return delegate.getIntAcquire(index);
    }
    @Override
    public void putInt(final int index, final int value) {
        delegate.putIntReverse(index, value);
    }
    @Override
    public void putIntReverse(final int index, final int value) {
        delegate.putInt(index, value);
    }
    /**
     * Byte-order reversal wrapper: the barrier access stays on the delegate's volatile path while the byte
     * order flip is applied to the value, so plain and volatile encodings at an index agree in this wrapper's
     * order - parity with the plain sibling, which delegates to the delegate's Reverse method. The float and
     * double variants use the raw-bits recipe over the barrier-protected int/long primitive.
     */
    @Override
    public void putIntVolatile(final int index, final int value) {
        delegate.putIntVolatile(index, Integer.reverseBytes(value));
    }
    @Override
    public void putIntVolatileReverse(final int index, final int value) {
        delegate.putIntVolatile(index, value);
    }
    /**
     * Byte-order reversal wrapper: the barrier access stays on the delegate's volatile path while the byte
     * order flip is applied to the value, so plain and volatile encodings at an index agree in this wrapper's
     * order - parity with the plain sibling, which delegates to the delegate's Reverse method. The float and
     * double variants use the raw-bits recipe over the barrier-protected int/long primitive.
     */
    @Override
    public void putIntRelease(final int index, final int value) {
        delegate.putIntRelease(index, Integer.reverseBytes(value));
    }
    @Override
    public void putIntReleaseReverse(final int index, final int value) {
        delegate.putIntRelease(index, value);
    }
    /**
     * Byte-order reversal wrapper: the barrier access stays on the delegate's volatile path while the byte
     * order flip is applied to the value, so plain and volatile encodings at an index agree in this wrapper's
     * order - parity with the plain sibling, which delegates to the delegate's Reverse method. The float and
     * double variants use the raw-bits recipe over the barrier-protected int/long primitive.
     */
    @Override
    public void putIntOrdered(final int index, final int value) {
        delegate.putIntOrdered(index, Integer.reverseBytes(value));
    }
    @Override
    public void putIntOrderedReverse(final int index, final int value) {
        delegate.putIntOrdered(index, value);
    }
    @Override
    public long getLong(final int index) {
        return delegate.getLongReverse(index);
    }
    @Override
    public long getLongReverse(final int index) {
        return delegate.getLong(index);
    }
    /**
     * Byte-order reversal wrapper: the barrier access stays on the delegate's volatile path while the byte
     * order flip is applied to the value, so plain and volatile encodings at an index agree in this wrapper's
     * order - parity with the plain sibling, which delegates to the delegate's Reverse method. The float and
     * double variants use the raw-bits recipe over the barrier-protected int/long primitive.
     */
    @Override
    public long getLongVolatile(final int index) {
        return Long.reverseBytes(delegate.getLongVolatile(index));
    }
    @Override
    public long getLongVolatileReverse(final int index) {
        return delegate.getLongVolatile(index);
    }
    /**
     * Byte-order reversal wrapper: the barrier access stays on the delegate's volatile path while the byte
     * order flip is applied to the value, so plain and volatile encodings at an index agree in this wrapper's
     * order - parity with the plain sibling, which delegates to the delegate's Reverse method. The float and
     * double variants use the raw-bits recipe over the barrier-protected int/long primitive.
     */
    @Override
    public long getLongAcquire(final int index) {
        return Long.reverseBytes(delegate.getLongAcquire(index));
    }
    @Override
    public long getLongAcquireReverse(final int index) {
        return delegate.getLongAcquire(index);
    }
    @Override
    public void putLong(final int index, final long value) {
        delegate.putLongReverse(index, value);
    }
    @Override
    public void putLongReverse(final int index, final long value) {
        delegate.putLong(index, value);
    }
    /**
     * Byte-order reversal wrapper: the barrier access stays on the delegate's volatile path while the byte
     * order flip is applied to the value, so plain and volatile encodings at an index agree in this wrapper's
     * order - parity with the plain sibling, which delegates to the delegate's Reverse method. The float and
     * double variants use the raw-bits recipe over the barrier-protected int/long primitive.
     */
    @Override
    public void putLongVolatile(final int index, final long value) {
        delegate.putLongVolatile(index, Long.reverseBytes(value));
    }
    @Override
    public void putLongVolatileReverse(final int index, final long value) {
        delegate.putLongVolatile(index, value);
    }
    /**
     * Byte-order reversal wrapper: the barrier access stays on the delegate's volatile path while the byte
     * order flip is applied to the value, so plain and volatile encodings at an index agree in this wrapper's
     * order - parity with the plain sibling, which delegates to the delegate's Reverse method. The float and
     * double variants use the raw-bits recipe over the barrier-protected int/long primitive.
     */
    @Override
    public void putLongRelease(final int index, final long value) {
        delegate.putLongRelease(index, Long.reverseBytes(value));
    }
    @Override
    public void putLongReleaseReverse(final int index, final long value) {
        delegate.putLongRelease(index, value);
    }
    /**
     * Byte-order reversal wrapper: the barrier access stays on the delegate's volatile path while the byte
     * order flip is applied to the value, so plain and volatile encodings at an index agree in this wrapper's
     * order - parity with the plain sibling, which delegates to the delegate's Reverse method. The float and
     * double variants use the raw-bits recipe over the barrier-protected int/long primitive.
     */
    @Override
    public void putLongOrdered(final int index, final long value) {
        delegate.putLongOrdered(index, Long.reverseBytes(value));
    }
    @Override
    public void putLongOrderedReverse(final int index, final long value) {
        delegate.putLongOrdered(index, value);
    }
    @Override
    public float getFloat(final int index) {
        return delegate.getFloatReverse(index);
    }
    @Override
    public float getFloatReverse(final int index) {
        return delegate.getFloat(index);
    }
    /**
     * Byte-order reversal wrapper: the barrier access stays on the delegate's volatile path while the byte
     * order flip is applied to the value, so plain and volatile encodings at an index agree in this wrapper's
     * order - parity with the plain sibling, which delegates to the delegate's Reverse method. The float and
     * double variants use the raw-bits recipe over the barrier-protected int/long primitive.
     */
    @Override
    public float getFloatVolatile(final int index) {
        final int bits = Float.floatToRawIntBits(delegate.getFloatVolatile(index));
        return Float.intBitsToFloat(Integer.reverseBytes(bits));
    }
    @Override
    public float getFloatVolatileReverse(final int index) {
        return delegate.getFloatVolatile(index);
    }
    @Override
    public void putFloat(final int index, final float value) {
        delegate.putFloatReverse(index, value);
    }
    @Override
    public void putFloatReverse(final int index, final float value) {
        delegate.putFloat(index, value);
    }
    /**
     * Byte-order reversal wrapper: the barrier access stays on the delegate's volatile path while the byte
     * order flip is applied to the value, so plain and volatile encodings at an index agree in this wrapper's
     * order - parity with the plain sibling, which delegates to the delegate's Reverse method. The float and
     * double variants use the raw-bits recipe over the barrier-protected int/long primitive.
     */
    @Override
    public void putFloatVolatile(final int index, final float value) {
        final int bits = Integer.reverseBytes(Float.floatToRawIntBits(value));
        delegate.putFloatVolatile(index, Float.intBitsToFloat(bits));
    }
    @Override
    public void putFloatVolatileReverse(final int index, final float value) {
        delegate.putFloatVolatile(index, value);
    }
    @Override
    public double getDouble(final int index) {
        return delegate.getDoubleReverse(index);
    }
    @Override
    public double getDoubleReverse(final int index) {
        return delegate.getDouble(index);
    }
    /**
     * Byte-order reversal wrapper: the barrier access stays on the delegate's volatile path while the byte
     * order flip is applied to the value, so plain and volatile encodings at an index agree in this wrapper's
     * order - parity with the plain sibling, which delegates to the delegate's Reverse method. The float and
     * double variants use the raw-bits recipe over the barrier-protected int/long primitive.
     */
    @Override
    public double getDoubleVolatile(final int index) {
        final long bits = Double.doubleToRawLongBits(delegate.getDoubleVolatile(index));
        return Double.longBitsToDouble(Long.reverseBytes(bits));
    }
    @Override
    public double getDoubleVolatileReverse(final int index) {
        return delegate.getDoubleVolatile(index);
    }
    @Override
    public void putDouble(final int index, final double value) {
        delegate.putDoubleReverse(index, value);
    }
    @Override
    public void putDoubleReverse(final int index, final double value) {
        delegate.putDouble(index, value);
    }
    /**
     * Byte-order reversal wrapper: the barrier access stays on the delegate's volatile path while the byte
     * order flip is applied to the value, so plain and volatile encodings at an index agree in this wrapper's
     * order - parity with the plain sibling, which delegates to the delegate's Reverse method. The float and
     * double variants use the raw-bits recipe over the barrier-protected int/long primitive.
     */
    @Override
    public void putDoubleVolatile(final int index, final double value) {
        final long bits = Long.reverseBytes(Double.doubleToRawLongBits(value));
        delegate.putDoubleVolatile(index, Double.longBitsToDouble(bits));
    }
    @Override
    public void putDoubleVolatileReverse(final int index, final double value) {
        delegate.putDoubleVolatile(index, value);
    }

    @Override
    public long addressOffset() {
        return delegate.addressOffset();
    }

    @Override
    public MutableDirectBuffer directBuffer() {
        return delegate.directBuffer();
    }

    @Override
    public MutableDirectBuffer asDirectBuffer() {
        return delegate.asDirectBuffer();
    }

    @Override
    public MutableDirectBuffer asDirectBuffer(final int index, final int length) {
        return delegate.asDirectBuffer(index, length);
    }

    @Override
    public IMemoryBuffer asMemoryBuffer() {
        return OrderedDelegateMemoryBuffer.maybeWrap(delegate.asMemoryBuffer(), order);
    }

    @Override
    public IMemoryBuffer asMemoryBufferFrom(final int index) {
        return OrderedDelegateMemoryBuffer.maybeWrap(delegate.asMemoryBufferFrom(index), order);
    }

    @Override
    public IMemoryBuffer asMemoryBuffer(final int index, final int length) {
        return OrderedDelegateMemoryBuffer.maybeWrap(delegate.asMemoryBuffer(index, length), order);
    }

    @Override
    public byte[] byteArray() {
        return delegate.byteArray();
    }

    @Override
    public java.nio.ByteBuffer nioByteBuffer() {
        return delegate.nioByteBuffer();
    }

    @Override
    public int capacity() {
        return delegate.capacity();
    }

    @Override
    public void getBytes(final int index, final byte[] dst, final int srcIndex, final int length) {
        delegate.getBytes(index, dst, srcIndex, length);
    }

    @Override
    public void getBytes(final int index, final MutableDirectBuffer dstBuffer, final int dstIndex, final int length) {
        delegate.getBytes(index, dstBuffer, dstIndex, length);
    }

    @Override
    public void getBytes(final int index, final IByteBuffer dstBuffer, final int dstIndex, final int length) {
        delegate.getBytes(index, dstBuffer, dstIndex, length);
    }

    @Override
    public void getBytes(final int index, final IMemoryBuffer dstBuffer, final long dstIndex, final int length) {
        delegate.getBytes(index, dstBuffer, dstIndex, length);
    }

    @Override
    public void getBytes(final int index, final java.nio.ByteBuffer dstBuffer, final int dstIndex, final int length) {
        delegate.getBytes(index, dstBuffer, dstIndex, length);
    }

    @Override
    public int wrapAdjustment() {
        return delegate.wrapAdjustment();
    }

    @Override
    public boolean isExpandable() {
        return delegate.isExpandable();
    }

    @Override
    public void putBytes(final int index, final byte[] src, final int srcIndex, final int length) {
        delegate.putBytes(index, src, srcIndex, length);
    }

    @Override
    public void putBytes(final int index, final java.nio.ByteBuffer srcBuffer, final int srcIndex, final int length) {
        delegate.putBytes(srcIndex, srcBuffer, srcIndex, length);
    }

    @Override
    public void putBytes(final int index, final DirectBuffer srcBuffer, final int srcIndex, final int length) {
        delegate.putBytes(srcIndex, srcBuffer, srcIndex, length);
    }

    @Override
    public void putBytes(final int index, final IByteBuffer srcBuffer, final int srcIndex, final int length) {
        delegate.putBytes(index, srcBuffer, srcIndex, length);
    }

    @Override
    public void putBytes(final int index, final IMemoryBuffer srcBuffer, final long srcIndex, final int length) {
        delegate.putBytes(index, srcBuffer, srcIndex, length);
    }

    @Override
    public InputStream asInputStream(final int index, final int length) {
        return delegate.asInputStream(index, length);
    }

    @Override
    public OutputStream asOutputStream(final int index, final int length) {
        return delegate.asOutputStream(index, length);
    }

    @Override
    public byte[] asByteArray(final int index, final int length) {
        return delegate.asByteArray(index, length);
    }

    @Override
    public byte[] asByteArrayCopy(final int index, final int length) {
        return delegate.asByteArrayCopy(index, length);
    }

    protected IMutableSlicedDelegateByteBufferFactory getMutableSliceFactory() {
        if (mutableSliceFactory == null) {
            mutableSliceFactory = IMutableSlicedDelegateByteBufferFactory.newInstance(this, order);
        }
        return mutableSliceFactory;
    }

    @Override
    public IByteBuffer sliceFrom(final int index) {
        return getMutableSliceFactory().sliceFrom(index);
    }

    @Override
    public IByteBuffer slice(final int index, final int length) {
        return getMutableSliceFactory().slice(index, length);
    }

    @Override
    public IByteBuffer newSliceFrom(final int index) {
        if (index == 0) {
            return this;
        } else {
            return maybeWrap(delegate.newSliceFrom(index), order);
        }
    }

    @Override
    public IByteBuffer newSlice(final int index, final int length) {
        if (index == 0 && length == capacity()) {
            return this;
        } else {
            return maybeWrap(delegate.newSlice(index, length), order);
        }
    }

    public static IByteBuffer maybeWrap(final IByteBuffer buffer, final ByteOrder order) {
        if (order != buffer.getOrder()) {
            if (buffer instanceof OrderedDelegateByteBuffer) {
                final OrderedDelegateByteBuffer cBuffer = (OrderedDelegateByteBuffer) buffer;
                return cBuffer.delegate;
            } else {
                return new OrderedDelegateByteBuffer(buffer, order);
            }
        } else {
            //no conversion needed, already uses default
            return buffer;
        }
    }

    @Override
    public String getStringAsciii(final int index, final int size) {
        return delegate.getStringAsciii(index, size);
    }

    @Override
    public void getStringAsciii(final int index, final int length, final Appendable dst) {
        delegate.getStringAsciii(index, length, dst);
    }

    @Override
    public void putStringAsciii(final int index, final CharSequence value, final int valueIndex, final int length) {
        delegate.putStringAsciii(index, value, valueIndex, length);
    }

    @Override
    public String getStringUtf8(final int index, final int length) {
        return delegate.getStringUtf8(index, length);
    }

    @Override
    public int putStringUtf8(final int index, final String value) {
        return delegate.putStringUtf8(index, value);
    }

    @Override
    public void getStringUtf8(final int index, final int length, final Appendable dst) {
        delegate.getStringUtf8(index, length, dst);
    }

    @Override
    public void getBytesTo(final int index, final DataOutput dst, final int length) throws IOException {
        delegate.getBytesTo(index, dst, length);
    }

    @Override
    public void getBytesTo(final int index, final OutputStream dst, final int length) throws IOException {
        delegate.getBytesTo(index, dst, length);
    }

    @Override
    public void putBytesTo(final int index, final DataInput src, final int length) throws IOException {
        delegate.putBytesTo(index, src, length);
    }

    @Override
    public void putBytesTo(final int index, final InputStream src, final int length) throws IOException {
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
    public java.nio.ByteBuffer asNioByteBuffer(final int index, final int length) {
        return delegate.asNioByteBuffer(index, length);
    }

    @Override
    public void getBytesTo(final int index, final WritableByteChannel dst, final int length) throws IOException {
        delegate.getBytesTo(index, dst, length);
    }

    @Override
    public void putBytesTo(final int index, final ReadableByteChannel src, final int length) throws IOException {
        delegate.putBytesTo(index, src, length);
    }

    @Override
    public String toString() {
        return ByteBuffers.toString(this);
    }

    //CHECKSTYLE:OFF
    @Override
    public IByteBuffer clone() {
        //CHECKSTYLE:ON
        return maybeWrap(delegate.clone(), order);
    }

    @Override
    public IByteBuffer clone(final int index, final int length) {
        return maybeWrap(delegate.clone(index, length), order);
    }

    @Override
    public IByteBuffer ensureCapacity(final int desiredCapacity) {
        delegate.ensureCapacity(desiredCapacity);
        return this;
    }

    @Override
    public void clear(final byte value, final int index, final int length) {
        delegate.clear(value, index, length);
    }

    @Override
    public IByteBuffer asImmutableSlice() {
        final IByteBuffer asImmutableSlice = delegate.asImmutableSlice();
        if (asImmutableSlice == delegate) {
            return this;
        } else {
            return maybeWrap(asImmutableSlice, order);
        }
    }
}
