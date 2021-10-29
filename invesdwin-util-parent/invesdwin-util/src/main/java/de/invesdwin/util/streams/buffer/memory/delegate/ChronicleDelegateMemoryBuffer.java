package de.invesdwin.util.streams.buffer.memory.delegate;

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

import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.UninitializedDirectByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.delegate.ChronicleDelegateByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.delegate.MemoryDelegateByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.extend.ArrayExpandableByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.extend.UnsafeByteBuffer;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.MemoryBuffers;
import de.invesdwin.util.streams.buffer.memory.delegate.slice.SlicedDelegateMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.delegate.slice.SlicedFromDelegateMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.delegate.slice.mutable.factory.IMutableSlicedDelegateMemoryBufferFactory;
import net.openhft.chronicle.bytes.BytesStore;

@NotThreadSafe
public class ChronicleDelegateMemoryBuffer implements IMemoryBuffer {

    private net.openhft.chronicle.bytes.Bytes<?> delegate;
    private IMutableSlicedDelegateMemoryBufferFactory mutableSliceFactory;

    public ChronicleDelegateMemoryBuffer(final net.openhft.chronicle.bytes.Bytes<?> delegate) {
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

    public java.nio.ByteBuffer nioByteBuffer() {
        final Object underlying = delegate.underlyingObject();
        if (underlying instanceof java.nio.ByteBuffer) {
            return (java.nio.ByteBuffer) underlying;
        }
        return null;
    }

    public byte[] byteArray() {
        final Object underlying = delegate.underlyingObject();
        if (underlying instanceof byte[]) {
            return (byte[]) underlying;
        }
        return null;
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
    public long capacity() {
        final long capacity = delegate.safeLimit();
        return capacity;
    }

    @Override
    public long getLong(final long index) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final long bits = delegate.readLong(index);
            return Long.reverseBytes(bits);
        } else {
            return delegate.readLong(index);
        }
    }

