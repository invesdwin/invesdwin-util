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
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.MemoryBuffers;
import de.invesdwin.util.streams.buffer.memory.delegate.slice.SlicedDelegateMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.delegate.slice.SlicedFromDelegateMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.delegate.slice.mutable.factory.IMutableSlicedDelegateMemoryBufferFactory;

@NotThreadSafe
public abstract class ADelegateMemoryBuffer implements IMemoryBuffer {

    private IMutableSlicedDelegateMemoryBufferFactory mutableSliceFactory;

    @Override
    public int getId() {
        return getDelegate().getId();
    }

    @Override
    public boolean isReadOnly() {
        return getDelegate().isReadOnly();
    }

    protected abstract IMemoryBuffer getDelegate();

    @Override
    public ByteOrder getOrder() {
        return getDelegate().getOrder();
    }

    /////////////////// delegates ////////////////////////////

    @Override
    public byte getByte(final long index) {
        return getDelegate().getByte(index);
    }

    @Override
    public byte getByteVolatile(final long index) {
        return getDelegate().getByteVolatile(index);
    }

    @Override
    public void putByte(final long index, final byte value) {
        getDelegate().putByte(index, value);
    }

    @Override
    public void putByteVolatile(final long index, final byte value) {
        getDelegate().putByteVolatile(index, value);
    }

    @Override
    public char getChar(final long index) {
        return getDelegate().getChar(index);
    }

    @Override
    public char getCharReverse(final long index) {
        return getDelegate().getCharReverse(index);
    }

    @Override
    public char getCharVolatile(final long index) {
        return getDelegate().getCharVolatile(index);
    }

    @Override
    public char getCharVolatileReverse(final long index) {
        return getDelegate().getCharVolatileReverse(index);
    }

    @Override
    public void putChar(final long index, final char value) {
        getDelegate().putChar(index, value);
    }

    @Override
    public void putCharReverse(final long index, final char value) {
        getDelegate().putCharReverse(index, value);
    }

    @Override
    public void putCharVolatile(final long index, final char value) {
        getDelegate().putCharVolatile(index, value);
    }

    @Override
    public void putCharVolatileReverse(final long index, final char value) {
        getDelegate().putCharVolatileReverse(index, value);
    }

    @Override
    public short getShort(final long index) {
        return getDelegate().getShort(index);
    }

    @Override
    public short getShortReverse(final long index) {
        return getDelegate().getShortReverse(index);
    }

    @Override
    public short getShortVolatile(final long index) {
        return getDelegate().getShortVolatile(index);
    }

    @Override
    public short getShortVolatileReverse(final long index) {
        return getDelegate().getShortVolatileReverse(index);
    }

    @Override
    public void putShort(final long index, final short value) {
        getDelegate().putShort(index, value);
    }

    @Override
    public void putShortReverse(final long index, final short value) {
        getDelegate().putShortReverse(index, value);
    }

    @Override
    public void putShortVolatile(final long index, final short value) {
        getDelegate().putShortVolatile(index, value);
    }

    @Override
    public void putShortVolatileReverse(final long index, final short value) {
        getDelegate().putShortVolatileReverse(index, value);
    }

    @Override
    public int getInt(final long index) {
        return getDelegate().getInt(index);
    }

    @Override
    public int getIntReverse(final long index) {
        return getDelegate().getIntReverse(index);
    }

    @Override
    public int getIntVolatile(final long index) {
        return getDelegate().getIntVolatile(index);
    }

    @Override
    public int getIntVolatileReverse(final long index) {
        return getDelegate().getIntVolatileReverse(index);
    }

    @Override
    public int getIntAcquire(final long index) {
        return getDelegate().getIntAcquire(index);
    }

    @Override
    public int getIntAcquireReverse(final long index) {
        return getDelegate().getIntAcquireReverse(index);
    }

    @Override
    public void putInt(final long index, final int value) {
        getDelegate().putInt(index, value);
    }

    @Override
    public void putIntReverse(final long index, final int value) {
        getDelegate().putIntReverse(index, value);
    }

    @Override
    public void putIntVolatile(final long index, final int value) {
        getDelegate().putIntVolatile(index, value);
    }

    @Override
    public void putIntVolatileReverse(final long index, final int value) {
        getDelegate().putIntVolatileReverse(index, value);
    }

    @Override
    public void putIntRelease(final long index, final int value) {
        getDelegate().putIntRelease(index, value);
    }

    @Override
    public void putIntReleaseReverse(final long index, final int value) {
        getDelegate().putIntReleaseReverse(index, value);
    }

    @Override
    public void putIntOrdered(final long index, final int value) {
        getDelegate().putIntOrdered(index, value);
    }

    @Override
    public void putIntOrderedReverse(final long index, final int value) {
        getDelegate().putIntOrderedReverse(index, value);
    }

