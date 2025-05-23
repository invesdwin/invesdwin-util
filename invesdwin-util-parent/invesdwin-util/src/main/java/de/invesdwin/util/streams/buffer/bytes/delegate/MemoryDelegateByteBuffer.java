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

import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.Longs;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.delegate.slice.SlicedDelegateByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.delegate.slice.SlicedFromDelegateByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.delegate.slice.mutable.factory.IMutableSlicedDelegateByteBufferFactory;
import de.invesdwin.util.streams.buffer.bytes.extend.ArrayExpandableByteBuffer;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;

/**
 * This wrapper can be used for remote communication where a fixed endianness should be used.
 */
@NotThreadSafe
public class MemoryDelegateByteBuffer implements IByteBuffer {

    protected final IMemoryBuffer delegate;
    protected IMutableSlicedDelegateByteBufferFactory mutableSliceFactory;

    public MemoryDelegateByteBuffer(final IMemoryBuffer delegate) {
        this.delegate = delegate;
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
        return delegate.getOrder();
    }

    @Override
    public void putChar(final int index, final char value) {
        delegate.putCharReverse(index, value);
    }

    @Override
    public void putCharReverse(final int index, final char value) {
        delegate.putChar(index, value);
    }

    @Override
    public void putDouble(final int index, final double value) {
        delegate.putDoubleReverse(index, value);
    }

    @Override
    public void putDoubleReverse(final int index, final double value) {
        delegate.putDouble(index, value);
    }

    @Override
    public void putFloat(final int index, final float value) {
        delegate.putFloatReverse(index, value);
    }

    @Override
    public void putFloatReverse(final int index, final float value) {
        delegate.putFloat(index, value);
    }

    @Override
    public void putInt(final int index, final int value) {
        delegate.putIntReverse(index, value);
    }

    @Override
    public void putIntReverse(final int index, final int value) {
        delegate.putInt(index, value);
    }

    @Override
    public void putLong(final int index, final long value) {
        delegate.putLongReverse(index, value);
    }

    @Override
    public void putLongReverse(final int index, final long value) {
        delegate.putLong(index, value);
    }

    @Override
    public void putShort(final int index, final short value) {
        delegate.putShortReverse(index, value);
    }

    @Override
    public void putShortReverse(final int index, final short value) {
        delegate.putShort(index, value);
    }

    @Override
    public char getChar(final int index) {
        return delegate.getCharReverse(index);
    }

    @Override
    public char getCharReverse(final int index) {
        return delegate.getChar(index);
    }

    @Override
    public double getDouble(final int index) {
        return delegate.getDoubleReverse(index);
    }

    @Override
    public double getDoubleReverse(final int index) {
        return delegate.getDouble(index);
    }

    @Override
    public float getFloat(final int index) {
        return delegate.getFloatReverse(index);
    }

    @Override
    public float getFloatReverse(final int index) {
        return delegate.getFloat(index);
    }

    @Override
    public int getInt(final int index) {
        return delegate.getIntReverse(index);
    }

    @Override
    public int getIntReverse(final int index) {
        return delegate.getInt(index);
    }

    @Override
    public long getLong(final int index) {
        return delegate.getLongReverse(index);
    }

    @Override
    public long getLongReverse(final int index) {
        return delegate.getLong(index);
    }

    @Override
    public short getShort(final int index) {
        return delegate.getShortReverse(index);
    }

    @Override
    public short getShortReverse(final int index) {
        return delegate.getShort(index);
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
        return null;
    }

    @Override
    public MutableDirectBuffer asDirectBuffer() {
        return delegate.asDirectBuffer(0, capacity());
    }

    @Override
    public MutableDirectBuffer asDirectBuffer(final int index, final int length) {
        return delegate.asDirectBuffer(index, length);
    }

    @Override
    public IMemoryBuffer asMemoryBuffer() {
        return delegate;
    }

    @Override
    public IMemoryBuffer asMemoryBufferFrom(final int index) {
        return delegate.newSliceFrom(index);
    }

    @Override
    public IMemoryBuffer asMemoryBuffer(final int index, final int length) {
        return delegate.newSlice(index, length);
    }

    @Override
    public byte[] byteArray() {
        return null;
    }

    @Override
    public java.nio.ByteBuffer nioByteBuffer() {
        return null;
    }

    @Override
    public int capacity() {
        return (int) Longs.min(delegate.capacity(), ArrayExpandableByteBuffer.MAX_ARRAY_LENGTH);
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
        return Integers.checkedCast(delegate.wrapAdjustment());
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
        return delegate.asInputStream();
    }

    @Override
    public OutputStream asOutputStream(final int index, final int length) {
        return delegate.asOutputStream();
    }

    @Override
    public byte[] asByteArray(final int index, final int length) {
        return delegate.asByteArrayCopy(index, length);
    }

    @Override
    public byte[] asByteArrayCopy(final int index, final int length) {
        return delegate.asByteArrayCopy(index, length);
    }

    protected IMutableSlicedDelegateByteBufferFactory getMutableSliceFactory() {
        if (mutableSliceFactory == null) {
            mutableSliceFactory = IMutableSlicedDelegateByteBufferFactory.newInstance(this);
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
            return new SlicedFromDelegateByteBuffer(this, index);
        }
    }

    @Override
    public IByteBuffer newSlice(final int index, final int length) {
        if (index == 0 && length == capacity()) {
            return this;
        } else {
            return new SlicedDelegateByteBuffer(this, index, length);
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
        return ByteBuffers.wrap(asByteArrayCopy());
    }

    @Override
    public IByteBuffer clone(final int index, final int length) {
        return ByteBuffers.wrap(asByteArrayCopy(index, length));
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
        final IMemoryBuffer asImmutableSlice = delegate.asImmutableSlice();
        if (asImmutableSlice == delegate) {
            return this;
        } else {
            return new MemoryDelegateByteBuffer(asImmutableSlice);
        }
    }
}
