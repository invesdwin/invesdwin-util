package de.invesdwin.util.streams.buffer.extend;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteOrder;

import javax.annotation.concurrent.NotThreadSafe;

import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.agrona.io.DirectBufferInputStream;
import org.agrona.io.DirectBufferOutputStream;

import de.invesdwin.util.streams.buffer.ByteBuffers;
import de.invesdwin.util.streams.buffer.IByteBuffer;
import de.invesdwin.util.streams.buffer.delegate.slice.mutable.factory.FixedMutableSlicedDelegateByteBufferFactory;
import de.invesdwin.util.streams.buffer.delegate.slice.mutable.factory.IMutableSlicedDelegateByteBufferFactory;

@NotThreadSafe
public class UnsafeByteBuffer extends UnsafeBuffer implements IByteBuffer {

    private IMutableSlicedDelegateByteBufferFactory mutableSliceFactory;

    public UnsafeByteBuffer() {
        super();
    }

    public UnsafeByteBuffer(final byte[] buffer) {
        super(ByteBuffers.assertBuffer(buffer));
    }

    public UnsafeByteBuffer(final byte[] buffer, final int offset, final int length) {
        super(ByteBuffers.assertBuffer(buffer), offset, length);
    }

    public UnsafeByteBuffer(final java.nio.ByteBuffer buffer) {
        super(ByteBuffers.assertBuffer(buffer));
    }

    public UnsafeByteBuffer(final java.nio.ByteBuffer buffer, final int offset, final int length) {
        super(ByteBuffers.assertBuffer(buffer), offset, length);
    }

    public UnsafeByteBuffer(final DirectBuffer buffer) {
        super(ByteBuffers.assertBuffer(buffer));
    }

    public UnsafeByteBuffer(final DirectBuffer buffer, final int offset, final int length) {
        super(ByteBuffers.assertBuffer(buffer), offset, length);
    }

