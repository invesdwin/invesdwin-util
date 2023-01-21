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

@Immutable
public final class EmptyMemoryBuffer implements IMemoryBuffer {

    public static final EmptyMemoryBuffer INSTANCE = new EmptyMemoryBuffer();

    private EmptyMemoryBuffer() {
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
    public boolean isExpandable() {
        return false;
    }

    @Override
    public void putLong(final long index, final long value) {
        throw newEmptyException();
    }

    @Override
    public void putInt(final long index, final int value) {
        throw newEmptyException();
    }

    @Override
    public void putDouble(final long index, final double value) {
        throw newEmptyException();
    }

    @Override
    public void putFloat(final long index, final float value) {
        throw newEmptyException();
    }

    @Override
    public void putShort(final long index, final short value) {
        throw newEmptyException();
    }

    @Override
    public void putChar(final long index, final char value) {
        throw newEmptyException();
    }

    @Override
    public void putLongReverse(final long index, final long value) {
        throw newEmptyException();
    }

    @Override
    public void putIntReverse(final long index, final int value) {
        throw newEmptyException();
    }

    @Override
    public void putDoubleReverse(final long index, final double value) {
        throw newEmptyException();
    }

    @Override
    public void putFloatReverse(final long index, final float value) {
        throw newEmptyException();
    }

    @Override
    public void putShortReverse(final long index, final short value) {
        throw newEmptyException();
    }

    @Override
    public void putCharReverse(final long index, final char value) {
        throw newEmptyException();
    }

    @Override
    public void putByte(final long index, final byte value) {
        throw newEmptyException();
    }

    @Override
    public void putBytes(final long index, final byte[] src) {
        if (index == 0 && src.length == 0) {
            return;
        }
        throw newEmptyException();
    }

    @Override
    public void putBytes(final long index, final byte[] src, final int srcIndex, final int length) {
        if (index == 0 && length == 0) {
            return;
        }
        throw newEmptyException();
    }

    @Override
    public void putBytes(final long index, final java.nio.ByteBuffer srcBuffer, final int srcIndex, final int length) {
        if (index == 0 && length == 0) {
            return;
        }
        throw newEmptyException();
    }

    @Override
    public void putBytes(final long index, final DirectBuffer srcBuffer, final int srcIndex, final int length) {
        if (index == 0 && length == 0) {
            return;
        }
        throw newEmptyException();
    }

    @Override
    public void putBytes(final long index, final IByteBuffer srcBuffer, final int srcIndex, final int length) {
        if (index == 0 && length == 0) {
            return;
        }
        throw newEmptyException();
    }

    @Override
    public void putBytes(final long index, final IMemoryBuffer srcBuffer, final long srcIndex, final long length) {
        if (index == 0 && length == 0) {
            return;
        }
        throw newEmptyException();
    }

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
        throw newEmptyException();
    }

    @Override
    public int getInt(final long index) {
        throw newEmptyException();
    }

    @Override
    public double getDouble(final long index) {
        throw newEmptyException();
    }

    @Override
    public float getFloat(final long index) {
        throw newEmptyException();
    }

    @Override
    public short getShort(final long index) {
        throw newEmptyException();
    }

    @Override
    public char getChar(final long index) {
        throw newEmptyException();
    }

    @Override
    public long getLongReverse(final long index) {
        throw newEmptyException();
    }

    @Override
    public int getIntReverse(final long index) {
        throw newEmptyException();
    }

    @Override
    public double getDoubleReverse(final long index) {
        throw newEmptyException();
    }

    @Override
    public float getFloatReverse(final long index) {
        throw newEmptyException();
    }

    @Override
    public short getShortReverse(final long index) {
        throw newEmptyException();
    }

    @Override
    public char getCharReverse(final long index) {
        throw newEmptyException();
    }

    @Override
    public byte getByte(final long index) {
        throw newEmptyException();
    }

    @Override
    public void getBytes(final long index, final byte[] dst, final int dstIndex, final int length) {
        if (index == 0 && length == 0) {
            return;
        }
        throw newEmptyException();
    }

    @Override
    public void getBytes(final long index, final MutableDirectBuffer dstBuffer, final int dstIndex, final int length) {
        if (index == 0 && length == 0) {
            return;
        }
        throw newEmptyException();
    }

    @Override
    public void getBytes(final long index, final java.nio.ByteBuffer dstBuffer, final int dstIndex, final int length) {
        if (index == 0 && length == 0) {
            return;
        }
        throw newEmptyException();
    }

    @Override
    public void getBytes(final long index, final IByteBuffer dstBuffer, final int dstIndex, final int length) {
        if (index == 0 && length == 0) {
            return;
        }
        throw newEmptyException();
    }

