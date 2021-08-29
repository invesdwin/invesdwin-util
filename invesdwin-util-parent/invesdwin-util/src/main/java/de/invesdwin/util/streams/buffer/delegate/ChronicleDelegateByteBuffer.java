package de.invesdwin.util.streams.buffer.delegate;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.annotation.concurrent.NotThreadSafe;

import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;

import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.streams.buffer.ByteBuffers;
import de.invesdwin.util.streams.buffer.IByteBuffer;
import de.invesdwin.util.streams.buffer.delegate.slice.SlicedDelegateByteBuffer;
import de.invesdwin.util.streams.buffer.delegate.slice.SlicedFromDelegateByteBuffer;
import de.invesdwin.util.streams.buffer.delegate.slice.mutable.factory.IMutableSlicedDelegateByteBufferFactory;
import de.invesdwin.util.streams.buffer.extend.UnsafeByteBuffer;

@NotThreadSafe
public class ChronicleDelegateByteBuffer implements IByteBuffer {

    private final net.openhft.chronicle.bytes.Bytes<?> delegate;
    private IMutableSlicedDelegateByteBufferFactory mutableSliceFactory;

    public ChronicleDelegateByteBuffer(final net.openhft.chronicle.bytes.Bytes<?> delegate) {
        this.delegate = delegate;
    }

    public net.openhft.chronicle.bytes.Bytes<?> getDelegate() {
        return delegate;
    }