    public UnsafeByteBuffer(final long address, final int length) {
        super(ByteBuffers.assertBuffer(address), length);
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public ByteOrder getOrder() {
        return ByteBuffers.DEFAULT_ORDER;
    }

    @Override
    public MutableDirectBuffer directBuffer() {
        return this;
    }

    @Override
    public void getBytes(final int index, final IByteBuffer dstBuffer, final int dstIndex, final int length) {
        //wrapadjustment only needed for byteArray
        if (dstBuffer.directBuffer() != null) {
            getBytes(index, dstBuffer.directBuffer(), dstIndex, length);
        } else if (dstBuffer.byteBuffer() != null) {
            getBytes(index, dstBuffer.byteBuffer(), dstIndex, length);
        } else if (dstBuffer.byteArray() != null) {
            getBytes(index, dstBuffer.byteArray(), dstIndex + dstBuffer.wrapAdjustment(), length);
        } else {
            for (int i = 0; i < length; i++) {
                dstBuffer.putByte(dstIndex + i, getByte(index + i));
            }
        }
    }

    @Override
    public void putBytes(final int index, final IByteBuffer srcBuffer, final int srcIndex, final int length) {
        //wrapadjustment only needed for byteArray
        if (srcBuffer.directBuffer() != null) {
            putBytes(index, srcBuffer.directBuffer(), srcIndex, length);
        } else if (srcBuffer.byteBuffer() != null) {
            putBytes(index, srcBuffer.byteBuffer(), srcIndex, length);
        } else if (srcBuffer.byteArray() != null) {
            putBytes(index, srcBuffer.byteArray(), srcIndex + srcBuffer.wrapAdjustment(), length);
        } else {
            for (int i = 0; i < length; i++) {
                putByte(index + i, srcBuffer.getByte(srcIndex + i));
            }
        }
    }

    @Override
    public InputStream asInputStream(final int index, final int length) {
        return new DirectBufferInputStream(this, index, length);
    }

    @Override
    public OutputStream asOutputStream(final int index, final int length) {
        return new DirectBufferOutputStream(this, index, length);
    }

    @Override
    public byte[] asByteArray() {
        if (wrapAdjustment() == 0) {
            final byte[] bytes = byteArray();
            if (bytes != null) {
                return bytes;
            }
            final java.nio.ByteBuffer byteBuffer = byteBuffer();
            if (byteBuffer != null) {
                final byte[] array = byteBuffer.array();
                if (array != null) {
                    return array;
                }
            }
        }
        return ByteBuffers.asByteArrayCopyGet(this, 0, capacity());
    }

    @Override
    public byte[] asByteArrayCopy() {
        if (wrapAdjustment() == 0) {
            final byte[] bytes = byteArray();
            if (bytes != null) {
                return bytes.clone();
            }
            final java.nio.ByteBuffer byteBuffer = byteBuffer();
            if (byteBuffer != null) {
                final byte[] array = byteBuffer.array();
                if (array != null) {
                    return array.clone();
                }
            }
        }
        return ByteBuffers.asByteArrayCopyGet(this, 0, capacity());
    }

    @Override
    public byte[] asByteArray(final int index, final int length) {
        if (wrapAdjustment() == 0 && index == 0 && length == capacity()) {
            final byte[] bytes = byteArray();
            if (bytes != null) {
                if (bytes.length != length) {
                    return ByteBuffers.asByteArrayCopyGet(this, index, length);
                }
                return bytes;
            }
            final java.nio.ByteBuffer byteBuffer = byteBuffer();
            if (byteBuffer != null) {
                final byte[] array = byteBuffer.array();
                if (array != null) {
                    if (array.length != length) {
                        return ByteBuffers.asByteArrayCopyGet(this, index, length);
                    }
                    return array;
                }
            }
        }
        return ByteBuffers.asByteArrayCopyGet(this, index, length);
    }

    @Override
    public byte[] asByteArrayCopy(final int index, final int length) {
        if (wrapAdjustment() == 0 && index == 0 && length == capacity()) {
            final byte[] bytes = byteArray();
            if (bytes != null) {
                if (bytes.length != length) {
                    return ByteBuffers.asByteArrayCopyGet(this, index, length);
                }
                return bytes.clone();
            }
            final java.nio.ByteBuffer byteBuffer = byteBuffer();
            if (byteBuffer != null) {
                final byte[] array = byteBuffer.array();
                if (array != null) {
                    if (array.length != length) {
                        return ByteBuffers.asByteArrayCopyGet(this, index, length);
                    }
                    return array.clone();
                }
            }
        }
        return ByteBuffers.asByteArrayCopyGet(this, index, length);
    }

    @Override
    public MutableDirectBuffer asDirectBuffer() {
        return directBuffer();
    }

    @Override
    public MutableDirectBuffer asDirectBuffer(final int index, final int length) {
        if (index == 0 && length == capacity()) {
            return this;
        } else {
            return new UnsafeBuffer(this, index, length);
        }
    }

    private IMutableSlicedDelegateByteBufferFactory getMutableSliceFactory() {
        if (mutableSliceFactory == null) {
            mutableSliceFactory = new FixedMutableSlicedDelegateByteBufferFactory(this);
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
        return newSlice(index, remaining(index));
    }

    @Override
    public IByteBuffer newSlice(final int index, final int length) {
        if (index == 0 && length == capacity()) {
            return this;
        } else {
            return new UnsafeByteBuffer(this, index, length);
        }
    }

    @Override
    public String getStringAsciii(final int index, final int length) {
        return getStringWithoutLengthAscii(index, length);
    }

    @Override
    public void getStringAsciii(final int index, final int length, final Appendable dst) {
        getStringWithoutLengthAscii(index, length, dst);
    }

    @Override
    public void putStringAsciii(final int index, final CharSequence value, final int valueIndex, final int length) {
        putStringWithoutLengthAscii(index, value, valueIndex, length);
    }

    @Override
    public String getStringUtf8(final int index, final int length) {
        return getStringWithoutLengthUtf8(index, length);
    }

    @Override
    public int putStringUtf8(final int index, final String value) {
        return putStringWithoutLengthUtf8(index, value);
    }

    @Override
    public void getStringUtf8(final int index, final int length, final Appendable dst) {
        final String string = getStringWithoutLengthUtf8(index, length);
        try {
            dst.append(string);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void getBytesTo(final int index, final DataOutput dst, final int length) throws IOException {
        int i = index;
        while (i < length) {
            final byte b = getByte(i);
            dst.write(b);
            i++;
        }
    }

    @Override
    public void getBytesTo(final int index, final OutputStream dst, final int length) throws IOException {
        int i = index;
        while (i < length) {
            final byte b = getByte(i);
            dst.write(b);
            i++;
        }
    }

    @Override
    public void putBytesTo(final int index, final DataInput src, final int length) throws IOException {
        int i = index;
        while (i < length) {
            final byte b = src.readByte();
            putByte(i, b);
            i++;
        }
    }

    @Override
    public void putBytesTo(final int index, final InputStream src, final int length) throws IOException {
        int i = index;
        while (i < length) {
            final int result = src.read();
            if (result == -1) {
                throw ByteBuffers.newPutBytesToEOF();
            }
            putByte(i, (byte) result);
            i++;
        }
    }

    @Override
    public void putLong(final int index, final long value) {
        putLong(index, value, getOrder());
    }

    @Override
    public void putInt(final int index, final int value) {
        putInt(index, value, getOrder());
    }

    @Override
    public void putDouble(final int index, final double value) {
        putDouble(index, value, getOrder());
    }

    @Override
    public void putFloat(final int index, final float value) {
        putFloat(index, value, getOrder());
    }

    @Override
    public void putShort(final int index, final short value) {
        putShort(index, value, getOrder());
    }

    @Override
    public void putChar(final int index, final char value) {
        putChar(index, value, getOrder());
    }

    @Override
    public long getLong(final int index) {
        return getLong(index, getOrder());
    }

    @Override
    public int getInt(final int index) {
        return getInt(index, getOrder());
    }

    @Override
    public double getDouble(final int index) {
        return getDouble(index, getOrder());
    }

    @Override
    public float getFloat(final int index) {
        return getFloat(index, getOrder());
    }

    @Override
    public short getShort(final int index) {
        return getShort(index, getOrder());
    }

    @Override
    public char getChar(final int index) {
        return getChar(index, getOrder());
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
    public java.nio.ByteBuffer asByteBuffer() {
        final java.nio.ByteBuffer byteBuffer = byteBuffer();
        if (byteBuffer != null) {
            return byteBuffer;
        }
        final byte[] array = byteArray();
        if (array != null) {
            final java.nio.ByteBuffer arrayBuffer = java.nio.ByteBuffer.wrap(array, wrapAdjustment(), capacity());
            return arrayBuffer;
        }
        final long address = addressOffset();
        return ByteBuffers.asDirectByteBuffer(address, capacity());
    }

    @Override
    public java.nio.ByteBuffer asByteBuffer(final int index, final int length) {
        final java.nio.ByteBuffer buffer = asByteBuffer();
        if (index == 0 && length == capacity()) {
            return buffer;
        } else {
            return ByteBuffers.slice(buffer, index, length);
        }
    }

    @Override
    public void clear(final byte value, final int index, final int length) {
        super.setMemory(index, length, value);
    }

    @Override
    public String toString() {
        return ByteBuffers.toString(this);
    }

    //CHECKSTYLE:OFF
    @Override
    public IByteBuffer clone() {
        //CHECKSTYLE:ON
        return ByteBuffers.wrap(asByteArrayCopy());
    }

    @Override
    public IByteBuffer clone(final int index, final int length) {
        return ByteBuffers.wrap(asByteArrayCopy(index, length));
    }

    @Override
    public void ensureCapacity(final int desiredCapacity) {
        checkLimit(desiredCapacity);
    }

}