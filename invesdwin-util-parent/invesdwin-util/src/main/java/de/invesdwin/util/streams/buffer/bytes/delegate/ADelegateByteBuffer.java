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
import de.invesdwin.util.streams.buffer.bytes.delegate.slice.SlicedDelegateByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.delegate.slice.SlicedFromDelegateByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.delegate.slice.mutable.factory.IMutableSlicedDelegateByteBufferFactory;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;

@NotThreadSafe
public abstract class ADelegateByteBuffer implements IByteBuffer {

    private IMutableSlicedDelegateByteBufferFactory mutableSliceFactory;

    @Override
    public boolean isReadOnly() {
        return getDelegate().isReadOnly();
    }

    protected abstract IByteBuffer getDelegate();

    @Override
    public ByteOrder getOrder() {
        return getDelegate().getOrder();
    }

    @Override
    public void putChar(final int index, final char value) {
        getDelegate().putChar(index, value);
    }

    @Override
    public void putCharReverse(final int index, final char value) {
        getDelegate().putCharReverse(index, value);
    }

    @Override
    public void putDouble(final int index, final double value) {
        getDelegate().putDouble(index, value);
    }

    @Override
    public void putDoubleReverse(final int index, final double value) {
        getDelegate().putDoubleReverse(index, value);
    }

    @Override
    public void putFloat(final int index, final float value) {
        getDelegate().putFloat(index, value);
    }

    @Override
    public void putFloatReverse(final int index, final float value) {
        getDelegate().putFloatReverse(index, value);
    }

    @Override
    public void putInt(final int index, final int value) {
        getDelegate().putInt(index, value);
    }

    @Override
    public void putIntReverse(final int index, final int value) {
        getDelegate().putIntReverse(index, value);
    }

    @Override
    public void putLong(final int index, final long value) {
        getDelegate().putLong(index, value);
    }

    @Override
    public void putLongReverse(final int index, final long value) {
        getDelegate().putLongReverse(index, value);
    }

    @Override
    public void putShort(final int index, final short value) {
        getDelegate().putShort(index, value);
    }

    @Override
    public void putShortReverse(final int index, final short value) {
        getDelegate().putShortReverse(index, value);
    }

    @Override
    public char getChar(final int index) {
        return getDelegate().getChar(index);
    }

    @Override
    public char getCharReverse(final int index) {
        return getDelegate().getCharReverse(index);
    }

    @Override
    public double getDouble(final int index) {
        return getDelegate().getDouble(index);
    }

    @Override
    public double getDoubleReverse(final int index) {
        return getDelegate().getDoubleReverse(index);
    }

    @Override
    public float getFloat(final int index) {
        return getDelegate().getFloat(index);
    }

    @Override
    public float getFloatReverse(final int index) {
        return getDelegate().getFloatReverse(index);
    }

    @Override
    public int getInt(final int index) {
        return getDelegate().getInt(index);
    }

    @Override
    public int getIntReverse(final int index) {
        return getDelegate().getIntReverse(index);
    }

    @Override
    public long getLong(final int index) {
        return getDelegate().getLong(index);
    }

    @Override
    public long getLongReverse(final int index) {
        return getDelegate().getLongReverse(index);
    }

    @Override
    public short getShort(final int index) {
        return getDelegate().getShort(index);
    }

    @Override
    public short getShortReverse(final int index) {
        return getDelegate().getShortReverse(index);
    }

    /////////////////// delegates ////////////////////////////

    @Override
    public void putByte(final int index, final byte value) {
        getDelegate().putByte(index, value);
    }

    @Override
    public byte getByte(final int index) {
        return getDelegate().getByte(index);
    }

    @Override
    public long addressOffset() {
        return getDelegate().addressOffset();
    }

    @Override
    public MutableDirectBuffer directBuffer() {
        return getDelegate().directBuffer();
    }

    @Override
    public MutableDirectBuffer asDirectBuffer() {
        return getDelegate().asDirectBuffer();
    }

    @Override
    public MutableDirectBuffer asDirectBuffer(final int index, final int length) {
        return getDelegate().asDirectBuffer(index, length);
    }

    @Override
    public IMemoryBuffer asMemoryBuffer() {
        return getDelegate().asMemoryBuffer();
    }

    @Override
    public IMemoryBuffer asMemoryBufferFrom(final int index) {
        return getDelegate().asMemoryBufferFrom(index);
    }

    @Override
    public IMemoryBuffer asMemoryBuffer(final int index, final int length) {
        return getDelegate().asMemoryBuffer(index, length);
    }

    @Override
    public byte[] byteArray() {
        return getDelegate().byteArray();
    }

    @Override
    public java.nio.ByteBuffer nioByteBuffer() {
        return getDelegate().nioByteBuffer();
    }

    @Override
    public int capacity() {
        return getDelegate().capacity();
    }

    @Override
    public void getBytes(final int index, final byte[] dst, final int srcIndex, final int length) {
        getDelegate().getBytes(index, dst, srcIndex, length);
    }

