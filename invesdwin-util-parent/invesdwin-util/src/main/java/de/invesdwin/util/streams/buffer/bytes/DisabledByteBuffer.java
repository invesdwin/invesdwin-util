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
import org.agrona.concurrent.AtomicBuffer;
import org.agrona.concurrent.UnsafeBuffer;

import de.invesdwin.util.math.Bytes;
import de.invesdwin.util.streams.EmptyInputStream;
import de.invesdwin.util.streams.EmptyOutputStream;
import de.invesdwin.util.streams.buffer.memory.EmptyMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.ICloseableMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;

@Immutable
public final class DisabledByteBuffer implements ICloseableByteBuffer {

    public static final java.nio.ByteBuffer EMPTY_BYTE_BUFFER = java.nio.ByteBuffer.allocate(0);
    public static final java.nio.ByteBuffer EMPTY_DIRECT_BYTE_BUFFER = java.nio.ByteBuffer.allocateDirect(0);

    public static final AtomicBuffer EMPTY_AGRONA_BUFFER = new UnsafeBuffer(Bytes.EMPTY_ARRAY);
    public static final AtomicBuffer EMPTY_DIRECT_AGRONA_BUFFER = new UnsafeBuffer(EMPTY_DIRECT_BYTE_BUFFER);

    public static final DisabledByteBuffer INSTANCE = new DisabledByteBuffer();

    private DisabledByteBuffer() {}

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
    public void putLong(final int index, final long value) {}

    @Override
    public void putInt(final int index, final int value) {}

    @Override
    public void putDouble(final int index, final double value) {}

    @Override
    public void putFloat(final int index, final float value) {}

    @Override
    public void putShort(final int index, final short value) {}

    @Override
    public void putChar(final int index, final char value) {}

    @Override
    public void putLongReverse(final int index, final long value) {}

    @Override
    public void putIntReverse(final int index, final int value) {}

    @Override
    public void putDoubleReverse(final int index, final double value) {}

    @Override
    public void putFloatReverse(final int index, final float value) {}

    @Override
    public void putShortReverse(final int index, final short value) {}

    @Override
    public void putCharReverse(final int index, final char value) {}

    @Override
    public void putByte(final int index, final byte value) {}

    @Override
    public void putBytes(final int index, final byte[] src) {}

    @Override
    public void putBytes(final int index, final byte[] src, final int srcIndex, final int length) {}

    @Override
    public void putBytes(final int index, final java.nio.ByteBuffer srcBuffer, final int srcIndex, final int length) {}

    @Override
    public void putBytes(final int index, final DirectBuffer srcBuffer, final int srcIndex, final int length) {}

    @Override
    public void putBytes(final int index, final IByteBuffer srcBuffer, final int srcIndex, final int length) {}

    @Override
    public void putBytes(final int index, final IMemoryBuffer srcBuffer, final long srcIndex, final int length) {}

    @Override
    public long addressOffset() {
        return 0;
    }

    @Override
    public byte[] byteArray() {
        return Bytes.EMPTY_ARRAY;
    }

    @Override
    public java.nio.ByteBuffer nioByteBuffer() {
        return EMPTY_BYTE_BUFFER;
    }

    @Override
    public int capacity() {
        return 0;
    }

    @Override
    public long getLong(final int index) {
        return 0;
    }

    @Override
    public int getInt(final int index) {
        return 0;
    }

    @Override
    public double getDouble(final int index) {
        return 0;
    }

    @Override
    public float getFloat(final int index) {
        return 0;
    }

    @Override
    public short getShort(final int index) {
        return 0;
    }

    @Override
    public char getChar(final int index) {
        return 0;
    }

    @Override
    public long getLongReverse(final int index) {
        return 0;
    }

    @Override
    public int getIntReverse(final int index) {
        return 0;
    }

    @Override
    public double getDoubleReverse(final int index) {
        return 0;
    }

    @Override
    public float getFloatReverse(final int index) {
        return 0;
    }

