package de.invesdwin.util.streams.buffer.bytes;

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

import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;

@Immutable
public class FakeAllocatorBuffer implements IByteBuffer {

    private final int capacity;

    public FakeAllocatorBuffer(final int capacity) {
        this.capacity = capacity;
    }

    @Override
    public IByteBuffer ensureCapacity(final int capacity) {
        ByteBuffers.ensureCapacity(this, capacity);
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
    public MutableDirectBuffer directBuffer() {
        return null;
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
        return capacity;
    }

    @Override
    public long getLong(final int index) {
        return 0;
    }

    @Override
    public long getLongReverse(final int index) {
        return 0;
    }

    @Override
    public int getInt(final int index) {
        return 0;
    }

    @Override
    public int getIntReverse(final int index) {
        return 0;
    }

    @Override
    public double getDouble(final int index) {
        return 0;
    }

    @Override
    public double getDoubleReverse(final int index) {
        return 0;
    }

    @Override
    public float getFloat(final int index) {
        return 0;
    }

    @Override
    public float getFloatReverse(final int index) {
        return 0;
    }

    @Override
    public short getShort(final int index) {
        return 0;
    }

    @Override
    public short getShortReverse(final int index) {
        return 0;
    }

    @Override
    public char getChar(final int index) {
        return 0;
    }

    @Override
    public char getCharReverse(final int index) {
        return 0;
    }

    @Override
    public byte getByte(final int index) {
        return 0;
    }

    @Override
    public void getBytes(final int index, final byte[] dst, final int dstIndex, final int length) {
        //noop
    }

    @Override
    public void getBytes(final int index, final MutableDirectBuffer dstBuffer, final int dstIndex, final int length) {
        //noop
    }

    @Override
    public void getBytes(final int index, final IByteBuffer dstBuffer, final int dstIndex, final int length) {
        //noop
    }

    @Override
    public void getBytes(final int index, final IMemoryBuffer dstBuffer, final long dstIndex, final int length) {
        //noop
    }

    @Override
    public void getBytes(final int index, final java.nio.ByteBuffer dstBuffer, final int dstIndex, final int length) {
        //noop
    }

    @Override
    public int wrapAdjustment() {
        return 0;
    }

    @Override
    public boolean isExpandable() {
        return false;
    }

    @Override
    public void putLong(final int index, final long value) {
        throw newReadOnlyException();
    }

    @Override
    public void putLongReverse(final int index, final long value) {
        throw newReadOnlyException();
    }

    @Override
    public void putInt(final int index, final int value) {
        throw newReadOnlyException();
    }

    @Override
    public void putIntReverse(final int index, final int value) {
        throw newReadOnlyException();
    }

    @Override
    public void putDouble(final int index, final double value) {
        throw newReadOnlyException();
    }

    @Override
    public void putDoubleReverse(final int index, final double value) {
        throw newReadOnlyException();
    }

    @Override
    public void putFloat(final int index, final float value) {
        throw newReadOnlyException();
    }

    @Override
    public void putFloatReverse(final int index, final float value) {
        throw newReadOnlyException();
    }

    @Override
    public void putShort(final int index, final short value) {
        throw newReadOnlyException();
    }

    @Override
    public void putShortReverse(final int index, final short value) {
        throw newReadOnlyException();
    }

    @Override
    public void putChar(final int index, final char value) {
        throw newReadOnlyException();
    }

    @Override
    public void putCharReverse(final int index, final char value) {
        throw newReadOnlyException();
    }

    @Override
    public void putByte(final int index, final byte value) {
        throw newReadOnlyException();
    }

    @Override
    public void putBytes(final int index, final byte[] src, final int srcIndex, final int length) {
        throw newReadOnlyException();
    }

    @Override
    public void putBytes(final int index, final java.nio.ByteBuffer srcBuffer, final int srcIndex, final int length) {
        throw newReadOnlyException();
    }

    @Override
    public void putBytes(final int index, final DirectBuffer srcBuffer, final int srcIndex, final int length) {
        throw newReadOnlyException();
    }

    @Override
    public void putBytes(final int index, final IByteBuffer srcBuffer, final int srcIndex, final int length) {
        throw newReadOnlyException();
    }

    @Override
    public void putBytes(final int index, final IMemoryBuffer srcBuffer, final long srcIndex, final int length) {
        throw newReadOnlyException();
    }

    @Override
    public InputStream asInputStream(final int index, final int length) {
        throw newReadOnlyException();
    }

    @Override
    public OutputStream asOutputStream(final int index, final int length) {
        throw newReadOnlyException();
    }

    @Override
    public byte[] asByteArray(final int index, final int length) {
        throw newReadOnlyException();
    }

    @Override
    public java.nio.ByteBuffer asNioByteBuffer(final int index, final int length) {
        throw newReadOnlyException();
    }

    @Override
    public byte[] asByteArrayCopy(final int index, final int length) {
        throw newReadOnlyException();
    }

    @Override
    public MutableDirectBuffer asDirectBuffer() {
        throw newReadOnlyException();
    }

    @Override
    public MutableDirectBuffer asDirectBuffer(final int index, final int length) {
        throw newReadOnlyException();
    }

    @Override
    public IMemoryBuffer asMemoryBuffer() {
        throw newReadOnlyException();
    }

    @Override
    public IMemoryBuffer asMemoryBuffer(final int index, final int length) {
        throw newReadOnlyException();
    }

    @Override
    public IByteBuffer sliceFrom(final int index) {
        return newSliceFrom(index);
    }

    @Override
    public IByteBuffer slice(final int index, final int length) {
        return newSlice(index, length);
    }

    @Override
    public IByteBuffer newSliceFrom(final int index) {
        if (index == 0) {
            return this;
        } else {
            return new FakeAllocatorBuffer(capacity - index);
        }
    }

    @Override
    public IByteBuffer newSlice(final int index, final int length) {
        if (index == 0 && length == capacity) {
            return this;
        } else {
            return new FakeAllocatorBuffer(length);
        }
    }

    @Override
    public String getStringAsciii(final int index, final int length) {
        throw newReadOnlyException();
    }

    @Override
    public void getStringAsciii(final int index, final int length, final Appendable dst) {}

    @Override
    public void putStringAsciii(final int index, final CharSequence value, final int valueIndex, final int length) {
        throw newReadOnlyException();
    }

    @Override
    public int putStringUtf8(final int index, final String value) {
        throw newReadOnlyException();
    }

    @Override
    public String getStringUtf8(final int index, final int length) {
        throw newReadOnlyException();
    }

    @Override
    public void getStringUtf8(final int index, final int length, final Appendable dst) {
        throw newReadOnlyException();
    }

    @Override
    public void getBytesTo(final int index, final DataOutput dst, final int length) throws IOException {
        //noop
    }

    @Override
    public void getBytesTo(final int index, final OutputStream dst, final int length) throws IOException {
        //noop
    }

    @Override
    public void getBytesTo(final int index, final WritableByteChannel dst, final int length) throws IOException {
        //noop
    }

    @Override
    public void putBytesTo(final int index, final DataInput src, final int length) throws IOException {
        throw newReadOnlyException();
    }

    @Override
    public void putBytesTo(final int index, final InputStream src, final int length) throws IOException {
        throw newReadOnlyException();
    }

    @Override
    public void putBytesTo(final int index, final ReadableByteChannel src, final int length) throws IOException {
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
    public IByteBuffer clone(final int index, final int length) {
        if (index == 0 && length == capacity) {
            return this;
        } else {
            return new FakeAllocatorBuffer(length);
        }
    }

    @Override
    public void clear(final byte value, final int index, final int length) {
        //noop
    }

    //CHECKSTYLE:OFF
    @Override
    public IByteBuffer clone() {
        //CHECKSTYLE:ON
        return this;
    }

}