    @Override
    public void getBytes(final int index, final MutableDirectBuffer dstBuffer, final int dstIndex, final int length) {
        getDelegate().getBytes(index, dstBuffer, dstIndex, length);
    }

    @Override
    public void getBytes(final int index, final IByteBuffer dstBuffer, final int dstIndex, final int length) {
        getDelegate().getBytes(index, dstBuffer, dstIndex, length);
    }

    @Override
    public void getBytes(final int index, final IMemoryBuffer dstBuffer, final long dstIndex, final int length) {
        getDelegate().getBytes(index, dstBuffer, dstIndex, length);
    }

    @Override
    public void getBytes(final int index, final java.nio.ByteBuffer dstBuffer, final int dstIndex, final int length) {
        getDelegate().getBytes(index, dstBuffer, dstIndex, length);
    }

    @Override
    public int wrapAdjustment() {
        return getDelegate().wrapAdjustment();
    }

    @Override
    public boolean isExpandable() {
        return getDelegate().isExpandable();
    }

    @Override
    public void putBytes(final int index, final byte[] src, final int srcIndex, final int length) {
        getDelegate().putBytes(index, src, srcIndex, length);
    }

    @Override
    public void putBytes(final int index, final java.nio.ByteBuffer srcBuffer, final int srcIndex, final int length) {
        getDelegate().putBytes(srcIndex, srcBuffer, srcIndex, length);
    }

    @Override
    public void putBytes(final int index, final DirectBuffer srcBuffer, final int srcIndex, final int length) {
        getDelegate().putBytes(srcIndex, srcBuffer, srcIndex, length);
    }

    @Override
    public void putBytes(final int index, final IByteBuffer srcBuffer, final int srcIndex, final int length) {
        getDelegate().putBytes(index, srcBuffer, srcIndex, length);
    }

    @Override
    public void putBytes(final int index, final IMemoryBuffer srcBuffer, final long srcIndex, final int length) {
        getDelegate().putBytes(index, srcBuffer, srcIndex, length);
    }

    @Override
    public InputStream asInputStream(final int index, final int length) {
        return getDelegate().asInputStream(index, length);
    }

    @Override
    public OutputStream asOutputStream(final int index, final int length) {
        return getDelegate().asOutputStream(index, length);
    }

    @Override
    public byte[] asByteArray(final int index, final int length) {
        return getDelegate().asByteArray(index, length);
    }

    @Override
    public byte[] asByteArrayCopy(final int index, final int length) {
        return getDelegate().asByteArrayCopy(index, length);
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
        return getDelegate().getStringAsciii(index, size);
    }

    @Override
    public void getStringAsciii(final int index, final int length, final Appendable dst) {
        getDelegate().getStringAsciii(index, length, dst);
    }

    @Override
    public void putStringAsciii(final int index, final CharSequence value, final int valueIndex, final int length) {
        getDelegate().putStringAsciii(index, value, valueIndex, length);
    }

    @Override
    public String getStringUtf8(final int index, final int length) {
        return getDelegate().getStringUtf8(index, length);
    }

    @Override
    public int putStringUtf8(final int index, final String value) {
        return getDelegate().putStringUtf8(index, value);
    }

    @Override
    public void getStringUtf8(final int index, final int length, final Appendable dst) {
        getDelegate().getStringUtf8(index, length, dst);
    }

    @Override
    public void getBytesTo(final int index, final DataOutput dst, final int length) throws IOException {
        getDelegate().getBytesTo(index, dst, length);
    }

    @Override
    public void getBytesTo(final int index, final OutputStream dst, final int length) throws IOException {
        getDelegate().getBytesTo(index, dst, length);
    }

    @Override
    public void putBytesTo(final int index, final DataInput src, final int length) throws IOException {
        getDelegate().putBytesTo(index, src, length);
    }

    @Override
    public void putBytesTo(final int index, final InputStream src, final int length) throws IOException {
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
    public java.nio.ByteBuffer asNioByteBuffer(final int index, final int length) {
        return getDelegate().asNioByteBuffer(index, length);
    }

    @Override
    public void getBytesTo(final int index, final WritableByteChannel dst, final int length) throws IOException {
        getDelegate().getBytesTo(index, dst, length);
    }

    @Override
    public void putBytesTo(final int index, final ReadableByteChannel src, final int length) throws IOException {
        getDelegate().putBytesTo(index, src, length);
    }

    @Override
    public String toString() {
        return ByteBuffers.toString(this);
    }

    //CHECKSTYLE:OFF
    @Override
    public IByteBuffer clone() {
        //CHECKSTYLE:ON
        return getDelegate().clone();
    }

    @Override
    public IByteBuffer clone(final int index, final int length) {
        return getDelegate().clone(index, length);
    }

    @Override
    public IByteBuffer ensureCapacity(final int desiredCapacity) {
        getDelegate().ensureCapacity(desiredCapacity);
        return this;
    }

    @Override
    public void clear(final byte value, final int index, final int length) {
        getDelegate().clear(value, index, length);
    }
}