    @Override
    public int getInt(final long index) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final int bits = delegate.readInt(index);
            return Integer.reverseBytes(bits);
        } else {
            return delegate.readInt(index);
        }
    }

    @Override
    public double getDouble(final long index) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final long bits = delegate.readLong(index);
            return Double.longBitsToDouble(Long.reverseBytes(bits));
        } else {
            return delegate.readDouble(index);
        }
    }

    @Override
    public float getFloat(final long index) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final int bits = delegate.readInt(index);
            return Float.intBitsToFloat(Integer.reverseBytes(bits));
        } else {
            return delegate.readFloat(index);
        }
    }

    @Override
    public short getShort(final long index) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final short bits = delegate.readShort(index);
            return Short.reverseBytes(bits);
        } else {
            return delegate.readShort(index);
        }
    }

    @Override
    public char getChar(final long index) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final short bits = delegate.readShort(index);
            return (char) Short.reverseBytes(bits);
        } else {
            return (char) delegate.readShort(index);
        }
    }

    @Override
    public long getLongReverse(final long index) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final long bits = delegate.readLong(index);
            return Long.reverseBytes(bits);
        } else {
            return delegate.readLong(index);
        }
    }

    @Override
    public int getIntReverse(final long index) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final int bits = delegate.readInt(index);
            return Integer.reverseBytes(bits);
        } else {
            return delegate.readInt(index);
        }
    }

    @Override
    public double getDoubleReverse(final long index) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final long bits = delegate.readLong(index);
            return Double.longBitsToDouble(Long.reverseBytes(bits));
        } else {
            return delegate.readDouble(index);
        }
    }

    @Override
    public float getFloatReverse(final long index) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final int bits = delegate.readInt(index);
            return Float.intBitsToFloat(Integer.reverseBytes(bits));
        } else {
            return delegate.readFloat(index);
        }
    }

    @Override
    public short getShortReverse(final long index) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final short bits = delegate.readShort(index);
            return Short.reverseBytes(bits);
        } else {
            return delegate.readShort(index);
        }
    }

    @Override
    public char getCharReverse(final long index) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final short bits = delegate.readShort(index);
            return (char) Short.reverseBytes(bits);
        } else {
            return (char) delegate.readShort(index);
        }
    }

    @Override
    public byte getByte(final long index) {
        return delegate.readByte(index);
    }

    @Override
    public void getBytes(final long index, final byte[] dst, final int dstIndex, final int length) {
        for (int i = 0; i < length; i++) {
            dst[dstIndex + i] = delegate.readByte(index + i);
        }
    }

    @Override
    public void getBytes(final long index, final MutableDirectBuffer dstBuffer, final int dstIndex, final int length) {
        for (int i = 0; i < length; i++) {
            dstBuffer.putByte(dstIndex + i, delegate.readByte(index + i));
        }
    }

    @Override
    public void getBytes(final long index, final IByteBuffer dstBuffer, final int dstIndex, final int length) {
        for (int i = 0; i < length; i++) {
            dstBuffer.putByte(dstIndex + i, delegate.readByte(index + i));
        }
    }

    @Override
    public void getBytes(final long index, final IMemoryBuffer dstBuffer, final long dstIndex, final long length) {
        for (long i = 0; i < length; i++) {
            dstBuffer.putByte(dstIndex + i, delegate.readByte(index + i));
        }
    }

    @Override
    public void getBytes(final long index, final java.nio.ByteBuffer dstBuffer, final int dstIndex, final int length) {
        for (int i = 0; i < length; i++) {
            dstBuffer.put(dstIndex + i, delegate.readByte(index + i));
        }
    }

    @Override
    public long wrapAdjustment() {
        final java.nio.ByteBuffer byteBuffer = nioByteBuffer();
        if (byteBuffer != null) {
            return ByteBuffers.wrapAdjustment(byteBuffer);
        }
        final byte[] array = byteArray();
        if (array != null) {
            final long offset = BufferUtil.ARRAY_BASE_OFFSET;
            return addressOffset() - offset;
        }
        return 0;
    }

    @Override
    public boolean isExpandable() {
        return delegate.isElastic();
    }

    @Override
    public void putLong(final long index, final long value) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final long bits = Long.reverseBytes(value);
            delegate.writeLong(index, bits);
        } else {
            delegate.writeLong(index, value);
        }
    }

    @Override
    public void putInt(final long index, final int value) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final int bits = Integer.reverseBytes(value);
            delegate.writeInt(index, bits);
        } else {
            delegate.writeInt(index, value);
        }
    }

    @Override
    public void putDouble(final long index, final double value) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final long bits = Long.reverseBytes(Double.doubleToRawLongBits(value));
            delegate.writeLong(index, bits);
        } else {
            delegate.writeDouble(index, value);
        }
    }

    @Override
    public void putFloat(final long index, final float value) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final int bits = Integer.reverseBytes(Float.floatToRawIntBits(value));
            delegate.writeInt(index, bits);
        } else {
            delegate.writeFloat(index, value);
        }
    }

    @Override
    public void putShort(final long index, final short value) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final short bits = Short.reverseBytes(value);
            delegate.writeShort(index, bits);
        } else {
            delegate.writeShort(index, value);
        }
    }

    @Override
    public void putChar(final long index, final char value) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final short bits = Short.reverseBytes((short) value);
            delegate.writeShort(index, bits);
        } else {
            delegate.writeShort(index, (short) value);
        }
    }

    @Override
    public void putLongReverse(final long index, final long value) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final long bits = Long.reverseBytes(value);
            delegate.writeLong(index, bits);
        } else {
            delegate.writeLong(index, value);
        }
    }

    @Override
    public void putIntReverse(final long index, final int value) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final int bits = Integer.reverseBytes(value);
            delegate.writeInt(index, bits);
        } else {
            delegate.writeInt(index, value);
        }
    }

    @Override
    public void putDoubleReverse(final long index, final double value) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final long bits = Long.reverseBytes(Double.doubleToRawLongBits(value));
            delegate.writeLong(index, bits);
        } else {
            delegate.writeDouble(index, value);
        }
    }

    @Override
    public void putFloatReverse(final long index, final float value) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final int bits = Integer.reverseBytes(Float.floatToRawIntBits(value));
            delegate.writeInt(index, bits);
        } else {
            delegate.writeFloat(index, value);
        }
    }

    @Override
    public void putShortReverse(final long index, final short value) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final short bits = Short.reverseBytes(value);
            delegate.writeShort(index, bits);
        } else {
            delegate.writeShort(index, value);
        }
    }

    @Override
    public void putCharReverse(final long index, final char value) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final short bits = Short.reverseBytes((short) value);
            delegate.writeShort(index, bits);
        } else {
            delegate.writeShort(index, (short) value);
        }
    }

    @Override
    public void putByte(final long index, final byte value) {
        delegate.writeByte(index, value);
    }

    @Override
    public void putBytes(final long index, final byte[] src, final int srcIndex, final int length) {
        delegate.write(index, src, srcIndex, length);
    }

    @Override
    public void putBytes(final long index, final java.nio.ByteBuffer srcBuffer, final int srcIndex, final int length) {
        delegate.write(index, srcBuffer, srcIndex, length);
    }

    @Override
    public void putBytes(final long index, final DirectBuffer srcBuffer, final int srcIndex, final int length) {
        for (int i = 0; i < length; i++) {
            delegate.writeByte(index + i, srcBuffer.getByte(srcIndex + i));
        }
    }

    @Override
    public void putBytes(final long index, final IByteBuffer srcBuffer, final int srcIndex, final int length) {
        for (int i = 0; i < length; i++) {
            delegate.writeByte(index + i, srcBuffer.getByte(srcIndex + i));
        }
    }

    @Override
    public void putBytes(final long index, final IMemoryBuffer srcBuffer, final long srcIndex, final long length) {
        for (int i = 0; i < length; i++) {
            delegate.writeByte(index + i, srcBuffer.getByte(srcIndex + i));
        }
    }

    @Override
    public InputStream asInputStream(final long index, final long length) {
        delegate.readPosition(index);
        delegate.readLimit(index + length);
        return delegate.inputStream();
    }

    @Override
    public OutputStream asOutputStream(final long index, final long length) {
        delegate.writePosition(index);
        delegate.writeLimit(index + length);
        return delegate.outputStream();
    }

    @Override
    public byte[] asByteArrayCopy(final long index, final int length) {
        return MemoryBuffers.asByteArrayCopyGet(this, index, length);
    }

    @Override
    public MutableDirectBuffer asDirectBuffer(final long index, final int length) {
        return new UnsafeByteBuffer(addressOffset() + wrapAdjustment() + index, length);
    }

    @Override
    public IByteBuffer asByteBuffer(final long index, final int length) {
        if (index + length < ArrayExpandableByteBuffer.MAX_ARRAY_LENGTH) {
            return new ChronicleDelegateByteBuffer(delegate).newSlice((int) index, length);
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
                final char c = (char) delegate.readByte(i);
                dst.append(c > 127 ? '?' : c);
            }
        } catch (final IOException e) {
            throw Throwables.propagate(e);
        }
    }

    @Override
    public void putStringAsciii(final long index, final CharSequence value, final int valueIndex, final int length) {
        for (int i = 0; i < length; i++) {
            char c = value.charAt(valueIndex + i);
            if (c > 127) {
                c = '?';
            }

            delegate.writeByte(index + i, (byte) c);
        }
    }

    @Override
    public int putStringUtf8(final long index, final String value) {
        final byte[] bytes = ByteBuffers.newStringUtf8Bytes(value);
        delegate.write(index, bytes);
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
        long i = index;
        while (i < length) {
            final byte b = delegate.readByte(i);
            dst.write(b);
            i++;
        }
    }

    @Override
    public void getBytesTo(final long index, final OutputStream dst, final long length) throws IOException {
        long i = index;
        while (i < length) {
            final byte b = delegate.readByte(i);
            dst.write(b);
            i++;
        }
    }

    @Override
    public void putBytesTo(final long index, final DataInput src, final long length) throws IOException {
        long i = index;
        while (i < length) {
            final byte b = src.readByte();
            delegate.writeByte(i, b);
            i++;
        }
    }

    @Override
    public void putBytesTo(final long index, final InputStream src, final long length) throws IOException {
        long i = index;
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
    public java.nio.ByteBuffer asNioByteBuffer(final long index, final int length) {
        final long address = addressOffset();
        return UninitializedDirectByteBuffers.asDirectByteBufferNoCleaner(address + index, length);
    }

    @Override
    public void clear(final byte value, final long index, final long length) {
        final long target = index + length;
        for (long i = index; i < target; i++) {
            delegate.writeByte(i, value);
        }
    }

    @Override
    public String toString() {
        return MemoryBuffers.toString(this);
    }

    @Override
    public IMemoryBuffer ensureCapacity(final long desiredCapacity) {
        delegate.ensureCapacity(desiredCapacity);
        return this;
    }

}
