package de.invesdwin.util.streams.buffer.bytes.delegate;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import javax.annotation.concurrent.NotThreadSafe;

import org.agrona.BufferUtil;
import org.agrona.DirectBuffer;
import org.agrona.ExpandableArrayBuffer;
import org.agrona.MutableDirectBuffer;
import org.apache.arrow.memory.ArrowBuf;
import org.apache.arrow.memory.NettyAllocationManager;

import de.invesdwin.util.concurrent.loop.ASpinWait;
import de.invesdwin.util.error.FastEOFException;
import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.lang.uri.URIs;
import de.invesdwin.util.streams.InputStreams;
import de.invesdwin.util.streams.OutputStreams;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.delegate.slice.SlicedDelegateByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.delegate.slice.SlicedFromDelegateByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.delegate.slice.mutable.factory.IMutableSlicedDelegateByteBufferFactory;
import de.invesdwin.util.streams.buffer.bytes.extend.ArrayExpandableByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.extend.UnsafeByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.stream.ByteBufferInputStream;
import de.invesdwin.util.streams.buffer.bytes.stream.ByteBufferOutputStream;
import de.invesdwin.util.streams.buffer.bytes.stream.ExpandableByteBufferOutputStream;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.delegate.ArrowDelegateMemoryBuffer;
import de.invesdwin.util.time.duration.Duration;

@NotThreadSafe
public class ArrowDelegateByteBuffer implements IByteBuffer {

    public static final ArrowBuf EMPTY_BYTES = NettyAllocationManager.FACTORY.empty();
    public static final ArrowDelegateMemoryBuffer EMPTY_BUFFER = new ArrowDelegateMemoryBuffer(EMPTY_BYTES);

    private ArrowBuf delegate;
    private IMutableSlicedDelegateByteBufferFactory mutableSliceFactory;

    public ArrowDelegateByteBuffer(final ArrowBuf delegate) {
        setDelegate(delegate);
    }

    @Override
    public int getId() {
        return System.identityHashCode(delegate);
    }

    public ArrowBuf getDelegate() {
        return delegate;
    }

    public void setDelegate(final ArrowBuf delegate) {
        if (delegate == null) {
            this.delegate = null;
        } else {
            if (delegate.order() != ByteBuffers.NATIVE_ORDER) {
                throw new IllegalArgumentException(
                        "Expecting " + ArrowBuf.class.getSimpleName() + " to always be in native order!");
            }
            this.delegate = delegate;
        }
    }

