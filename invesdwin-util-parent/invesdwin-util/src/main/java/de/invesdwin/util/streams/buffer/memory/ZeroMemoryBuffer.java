package de.invesdwin.util.streams.buffer.memory;

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
import de.invesdwin.util.streams.buffer.bytes.ICloseableByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.ZeroByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.stream.ByteBufferInputStream;

@NotThreadSafe
public class ZeroMemoryBuffer implements ICloseableMemoryBuffer {

    public static final ZeroMemoryBuffer INSTANCE = new ZeroMemoryBuffer();

    @Override
    public int getId() {
        return ID_ZERO;
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
        return ZeroByteBuffer.ZERO_ARRAY.length;
    }

    private IndexOutOfBoundsException newZeroException() {
        return new IndexOutOfBoundsException("one zero byte only");
    }

    @Override
    public long getLong(final long index) {
        throw newZeroException();
    }

    @Override
    public int getInt(final long index) {
        throw newZeroException();
    }

    @Override
    public double getDouble(final long index) {
        throw newZeroException();
    }

    @Override
    public float getFloat(final long index) {
        throw newZeroException();
    }

    @Override
    public short getShort(final long index) {
        throw newZeroException();
    }

    @Override
    public char getChar(final long index) {
        throw newZeroException();
    }

    @Override
    public long getLongReverse(final long index) {
        throw newZeroException();
    }

    @Override
    public int getIntReverse(final long index) {
        throw newZeroException();
    }

    @Override
    public double getDoubleReverse(final long index) {
        throw newZeroException();
    }

    @Override
    public float getFloatReverse(final long index) {
        throw newZeroException();
    }

    @Override
    public short getShortReverse(final long index) {
        throw newZeroException();
    }

    @Override
    public char getCharReverse(final long index) {
        throw newZeroException();
    }

    @Override
    public byte getByte(final long index) {
        if (index == 0) {
            return ZeroByteBuffer.ZERO_BYTE;
        } else {
            throw newZeroException();
        }
    }

    @Override
    public void getBytes(final long index, final byte[] dst, final int dstIndex, final int length) {
        for (int i = 0; i < length; i++) {
            dst[dstIndex + i] = getByte(index + i);
        }
    }

    @Override
    public void getBytes(final long index, final MutableDirectBuffer dstBuffer, final int dstIndex, final int length) {
        for (int i = 0; i < length; i++) {
            dstBuffer.putByte(dstIndex + i, getByte(index + i));
        }
    }

    @Override
    public void getBytes(final long index, final IByteBuffer dstBuffer, final int dstIndex, final int length) {
        for (int i = 0; i < length; i++) {
            dstBuffer.putByte(dstIndex + i, getByte(index + i));
        }
    }

    @Override
    public void getBytes(final long index, final IMemoryBuffer dstBuffer, final long dstIndex, final long length) {
        for (int i = 0; i < length; i++) {
            dstBuffer.putByte(dstIndex + i, getByte(index + i));
        }
    }

    @Override
    public void getBytes(final long index, final java.nio.ByteBuffer dstBuffer, final int dstIndex, final int length) {
        for (int i = 0; i < length; i++) {
            dstBuffer.put(dstIndex + i, getByte(index + i));
        }
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
        throw newZeroException();
    }

    @Override
    public void putInt(final long index, final int value) {
        throw newZeroException();
    }

    @Override
    public void putDouble(final long index, final double value) {
        throw newZeroException();
    }

    @Override
    public void putFloat(final long index, final float value) {
        throw newZeroException();
    }

    @Override
    public void putShort(final long index, final short value) {
        throw newZeroException();
    }

    @Override
    public void putChar(final long index, final char value) {
        throw newZeroException();
    }

    @Override
    public void putLongReverse(final long index, final long value) {
        throw newZeroException();
    }

    @Override
    public void putIntReverse(final long index, final int value) {
        throw newZeroException();
    }

    @Override
    public void putDoubleReverse(final long index, final double value) {
        throw newZeroException();
    }

    @Override
    public void putFloatReverse(final long index, final float value) {
        throw newZeroException();
    }

    @Override
    public void putShortReverse(final long index, final short value) {
        throw newZeroException();
    }

    @Override
    public void putCharReverse(final long index, final char value) {
        throw newZeroException();
    }

    @Override
    public void putByte(final long index, final byte value) {
        throw newZeroException();
    }

    @Override
    public void putBytes(final long index, final byte[] src, final int srcIndex, final int length) {
        throw newZeroException();
    }

    @Override
    public void putBytes(final long index, final java.nio.ByteBuffer srcBuffer, final int srcIndex, final int length) {
        throw newZeroException();
    }

