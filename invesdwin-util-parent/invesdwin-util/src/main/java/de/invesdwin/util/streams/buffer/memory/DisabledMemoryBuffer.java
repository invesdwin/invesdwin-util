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

import de.invesdwin.util.math.Bytes;
import de.invesdwin.util.streams.EmptyInputStream;
import de.invesdwin.util.streams.EmptyOutputStream;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.EmptyByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.ICloseableByteBuffer;

@Immutable
public final class DisabledMemoryBuffer implements ICloseableMemoryBuffer {

    public static final DisabledMemoryBuffer INSTANCE = new DisabledMemoryBuffer();

    private DisabledMemoryBuffer() {}

    @Override
    public ByteOrder getOrder() {
        return ByteBuffers.DEFAULT_ORDER;
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    @Override
    public boolean isExpandable() {
        return false;
    }

    @Override
    public void putLong(final long index, final long value) {}

    @Override
    public void putInt(final long index, final int value) {}

    @Override
    public void putDouble(final long index, final double value) {}

    @Override
    public void putFloat(final long index, final float value) {}

    @Override
    public void putShort(final long index, final short value) {}

    @Override
    public void putChar(final long index, final char value) {}

    @Override
    public void putLongReverse(final long index, final long value) {}

    @Override
    public void putIntReverse(final long index, final int value) {}

    @Override
    public void putDoubleReverse(final long index, final double value) {}

    @Override
    public void putFloatReverse(final long index, final float value) {}

    @Override
    public void putShortReverse(final long index, final short value) {}

    @Override
    public void putCharReverse(final long index, final char value) {}

    @Override
    public void putByte(final long index, final byte value) {}

    @Override
    public void putBytes(final long index, final byte[] src) {}

    @Override
    public void putBytes(final long index, final byte[] src, final int srcIndex, final int length) {}

    @Override
    public void putBytes(final long index, final java.nio.ByteBuffer srcBuffer, final int srcIndex, final int length) {}

    @Override
    public void putBytes(final long index, final DirectBuffer srcBuffer, final int srcIndex, final int length) {}

    @Override
    public void putBytes(final long index, final IByteBuffer srcBuffer, final int srcIndex, final int length) {}

    @Override
    public void putBytes(final long index, final IMemoryBuffer srcBuffer, final long srcIndex, final long length) {}

    @Override
    public long addressOffset() {
        return 0;
    }

    @Override
    public long capacity() {
        return 0;
    }

    @Override
    public long getLong(final long index) {
        return 0;
    }

    @Override
    public int getInt(final long index) {
        return 0;
    }

    @Override
    public double getDouble(final long index) {
        return 0;
    }

    @Override
    public float getFloat(final long index) {
        return 0;
    }

    @Override
    public short getShort(final long index) {
        return 0;
    }

    @Override
    public char getChar(final long index) {
        return 0;
    }

    @Override
    public long getLongReverse(final long index) {
        return 0;
    }

    @Override
    public int getIntReverse(final long index) {
        return 0;
    }

    @Override
    public double getDoubleReverse(final long index) {
        return 0;
    }

    @Override
    public float getFloatReverse(final long index) {
        return 0;
    }

    @Override
    public short getShortReverse(final long index) {
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
    public void getBytes(final long index, final byte[] dst, final int dstIndex, final int length) {}

    @Override
    public void getBytes(final long index, final MutableDirectBuffer dstBuffer, final int dstIndex, final int length) {}

    @Override
    public void getBytes(final long index, final java.nio.ByteBuffer dstBuffer, final int dstIndex, final int length) {}

    @Override
    public void getBytes(final long index, final IByteBuffer dstBuffer, final int dstIndex, final int length) {}

    @Override
    public void getBytes(final long index, final IMemoryBuffer dstBuffer, final long dstIndex, final long length) {}

    @Override
    public long wrapAdjustment() {
        return 0;
    }

    @Override
    public MutableDirectBuffer asDirectBuffer(final long index, final int length) {
        return EmptyByteBuffer.EMPTY_AGRONA_BUFFER;
    }

    @Override
    public ICloseableByteBuffer asByteBuffer(final long index, final int length) {
        return EmptyByteBuffer.INSTANCE;
    }

    @Override
    public InputStream asInputStream(final long index, final long length) {
        return EmptyInputStream.INSTANCE;
    }

    @Override
    public OutputStream asOutputStream(final long index, final long length) {
        return EmptyOutputStream.INSTANCE;
    }

    @Override
    public byte[] asByteArrayCopy(final long index, final int length) {
        return Bytes.EMPTY_ARRAY;
    }

    @Override
    public ICloseableMemoryBuffer sliceFrom(final long index) {
        return slice(index, remaining(index));
    }

    @Override
    public IMemoryBuffer newSliceFrom(final long index) {
        return sliceFrom(index);
    }

    @Override
    public ICloseableMemoryBuffer slice(final long index, final long length) {
        return this;
    }

    @Override
    public IMemoryBuffer newSlice(final long index, final long length) {
        return slice(index, length);
    }

    @Override
    public String getStringAsciii(final long index, final int length) {
        return null;
    }

    @Override
    public void getStringAsciii(final long index, final int length, final Appendable dst) {}

    @Override
    public void putStringAsciii(final long index, final CharSequence value, final int valueIndex, final int length) {}

    @Override
    public String getStringUtf8(final long index, final int length) {
        return null;
    }

    @Override
    public int putStringUtf8(final long index, final String value) {
        return 0;
    }

    @Override
    public void getStringUtf8(final long index, final int length, final Appendable dst) {}

    @Override
    public void getBytesTo(final long index, final DataOutput dst, final long length) {}

    @Override
    public void getBytesTo(final long index, final OutputStream dst, final long length) {}

    @Override
    public void putBytesTo(final long index, final DataInput src, final long length) {}

    @Override
    public void putBytesTo(final long index, final InputStream src, final long length) {}

    @SuppressWarnings("unchecked")
    @Override
    public <T> T unwrap(final Class<T> type) {
        if (getClass().isAssignableFrom(type)) {
            return (T) this;
        }
        return null;
    }

    @Override
    public java.nio.ByteBuffer asNioByteBuffer(final long index, final int length) {
        return EmptyByteBuffer.EMPTY_BYTE_BUFFER;
    }

    @Override
    public void getBytesTo(final long index, final WritableByteChannel dst, final long length) throws IOException {}

    @Override
    public void putBytesTo(final long index, final ReadableByteChannel src, final long length) throws IOException {}

    @Override
    public void clear(final byte value, final long index, final long length) {}

    @Override
    public ICloseableMemoryBuffer ensureCapacity(final long desiredCapacity) {
        MemoryBuffers.ensureCapacity(this, desiredCapacity);
        return this;
    }

    @Override
    public String toString() {
        return MemoryBuffers.toString(this);
    }

    @Override
    public void close() {
        //noop
    }

    @Override
    public IMemoryBuffer clone(final long index, final int length) {
        return this;
    }

    @Override
    public ICloseableMemoryBuffer asImmutableSlice() {
        return this;
    }

}
