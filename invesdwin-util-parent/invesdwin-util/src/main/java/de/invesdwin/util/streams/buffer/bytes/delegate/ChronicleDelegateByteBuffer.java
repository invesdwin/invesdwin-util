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

import de.invesdwin.util.concurrent.loop.ASpinWait;
import de.invesdwin.util.error.FastEOFException;
import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.lang.uri.URIs;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.EmptyByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.UninitializedDirectByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.delegate.slice.SlicedDelegateByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.delegate.slice.SlicedFromDelegateByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.delegate.slice.mutable.factory.IMutableSlicedDelegateByteBufferFactory;
import de.invesdwin.util.streams.buffer.bytes.extend.ArrayExpandableByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.extend.UnsafeByteBuffer;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.delegate.ChronicleDelegateMemoryBuffer;
import de.invesdwin.util.time.duration.Duration;
import net.openhft.chronicle.bytes.BytesStore;

@NotThreadSafe
public class ChronicleDelegateByteBuffer implements IByteBuffer {

    public static final net.openhft.chronicle.bytes.Bytes<?> EMPTY_BYTES = BytesStore
            .wrap(EmptyByteBuffer.EMPTY_DIRECT_BYTE_BUFFER)
            .bytesForWrite();
    public static final ChronicleDelegateByteBuffer EMPTY_BUFFER = new ChronicleDelegateByteBuffer(EMPTY_BYTES);

    private net.openhft.chronicle.bytes.Bytes<?> delegate;
    private IMutableSlicedDelegateByteBufferFactory mutableSliceFactory;

    public ChronicleDelegateByteBuffer(final net.openhft.chronicle.bytes.Bytes<?> delegate) {
        setDelegate(delegate);
    }

    public net.openhft.chronicle.bytes.Bytes<?> getDelegate() {
        return delegate;
    }

    public void setDelegate(final net.openhft.chronicle.bytes.Bytes<?> delegate) {
        if (delegate.byteOrder() != ByteBuffers.NATIVE_ORDER) {
            throw new IllegalArgumentException("Expecting chronicle-bytes to always be in native order!");
        }
        this.delegate = delegate;
    }