    @Override
    public void getBytes(final long index, final IMemoryBuffer dstBuffer, final long dstIndex, final long length) {
        if (index == 0 && length == 0) {
            return;
        }
        throw newEmptyException();
    }

    @Override
    public long wrapAdjustment() {
        return 0;
    }

    @Override
    public MutableDirectBuffer asDirectBuffer(final long index, final int length) {
        if (index == 0 && length == 0) {
            return EmptyByteBuffer.EMPTY_AGRONA_BUFFER;
        } else {
            throw newEmptyException();
        }
    }

    @Override
    public IByteBuffer asByteBuffer(final long index, final int length) {
        if (index == 0 && length == 0) {
            return EmptyByteBuffer.INSTANCE;
        } else {
            throw newEmptyException();
        }
    }

    @Override
    public InputStream asInputStream(final long index, final long length) {
        if (index == 0 && length == 0) {
            return EmptyInputStream.INSTANCE;
        } else {
            throw newEmptyException();
        }
    }

    @Override
    public OutputStream asOutputStream(final long index, final long length) {
        if (index == 0 && length == 0) {
            return EmptyOutputStream.INSTANCE;
        } else {
            throw newEmptyException();
        }
    }

    @Override
    public byte[] asByteArrayCopy(final long index, final int length) {
        if (index == 0 && length == 0) {
            return Bytes.EMPTY_ARRAY;
        } else {
            throw newEmptyException();
        }
    }

    @Override
    public IMemoryBuffer sliceFrom(final long index) {
        return newSliceFrom(index);
    }

    @Override
    public IMemoryBuffer newSliceFrom(final long index) {
        return newSlice(index, remaining(index));
    }

    @Override
    public IMemoryBuffer slice(final long index, final long length) {
        return newSlice(index, length);
    }

    @Override
    public IMemoryBuffer newSlice(final long index, final long length) {
        if (index == 0 && length == 0) {
            return this;
        } else {
            throw newEmptyException();
        }
    }

    @Override
    public String getStringAsciii(final long index, final int length) {
        if (index == 0 && length == 0) {
            return "";
        }
        throw newEmptyException();
    }

    @Override
    public void getStringAsciii(final long index, final int length, final Appendable dst) {
        if (index == 0 && length == 0) {
            return;
        }
        throw newEmptyException();
    }

    @Override
    public void putStringAsciii(final long index, final CharSequence value, final int valueIndex, final int length) {
        if (index == 0 && length == 0) {
            return;
        }
        throw newEmptyException();
    }

    @Override
    public String getStringUtf8(final long index, final int length) {
        if (index == 0 && length == 0) {
            return "";
        }
        throw newEmptyException();
    }

    @Override
    public int putStringUtf8(final long index, final String value) {
        if (index == 0 && value.length() == 0) {
            return 0;
        }
        throw newEmptyException();
    }

    @Override
    public void getStringUtf8(final long index, final int length, final Appendable dst) {
        if (index == 0 && length == 0) {
            return;
        }
        throw newEmptyException();
    }

    @Override
    public void getBytesTo(final long index, final DataOutput dst, final long length) {
        if (index == 0 && length == 0) {
            return;
        }
        throw newEmptyException();
    }

    @Override
    public void getBytesTo(final long index, final OutputStream dst, final long length) {
        if (index == 0 && length == 0) {
            return;
        }
        throw newEmptyException();
    }

    @Override
    public void putBytesTo(final long index, final DataInput src, final long length) {
        if (index == 0 && length == 0) {
            return;
        }
        throw newEmptyException();
    }

    @Override
    public void putBytesTo(final long index, final InputStream src, final long length) {
        if (index == 0 && length == 0) {
            return;
        }
        throw newEmptyException();
    }

    private IndexOutOfBoundsException newEmptyException() {
        return new IndexOutOfBoundsException("empty");
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
        if (index == 0 && length == 0) {
            return EmptyByteBuffer.EMPTY_BYTE_BUFFER;
        }
        throw newEmptyException();
    }

    @Override
    public void getBytesTo(final long index, final WritableByteChannel dst, final long length) throws IOException {
        if (index == 0 && length == 0) {
            return;
        }
        throw newEmptyException();
    }

    @Override
    public void putBytesTo(final long index, final ReadableByteChannel src, final long length) throws IOException {
        if (index == 0 && length == 0) {
            return;
        }
        throw newEmptyException();
    }

    @Override
    public void clear(final byte value, final long index, final long length) {
        if (index == 0 && length == 0) {
            return;
        }
        throw newEmptyException();
    }

    @Override
    public IMemoryBuffer ensureCapacity(final long desiredCapacity) {
        MemoryBuffers.ensureCapacity(this, desiredCapacity);
        return this;
    }

    @Override
    public String toString() {
        return MemoryBuffers.toString(this);
    }

}
