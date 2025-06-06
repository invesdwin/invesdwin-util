package de.invesdwin.util.streams.buffer.bytes;

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
import org.agrona.concurrent.AtomicBuffer;
import org.agrona.concurrent.UnsafeBuffer;

import de.invesdwin.util.streams.buffer.bytes.stream.ByteBufferInputStream;
import de.invesdwin.util.streams.buffer.memory.ClosedMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.ICloseableMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;

@NotThreadSafe
public class ClosedByteBuffer implements ICloseableByteBuffer {

    /**
     * This is also read from a closed socket.
     */
    public static final byte CLOSED_BYTE = (byte) -1;
    public static final byte[] CLOSED_ARRAY = new byte[] { CLOSED_BYTE };

    public static final java.nio.ByteBuffer CLOSED_BYTE_BUFFER = java.nio.ByteBuffer.wrap(CLOSED_ARRAY);
    public static final AtomicBuffer CLOSED_DIRECT_BUFFER = new UnsafeBuffer(CLOSED_ARRAY);

    public static final ClosedByteBuffer INSTANCE = new ClosedByteBuffer();

    @Override
    public int getId() {
        return ID_CLOSED;
    }

    @Override
    public ByteOrder getOrder() {
        return ByteBuffers.DEFAULT_ORDER;
    }

    public static boolean isClosed(final DirectBuffer buffer, final int index, final int length) {
        return length == 1 && buffer.getByte(index) == CLOSED_BYTE;
    }

    public static boolean isClosed(final java.nio.ByteBuffer buffer, final int index, final int length) {
        return length == 1 && buffer.get(index) == CLOSED_BYTE;
    }

    public static boolean isClosed(final IByteBuffer buffer, final int index, final int length) {
        return length == 1 && buffer.getByte(index) == CLOSED_BYTE;
    }

    public static boolean isClosed(final IByteBuffer buffer) {
        return isClosed(buffer, 0, buffer.capacity());
    }

    public static boolean isClosed(final IMemoryBuffer buffer, final long index, final long length) {
        return length == 1 && buffer.getByte(index) == CLOSED_BYTE;
    }

    public static boolean isClosed(final IMemoryBuffer buffer) {
        return isClosed(buffer, 0, buffer.capacity());
    }

    public static boolean isClosed(final byte[] bytes, final int index, final int length) {
        return length == 1 && bytes[index] == CLOSED_BYTE;
    }

    public static boolean isClosed(final byte[] bytes) {
        return isClosed(bytes, 0, bytes.length);
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
        return CLOSED_DIRECT_BUFFER;
    }

    @Override
    public byte[] byteArray() {
        return CLOSED_ARRAY;
    }

    @Override
    public java.nio.ByteBuffer nioByteBuffer() {
        return CLOSED_BYTE_BUFFER;
    }

    @Override
    public int capacity() {
        return CLOSED_ARRAY.length;
    }

    private IndexOutOfBoundsException newClosedException() {
        return new IndexOutOfBoundsException("closed");
    }

    @Override
    public long getLong(final int index) {
        throw newClosedException();
    }

    @Override
    public int getInt(final int index) {
        throw newClosedException();
    }

    @Override
    public double getDouble(final int index) {
        throw newClosedException();
    }

    @Override
    public float getFloat(final int index) {
        throw newClosedException();
    }

    @Override
    public short getShort(final int index) {
        throw newClosedException();
    }

    @Override
    public char getChar(final int index) {
        throw newClosedException();
    }

    @Override
    public long getLongReverse(final int index) {
        throw newClosedException();
    }

    @Override
    public int getIntReverse(final int index) {
        throw newClosedException();
    }

    @Override
    public double getDoubleReverse(final int index) {
        throw newClosedException();
    }

    @Override
    public float getFloatReverse(final int index) {
        throw newClosedException();
    }

    @Override
    public short getShortReverse(final int index) {
        throw newClosedException();
    }

    @Override
    public char getCharReverse(final int index) {
        throw newClosedException();
    }

    @Override
    public byte getByte(final int index) {
        if (index == 0) {
            return CLOSED_BYTE;
        } else {
            throw newClosedException();
        }
    }