    @Override
    public ByteOrder getOrder() {
        return ByteBuffers.DEFAULT_ORDER;
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public long addressOffset() {
        final java.nio.ByteBuffer byteBuffer = nioByteBuffer();
        if (byteBuffer != null) {
            return ByteBuffers.addressOffset(byteBuffer);
        }
        final byte[] array = byteArray();
        if (array != null) {
            return BufferUtil.ARRAY_BASE_OFFSET;
        }
        final BytesStore store = delegate.bytesStore();
        final long address = store.addressForRead(store.start());
        return address;
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
        final java.nio.ByteBuffer buffer = nioByteBuffer();
        if (buffer != null && buffer.hasArray()) {
            return buffer.array();
        }
        return null;
    }

    @Override
    public java.nio.ByteBuffer nioByteBuffer() {
        final Object underlying = delegate.underlyingObject();
        if (underlying instanceof java.nio.ByteBuffer) {
            return (java.nio.ByteBuffer) underlying;
        }
        return null;
    }

    @Override
    public int capacity() {
        final long capacity = delegate.safeLimit();
        if (capacity > ArrayExpandableByteBuffer.MAX_ARRAY_LENGTH) {
            return ArrayExpandableByteBuffer.MAX_ARRAY_LENGTH;
        } else {
            return (int) capacity;
        }
    }

    @Override
    public long getLong(final int index) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final long bits = delegate.readLong(index);
            return Long.reverseBytes(bits);
        } else {
            return delegate.readLong(index);
        }
    }

    @Override
    public int getInt(final int index) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final int bits = delegate.readInt(index);
            return Integer.reverseBytes(bits);
        } else {
            return delegate.readInt(index);
        }
    }

    @Override
    public double getDouble(final int index) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final long bits = delegate.readLong(index);
            return Double.longBitsToDouble(Long.reverseBytes(bits));
        } else {
            return delegate.readDouble(index);
        }
    }

    @Override
    public float getFloat(final int index) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final int bits = delegate.readInt(index);
            return Float.intBitsToFloat(Integer.reverseBytes(bits));
        } else {
            return delegate.readFloat(index);
        }
    }

    @Override
    public short getShort(final int index) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final short bits = delegate.readShort(index);
            return Short.reverseBytes(bits);
        } else {
            return delegate.readShort(index);
        }
    }

    @Override
    public char getChar(final int index) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final short bits = delegate.readShort(index);
            return (char) Short.reverseBytes(bits);
        } else {
            return (char) delegate.readShort(index);
        }
    }

    @Override
    public long getLongReverse(final int index) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final long bits = delegate.readLong(index);
            return Long.reverseBytes(bits);
        } else {
            return delegate.readLong(index);
        }
    }

    @Override
    public int getIntReverse(final int index) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final int bits = delegate.readInt(index);
            return Integer.reverseBytes(bits);
        } else {
            return delegate.readInt(index);
        }
    }

    @Override
    public double getDoubleReverse(final int index) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final long bits = delegate.readLong(index);
            return Double.longBitsToDouble(Long.reverseBytes(bits));
        } else {
            return delegate.readDouble(index);
        }
    }

    @Override
    public float getFloatReverse(final int index) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final int bits = delegate.readInt(index);
            return Float.intBitsToFloat(Integer.reverseBytes(bits));
        } else {
            return delegate.readFloat(index);
        }
    }

    @Override
    public short getShortReverse(final int index) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final short bits = delegate.readShort(index);
            return Short.reverseBytes(bits);
        } else {
            return delegate.readShort(index);
        }
    }

    @Override
    public char getCharReverse(final int index) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final short bits = delegate.readShort(index);
            return (char) Short.reverseBytes(bits);
        } else {
            return (char) delegate.readShort(index);
        }
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
    public void getBytes(final int index, final IMemoryBuffer dstBuffer, final long dstIndex, final int length) {
        for (int i = 0; i < length; i++) {
            dstBuffer.putByte(dstIndex + i, delegate.readByte(index + i));
        }
    }

    @Override
    public void getBytes(final int index, final java.nio.ByteBuffer dstBuffer, final int dstIndex, final int length) {
        for (int i = 0; i < length; i++) {
            dstBuffer.put(dstIndex + i, delegate.readByte(index + i));
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
        return delegate.isElastic();
    }

    @Override
    public void putLong(final int index, final long value) {
        ensureCapacity(index, Long.BYTES);
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final long bits = Long.reverseBytes(value);
            delegate.writeLong(index, bits);
        } else {
            delegate.writeLong(index, value);
        }
    }

    @Override
    public void putInt(final int index, final int value) {
        ensureCapacity(index, Integer.BYTES);
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final int bits = Integer.reverseBytes(value);
            delegate.writeInt(index, bits);
        } else {
            delegate.writeInt(index, value);
        }
    }

    @Override
    public void putDouble(final int index, final double value) {
        ensureCapacity(index, Double.BYTES);
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final long bits = Long.reverseBytes(Double.doubleToRawLongBits(value));
            delegate.writeLong(index, bits);
        } else {
            delegate.writeDouble(index, value);
        }
    }

    @Override
    public void putFloat(final int index, final float value) {
        ensureCapacity(index, Float.BYTES);
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final int bits = Integer.reverseBytes(Float.floatToRawIntBits(value));
            delegate.writeInt(index, bits);
        } else {
            delegate.writeFloat(index, value);
        }
    }

    @Override
    public void putShort(final int index, final short value) {
        ensureCapacity(index, Short.BYTES);
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final short bits = Short.reverseBytes(value);
            delegate.writeShort(index, bits);
        } else {
            delegate.writeShort(index, value);
        }
    }

    @Override
    public void putChar(final int index, final char value) {
        ensureCapacity(index, Character.BYTES);
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final short bits = Short.reverseBytes((short) value);
            delegate.writeShort(index, bits);
        } else {
            delegate.writeShort(index, (short) value);
        }
    }

    @Override
    public void putLongReverse(final int index, final long value) {
        ensureCapacity(index, Long.BYTES);
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final long bits = Long.reverseBytes(value);
            delegate.writeLong(index, bits);
        } else {
            delegate.writeLong(index, value);
        }
    }

    @Override
    public void putIntReverse(final int index, final int value) {
        ensureCapacity(index, Integer.BYTES);
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final int bits = Integer.reverseBytes(value);
            delegate.writeInt(index, bits);
        } else {
            delegate.writeInt(index, value);
        }
    }

    @Override
    public void putDoubleReverse(final int index, final double value) {
        ensureCapacity(index, Double.BYTES);
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final long bits = Long.reverseBytes(Double.doubleToRawLongBits(value));
            delegate.writeLong(index, bits);
        } else {
            delegate.writeDouble(index, value);
        }
    }

    @Override
    public void putFloatReverse(final int index, final float value) {
        ensureCapacity(index, Float.BYTES);
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final int bits = Integer.reverseBytes(Float.floatToRawIntBits(value));
            delegate.writeInt(index, bits);
        } else {
            delegate.writeFloat(index, value);
        }
    }

    @Override
    public void putShortReverse(final int index, final short value) {
        ensureCapacity(index, Short.BYTES);
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final short bits = Short.reverseBytes(value);
            delegate.writeShort(index, bits);
        } else {
            delegate.writeShort(index, value);
        }
    }

    @Override
    public void putCharReverse(final int index, final char value) {
        ensureCapacity(index, Character.BYTES);
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final short bits = Short.reverseBytes((short) value);
            delegate.writeShort(index, bits);
        } else {
            delegate.writeShort(index, (short) value);
        }
    }

    @Override
    public void putByte(final int index, final byte value) {
        ensureCapacity(index, Byte.BYTES);
        delegate.writeByte(index, value);
    }

    @Override
    public void putBytes(final int index, final byte[] src, final int srcIndex, final int length) {
        ensureCapacity(index, length);
        delegate.write(index, src, srcIndex, length);
    }

    @Override
    public void putBytes(final int index, final java.nio.ByteBuffer srcBuffer, final int srcIndex, final int length) {
        ensureCapacity(index, length);
        delegate.write(index, srcBuffer, srcIndex, length);
    }

    @Override
    public void putBytes(final int index, final DirectBuffer srcBuffer, final int srcIndex, final int length) {
        ensureCapacity(index, length);
        for (int i = 0; i < length; i++) {
            delegate.writeByte(index + i, srcBuffer.getByte(srcIndex + i));
        }
    }

    @Override
    public void putBytes(final int index, final IByteBuffer srcBuffer, final int srcIndex, final int length) {
        ensureCapacity(index, length);
        for (int i = 0; i < length; i++) {
            delegate.writeByte(index + i, srcBuffer.getByte(srcIndex + i));
        }
    }

    @Override
    public void putBytes(final int index, final IMemoryBuffer srcBuffer, final long srcIndex, final int length) {
        ensureCapacity(index, length);
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
        return new ChronicleDelegateMemoryBuffer(delegate);
    }

    @Override
    public IMemoryBuffer asMemoryBufferFrom(final int index) {
        return new ChronicleDelegateMemoryBuffer(delegate).newSliceFrom(index);
    }

    @Override
    public IMemoryBuffer asMemoryBuffer(final int index, final int length) {
        return new ChronicleDelegateMemoryBuffer(delegate).newSlice(index, length);
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
                final char c = (char) delegate.readByte(i);
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

            delegate.writeByte(index + i, (byte) c);
        }
    }

    @Override
    public int putStringUtf8(final int index, final String value) {
        final byte[] bytes = ByteBuffers.newStringUtf8Bytes(value);
        ensureCapacity(index, bytes.length);
        delegate.write(index, bytes);
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
            while (i < length) {
                final byte b = delegate.readByte(i);
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
            while (i < length) {
                final byte b = delegate.readByte(i);
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
            while (i < length) {
                final byte b = src.readByte();
                delegate.writeByte(i, b);
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
            while (i < length) {
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
                    delegate.writeByte(i, (byte) result);
                    i++;
                }
            }
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

    @SuppressWarnings("rawtypes")
    @Override
    public java.nio.ByteBuffer asNioByteBuffer() {
        final java.nio.ByteBuffer byteBuffer = nioByteBuffer();
        final int wrapAdjustment = wrapAdjustment();
        if (byteBuffer != null) {
            if (wrapAdjustment == 0 && capacity() == byteBuffer.capacity()) {
                return byteBuffer;
            } else {
                return ByteBuffers.slice(byteBuffer, wrapAdjustment, capacity());
            }
        }
        final byte[] array = byteArray();
        if (array != null) {
            final java.nio.ByteBuffer arrayBuffer = java.nio.ByteBuffer.wrap(array, wrapAdjustment, capacity());
            return arrayBuffer;
        }
        final BytesStore store = delegate.bytesStore();
        final long address = store.addressForRead(store.start());
        return UninitializedDirectByteBuffers.asDirectByteBufferNoCleaner(address, capacity());
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
        final int target = index + length;
        for (int i = index; i < target; i++) {
            delegate.writeByte(i, value);
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
            delegate.ensureCapacity(desiredCapacity);
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

}
