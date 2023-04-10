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

import de.invesdwin.util.math.Integers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.MemoryBuffers;
import de.invesdwin.util.streams.buffer.memory.delegate.slice.SlicedDelegateMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.delegate.slice.SlicedFromDelegateMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.delegate.slice.mutable.factory.FixedMutableSlicedDelegateMemoryBufferFactory;
import de.invesdwin.util.streams.buffer.memory.delegate.slice.mutable.factory.IMutableSlicedDelegateMemoryBufferFactory;

@NotThreadSafe
public class ByteDelegateMemoryBuffer implements IMemoryBuffer {

    protected IByteBuffer delegate;
    protected IMutableSlicedDelegateMemoryBufferFactory mutableSliceFactory;

    public ByteDelegateMemoryBuffer(final IByteBuffer buffer) {
        setDelegate(buffer);
    }

    public IByteBuffer getDelegate() {
        return delegate;
    }

    public void setDelegate(final IByteBuffer delegate) {
        this.delegate = delegate;
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
    public long addressOffset() {
        return delegate.addressOffset();
    }

    @Override
    public long capacity() {
        return delegate.capacity();
    }

    @Override
    public long getLong(final long index) {
        return delegate.getLong(Integers.checkedCast(index));
    }

    @Override
    public int getInt(final long index) {
        return delegate.getInt(Integers.checkedCast(index));
    }

    @Override
    public double getDouble(final long index) {
        return delegate.getDouble(Integers.checkedCast(index));
    }

    @Override
    public float getFloat(final long index) {
        return delegate.getFloat(Integers.checkedCast(index));
    }

    @Override
    public short getShort(final long index) {
        return delegate.getShort(Integers.checkedCast(index));
    }

    @Override
    public char getChar(final long index) {
        return delegate.getChar(Integers.checkedCast(index));
    }

    @Override
    public long getLongReverse(final long index) {
        return delegate.getLongReverse(Integers.checkedCast(index));
    }

    @Override
    public int getIntReverse(final long index) {
        return delegate.getIntReverse(Integers.checkedCast(index));
    }

    @Override
    public double getDoubleReverse(final long index) {
        return delegate.getDoubleReverse(Integers.checkedCast(index));
    }

    @Override
    public float getFloatReverse(final long index) {
        return delegate.getFloatReverse(Integers.checkedCast(index));
    }

    @Override
    public short getShortReverse(final long index) {
        return delegate.getShortReverse(Integers.checkedCast(index));
    }

    @Override
    public char getCharReverse(final long index) {
        return delegate.getCharReverse(Integers.checkedCast(index));
    }

    @Override
    public byte getByte(final long index) {
        return delegate.getByte(Integers.checkedCast(index));
    }

    @Override
    public void getBytes(final long index, final byte[] dst, final int dstIndex, final int length) {
        delegate.getBytes(Integers.checkedCast(index), dst, dstIndex, length);
    }

    @Override
    public void getBytes(final long index, final MutableDirectBuffer dstBuffer, final int dstIndex, final int length) {
        delegate.getBytes(Integers.checkedCast(index), dstBuffer, dstIndex, length);
    }

    @Override
    public void getBytes(final long index, final IByteBuffer dstBuffer, final int dstIndex, final int length) {
        delegate.getBytes(Integers.checkedCast(index), dstBuffer, dstIndex, length);
    }

    @Override
    public void getBytes(final long index, final IMemoryBuffer dstBuffer, final long dstIndex, final long length) {
        delegate.getBytes(Integers.checkedCast(index), dstBuffer, dstIndex, Integers.checkedCast(length));
    }

    @Override
    public void getBytes(final long index, final java.nio.ByteBuffer dstBuffer, final int dstIndex, final int length) {
        delegate.getBytes(Integers.checkedCast(index), dstBuffer, dstIndex, length);
    }

    @Override
    public MutableDirectBuffer asDirectBuffer(final long index, final int length) {
        return delegate.asDirectBuffer(Integers.checkedCast(index), length);
    }

    @Override
    public IByteBuffer asByteBuffer(final long index, final int length) {
        return delegate.newSlice(Integers.checkedCast(index), length);
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
    public void putLong(final long index, final long value) {
        delegate.putLong(Integers.checkedCast(index), value);
    }

    @Override
    public void putInt(final long index, final int value) {
        delegate.putInt(Integers.checkedCast(index), value);
    }

    @Override
    public void putDouble(final long index, final double value) {
        delegate.putDouble(Integers.checkedCast(index), value);
    }

    @Override
    public void putFloat(final long index, final float value) {
        delegate.putFloat(Integers.checkedCast(index), value);
    }

    @Override
    public void putShort(final long index, final short value) {
        delegate.putShort(Integers.checkedCast(index), value);
    }

    @Override
    public void putChar(final long index, final char value) {
        delegate.putChar(Integers.checkedCast(index), value);
    }

    @Override
    public void putLongReverse(final long index, final long value) {
        delegate.putLongReverse(Integers.checkedCast(index), value);
    }

    @Override
    public void putIntReverse(final long index, final int value) {
        delegate.putIntReverse(Integers.checkedCast(index), value);
    }

    @Override
    public void putDoubleReverse(final long index, final double value) {
        delegate.putDoubleReverse(Integers.checkedCast(index), value);
    }

    @Override
    public void putFloatReverse(final long index, final float value) {
        delegate.putFloatReverse(Integers.checkedCast(index), value);
    }

    @Override
    public void putShortReverse(final long index, final short value) {
        delegate.putShortReverse(Integers.checkedCast(index), value);
    }

    @Override
    public void putCharReverse(final long index, final char value) {
        delegate.putCharReverse(Integers.checkedCast(index), value);
    }

    @Override
    public void putByte(final long index, final byte value) {
        delegate.putByte(Integers.checkedCast(index), value);
    }

    @Override
    public void putBytes(final long index, final byte[] src, final int srcIndex, final int length) {
        delegate.putBytes(Integers.checkedCast(index), src, srcIndex, length);
    }

    @Override
    public void putBytes(final long index, final java.nio.ByteBuffer srcBuffer, final int srcIndex, final int length) {
        delegate.putBytes(Integers.checkedCast(index), srcBuffer, srcIndex, length);
    }

    @Override
    public void putBytes(final long index, final DirectBuffer srcBuffer, final int srcIndex, final int length) {
        delegate.putBytes(Integers.checkedCast(index), srcBuffer, srcIndex, length);
    }

    @Override
    public void putBytes(final long index, final IByteBuffer srcBuffer, final int srcIndex, final int length) {
        delegate.putBytes(Integers.checkedCast(index), srcBuffer, srcIndex, length);
    }

    @Override
    public void putBytes(final long index, final IMemoryBuffer srcBuffer, final long srcIndex, final long length) {
        delegate.putBytes(Integers.checkedCast(index), srcBuffer, srcIndex, Integers.checkedCast(length));
    }

    @Override
    public InputStream asInputStream(final long index, final long length) {
        return delegate.asInputStream(Integers.checkedCast(index), Integers.checkedCast(length));
    }

    @Override
    public OutputStream asOutputStream(final long index, final long length) {
        return delegate.asOutputStream(Integers.checkedCast(index), Integers.checkedCast(length));
    }

    @Override
    public byte[] asByteArrayCopy(final long index, final int length) {
        return delegate.asByteArrayCopy(Integers.checkedCast(index), length);
    }

    protected IMutableSlicedDelegateMemoryBufferFactory getMutableSliceFactory() {
        if (mutableSliceFactory == null) {
            mutableSliceFactory = new FixedMutableSlicedDelegateMemoryBufferFactory(this);
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
            return new SlicedFromDelegateMemoryBuffer(this, index);
        }
    }

    @Override
    public IMemoryBuffer newSlice(final long index, final long length) {
        if (index == 0 && length == capacity()) {
            return this;
        } else {
            return new SlicedDelegateMemoryBuffer(this, index, length);
        }
    }

    @Override
    public String getStringAsciii(final long index, final int length) {
        return delegate.getStringAsciii(Integers.checkedCast(index), length);
    }

    @Override
    public void getStringAsciii(final long index, final int length, final Appendable dst) {
        delegate.getStringAsciii(Integers.checkedCast(index), length, dst);
    }

    @Override
    public void putStringAsciii(final long index, final CharSequence value, final int valueIndex, final int length) {
        delegate.putStringAsciiiFrom(Integers.checkedCast(index), value, valueIndex);
    }

    @Override
    public int putStringUtf8(final long index, final String value) {
        return delegate.putStringUtf8(Integers.checkedCast(index), value);
    }

    @Override
    public String getStringUtf8(final long index, final int length) {
        return delegate.getStringUtf8(Integers.checkedCast(index), length);
    }

    @Override
    public void getStringUtf8(final long index, final int length, final Appendable dst) {
        delegate.getStringUtf8(Integers.checkedCast(index), length, dst);
    }

    @Override
    public void getBytesTo(final long index, final DataOutput dst, final long length) throws IOException {
        delegate.getBytesTo(Integers.checkedCast(index), dst, Integers.checkedCast(length));
    }

    @Override
    public void getBytesTo(final long index, final OutputStream dst, final long length) throws IOException {
        delegate.getBytesTo(Integers.checkedCast(index), dst, Integers.checkedCast(length));
    }

    @Override
    public void putBytesTo(final long index, final DataInput src, final long length) throws IOException {
        delegate.putBytesTo(Integers.checkedCast(index), src, Integers.checkedCast(length));
    }

    @Override
    public void putBytesTo(final long index, final InputStream src, final long length) throws IOException {
        delegate.putBytesTo(Integers.checkedCast(index), src, Integers.checkedCast(length));
    }

    @Override
    public void putBytesTo(final long index, final ReadableByteChannel src, final long length) throws IOException {
        delegate.putBytesTo(Integers.checkedCast(index), src, Integers.checkedCast(length));
    }

    @Override
    public void getBytesTo(final long index, final WritableByteChannel dst, final long length) throws IOException {
        delegate.getBytesTo(Integers.checkedCast(index), dst, Integers.checkedCast(length));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T unwrap(final Class<T> type) {
        if (getClass().isAssignableFrom(type)) {
            return (T) this;
        }
        if (delegate.getClass().isAssignableFrom(type)) {
            return (T) delegate;
        }
        return null;
    }

    @Override
    public java.nio.ByteBuffer asNioByteBuffer(final long index, final int length) {
        return delegate.asNioByteBuffer(Integers.checkedCast(index), length);
    }

    @Override
    public String toString() {
        return MemoryBuffers.toString(this);
    }

    @Override
    public IMemoryBuffer ensureCapacity(final long desiredCapacity) {
        delegate.ensureCapacity(Integers.checkedCast(desiredCapacity));
        return this;
    }

    @Override
    public void clear(final byte value, final long index, final long length) {
        delegate.clear(value, Integers.checkedCast(index), Integers.checkedCast(length));
    }

}
