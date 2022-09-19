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

import org.agrona.BitUtil;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.agrona.io.DirectBufferInputStream;
import org.agrona.io.DirectBufferOutputStream;
import org.agrona.io.ExpandableDirectBufferOutputStream;

import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.EmptyByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.delegate.slice.SlicedFromDelegateByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.delegate.slice.mutable.factory.IMutableSlicedDelegateByteBufferFactory;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.delegate.ByteDelegateMemoryBuffer;

@NotThreadSafe
public class AgronaDelegateMutableByteBuffer implements IByteBuffer {

    public static final MutableDirectBuffer EMPTY_BYTES = EmptyByteBuffer.EMPTY_AGRONA_BUFFER;
    public static final AgronaDelegateMutableByteBuffer EMPTY_BUFFER = new AgronaDelegateMutableByteBuffer(EMPTY_BYTES);

    private MutableDirectBuffer delegate;
    private IMutableSlicedDelegateByteBufferFactory mutableSliceFactory;

    public AgronaDelegateMutableByteBuffer(final MutableDirectBuffer delegate) {
        setDelegate(delegate);
    }

    public MutableDirectBuffer getDelegate() {
        return delegate;
    }

    public void setDelegate(final MutableDirectBuffer delegate) {
        this.delegate = delegate;
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
    public java.nio.ByteBuffer nioByteBuffer() {
        return delegate.byteBuffer();
    }

    @Override
    public int capacity() {
        return delegate.capacity();
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
            return delegate.getChar(index);
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
            return delegate.getChar(index);
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
        delegate.getBytes(index, dstBuffer, dstIndex, length);
    }

    @Override
    public void getBytes(final int index, final java.nio.ByteBuffer dstBuffer, final int dstIndex, final int length) {
        delegate.getBytes(index, dstBuffer, dstIndex, length);
    }

    @Override
    public void getBytes(final int index, final IByteBuffer dstBuffer, final int dstIndex, final int length) {
        final MutableDirectBuffer directBuffer = dstBuffer.directBuffer();
        if (directBuffer != null) {
            delegate.getBytes(index, directBuffer,
                    dstIndex + dstBuffer.wrapAdjustment() - directBuffer.wrapAdjustment(), length);
        } else if (dstBuffer.nioByteBuffer() != null) {
            delegate.getBytes(index, dstBuffer.nioByteBuffer(), dstIndex + dstBuffer.wrapAdjustment(), length);
        } else if (dstBuffer.byteArray() != null) {
            delegate.getBytes(index, dstBuffer.byteArray(), dstIndex + dstBuffer.wrapAdjustment(), length);
        } else {
            for (int i = 0; i < length; i++) {
                dstBuffer.putByte(dstIndex + i, delegate.getByte(index + i));
            }
        }
    }

    @Override
    public void getBytes(final int index, final IMemoryBuffer dstBuffer, final long dstIndex, final int length) {
        for (int i = 0; i < length; i++) {
            dstBuffer.putByte(dstIndex + i, delegate.getByte(index + i));
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
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final long bits = Long.reverseBytes(value);
            delegate.putLong(index, bits);
        } else {
            delegate.putLong(index, value);
        }
    }

    @Override
    public void putInt(final int index, final int value) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final int bits = Integer.reverseBytes(value);
            delegate.putInt(index, bits);
        } else {
            delegate.putInt(index, value);
        }
    }

    @Override
    public void putDouble(final int index, final double value) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final long bits = Long.reverseBytes(Double.doubleToRawLongBits(value));
            delegate.putLong(index, bits);
        } else {
            delegate.putDouble(index, value);
        }
    }

    @Override
    public void putFloat(final int index, final float value) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final int bits = Integer.reverseBytes(Float.floatToRawIntBits(value));
            delegate.putInt(index, bits);
        } else {
            delegate.putFloat(index, value);
        }
    }

    @Override
    public void putShort(final int index, final short value) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final short bits = Short.reverseBytes(value);
            delegate.putShort(index, bits);
        } else {
            delegate.putShort(index, value);
        }
    }

    @Override
    public void putChar(final int index, final char value) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final short bits = Short.reverseBytes((short) value);
            delegate.putShort(index, bits);
        } else {
            delegate.putChar(index, value);
        }
    }

    @Override
    public void putLongReverse(final int index, final long value) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final long bits = Long.reverseBytes(value);
            delegate.putLong(index, bits);
        } else {
            delegate.putLong(index, value);
        }
    }

    @Override
    public void putIntReverse(final int index, final int value) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final int bits = Integer.reverseBytes(value);
            delegate.putInt(index, bits);
        } else {
            delegate.putInt(index, value);
        }
    }

    @Override
    public void putDoubleReverse(final int index, final double value) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final long bits = Long.reverseBytes(Double.doubleToRawLongBits(value));
            delegate.putLong(index, bits);
        } else {
            delegate.putDouble(index, value);
        }
    }

    @Override
    public void putFloatReverse(final int index, final float value) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final int bits = Integer.reverseBytes(Float.floatToRawIntBits(value));
            delegate.putInt(index, bits);
        } else {
            delegate.putFloat(index, value);
        }
    }

    @Override
    public void putShortReverse(final int index, final short value) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final short bits = Short.reverseBytes(value);
            delegate.putShort(index, bits);
        } else {
            delegate.putShort(index, value);
        }
    }

    @Override
    public void putCharReverse(final int index, final char value) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final short bits = Short.reverseBytes((short) value);
            delegate.putShort(index, bits);
        } else {
            delegate.putChar(index, value);
        }
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
        final MutableDirectBuffer directBuffer = srcBuffer.directBuffer();
        if (directBuffer != null) {
            delegate.putBytes(index, directBuffer,
                    srcIndex + srcBuffer.wrapAdjustment() - directBuffer.wrapAdjustment(), length);
        } else if (srcBuffer.nioByteBuffer() != null) {
            delegate.putBytes(index, srcBuffer.nioByteBuffer(), srcIndex + srcBuffer.wrapAdjustment(), length);
        } else if (srcBuffer.byteArray() != null) {
            delegate.putBytes(index, srcBuffer.byteArray(), srcIndex + srcBuffer.wrapAdjustment(), length);
        } else {
            for (int i = 0; i < length; i++) {
                delegate.putByte(index + i, srcBuffer.getByte(srcIndex + i));
            }
        }
    }

    @Override
    public void putBytes(final int index, final IMemoryBuffer srcBuffer, final long srcIndex, final int length) {
        for (int i = 0; i < length; i++) {
            delegate.putByte(index + i, srcBuffer.getByte(srcIndex + i));
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
    public IMemoryBuffer asMemoryBuffer() {
        return new ByteDelegateMemoryBuffer(this);
    }

    @Override
    public IMemoryBuffer asMemoryBufferFrom(final int index) {
        return new ByteDelegateMemoryBuffer(this).newSliceFrom(index);
    }

    @Override
    public IMemoryBuffer asMemoryBuffer(final int index, final int length) {
        return new ByteDelegateMemoryBuffer(this).newSlice(index, length);
    }

    @Override
    public InputStream asInputStream(final int index, final int length) {
        return new DirectBufferInputStream(delegate, index, length);
    }

    @Override
    public OutputStream asOutputStream(final int index, final int length) {
        if (delegate.isExpandable() && index + length >= capacity()) {
            //allow output stream to actually grow the buffer
            return new ExpandableDirectBufferOutputStream(delegate, index);
        } else {
            return new DirectBufferOutputStream(delegate, index, length);
        }
    }

    @Override
    public byte[] asByteArray() {
        if (wrapAdjustment() == 0) {
            final byte[] bytes = byteArray();
            if (bytes != null) {
                return bytes;
            }
            final java.nio.ByteBuffer byteBuffer = nioByteBuffer();
            if (byteBuffer != null && byteBuffer.hasArray()) {
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
            final java.nio.ByteBuffer byteBuffer = nioByteBuffer();
            if (byteBuffer != null && byteBuffer.hasArray()) {
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
        return ByteBuffers.asByteArray(this, index, length);
    }

    @Override
    public byte[] asByteArrayCopy(final int index, final int length) {
        return ByteBuffers.asByteArrayCopy(this, index, length);
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
        } else if (isExpandable()) {
            return new SlicedFromDelegateByteBuffer(this, index);
        } else {
            return new AgronaDelegateMutableByteBuffer(new UnsafeBuffer(delegate, index, remaining(index)));
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
        if (dst instanceof WritableByteChannel) {
            getBytesTo(index, (WritableByteChannel) dst, length);
        } else {
            int i = index;
            while (i < length) {
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
            while (i < length) {
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
            int i = index;
            while (i < length) {
                final byte b = src.readByte();
                delegate.putByte(i, b);
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
        final long address = addressOffset();
        return de.invesdwin.util.streams.buffer.bytes.UninitializedDirectByteBuffers
                .asDirectByteBufferNoCleaner(address, capacity());
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
        delegate.setMemory(index, length, value);
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
        if (desiredCapacity > capacity()) {
            if (isExpandable()) {
                //we need this workaround to prevent growth when capacity matches on the last bit
                delegate.checkLimit(desiredCapacity - BitUtil.SIZE_OF_BYTE);
            } else {
                delegate.checkLimit(desiredCapacity);
            }
        }
        return this;
    }

}