    @Override
    public ByteOrder getOrder() {
        return ByteBuffers.DEFAULT_ORDER;
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public long addressOffset() {
        final java.nio.ByteBuffer byteBuffer = nioByteBuffer();
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
        return null;
    }

    @Override
    public java.nio.ByteBuffer nioByteBuffer() {
        return null;
    }

    @Override
    public int capacity() {
        final long capacity = delegate.capacity();
        if (capacity > ArrayExpandableByteBuffer.MAX_ARRAY_LENGTH) {
            return ArrayExpandableByteBuffer.MAX_ARRAY_LENGTH;
        } else {
            return (int) capacity;
        }
    }

    @Override
    public long getLong(final int index) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final long bits = delegate.getLong(index);
            return Long.reverseBytes(bits);
        } else {
            return delegate.getLong(index);
        }
    }

    @Override
    public int getInt(final int index) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final int bits = delegate.getInt(index);
            return Integer.reverseBytes(bits);
        } else {
            return delegate.getInt(index);
        }
    }

    @Override
    public double getDouble(final int index) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final long bits = delegate.getLong(index);
            return Double.longBitsToDouble(Long.reverseBytes(bits));
        } else {
            return delegate.getDouble(index);
        }
    }

    @Override
    public float getFloat(final int index) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final int bits = delegate.getInt(index);
            return Float.intBitsToFloat(Integer.reverseBytes(bits));
        } else {
            return delegate.getFloat(index);
        }
    }

    @Override
    public short getShort(final int index) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final short bits = delegate.getShort(index);
            return Short.reverseBytes(bits);
        } else {
            return delegate.getShort(index);
        }
    }

    @Override
    public char getChar(final int index) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final short bits = delegate.getShort(index);
            return (char) Short.reverseBytes(bits);
        } else {
            return (char) delegate.getShort(index);
        }
    }

    @Override
    public long getLongReverse(final int index) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final long bits = delegate.getLong(index);
            return Long.reverseBytes(bits);
        } else {
            return delegate.getLong(index);
        }
    }

    @Override
    public int getIntReverse(final int index) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final int bits = delegate.getInt(index);
            return Integer.reverseBytes(bits);
        } else {
            return delegate.getInt(index);
        }
    }

    @Override
    public double getDoubleReverse(final int index) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final long bits = delegate.getLong(index);
            return Double.longBitsToDouble(Long.reverseBytes(bits));
        } else {
            return delegate.getDouble(index);
        }
    }

    @Override
    public float getFloatReverse(final int index) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final int bits = delegate.getInt(index);
            return Float.intBitsToFloat(Integer.reverseBytes(bits));
        } else {
            return delegate.getFloat(index);
        }
    }

    @Override
    public short getShortReverse(final int index) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final short bits = delegate.getShort(index);
            return Short.reverseBytes(bits);
        } else {
            return delegate.getShort(index);
        }
    }

    @Override
    public char getCharReverse(final int index) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final short bits = delegate.getShort(index);
            return (char) Short.reverseBytes(bits);
        } else {
            return (char) delegate.getShort(index);
        }
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
    public void getBytes(final int index, final IMemoryBuffer dstBuffer, final long dstIndex, final int length) {
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
        final java.nio.ByteBuffer byteBuffer = nioByteBuffer();
        if (byteBuffer != null) {
            return ByteBuffers.wrapAdjustment(byteBuffer);
        } else {
            final long offset = BufferUtil.ARRAY_BASE_OFFSET;
            return (int) (addressOffset() - offset);
        }
    }

    @Override
    public boolean isExpandable() {
        //only grpc ArrowBufs are actually expandable, though how to determine that?
        return true;
    }

    @Override
    public void putLong(final int index, final long value) {
        ensureCapacity(index, Long.BYTES);
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final long bits = Long.reverseBytes(value);
            delegate.setLong(index, bits);
        } else {
            delegate.setLong(index, value);
        }
    }

    @Override
    public void putInt(final int index, final int value) {
        ensureCapacity(index, Integer.BYTES);
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final int bits = Integer.reverseBytes(value);
            delegate.setInt(index, bits);
        } else {
            delegate.setInt(index, value);
        }
    }

    @Override
    public void putDouble(final int index, final double value) {
        ensureCapacity(index, Double.BYTES);
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final long bits = Long.reverseBytes(Double.doubleToRawLongBits(value));
            delegate.setLong(index, bits);
        } else {
            delegate.setDouble(index, value);
        }
    }

    @Override
    public void putFloat(final int index, final float value) {
        ensureCapacity(index, Float.BYTES);
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final int bits = Integer.reverseBytes(Float.floatToRawIntBits(value));
            delegate.setInt(index, bits);
        } else {
            delegate.setFloat(index, value);
        }
    }

    @Override
    public void putShort(final int index, final short value) {
        ensureCapacity(index, Short.BYTES);
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final short bits = Short.reverseBytes(value);
            delegate.setShort(index, bits);
        } else {
            delegate.setShort(index, value);
        }
    }

    @Override
    public void putChar(final int index, final char value) {
        ensureCapacity(index, Character.BYTES);
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final short bits = Short.reverseBytes((short) value);
            delegate.setShort(index, bits);
        } else {
            delegate.setShort(index, (short) value);
        }
    }

    @Override
    public void putLongReverse(final int index, final long value) {
        ensureCapacity(index, Long.BYTES);
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final long bits = Long.reverseBytes(value);
            delegate.setLong(index, bits);
        } else {
            delegate.setLong(index, value);
        }
    }

    @Override
    public void putIntReverse(final int index, final int value) {
        ensureCapacity(index, Integer.BYTES);
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final int bits = Integer.reverseBytes(value);
            delegate.setInt(index, bits);
        } else {
            delegate.setInt(index, value);
        }
    }

    @Override
    public void putDoubleReverse(final int index, final double value) {
        ensureCapacity(index, Double.BYTES);
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final long bits = Long.reverseBytes(Double.doubleToRawLongBits(value));
            delegate.setLong(index, bits);
        } else {
            delegate.setDouble(index, value);
        }
    }

    @Override
    public void putFloatReverse(final int index, final float value) {
        ensureCapacity(index, Float.BYTES);
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final int bits = Integer.reverseBytes(Float.floatToRawIntBits(value));
            delegate.setInt(index, bits);
        } else {
            delegate.setFloat(index, value);
        }
    }

    @Override
    public void putShortReverse(final int index, final short value) {
        ensureCapacity(index, Short.BYTES);
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final short bits = Short.reverseBytes(value);
            delegate.setShort(index, bits);
        } else {
            delegate.setShort(index, value);
        }
    }

    @Override
    public void putCharReverse(final int index, final char value) {
        ensureCapacity(index, Character.BYTES);
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final short bits = Short.reverseBytes((short) value);
            delegate.setShort(index, bits);
        } else {
            delegate.setShort(index, (short) value);
        }
    }

    @Override
    public void putByte(final int index, final byte value) {
        ensureCapacity(index, Byte.BYTES);
        delegate.setByte(index, value);
    }

    @Override
    public void putBytes(final int index, final byte[] src, final int srcIndex, final int length) {
        ensureCapacity(index, length);
        delegate.setBytes(index, src, srcIndex, length);
    }

    @Override
    public void putBytes(final int index, final java.nio.ByteBuffer srcBuffer, final int srcIndex, final int length) {
        ensureCapacity(index, length);
        delegate.setBytes(index, srcBuffer, srcIndex, length);
    }

    @Override
    public void putBytes(final int index, final DirectBuffer srcBuffer, final int srcIndex, final int length) {
        ensureCapacity(index, length);
        for (int i = 0; i < length; i++) {
            delegate.setByte(index + i, srcBuffer.getByte(srcIndex + i));
        }
    }

    @Override
    public void putBytes(final int index, final IByteBuffer srcBuffer, final int srcIndex, final int length) {
        ensureCapacity(index, length);
        for (int i = 0; i < length; i++) {
            delegate.setByte(index + i, srcBuffer.getByte(srcIndex + i));
        }
    }

    @Override
    public void putBytes(final int index, final IMemoryBuffer srcBuffer, final long srcIndex, final int length) {
        ensureCapacity(index, length);
        for (int i = 0; i < length; i++) {
            delegate.setByte(index + i, srcBuffer.getByte(srcIndex + i));
        }
    }

    @Override
    public InputStream asInputStream(final int index, final int length) {
        return new ByteBufferInputStream(this, index, length);
    }

    @Override
    public OutputStream asOutputStream(final int index, final int length) {
        if (isExpandable() && index + length >= capacity()) {
            //allow output stream to actually grow the buffer
            return new ExpandableByteBufferOutputStream(this, index);
        } else {
            return new ByteBufferOutputStream(this, index, length);
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

    @Override
    public MutableDirectBuffer asDirectBuffer() {
        final java.nio.ByteBuffer bytes = asNioByteBuffer();
        return new UnsafeByteBuffer(bytes);
    }

    @Override
    public MutableDirectBuffer asDirectBuffer(final int index, final int length) {
        final java.nio.ByteBuffer bytes = asNioByteBuffer();
        return new UnsafeByteBuffer(bytes, index, length);
    }

    @Override
    public IMemoryBuffer asMemoryBuffer() {
        return new ArrowDelegateMemoryBuffer(delegate);
    }

    @Override
    public IMemoryBuffer asMemoryBufferFrom(final int index) {
        return new ArrowDelegateMemoryBuffer(delegate).newSliceFrom(index);
    }

    @Override
    public IMemoryBuffer asMemoryBuffer(final int index, final int length) {
        return new ArrowDelegateMemoryBuffer(delegate).newSlice(index, length);
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
        if (index == 0) {
            return this;
        } else {
            return new SlicedFromDelegateByteBuffer(this, index);
        }
    }

    @Override
    public IByteBuffer newSlice(final int index, final int length) {
        if (index == 0 && length == capacity()) {
            return this;
        } else {
            return new SlicedDelegateByteBuffer(this, index, length);
        }
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
        ensureCapacity(index, length);
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
        ensureCapacity(index, bytes.length);
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
        if (dst instanceof WritableByteChannel) {
            getBytesTo(index, (WritableByteChannel) dst, length);
        } else {
            int i = index;
            final int targetIndex = index + length;
            while (i < targetIndex) {
                final byte b = delegate.getByte(i);
                dst.write(b);
                i++;
            }
        }
    }

    @Override
    public void getBytesTo(final int index, final OutputStream dst, final int length) throws IOException {
        if (dst instanceof WritableByteChannel) {
            getBytesTo(index, (WritableByteChannel) dst, length);
        } else if (dst instanceof FileOutputStream && ((FileOutputStream) dst).getChannel() != null) {
            final FileOutputStream cDst = (FileOutputStream) dst;
            getBytesTo(index, cDst.getChannel(), length);
        } else if (dst instanceof DataOutput) {
            getBytesTo(index, (DataOutput) dst, length);
        } else {
            int i = index;
            final int targetIndex = index + length;
            while (i < targetIndex) {
                final byte b = delegate.getByte(i);
                dst.write(b);
                i++;
            }
        }
    }

    @Override
    public void putBytesTo(final int index, final DataInput src, final int length) throws IOException {
        if (src instanceof ReadableByteChannel) {
            putBytesTo(index, (ReadableByteChannel) src, length);
        } else {
            ensureCapacity(index, length);
            int i = index;
            final int targetIndex = index + length;
            while (i < targetIndex) {
                final byte b = src.readByte();
                delegate.setByte(i, b);
                i++;
            }
        }
    }

    @Override
    public void putBytesTo(final int index, final InputStream src, final int length) throws IOException {
        if (src instanceof ReadableByteChannel) {
            putBytesTo(index, (ReadableByteChannel) src, length);
        } else if (src instanceof FileInputStream && ((FileInputStream) src).getChannel() != null) {
            final FileInputStream cSrc = (FileInputStream) src;
            putBytesTo(index, cSrc.getChannel(), length);
        } else if (src instanceof DataInput) {
            putBytesTo(index, (DataInput) src, length);
        } else {
            final Duration timeout = URIs.getDefaultNetworkTimeout();
            long zeroCountNanos = -1L;

            ensureCapacity(index, length);
            int i = index;
            final int targetIndex = index + length;
            while (i < targetIndex) {
                final int result = src.read();
                if (result < 0) { // EOF
                    throw ByteBuffers.newEOF();
                }
                if (result == 0 && timeout != null) {
                    if (zeroCountNanos == -1) {
                        zeroCountNanos = System.nanoTime();
                    } else if (timeout.isLessThanNanos(System.nanoTime() - zeroCountNanos)) {
                        throw FastEOFException.getInstance("write timeout exceeded");
                    }
                    ASpinWait.onSpinWaitStatic();
                } else {
                    zeroCountNanos = -1L;
                    delegate.setByte(i, (byte) result);
                    i++;
                }
            }
        }
    }

    @Override
    public void getBytesTo(final int index, final WritableByteChannel dst, final int length) throws IOException {
        OutputStreams.writeFully(dst, asNioByteBuffer(index, length));
    }

    @Override
    public void putBytesTo(final int index, final ReadableByteChannel src, final int length) throws IOException {
        InputStreams.readFully(src, asNioByteBuffer(index, length));
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

    /**
     * https://github.com/netty/netty/wiki/New-and-noteworthy-in-4.0#predictable-nio-buffer-conversion
     */
    @Override
    public java.nio.ByteBuffer asNioByteBuffer() {
        final long writerIndexBefore = delegate.writerIndex();
        delegate.writerIndex(capacity());
        try {
            return delegate.nioBuffer();
        } finally {
            delegate.writerIndex(writerIndexBefore);
        }
    }

    @Override
    public java.nio.ByteBuffer asNioByteBuffer(final int index, final int length) {
        final java.nio.ByteBuffer buffer = asNioByteBuffer();
        if (index == 0 && length == capacity()) {
            return buffer;
        } else {
            return ByteBuffers.slice(buffer, index, length);
        }
    }

    @Override
    public void clear(final byte value, final int index, final int length) {
        final int limit = index + length;
        for (int i = index; i < limit; i++) {
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
    public IByteBuffer ensureCapacity(final int desiredCapacity) {
        if (capacity() < desiredCapacity) {
            delegate.capacity(desiredCapacity);
        }
        return this;
    }

    private void ensureCapacity(final int index, final int length) {
        if (index < 0 || length < 0) {
            throw new IndexOutOfBoundsException("negative value: index=" + index + " length=" + length);
        }

        final long resultingPosition = index + (long) length;
        final int currentArrayLength = capacity();
        if (resultingPosition > currentArrayLength) {
            if (resultingPosition > ExpandableArrayBuffer.MAX_ARRAY_LENGTH) {
                throw new IndexOutOfBoundsException("index=" + index + " length=" + length + " maxCapacity="
                        + ExpandableArrayBuffer.MAX_ARRAY_LENGTH);
            }

            ensureCapacity((int) resultingPosition);
        }
    }

    @Override
    public IByteBuffer asImmutableSlice() {
        return this;
    }

}
