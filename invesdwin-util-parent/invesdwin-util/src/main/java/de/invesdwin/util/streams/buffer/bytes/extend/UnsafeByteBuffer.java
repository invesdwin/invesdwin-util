package de.invesdwin.util.streams.buffer.bytes.extend;

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

import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.agrona.io.DirectBufferInputStream;
import org.agrona.io.DirectBufferOutputStream;

import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.UninitializedDirectByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.delegate.slice.mutable.factory.FixedMutableSlicedDelegateByteBufferFactory;
import de.invesdwin.util.streams.buffer.bytes.delegate.slice.mutable.factory.IMutableSlicedDelegateByteBufferFactory;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.delegate.ByteDelegateMemoryBuffer;

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
    public java.nio.ByteBuffer nioByteBuffer() {
        return byteBuffer();
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
        final MutableDirectBuffer directBuffer = dstBuffer.directBuffer();
        if (directBuffer != null) {
            getBytes(index, directBuffer, dstIndex + dstBuffer.wrapAdjustment() - directBuffer.wrapAdjustment(),
                    length);
        } else if (dstBuffer.nioByteBuffer() != null) {
            getBytes(index, dstBuffer.nioByteBuffer(), dstIndex + dstBuffer.wrapAdjustment(), length);
        } else if (dstBuffer.byteArray() != null) {
            getBytes(index, dstBuffer.byteArray(), dstIndex + dstBuffer.wrapAdjustment(), length);
        } else {
            for (int i = 0; i < length; i++) {
                dstBuffer.putByte(dstIndex + i, getByte(index + i));
            }
        }
    }

    @Override
    public void getBytes(final int index, final IMemoryBuffer dstBuffer, final long dstIndex, final int length) {
        for (int i = 0; i < length; i++) {
            dstBuffer.putByte(dstIndex + i, getByte(index + i));
        }
    }

    @Override
    public void putBytes(final int index, final IByteBuffer srcBuffer, final int srcIndex, final int length) {
        //wrapadjustment only needed for byteArray
        final MutableDirectBuffer directBuffer = srcBuffer.directBuffer();
        if (directBuffer != null) {
            putBytes(index, directBuffer, srcIndex + srcBuffer.wrapAdjustment() - directBuffer.wrapAdjustment(),
                    length);
        } else if (srcBuffer.nioByteBuffer() != null) {
            putBytes(index, srcBuffer.nioByteBuffer(), srcIndex + srcBuffer.wrapAdjustment(), length);
        } else if (srcBuffer.byteArray() != null) {
            putBytes(index, srcBuffer.byteArray(), srcIndex + srcBuffer.wrapAdjustment(), length);
        } else {
            for (int i = 0; i < length; i++) {
                putByte(index + i, srcBuffer.getByte(srcIndex + i));
            }
        }
    }

    @Override
    public void putBytes(final int index, final IMemoryBuffer srcBuffer, final long srcIndex, final int length) {
        for (int i = 0; i < length; i++) {
            putByte(index + i, srcBuffer.getByte(srcIndex + i));
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
        }
        return new UnsafeByteBuffer(this, index, remaining(index));
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
        if (dst instanceof WritableByteChannel) {
            getBytesTo(index, (WritableByteChannel) dst, length);
        } else {
            int i = index;
            while (i < length) {
                final byte b = getByte(i);
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
                final byte b = getByte(i);
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
                putByte(i, b);
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
                putByte(i, (byte) result);
                i++;
            }
        }
    }

    @Override
    public void putLong(final int index, final long value) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final long bits = Long.reverseBytes(value);
            super.putLong(index, bits);
        } else {
            super.putLong(index, value);
        }
    }

    @Override
    public void putInt(final int index, final int value) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final int bits = Integer.reverseBytes(value);
            super.putInt(index, bits);
        } else {
            super.putInt(index, value);
        }
    }

    @Override
    public void putDouble(final int index, final double value) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final long bits = Long.reverseBytes(Double.doubleToRawLongBits(value));
            super.putLong(index, bits);
        } else {
            super.putDouble(index, value);
        }
    }

    @Override
    public void putFloat(final int index, final float value) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final int bits = Integer.reverseBytes(Float.floatToRawIntBits(value));
            super.putInt(index, bits);
        } else {
            super.putFloat(index, value);
        }
    }

    @Override
    public void putShort(final int index, final short value) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final short bits = Short.reverseBytes(value);
            super.putShort(index, bits);
        } else {
            super.putShort(index, value);
        }
    }

    @Override
    public void putChar(final int index, final char value) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final short bits = Short.reverseBytes((short) value);
            super.putShort(index, bits);
        } else {
            super.putChar(index, value);
        }
    }

    @Override
    public void putLongReverse(final int index, final long value) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final long bits = Long.reverseBytes(value);
            super.putLong(index, bits);
        } else {
            super.putLong(index, value);
        }
    }

    @Override
    public void putIntReverse(final int index, final int value) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final int bits = Integer.reverseBytes(value);
            super.putInt(index, bits);
        } else {
            super.putInt(index, value);
        }
    }

    @Override
    public void putDoubleReverse(final int index, final double value) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final long bits = Long.reverseBytes(Double.doubleToRawLongBits(value));
            super.putLong(index, bits);
        } else {
            super.putDouble(index, value);
        }
    }

    @Override
    public void putFloatReverse(final int index, final float value) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final int bits = Integer.reverseBytes(Float.floatToRawIntBits(value));
            super.putInt(index, bits);
        } else {
            super.putFloat(index, value);
        }
    }

    @Override
    public void putShortReverse(final int index, final short value) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final short bits = Short.reverseBytes(value);
            super.putShort(index, bits);
        } else {
            super.putShort(index, value);
        }
    }

    @Override
    public void putCharReverse(final int index, final char value) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final short bits = Short.reverseBytes((short) value);
            super.putShort(index, bits);
        } else {
            super.putChar(index, value);
        }
    }

    @Override
    public long getLong(final int index) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final long bits = super.getLong(index);
            return Long.reverseBytes(bits);
        } else {
            return super.getLong(index);
        }
    }

    @Override
    public int getInt(final int index) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final int bits = super.getInt(index);
            return Integer.reverseBytes(bits);
        } else {
            return super.getInt(index);
        }
    }

    @Override
    public double getDouble(final int index) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final long bits = super.getLong(index);
            return Double.longBitsToDouble(Long.reverseBytes(bits));
        } else {
            return super.getDouble(index);
        }
    }

    @Override
    public float getFloat(final int index) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final int bits = super.getInt(index);
            return Float.intBitsToFloat(Integer.reverseBytes(bits));
        } else {
            return super.getFloat(index);
        }
    }

    @Override
    public short getShort(final int index) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final short bits = super.getShort(index);
            return Short.reverseBytes(bits);
        } else {
            return super.getShort(index);
        }
    }

    @Override
    public char getChar(final int index) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final short bits = super.getShort(index);
            return (char) Short.reverseBytes(bits);
        } else {
            return super.getChar(index);
        }
    }

    @Override
    public long getLongReverse(final int index) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final long bits = super.getLong(index);
            return Long.reverseBytes(bits);
        } else {
            return super.getLong(index);
        }
    }

    @Override
    public int getIntReverse(final int index) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final int bits = super.getInt(index);
            return Integer.reverseBytes(bits);
        } else {
            return super.getInt(index);
        }
    }

    @Override
    public double getDoubleReverse(final int index) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final long bits = super.getLong(index);
            return Double.longBitsToDouble(Long.reverseBytes(bits));
        } else {
            return super.getDouble(index);
        }
    }

    @Override
    public float getFloatReverse(final int index) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final int bits = super.getInt(index);
            return Float.intBitsToFloat(Integer.reverseBytes(bits));
        } else {
            return super.getFloat(index);
        }
    }

    @Override
    public short getShortReverse(final int index) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final short bits = super.getShort(index);
            return Short.reverseBytes(bits);
        } else {
            return super.getShort(index);
        }
    }

    @Override
    public char getCharReverse(final int index) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final short bits = super.getShort(index);
            return (char) Short.reverseBytes(bits);
        } else {
            return super.getChar(index);
        }
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
    public int wrapAdjustment() {
        if (byteArray() == null && byteBuffer() == null) {
            //nullpointer workaround when we have no byteBuffer, thus wrapAdjustment=0
            return 0;
        } else {
            return super.wrapAdjustment();
        }
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
    public IByteBuffer ensureCapacity(final int desiredCapacity) {
        if (desiredCapacity > capacity()) {
            checkLimit(desiredCapacity);
        }
        return this;
    }

}
