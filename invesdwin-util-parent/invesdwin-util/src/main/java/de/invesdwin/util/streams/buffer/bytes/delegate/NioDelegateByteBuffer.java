package de.invesdwin.util.streams.buffer.bytes.delegate;

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

import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.delegate.slice.mutable.factory.FixedMutableSlicedDelegateByteBufferFactory;
import de.invesdwin.util.streams.buffer.bytes.delegate.slice.mutable.factory.IMutableSlicedDelegateByteBufferFactory;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.delegate.ByteDelegateMemoryBuffer;

@NotThreadSafe
public class NioDelegateByteBuffer implements IByteBuffer {

    protected java.nio.ByteBuffer delegate;
    private java.nio.ByteBuffer reverse;
    private UnsafeBuffer directBuffer;
    private IMutableSlicedDelegateByteBufferFactory mutableSliceFactory;

    public NioDelegateByteBuffer(final byte[] bytes) {
        this(java.nio.ByteBuffer.wrap(bytes));
    }

    public NioDelegateByteBuffer(final java.nio.ByteBuffer buffer) {
        setDelegate(buffer);
    }

    public java.nio.ByteBuffer getDelegate() {
        return delegate;
    }

    public void setDelegate(final java.nio.ByteBuffer delegate) {
        if (delegate.order() != ByteBuffers.DEFAULT_ORDER) {
            this.delegate = delegate.duplicate().order(ByteBuffers.DEFAULT_ORDER);
        } else {
            this.delegate = delegate;
        }
        this.reverse = null;
        this.directBuffer = null;
    }

    public void setDelegateUnchecked(final java.nio.ByteBuffer delegate) {
        this.delegate = delegate;
        this.reverse = null;
        this.directBuffer = null;
    }

    protected java.nio.ByteBuffer getReverse() {
        if (reverse == null) {
            reverse = delegate.duplicate().order(ByteOrder.LITTLE_ENDIAN);
        }
        return reverse;
    }

    @Override
    public boolean isReadOnly() {
        return delegate.isReadOnly();
    }

    @Override
    public ByteOrder getOrder() {
        return ByteBuffers.DEFAULT_ORDER;
    }

    @Override
    public long addressOffset() {
        return ByteBuffers.addressOffset(delegate);
    }

    @Override
    public MutableDirectBuffer directBuffer() {
        if (directBuffer == null) {
            directBuffer = new UnsafeBuffer(delegate);
        }
        return directBuffer;
    }

    @Override
    public byte[] byteArray() {
        if (delegate.hasArray()) {
            return delegate.array();
        } else {
            return null;
        }
    }

    @Override
    public java.nio.ByteBuffer nioByteBuffer() {
        return delegate;
    }

    @Override
    public int capacity() {
        return delegate.capacity();
    }

    @Override
    public long getLong(final int index) {
        return delegate.getLong(index);
    }

    @Override
    public int getInt(final int index) {
        return delegate.getInt(index);
    }

    @Override
    public double getDouble(final int index) {
        return delegate.getDouble(index);
    }

    @Override
    public float getFloat(final int index) {
        return delegate.getFloat(index);
    }

    @Override
    public short getShort(final int index) {
        return delegate.getShort(index);
    }

    @Override
    public char getChar(final int index) {
        return delegate.getChar(index);
    }

    @Override
    public long getLongReverse(final int index) {
        return getReverse().getLong(index);
    }

    @Override
    public int getIntReverse(final int index) {
        return getReverse().getInt(index);
    }

    @Override
    public double getDoubleReverse(final int index) {
        return getReverse().getDouble(index);
    }

    @Override
    public float getFloatReverse(final int index) {
        return getReverse().getFloat(index);
    }

    @Override
    public short getShortReverse(final int index) {
        return getReverse().getShort(index);
    }

    @Override
    public char getCharReverse(final int index) {
        return getReverse().getChar(index);
    }

    @Override
    public byte getByte(final int index) {
        return delegate.get(index);
    }

    @Override
    public void getBytes(final int index, final byte[] dst, final int dstIndex, final int length) {
        ByteBuffers.get(delegate, index, dst, dstIndex, length);
    }

