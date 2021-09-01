package de.invesdwin.util.streams.buffer.delegate;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteOrder;

import javax.annotation.concurrent.NotThreadSafe;

import org.agrona.DirectBuffer;
import org.agrona.ExpandableArrayBuffer;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.agrona.io.DirectBufferInputStream;
import org.agrona.io.DirectBufferOutputStream;

import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.streams.buffer.ByteBuffers;
import de.invesdwin.util.streams.buffer.IByteBuffer;
import de.invesdwin.util.streams.buffer.delegate.slice.SlicedFromDelegateByteBuffer;
import de.invesdwin.util.streams.buffer.delegate.slice.mutable.factory.IMutableSlicedDelegateByteBufferFactory;
import de.invesdwin.util.streams.buffer.extend.DirectExpandableByteBuffer;

@NotThreadSafe
public class AgronaDelegateMutableByteBuffer implements IByteBuffer {

    private final MutableDirectBuffer delegate;
    private IMutableSlicedDelegateByteBufferFactory mutableSliceFactory;

    public AgronaDelegateMutableByteBuffer(final MutableDirectBuffer delegate) {
        this.delegate = delegate;
    }

    public MutableDirectBuffer getDelegate() {
        return delegate;
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
    public long addressOffset() {
        return delegate.addressOffset();
    }

    @Override
    public byte[] byteArray() {
        return delegate.byteArray();
    }

    @Override
    public java.nio.ByteBuffer byteBuffer() {
        return delegate.byteBuffer();
    }

    @Override
    public int capacity() {
        return delegate.capacity();
    }

    @Override
    public long getLong(final int index) {
        return delegate.getLong(index, getOrder());
    }

    @Override
    public int getInt(final int index) {
        return delegate.getInt(index, getOrder());
    }

    @Override
    public double getDouble(final int index) {
        return delegate.getDouble(index, getOrder());
    }

    @Override
    public float getFloat(final int index) {
        return delegate.getFloat(index, getOrder());
    }

    @Override
    public short getShort(final int index) {
        return delegate.getShort(index, getOrder());
    }

    @Override
    public char getChar(final int index) {
        return delegate.getChar(index, getOrder());
    }

    @Override
    public byte getByte(final int index) {
        return delegate.getByte(index);
    }

    @Override
    public void getBytes(final int index, final byte[] dst, final int dstIndex, final int length) {
        delegate.getBytes(index, dst, dstIndex, length);
    }

    @Override
    public void getBytes(final int index, final MutableDirectBuffer dstBuffer, final int dstIndex, final int length) {
        delegate.getBytes(index, dstBuffer, dstIndex, length);
    }

    @Override
    public void getBytes(final int index, final java.nio.ByteBuffer dstBuffer, final int dstIndex, final int length) {
        delegate.getBytes(index, dstBuffer, dstIndex, length);
    }

    @Override
    public void getBytes(final int index, final IByteBuffer dstBuffer, final int dstIndex, final int length) {
        if (dstBuffer.directBuffer() != null) {
            delegate.getBytes(index, dstBuffer.directBuffer(), dstIndex + dstBuffer.wrapAdjustment(), length);
        } else if (dstBuffer.byteBuffer() != null) {
            delegate.getBytes(index, dstBuffer.byteBuffer(), dstIndex + dstBuffer.wrapAdjustment(), length);
        } else if (dstBuffer.byteArray() != null) {
            delegate.getBytes(index, dstBuffer.byteArray(), dstIndex + dstBuffer.wrapAdjustment(), length);
        } else {
            for (int i = 0; i < length; i++) {
                dstBuffer.putByte(dstIndex + i, delegate.getByte(index + i));
            }
        }
    }

    @Override
    public int wrapAdjustment() {
        return delegate.wrapAdjustment();
    }

    @Override
    public boolean isExpandable() {
        return delegate.isExpandable();
    }

    @Override
    public void putLong(final int index, final long value) {
        delegate.putLong(index, value, getOrder());
    }

    @Override
    public void putInt(final int index, final int value) {
        delegate.putInt(index, value, getOrder());
    }

    @Override
    public void putDouble(final int index, final double value) {
        delegate.putDouble(index, value, getOrder());
    }

    @Override
    public void putFloat(final int index, final float value) {
        delegate.putFloat(index, value, getOrder());
    }

    @Override
    public void putShort(final int index, final short value) {
        delegate.putShort(index, value, getOrder());
    }

    @Override
    public void putChar(final int index, final char value) {
        delegate.putChar(index, value, getOrder());
    }

    @Override
    public void putByte(final int index, final byte value) {
        delegate.putByte(index, value);
    }

    @Override
    public void putBytes(final int index, final byte[] src, final int srcIndex, final int length) {
        delegate.putBytes(index, src, srcIndex, length);
    }

    @Override
    public void putBytes(final int index, final java.nio.ByteBuffer srcBuffer, final int srcIndex, final int length) {
        delegate.putBytes(index, srcBuffer, srcIndex, length);
    }

    @Override
    public void putBytes(final int index, final DirectBuffer srcBuffer, final int srcIndex, final int length) {
        delegate.putBytes(index, srcBuffer, srcIndex, length);
    }

    @Override
    public void putBytes(final int index, final IByteBuffer srcBuffer, final int srcIndex, final int length) {
        if (srcBuffer.directBuffer() != null) {
            delegate.putBytes(index, srcBuffer.directBuffer(), srcIndex + srcBuffer.wrapAdjustment(), length);
        } else if (srcBuffer.byteBuffer() != null) {
            delegate.putBytes(index, srcBuffer.byteBuffer(), srcIndex + srcBuffer.wrapAdjustment(), length);
        } else if (srcBuffer.byteArray() != null) {
            delegate.putBytes(index, srcBuffer.byteArray(), srcIndex + srcBuffer.wrapAdjustment(), length);
        } else {
            for (int i = 0; i < length; i++) {
                delegate.putByte(index + i, srcBuffer.getByte(srcIndex + i));
            }
        }
    }

    @Override
    public MutableDirectBuffer directBuffer() {
        return delegate;
    }

    @Override
    public MutableDirectBuffer asDirectBuffer() {
        return delegate;
    }

    @Override
    public MutableDirectBuffer asDirectBuffer(final int index, final int length) {
        if (index == 0 && length == capacity()) {
            return delegate;
        } else {
            return new UnsafeBuffer(delegate, index, length);
        }
    }

    @Override
    public InputStream asInputStream(final int index, final int length) {
        return new DirectBufferInputStream(delegate, index, length);
    }

    @Override
    public OutputStream asOutputStream(final int index, final int length) {
        if (delegate.isExpandable() && index + length >= capacity()) {
            //allow output stream to actually grow the buffer
            return new DirectBufferOutputStream(delegate, index, ExpandableArrayBuffer.MAX_ARRAY_LENGTH - index);
        } else {
            return new DirectBufferOutputStream(delegate, index, length);
        }
    }

    @Override
    public byte[] asByteArray() {
        if (delegate.isExpandable()) {
            throw DirectExpandableByteBuffer.newAsByteArrayUnsupported();
        }
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
        if (delegate.isExpandable()) {
            throw DirectExpandableByteBuffer.newAsByteArrayUnsupported();
        }
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

    private IMutableSlicedDelegateByteBufferFactory getMutableSliceFactory() {
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
        if (isExpandable()) {
            return new SlicedFromDelegateByteBuffer(this, index);
        } else {
            return newSlice(index, remaining(index));
        }
    }

    @Override
    public IByteBuffer newSlice(final int index, final int length) {
        if (index == 0 && length == capacity()) {
            return this;
        } else {
            return new AgronaDelegateMutableByteBuffer(new UnsafeBuffer(delegate, index, length));
        }
    }

    @Override
    public String getStringAsciii(final int index, final int length) {
        return delegate.getStringWithoutLengthAscii(index, length);
    }

    @Override
    public void putStringAsciii(final int index, final CharSequence value, final int valueIndex, final int length) {
        delegate.putStringWithoutLengthAscii(index, value, valueIndex, length);
    }

    @Override
    public String getStringUtf8(final int index, final int length) {
        return delegate.getStringWithoutLengthUtf8(index, length);
    }

    @Override
    public void getStringAsciii(final int index, final int length, final Appendable dst) {
        delegate.getStringWithoutLengthAscii(index, length, dst);
    }

    @Override
    public int putStringUtf8(final int index, final String value) {
        return delegate.putStringWithoutLengthUtf8(index, value);
    }

    @Override
    public void getStringUtf8(final int index, final int length, final Appendable dst) {
        final String string = delegate.getStringWithoutLengthUtf8(index, length);
        try {
            dst.append(string);
        } catch (final IOException e) {
            throw Throwables.propagate(e);
        }
    }

    @Override
    public void getBytesTo(final int index, final DataOutput dst, final int length) throws IOException {
        int i = index;
        while (i < length) {
            final byte b = delegate.getByte(i);
            dst.write(b);
            i++;
        }
    }

    @Override
    public void getBytesTo(final int index, final OutputStream dst, final int length) throws IOException {
        int i = index;
        while (i < length) {
            final byte b = delegate.getByte(i);
            dst.write(b);
            i++;
        }
    }

    @Override
    public void putBytesTo(final int index, final DataInput src, final int length) throws IOException {
        int i = index;
        while (i < length) {
            final byte b = src.readByte();
            delegate.putByte(i, b);
            i++;
        }
    }

    @Override
    public void putBytesTo(final int index, final InputStream src, final int length) throws IOException {
        int i = index;
        while (i < length) {
            final int result = src.read();
            if (result < 0) {
                throw ByteBuffers.newPutBytesToEOF();
            }
            delegate.putByte(i, (byte) result);
            i++;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T unwrap(final Class<T> type) {
        if (getClass().isAssignableFrom(type)) {
            return (T) this;
        }
        if (delegate.getClass().isAssignableFrom(type)) {
            return (T) delegate;
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

}
