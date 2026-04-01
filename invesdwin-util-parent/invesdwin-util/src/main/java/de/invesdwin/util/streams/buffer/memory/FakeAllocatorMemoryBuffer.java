package de.invesdwin.util.streams.buffer.memory;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import javax.annotation.concurrent.Immutable;

import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;

import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.ZeroByteBuffer;

@Immutable
public class FakeAllocatorMemoryBuffer implements IMemoryBuffer {

    private final int id;
    private final long capacity;

    public FakeAllocatorMemoryBuffer(final int id, final long capacity) {
        this.id = id;
        if (capacity < 0) {
            throw new IllegalArgumentException("capacity must be non-negative: " + capacity);
        }
        this.capacity = capacity;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public IMemoryBuffer ensureCapacity(final long capacity) {
        MemoryBuffers.ensureCapacity(this, capacity);
        return this;
    }

    @Override
    public ByteOrder getOrder() {
        return ByteBuffers.DEFAULT_ORDER;
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    @Override
    public long addressOffset() {
        return 0;
    }

    @Override
    public long capacity() {
        return capacity;
    }

    @Override
    public long getLong(final long index) {
        return 0;
    }

    @Override
    public long getLongReverse(final long index) {
        return 0;
    }

    @Override
    public int getInt(final long index) {
        return 0;
    }

    @Override
    public int getIntReverse(final long index) {
        return 0;
    }

    @Override
    public double getDouble(final long index) {
        return 0;
    }

    @Override
    public double getDoubleReverse(final long index) {
        return 0;
    }

    @Override
    public float getFloat(final long index) {
        return 0;
    }

    @Override
    public float getFloatReverse(final long index) {
        return 0;
    }

    @Override
    public short getShort(final long index) {
        return 0;
    }

    @Override
    public short getShortReverse(final long index) {
        return 0;
    }

    @Override
    public char getChar(final long index) {
        return 0;
    }

    @Override
    public char getCharReverse(final long index) {
        return 0;
    }

    @Override
    public byte getByte(final long index) {
        return 0;
    }

    @Override
    public void getBytes(final long index, final byte[] dst, final int dstIndex, final int length) {
        //noop
    }

    @Override
    public void getBytes(final long index, final MutableDirectBuffer dstBuffer, final int dstIndex, final int length) {
        dstBuffer.checkLimit(dstIndex + length);
    }

    @Override
    public void getBytes(final long index, final IByteBuffer dstBuffer, final int dstIndex, final int length) {
        dstBuffer.ensureCapacity(dstIndex + length);
    }

    @Override
    public void getBytes(final long index, final IMemoryBuffer dstBuffer, final long dstIndex, final long length) {
        dstBuffer.ensureCapacity(dstIndex + length);
    }

    @Override
    public void getBytes(final long index, final java.nio.ByteBuffer dstBuffer, final int dstIndex, final int length) {
        //noop
    }

    @Override
    public long wrapAdjustment() {
        return 0;
    }

    @Override
    public boolean isExpandable() {
        return false;
    }

    @Override
    public void putLong(final long index, final long value) {
        throw newReadOnlyException();
    }

    @Override
    public void putLongReverse(final long index, final long value) {
        throw newReadOnlyException();
    }

    @Override
    public void putInt(final long index, final int value) {
        throw newReadOnlyException();
    }

    @Override
    public void putIntReverse(final long index, final int value) {
        throw newReadOnlyException();
    }

    @Override
    public void putDouble(final long index, final double value) {
        throw newReadOnlyException();
    }

    @Override
    public void putDoubleReverse(final long index, final double value) {
        throw newReadOnlyException();
    }

    @Override
    public void putFloat(final long index, final float value) {
        throw newReadOnlyException();
    }

    @Override
    public void putFloatReverse(final long index, final float value) {
        throw newReadOnlyException();
    }

    @Override
    public void putShort(final long index, final short value) {
        throw newReadOnlyException();
    }

    @Override
    public void putShortReverse(final long index, final short value) {
        throw newReadOnlyException();
    }

    @Override
    public void putChar(final long index, final char value) {
        throw newReadOnlyException();
    }

    @Override
    public void putCharReverse(final long index, final char value) {
        throw newReadOnlyException();
    }

    @Override
    public void putByte(final long index, final byte value) {
        throw newReadOnlyException();
    }

    @Override
    public void putBytes(final long index, final byte[] src, final int srcIndex, final int length) {
        throw newReadOnlyException();
    }

    @Override
    public void putBytes(final long index, final java.nio.ByteBuffer srcBuffer, final int srcIndex, final int length) {
        throw newReadOnlyException();
    }

    @Override
    public void putBytes(final long index, final DirectBuffer srcBuffer, final int srcIndex, final int length) {
        throw newReadOnlyException();
    }

    @Override
    public void putBytes(final long index, final IByteBuffer srcBuffer, final int srcIndex, final int length) {
        throw newReadOnlyException();
    }

    @Override
    public void putBytes(final long index, final IMemoryBuffer srcBuffer, final long srcIndex, final long length) {
        throw newReadOnlyException();
    }

    @Override
    public InputStream asInputStream(final long index, final long length) {
        throw newReadOnlyException();
    }

    @Override
    public OutputStream asOutputStream(final long index, final long length) {
        throw newReadOnlyException();
    }

    @Override
    public java.nio.ByteBuffer asNioByteBuffer(final long index, final int length) {
        throw newReadOnlyException();
    }

    @Override
    public byte[] asByteArrayCopy(final long index, final int length) {
        throw newReadOnlyException();
    }

    @Override
    public MutableDirectBuffer asDirectBuffer(final long index, final int length) {
        throw newReadOnlyException();
    }

    @Override
    public IByteBuffer asByteBuffer(final long index, final int length) {
        throw newReadOnlyException();
    }

    @Override
    public IMemoryBuffer sliceFrom(final long index) {
        return newSliceFrom(index);
    }

    @Override
    public IMemoryBuffer slice(final long index, final long length) {
        return newSlice(index, length);
    }

    @Override
    public IMemoryBuffer newSliceFrom(final long index) {
        if (index == 0) {
            return this;
        } else {
            return new FakeAllocatorMemoryBuffer(id, capacity - index);
        }
    }

    @Override
    public IMemoryBuffer newSlice(final long index, final long length) {
        if (index == 0 && length == capacity) {
            return this;
        } else {
            return new FakeAllocatorMemoryBuffer(id, length);
        }
    }

    @Override
    public String getStringAsciii(final long index, final int length) {
        throw newReadOnlyException();
    }

    @Override
    public void getStringAsciii(final long index, final int length, final Appendable dst) {}

    @Override
    public void putStringAsciii(final long index, final CharSequence value, final int valueIndex, final int length) {
        throw newReadOnlyException();
    }

    @Override
    public int putStringUtf8(final long index, final String value) {
        throw newReadOnlyException();
    }

    @Override
    public String getStringUtf8(final long index, final int length) {
        throw newReadOnlyException();
    }

    @Override
    public void getStringUtf8(final long index, final int length, final Appendable dst) {
        throw newReadOnlyException();
    }

    @Override
    public void getBytesTo(final long index, final DataOutput dst, final long length) throws IOException {
        for (int i = 0; i < length; i++) {
            dst.writeByte(0);
        }
    }

    @Override
    public void getBytesTo(final long index, final OutputStream dst, final long length) throws IOException {
        for (int i = 0; i < length; i++) {
            dst.write(0);
        }
    }

    @Override
    public void getBytesTo(final long index, final WritableByteChannel dst, final long length) throws IOException {
        for (int i = 0; i < length; i++) {
            dst.write(ZeroByteBuffer.ZERO_BYTE_BUFFER);
        }
    }

    @Override
    public void putBytesTo(final long index, final DataInput src, final long length) throws IOException {
        throw newReadOnlyException();
    }

    @Override
    public void putBytesTo(final long index, final InputStream src, final long length) throws IOException {
        throw newReadOnlyException();
    }

    @Override
    public void putBytesTo(final long index, final ReadableByteChannel src, final long length) throws IOException {
        throw newReadOnlyException();
    }

    private UnsupportedOperationException newReadOnlyException() {
        return new UnsupportedOperationException("read only");
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T unwrap(final Class<T> type) {
        if (getClass().isAssignableFrom(type)) {
            return (T) this;
        } else {
            return null;
        }
    }

    @Override
    public void clear(final byte value, final long index, final long length) {
        //noop
    }

    @Override
    public IMemoryBuffer clone(final long index, final int length) {
        if (index == 0 && length == capacity) {
            return this;
        } else {
            return new FakeAllocatorMemoryBuffer(id, length);
        }
    }

    @Override
    public IMemoryBuffer asImmutableSlice() {
        return this;
    }

}