    @Override
    public void getBytes(final int index, final MutableDirectBuffer dstBuffer, final int dstIndex, final int length) {
        dstBuffer.putBytes(dstIndex, delegate, index, length);
    }

    @Override
    public void getBytes(final int index, final IByteBuffer dstBuffer, final int dstIndex, final int length) {
        dstBuffer.putBytes(dstIndex, delegate, index, length);
    }

    @Override
    public void getBytes(final int index, final IMemoryBuffer dstBuffer, final long dstIndex, final int length) {
        dstBuffer.putBytes(dstIndex, delegate, index, length);
    }

    @Override
    public void getBytes(final int index, final java.nio.ByteBuffer dstBuffer, final int dstIndex, final int length) {
        directBuffer().getBytes(index, dstBuffer, dstIndex, length);
    }

    @Override
    public MutableDirectBuffer asDirectBuffer() {
        return directBuffer();
    }

    @Override
    public MutableDirectBuffer asDirectBuffer(final int index, final int length) {
        if (index == 0 && length == capacity()) {
            return directBuffer();
        } else {
            return new UnsafeBuffer(delegate, index, length);
        }
    }

    @Override
    public IMemoryBuffer asMemoryBuffer() {
        return new ByteDelegateMemoryBuffer(this);
    }

    @Override
    public IMemoryBuffer asMemoryBuffer(final int index, final int length) {
        return new ByteDelegateMemoryBuffer(this).newSlice(index, length);
    }

    @Override
    public int wrapAdjustment() {
        return ByteBuffers.wrapAdjustment(delegate);
    }

    @Override
    public boolean isExpandable() {
        return false;
    }

    @Override
    public void putLong(final int index, final long value) {
        delegate.putLong(index, value);
    }

    @Override
    public void putInt(final int index, final int value) {
        delegate.putInt(index, value);
    }

    @Override
    public void putDouble(final int index, final double value) {
        delegate.putDouble(index, value);
    }

    @Override
    public void putFloat(final int index, final float value) {
        delegate.putFloat(index, value);
    }

    @Override
    public void putShort(final int index, final short value) {
        delegate.putShort(index, value);
    }

    @Override
    public void putChar(final int index, final char value) {
        delegate.putChar(index, value);
    }

    @Override
    public void putLongReverse(final int index, final long value) {
        getReverse().putLong(index, value);
    }

    @Override
    public void putIntReverse(final int index, final int value) {
        getReverse().putInt(index, value);
    }

    @Override
    public void putDoubleReverse(final int index, final double value) {
        getReverse().putDouble(index, value);
    }

    @Override
    public void putFloatReverse(final int index, final float value) {
        getReverse().putFloat(index, value);
    }

    @Override
    public void putShortReverse(final int index, final short value) {
        getReverse().putShort(index, value);
    }

    @Override
    public void putCharReverse(final int index, final char value) {
        getReverse().putChar(index, value);
    }

    @Override
    public void putByte(final int index, final byte value) {
        delegate.put(index, value);
    }

    @Override
    public void putBytes(final int index, final byte[] src, final int srcIndex, final int length) {
        ByteBuffers.put(delegate, index, src, srcIndex, length);
    }

    @Override
    public void putBytes(final int index, final java.nio.ByteBuffer srcBuffer, final int srcIndex, final int length) {
        directBuffer().putBytes(index, srcBuffer, srcIndex, length);
    }

    @Override
    public void putBytes(final int index, final DirectBuffer srcBuffer, final int srcIndex, final int length) {
        srcBuffer.getBytes(srcIndex, delegate, index, length);
    }

    @Override
    public void putBytes(final int index, final IByteBuffer srcBuffer, final int srcIndex, final int length) {
        srcBuffer.getBytes(srcIndex, delegate, index, length);
    }

    @Override
    public void putBytes(final int index, final IMemoryBuffer srcBuffer, final long srcIndex, final int length) {
        srcBuffer.getBytes(srcIndex, delegate, index, length);
    }

