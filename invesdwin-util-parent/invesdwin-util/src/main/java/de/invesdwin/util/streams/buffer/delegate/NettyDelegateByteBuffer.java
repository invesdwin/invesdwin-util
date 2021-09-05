package de.invesdwin.util.streams.buffer.delegate;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteOrder;

import javax.annotation.concurrent.NotThreadSafe;

import org.agrona.BufferUtil;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.agrona.io.DirectBufferInputStream;
import org.agrona.io.DirectBufferOutputStream;
import org.agrona.io.ExpandableDirectBufferOutputStream;

import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.math.Bytes;
import de.invesdwin.util.streams.buffer.ByteBuffers;
import de.invesdwin.util.streams.buffer.IByteBuffer;
import de.invesdwin.util.streams.buffer.delegate.slice.SlicedDelegateByteBuffer;
import de.invesdwin.util.streams.buffer.delegate.slice.SlicedFromDelegateByteBuffer;
import de.invesdwin.util.streams.buffer.delegate.slice.mutable.factory.IMutableSlicedDelegateByteBufferFactory;
import de.invesdwin.util.streams.buffer.extend.UnsafeByteBuffer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

@NotThreadSafe
public class NettyDelegateByteBuffer implements IByteBuffer {

    public static final ByteBuf EMPTY_BYTES = Unpooled.wrappedBuffer(Bytes.EMPTY_ARRAY);
    public static final NettyDelegateByteBuffer EMPTY_BUFFER = new NettyDelegateByteBuffer(EMPTY_BYTES);

    private ByteBuf delegate;
    private IMutableSlicedDelegateByteBufferFactory mutableSliceFactory;

    public NettyDelegateByteBuffer(final ByteBuf delegate) {
        setDelegate(delegate);
    }

    public ByteBuf getDelegate() {
        return delegate;
    }

    @SuppressWarnings("deprecation")
    public void setDelegate(final ByteBuf delegate) {
        if (delegate.order() != ByteBuffers.DEFAULT_ORDER) {
            //unwrap SwappedByteBuf
            this.delegate = delegate.order(ByteBuffers.DEFAULT_ORDER);
        } else {
            this.delegate = delegate;
        }
    }

    @Override
    public ByteOrder getOrder() {
        return ByteBuffers.DEFAULT_ORDER;
    }

    @Override
    public boolean isReadOnly() {
        return delegate.isReadOnly();
    }

    @Override
    public long addressOffset() {
        final java.nio.ByteBuffer byteBuffer = byteBuffer();
        if (byteBuffer != null) {
            return ByteBuffers.addressOffset(byteBuffer);
        } else {
            return BufferUtil.ARRAY_BASE_OFFSET;
        }
    }

    @Override
    public MutableDirectBuffer directBuffer() {
        return null;
    }

    @Override
    public byte[] byteArray() {
        if (delegate.hasArray()) {
            return delegate.array();
        } else {
            return null;
        }
    }

    /**
     * https://github.com/netty/netty/wiki/New-and-noteworthy-in-4.0#predictable-nio-buffer-conversion
     */
    @Override
    public java.nio.ByteBuffer byteBuffer() {
        if (delegate.nioBufferCount() == 1) {
            return delegate.nioBuffer();
        } else {
            return null;
        }
    }

    @Override
    public int capacity() {
        return delegate.capacity();
    }

    @SuppressWarnings("deprecation")
    private ByteOrder getDelegateOrder() {
        return delegate.order();
    }

    @Override
    public long getLong(final int index) {
        return delegate.getLong(index);
    }

    @Override
    public long getLongReverse(final int index) {
        return delegate.getLongLE(index);
    }

    @Override
    public int getInt(final int index) {
        return delegate.getInt(index);
    }

    @Override
    public int getIntReverse(final int index) {
        return delegate.getIntLE(index);
    }

    @Override
    public double getDouble(final int index) {
        return delegate.getDouble(index);
    }

    @Override
    public double getDoubleReverse(final int index) {
        return delegate.getDoubleLE(index);
    }

    @Override
    public float getFloat(final int index) {
        return delegate.getFloat(index);
    }

    @Override
    public float getFloatReverse(final int index) {
        return delegate.getFloatLE(index);
    }

    @Override
    public short getShort(final int index) {
        return delegate.getShort(index);
    }

    @Override
    public short getShortReverse(final int index) {
        return delegate.getShortLE(index);
    }

    @Override
    public char getChar(final int index) {
        return (char) delegate.getShort(index);
    }

    @Override
    public char getCharReverse(final int index) {
        return (char) delegate.getShortLE(index);
    }

    @Override
    public byte getByte(final int index) {
        return delegate.getByte(index);
    }

    @Override
    public void getBytes(final int index, final byte[] dst, final int dstIndex, final int length) {
        for (int i = 0; i < length; i++) {
            dst[dstIndex + i] = delegate.getByte(index + i);
        }
    }

    @Override
    public void getBytes(final int index, final MutableDirectBuffer dstBuffer, final int dstIndex, final int length) {
        for (int i = 0; i < length; i++) {
            dstBuffer.putByte(dstIndex + i, delegate.getByte(index + i));
        }
    }

    @Override
    public void getBytes(final int index, final IByteBuffer dstBuffer, final int dstIndex, final int length) {
        for (int i = 0; i < length; i++) {
            dstBuffer.putByte(dstIndex + i, delegate.getByte(index + i));
        }
    }