    @Override
    public ByteOrder getOrder() {
        return delegate.byteOrder();
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public long addressOffset() {
        final ByteBuffer byteBuffer = byteBuffer();
        return ByteBuffers.addressOffset(byteBuffer);
    }

    @Override
    public MutableDirectBuffer directBuffer() {
        return null;
    }

    @Override
    public byte[] byteArray() {
        final Object underlying = delegate.underlyingObject();
        if (underlying instanceof byte[]) {
            return (byte[]) underlying;
        }
        return null;
    }

    @Override
    public ByteBuffer byteBuffer() {
        final Object underlying = delegate.underlyingObject();
        if (underlying instanceof ByteBuffer) {
            return (ByteBuffer) underlying;
        }
        return null;
    }

    @Override
    public int capacity() {
        return Integers.checkedCast(delegate.capacity());
    }

    @Override
    public long getLong(final int index) {
        return delegate.readLong(index);
    }

    @Override
    public int getInt(final int index) {
        return delegate.readInt(index);
    }

    @Override
    public double getDouble(final int index) {
        return delegate.readDouble(index);
    }

    @Override
    public float getFloat(final int index) {
        return delegate.readFloat(index);
    }

    @Override
    public short getShort(final int index) {
        return delegate.readShort(index);
    }

    @Override
    public char getChar(final int index) {
        return (char) delegate.readShort(index);
    }

    @Override
    public byte getByte(final int index) {
        return delegate.readByte(index);
    }

    @Override
    public void getBytes(final int index, final byte[] dst, final int dstIndex, final int length) {
        for (int i = 0; i < length; i++) {
            dst[dstIndex + i] = delegate.readByte(index + i);
        }
    }

    @Override
    public void getBytes(final int index, final MutableDirectBuffer dstBuffer, final int dstIndex, final int length) {
        for (int i = 0; i < length; i++) {
            dstBuffer.putByte(dstIndex + i, delegate.readByte(index + i));
        }
    }

    @Override
    public void getBytes(final int index, final IByteBuffer dstBuffer, final int dstIndex, final int length) {
        for (int i = 0; i < length; i++) {
            dstBuffer.putByte(dstIndex + i, delegate.readByte(index + i));
        }
    }

    @Override
    public void getBytes(final int index, final ByteBuffer dstBuffer, final int dstIndex, final int length) {
        for (int i = 0; i < length; i++) {
            dstBuffer.put(dstIndex + i, delegate.readByte(index + i));
        }
    }

    @Override
    public int wrapAdjustment() {
        final ByteBuffer byteBuffer = byteBuffer();
        if (byteBuffer != null) {
            return ByteBuffers.wrapAdjustment(byteBuffer);
        } else {
            return 0;
        }
    }

    @Override
    public boolean isExpandable() {
        return delegate.isElastic();
    }

    @Override
    public void putLong(final int index, final long value) {
        delegate.writeLong(index, value);
    }

    @Override
    public void putInt(final int index, final int value) {
        delegate.writeInt(index, value);
    }

    @Override
    public void putDouble(final int index, final double value) {
        delegate.writeDouble(index, value);
    }

    @Override
    public void putFloat(final int index, final float value) {
        delegate.writeFloat(index, value);
    }

    @Override
    public void putShort(final int index, final short value) {
        delegate.writeShort(index, value);
    }

    @Override
    public void putChar(final int index, final char value) {
        delegate.writeShort(index, (short) value);
    }

    @Override
    public void putByte(final int index, final byte value) {
        delegate.writeByte(index, value);
    }

    @Override
    public void putBytes(final int index, final byte[] src, final int srcIndex, final int length) {
        delegate.write(index, src, srcIndex, length);
    }

    @Override
    public void putBytes(final int index, final ByteBuffer srcBuffer, final int srcIndex, final int length) {
        delegate.write(index, srcBuffer, srcIndex, length);
    }

    @Override
    public void putBytes(final int index, final DirectBuffer srcBuffer, final int srcIndex, final int length) {
        for (int i = 0; i < length; i++) {
            delegate.writeByte(index + i, srcBuffer.getByte(srcIndex + i));
        }
    }

    @Override
    public void putBytes(final int index, final IByteBuffer srcBuffer, final int srcIndex, final int length) {
        for (int i = 0; i < length; i++) {
            delegate.writeByte(index + i, srcBuffer.getByte(srcIndex + i));
        }
    }

    @Override
    public InputStream asInputStream(final int index, final int length) {
        delegate.readPosition(index);
        delegate.readLimit(index + length);
        return delegate.inputStream();
    }

    @Override
    public OutputStream asOutputStream(final int index, final int length) {
        delegate.writePosition(index);
        delegate.writeLimit(index + length);
        return delegate.outputStream();
    }

    @Override
    public byte[] asByteArray(final int index, final int length) {
        if (wrapAdjustment() == 0) {
            final byte[] bytes = byteArray();
            if (bytes != null) {
                return bytes;
            }
            final ByteBuffer byteBuffer = byteBuffer();
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
    public byte[] asByteArrayCopy(final int index, final int length) {
        delegate.readPosition(index);
        delegate.readLimit(index + length);
        return delegate.toByteArray();
    }

    @Override
    public MutableDirectBuffer asDirectBuffer(final int index, final int length) {
        final ByteBuffer bytes = asByteBuffer();
        return new UnsafeByteBuffer(bytes, index, length);
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
        return new SlicedFromDelegateByteBuffer(this, index);
    }

    @Override
    public IByteBuffer newSlice(final int index, final int length) {
        return new SlicedDelegateByteBuffer(this, index, length);
    }

    @Override
    public String getStringAsciii(final int index, final int length) {
        final byte[] bytes = new byte[length];
        getBytes(index, bytes, 0, length);
        return ByteBuffers.newStringAscii(bytes);
    }

    @Override
    public void getStringAsciii(final int index, final int length, final Appendable dst) {
        try {
            final int limit = index + length;
            for (int i = index; i < limit; i++) {
                final char c = (char) delegate.readByte(i);
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

            delegate.writeByte(index + i, (byte) c);
        }
    }

    @Override
    public int putStringUtf8(final int index, final String value) {
        final byte[] bytes = ByteBuffers.newStringUtf8Bytes(value);
        delegate.write(index, bytes);
        return bytes.length;
    }

    @Override
    public String getStringUtf8(final int index, final int length) {
        final byte[] bytes = new byte[length];
        getBytes(index, bytes, 0, length);
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
            final byte b = delegate.readByte(i);
            dst.write(b);
            i++;
        }
    }

    @Override
    public void getBytesTo(final int index, final OutputStream dst, final int length) throws IOException {
        int i = index;
        while (i < length) {
            final byte b = delegate.readByte(i);
            dst.write(b);
            i++;
        }
    }

    @Override
    public void putBytesTo(final int index, final DataInput src, final int length) throws IOException {
        int i = index;
        while (i < length) {
            final byte b = src.readByte();
            delegate.writeByte(i, b);
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
            delegate.writeByte(i, (byte) result);
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
    public ByteBuffer asByteBuffer() {
        final ByteBuffer byteBuffer = byteBuffer();
        if (byteBuffer != null) {
            return byteBuffer;
        }
        final byte[] array = byteArray();
        if (array != null) {
            final ByteBuffer arrayBuffer = ByteBuffer.wrap(array, wrapAdjustment(), capacity());
            return arrayBuffer;
        }
        throw UnknownArgumentException.newInstance(Object.class, delegate.underlyingObject());
    }

    @Override
    public ByteBuffer asByteBuffer(final int index, final int length) {
        final ByteBuffer buffer = asByteBuffer();
        if (index == 0 && length == capacity()) {
            return buffer;
        } else {
            return ByteBuffers.slice(buffer, index, length);
        }
    }

}
