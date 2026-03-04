package de.invesdwin.util.streams.buffer.memory.delegate;

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

import de.invesdwin.util.concurrent.loop.spinwait.ASpinWait;
import de.invesdwin.util.error.FastEOFException;
import de.invesdwin.util.error.FastIndexOutOfBoundsException;
import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.lang.uri.URIs;
import de.invesdwin.util.math.Longs;
import de.invesdwin.util.streams.InputStreams;
import de.invesdwin.util.streams.OutputStreams;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.UninitializedDirectByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.delegate.ArrowDelegateByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.delegate.MemoryDelegateByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.extend.ArrayExpandableByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.extend.UnsafeByteBuffer;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.MemoryBuffers;
import de.invesdwin.util.streams.buffer.memory.delegate.slice.SlicedDelegateMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.delegate.slice.SlicedFromDelegateMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.delegate.slice.mutable.factory.IMutableSlicedDelegateMemoryBufferFactory;
import de.invesdwin.util.streams.buffer.memory.stream.ExpandableMemoryBufferOutputStream;
import de.invesdwin.util.streams.buffer.memory.stream.MemoryBufferInputStream;
import de.invesdwin.util.streams.buffer.memory.stream.MemoryBufferOutputStream;
import de.invesdwin.util.time.duration.Duration;

@NotThreadSafe
public class ArrowDelegateMemoryBuffer implements IMemoryBuffer {

    public static final ArrowBuf EMPTY_BYTES = ArrowDelegateByteBuffer.EMPTY_BYTES;
    public static final ArrowDelegateMemoryBuffer EMPTY_BUFFER = new ArrowDelegateMemoryBuffer(EMPTY_BYTES);

    private ArrowBuf delegate;
    private IMutableSlicedDelegateMemoryBufferFactory mutableSliceFactory;

    public ArrowDelegateMemoryBuffer(final ArrowBuf delegate) {
        setDelegate(delegate);
    }