    @Override
    public void getBytes(final int index, final java.nio.ByteBuffer dstBuffer, final int dstIndex, final int length) {
        for (int i = 0; i < length; i++) {
            dstBuffer.put(dstIndex + i, delegate.getByte(index + i));
        }
    }

    @Override
    public int wrapAdjustment() {
        final java.nio.ByteBuffer byteBuffer = byteBuffer();
        if (byteBuffer != null) {
            return ByteBuffers.wrapAdjustment(byteBuffer);
        } else {
            final long offset = BufferUtil.ARRAY_BASE_OFFSET;
            return (int) (addressOffset() - offset);
        }
    }

    @Override
    public boolean isExpandable() {
        return delegate.maxCapacity() > delegate.capacity();
    }

    @Override
    public void putLong(final int index, final long value) {
        delegate.setLong(index, value);
    }

    @Override
    public void putLongReverse(final int index, final long value) {
        delegate.setLongLE(index, value);
    }

    @Override
    public void putInt(final int index, final int value) {
        delegate.setInt(index, value);
    }

    @Override
    public void putIntReverse(final int index, final int value) {
        delegate.setIntLE(index, value);
    }

    @Override
    public void putDouble(final int index, final double value) {
        delegate.setDouble(index, value);
    }

    @Override
    public void putDoubleReverse(final int index, final double value) {
        delegate.setDoubleLE(index, value);
    }

    @Override
    public void putFloat(final int index, final float value) {
        delegate.setFloat(index, value);
    }

    @Override
    public void putFloatReverse(final int index, final float value) {
        delegate.setFloatLE(index, value);
    }

    @Override
    public void putShort(final int index, final short value) {
        delegate.setShort(index, value);
    }

    @Override
    public void putShortReverse(final int index, final short value) {
        delegate.setShortLE(index, value);
    }

    @Override
    public void putChar(final int index, final char value) {
        delegate.setShort(index, (short) value);
    }

    @Override
    public void putCharReverse(final int index, final char value) {
        delegate.setShortLE(index, (short) value);
    }

    @Override
    public void putByte(final int index, final byte value) {
        delegate.setByte(index, value);
    }

    @Override
    public void putBytes(final int index, final byte[] src, final int srcIndex, final int length) {
        delegate.setBytes(index, src, srcIndex, length);
    }

    @Override
    public void putBytes(final int index, final java.nio.ByteBuffer srcBuffer, final int srcIndex, final int length) {
        delegate.setBytes(index, Unpooled.wrappedBuffer(srcBuffer), srcIndex, length);
    }

    @Override
    public void putBytes(final int index, final DirectBuffer srcBuffer, final int srcIndex, final int length) {
        for (int i = 0; i < length; i++) {
            delegate.setByte(index + i, srcBuffer.getByte(srcIndex + i));
        }
    }

    @Override
    public void putBytes(final int index, final IByteBuffer srcBuffer, final int srcIndex, final int length) {
        for (int i = 0; i < length; i++) {
            delegate.setByte(index + i, srcBuffer.getByte(srcIndex + i));
        }
    }

    @Override
    public InputStream asInputStream(final int index, final int length) {
        return new DirectBufferInputStream(asDirectBuffer(), index, length);
    }

    @Override
    public OutputStream asOutputStream(final int index, final int length) {
        if (isExpandable() && index + length >= capacity()) {
            //allow output stream to actually grow the buffer
            return new ExpandableDirectBufferOutputStream(asDirectBuffer(), index);
        } else {
            return new DirectBufferOutputStream(asDirectBuffer(), index, length);
        }
    }

    @Override
    public byte[] asByteArray(final int index, final int length) {
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
    public byte[] asByteArrayCopy(final int index, final int length) {
        final byte[] bytes = new byte[length];
        delegate.getBytes(index, bytes);
        return bytes;
    }

    @Override
    public MutableDirectBuffer asDirectBuffer() {
        final java.nio.ByteBuffer bytes = asByteBuffer();
        return new UnsafeByteBuffer(bytes);
    }

    @Override
    public MutableDirectBuffer asDirectBuffer(final int index, final int length) {
        final java.nio.ByteBuffer bytes = asByteBuffer();
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
        final byte[] bytes = ByteBuffers.allocateByteArray(length);
        getBytes(index, bytes, 0, length);
        return ByteBuffers.newStringAscii(bytes);
    }

    @Override
    public void getStringAsciii(final int index, final int length, final Appendable dst) {
        try {
            final int limit = index + length;
            for (int i = index; i < limit; i++) {
                final char c = (char) delegate.getByte(i);
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

            delegate.setByte(index + i, (byte) c);
        }
    }

    @Override
    public int putStringUtf8(final int index, final String value) {
        final byte[] bytes = ByteBuffers.newStringUtf8Bytes(value);
        delegate.setBytes(index, bytes);
        return bytes.length;
    }

    @Override
    public String getStringUtf8(final int index, final int length) {
        final byte[] bytes = ByteBuffers.allocateByteArray(length);
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
            delegate.setByte(i, b);
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
            delegate.setByte(i, (byte) result);
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
        throw UnknownArgumentException.newInstance(ByteBuf.class, delegate);
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
        final int target = index + length;
        for (int i = index; i < target; i++) {
            delegate.setByte(i, value);
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
    public void ensureCapacity(final int desiredCapacity) {
        delegate.capacity(desiredCapacity);
    }

}