    @Override
    public void getBytes(final int index, final byte[] dst, final int dstIndex, final int length) {
        CLOSED_DIRECT_BUFFER.getBytes(index, dst, dstIndex, length);
    }

    @Override
    public void getBytes(final int index, final MutableDirectBuffer dstBuffer, final int dstIndex, final int length) {
        CLOSED_DIRECT_BUFFER.getBytes(index, dstBuffer, dstIndex, length);
    }

    @Override
    public void getBytes(final int index, final IByteBuffer dstBuffer, final int dstIndex, final int length) {
        final MutableDirectBuffer directBuffer = dstBuffer.directBuffer();
        if (directBuffer != null) {
            CLOSED_DIRECT_BUFFER.getBytes(index, directBuffer,
                    dstIndex + dstBuffer.wrapAdjustment() - directBuffer.wrapAdjustment(), length);
        } else if (dstBuffer.nioByteBuffer() != null) {
            CLOSED_DIRECT_BUFFER.getBytes(index, dstBuffer.nioByteBuffer(), dstIndex + dstBuffer.wrapAdjustment(),
                    length);
        } else if (dstBuffer.byteArray() != null) {
            CLOSED_DIRECT_BUFFER.getBytes(index, dstBuffer.byteArray(), dstIndex + dstBuffer.wrapAdjustment(), length);
        } else {
            for (int i = 0; i < length; i++) {
                dstBuffer.putByte(dstIndex + i, getByte(index + i));
            }
        }
    }

    @Override
    public void getBytes(final int index, final IMemoryBuffer dstBuffer, final long dstIndex, final int length) {
        for (int i = 0; i < length; i++) {
            dstBuffer.putByte(dstIndex + i, getByte(index + i));
        }
    }

    @Override
    public void getBytes(final int index, final java.nio.ByteBuffer dstBuffer, final int dstIndex, final int length) {
        CLOSED_DIRECT_BUFFER.getBytes(index, dstBuffer, dstIndex, length);
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
        throw newClosedException();
    }

    @Override
    public void putInt(final int index, final int value) {
        throw newClosedException();
    }

    @Override
    public void putDouble(final int index, final double value) {
        throw newClosedException();
    }

    @Override
    public void putFloat(final int index, final float value) {
        throw newClosedException();
    }

    @Override
    public void putShort(final int index, final short value) {
        throw newClosedException();
    }

    @Override
    public void putChar(final int index, final char value) {
        throw newClosedException();
    }

    @Override
    public void putLongReverse(final int index, final long value) {
        throw newClosedException();
    }

    @Override
    public void putIntReverse(final int index, final int value) {
        throw newClosedException();
    }

    @Override
    public void putDoubleReverse(final int index, final double value) {
        throw newClosedException();
    }

    @Override
    public void putFloatReverse(final int index, final float value) {
        throw newClosedException();
    }

    @Override
    public void putShortReverse(final int index, final short value) {
        throw newClosedException();
    }

    @Override
    public void putCharReverse(final int index, final char value) {
        throw newClosedException();
    }

    @Override
    public void putByte(final int index, final byte value) {
        throw newClosedException();
    }

    @Override
    public void putBytes(final int index, final byte[] src, final int srcIndex, final int length) {
        throw newClosedException();
    }

    @Override
    public void putBytes(final int index, final java.nio.ByteBuffer srcBuffer, final int srcIndex, final int length) {
        throw newClosedException();
    }

    @Override
    public void putBytes(final int index, final DirectBuffer srcBuffer, final int srcIndex, final int length) {
        throw newClosedException();
    }

    @Override
    public void putBytes(final int index, final IByteBuffer srcBuffer, final int srcIndex, final int length) {
        throw newClosedException();
    }

    @Override
    public void putBytes(final int index, final IMemoryBuffer srcBuffer, final long srcIndex, final int length) {
        throw newClosedException();
    }

    @Override
    public InputStream asInputStream(final int index, final int length) {
        if (index == 0 && length == ClosedByteBuffer.CLOSED_ARRAY.length) {
            return new ByteBufferInputStream(this);
        } else {
            throw newClosedException();
        }
    }

    @Override
    public OutputStream asOutputStream(final int index, final int length) {
        throw newClosedException();
    }