    public ArrowDelegateMemoryBuffer() {}

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
        final java.nio.ByteBuffer byteBuffer = asNioByteBuffer();
        if (byteBuffer != null) {
            return ByteBuffers.addressOffset(byteBuffer);
        } else {
            return BufferUtil.ARRAY_BASE_OFFSET;
        }
    }

    @Override
    public long capacity() {
        return delegate.capacity();
    }

    @Override
    public long getLong(final long index) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final long bits = delegate.getLong(index);
            return Long.reverseBytes(bits);
        } else {
            return delegate.getLong(index);
        }
    }

    @Override
    public int getInt(final long index) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final int bits = delegate.getInt(index);
            return Integer.reverseBytes(bits);
        } else {
            return delegate.getInt(index);
        }
    }

    @Override
    public double getDouble(final long index) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final long bits = delegate.getLong(index);
            return Double.longBitsToDouble(Long.reverseBytes(bits));
        } else {
            return delegate.getDouble(index);
        }
    }

    @Override
    public float getFloat(final long index) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final int bits = delegate.getInt(index);
            return Float.intBitsToFloat(Integer.reverseBytes(bits));
        } else {
            return delegate.getFloat(index);
        }
    }

    @Override
    public short getShort(final long index) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final short bits = delegate.getShort(index);
            return Short.reverseBytes(bits);
        } else {
            return delegate.getShort(index);
        }
    }

    @Override
    public char getChar(final long index) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final short bits = delegate.getShort(index);
            return (char) Short.reverseBytes(bits);
        } else {
            return delegate.getChar(index);
        }
    }

    @Override
    public long getLongReverse(final long index) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final long bits = delegate.getLong(index);
            return Long.reverseBytes(bits);
        } else {
            return delegate.getLong(index);
        }
    }

    @Override
    public int getIntReverse(final long index) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final int bits = delegate.getInt(index);
            return Integer.reverseBytes(bits);
        } else {
            return delegate.getInt(index);
        }
    }

    @Override
    public double getDoubleReverse(final long index) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final long bits = delegate.getLong(index);
            return Double.longBitsToDouble(Long.reverseBytes(bits));
        } else {
            return delegate.getDouble(index);
        }
    }

    @Override
    public float getFloatReverse(final long index) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final int bits = delegate.getInt(index);
            return Float.intBitsToFloat(Integer.reverseBytes(bits));
        } else {
            return delegate.getFloat(index);
        }
    }

    @Override
    public short getShortReverse(final long index) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final short bits = delegate.getShort(index);
            return Short.reverseBytes(bits);
        } else {
            return delegate.getShort(index);
        }
    }

    @Override
    public char getCharReverse(final long index) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final short bits = delegate.getShort(index);
            return (char) Short.reverseBytes(bits);
        } else {
            return delegate.getChar(index);
        }
    }

    @Override
    public byte getByte(final long index) {
        return delegate.getByte(index);
    }

    @Override
    public void getBytes(final long index, final byte[] dst, final int dstIndex, final int length) {
        delegate.getBytes(index, dst, dstIndex, length);
    }

    @Override
    public void getBytes(final long index, final MutableDirectBuffer dstBuffer, final int dstIndex, final int length) {
        for (int i = 0; i < length; i++) {
            dstBuffer.putByte(dstIndex + i, delegate.getByte(index + i));
        }
    }

    @Override
    public void getBytes(final long index, final IByteBuffer dstBuffer, final int dstIndex, final int length) {
        for (int i = 0; i < length; i++) {
            dstBuffer.putByte(dstIndex + i, delegate.getByte(index + i));
        }
    }

    @Override
    public void getBytes(final long index, final IMemoryBuffer dstBuffer, final long dstIndex, final long length) {
        for (int i = 0; i < length; i++) {
            dstBuffer.putByte(dstIndex + i, delegate.getByte(index + i));
        }
    }

    @Override
    public void getBytes(final long index, final java.nio.ByteBuffer dstBuffer, final int dstIndex, final int length) {
        for (int i = 0; i < length; i++) {
            dstBuffer.put(dstIndex + i, delegate.getByte(index + i));
        }
    }

    @Override
    public long wrapAdjustment() {
        final java.nio.ByteBuffer byteBuffer = asNioByteBuffer();
        if (byteBuffer != null) {
            return ByteBuffers.wrapAdjustment(byteBuffer);
        }
        return 0;
    }

    @Override
    public boolean isExpandable() {
        //only grpc ArrowBufs are actually expandable, though how to determine that?
        return true;
    }

    @Override
    public void putLong(final long index, final long value) {
        ensureCapacity(index, Long.BYTES);
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final long bits = Long.reverseBytes(value);
            delegate.setLong(index, bits);
        } else {
            delegate.setLong(index, value);
        }
    }

    @Override
    public void putInt(final long index, final int value) {
        ensureCapacity(index, Integer.BYTES);
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final int bits = Integer.reverseBytes(value);
            delegate.setInt(index, bits);
        } else {
            delegate.setInt(index, value);
        }
    }

    @Override
    public void putDouble(final long index, final double value) {
        ensureCapacity(index, Double.BYTES);
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final long bits = Long.reverseBytes(Double.doubleToRawLongBits(value));
            delegate.setLong(index, bits);
        } else {
            delegate.setDouble(index, value);
        }
    }

    @Override
    public void putFloat(final long index, final float value) {
        ensureCapacity(index, Float.BYTES);
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final int bits = Integer.reverseBytes(Float.floatToRawIntBits(value));
            delegate.setInt(index, bits);
        } else {
            delegate.setFloat(index, value);
        }
    }

    @Override
    public void putShort(final long index, final short value) {
        ensureCapacity(index, Short.BYTES);
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final short bits = Short.reverseBytes(value);
            delegate.setShort(index, bits);
        } else {
            delegate.setShort(index, value);
        }
    }

    @Override
    public void putChar(final long index, final char value) {
        ensureCapacity(index, Character.BYTES);
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final short bits = Short.reverseBytes((short) value);
            delegate.setShort(index, bits);
        } else {
            delegate.setShort(index, (short) value);
        }
    }

    @Override
    public void putLongReverse(final long index, final long value) {
        ensureCapacity(index, Long.BYTES);
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final long bits = Long.reverseBytes(value);
            delegate.setLong(index, bits);
        } else {
            delegate.setLong(index, value);
        }
    }

    @Override
    public void putIntReverse(final long index, final int value) {
        ensureCapacity(index, Integer.BYTES);
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final int bits = Integer.reverseBytes(value);
            delegate.setInt(index, bits);
        } else {
            delegate.setInt(index, value);
        }
    }

    @Override
    public void putDoubleReverse(final long index, final double value) {
        ensureCapacity(index, Double.BYTES);
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final long bits = Long.reverseBytes(Double.doubleToRawLongBits(value));
            delegate.setLong(index, bits);
        } else {
            delegate.setDouble(index, value);
        }
    }

    @Override
    public void putFloatReverse(final long index, final float value) {
        ensureCapacity(index, Float.BYTES);
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final int bits = Integer.reverseBytes(Float.floatToRawIntBits(value));
            delegate.setInt(index, bits);
        } else {
            delegate.setFloat(index, value);
        }
    }

    @Override
    public void putShortReverse(final long index, final short value) {
        ensureCapacity(index, Short.BYTES);
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final short bits = Short.reverseBytes(value);
            delegate.setShort(index, bits);
        } else {
            delegate.setShort(index, value);
        }
    }

    @Override
    public void putCharReverse(final long index, final char value) {
        ensureCapacity(index, Character.BYTES);
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final short bits = Short.reverseBytes((short) value);
            delegate.setShort(index, bits);
        } else {
            delegate.setShort(index, (short) value);
        }
    }

    @Override
    public void putByte(final long index, final byte value) {
        ensureCapacity(index, Byte.BYTES);
        delegate.setByte(index, value);
    }

    @Override
    public void putBytes(final long index, final byte[] src, final int srcIndex, final int length) {
        ensureCapacity(index, length);
        delegate.setBytes(index, src, srcIndex, length);
    }

    @Override
    public void putBytes(final long index, final java.nio.ByteBuffer srcBuffer, final int srcIndex, final int length) {
        ensureCapacity(index, length);
        delegate.setBytes(index, srcBuffer, srcIndex, length);
    }

    @Override
    public void putBytes(final long index, final DirectBuffer srcBuffer, final int srcIndex, final int length) {
        ensureCapacity(index, length);
        for (int i = 0; i < length; i++) {
            delegate.setByte(index + i, srcBuffer.getByte(srcIndex + i));
        }
    }

    @Override
    public void putBytes(final long index, final IByteBuffer srcBuffer, final int srcIndex, final int length) {
        ensureCapacity(index, length);
        for (int i = 0; i < length; i++) {
            delegate.setByte(index + i, srcBuffer.getByte(srcIndex + i));
        }
    }

    @Override
    public void putBytes(final long index, final IMemoryBuffer srcBuffer, final long srcIndex, final long length) {
        ensureCapacity(index, length);
        for (int i = 0; i < length; i++) {
            delegate.setByte(index + i, srcBuffer.getByte(srcIndex + i));
        }
    }

    @Override
    public InputStream asInputStream(final long index, final long length) {
        return new MemoryBufferInputStream(this, index, length);
    }

    @Override
    public OutputStream asOutputStream(final long index, final long length) {
        if (isExpandable() && index + length >= capacity()) {
            //allow output stream to actually grow the buffer
            return new ExpandableMemoryBufferOutputStream(this, index);
        } else {
            return new MemoryBufferOutputStream(this, index, length);
        }
    }

    @Override
    public byte[] asByteArrayCopy(final long index, final int length) {
        final byte[] bytes = new byte[length];
        delegate.getBytes(index, bytes);
        return bytes;
    }

    @Override
    public MutableDirectBuffer asDirectBuffer(final long index, final int length) {
        return new UnsafeByteBuffer(addressOffset() + wrapAdjustment() + index, length);
    }

    @Override
    public IByteBuffer asByteBuffer(final long index, final int length) {
        if (index + length < ArrayExpandableByteBuffer.MAX_ARRAY_LENGTH) {
            return new ArrowDelegateByteBuffer(delegate).newSlice((int) index, length);
        } else {
            return new MemoryDelegateByteBuffer(newSlice(index, length));
        }
    }

    private IMutableSlicedDelegateMemoryBufferFactory getMutableSliceFactory() {
        if (mutableSliceFactory == null) {
            mutableSliceFactory = IMutableSlicedDelegateMemoryBufferFactory.newInstance(this);
        }
        return mutableSliceFactory;
    }

    @Override
    public IMemoryBuffer sliceFrom(final long index) {
        return getMutableSliceFactory().sliceFrom(index);
    }

    @Override
    public IMemoryBuffer slice(final long index, final long length) {
        return getMutableSliceFactory().slice(index, length);
    }

    @Override
    public IMemoryBuffer newSliceFrom(final long index) {
        if (index == 0) {
            return this;
        } else {
            return new SlicedFromDelegateMemoryBuffer(this, index);
        }
    }

    @Override
    public IMemoryBuffer newSlice(final long index, final long length) {
        if (index == 0 && length == capacity()) {
            return this;
        } else {
            return new SlicedDelegateMemoryBuffer(this, index, length);
        }
    }

    @Override
    public String getStringAsciii(final long index, final int length) {
        final byte[] bytes = ByteBuffers.allocateByteArray(length);
        getBytes(index, bytes, 0, length);
        return ByteBuffers.newStringAscii(bytes);
    }

    @Override
    public void getStringAsciii(final long index, final int length, final Appendable dst) {
        try {
            final long limit = index + length;
            for (long i = index; i < limit; i++) {
                final char c = (char) delegate.getByte(i);
                dst.append(c > 127 ? '?' : c);
            }
        } catch (final IOException e) {
            throw Throwables.propagate(e);
        }
    }

    @Override
    public void putStringAsciii(final long index, final CharSequence value, final int valueIndex, final int length) {
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
    public int putStringUtf8(final long index, final String value) {
        final byte[] bytes = ByteBuffers.newStringUtf8Bytes(value);
        ensureCapacity(index, bytes.length);
        delegate.setBytes(index, bytes);
        return bytes.length;
    }

    @Override
    public String getStringUtf8(final long index, final int length) {
        final byte[] bytes = ByteBuffers.allocateByteArray(length);
        getBytes(index, bytes, 0, length);
        return ByteBuffers.newStringUtf8(bytes);
    }

    @Override
    public void getStringUtf8(final long index, final int length, final Appendable dst) {
        final String string = getStringUtf8(index, length);
        try {
            dst.append(string);
        } catch (final IOException e) {
            throw Throwables.propagate(e);
        }
    }

    @Override
    public void getBytesTo(final long index, final DataOutput dst, final long length) throws IOException {
        if (dst instanceof WritableByteChannel) {
            getBytesTo(index, (WritableByteChannel) dst, length);
        } else {
            final long limit = index + length;
            for (long i = index; i < limit; i++) {
                final byte b = delegate.getByte(i);
                dst.write(b);
            }
        }
    }

    @Override
    public void getBytesTo(final long index, final OutputStream dst, final long length) throws IOException {
        if (dst instanceof WritableByteChannel) {
            getBytesTo(index, (WritableByteChannel) dst, length);
        } else if (dst instanceof FileOutputStream && ((FileOutputStream) dst).getChannel() != null) {
            final FileOutputStream cDst = (FileOutputStream) dst;
            getBytesTo(index, cDst.getChannel(), length);
        } else if (dst instanceof DataOutput) {
            getBytesTo(index, (DataOutput) dst, length);
        } else {
            final long limit = index + length;
            for (long i = index; i < limit; i++) {
                final byte b = delegate.getByte(i);
                dst.write(b);
            }
        }
    }

    @Override
    public void putBytesTo(final long index, final DataInput src, final long length) throws IOException {
        if (src instanceof ReadableByteChannel) {
            putBytesTo(index, (ReadableByteChannel) src, length);
        } else {
            ensureCapacity(index, length);
            final long limit = index + length;
            for (long i = index; i < limit; i++) {
                final byte b = src.readByte();
                delegate.setByte(i, b);
            }
        }
    }

    @Override
    public void putBytesTo(final long index, final InputStream src, final long length) throws IOException {
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
            final long limit = index + length;
            for (long i = index; i < limit;) {
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
    public void getBytesTo(final long index, final WritableByteChannel dst, final long length) throws IOException {
        if (length > ArrayExpandableByteBuffer.MAX_ARRAY_LENGTH) {
            long remaining = length;
            while (remaining > 0L) {
                final long chunk = Longs.min(remaining, ArrayExpandableByteBuffer.MAX_ARRAY_LENGTH);
                remaining -= chunk;
                OutputStreams.writeFullyNoTimeout(dst, asNioByteBuffer(index, (int) chunk));
            }
        } else {
            OutputStreams.writeFullyNoTimeout(dst, asNioByteBuffer(index, (int) length));
        }
    }

    @Override
    public void putBytesTo(final long index, final ReadableByteChannel src, final long length) throws IOException {
        if (length > ArrayExpandableByteBuffer.MAX_ARRAY_LENGTH) {
            long remaining = length;
            while (remaining > 0L) {
                final long chunk = Longs.min(remaining, ArrayExpandableByteBuffer.MAX_ARRAY_LENGTH);
                remaining -= chunk;
                InputStreams.readFullyNoTimeout(src, asNioByteBuffer(index, (int) chunk));
            }
        } else {
            InputStreams.readFullyNoTimeout(src, asNioByteBuffer(index, (int) length));
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

    /**
     * https://github.com/netty/netty/wiki/New-and-noteworthy-in-4.0#predictable-nio-buffer-conversion
     */
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
    public java.nio.ByteBuffer asNioByteBuffer(final long index, final int length) {
        final long address = addressOffset();
        return UninitializedDirectByteBuffers.asDirectByteBufferNoCleaner(address + index, length);
    }

    @Override
    public void clear(final byte value, final long index, final long length) {
        final long limit = index + length;
        for (long i = index; i < limit; i++) {
            delegate.setByte(i, value);
        }
    }

    @Override
    public String toString() {
        return MemoryBuffers.toString(this);
    }

    @Override
    public IMemoryBuffer ensureCapacity(final long desiredCapacity) {
        if (delegate.capacity() < desiredCapacity) {
            delegate.capacity(desiredCapacity);
        }
        return this;
    }

    private void ensureCapacity(final long index, final long length) {
        if (index < 0 || length < 0) {
            throw FastIndexOutOfBoundsException.getInstance("negative value: index=%s length=%s", index, length);
        }

        final long resultingPosition = index + length;
        final long currentArrayLength = delegate.capacity();
        if (resultingPosition > currentArrayLength) {
            if (resultingPosition > ExpandableArrayBuffer.MAX_ARRAY_LENGTH) {
                throw FastIndexOutOfBoundsException.getInstance("index=%s length=%s maxCapacity=%s", index, length,
                        +ExpandableArrayBuffer.MAX_ARRAY_LENGTH);
            }

            ensureCapacity((int) resultingPosition);
        }
    }

    @Override
    public IMemoryBuffer clone(final long index, final int length) {
        return MemoryBuffers.wrap(asByteArrayCopy(index, length));
    }

    @Override
    public IMemoryBuffer asImmutableSlice() {
        return this;
    }

}
