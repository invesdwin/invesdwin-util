package de.invesdwin.util.streams.buffer.memory.delegate.slice.mutable;

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
import de.invesdwin.util.streams.buffer.bytes.delegate.MemoryDelegateByteBuffer;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.MemoryBuffers;
import de.invesdwin.util.streams.buffer.memory.delegate.slice.SlicedFromDelegateMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.delegate.slice.mutable.factory.ExpandableMutableSlicedDelegateMemoryBufferFactory;
import de.invesdwin.util.streams.buffer.memory.delegate.slice.mutable.factory.IMutableSlicedDelegateMemoryBufferFactory;

@NotThreadSafe
public class MutableSlicedFromDelegateMemoryBuffer implements IMemoryBuffer {

    private final IMemoryBuffer delegate;
    private long from;
    private IMutableSlicedDelegateMemoryBufferFactory mutableSliceFactory;

    public MutableSlicedFromDelegateMemoryBuffer(final IMemoryBuffer delegate, final long from) {
        this.delegate = delegate;
        this.from = from;
    }

    public void setFrom(final long from) {
        this.from = from;
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
        return delegate.addressOffset() + from;
    }

    @Override
    public long capacity() {
        return delegate.capacity() - from;
    }

    @Override
    public long getLong(final long index) {
        return delegate.getLong(index + from);
    }

    @Override
    public int getInt(final long index) {
        return delegate.getInt(index + from);
    }

    @Override
    public double getDouble(final long index) {
        return delegate.getDouble(index + from);
    }

    @Override
    public float getFloat(final long index) {
        return delegate.getFloat(index + from);
    }

    @Override
    public short getShort(final long index) {
        return delegate.getShort(index + from);
    }

    @Override
    public char getChar(final long index) {
        return delegate.getChar(index + from);
    }

    @Override
    public long getLongReverse(final long index) {
        return delegate.getLongReverse(index + from);
    }

    @Override
    public int getIntReverse(final long index) {
        return delegate.getIntReverse(index + from);
    }

    @Override
    public double getDoubleReverse(final long index) {
        return delegate.getDoubleReverse(index + from);
    }

    @Override
    public float getFloatReverse(final long index) {
        return delegate.getFloatReverse(index + from);
    }

    @Override
    public short getShortReverse(final long index) {
        return delegate.getShortReverse(index + from);
    }

    @Override
    public char getCharReverse(final long index) {
        return delegate.getCharReverse(index + from);
    }

    @Override
    public byte getByte(final long index) {
        return delegate.getByte(index + from);
    }

    @Override
    public void getBytes(final long index, final byte[] dst, final int dstIndex, final int length) {
        delegate.getBytes(index + from, dst, dstIndex, length);
    }

    @Override
    public void getBytes(final long index, final MutableDirectBuffer dstBuffer, final int dstIndex, final int length) {
        delegate.getBytes(index + from, dstBuffer, dstIndex, length);
    }

    @Override
    public void getBytes(final long index, final IByteBuffer dstBuffer, final int dstIndex, final int length) {
        delegate.getBytes(index + from, dstBuffer, dstIndex, length);
    }

    @Override
    public void getBytes(final long index, final IMemoryBuffer dstBuffer, final long dstIndex, final long length) {
        delegate.getBytes(index + from, dstBuffer, dstIndex, length);
    }

    @Override
    public void getBytes(final long index, final java.nio.ByteBuffer dstBuffer, final int dstIndex, final int length) {
        delegate.getBytes(index + from, dstBuffer, dstIndex, length);
    }

    @Override
    public long wrapAdjustment() {
        return delegate.wrapAdjustment() + from;
    }

    @Override
    public boolean isExpandable() {
        return delegate.isExpandable();
    }

    @Override
    public void putLong(final long index, final long value) {
        delegate.putLong(index + from, value);
    }

    @Override
    public void putInt(final long index, final int value) {
        delegate.putInt(index + from, value);
    }

    @Override
    public void putDouble(final long index, final double value) {
        delegate.putDouble(index + from, value);
    }

    @Override
    public void putFloat(final long index, final float value) {
        delegate.putFloat(index + from, value);
    }

    @Override
    public void putShort(final long index, final short value) {
        delegate.putShort(index + from, value);
    }

    @Override
    public void putChar(final long index, final char value) {
        delegate.putChar(index + from, value);
    }

    @Override
    public void putLongReverse(final long index, final long value) {
        delegate.putLongReverse(index + from, value);
    }

    @Override
    public void putIntReverse(final long index, final int value) {
        delegate.putIntReverse(index + from, value);
    }

    @Override
    public void putDoubleReverse(final long index, final double value) {
        delegate.putDoubleReverse(index + from, value);
    }

    @Override
    public void putFloatReverse(final long index, final float value) {
        delegate.putFloatReverse(index + from, value);
    }