    @Override
    public short getShortReverse(final int index) {
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
    public void getBytes(final int index, final byte[] dst, final int dstIndex, final int length) {}

    @Override
    public void getBytes(final int index, final MutableDirectBuffer dstBuffer, final int dstIndex, final int length) {}

    @Override
    public void getBytes(final int index, final java.nio.ByteBuffer dstBuffer, final int dstIndex, final int length) {}

    @Override
    public void getBytes(final int index, final IByteBuffer dstBuffer, final int dstIndex, final int length) {}

    @Override
    public void getBytes(final int index, final IMemoryBuffer dstBuffer, final long dstIndex, final int length) {}

    @Override
    public int wrapAdjustment() {
        return 0;
    }

    @Override
    public ICloseableMemoryBuffer asMemoryBuffer() {
        return EmptyMemoryBuffer.INSTANCE;
    }

    @Override
    public ICloseableMemoryBuffer asMemoryBuffer(final int index, final int length) {
        return EmptyMemoryBuffer.INSTANCE;
    }

    @Override
    public MutableDirectBuffer asDirectBuffer() {
        return directBuffer();
    }

    @Override
    public MutableDirectBuffer asDirectBuffer(final int index, final int length) {
        return directBuffer();
    }

    @Override
    public MutableDirectBuffer directBuffer() {
        return EMPTY_AGRONA_BUFFER;
    }

    @Override
    public InputStream asInputStream(final int index, final int length) {
        return EmptyInputStream.INSTANCE;
    }

    @Override
    public OutputStream asOutputStream(final int index, final int length) {
        return EmptyOutputStream.INSTANCE;
    }

    @Override
    public byte[] asByteArray(final int index, final int length) {
        return byteArray();
    }

    @Override
    public byte[] asByteArrayCopy(final int index, final int length) {
        return byteArray();
    }

    @Override
    public ICloseableByteBuffer sliceFrom(final int index) {
        return slice(index, remaining(index));
    }

    @Override
    public IByteBuffer newSliceFrom(final int index) {
        return sliceFrom(index);
    }

    @Override
    public ICloseableByteBuffer slice(final int index, final int length) {
        return this;
    }

    @Override
    public IByteBuffer newSlice(final int index, final int length) {
        return slice(index, length);
    }

    @Override
    public String getStringAsciii(final int index, final int length) {
        return null;
    }

    @Override
    public void getStringAsciii(final int index, final int length, final Appendable dst) {}

    @Override
    public void putStringAsciii(final int index, final CharSequence value, final int valueIndex, final int length) {}

    @Override
    public String getStringUtf8(final int index, final int length) {
        return null;
    }

    @Override
    public int putStringUtf8(final int index, final String value) {
        return 0;
    }

    @Override
    public void getStringUtf8(final int index, final int length, final Appendable dst) {}

    @Override
    public void getBytesTo(final int index, final DataOutput dst, final int length) {}

    @Override
    public void getBytesTo(final int index, final OutputStream dst, final int length) {}

    @Override
    public void putBytesTo(final int index, final DataInput src, final int length) {}

    @Override
    public void putBytesTo(final int index, final InputStream src, final int length) {}

    @SuppressWarnings("unchecked")
    @Override
    public <T> T unwrap(final Class<T> type) {
        if (getClass().isAssignableFrom(type)) {
            return (T) this;
        }
        return null;
    }

    @Override
    public java.nio.ByteBuffer asNioByteBuffer(final int index, final int length) {
        return EMPTY_BYTE_BUFFER;
    }

    @Override
    public void getBytesTo(final int index, final WritableByteChannel dst, final int length) throws IOException {}

    @Override
    public void putBytesTo(final int index, final ReadableByteChannel src, final int length) throws IOException {}

    @Override
    public void clear(final byte value, final int index, final int length) {}

    //CHECKSTYLE:OFF
    @Override
    public IByteBuffer clone() {
        //CHECKSTYLE:ON
        return this;
    }

    @Override
    public IByteBuffer clone(final int index, final int length) {
        return this;
    }

    @Override
    public ICloseableByteBuffer ensureCapacity(final int desiredCapacity) {
        ByteBuffers.ensureCapacity(this, desiredCapacity);
        return this;
    }

    @Override
    public String toString() {
        return ByteBuffers.toString(this);
    }

    @Override
    public void close() {
        //noop
    }

    @Override
    public ICloseableByteBuffer asImmutableSlice() {
        return this;
    }

}
