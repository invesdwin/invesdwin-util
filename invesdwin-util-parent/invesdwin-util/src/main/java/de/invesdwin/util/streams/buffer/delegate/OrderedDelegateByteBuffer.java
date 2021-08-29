package de.invesdwin.util.streams.buffer.delegate;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.annotation.concurrent.NotThreadSafe;

import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;

import de.invesdwin.util.streams.buffer.IByteBuffer;
import de.invesdwin.util.streams.buffer.delegate.slice.mutable.factory.IMutableSliceDelegateByteBufferFactory;

/**
 * This wrapper can be used for remote communication where a fixed endianness should be used.
 */
@NotThreadSafe
public final class OrderedDelegateByteBuffer implements IByteBuffer {

    private final IByteBuffer delegate;
    private final ByteOrder order;
    private IMutableSliceDelegateByteBufferFactory mutableSliceFactory;

    private OrderedDelegateByteBuffer(final IByteBuffer delegate, final ByteOrder order) {
        this.delegate = delegate;
        this.order = order;
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
    public void putChar(final int index, final char value) {
        final char bits = (char) Short.reverseBytes((short) value);
        delegate.putChar(index, bits);
    }

    @Override
    public void putDouble(final int index, final double value) {
        final long bits = Long.reverseBytes(Double.doubleToRawLongBits(value));
        delegate.putLong(index, bits);
    }

    @Override
    public void putFloat(final int index, final float value) {
        final int bits = Integer.reverseBytes(Float.floatToRawIntBits(value));
        delegate.putInt(index, bits);
    }

    @Override
    public void putInt(final int index, final int value) {
        final int bits = Integer.reverseBytes(value);
        delegate.putInt(index, bits);
    }

    @Override
    public void putLong(final int index, final long value) {
        final long bits = Long.reverseBytes(value);
        delegate.putLong(index, bits);
    }

    @Override
    public void putShort(final int index, final short value) {
        final short bits = Short.reverseBytes(value);
        delegate.putShort(index, bits);
    }

    @Override
    public char getChar(final int index) {
        final char bits = delegate.getChar(index);
        return (char) Short.reverseBytes((short) bits);
    }

    @Override
    public double getDouble(final int index) {
        final long bits = delegate.getLong(index);
        return Double.longBitsToDouble(Long.reverseBytes(bits));
    }

    @Override
    public float getFloat(final int index) {
        final int bits = delegate.getInt(index);
        return Float.intBitsToFloat(Integer.reverseBytes(bits));
    }

    @Override
    public int getInt(final int index) {
        final int bits = delegate.getInt(index);
        return Integer.reverseBytes(bits);
    }

    @Override
    public long getLong(final int index) {
        final long bits = delegate.getLong(index);
        return Long.reverseBytes(bits);
    }

    @Override
    public short getShort(final int index) {
        final short bits = delegate.getShort(index);
        return Short.reverseBytes(bits);
    }

    /////////////////// delegates ////////////////////////////

    @Override
    public void putByte(final int index, final byte value) {
        delegate.putByte(index, value);
    }

    @Override
    public byte getByte(final int index) {
        return delegate.getByte(index);
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
    public MutableDirectBuffer asDirectBuffer(final int index, final int length) {
        return delegate.asDirectBuffer(index, length);
    }

    @Override
    public byte[] byteArray() {
        return delegate.byteArray();
    }

    @Override
    public ByteBuffer byteBuffer() {
        return delegate.byteBuffer();
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
        delegate.getBytes(dstIndex, dstBuffer, dstIndex, length);
    }

    @Override
    public void getBytes(final int index, final ByteBuffer dstBuffer, final int dstIndex, final int length) {
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
    public void putBytes(final int index, final ByteBuffer srcBuffer, final int srcIndex, final int length) {
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
    public InputStream asInputStream(final int index, final int length) {
        return delegate.asInputStream();
    }

    @Override
    public OutputStream asOutputStream(final int index, final int length) {
        return delegate.asOutputStream();
    }

    @Override
    public byte[] asByteArray(final int index, final int length) {
        return delegate.asByteArray(index, length);
    }

    @Override
    public byte[] asByteArrayCopy(final int index, final int length) {
        return delegate.asByteArrayCopy(index, length);
    }

    private IMutableSliceDelegateByteBufferFactory getMutableSliceFactory() {
        if (mutableSliceFactory == null) {
            mutableSliceFactory = IMutableSliceDelegateByteBufferFactory.newInstance(this, order);
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
        return maybeWrap(delegate.newSliceFrom(index), order);
    }

    @Override
    public IByteBuffer newSlice(final int index, final int length) {
        return maybeWrap(delegate.newSlice(index, length), order);
    }

    public static IByteBuffer maybeWrap(final IByteBuffer buffer, final ByteOrder order) {
        if (order != buffer.getOrder()) {
            return new OrderedDelegateByteBuffer(buffer, order);
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

}