    @Override
    public long getLong(final long index) {
        return getDelegate().getLong(index);
    }

    @Override
    public long getLongReverse(final long index) {
        return getDelegate().getLongReverse(index);
    }

    @Override
    public long getLongVolatile(final long index) {
        return getDelegate().getLongVolatile(index);
    }

    @Override
    public long getLongVolatileReverse(final long index) {
        return getDelegate().getLongVolatileReverse(index);
    }

    @Override
    public long getLongAcquire(final long index) {
        return getDelegate().getLongAcquire(index);
    }

    @Override
    public long getLongAcquireReverse(final long index) {
        return getDelegate().getLongAcquireReverse(index);
    }

    @Override
    public void putLong(final long index, final long value) {
        getDelegate().putLong(index, value);
    }

    @Override
    public void putLongReverse(final long index, final long value) {
        getDelegate().putLongReverse(index, value);
    }

    @Override
    public void putLongVolatile(final long index, final long value) {
        getDelegate().putLongVolatile(index, value);
    }

    @Override
    public void putLongVolatileReverse(final long index, final long value) {
        getDelegate().putLongVolatileReverse(index, value);
    }

    @Override
    public void putLongRelease(final long index, final long value) {
        getDelegate().putLongRelease(index, value);
    }

    @Override
    public void putLongReleaseReverse(final long index, final long value) {
        getDelegate().putLongReleaseReverse(index, value);
    }

    @Override
    public void putLongOrdered(final long index, final long value) {
        getDelegate().putLongOrdered(index, value);
    }

    @Override
    public void putLongOrderedReverse(final long index, final long value) {
        getDelegate().putLongOrderedReverse(index, value);
    }

    @Override
    public float getFloat(final long index) {
        return getDelegate().getFloat(index);
    }

    @Override
    public float getFloatReverse(final long index) {
        return getDelegate().getFloatReverse(index);
    }

    @Override
    public float getFloatVolatile(final long index) {
        return getDelegate().getFloatVolatile(index);
    }

    @Override
    public float getFloatVolatileReverse(final long index) {
        return getDelegate().getFloatVolatileReverse(index);
    }

    @Override
    public void putFloat(final long index, final float value) {
        getDelegate().putFloat(index, value);
    }

    @Override
    public void putFloatReverse(final long index, final float value) {
        getDelegate().putFloatReverse(index, value);
    }

    @Override
    public void putFloatVolatile(final long index, final float value) {
        getDelegate().putFloatVolatile(index, value);
    }

    @Override
    public void putFloatVolatileReverse(final long index, final float value) {
        getDelegate().putFloatVolatileReverse(index, value);
    }

    @Override
    public double getDouble(final long index) {
        return getDelegate().getDouble(index);
    }

    @Override
    public double getDoubleReverse(final long index) {
        return getDelegate().getDoubleReverse(index);
    }

    @Override
    public double getDoubleVolatile(final long index) {
        return getDelegate().getDoubleVolatile(index);
    }

    @Override
    public double getDoubleVolatileReverse(final long index) {
        return getDelegate().getDoubleVolatileReverse(index);
    }

    @Override
    public void putDouble(final long index, final double value) {
        getDelegate().putDouble(index, value);
    }

    @Override
    public void putDoubleReverse(final long index, final double value) {
        getDelegate().putDoubleReverse(index, value);
    }

    @Override
    public void putDoubleVolatile(final long index, final double value) {
        getDelegate().putDoubleVolatile(index, value);
    }

    @Override
    public void putDoubleVolatileReverse(final long index, final double value) {
        getDelegate().putDoubleVolatileReverse(index, value);
    }

    @Override
    public long addressOffset() {
        return getDelegate().addressOffset();
    }

    @Override
    public MutableDirectBuffer asDirectBuffer(final long index, final int length) {
        return getDelegate().asDirectBuffer(index, length);
    }

    @Override
    public IByteBuffer asByteBuffer(final long index, final int length) {
        return getDelegate().asByteBuffer(index, length);
    }

    @Override
    public long capacity() {
        return getDelegate().capacity();
    }

    @Override
    public void getBytes(final long index, final byte[] dst, final int srcIndex, final int length) {
        getDelegate().getBytes(index, dst, srcIndex, length);
    }

    @Override
    public void getBytes(final long index, final MutableDirectBuffer dstBuffer, final int dstIndex, final int length) {
        getDelegate().getBytes(index, dstBuffer, dstIndex, length);
    }

    @Override
    public void getBytes(final long index, final IByteBuffer dstBuffer, final int dstIndex, final int length) {
        getDelegate().getBytes(index, dstBuffer, dstIndex, length);
    }

    @Override
    public void getBytes(final long index, final IMemoryBuffer dstBuffer, final long dstIndex, final long length) {
        getDelegate().getBytes(index, dstBuffer, dstIndex, length);
    }