    @Override
    public void putBytes(final long index, final DirectBuffer srcBuffer, final int srcIndex, final int length) {
        throw newZeroException();
    }

    @Override
    public void putBytes(final long index, final IByteBuffer srcBuffer, final int srcIndex, final int length) {
        throw newZeroException();
    }

    @Override
    public void putBytes(final long index, final IMemoryBuffer srcBuffer, final long srcIndex, final long length) {
        throw newZeroException();
    }

    @Override
    public InputStream asInputStream(final long index, final long length) {
        if (index == 0 && length == ZeroByteBuffer.ZERO_ARRAY.length) {
            return new ByteBufferInputStream(ZeroByteBuffer.INSTANCE);
        } else {
            throw newZeroException();
        }
    }

    @Override
    public OutputStream asOutputStream(final long index, final long length) {
        throw newZeroException();
    }

    @Override
    public byte[] asByteArrayCopy(final long index, final int length) {
        if (index == 0 && length == ZeroByteBuffer.ZERO_ARRAY.length) {
            return ZeroByteBuffer.ZERO_ARRAY.clone();
        } else {
            throw newZeroException();
        }
    }

    @Override
    public ICloseableByteBuffer asByteBuffer(final long index, final int length) {
        if (index == 0 && length == ZeroByteBuffer.ZERO_ARRAY.length) {
            return ZeroByteBuffer.INSTANCE;
        } else {
            throw newZeroException();
        }
    }

    @Override
    public MutableDirectBuffer asDirectBuffer(final long index, final int length) {
        if (index == 0 && length == ZeroByteBuffer.ZERO_ARRAY.length) {
            return ZeroByteBuffer.ZERO_DIRECT_BUFFER;
        } else {
            throw newZeroException();
        }
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
        if (index == 0 && length == ZeroByteBuffer.ZERO_ARRAY.length) {
            return this;
        } else {
            throw newZeroException();
        }
    }

    @Override
    public IMemoryBuffer newSlice(final long index, final long length) {
        return slice(index, length);
    }

    @Override
    public String getStringAsciii(final long index, final int length) {
        throw newZeroException();
    }

    @Override
    public void getStringAsciii(final long index, final int length, final Appendable dst) {
        throw newZeroException();
    }

    @Override
    public void putStringAsciii(final long index, final CharSequence value, final int valueIndex, final int length) {
        throw newZeroException();
    }

    @Override
    public int putStringUtf8(final long index, final String value) {
        throw newZeroException();
    }

    @Override
    public String getStringUtf8(final long index, final int length) {
        throw newZeroException();
    }

    @Override
    public void getStringUtf8(final long index, final int length, final Appendable dst) {
        throw newZeroException();
    }

    @Override
    public void getBytesTo(final long index, final DataOutput dst, final long length) throws IOException {
        if (index == 0 && length == ZeroByteBuffer.ZERO_ARRAY.length) {
            dst.write(ZeroByteBuffer.ZERO_ARRAY);
        } else {
            throw newZeroException();
        }
    }

    @Override
    public void getBytesTo(final long index, final OutputStream dst, final long length) throws IOException {
        if (index == 0 && length == ZeroByteBuffer.ZERO_ARRAY.length) {
            dst.write(ZeroByteBuffer.ZERO_ARRAY);
        } else {
            throw newZeroException();
        }
    }

    @Override
    public void putBytesTo(final long index, final DataInput src, final long length) throws IOException {
        throw newZeroException();
    }

    @Override
    public void putBytesTo(final long index, final InputStream src, final long length) throws IOException {
        throw newZeroException();
    }

    @Override
    public void clear(final byte value, final long index, final long length) {
        throw newZeroException();
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
    public java.nio.ByteBuffer asNioByteBuffer(final long index, final int length) {
        if (index == 0 && length == ZeroByteBuffer.ZERO_ARRAY.length) {
            return ZeroByteBuffer.ZERO_BYTE_BUFFER;
        } else {
            throw newZeroException();
        }
    }

    @Override
    public void getBytesTo(final long index, final WritableByteChannel dst, final long length) throws IOException {
        if (index == 0 && length == ZeroByteBuffer.ZERO_ARRAY.length) {
            dst.write(ZeroByteBuffer.ZERO_BYTE_BUFFER);
        } else {
            throw newZeroException();
        }
    }

    @Override
    public void putBytesTo(final long index, final ReadableByteChannel src, final long length) throws IOException {
        throw newZeroException();
    }

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
        if (index == 0 && length == 0) {
            return this;
        } else {
            throw newZeroException();
        }
    }

    @Override
    public ICloseableMemoryBuffer asImmutableSlice() {
        return this;
    }

}