    @Override
    public void putShortReverse(final long index, final short value) {
        delegate.putShortReverse(index + from, value);
    }

    @Override
    public void putCharReverse(final long index, final char value) {
        delegate.putCharReverse(index + from, value);
    }

    @Override
    public void putByte(final long index, final byte value) {
        delegate.putByte(index + from, value);
    }

    @Override
    public void putBytes(final long index, final byte[] src, final int srcIndex, final int length) {
        delegate.putBytes(index + from, src, srcIndex, length);
    }

    @Override
    public void putBytes(final long index, final java.nio.ByteBuffer srcBuffer, final int srcIndex, final int length) {
        delegate.putBytes(index + from, srcBuffer, srcIndex, length);
    }

    @Override
    public void putBytes(final long index, final DirectBuffer srcBuffer, final int srcIndex, final int length) {
        delegate.putBytes(index + from, srcBuffer, srcIndex, length);
    }

    @Override
    public void putBytes(final long index, final IByteBuffer srcBuffer, final int srcIndex, final int length) {
        delegate.putBytes(index + from, srcBuffer, srcIndex, length);
    }

    @Override
    public void putBytes(final long index, final IMemoryBuffer srcBuffer, final long srcIndex, final long length) {
        delegate.putBytes(index + from, srcBuffer, srcIndex, length);
    }

    @Override
    public InputStream asInputStream(final long index, final long length) {
        return delegate.asInputStream(index + from, length);
    }

    @Override
    public OutputStream asOutputStream(final long index, final long length) {
        return delegate.asOutputStream(index + from, length);
    }

    @Override
    public byte[] asByteArrayCopy(final long index, final int length) {
        return delegate.asByteArrayCopy(index + from, length);
    }

    @Override
    public MutableDirectBuffer asDirectBuffer(final long index, final int length) {
        return delegate.asDirectBuffer(index + from, length);
    }

    @Override
    public IByteBuffer asByteBuffer(final long index, final int length) {
        return new MemoryDelegateByteBuffer(newSlice(index, length));
    }

    private IMutableSlicedDelegateMemoryBufferFactory getMutableSliceFactory() {
        if (mutableSliceFactory == null) {
            mutableSliceFactory = new ExpandableMutableSlicedDelegateMemoryBufferFactory(this);
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
            return new SlicedFromDelegateMemoryBuffer(delegate, index + from);
        }
    }

    @Override
    public IMemoryBuffer newSlice(final long index, final long length) {
        if (index == 0 && length == capacity()) {
            return this;
        } else {
            return delegate.newSlice(index + from, length);
        }
    }

    @Override
    public String getStringAscii(final long index, final int length) {
        return delegate.getStringAscii(index + from, length);
    }

    @Override
    public int getStringAscii(final long index, final int length, final Appendable dst) {
        return delegate.getStringAscii(index + from, length, dst);
    }

    @Override
    public int putStringAscii(final long index, final CharSequence value, final int valueIndex, final int length) {
        return delegate.putStringAscii(index + from, value, valueIndex, length);
    }

    @Override
    public int putStringUtf8(final long index, final String value) {
        return delegate.putStringUtf8(index + from, value);
    }

    @Override
    public String getStringUtf8(final long index, final int length) {
        return delegate.getStringUtf8(index + from, length);
    }

    @Override
    public int getStringUtf8(final long index, final int length, final Appendable dst) {
        return delegate.getStringUtf8(index + from, length, dst);
    }

    @Override
    public void getBytesTo(final long index, final DataOutput dst, final long length) throws IOException {
        delegate.getBytesTo(index + from, dst, length);
    }

    @Override
    public void getBytesTo(final long index, final OutputStream dst, final long length) throws IOException {
        delegate.getBytesTo(index + from, dst, length);
    }

    @Override
    public void putBytesTo(final long index, final DataInput src, final long length) throws IOException {
        delegate.putBytesTo(index + from, src, length);
    }

    @Override
    public void putBytesTo(final long index, final InputStream src, final long length) throws IOException {
        delegate.putBytesTo(index + from, src, length);
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
    public java.nio.ByteBuffer asNioByteBuffer(final long index, final int length) {
        return delegate.asNioByteBuffer(index + from, length);
    }

    @Override
    public void getBytesTo(final long index, final WritableByteChannel dst, final long length) throws IOException {
        delegate.getBytesTo(index + from, dst, length);
    }

    @Override
    public void putBytesTo(final long index, final ReadableByteChannel src, final long length) throws IOException {
        delegate.putBytesTo(index + from, src, length);
    }

    @Override
    public void clear(final byte value, final long index, final long length) {
        delegate.clear(value, index + from, length);
    }

    @Override
    public String toString() {
        return MemoryBuffers.toString(this);
    }

    @Override
    public IMemoryBuffer ensureCapacity(final long desiredCapacity) {
        delegate.ensureCapacity(desiredCapacity + from);
        return this;
    }

}