    @Override
    public byte[] asByteArray(final int index, final int length) {
        if (index == 0 && length == CLOSED_ARRAY.length) {
            return CLOSED_ARRAY;
        } else {
            throw newClosedException();
        }
    }

    @Override
    public byte[] asByteArrayCopy(final int index, final int length) {
        if (index == 0 && length == CLOSED_ARRAY.length) {
            return CLOSED_ARRAY.clone();
        } else {
            throw newClosedException();
        }
    }

    @Override
    public MutableDirectBuffer asDirectBuffer() {
        return directBuffer();
    }

    @Override
    public MutableDirectBuffer asDirectBuffer(final int index, final int length) {
        if (index == 0 && length == CLOSED_ARRAY.length) {
            return directBuffer();
        } else {
            throw newClosedException();
        }
    }

    @Override
    public ICloseableMemoryBuffer asMemoryBuffer() {
        return ClosedMemoryBuffer.INSTANCE;
    }

    @Override
    public ICloseableMemoryBuffer asMemoryBuffer(final int index, final int length) {
        if (index == 0 && length == CLOSED_ARRAY.length) {
            return asMemoryBuffer();
        } else {
            throw newClosedException();
        }
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
        if (index == 0 && length == CLOSED_ARRAY.length) {
            return this;
        } else {
            throw newClosedException();
        }
    }

    @Override
    public IByteBuffer newSlice(final int index, final int length) {
        return slice(index, length);
    }

    @Override
    public String getStringAsciii(final int index, final int length) {
        throw newClosedException();
    }

    @Override
    public void getStringAsciii(final int index, final int length, final Appendable dst) {
        throw newClosedException();
    }

    @Override
    public void putStringAsciii(final int index, final CharSequence value, final int valueIndex, final int length) {
        throw newClosedException();
    }

    @Override
    public int putStringUtf8(final int index, final String value) {
        throw newClosedException();
    }

    @Override
    public String getStringUtf8(final int index, final int length) {
        throw newClosedException();
    }

    @Override
    public void getStringUtf8(final int index, final int length, final Appendable dst) {
        throw newClosedException();
    }

    @Override
    public void getBytesTo(final int index, final DataOutput dst, final int length) throws IOException {
        if (index == 0 && length == CLOSED_ARRAY.length) {
            dst.write(CLOSED_ARRAY);
        } else {
            throw newClosedException();
        }
    }

    @Override
    public void getBytesTo(final int index, final OutputStream dst, final int length) throws IOException {
        if (index == 0 && length == CLOSED_ARRAY.length) {
            dst.write(CLOSED_ARRAY);
        } else {
            throw newClosedException();
        }
    }

    @Override
    public void putBytesTo(final int index, final DataInput src, final int length) throws IOException {
        throw newClosedException();
    }

    @Override
    public void putBytesTo(final int index, final InputStream src, final int length) throws IOException {
        throw newClosedException();
    }

    @Override
    public void clear(final byte value, final int index, final int length) {
        throw newClosedException();
    }

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
        if (index == 0 && length == CLOSED_ARRAY.length) {
            return CLOSED_BYTE_BUFFER;
        } else {
            throw newClosedException();
        }
    }

    @Override
    public void getBytesTo(final int index, final WritableByteChannel dst, final int length) throws IOException {
        if (index == 0 && length == CLOSED_ARRAY.length) {
            dst.write(CLOSED_BYTE_BUFFER);
        } else {
            throw newClosedException();
        }
    }

    @Override
    public void putBytesTo(final int index, final ReadableByteChannel src, final int length) throws IOException {
        throw newClosedException();
    }

    @Override
    public ICloseableByteBuffer ensureCapacity(final int desiredCapacity) {
        ByteBuffers.ensureCapacity(this, desiredCapacity);
        return this;
    }

    //CHECKSTYLE:OFF
    @Override
    public IByteBuffer clone() {
        //CHECKSTYLE:ON
        return this;
    }

    @Override
    public IByteBuffer clone(final int index, final int length) {
        if (index == 0 && length == CLOSED_ARRAY.length) {
            return this;
        } else {
            throw newClosedException();
        }
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