    @Override
    public InputStream asInputStream(final int index, final int length) {
        return new DirectBufferInputStream(directBuffer(), index, length);
    }

    @Override
    public OutputStream asOutputStream(final int index, final int length) {
        return new DirectBufferOutputStream(directBuffer(), index, length);
    }

    @Override
    public byte[] asByteArray() {
        final byte[] bytes = byteArray();
        if (bytes != null) {
            return bytes;
        } else {
            return ByteBuffers.asByteArrayCopyGet(delegate, 0, capacity());
        }
    }

    @Override
    public byte[] asByteArrayCopy() {
        final byte[] bytes = byteArray();
        if (bytes != null) {
            return bytes.clone();
        } else {
            return ByteBuffers.asByteArrayCopyGet(delegate, 0, capacity());
        }
    }

    @Override
    public byte[] asByteArray(final int index, final int length) {
        return ByteBuffers.asByteArray(this, index, length);
    }

    @Override
    public byte[] asByteArrayCopy(final int index, final int length) {
        return ByteBuffers.asByteArrayCopy(this, index, length);
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
        if (index == 0) {
            return this;
        } else {
            return new NioDelegateByteBuffer(ByteBuffers.slice(delegate, index, remaining(index)));
        }
    }

    @Override
    public IByteBuffer newSlice(final int index, final int length) {
        if (index == 0 && length == capacity()) {
            return this;
        } else {
            return new NioDelegateByteBuffer(ByteBuffers.slice(delegate, index, length));
        }
    }

    @Override
    public String getStringAsciii(final int index, final int length) {
        final byte[] bytes = ByteBuffers.allocateByteArray(length);
        ByteBuffers.get(delegate, index, bytes, 0, length);
        return ByteBuffers.newStringAscii(bytes);
    }

    @Override
    public void getStringAsciii(final int index, final int length, final Appendable dst) {
        try {
            final int limit = index + length;
            for (int i = index; i < limit; i++) {
                final char c = (char) delegate.get(i);
                dst.append(c > 127 ? '?' : c);
            }
        } catch (final IOException e) {
            throw Throwables.propagate(e);
        }
    }

    @Override
    public void putStringAsciii(final int index, final CharSequence value, final int valueIndex, final int length) {
        for (int i = 0; i < length; i++) {
            char c = value.charAt(valueIndex + i);
            if (c > 127) {
                c = '?';
            }

            delegate.put(index + i, (byte) c);
        }
    }

    @Override
    public int putStringUtf8(final int index, final String value) {
        final byte[] bytes = ByteBuffers.newStringUtf8Bytes(value);
        ByteBuffers.put(delegate, index, bytes);
        return bytes.length;
    }

    @Override
    public String getStringUtf8(final int index, final int length) {
        final byte[] bytes = ByteBuffers.allocateByteArray(length);
        ByteBuffers.get(delegate, index, bytes, 0, length);
        return ByteBuffers.newStringUtf8(bytes);
    }

    @Override
    public void getStringUtf8(final int index, final int length, final Appendable dst) {
        final String string = getStringUtf8(index, length);
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
            final byte b = delegate.get(i);
            dst.write(b);
            i++;
        }
    }

    @Override
    public void getBytesTo(final int index, final OutputStream dst, final int length) throws IOException {
        int i = index;
        while (i < length) {
            final byte b = delegate.get(i);
            dst.write(b);
            i++;
        }
    }

    @Override
    public void putBytesTo(final int index, final DataInput src, final int length) throws IOException {
        int i = index;
        while (i < length) {
            final byte b = src.readByte();
            delegate.put(i, b);
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
            delegate.put(i, (byte) result);
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
    public java.nio.ByteBuffer asNioByteBuffer(final int index, final int length) {
        final java.nio.ByteBuffer buffer = nioByteBuffer();
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

    @Override
    public IByteBuffer ensureCapacity(final int desiredCapacity) {
        ByteBuffers.ensureCapacity(this, desiredCapacity);
        return this;
    }

    @Override
    public void clear(final byte value, final int index, final int length) {
        directBuffer().setMemory(index, length, value);
    }

}