    @Override
    public void getBytes(final long index, final java.nio.ByteBuffer dstBuffer, final int dstIndex, final int length) {
        getDelegate().getBytes(index, dstBuffer, dstIndex, length);
    }

    @Override
    public long wrapAdjustment() {
        return getDelegate().wrapAdjustment();
    }

    @Override
    public boolean isExpandable() {
        return getDelegate().isExpandable();
    }

    @Override
    public void putBytes(final long index, final byte[] src, final int srcIndex, final int length) {
        getDelegate().putBytes(index, src, srcIndex, length);
    }

    @Override
    public void putBytes(final long index, final java.nio.ByteBuffer srcBuffer, final int srcIndex, final int length) {
        getDelegate().putBytes(srcIndex, srcBuffer, srcIndex, length);
    }

    @Override
    public void putBytes(final long index, final DirectBuffer srcBuffer, final int srcIndex, final int length) {
        getDelegate().putBytes(srcIndex, srcBuffer, srcIndex, length);
    }

    @Override
    public void putBytes(final long index, final IByteBuffer srcBuffer, final int srcIndex, final int length) {
        getDelegate().putBytes(index, srcBuffer, srcIndex, length);
    }

    @Override
    public void putBytes(final long index, final IMemoryBuffer srcBuffer, final long srcIndex, final long length) {
        getDelegate().putBytes(index, srcBuffer, srcIndex, length);
    }

    @Override
    public InputStream asInputStream(final long index, final long length) {
        return getDelegate().asInputStream(index, length);
    }

    @Override
    public OutputStream asOutputStream(final long index, final long length) {
        return getDelegate().asOutputStream(index, length);
    }

    @Override
    public byte[] asByteArrayCopy(final long index, final int length) {
        return getDelegate().asByteArrayCopy(index, length);
    }

    protected IMutableSlicedDelegateMemoryBufferFactory getMutableSliceFactory() {
        if (mutableSliceFactory == null) {
            mutableSliceFactory = IMutableSlicedDelegateMemoryBufferFactory.newInstance(this);
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
    public String getStringAsciii(final long index, final int size) {
        return getDelegate().getStringAsciii(index, size);
    }

    @Override
    public void getStringAsciii(final long index, final int length, final Appendable dst) {
        getDelegate().getStringAsciii(index, length, dst);
    }

    @Override
    public void putStringAsciii(final long index, final CharSequence value, final int valueIndex, final int length) {
        getDelegate().putStringAsciii(index, value, valueIndex, length);
    }

    @Override
    public String getStringUtf8(final long index, final int length) {
        return getDelegate().getStringUtf8(index, length);
    }

    @Override
    public int putStringUtf8(final long index, final String value) {
        return getDelegate().putStringUtf8(index, value);
    }

    @Override
    public void getStringUtf8(final long index, final int length, final Appendable dst) {
        getDelegate().getStringUtf8(index, length, dst);
    }

    @Override
    public void getBytesTo(final long index, final DataOutput dst, final long length) throws IOException {
        getDelegate().getBytesTo(index, dst, length);
    }

    @Override
    public void getBytesTo(final long index, final OutputStream dst, final long length) throws IOException {
        getDelegate().getBytesTo(index, dst, length);
    }

    @Override
    public void putBytesTo(final long index, final DataInput src, final long length) throws IOException {
        getDelegate().putBytesTo(index, src, length);
    }

    @Override
    public void putBytesTo(final long index, final InputStream src, final long length) throws IOException {
        getDelegate().putBytesTo(index, src, length);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T unwrap(final Class<T> type) {
        if (getClass().isAssignableFrom(type)) {
            return (T) this;
        }
        return getDelegate().unwrap(type);
    }

    @Override
    public java.nio.ByteBuffer asNioByteBuffer(final long index, final int length) {
        return getDelegate().asNioByteBuffer(index, length);
    }

    @Override
    public void getBytesTo(final long index, final WritableByteChannel dst, final long length) throws IOException {
        getDelegate().getBytesTo(index, dst, length);
    }

    @Override
    public void putBytesTo(final long index, final ReadableByteChannel src, final long length) throws IOException {
        getDelegate().putBytesTo(index, src, length);
    }

    @Override
    public String toString() {
        return MemoryBuffers.toString(this);
    }

    @Override
    public IMemoryBuffer clone(final long index, final int length) {
        return getDelegate().clone(index, length);
    }

    @Override
    public IMemoryBuffer ensureCapacity(final long desiredCapacity) {
        getDelegate().ensureCapacity(desiredCapacity);
        return this;
    }

    @Override
    public void clear(final byte value, final long index, final long length) {
        getDelegate().clear(value, index, length);
    }

    @Override
    public IMemoryBuffer asImmutableSlice() {
        return this;
    }
}
